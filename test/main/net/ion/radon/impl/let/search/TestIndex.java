package net.ion.radon.impl.let.search;

import junit.framework.TestCase;
import net.ion.framework.util.DateFormatUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.NumberUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.ISearchRequest;
import net.ion.isearcher.searcher.ISearchResponse;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;
import net.ion.isearcher.searcher.SearchRequest;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.restlet.data.Parameter;

public class TestIndex extends TestCase {

	
	public void testIndex() throws Exception {
		Central c = createIndex() ;
		MyKoreanAnalyzer anal = new MyKoreanAnalyzer() ;
		ISearcher searcher = c.newSearcher() ;
//		ISearchRequest sreq = SearchRequest.create("ename:bleujin", null, anal) ;
		ISearchRequest sreq = SearchRequest.create("emp.no:영준", null, anal) ;
		ISearchResponse sres = searcher.search(sreq) ;
		
		Debug.debug(sres.getDocument().size(), sres.getDocument()) ;
	}
	
	public Central createIndex() throws Exception {
		Directory dir = new RAMDirectory() ;
		// Central c = Central.createOrGet(new File("c:/tmp")) ;
		Central c = Central.createOrGet(dir) ;
		
		MyKoreanAnalyzer anal = new MyKoreanAnalyzer() ;
		IWriter writer = c.testIndexer(anal) ;
		writer.begin("test") ;
		
		writer.insertDocument(createDoc(new Parameter[]{new Parameter("emp.no", "7186"), new Parameter("ename", "bleujin"), })) ;
		writer.insertDocument(createDoc(new Parameter[]{new Parameter("emp.no", "오영준"), new Parameter("ename", "bacdd")})) ;
		writer.insertDocument(createDoc(new Parameter[]{new Parameter("emp.no", "2010.02.10"), new Parameter("ename", "bleujin hi")})) ;
		
		writer.end() ;
		
		return c ;
	}
	
	public void testNumeric() throws Exception {
		String s = "333333333333333333333333333333333.33" ;
		Debug.debug(NumberUtil.toLong(s), (long)NumberUtil.toDouble(s)) ;
		
		assertEquals(true, NumberUtil.isNumber(s)) ;
	}
	
	public void testDate() throws Exception {
		String s = "20000101-121214" ;
		Debug.debug(DateFormatUtil.isDateFormat(s), DateFormatUtil.getDateIfMatchedType("2010.02.10")) ;
	}
	
	private MyDocument createDoc(Parameter... params){
		MyDocument doc = MyDocument.testDocument() ;
		for (Parameter param : params) {
			doc.add(MyField.unknown(param.getName(), param.getValue())) ;
		}
		return doc ;
	}

}
