package fr.cephb.lindenb.bio.ncbo.bioportal;


import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.lindenb.lang.IllegalInputException;
import org.lindenb.xml.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class NCBOConcept
	{
	protected static class NCBOConceptBeanImpl
		implements NCBOConceptBean
		{
		protected String id;
		protected String label;
		protected String description;

		@Override
		public String getDescription() {
			return this.description==null?getLabel():this.description;
		}
		@Override
		public String getId() {
			return this.id;
		}
		
		@Override
		public String getLabel() {
			return this.label==null?getId():this.label;
		}
		
		@Override
		public String toString() {
			return "{"+getId()+" "+getLabel()+" "+getDescription()+"}";
			}
		}
	
	protected static class NCBOClassBeanImpl
	 extends NCBOConceptBeanImpl
	 implements NCBOClassBean
	 	{
		List<NCBOConceptBean> subclasses= new ArrayList<NCBOConceptBean>();
		NCBOConceptBean superClass;
		
		public List<NCBOConceptBean> getSubClasses()
			{
			return subclasses;
			}

		public NCBOConceptBean getSuperClass() {
			return superClass;
			}
		
		@Override
		public String toString() {
			return super.toString()+"\n parent:"+getSuperClass()+"\n child:"+getSubClasses();
			}
	 	}
	
	

	
	
	public NCBOClassBean search(int ontologyVersionId, String conceptId)
	   throws IOException
		{
		NCBOClassBeanImpl bean=null;
		
		StringBuilder builder= new StringBuilder(
			NCBO.BIOPORTAL_URL);
		builder.append("/concepts/");
		builder.append(ontologyVersionId);
		builder.append("/");
		builder.append(URLEncoder.encode(conceptId,"UTF-8"));
			
		try
			{
			DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
			factory.setCoalescing(true);
			factory.setExpandEntityReferences(true);
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setNamespaceAware(false);
			DocumentBuilder docbuild= factory.newDocumentBuilder();
			Document dom=docbuild.parse(builder.toString());
			Element root= dom.getDocumentElement();
			if(root==null) return null;
			Element data= XMLUtilities.firstChild(root, "data");
			if(data==null) throw new IllegalInputException("<data> missing");
			Element classBean = XMLUtilities.firstChild(data, "classBean");
			if(classBean==null)  throw new IllegalInputException("<classBean> missing");
			bean= new NCBOClassBeanImpl();
			Element id=  XMLUtilities.firstChild(classBean, "id");
			if(id==null)  throw new IllegalInputException("<id> missing");
			bean.id= id.getTextContent().trim();
			Element label=  XMLUtilities.firstChild(classBean, "label");
			if(label==null)  throw new IllegalInputException("<label> missing");
			bean.label= label.getTextContent().trim();
			Element relations= XMLUtilities.firstChild(classBean, "relations");
			if(relations==null)  throw new IllegalInputException("<relations> missing");
			for(Element entry: XMLUtilities.elements(relations,"entry"))
				{
				Element string= XMLUtilities.firstChild(entry, "string");
				if(string==null) continue;
				String key= string.getTextContent().trim();
				if(key.equals("rdfs:comment"))
					{
					bean.description= entry_list_string(entry);
					}
				else if(key.equals("SuperClass") || key.equals("SubClass"))
					{
					Element list= XMLUtilities.firstChild(entry, "list");
					if(list==null)  throw new IllegalInputException("<list> missing");
					for(Element clazz: XMLUtilities.elements(list, "classBean"))
						{
						NCBOConceptBean linked= parseLinkedClass(clazz);
						if(linked==null) continue;
						if(key.equals("SuperClass"))
							{
							bean.superClass=linked;
							}
						else
							{
							bean.subclasses.add(linked);
							}
						}
					
					}
				}
			
			return bean;
			}
		catch(IOException err)
			{
			throw err;
			}
		catch(Exception err)
			{
			throw new IOException(err);
			}
		}
	
	private NCBOConceptBean parseLinkedClass(Element classBean) throws IOException
		{
		NCBOConceptBeanImpl bean=new NCBOConceptBeanImpl();
		Element id=  XMLUtilities.firstChild(classBean, "id");
		if(id==null)  throw new IllegalInputException("<id> missing");
		bean.id= id.getTextContent().trim();
		Element label=  XMLUtilities.firstChild(classBean, "label");
		if(label==null)  throw new IllegalInputException("<label> missing");
		bean.label= label.getTextContent().trim();
		
		return bean;
		}
	
	private String entry_list_string(Element entry) throws IOException
		{
		Element list= XMLUtilities.firstChild(entry, "list");
		if(list==null)  throw new IllegalInputException("<list> missing");
		Element string= XMLUtilities.firstChild(list, "string");
		if(string==null)  throw new IllegalInputException("<string> missing");
		return string.getTextContent().trim();
		}
	

	public static void main(String[] args)
		{
		try {
			NCBOConcept app= new NCBOConcept();
			System.out.println(app.search(39002,"BRO:Computational_Service"));
			}
		catch (Exception e) {
			e.printStackTrace();
			}
		}
	
	}
