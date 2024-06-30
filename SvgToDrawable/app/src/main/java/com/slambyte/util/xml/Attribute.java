package com.slambyte.util.xml;

import com.slambyte.util.Tabs;

public class Attribute	{
	private String name;
	private String value;
	private String namespace = null;
	
	private boolean inline = false;

	public Attribute(String name, String value)	{
		this.name = name;
		this.value = value;
	}

	public Attribute(String name, String value, boolean inline)	{
		this.name = name;
		this.value = value;
		this.inline = inline;
	}

	public Attribute(String namespace,String name, String value)	{
		this.name = name;
		this.value = value;
		this.namespace = namespace;
	}

	public Attribute(String namespace,String name, String value, boolean inline)	{
		this.name = name;
		this.value = value;
		this.inline = inline;
		this.namespace = namespace;
	}

	public String getName()	{
		return name;
	}

	public boolean isInline()	{
		return inline;
	}

	public String getValue()	{
		return value;
	}

	public String getNamespace()	{
		return namespace;
	}

	public String toString()	{
		String prefix = "";

		if(namespace != null)	{
			prefix = namespace + ":";
		}

		return prefix + name + "=\"" + value + "\"";
	}

	public String toString(int nTabs)	{
		String tabs = new Tabs().tabs(nTabs);
		String prefix = "";

		if(namespace != null)	{
			prefix = namespace + ":";
		}

		return tabs + prefix + name + "=\"" + value + "\"";
	}
}
