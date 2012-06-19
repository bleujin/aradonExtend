package net.ion.radon.impl.let.board;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import net.ion.radon.repository.ISequence;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;

import org.restlet.representation.Representation;

public class AddLet extends AbstractBoardLet {


	private static AtomicInteger ARTID = new AtomicInteger() ;
	
	@Override
	protected Representation myGet() throws Exception {
		Node article = findArticle() ;
		
		return toRepresentation(article);
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		
		Node bnode = findBoard();

		//final String articleNo = String.valueOf(ARTID.incrementAndGet());
		final long articleNo = makeArticleNo();
		Node article = bnode.createChild(String.valueOf(articleNo));
		article.putAll(makeFormToProperty().toMap());
		article.put("no", articleNo);
		
		article.getSession().commit();
		
		return toRepresentation(article);
	}
	
	@Override
	protected Representation myPut(Representation entity) throws Exception {
		
		Node bnode = findBoard();

		//final String articleNo = String.valueOf(ARTID.incrementAndGet());
		final long articleNo = getInnerRequest().getAttributeAsInteger("no", 0);
		Node article = bnode.createChild(String.valueOf(articleNo));
		article.putAll(makeFormToProperty().toMap());
		article.put("no", articleNo);
		
		article.getSession().commit();
		
		return toRepresentation(article);
	}


	private long makeArticleNo() throws IOException, ClassNotFoundException {
		Session session = login() ;
		ISequence seq = session.getSequence("board", getBoardId()) ;
		return  seq.nextVal();
	}


}
