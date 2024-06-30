package com.slambyte.util;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class Main	{
	public static void main(String[] args)	{
		String xmlDocument = "";
		if(args.length > 0)	{
			String line = null;
			BufferedReader br = null;
			String filepath = args[0];

			String outfile = null;

			if(args.length == 2)	{
				outfile = args[1];
			}
			try	{
				File fp = new File(filepath);
				String filename = fp.getName();

				String[] tmp = filename.split("\\.");
				String extension = tmp[tmp.length-1];
				
				br = new BufferedReader(new FileReader(filepath));
				while((line = br.readLine()) != null)	{
					xmlDocument += line;
				}

				if(extension.equals("svg"))	{
					xmlDocument = new AndroidVector(xmlDocument).getData();
					extension = ".xml";
				}else if(extension.equals("xml"))	{
					xmlDocument = new ToSvg(xmlDocument).getData();
					extension = ".svg";
				}


				if(!xmlDocument.equals(""))	{
					if(outfile == null)	outfile = fp.getParent() + "/" + tmp[0] + extension; 
				System.out.println(outfile);
					FileOutputStream fos = new FileOutputStream(outfile);

					byte[] buffer = xmlDocument.getBytes();
					fos.write(buffer,0,buffer.length);
					fos.close();
				}
					// new AndroidVector(xmlDocument);
					// System.out.println(new AndroidVector(xmlDocument).getData());
			}catch(IOException e)	{
				if(e instanceof FileNotFoundException)	{
					System.out.println("file not found or does not exist");
				}
			}catch(Exception e)	{
				System.out.println(e.getMessage());
			}
		}
	}
}
