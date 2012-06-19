package net.ion.radon.impl.let.mongo;

import groovy.lang.GroovyObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.SerializedQuery;
import net.ion.framework.db.procedure.SerializedQuery.SerialType;
import net.ion.framework.util.PathMaker;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

public class MongoServerResource extends AbstractServerResource {

	private RepositoryCentral getCentral() {
		return getContext().getAttributeObject("my.mongodb.id", RepositoryCentral.class);
	}

	private File getGroovyFile(String packageName) {
		String parentDir = getContext().getAttributeObject("my.package.dir", "./resource/package/procedure/", String.class);
		return new File(PathMaker.getFilePath(parentDir, StringUtil.capitalize(packageName) + ".groovy"));
	}

	private Session getSession() {
		return getCentral().testLogin("icsstest");
	}

	// public int updateMethod(SerializedQuery squery) throws RepositoryException {
	// int resultCount = 0 ;
	// if (SerialType.UserProcedures == squery.getSerialType()) {
	// SerializedQuery[] querys = squery.getQuerys();
	//			
	// for (SerializedQuery query : querys) {
	// Object result = updateMethod(query);
	// resultCount += Integer.class.cast(result);
	// }
	// } else {
	// resultCount += ((Integer)invokeGroovyMethod(squery)).intValue();
	// }
	//		
	// getSession().commit() ;
	// return resultCount ;
	// }

	public int updateMethod(SerializedQuery squery) throws RepositoryException {
		if (SerialType.UserProcedures == squery.getSerialType()) {
			SerializedQuery[] querys = squery.getQuerys();

			int sumCount = 0;
			for (SerializedQuery query : querys) {
				Object result = updateMethod(query);
				sumCount += Integer.class.cast(result);
			}
			return sumCount;
		} else {
			return ((Integer) invokeGroovyMethod(squery)).intValue();
		}
	}

	private Object invokeGroovyMethod(SerializedQuery squery) {
		try {
			String procName = squery.getProcSQL();

			String packageName = StringUtil.substringBefore(procName, "@");
			String methodName = StringUtil.substringBetween(procName, "@", "(");
			List params = squery.getParams();

			// Class gclass = ScriptFactory.groovyLoader().parseClass(getGroovyFile(packageName));
			Class gclass;
			gclass = Class.forName("procedure." + StringUtil.capitalize(packageName));
			// GroovyObject gobject = (GroovyObject) gclass.getDeclaredConstructor(Session.class).newInstance(getSession("default"));
			GroovyObject gobject = (GroovyObject) gclass.newInstance();
			gobject.invokeMethod("init", new Object[] { getSession(), squery });

			return gobject.invokeMethod(methodName, params.toArray());
		} catch (ClassNotFoundException e) {
			throw RepositoryException.throwIt(e);
		} catch (InstantiationException e) {
			throw RepositoryException.throwIt(e);
		} catch (IllegalAccessException e) {
			throw RepositoryException.throwIt(e);
		} catch(Throwable ex){
			ex.printStackTrace() ;
			throw RepositoryException.throwIt(new Exception(ex));
		}

		// } catch (Throwable ex){
		// ex.printStackTrace() ;
		// throw RepositoryException.throwIt(new Exception(ex));
		// catch (InvocationTargetException e) {
		// throw RepositoryException.throwIt(e);
		// } catch (NoSuchMethodException e) {
		// throw RepositoryException.throwIt(e);
		// }
 	}

	public Rows queryMethod(SerializedQuery squery) {
		if (SerialType.UserProcedures == squery.getSerialType()) {
			SerializedQuery[] querys = squery.getQuerys();

			Rows firstRows = null;
			Rows nextRows = null;
			for (SerializedQuery query : querys) {
				Object result = queryMethod(query);
				if (firstRows == null) {
					firstRows = Rows.class.cast(result);
					nextRows = firstRows;
				} else {
					nextRows = nextRows.setNextRows(Rows.class.cast(result));
				}
			}
			return firstRows;
		} else {
			return ((Rows) invokeGroovyMethod(squery));
		}
	}

	public Representation toRepresentation(Rows rows) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(bos);
		output.writeObject(rows);
		output.close();

		return new InputRepresentation(new ByteArrayInputStream(bos.toByteArray()));
	}

}

// When Reflect
// private Object callMethod(SerializedQuery squery) {
// try {
// String procName = squery.getProcSQL();
// String packageName = StringUtil.substringBefore(procName, "@");
// String methodName = StringUtil.substringBetween(procName, "@", "(");
// List params = squery.getParams();
//
// final Class<?> clz = Class.forName("net.ion.radon.impl.let.mongo." + StringUtil.capitalize(packageName));
//
// Object instance = clz.getDeclaredConstructor(Session.class).newInstance(getSession("default"));
// return MethodUtils.invokeMethod(instance, methodName, params.toArray());
// } catch (NoSuchMethodException ex) {
// throw RepositoryException.throwIt(ex);
// } catch (IllegalAccessException ex) {
// throw RepositoryException.throwIt(ex);
// } catch (InvocationTargetException ex) {
// throw RepositoryException.throwIt(ex);
// } catch (ClassNotFoundException ex) {
// throw RepositoryException.throwIt(ex);
// } catch (InstantiationException ex) {
// throw RepositoryException.throwIt(ex);
// }
// }