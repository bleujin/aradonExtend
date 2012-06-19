package procedure

import net.ion.framework.db.Rows;

import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.myapi.ICursor;
import static procedure.GroupConstants.*;
import net.ion.radon.repository.Node;
import static procedure.table.ScheduleTable.TaskId;
import static procedure.table.RepositoryTable.RepoId;
import static procedure.table.ContextTable.*;

class Base extends IProcedure{
	public Base(){}
	public void initSelf() {}
	
	private final String NEXTVALUE = "nextValue";
	public Rows getIndexTaskId(){
		Node idNode = session.createAradonGroupQuery(SCHEDULE_TASK_ID, NEXTVALUE).findOne();
		if(idNode == null){
			idNode = session.newNode().setAradonId(SCHEDULE_TASK_ID, NEXTVALUE).put(TaskId, 0);
		}
		idNode.put(TaskId, idNode.getAsInt(TaskId) +1);
		commit();
		return fromNode(idNode, TaskId);
	}
	
	
	public Rows contextListBy(String repoId){
		ICursor cursor = session.createAradonGroupQuery(CONTEXT).eq(RepoId, repoId).eq(UseFlg, "T").find();
		return fromCursor(cursor, Config, IncrementKey);
	}
	
	public int setIncrementRepoCtextListBy(String repoId, String incrementKey){
		ICursor cursor = session.createAradonGroupQuery(CONTEXT).eq(RepoId, repoId).eq(UseFlg, "T").find();
		return setIncrementKey(cursor, incrementKey);
	}
	
	public int setIncrementCtextListBy(String ctexId, String incrementKey){
		ICursor cursor = session.createAradonGroupQuery(CONTEXT, ctexId).eq(UseFlg, "T").find();
		return setIncrementKey(cursor, incrementKey);
	}
	
	private setIncrementKey(ICursor cursor, String incrementKey){
		int result = 0;
		while(cursor.hasNext()){
			Node node = cursor.next();
			node.put(IncrementKey, incrementKey);
			result += session.commit();
		}
		return result;
	}
}
