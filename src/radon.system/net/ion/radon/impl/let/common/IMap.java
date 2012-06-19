package net.ion.radon.impl.let.common;

import java.io.IOException;

public interface IMap {

	public void put(String key, String path) throws IOException ;
	public String getValue(String key) ;
	public boolean contains(String key) ;
	public boolean remove(String refId) throws IOException;
}
