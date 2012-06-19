package net.ion.radon.impl.let.board;

import net.ion.radon.repository.Node;

import org.restlet.representation.Representation;

public class EditLet extends AbstractBoardLet {

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		Node article = findArticle() ;
		article.putAll(makeFormToProperty().toMap()) ;
		article.getSession().commit() ;
		
		return toRepresentation(article);
		
	}

	@Override
	protected Representation myGet() throws Exception {
		Node article = findArticle() ;
		
		return toRepresentation(article);
	}

	
}
