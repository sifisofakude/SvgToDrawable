package com.slambyte.util;

import com.slambyte.util.xml.Element;
import com.slambyte.util.xml.Document;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class ToSvg	{
	private Document document = null;
	private ArrayList<Element> tmpGrads;
	private ArrayList<Element> tmpGroups;
	
	String style = null;
	String aaptName = null;
	
	public ToSvg(String xmlDoc) throws Exception	{
		tmpGrads = new ArrayList<Element>();
		tmpGroups = new ArrayList<Element>();
		String lastGrad = null;
		String transform = null;
		
		String xmlns = "xmlns=\"http://www.w3.org/2000/svg\"";
		String svgNs = "xmlns:svg=\"http://www.w3.org/2000/svg\"";
		String xlink = "xmlns:xlink=\"http://www.w3.org/1999/xlink\"";

		Element tmpGroup = null;
		Element tmpGrad = null;
		Element tmpItem = null;
		Element tmpPath = null;
		
		Element defs = new Element("defs");
		defs.addAttribute("id","defs1");
		
		Element rootElement = new Element("svg");

		XMLInputFactory factory = XMLInputFactory.newFactory();
		XMLStreamReader stream = factory.createXMLStreamReader(new StringReader(xmlDoc));

		int nTabs = 1;

		while(stream.hasNext())	{
			if(stream.getEventType() == XMLStreamConstants.END_ELEMENT)	{
				String tag = stream.getLocalName();
				
				if(tag.equals("group"))	{
					String t = transformation(transform);
					if(!t.equals("") && t != null)	{
						if(tmpGroup != null)	{
							tmpGroup.addAttribute("transform",t);
						}
					}
					
					if(tmpGroups.size() > 0)	{
						int index = tmpGroups.size()-1;
						Element tmp = tmpGroups.get(index);
						
						tmpGroups.remove(index);
						
						if(tmpGroup != null)	{
							tmp.appendChild(tmpGroup);
							tmpGroups.add(tmp);
						}else	{
							rootElement.appendChild(tmp);
						}
					}else	{
						rootElement.appendChild(tmpGroup);
					}

					// if(tmpGroup != null)	{
						// rootElement.appendChild(tmpGroup);
					// }
					tmpGroup = null;
				}

				if(tag.equals("path"))	{
					if(style != null && tmpPath != null)	{
						tmpPath.addAttribute("style",style);

						String d = tmpPath.getAttribute("d");
						tmpPath.removeAttribute("d");
						tmpPath.addAttribute("d",d);

						style = null;
					}
					if(tmpGroup == null)	{
						rootElement.appendChild(tmpPath);
					}else	{
						tmpGroup.appendChild(tmpPath);
					}
				}

				if(tag.equals("attr"))	{
					if(aaptName.equals("fillColor"))	{
						if(tmpPath != null)	{
							if(style == null) style = "fill:url(#"+ lastGrad +");fill-opacity:1";
							else style += ";fill:url(#"+ lastGrad +");fill-opacity:1";
							
							tmpPath.addAttributeAt(0,"style",style);
						}
					}
					
					if(aaptName.equals("strokeColor"))	{
						if(tmpPath != null)	{
							if(style == null) style = "stroke:url(#"+ lastGrad +");stroke-opacity:1";
							else style += ";stroke:url(#"+ lastGrad +");stroke-opacity:1";
							
							tmpPath.addAttributeAt(0,"style",style);
						}
					}
				}

				if(tag.equals("gradient"))	{
					if(tmpGrad != null)	{
						boolean exists = false;
						
						String id = tmpGrad.getAttribute("id");
						if(id == null)	{
							id = tmpGrad.getLocalName() + (Math.round(Math.random()*9999)+1);
							println("p");
							tmpGrad.addAttribute("id",id);
						}

						lastGrad = id;
						
						for(Element grad : tmpGrads)	{
							String gn = grad.getAttribute("id");
							if(gn.equals(tmpGrad.getAttribute("id")))	{
								exists = true;
								break;
							}
						}

						if(!exists) tmpGrads.add(tmpGrad);
						tmpGrad = null;
					}
				}
			}
			
			if(stream.getEventType() == XMLStreamConstants.START_ELEMENT)	{
				String tag = stream.getLocalName();
				int attrCount = stream.getAttributeCount();

				if(tag.equals("vector"))	{
					for(int i = 0; i < attrCount; i ++)	{
						String attrName = stream.getAttributeLocalName(i);
						String attrValue = stream.getAttributeValue(i);

						if(attrName.equals("name")) rootElement.addAttribute("id",attrValue);
						if(attrName.equals("width")) rootElement.addAttribute("width",attrValue.replaceAll("[^\\d]",""));
						if(attrName.equals("height")) rootElement.addAttribute("height",attrValue.replaceAll("[^\\d]",""));

						if(!rootElement.hasAttribute("viewBox"))	{
							rootElement.addAttribute("viewBox","0 0");
						}

						if(attrName.equals("viewportWidth"))	{
							String viewBox = rootElement.getAttribute("viewBox");
							rootElement.addAttribute("viewBox",viewBox + " " + attrValue);
						}

						if(attrName.equals("viewportHeight"))	{
							String viewBox = rootElement.getAttribute("viewBox");
							rootElement.addAttribute("viewBox",viewBox + " " + attrValue);
						}
					}
				}

				if(tag.equals("gradient"))	{
					Element tmpElm = new Element("gradient");
					ArrayList<String> gradient = new ArrayList<String>();

					for(int i = 0; i < attrCount; i ++)	{
						String attrName = stream.getAttributeLocalName(i);
						String attrValue = stream.getAttributeValue(i);

						if(attrName.equals("collect") || attrName.equals("gradientUnits")) continue;
						
						if(attrName.equals("name")) tmpElm.addAttribute("id",attrValue);
						if(attrName.equals("startX")) tmpElm.addAttribute("x1",attrValue);
						if(attrName.equals("startY")) tmpElm.addAttribute("y1",attrValue);
						if(attrName.equals("endX")) tmpElm.addAttribute("x2",attrValue);
						if(attrName.equals("endY")) tmpElm.addAttribute("y2",attrValue);
						if(attrName.equals("centerY")) tmpElm.addAttribute("cy",attrValue);
						if(attrName.equals("centerY")) tmpElm.addAttribute("fy",attrValue);
						if(attrName.equals("centerX")) tmpElm.addAttribute("cx",attrValue);
						if(attrName.equals("centerX")) tmpElm.addAttribute("fx",attrValue);
						if(attrName.equals("gradientRadius")) tmpElm.addAttribute("r",attrValue);

						if(attrName.equals("type"))	{
							if(attrValue.equals("linear")) tmpElm.changeLocalName("linearGradient");
							else if(attrValue.equals("radial")) tmpElm.changeLocalName("radialGradient");
							else continue;
						}
					}
					tmpGrad = tmpElm;
				}

				if(tag.equals("item"))	{
					if(tmpItem == null)	{
						rootElement.setNamespace(xlink);
					}

					Element tmpElm = new Element("stop");

					for(int i = 0; i < attrCount; i ++)	{
						String attrName = stream.getAttributeLocalName(i);
						String attrValue = stream.getAttributeValue(i);

						if(attrName.equals("id")) continue;

						if(attrName.equals("color"))	{
							String color = attrValue.replace("#","");
							String opacity = null;

							if(color.length() == 8)	{
								opacity = color.substring(0,2);
								color = "#" + color.substring(2);

								int hex = Integer.valueOf(opacity,16);
								opacity = ((double) hex/255) + "";
								
								tmpElm.addAttribute("style","stop-color:"+ color + ";stop-opacity:" + opacity);
							}
						}

						if(attrName.equals("offset"))	{
							tmpElm.addAttribute("offset",Double.valueOf(attrValue)+"");
						}
					}
					tmpGrad.appendChild(tmpElm);
				}

				if(tag.equals("group"))	{
					Element tmpElm = new Element("g");

					if(tmpGroup != null)	{
						String t = transformation(transform);
						if(t != null) tmpGroup.addAttribute("transform",t);
						tmpGroups.add(tmpGroup);

						transform =  null;
					}

					for(int i = 0; i < attrCount; i ++)	{
						String attrName = stream.getAttributeLocalName(i);
						String attrValue = stream.getAttributeValue(i);

						if(attrName.equals("name")) tmpElm.addAttribute("id",attrValue);

						if(attrName.equals("scaleX"))	{
							if(transform == null)	{
								transform = "scale x" + attrValue;
							}else	{
								transform += " x" + attrValue;
							}
						}

						if(attrName.equals("scaleY"))	{
							if(transform == null)	{
								transform = "scale y" + attrValue;
							}else	{
								transform += " y" + attrValue;
							}
						}

						if(attrName.equals("translateX"))	{
							if(transform == null)	{
								transform = "translate x" + attrValue;
							}else	{
								transform += " x" + attrValue;
							}
						}

						if(attrName.equals("translateY"))	{
							if(transform == null)	{
								transform = "translate y" + attrValue;
							}else	{
								transform += " y" + attrValue;
							}
						}

						if(attrName.equals("rotation"))	{
							if(transform == null)	{
								transform = "rotate r" + attrValue;
							}else	{
								transform += " r" + attrValue;
							}
						}

						if(attrName.equals("pivotX"))	{
							if(transform == null)	{
								transform = "rotate px" + attrValue;
							}else	{
								transform += " px" + attrValue;
							}
						}

						if(attrName.equals("pivotX"))	{
							if(transform == null)	{
								transform = "rotate py" + attrValue;
							}else	{
								transform += " py" + attrValue;
							}
						}
					}
					tmpGroup = tmpElm;
				}

				if(tag.equals("path"))	{
					Element tmpElm = new Element("path");
					for(int i = 0; i < attrCount; i++)	{
						String attrName = stream.getAttributeLocalName(i);
						String attrValue = stream.getAttributeValue(i);

						if(attrName.equals("name")) tmpElm.addAttribute("id",attrValue);

						if(attrName.equals("fillColor"))	{
							String opacity = null;
							String color = attrValue.replace("#","");
							if(color.length() == 8)	{
								opacity = color.substring(0,2);
								opacity = ((double) Integer.valueOf(opacity,16)/255)+"";
								color = color.substring(2);
							}
							setStyle("fill","#"+ color);

							if(opacity != null)	{
								setStyle("fill-opacity",opacity);
							}
						}

						if(attrName.equals("strokeColor"))	{
							String opacity = null;
							String color = attrValue.replace("#","");
							if(color.length() == 8)	{
								opacity = color.substring(0,2);
								opacity = ((double) Integer.valueOf(opacity,16)/255)+"";
								color = color.substring(2);
							}
							setStyle("stroke","#"+ color);

							if(opacity != null)	{
								setStyle("stroke-opacity",opacity);
							}
						}
						
						if(attrName.equals("strokeWidth")) setStyle("stroke-width",attrValue);
						if(attrName.equals("strokeLineCap")) setStyle("stroke-linecap",attrValue);
						if(attrName.equals("strokeLineJoin")) setStyle("stroke-linejoin",attrValue);
						if(attrName.equals("strokeMiterLimit")) setStyle("stroke-miterlimit",attrValue);
						
						if(attrName.equals("pathData")) tmpElm.addAttribute("d",attrValue);
					}
					tmpPath = tmpElm;
				}

				if(tag.equals("attr"))	{
					String attr = stream.getAttributeValue(0);
					aaptName = attr.split(":")[1];
				}
			}
			stream.next();
		}

		for(Element grad : tmpGrads)	{
			Element child = new Element("linearGradient");
			ArrayList<Element> children = grad.getChildren();
			for(int i = 0; i < children.size(); i ++)	{
				child.appendChild(children.get(i));
			}

			String id = "linearGradient" + (Math.round(Math.random()*9999)+1);
			child.addAttribute("gradientUnits","userSpaceOnUse");
			child.addAttribute("id",id);
			defs.appendChild(child);

			grad.removeChildren();
			grad.addNsAttributeAt(0,"xlink","href","#" + id);
			grad.addAttribute("gradientUnits","userSpaceOnUse");
			defs.appendChild(grad);
		}
		rootElement.appendChildAt(0,defs);
		rootElement.setNamespace(xmlns);
		rootElement.setNamespace(svgNs);

		document = new Document(rootElement);
	}

	public String getData()	{
		if(document != null)	{
			return document.toString(1);
		}
		return null;
	}

	public Element linkGradients(String gradient)	{
		Element grad = null;
		for(int i = 0; i < tmpGrads.size(); i ++)	{
			grad = tmpGrads.get(i);
			if(grad.hasAttribute("link"))	{
				if(!grad.getAttribute("name").equals(gradient)) continue;
				
				String link = grad.getAttribute("link");
				for(int j = 0; j < tmpGrads.size(); j ++)	{
					Element tmpGrad = tmpGrads.get(j);
					if(tmpGrad.getAttribute("name").equals(link))	{
						int tmpCount = tmpGrad.childCount();
						for(int k = 0; k < tmpCount; k ++)	{
							grad.appendChild(tmpGrad.getChildAt(k));
						}
					}
				}
				grad.removeAttribute("link");
				break;
			}
		}
		return grad;
	}

	public void setStyle(String attr, String value)	{
		if(style == null)	{
			style = attr + ":" + value;
		}else	{
			style += ";" + attr +":"+ value;
		}
	}

	public String transformation(String trans)	{
		if(trans == null) return "";
		
		String[] tmp = trans.split(" ");
		trans = "";
		String scaling = null;
		String rotation = null;
		String translation = null;
		
		if(tmp[0].equals("rotate"))	{
			String x = meshTranformString(tmp,"x");
			String y = meshTranformString(tmp,"y");
			String a = meshTranformString(tmp,"a");

			
			rotation = "rotate("+ a + ","+ x + "," + y + ")";
		}

		if(tmp[0].equals("translate"))	{
			String x = meshTranformString(tmp,"x");
			String y = meshTranformString(tmp,"y");

			translation = "translate("+ x +","+ y +")";
		}

		if(tmp[0].equals("scale"))	{
			String x = meshTranformString(tmp,"x");
			String y = meshTranformString(tmp,"y");

			scaling = "scale("+ x +","+ y +")";
		}

		if(scaling != null && rotation == null && translation == null) trans = scaling;
		if(scaling == null && rotation != null && translation == null) trans = rotation;
		if(scaling == null && rotation == null && translation != null) trans = translation;
		if(scaling == null && rotation == null && translation == null) trans = translation;
		
		return trans;
	}

	public String meshTranformString(String[] array, String search)	{
		String match = null;
		
		for(String var : array)	{
			if(var.startsWith(search))	{
				match = var.replaceAll(search,"");
				break;
			}
		}
		return match;
	}

	public void println(String str)	{
		System.out.println(str);
	}
}



