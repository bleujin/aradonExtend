package net.ion.radon.impl.let.mongo;

import java.io.IOException;
import java.sql.SQLException;

import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.SerializedQuery;

import org.restlet.representation.Representation;
import org.restlet.resource.Execute;

public class QueryLet extends MongoServerResource {
	
	
	@Execute
	public Representation execQuery(SerializedQuery squery) throws IOException, SQLException {
		
		Rows rows = super.queryMethod(squery) ;
		
		
		return super.toRepresentation(rows) ; 
	}



}
