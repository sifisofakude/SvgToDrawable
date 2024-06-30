package com.slambyte.util.xml;

import java.util.ArrayList;

import com.slambyte.util.Tabs;

public class Element	{
	String name;
	ArrayList<String> namespaces = new ArrayList<String>();
	ArrayList<Element> children = new ArrayList<Element>();
	ArrayList<Attribute> attributes = new ArrayList<Attribute>();

	public Element(String name)	{
		this.name = name;
	}

	public Element(String namespace,String name)	{
		this.name = name;
		this.namespaces.add(namespace);
	}

	public Element(String name, ArrayList<Attribute> attrs)	{
		this.name = name;
		attributes = attrs;
	}

	public Element(String namespace,String name, ArrayList<Attribute> attrs)	{
		this.name = name;
		attributes = attrs;
		this.namespaces.add(namespace);
	}

	public void appendChild(Element child)	{
		if(child != null)	{
			children.add(child);
		}
	}

	public void appendChildAt(int index, Element child)	{
		if(child != null && index > -1 && index < children.size())	{
			children.add(index,child);
		}
	}

	public void addAttribute(String name, String value)	{
		Attribute attr = new Attribute(name,value);
		for(int i = 0; i < attributes.size(); i ++)	{
			if(attributes.get(i).getName().equals(name))	{
				attributes.remove(i);
				attributes.add(attr);
				// System.out.prtln("im in" + name);
				return;
			}
		}
				// System.out.println("im out" + name);
		attributes.add(new Attribute(name,value));
	}

	public void addAttributeAt(int index,String name, String value)	{
		if(index < 0 || index >= attributes.size()) return;
		
		Attribute attr = new Attribute(name,value);
		for(int i = 0; i < attributes.size(); i ++)	{
			if(attributes.get(i).getName().equals(name))	{
				attributes.remove(i);
				break;
			}
		}
		attributes.add(index,attr);
	}

	public void addAttribute(String name, String value, boolean inline)	{
		Attribute attr = new Attribute(name,value,inline);
		for(int i = 0; i < attributes.size(); i ++)	{
			if(attributes.get(i).getName().equals(name))	{
				attributes.remove(i);
				attributes.add(attr);
				return;
			}
		}
		attributes.add(attr);
	}

	public void addNsAttribute(String namespace,String name, String value)	{
		if(namespace != null)	{
			Attribute attr = new Attribute(namespace,name,value);
			for(int i = 0; i < attributes.size(); i ++)	{
				if(attributes.get(i).getName().equals(name))	{
					attributes.remove(i);
					attributes.add(attr);
					return;
				}
			}
			attributes.add(attr);
		}
	}

	public void addNsAttributeAt(int index,String namespace,String name, String value)	{
		if(index < 0 || index >= attributes.size()) return;
		
		if(namespace != null)	{
			Attribute attr = new Attribute(namespace,name,value);
			for(int i = 0; i < attributes.size(); i ++)	{
				if(attributes.get(i).getName().equals(name))	{
					attributes.remove(i);
					break;
				}
			}
			attributes.add(index,attr);
		}
	}

	public void addNsAttribute(String namespace,String name, String value, boolean inline)	{
		if(namespace != null)	{
			Attribute attr = new Attribute(namespace,name,value,inline);
			for(int i = 0; i < attributes.size(); i ++)	{
				if(attributes.get(i).getName().equals(name))	{
					attributes.remove(i);
					attributes.add(attr);
					return;
				}
			}
			attributes.add(attr);
		}
	}

	public void setNamespace(String namespace)	{
		if(namespace != null)	{
			if(namespaces.size() == 0)	{
				namespaces.add(namespace);
			}
			else	{
				if(namespaces.indexOf(namespace) > -1) return;
					namespaces.add(namespace);
				// for(String ns : namespaces)	{
					// if(ns.equals(namespace)) break;
				// }
			}
			// System.out.println(namespace);
		}
	}

	public void setNamespaces(ArrayList<String> namespaces)	{
		if(namespaces != null)	{
			if(this.namespaces.size() == 0)	{
				this.namespaces.addAll(namespaces);
			}
			else	{
				for(int i = 0; i < namespaces.size(); i ++)	{
					String ns = namespaces.get(i);
					for(int k = 0; k < this.namespaces.size(); k ++)	{
						if(ns.equals(this.namespaces.get(k))) break;
						this.namespaces.add(ns);
					}
				}
			}
		}
	}

	public void changeLocalName(String name)	{
		if(!name.equals(""))	{
			this.name = name;
		}
	}

	public ArrayList<Element> getChildren()	{
		return children;
	}

	public Element getChildAt(int index)	{
		if(index > -1 && index < children.size())	{
			return children.get(index);
		}
		return null;
	}

	public int childCount()	{
		return children.size();
	}

	public String getAttribute(String name)	{
		String attrValue = null;
		for(Attribute attr : attributes)	{
			if(attr.getName().equals(name))	{
				// System.out.println(attr.getValue());
				attrValue = attr.getValue();
				break;
			}
		}
		return attrValue;
	}

	public String getLocalName()	{
		return name;
	}

	public Element getElement()	{
		return this;
	}

	public boolean hasAttribute(String name)	{
		if(getAttribute(name) != null)	{
			return true;
		}
		return false;
	}

	public void removeAttribute(String name)	{
		for(int i = 0; i < attributes.size(); i ++)	{
			String attr = attributes.get(i).getName();
			if(attr.equals(name))	{
				attributes.remove(i);
				break;
			}
		}
	}

	public void removeChildAt(int index)	{
		if(index > -1 && index < children.size())	{
			children.remove(index);
		}
	}

	public void removeChildren()	{
		children = new ArrayList<Element>();
	}

	public String toString(int nTabs)	{
		Tabs cTabs = new Tabs();
		String tabs = cTabs.tabs(nTabs);
		String str = "<" + name;
		for(String ns : namespaces)	{
			str += "\n" + tabs + ns;
		}
		
		for(Attribute attr : attributes)	{
			if(attr.isInline())	{
				str += " " + attr.toString();
			}else	{
				str += "\n" + tabs + attr.toString();
			}
		}

		if(children.size() > 0)	{
			str += ">";

			for(Element elem : children)	{
				str += "\n" + tabs + elem.toString(nTabs+1);
			}
			tabs = cTabs.tabs(nTabs-1);
			str += "\n" + tabs + "</" + name + ">";
		}else	{
			str	+= "/>";
		}
		return str;
	}
}
