package com.slambyte.util.xml;

public class Document	{
	Element root = null;
	String standalone = null;
	String encoding = "utf-8";
	String version = "1.0";
	
	public Document()	{}
	
	public Document(Element elem)	{
		root = elem;
	}

	public void setEncoding(String enc)	{
		encoding = enc;
	}

	public void setStandalone(String standalone)	{
		this.standalone = standalone;
	}

	public String getEncoding()	{
		return encoding;
	}

	public boolean isStandalone()	{
		if(standalone != null && standalone.toLowerCase().equals("yes"))	{
			return true;
		}
		return false;
	}

	public String toString(int nTabs)	{
		if(root != null)	{
			String str = "<?xml version=\"" + version + "\" encoding=\"" + encoding + "\" ";
			if(standalone != null)	str += "standalone=\"" + standalone + "\" ";
			str += "?>";
			str += "\n" + root.toString(nTabs);
			return str;
		}
		return null;
	}
}
