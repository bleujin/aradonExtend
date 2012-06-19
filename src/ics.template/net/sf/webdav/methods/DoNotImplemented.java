package net.sf.webdav.methods;

import java.io.IOException;
import java.util.logging.Logger;

import net.ion.framework.logging.LogBroker;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.sf.webdav.IMethodExecutor;
import net.sf.webdav.ITransaction;
import net.sf.webdav.WebdavStatus;

public class DoNotImplemented implements IMethodExecutor {

	private static Logger LOG = LogBroker.getLogger(DoNotImplemented.class);
	private boolean _readOnly;

	public DoNotImplemented(boolean readOnly) {
		_readOnly = readOnly;
	}

	public void execute(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException {
		LOG.info("-- " + req.getMethod());

		if (_readOnly) {
			resp.sendError(WebdavStatus.SC_FORBIDDEN);
		} else
			resp.sendError(WebdavStatus.SC_NOT_FOUND);
	}
}
