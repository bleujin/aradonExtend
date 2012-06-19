package net.ion.im.bbs.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ion.im.bbs.IMAbstractLet;

import org.restlet.representation.Representation;

public class BoardLet extends IMAbstractLet {
	
	@Override
	protected Representation myDelete() throws Exception {
		return null;
	}

	@Override
	protected Representation myGet() throws Exception {
		BoardListLoader loader = (BoardListLoader)getContext().getAttributeObject("boardList.config");
		Boards[] boards = loader.getBoardGroup().getBoards();
		
		return toRepresentation(toMap(boards));
	}
	
	private List<Map<String, ?>> toMap(Boards[] boardList) {
		List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
		for(Boards boards : boardList) {
			list.add(boards.toMap());
		}
		return list;
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		return null;
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return null;
	}

}
