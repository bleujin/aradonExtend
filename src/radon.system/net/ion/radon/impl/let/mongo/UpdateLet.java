package net.ion.radon.impl.let.mongo;

import java.io.IOException;
import java.sql.SQLException;

import net.ion.framework.db.procedure.SerializedQuery;

import org.restlet.resource.Execute;

public class UpdateLet extends MongoServerResource{

	@Execute
	public int execUpdate(SerializedQuery squery) throws IOException, SQLException {
		
		Object result = super.updateMethod(squery) ;
		return ((Integer)result).intValue();
		
		
	}

}
