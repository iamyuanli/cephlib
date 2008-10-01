package fr.cephb.lindenb.berkeley;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lindenb.lang.ResourceUtils;
import org.lindenb.xml.XMLUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class OPERON
	{
	public static final String NS="http://operon.cng.fr";
	}

/**
 * 
 * AbstractCodeGenerator
 *
 */
public abstract class AbstractCodeGenerator
	{
	
	protected static class SnippetGenerator
		{
		String content;
		private HashMap<String, String> replace= new HashMap<String, String>();
		SnippetGenerator(String content)
			{
			this.content = content;
			if(this.content==null) throw new NullPointerException();
			}
		public SnippetGenerator put(String search,String replace)
			{
			this.replace.put(search, replace);
			return this;
			}
		
		@Override
		public String toString() {
			String s= this.content;
			for(String key: this.replace.keySet())
				{
				s=s.replaceAll(key, this.replace.get(key));
				}
			return s;
			}
		
		public SnippetGenerator write(PrintWriter out)
			{
			out.print(this.toString());
			return this;
			}
		}
	
	public class DatabaseRecord
		extends NodeWrapper
		{
		Structure structure;
		Vector<Field> fields=new Vector<Field>();
		public DatabaseRecord(Element root,Structure structure)
			throws IOException
			{
			super(root);
			this.structure=structure;
			for(Element e: XMLUtilities.elements(root, OPERON.NS, "column-ref"))
				{
				Attr att= e.getAttributeNode("ref");
				if(att==null) throw new IOException("@ref missing");
				Field f= this.structure.name2field.get(att.getValue());
				if(f==null)  throw new IOException("cannot find element "+att.getValue());
				this.fields.addElement(f);
				}
			}
		
		public Collection<SimpleField> getSimpleFields()
			{
			ArrayList<SimpleField> sf= new ArrayList<SimpleField>();
			for(Field f: this.fields)
				{
				if(f instanceof SimpleField) sf.add(SimpleField.class.cast(f));
				}
			
			return sf;
			}
		}
	
	public class DatabaseInstance
	extends NodeWrapper
		{
		Structure structure;
		public DatabaseInstance(Element root,DefinitionFile df)
			throws IOException
			{
			super(root);
			Element e = XMLUtilities.firstChild(root, OPERON.NS, "table-ref");
			if(e==null)  throw new IOException("cannot find element table-ref");
			Attr att= e.getAttributeNode("ref");
			if(att==null) throw new IOException("@ref missing");
			this.structure=df.findStructureByName(att.getValue());
			if(this.structure==null)  throw new IOException("cannot find element "+att.getValue());
			}
		
		}
	
	
	protected  class NodeWrapper
		{
		Element element;
		public NodeWrapper(Element element)
			{
			this.element = element;
			}
		public Element getElement() { return this.element;}
		
		
		public String getAttribute(String name,String defaultValue)
			{
			Attr att= getElement().getAttributeNode(name);
			if(att==null) return defaultValue;
			return att.getValue();
			}
		
		public String getAttribute(String name)
			{
			Attr att= getElement().getAttributeNode(name);
			if(att==null) throw new IllegalArgumentException("No such attribute: "+name);
			return att.getValue();
			}
		}
	
	public  class Component
	extends NodeWrapper
		{
		Component(Element e)
			{
			super(e);
			}
		
		public String getName()
			{
			return getAttribute("name");
			}
		
		public String getDescription()
			{
			Element e=XMLUtilities.firstChild(getElement(), OPERON.NS, "description");
			return e==null?getName():e.getTextContent();
			}
		
		public String  getXmlTagName()
			{
			return getName();
			}
		}
	
	public class DefinitionFile
		extends NodeWrapper
		{
		private File file;
		private Vector<Structure> structures= new Vector<Structure>();
		private Vector<DatabaseInstance> dbInstances= new Vector<DatabaseInstance>();
		
		DefinitionFile(File file,Element e)
			{
			super(e);
			this.file=file;
			}
		
		public Structure findStructureByName(String name)
			{
			for(Structure structure: getStructures())
				{
				if(structure.getName().equals(name)) return structure;
				}
			return null;
			}
		
		public Collection<Structure> getStructures()
			{
			return this.structures;
			}
		
		public Collection<DatabaseInstance> getDatabases()
			{
			return this.dbInstances;
			}
		
		public String getName()
			{
			return getAttribute("name");
			}
		}
	
	protected  class Field
		extends Component
		{
		Structure mystructure;
		String name;
		Field(Element e)
			{
			super(e);
			}
		
		
		}
	
	public  class SimpleField
		extends Field
		{
		SimpleField(Element e)
			{
			super(e);
			}
		
		public String freePtr(String ptrName)
			{
			if(!isPointer()) return "";
			return "Free("+ptrName+"->"+getName()+"); "+ptrName+"->"+getName()+"=NULL;";
			}
		
		public String printXML(String outName, String ptrName)
			{
			if(getType().equals("char*"))
				{
				return "xmlputs("+outName+","+ptrName+"->"+getName()+");";
				}
			if(getType().equals("int") || getType().equals("short"))
				{
				return "fprintf("+outName+",\"%d\","+ptrName+"->"+getName()+");";
				}
			if(getType().equals("long int") ||getType().equals("long"))
				{
				return "fprintf("+outName+",\"%ld\","+ptrName+"->"+getName()+");";
				}
			if(getType().equals("char"))
				{
				return "xmlputc("+outName+","+ptrName+"->"+getName()+");";
				}
			throw new RuntimeException("cannot handle"+getType());
			}
		
		public String printJSON(String outName, String ptrName)
			{
			if(getType().equals("char*"))
				{
				return "if("+ptrName+"->"+getName()+"==NULL) { fputs(\"null\","+outName+");} else { fprintf("+outName+",\"\\\"%s\\\"\","+ptrName+"->"+getName()+");}";
				}
			if(getType().equals("char"))
				{
				return "fprintf("+outName+",\"\\\"%c\\\"\","+ptrName+"->"+getName()+");";
				}
			return printText(outName,ptrName);
			}
		
		public String printText(String outName, String ptrName)
			{
			if(getType().equals("char*"))
				{
				return "if("+ptrName+"->"+getName()+"==NULL) { fputs(\"null\","+outName+");} else { fprintf("+outName+",\"%s\","+ptrName+"->"+getName()+");}";
				}
			if(getType().equals("int") || getType().equals("short"))
				{
				return "fprintf("+outName+",\"%d\","+ptrName+"->"+getName()+");";
				}
			if(getType().equals("long int") ||getType().equals("long"))
				{
				return "fprintf("+outName+",\"%ld\","+ptrName+"->"+getName()+");";
				}
			if(getType().equals("char"))
				{
				return "fprintf("+outName+",\"%c\","+ptrName+"->"+getName()+");";
				}
			throw new RuntimeException("cannot handle"+getType());
			}
		
		public String packDBT(String dbtName)
			{
			return packDBT(dbtName,null);
			}
		
		public String packDBT(String dbtName,String prtName)
			{
			String prefix= (prtName==null?"":prtName+"->");
			if(getType().equals("char*"))
				{
				return "berkeleyPackString("+dbtName+","+ prefix+getName()+")";
				}
			return "berkeleyPack("+dbtName+",&("+prefix+getName()+"),sizeof("+getType()+"))";
			}

		public String unpackDBT(String dbtName,String prtName,String sizeName)
			{
			String prefix=prtName+"->";
			if(getType().equals("char*"))
				{
				return prefix+getName()+"=strdup(&(((char*)"+dbtName+")["+sizeName+"])); "+sizeName+"+=strlen("+prefix+getName()+");";
				}
			return "memcpy(&(((char*)"+dbtName+")["+sizeName+"]),&("+prefix+getName()+"),sizeof("+getType()+")); "+sizeName+"+=sizeof("+getType()+");";
			}
		
		public String getType()
			{
			return getAttribute("type");
			}
		
		public String getConstType()
			{
			if(getType().endsWith("*"))
				{
				return "const "+getType();
				}
			return getType();
			}
		
		public boolean isPointer()
			{
			return getType().endsWith("*");
			}
		}
	
	public  class Structure
		extends Component
		{
		// fields as their are declared
		private Vector<Field> fields= new Vector<Field>();
		//map name to field
		private HashMap<String, Field> name2field= new HashMap<String, Field>();
		/* key */
		private DatabaseRecord key;
		/** value */
		private DatabaseRecord value;
		
		public Structure(Element e)
			{
			super(e);	
			}
		
		public DatabaseRecord getKey() { return this.key;}
		public DatabaseRecord getValue() { return this.value;}
		
		public String getPrefix()
			{
			return getAttribute("prefix",getName()+"_");
			}
		
		public String getTypedef()
			{
			return getAttribute("typedef",getName()+"_t");
			}
		
		public String getPtr()
			{
			return getAttribute("ptr",getName()+"Ptr");
			}
		
		public String getDbType()
			{
			return getAttribute("db-type",getName()+"DB_TREE");
			}
		
		public String getDbName()
			{
			return getAttribute("db-name",getName()+".db");
			}
		
		public Collection<SimpleField> getSimpleFields()
			{
			ArrayList<SimpleField> sf= new ArrayList<SimpleField>();
			for(Field f: fields)
				{
				if(f instanceof SimpleField) sf.add(SimpleField.class.cast(f));
				}
			return sf;
			}
		
		
		}
	
	protected Structure newStructure(Element table)
		{
		return new Structure(table);
		}
	
	private Structure parseTable(Element table) throws IOException
		{
		Structure structure= newStructure(table);
		Element fields= XMLUtilities.firstChild(table, OPERON.NS, "fields");
		if(fields==null) throw new IOException("op:fields not found");
		for(Node n= fields.getFirstChild();
			n!=null;
			n=n.getNextSibling()
			)
			{
			if(XMLUtilities.isA(n, OPERON.NS, "column"))
				{
				parseField(structure,Element.class.cast(n));
				}
			}
		if(structure.fields.isEmpty())
			{
			throw new IOException("structure is empty");
			}
		
		Element dbt = XMLUtilities.firstChild(table, OPERON.NS, "db-key");
		if(dbt==null) throw new IOException("op:db-key not found");
		structure.key= new DatabaseRecord(dbt,structure);
		dbt = XMLUtilities.firstChild(table, OPERON.NS, "db-value");
		if(dbt==null) throw new IOException("op:db-value not found");
		structure.value= new DatabaseRecord(dbt,structure);
		return structure;
		}
	
	protected SimpleField newField(Element e)
		{
		return new SimpleField(e);
		}
	
	private void parseField(Structure structure,Element root) throws IOException
		{
		SimpleField field= newField(root);
		field.mystructure=structure;
		structure.fields.addElement(field);
		if(structure.name2field.containsKey(field.getName()))
			{
			throw new IOException("field "+field.getName()+" defined twice");
			}
		structure.name2field.put(field.getName(), field);
		
		
		}
	
	/** all definition */
	protected Vector<DefinitionFile> definitionFiles= new Vector<DefinitionFile>();
	
	/** ouput directory */
	private File outputDir=null;
	
	/** properties containing snippet */ 
	private Properties snippetsCollection=null;
	
	
	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
		}
	
	public File getOutputDir()
		{
		return outputDir;
		}
	
	public abstract void makeCode() throws Exception;
	protected abstract String getSnippetResourceName();
	
	protected SnippetGenerator getSnippet(String name)
		{
		return new SnippetGenerator(this.snippetsCollection.getProperty(name));
		}
	
	public void parse(File xml) throws IOException
		{
		try
			{
			this.snippetsCollection= new Properties();
			this.snippetsCollection.loadFromXML(ResourceUtils.getResourceAsStream(
					AbstractCodeGenerator.class, getSnippetResourceName()));
			
			DocumentBuilderFactory f= DocumentBuilderFactory.newInstance();
			f.setNamespaceAware(true);
			f.setCoalescing(true);
			f.setExpandEntityReferences(true);
			f.setNamespaceAware(true);
			f.setValidating(false);
			f.setIgnoringComments(true);
			f.setIgnoringElementContentWhitespace(false);
			DocumentBuilder b=f.newDocumentBuilder();
			Document doc=b.parse(xml);
			Element root=doc.getDocumentElement();
			if(root==null) throw new IOException("No root");
			if(!(	root.getLocalName().equals("operon") &&
					OPERON.NS.equals(root.getNamespaceURI())
				)) throw new IOException("not a op:operon root");
			
			DefinitionFile file= new DefinitionFile(xml,root);
			this.definitionFiles.add(file);
			
			for(Element table:XMLUtilities.elements(root, OPERON.NS, "table"))
				{
				Structure s=parseTable(table);
				file.structures.addElement(s);
				}
			
			for(Element dbi:XMLUtilities.elements(root, OPERON.NS, "op:database"))
				{
				DatabaseInstance db= new DatabaseInstance(dbi,file);
				file.dbInstances.addElement(db);
				}
			}
		catch(IOException err)
			{
			throw err;
			}
		catch(Throwable err)
			{
			throw new IOException(err);
			}
		}
	
	}
