package net.ion.radon.impl.let.board;

import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.repository.Node;

import org.restlet.representation.Representation;

public class DeleteLet extends AbstractBoardLet{

	@Override
	protected Representation myGet() throws Exception {
		return notImpl();
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		Node bnode = findBoard();
		int result = bnode.relation("" + getArticleNo()).remove() ;
		bnode.getSession().createQuery().eq("no", getArticleNo()).remove() ;
		bnode.getSession().commit() ;
		
		return toRepresentation(IRequest.EMPTY_REQUEST, ListUtil.EMPTY, IResponse.create(MapUtil.create("count", result)));
	}
	
	@Override
	protected Representation myDelete() throws Exception {
		return myPost(EMPTY_REPRESENTATION);
	}
}
