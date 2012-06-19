package net.ion.radon.impl.let.system;

import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class SequenceLet extends MongoDefaultLet{
	
	
	protected Representation myPut(Representation entity) throws Exception {
		Node snode = getSequence() ;
		snode.put("currval", ((Integer)snode.get("currval")).intValue() + getInnerRequest().getAttributeAsInteger("num", 1)) ;
		
		getSession().createQuery().id(snode.getIdentifier()).updateOne(snode.toPropertyMap()) ;
		return toRepresentation(snode);
	}

	protected Representation myDelete() throws Exception {
		String seqId = getInnerRequest().getAttribute("seqId") ;
		Node removedSequence = removeSequence(seqId) ;
		if (removedSequence == null){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "seqId:" + seqId) ;
		}
		return toRepresentation(removedSequence) ;
	}

	protected Representation myPost(Representation entity) throws Exception {
		Node snode = getSequence() ;
		snode.put("currval", getInnerRequest().getAttributeAsInteger("num", 0)) ;
		
		getSession().createQuery().id(snode.getIdentifier()).updateOne(snode.toPropertyMap()) ;
		return toRepresentation(snode);
	}

	protected Representation myGet() throws Exception {
		Node snode = getSequence() ;
		return toRepresentation(snode);
	}


	private Node getSequence() {
		String seqId = getInnerRequest().getAttribute("seqId") ;

		Node seqNode = getSession().createQuery().eq("seqId", seqId).findOne();
		if (seqNode == null){
			seqNode = getSession().newNode(seqId) ;
			seqNode.put("seqId", seqId) ;
			seqNode.put("currval", 0) ;
		}
		getSession().commit();
		
		return seqNode ;
	}

	private Node removeSequence(String seqId) {
		SessionQuery query = getSession().createQuery().eq("seqId", seqId);
		Node remove = query.findOne();
		query.remove() ;
		return remove ;
	}
	
	private Session getSession(){
		return getSession("_sequence") ;
	}
	
}
