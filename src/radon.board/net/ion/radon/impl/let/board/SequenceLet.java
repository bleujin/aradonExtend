package net.ion.radon.impl.let.board;

import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.repository.Node;

import org.restlet.representation.Representation;

public class SequenceLet extends AbstractBoardLet{

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		return null;
	}

	@Override
	protected Representation myGet() throws Exception {
		Node bnode = findBoard();
		int seq = bnode.getAsInt("seq");
		bnode.put("seq", seq+1);
		bnode.getSession().commit();
		
		return toRepresentation(IRequest.EMPTY_REQUEST, ListUtil.EMPTY, IResponse.create(MapUtil.create("seq", bnode.get("seq"))));
	}

}
