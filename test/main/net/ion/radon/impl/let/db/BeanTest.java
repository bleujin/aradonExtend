package net.ion.radon.impl.let.db;

public class BeanTest {
	private String name;
	private int y;

	public BeanTest(String x, int y) {
		this.name = x;
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}