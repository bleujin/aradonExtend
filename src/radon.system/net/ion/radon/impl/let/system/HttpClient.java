package net.ion.radon.impl.let.system;

import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Uniform;
import org.restlet.data.Protocol;

public class HttpClient {

	private Client client ;
	private String name ;
	public HttpClient(String name){
		this.client = new Client(Protocol.HTTP) ;
		this.name = name ;
	}
	
	
	
	public Response handle(Request request){
		return client.handle(request) ;
	}

	
	public void handle(Request request, Response response){
		client.handle(request, response) ;
	}

	
	public void handle(Request request, Uniform receiveCallback){
		client.handle(request, receiveCallback) ;
	}
	
	public void stop() throws Exception{
		client.stop() ;
	}

	public void start() throws Exception{
		client.start() ;
	}

}
