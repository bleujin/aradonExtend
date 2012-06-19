package net.ion.radon.impl.let.db;

import java.lang.reflect.Field;
import java.util.Map;

import net.ion.framework.db.IDBController;
import net.ion.framework.util.CaseInsensitiveHashMap;
import net.ion.radon.core.TreeContext;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class ProcedureHelper {

	private static CaseInsensitiveHashMap<Integer> TYPE_MAPPING = new CaseInsensitiveHashMap<Integer>() ;
	static {
		init(TYPE_MAPPING) ;
	}
	
	static Procedures getProcedures(TreeContext context, String id) {
		ConfigLoader loader = context.getAttributeObject("procedure.config", ConfigLoader.class);
		Procedures cmd = loader.getProcedures(id);
		if (cmd == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, id + " not found");
		}
		return cmd;
	}

	static IDBController getDBController(TreeContext context, String connectId) {
		IDBController dc = context.getAttributeObject(connectId, IDBController.class);
		if (dc == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, connectId + " not found");
		}
		return dc;
	}

	static void init(Map mappingTable) {
		try {
			Field[] fields = java.sql.Types.class.getFields();
			for (Field field : fields) {
				mappingTable.put(field.getName(), field.getInt(null));
			}
			
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException(ex.getMessage(), ex) ;
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			throw new IllegalArgumentException(ex.getMessage(), ex) ;
		}
	}

	static int getType(String type) {
		
		if (! TYPE_MAPPING.containsKey(type)) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Field Type \" " + type + " \" not found");
		return TYPE_MAPPING.get(type) ;
	}
	
}
