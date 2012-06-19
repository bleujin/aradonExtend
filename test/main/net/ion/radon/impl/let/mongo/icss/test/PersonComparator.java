package net.ion.radon.impl.let.mongo.icss.test;

import java.util.Comparator;

public class PersonComparator  implements Comparator<Person>{
	
	private String[] key;
	public PersonComparator(String... key) {
		this.key = key;
	}

	public int compare(Person o1, Person o2) {
		for(String k : key){
			if(!o1.get(k).equals(o2.get(k)))
				return o1.get(k).compareTo(o2.get(k));
		}
		return 0;
	}

}
