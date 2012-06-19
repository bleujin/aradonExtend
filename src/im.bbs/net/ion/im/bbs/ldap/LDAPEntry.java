package net.ion.im.bbs.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LDAPEntry {

	private String server;
	private int port;
	private String baseDN;

	public LDAPEntry(String server, Integer port, String baseDN) {
		this.server = server;
		this.port = port;
		this.baseDN = baseDN;
	}
	
	public List<Map<String, ?>> getList(String filter) throws NamingException {
		return getList(filter, Collections.EMPTY_MAP);
	}
	
	public List<Map<String, ?>> getList(String filter, Map<String, String> additionalProperty) throws NamingException {
		NamingEnumeration<SearchResult> results = search(filter);
		List<Map<String, ?>> persons = new ArrayList<Map<String, ?>>();

		while (results.hasMore()) {
			SearchResult result = results.next();
			Attributes attrs = result.getAttributes();

			if (attrs != null) {
				Map<String, Object> person = new HashMap<String, Object>();
				NamingEnumeration<String> ne = attrs.getIDs();

				while (ne.hasMore()) {
					String key = (String) ne.next();
					Attribute att = attrs.get(key);
					
					person.put(key, att.get().toString());
				}
				person.putAll(additionalProperty);
				persons.add(person);
			}
		}
		return persons;
	}

	private NamingEnumeration<SearchResult> search(String filter) throws NamingException {

		NamingEnumeration<SearchResult> result = null;

		DirContext ctx = null;

		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		props.setProperty(Context.PROVIDER_URL, String.format("ldap://%s:%d", server, port));

		// -------------------------------------------------------------
		// specify the root username
		// -------------------------------------------------------------
		// 검색이기 때문에 그냥 로그인 함
		props.setProperty(Context.SECURITY_PRINCIPAL, "");

		// -------------------------------------------------------------
		// specify the root password
		// -------------------------------------------------------------
		// 검색이기 때문에 그냥 로그인 함
		props.setProperty(Context.SECURITY_CREDENTIALS, "");

		// -------------------------------------------------------------
		// Get the environment properties for creation initial
		// context and specifying LDAP service provider parameters.
		// -------------------------------------------------------------
		try {
			ctx = new InitialDirContext(props);

			// -------------------------------------------------------------
			// LDAP Search
			// -------------------------------------------------------------
			// 검색 제약 조건 설정
			// SUBTREE_SCOPE : 기본 엔트리에서 시작하여 기본 엔트리 및 이 및에 있는 모든 것을 검색한다.
			// 이것은 가정느리고 가장 비싼 검색이다.
			// ONELEVEL_SCOPE : 기본 엔트리 밑에 있는 엔트리들을 검색한다.
			// OBJECT_SCOPE : 기본 엔트리만 검색한다. 가장 빠르고 저렴한 검색이다.
			SearchControls cons = new SearchControls();
			cons.setSearchScope(SearchControls.SUBTREE_SCOPE);

			result = ctx.search(baseDN, filter, cons);

		} finally {
			try {
				if (null != ctx)
					ctx.close();
			} catch (Exception e2) {
			}
		}
		return result;
	}

	
	public static void main(String[] args) {
		LDAPEntry entry = new LDAPEntry("ldap.i-on.net", 389, "dc=i-on,dc=net");
		try {
			List<Map<String, ?>> searchResult = entry.getList("(&(objectclass=person)(cn=*김*))");
			for(Map<String, ?> singleSearchResult : searchResult) {
				System.out.println(singleSearchResult.get("cn"));
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
