package net.ion.im.bbs.util;

import net.ion.im.bbs.IMAbstractLet;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class LogViewLet extends IMAbstractLet {

	@Override
	protected Representation myDelete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Representation myGet() throws Exception {
		Tail tail = (Tail) getContext().getAttributeObject("log.viewer");
		return new StringRepresentation(tail.read(10, "<br/>"), MediaType.TEXT_HTML);
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
