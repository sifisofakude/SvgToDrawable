package com.slambyte.util;

import com.slambyte.util.xml.Element;
import com.slambyte.util.xml.Document;

import java.util.ArrayList;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class AndroidVector	{
	private OpenTags op;
	private String data = "";
	private Document document = null;
	private ArrayList<Element> tmpGrads;
	private ArrayList<Element> tmpGroups;
	
	public AndroidVector(String xmlDoc) throws Exception	{
		op = new OpenTags();
		tmpGrads = new ArrayList<Element>();
		tmpGroups = new ArrayList<Element>();
		String lastGrad = null;
		
		String aaptNamespace = "xmlns:aapt=\"http://schemas.android.com/aapt\"";
		String androidNamespace = "xmlns:android=\"http://schemas.android.com/apk/res/android\"";

		Element tmpGrad = null;
		Element tmpGroup = null;
		Element rootElement = new Element("vector");;
		rootElement.setNamespace(androidNamespace);

		XMLInputFactory factory = XMLInputFactory.newFactory();
		XMLStreamReader stream = factory.createXMLStreamReader(new StringReader(xmlDoc));

		while(stream.hasNext())	{
			if(stream.getEventType() == XMLStreamConstants.END_ELEMENT)	{
				String tag = stream.getLocalName();

				if(tag.equals("g"))	{
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
					tmpGroup = null;
				}

				if(tag.startsWith("linear") || tag.startsWith("radial"))	{
					tmpGrads.add(tmpGrad);
					tmpGrad = null;
				}
			}
			
			if(stream.getEventType() == XMLStreamConstants.START_ELEMENT)	{
				String tag = stream.getLocalName();
				int attrCount = stream.getAttributeCount();


				if(tag.equals("svg"))	{
					String name = stream.getAttributeValue("","id");
					String width = stream.getAttributeValue("","width").replaceAll("[^\\d]","");
					String height = stream.getAttributeValue("","height").replaceAll("[^\\d]","");
					String[] viewBox = stream.getAttributeValue("","viewBox").split(" ");


					if(name != null) rootElement.addNsAttribute("android","name",name);
					rootElement.addNsAttribute("android","width",width + "dp");
					rootElement.addNsAttribute("android","height",height + "dp");
					rootElement.addNsAttribute("android","viewportWidth",viewBox[2]);
					rootElement.addNsAttribute("android","viewportHeight",viewBox[3]);
				}

				if(tag.equals("linearGradient") || tag.equals("radialGradient"))	{
					Element tmpElm = new Element("gradient");
					ArrayList<String> gradient = new ArrayList<String>();

					for(int i = 0; i < attrCount; i ++)	{
						String attrName = stream.getAttributeLocalName(i);
						String attrValue = stream.getAttributeValue(i);

						if(attrName.equals("collect") || attrName.equals("gradientUnits")) continue;
						
						if(attrName.equals("href"))	{
							tmpElm.addAttribute("link",attrValue.replace("#",""));
						}

						if(attrName.equals("id")) tmpElm.addNsAttribute("android","name",attrValue);
						if(attrName.equals("x1")) tmpElm.addNsAttribute("android","startX",attrValue);
						if(attrName.equals("y1")) tmpElm.addNsAttribute("android","startY",attrValue);
						if(attrName.equals("x2")) tmpElm.addNsAttribute("android","endX",attrValue);
						if(attrName.equals("y2")) tmpElm.addNsAttribute("android","endY",attrValue);
						if(attrName.equals("cx")) tmpElm.addNsAttribute("android","centerX",attrValue);
						if(attrName.equals("cy")) tmpElm.addNsAttribute("android","centerY",attrValue);
						if(attrName.equals("r")) tmpElm.addNsAttribute("android","gradientRadius",attrValue);

						if(!tmpElm.hasAttribute("type"))	{
							if(tag.startsWith("linear"))	{
								tmpElm.addNsAttribute("android","type","linear");
							}
							else if(tag.startsWith("radial"))	{
								tmpElm.addNsAttribute("android","type","radial");
							}
						}
					}

					tmpGrad = tmpElm;
				}

				if(tag.equals("stop"))	{
					Element tmpElm = new Element("item");

					for(int i = 0; i < attrCount; i ++)	{
						String attrName = stream.getAttributeLocalName(i);
						String attrValue = stream.getAttributeValue(i);

						if(attrName.equals("id")) continue;

						if(attrName.equals("style"))	{
							String color = null;
							String opacity = null;
							String[] values = attrValue.split(";");
							for(String value : values)	{
								String[] tmp = value.split(":");
								if(tmp[0].equals("stop-color"))	{
									color = tmp[1].replace("#","").toUpperCase();
								}

								if(tmp[0].equals("stop-opacity"))	{
									opacity = String.format("%X",Math.round(Integer.valueOf(tmp[1])*255));
									if(opacity.equals("0"))	{
										opacity += "0";
									}
								}
							}

							// if(!tmpElm.hasAttribute("color"))	{
							tmpElm.addNsAttribute("android","color","#" + opacity + color);
							// }
						}

						if(attrName.equals("offset"))	{
							// if(!tmpElm.hasAttribute("offset"))	{
								tmpElm.addNsAttribute("android","offset",Double.valueOf(attrValue)+"");
							// }
						}
					}

					tmpGrad.appendChild(tmpElm);
				}

				if(tag.equals("g"))	{
					Element tmpElm = new Element("group");

					for(int i = 0; i < attrCount; i ++)	{
						String attrName = stream.getAttributeLocalName(i);
						String attrValue = stream.getAttributeValue(i);

						if(attrName.equals("id")) tmpElm.addNsAttribute("android","name",attrValue);

						if(attrName.equals("transform"))	{
							String[] tmp = attrValue.replaceAll("\\(|\\)"," ").split(" ");
							
							tmpElm = transformation(tmpElm,tmp[0],tmp[1]);
						}
					}

					if(tmpGroup != null)	{
						// Previous group has not reached a closing element
						tmpGroups.add(tmpGroup);
					}
					tmpGroup = tmpElm;
				}

				if(tag.equals("path"))	{
					Element tmpElm = new Element("path");

					// println(tmpElm.toString(1));
					for(int i = 0; i < attrCount; i++)	{
						String attrName = stream.getAttributeLocalName(i);
						String attrValue = stream.getAttributeValue(i);

						if(attrName.equals("id")) tmpElm.addNsAttribute("android","name",attrValue);

						if(attrName.equals("style"))	{
							String[] values = attrValue.split(";");
							for(String value : values)	{
								String[] tmp = value.split(":");
								if(tmp[0].equals("opacity"))	{
									tmpElm.addNsAttribute("android","fillAlpha",tmp[1]);
									tmpElm.addNsAttribute("android","strokeAlpha",tmp[1]);
								}

								if(tmp[0].equals("fill"))	{
									if(tmp[1].startsWith("url"))	{
										
										Element aaptElem = new Element("aapt:attr");
										// println(gradName);
										aaptElem.addAttribute("name","android:fillColor",true);
										String gradName = tmp[1].replaceAll("url|\\(|#|\\)","");
										rootElement.setNamespace(aaptNamespace);

										ArrayList<Element> linked = linkGradients();
										
										for(Element grad : tmpGrads)	{
											if(gradName.equals(grad.getAttribute("name")))	{
												aaptElem.appendChild(grad);
												tmpElm.appendChild(aaptElem);
											}
										}
									}else	{
										tmpElm.addNsAttribute("android","fillColor",tmp[1]);
									}
								}

								if(tmp[0].equals("stroke"))	{
									if(tmp[1].startsWith("url"))	{
										rootElement.setNamespace(aaptNamespace);
										
										Element aaptElem = new Element("aapt:attr");
										aaptElem.addAttribute("name","android:strokeColor",true);
										String gradName = tmp[1].replaceAll("url|\\(|#|\\)","");
										
										ArrayList<Element> linked = linkGradients();
										
										for(Element grad : tmpGrads)	{
											if(gradName.equals(grad.getAttribute("name")))	{
												aaptElem.appendChild(grad);
												tmpElm.appendChild(aaptElem);
											}
										}
									}else	{
										tmpElm.addNsAttribute("android","strokeColor",tmp[1]);
									}
								}

								if(tmp[0].equals("stroke-width"))	{
									tmpElm.addNsAttribute("android","strokeWidth",tmp[1]);
								}

								if(tmp[0].equals("stroke-linecap"))	{
									tmpElm.addNsAttribute("android","strokeLineCap",tmp[1]);
								}

								if(tmp[0].equals("stroke-linejoin"))	{
									tmpElm.addNsAttribute("android","strokeLineJoin",tmp[1]);
								}

								if(tmp[0].equals("stroke-miterlimit"))	{
									tmpElm.addNsAttribute("android","strokeMiterLimit",tmp[1]);
								}
							}
						}
						
						if(attrName.equals("d")) tmpElm.addNsAttribute("android","pathData",attrValue);
					}
					if(tmpGroup != null)	{
						tmpGroup.appendChild(tmpElm);
					}else	{
						rootElement.appendChild(tmpElm);
					}
				}
			}
			stream.next();
		}
		document = new Document(rootElement);
	}

	public String getData()	{
		if(document != null)	{
			return document.toString(1);
		}
		return null;
	}

	public void saveAsFile(String filepath)	{
		
	}

	public ArrayList<Element> linkGradients()	{
		ArrayList<Element> linked = new ArrayList<Element>();
		for(int i = 0; i < tmpGrads.size(); i ++)	{
			Element grad = tmpGrads.get(i);
		// println();
			if(grad.hasAttribute("link"))	{
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
				linked.add(grad);
				// break;
			}
		}
		// println(linked.size() + "");
		return linked;
	}

	public Element transformation(Element elem, String name,String value)	{
		String[] tmp = value.split(",");
		
		if(name.equals("rotate"))	{
			double angle = Double.valueOf(tmp[0]);
			double pivotX = Double.valueOf(tmp[1]);
			double pivotY = Double.valueOf(tmp[2]);
			
			elem.addNsAttribute("android","pivotX",tmp[0]);
			elem.addNsAttribute("android","pivotY",tmp[1]);
			
			elem.addNsAttribute("android","rotation",tmp[2]);
		}

		if(name.equals("translate"))	{
			// double translateX = Double.valueOf(tmp[0]);
			// double translateY = Double.valueOf(tmp[1]);

			elem.addNsAttribute("android","translateX",tmp[0]);
			elem.addNsAttribute("android","translateY",tmp[1]);
		}

		if(name.equals("scale"))	{
			elem.addNsAttribute("android","scaleX",tmp[0]);
			elem.addNsAttribute("android","scaleY",tmp[1]);
		}

		if(name.equals("matrix"))	{
			
		}
		return elem;
	}

	public void println(String str)	{
		System.out.println(str);
	}

	public String tabs(int n)	{
		String tab = "";
		if(n > 0)	{
			for(int i = 0; i < n; i ++)	{
				tab += "\t";
			}
		}
		return tab;
	}
	// public Element populateAttributes(Element element)

	private class OpenTags	{
		boolean svg = false;
		boolean group = false;
		boolean path = false;
		boolean gradient = false;

		public void setTag(String key,boolean value)	{
			switch(key)	{
				case "svg":
					svg = value;
					break;
				
				case "group":
					path = value;
					break;
				
				case "path":
					group = value;
					break;
				
				case "gradient":
					gradient = value;
			}
		}

		public boolean getTag(String name)	{
			switch(name)	{
				case "svg":
					return svg;
					
				case "path":
					return path;
					
				case "group":
					return group;
					
				case "gradient":
					return gradient;
			}
			return false;
		}
	}


	private class Gradient	{
		String name;
		Element gradient;
		public Gradient(Element elem)	{
			if(elem != null)	{
				gradient = elem;
				name = elem.getAttribute("name");
			}
		}

		public Element getGradient()	{
			return gradient;
		}

		public String getName()	{
			return name;
		}
	}
}


