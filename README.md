# SvgToDrawable
Convert svg images to android drawables and vice versa.
# How to use?
This repo contains two jar files, one with main method in the cmd directory and the other to use as a library in your project.

Once you have cloned and extracted the repo
``> cd svg2drawable``
## To use in command line
``> cd cmd`` or move the jar file from the cmd folder to wherever for easy access then ``> java -jar SvgToDrawable.jar /path/to/file [/path/to/another/file]``. Only takes input file(s) as argument(s) and output to the same directory as input, any existing file(s) with the same name will be replaced. Only converts files according to file extension

## To use in your project as library
Copy the jar file in lib folder to your project's libs folder.

To convert svg to android vector drawable

``import com.slambyte.util.AndroidVector``

``AndroidVector instance = new AndroidVector(String xmlString);`` 

``String converted = instance.getData();`` 

the string returned will be a formatted android drawable xml.

To convert from android vector drawable to svg

``import com.slambyte.util.ToSvg;`` 

``ToSvg instance = new ToSvg(String xmlString);``

``String converted = instance.getData();`` 
## Limitations
1 does not support shapes - convert all objects to paths

2 supports only linear and radial gradients

3 does not support matrix.(only supports scale.translate and rotate)
