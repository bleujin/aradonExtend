package net.ion.radon.impl.let.search;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.ion.framework.util.StringUtil;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.IReader;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.channel.MemoryChannel;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

public class EngineEntry {

	public final static String ARADON_GROUP = "aradon.group" ;
	public final static String ARADON_UID = "aradon.uid" ;

	private File dir ;
	private MemoryChannel<MyDocument> store = new MemoryChannel<MyDocument>(100) ;

	public EngineEntry(String filePath) throws LockObtainFailedException, IOException{
		dir = new File(filePath) ;
		if (!dir.exists())
			dir.mkdirs();
		
		SimpleIndexer indexer = new SimpleIndexer(store) ;
		indexer.setWriter(getWriter()) ;
		
		new Thread(indexer).start() ;
		
	}
	
	public void addIndex(Map<String, Object> values, String groupId, String docId){
		MyDocument doc = makeDocument(values, groupId, docId) ;
		doc.setAction(Action.Insert) ;
		store.addMessage(doc) ;
	}
	public void updateIndex(Map<String, Object> values, String groupId, String docId){
		MyDocument doc = makeDocument(values, groupId, docId) ;
		doc.setAction(Action.Update) ;
		store.addMessage(doc) ;
	}
	public void deleteIndex(Map<String, Object> values, String groupId, String docId){
		MyDocument doc = makeDocument(values, groupId, docId) ;
		doc.setAction(Action.Delete) ;
		store.addMessage(doc) ;
	}
	
	
	public Map<String, Object> getInfo() throws CorruptIndexException, IOException{
		
		IReader reader = getCentral().newReader();
		IndexReader ireader = reader.getIndexReader() ;

		Map<String, Object> infoMap = new HashMap<String, Object>() ;
		Directory dir = ireader.directory();
		infoMap.put("directory", dir.toString()) ;
		infoMap.put("current version", ireader.getCurrentVersion(dir)) ;
		infoMap.put("directory version", ireader.getVersion()) ;
		infoMap.put("indexExists", ireader.indexExists(dir)) ;
		infoMap.put("isOptimized", ireader.isOptimized()) ;
		infoMap.put("lastModified", new Date(ireader.lastModified(dir))) ;
		infoMap.put("maxDoc", ireader.maxDoc()) ;
		infoMap.put("numDoc", ireader.numDocs()) ;
		
		return infoMap ;
	}
	
	
	private MyDocument makeDocument(Map<String, Object> params, final String groupId, final String dId) {
		String docId = groupId + "." + dId ;
		
		MyDocument doc = MyDocument.newDocument(docId, params);
		String[] groups = StringUtil.split(groupId, ".:/") ;
		for (int i = 0; i < groups.length; i++) {
			String path = StringUtil.join(groups, '.', 0, i+1) ;
			doc.add(MyField.keyword(ARADON_GROUP, path)) ;
		}
		doc.add(MyField.keyword(ARADON_UID, dId)) ;
		return doc;
	}
	
	public ISearcher getSearcher() throws IOException, LockObtainFailedException {
		Central c = getCentral();
		return c.newSearcher() ;
	}

	private IWriter getWriter() throws IOException, LockObtainFailedException {
		Central c = getCentral();

		Analyzer analyzer = new MyKoreanAnalyzer();

		IWriter writer = c.newIndexer(analyzer);
		return writer;
	}
	
	
	private Central getCentral() throws IOException {
		Central c = Central.createOrGet(dir);
		return c;
	}
	
}
