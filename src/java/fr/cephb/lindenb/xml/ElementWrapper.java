package fr.cephb.lindenb.xml;

import org.w3c.dom.Element;

public class ElementWrapper extends NodeWrapper {
public ElementWrapper(Element e)
	{
	super(e);
	}

public Element getElement()
	{
	return Element.class.cast(getNode());
	}
}
