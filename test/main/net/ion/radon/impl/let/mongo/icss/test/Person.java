package net.ion.radon.impl.let.mongo.icss.test;

import java.util.Map;

import net.ion.framework.util.MapUtil;

public class Person {

	private int age;
	private Map<String, String> map = MapUtil.newOrdereddMap();

	public Person(int age, String name, String loc) {
		this.age = age;
		map.put("name", name);
		map.put("loc", loc);
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	@Override
	public String toString() {
		//return ToStringBuilder.reflectionToString(this);
		return String.valueOf(age) + map.values();

	}

	public String get(String key) {
		return map.get(key);
		
	}

}
