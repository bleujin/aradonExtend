package net.ion.im.bbs;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.bean.JSONRowProcessor;
import net.ion.framework.db.bean.handlers.MapListHandler;
import net.ion.radon.core.RadonAttributeKey;
import net.ion.radon.core.EnumClass.IFormat;
import net.ion.radon.core.let.AbstractLet;
import net.ion.radon.core.let.ResultFormat;

import org.restlet.representation.Representation;

public abstract class IMAbstractLet extends AbstractLet {
	
	public void doInit(){
		super.doInit() ;
		
		if(! getInnerRequest().getFormParameter().containsKey(RadonAttributeKey.ARADON_RESULT_FORMAT)) {
			getInnerRequest().setResultFormat(ResultFormat.create(IFormat.JSON, ""));
		}
	}
	
	protected IDBController getDBController() {
		return (IDBController)getContext().getAttributeObject("connection.mysql.im");		
	}
	
	protected Representation rowsToRepresentation(Rows rows) throws SQLException {
		List<Map<String, ?>> datas = (List<Map<String, ?>>) (rows.toHandle(new MapListHandler(new JSONRowProcessor())));
		return toRepresentation(datas);		
	}
	
	protected List<Map<String, Object>> toMap(Rows rows, Map<String, Boolean> columns) throws SQLException, UnsupportedEncodingException {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		while(rows.next()) {
			Map<String, Object> record = new HashMap<String, Object>();
			Iterator<String> iterator = columns.keySet().iterator();
			
			while(iterator.hasNext()) {
				String columnName = iterator.next();
				boolean isKoreanCharacter = columns.get(columnName).booleanValue();
				
				String rawValue = rows.getString(columnName);
				record.put(columnName, isKoreanCharacter ? new String(rawValue.getBytes("ISO-8859-1")) : rawValue);
			}
			
			results.add(record);
		}
		
		return results;
	}
}
