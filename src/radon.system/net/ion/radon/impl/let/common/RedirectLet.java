package net.ion.radon.impl.let.common;

import java.util.logging.Level;

import net.ion.radon.core.let.DefaultLet;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.routing.Template;

public class RedirectLet extends DefaultLet {

	public static final int MODE_CLIENT_PERMANENT = 1;
	public static final int MODE_CLIENT_FOUND = 2;
	public static final int MODE_CLIENT_SEE_OTHER = 3;
	public static final int MODE_CLIENT_TEMPORARY = 4;
	public static final int MODE_SERVER_OUTBOUND = 6;
	public static final int MODE_SERVER_INBOUND = 7;
	protected volatile String targetTemplate;
	protected volatile int mode;

	@Override
	protected Representation myGet() throws Exception {
		Request request = getRequest();
		Response response = getResponse();

		setTargetTemplate(getContext().getAttributeObject("target.reference", "", String.class)) ;
		mode = Integer.parseInt(getContext().getAttributeObject("redirector.mode", "3", String.class)) ;
		
		Reference targetRef = getTargetRef(request, response);
		switch (mode) {
		case 1: // '\001'
			getLogger().log(Level.INFO, (new StringBuilder()).append("Permanently redirecting client to: ").append(targetRef).toString());
			response.redirectPermanent(targetRef);
			break;

		case 2: // '\002'
			getLogger().log(Level.INFO, (new StringBuilder()).append("Redirecting client to found location: ").append(targetRef).toString());
			response.setLocationRef(targetRef);
			response.setStatus(Status.REDIRECTION_FOUND);
			break;

		case 3: // '\003'
			getLogger().log(Level.INFO, (new StringBuilder()).append("Redirecting client to another location: ").append(targetRef).toString());
			response.redirectSeeOther(targetRef);
			break;

		case 4: // '\004'
			getLogger().log(Level.INFO, (new StringBuilder()).append("Temporarily redirecting client to: ").append(targetRef).toString());
			response.redirectTemporary(targetRef);
			break;

		case 6: // '\006'
			getLogger().log(Level.INFO, (new StringBuilder()).append("Redirecting via client dispatcher to: ").append(targetRef).toString());
			outboundServerRedirect(targetRef, request, response);
			break;

		case 7: // '\007'
			getLogger().log(Level.INFO, (new StringBuilder()).append("Redirecting via server dispatcher to: ").append(targetRef).toString());
			inboundServerRedirect(targetRef, request, response);
			break;
		}
		return EMPTY_REPRESENTATION ;
	}

	protected void inboundServerRedirect(Reference targetRef, Request request, Response response) {
		serverRedirect(getContext().getServerDispatcher(), targetRef, request, response);
	}

	protected void outboundServerRedirect(Reference targetRef, Request request, Response response) {
		Restlet next = getApplication() != null ? getApplication().getOutboundRoot() : null;
		if (next == null)
			next = getContext().getClientDispatcher();
		serverRedirect(next, targetRef, request, response);
		if (response.getEntity() != null && !request.getResourceRef().getScheme().equalsIgnoreCase(targetRef.getScheme()))
			response.getEntity().setLocationRef((Reference) null);
	}

	protected Representation rewrite(Representation initialEntity) {
		return initialEntity;
	}

	protected void serverRedirect(Restlet next, Reference targetRef, Request request, Response response) {
		if (next == null) {
			getLogger().warning((new StringBuilder()).append("No next Restlet provided for server redirection to ").append(targetRef).toString());
		} else {
			Reference resourceRef = request.getResourceRef();
			Reference baseRef = resourceRef.getBaseRef();
			request.setProtocol(null);
			request.setResourceRef(targetRef);
			request.getAttributes().remove("org.restlet.http.headers");
			next.handle(request, response);
			response.setEntity(rewrite(response.getEntity()));
			response.getAttributes().remove("org.restlet.http.headers");
			request.setResourceRef(resourceRef);
			if (response.getLocationRef() != null) {
				Template rt = new Template(targetTemplate);
				rt.setLogger(getLogger());
				int matched = rt.parse(response.getLocationRef().toString(), request);
				if (matched > 0) {
					String remainingPart = (String) request.getAttributes().get("rr");
					if (remainingPart != null)
						response.setLocationRef((new StringBuilder()).append(baseRef.toString()).append(remainingPart).toString());
				}
			}
		}
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setTargetTemplate(String targetTemplate) {
		this.targetTemplate = targetTemplate;
	}

	protected Reference getTargetRef(Request request, Response response) {
		Template rt = new Template(targetTemplate);
		rt.setLogger(getLogger());
		return new Reference(request.getResourceRef(), rt.format(request, response));
	}

}
