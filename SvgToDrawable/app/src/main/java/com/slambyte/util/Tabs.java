package com.slambyte.util;

public class Tabs	{
	int length = 0;
	public String tabs(int tabs)	{
		String tmpTab = "";

		for(int i = 0; i < tabs; i ++)	{
			tmpTab += "\t";
			length ++;
		}
		return tmpTab;
	}

	public int length()	{
		return length;
	}
}
