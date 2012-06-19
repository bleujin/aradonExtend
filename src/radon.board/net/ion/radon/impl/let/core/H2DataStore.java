package net.ion.radon.impl.let.core;

import java.sql.SQLException;
import java.util.List;

import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.Row;
import net.ion.framework.db.Rows;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.procedure.H2EmbedDBManager;
import net.ion.framework.db.procedure.HSQLBean;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;

import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

public class H2DataStore extends AbstractResource {

	private static IDBController dc;

	private void init() {
		try {
			if (dc != null) {
				dc.destroySelf();
			}

			HSQLBean bean = new HSQLBean();
			bean.setUserId("sa");
			bean.setUserPwd("");
			bean.setAddress("jdbc:h2:mem");
			DBManager dbm = new H2EmbedDBManager(bean);
			dc = new DBController(dbm);
			dc.initSelf();
			dc.execUpdate(dc.createUserCommand("drop table if exists board"));
			IUserCommand cmd = dc.createUserCommand("create table board (path varchar(2000), parentname varchar(80), name varchar(80), attribute varchar(64000))");
			cmd.execUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doFirstInit() {
		super.doFirstInit();
		init();
	}

	@Delete
	public WrapNode myDelete() {
		try {
			WrapNode result = myGet();

			IUserCommand cmd = dc.createUserCommand("delete from board where parentname=:parentname and name=:name");
			cmd.addParam("parentname", getParentName());
			cmd.addParam("name", getName());
			int count = cmd.execUpdate();

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		}
	}

	@Get
	public WrapNode myGet() {
		try {
			IUserCommand cmd = dc.createUserCommand("select * from board where parentname=:parentname and name=:name");
			cmd.addParam("parentname", getParentName());
			cmd.addParam("name", getName());
			Row row = cmd.execQuery().firstRow();

			return WrapNode.create(DataNode.load(row));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		}
	}

	@Put
	public WrapNode myPut(WrapNode wnode) {
		try {
			// IUserCommand cmd = dc.createUserCommand("create table board (groupid varchar(40), seqno int, subject varchar(400), creuserid varchar(20), credate date, attribute varchar(20000))") ;

			IUserCommand cmd = dc.createUserCommand("insert into board(path, parentname, name, attribute) select :path, :parentname, :name, :attribute from dual where not exists(select 1 from board where parentname=:parentname and name=:name)");
			cmd.addParam("path", getPath());
			cmd.addParam("parentname", getParentName());
			cmd.addParam("name", getName());
			cmd.addParam("attribute", JsonParser.fromMap(wnode.getDataNode().getAttribute()).toString());

			int count = cmd.execUpdate();
			if (count == 0) {
				IUserCommand update = dc.createUserCommand("update board set attribute = :attribute where parentname=:parentname and name=:name");
				update.addParam("parentname", getParentName());
				update.addParam("name", getName());
				update.addParam("attribute", JsonParser.fromMap(wnode.getDataNode().getAttribute()).toString());
				count = update.execUpdate();
			}

			return wnode;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		}

	}

	@org.restlet.resource.List
	public List<DataNode> toList(WrapNode parent) {
		try {
			IUserCommand cmd = dc.createUserCommand("select path, parentname, name, attribute from board where parentname = :parentname ");
			cmd.addParam("parentname", parent.getDataNode().getName());
			cmd.setPage(parent.getExtra(PageBean.class).toPage());
			Rows rows = cmd.execQuery();

			List<DataNode> result = ListUtil.newList();
			while (rows.next()) {
				result.add(DataNode.load(rows));
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		}
	}

	@Post
	public WrapNode myPost(WrapNode node) throws Exception {
		return myPut(node);
	}

	private String getPath() {
		return "/" + getRequest().getResourceRef().getRemainingPart();
	}

	private String getParentName() {
		String[] paths = StringUtil.split(getPath(), "/");
		return paths.length <= 1 ? "/" : paths[paths.length - 2];
	}

	private String getName() {
		return StringUtil.substringAfter(getPath(), "/");
	}
}
