package net.ion.radon.impl.let.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.events.ApplyEvent;
import net.ion.isearcher.events.CollectorEvent;
import net.ion.isearcher.events.IIndexEvent;
import net.ion.isearcher.events.IndexEndEvent;
import net.ion.isearcher.events.IndexExceptionEvent;
import net.ion.isearcher.indexer.AfterIndexHandler;
import net.ion.isearcher.indexer.BeforeIndexHandler;
import net.ion.isearcher.indexer.channel.RelayChannel;
import net.ion.isearcher.indexer.collect.ICollector;
import net.ion.isearcher.indexer.policy.ContinueIgnoreException;
import net.ion.isearcher.indexer.policy.ExceptionPolicy;
import net.ion.isearcher.indexer.policy.IWritePolicy;
import net.ion.isearcher.indexer.policy.MergePolicy;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.util.CloseUtils;

import org.apache.lucene.store.LockObtainFailedException;

public class SimpleIndexer implements Runnable {

	private final RelayChannel<MyDocument> channel ;

	private IWriter iwriter = IWriter.EMPTY_WRITER;
	private IWritePolicy wpolicy = new MergePolicy();
	private ExceptionPolicy epolicy = new ContinueIgnoreException();

	private List<BeforeIndexHandler> ibefores = new ArrayList<BeforeIndexHandler>();
	private List<AfterIndexHandler> iafters = new ArrayList<AfterIndexHandler>();

	private static CollectorEvent EMPTY_COLLECTOR_EVENT = new CollectorEvent() {

		public long getEventId() throws IOException {
			return 0;
		}

		public long getEventBody() throws IOException {
			return 0;
		}

		public String getCollectorName() {
			return null;
		}

		public ICollector getCollector() {
			return null;
		}
	};

	public SimpleIndexer(RelayChannel<MyDocument> channel) {
		this.channel = channel ;
	}

	public void setWritePolicy(IWritePolicy wpolicy) {
		this.wpolicy = wpolicy;
	}

	public void addBeforeHandler(BeforeIndexHandler ibefore) {
		ibefores.add(ibefore);
	}

	public void addAfterHandler(AfterIndexHandler iafter) {
		iafters.add(iafter);
	}

	private void beforeHandle(CollectorEvent event, MyDocument mydoc) {
		for (BeforeIndexHandler before : ibefores) {
			before.handleDoc(event, mydoc);
		}
	}

	private void afterHandle(IIndexEvent ievent) {
		for (AfterIndexHandler after : iafters) {
			after.indexed(ievent);
		}
	}

	public void run() {
		Debug.debug("writePolicy : " + getWritePolicy());
		Debug.debug("exceptionPolicy : " + getExceptionPolicy());
		Debug.debug("iWriter : " + getWriter());

		while (true) {
			try {
				final String tranName = DateUtil.currentGMTToString();
				Debug.line(tranName) ;
				
				getWriter().begin(tranName);
				
				do {
					MyDocument doc = getChannel().pollMessage();
					beforeHandle(EMPTY_COLLECTOR_EVENT, doc);
					getWritePolicy().apply(getWriter(), doc);
					afterHandle(new ApplyEvent(doc));
				} while(getChannel().hasMessage()) ;
				
				if (getChannel().isEndMessageOccured()) {
					break;
				}
			} catch (LockObtainFailedException ex) {
				ex.printStackTrace();
				afterHandle(new IndexExceptionEvent(ex));
			} catch (Throwable ex) {
				ex.printStackTrace();
				afterHandle(new IndexExceptionEvent(ex));
			} finally {
				try {
					getWriter().end();
				} catch (IOException ex) {
					afterHandle(new IndexExceptionEvent(ex));
				}
				afterHandle(new IndexEndEvent());
			}
		}
	}

	public void setExceptionPolicy(ExceptionPolicy epolicy) {
		if (epolicy == null)
			throw new IllegalArgumentException("exception.indexer.exception_policy.not_defined");
		this.epolicy = epolicy;
	}

	private ExceptionPolicy getExceptionPolicy() {
		return epolicy;
	}

	public void setWriter(IWriter iw) {
		this.iwriter = iw;
	}

	private IWritePolicy getWritePolicy() {
		return this.wpolicy;
	}

	private IWriter getWriter() {
		return this.iwriter;
	}

	private RelayChannel<MyDocument> getChannel() {
		return this.channel;
	}

	public String toString() {
		return "WritePolicy:" + getWritePolicy() + ", ExceptionPolicy:" + getExceptionPolicy();

	}

	public void forceClose() {
		CloseUtils.silentClose(getWriter());
	}
}