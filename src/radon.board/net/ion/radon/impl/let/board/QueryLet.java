package net.ion.radon.impl.let.board;

import java.io.IOException;
import java.sql.SQLException;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IQueryable;
import net.ion.radon.core.let.AbstractServerResource;

import org.restlet.resource.Post;

public class QueryLet extends AbstractServerResource {
	
	
	@Post
	public Rows execQuery(IQueryable cmd) throws IOException, SQLException {
		IDBController dc = getContext().getAttributeObject(IDBController.class.getCanonicalName(), IDBController.class) ;
		
		return dc.getRows(cmd) ;
	}
	
}
