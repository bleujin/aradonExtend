package net.ion.radon.impl.let.board;

import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;

import org.restlet.representation.Representation;

public class ViewLet extends AbstractBoardLet{

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		return notImpl();
	}

	///{boardid}/view/{no}
	@Override
	protected Representation myGet() throws Exception {
		Session session = login();
		
		Node node = session.createQuery().path("/" + getBoardId() + "/" + getArticleNo()).findOne();
		
		return toRepresentation(node);
	}

}
