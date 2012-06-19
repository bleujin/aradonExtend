package net.ion.radon.impl.let.search;

import java.io.IOException;
import java.sql.SQLException;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.rest.formater.SearchHTMLFormater;
import net.ion.isearcher.rest.formater.SearchJSONFormater;
import net.ion.isearcher.rest.formater.SearchResponseFormater;
import net.ion.isearcher.rest.formater.SearchXMLFormater;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.ISearchResponse;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;
import net.ion.isearcher.searcher.SearchRequest;
import net.ion.isearcher.searcher.filter.TermFilter;
import net.ion.radon.core.PageBean;
import net.ion.radon.core.EnumClass.IFormat;
import net.ion.radon.core.let.DefaultLet;
import net.ion.radon.param.MyParameter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.TermsFilter;
import org.restlet.representation.Representation;

public class SearchLet extends DefaultLet  {
	
	@Override 
	protected Representation myGet() throws Exception {
		ISearcher searcher = getEngine().getSearcher() ;
		String groupid = getInnerRequest().getAttribute("groupid") ;
		TermsFilter groupFilter = new TermsFilter() ;
		groupFilter.addTerm(new Term(EngineEntry.ARADON_GROUP, groupid)) ;
		searcher.andFilter(groupFilter) ;
		String query = getInnerRequest().getAttribute("query") ;
		String sort =  getInnerRequest().getAttribute("sort") ;
		String direction =  getInnerRequest().getAttribute("direction") ;
		
		ISearchRequest srequest = SearchRequest.create(query, sort + ("asc".equals(direction) ? " asc" : " desc"), new MyKoreanAnalyzer()) ;
		
		ISearchResponse sres = searcher.search(srequest) ;
		return getFormater().toRepresentation(sres) ;
	}
	
	
	private SearchResponseFormater getFormater() {
		if (getInnerRequest().getIFormat() == IFormat.HTML ){
			return new SearchHTMLFormater() ;
		} else if (getInnerRequest().getIFormat() == IFormat.JSON) {
			return new SearchJSONFormater() ;
		} else if (getInnerRequest().getIFormat() == IFormat.XML) {
			return new SearchXMLFormater() ;
		}
		throw new IllegalArgumentException(getFormater().toString());
	}


	@Override 
	protected Representation myPost(Representation entity) throws Exception {
		ISearcher searcher = getEngine().getSearcher() ;
		MyParameter mparam = MyParameter.create(getInnerRequest().getParameter(ARADON_PARAMETER, ""));
		
		ISearchRequest srequest = createSearchRequest(mparam) ;
		setSearchFilter(searcher, srequest, mparam) ;
		
		ISearchResponse sres = searcher.search(srequest) ;
		return new SearchJSONFormater().toRepresentation(sres) ;
	}

	
	
	// example : "{query:'text', sort:'a, b', searchfilter:[{type:'nrange', name:'empno', from:3, to:5}, {type:'term', name:'empname', terms:'end'}], filter:'time:0 TO 222', page:{listNum:10, pageNo:1}, param:{userQuery:'', userId:'bleujin'} }";
	private void setSearchFilter(ISearcher searcher, ISearchRequest srequest, MyParameter param) throws IOException, ParseException {
		int filterCount = 0 ;
		Object[] filters = param.getParams("searchfilter");

		for (int i = 0; i < filters.length; i++) {
			MyParameter fparam = MyParameter.create(filters[0]) ;
			String name = fparam.getParamAsString("name");
			String type = fparam.getParamAsString("type");
			Filter newFilter = null ;
			if ("query".equals(type)) {
				newFilter  = new QueryFilter(srequest.parse(fparam.getParamAsString("query"))) ;
			} else if ("nrange".equals(type)){
				long from = Long.parseLong(fparam.getParamAsString("from")) ;
				long to = Long.parseLong(fparam.getParamAsString("to")) ;
				newFilter = NumericRangeFilter.newLongRange(name, 32, from, to, true, true) ;
			} else if ("trange".equals(type)){
				String from = fparam.getParamAsString("from") ;
				String to = fparam.getParamAsString("to") ;
				newFilter = new TermRangeFilter(name, from, to, true, true) ;
			} else if ("term".equals(type)) {
				TermFilter tfilter = new TermFilter() ;
				String termExpr = fparam.getParamAsString("terms");
				String[] termStr = StringUtil.split(termExpr, ',');
				for (int k = 0; k < termStr.length; k++) {
					Term term = new Term(name, termStr[k]) ;
					tfilter.addTerm(term) ;
				}
				newFilter = tfilter ;
				
			} else {
				throw new IllegalArgumentException("unknown filter type");
			}
			Debug.debug(newFilter) ;
			searcher.andFilter(newFilter) ;
		}
	}

	private ISearchRequest createSearchRequest(MyParameter mparam) throws IOException, SQLException, ParseException {
		String query = mparam.getParamAsString("query") ; // mandatory
		String sort = mparam.getParamAsString("sort") ; // mandatory
		
		Analyzer analyzer = new MyKoreanAnalyzer() ;
		ISearchRequest request = SearchRequest.create(query, sort, analyzer);

		PageBean pb = getInnerRequest().getAradonPage() ;
		request.setPage(pb.toPage());

		if (StringUtil.isNotBlank(mparam.getParamAsString("filter"))){
			request.setQueryFilter(mparam.getParamAsString("filter")) ;
		}

		return request;
	}
	

	private EngineEntry getEngine() throws IOException {
		final String engineName = getContext().getAttributeObject("let.search.name", "aradon.search.engine", String.class);
		final EngineEntry engine = getContext().getAttributeObject(engineName, EngineEntry.class);
		if (engine == null) throw new IllegalStateException("index engine not found : " + engineName) ;
		return engine;
	}

}
