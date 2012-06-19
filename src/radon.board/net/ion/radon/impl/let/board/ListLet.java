package net.ion.radon.impl.let.board;

import java.util.List;
import java.util.Map;

import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeScreen;

import org.restlet.representation.Representation;

public class ListLet extends AbstractBoardLet {

	@Override
	protected Representation myGet() throws Exception {
		//InboundLet let = lookupLet("core", "/dataio/" + getBoardId() + "?aradon.result.format=object" );

		PageBean page = PageBean.create(getInnerRequest().getAttributeAsInteger("listnum", 10), getInnerRequest().getAttributeAsInteger("pageno", 1)) ;
		
//		Node bnode = findBoard() ;
		
		NodeScreen screen = createQuery().eq("boardid", getBoardId()).descending("no").find().screen(page) ;
		// NodeScreen screen = bnode.getChild().screen(page) ;
		List<Map<String, ?>> articles = screen.getPageMap() ;

		final Map<String, Object> resMap = MapUtil.create("count",(Object)articles.size());
	 	resMap.put("totalCount", screen.getScreenSize()) ;
	 	
		return toRepresentation(IRequest.EMPTY_REQUEST, articles, IResponse.create(resMap  ));
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		return notImpl();
	}

}
