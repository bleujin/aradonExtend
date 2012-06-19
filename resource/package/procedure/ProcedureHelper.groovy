package procedure

import java.sql.Date;
import java.util.ArrayList;

import org.apache.commons.lang.ObjectUtils;

import net.ion.framework.db.Rows;
import net.ion.framework.db.bean.handlers.ArrayListHandler;
import net.ion.framework.db.bean.handlers.StringArrayHandler;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.myapi.ICursor;

class ProcedureHelper {

	public static String getUID(Object... keys){
		String uid = "";
		keys.each {it -> uid += ( "/" + ObjectUtil.toString(it)) }
		return (uid.length() == 0 )? "" : uid.substring(1);
	}

	public static int getIncrementNo(String key, SessionQuery query){
		NodeCursor nc = query.descending(key).find();
		if(nc.count() == 0 ) return 1

		return nc.next().getAsInt(key) + 1;
	}

	public static int nvl(int value, int defaultValue){
		if(value > 0) return value;
		return defaultValue;
	}

	public static Object decode(Object... args){
		if(args.length < 3) return "";
		int i=1;
		for (; i < (args.length - 1); i += 2) {
			if (ObjectUtils.equals( args[0], args[i]))
				return  args[i + 1];
		}
		return i == (args.length - 1) ? args[i] : "";
	}


	public static int getNextId(Session session, String groupId){
		Node idNode = session.createQuery().aradonGroupId(groupId, "nextValue").findOne();
		if(idNode == null){
			idNode =  session.newNode().setAradonId(groupId, "nextValue").put("next", 0);
		}
		int next =  idNode.getAsInt("next")+1;
		idNode.put("next", next);
		session.commit();
		return next;
	}

	public static String[] getStringArray(ICursor cursor, String key) throws Exception{
		Rows rows = NodeRows.createByCursor(Queryable.Fake, cursor, key);
		rows.beforeFirst();
		String [] results = (String[]) new StringArrayHandler(key).handle(rows);
		return results;
	}
	
	public static ArrayList<Object> getObjectList(ICursor cursor, String key) throws Exception{
		Rows rows = NodeRows.createByCursor(Queryable.Fake, cursor, key);
		rows.beforeFirst();
		
		new ArrayListHandler().handleString(rows, new String[0]{key});
		return (ArrayList<Object>) rows.toHandle(new ArrayListHandler());
	}
	
	public static Date getPreviousDate(int opt){
		Calendar c = Calendar.getInstance();
		c.add(opt, -1);
		return new Date(c.getTimeInMillis());
	}
	
	public static <T> NodeColumns makeColumns(T...columns){
		return NodeColumns.create(makeColumnsArray(columns));
	}

	public  static <T> String [] makeColumnsArray(T...columns){
		if(columns == null && columns.length == 0){
			return new String[0];
		}

		List<String> list = ListUtil.newList();
		for(T column : columns){
			list.add(column.toString());
		}
		return list.toArray(new String[0]);
	}
}
