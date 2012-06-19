package net.ion.radon.impl.let.webdav.back;

public class DavProperty implements Comparable<DavProperty> {
	private String namespace = "";
	private String property = "";

	public DavProperty(final String namespace1, final String property1) {
		setNamespace(namespace1);
		setProperty(property1);
	}

	public int compareTo(final DavProperty dpother) {
		final String here = toString();
		final String there = dpother.toString();
		return here.compareTo(there);
	}

	public boolean equals(final Object other) {
		try {
			final DavProperty dpother = (DavProperty) other;
			if (toString().equals(dpother.toString()))
				return true;
		} catch (final Exception castAssumptionsFailed) {
			return false;
		}
		return false;
	}

	protected String getNamespace() {
		return namespace;
	}

	protected String getProperty() {
		return property;
	}

	public int hashCode() {
		return toString().hashCode();
	}

	protected void setNamespace(final String namespace) {
		this.namespace = namespace;
	}

	protected void setProperty(final String property) {
		this.property = property;
	}

	public String toString() {
		return getNamespace() + ":" + getProperty();
	}
}
