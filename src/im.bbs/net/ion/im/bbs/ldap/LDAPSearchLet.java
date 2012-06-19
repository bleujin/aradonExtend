package net.ion.im.bbs.ldap;

import java.util.HashMap;
import java.util.Map;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Row;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.im.bbs.IMAbstractLet;

import org.restlet.representation.Representation;

public class LDAPSearchLet extends IMAbstractLet {
	
	private static final String SEARCH_TEXT = "search_text";
	private static final String ID = "id";
	private static final String LDAP_ENTRY = "ldap.entry";
	
	@Override
	protected Representation myDelete() throws Exception {
		return null;
	}

	@Override
	protected Representation myGet() throws Exception {
		String name = getInnerRequest().getAttribute(SEARCH_TEXT);
		String filter = LDAPFilter.getFilterWithName(name);
		LDAPEntry entry = (LDAPEntry) getContext().getAttributeObject(LDAP_ENTRY);
		
		return toRepresentation(entry.getList(filter));
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		String name = getInnerRequest().getAttribute(ID);
		String filter = LDAPFilter.getFilterWithID(name);
		LDAPEntry entry = (LDAPEntry) getContext().getAttributeObject(LDAP_ENTRY);
		
		IDBController dc = getDBController();
		String email = String.format("%s@i-on.net", name);
		String updateViewCountSQL = "select case open_picture when '1' then picture else '' end as picture, case open_handphone when '1' then handphone else '' end as handphone from zetyx_member_table where email = :email";
		IUserCommand updateCommand = dc.createUserCommand(updateViewCountSQL);
		updateCommand.addParam("email", email);
		Rows rows = updateCommand.execQuery();
		
		Map<String, String> additionalProperty = new HashMap<String, String>();
		
		if(rows.first()) {
			Row row = rows.firstRow();
			additionalProperty.put("picture", row.getString("picture"));
			additionalProperty.put("cellphone", row.getString("handphone"));
		}
		
		
		
		return toRepresentation(entry.getList(filter, additionalProperty));
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return null;
	}

}
