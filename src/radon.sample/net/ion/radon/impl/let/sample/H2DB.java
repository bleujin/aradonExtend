package net.ion.radon.impl.let.sample;

import java.sql.SQLException;

import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.procedure.H2EmbedPoolDBManager;
import net.ion.framework.db.procedure.HSQLBean;
import net.ion.radon.core.IService;
import net.ion.radon.core.context.OnEventObject;

public class H2DB implements OnEventObject {

	private IDBController dc;
	public H2DB(HSQLBean bean) {
		this.dc = new DBController("h2db", new H2EmbedPoolDBManager(bean));
	}

	public void onEvent(AradonEvent event, IService service) {
		try {
			if (event == AradonEvent.START) {
				
				dc.initSelf();
			} else if (event == AradonEvent.STOP) {
				dc.destroySelf() ;
			}
		} catch (SQLException ex) {
			throw new IllegalStateException(ex) ;
		}
	}

	public IDBController getIDBController() {
		return dc;
	}

}
