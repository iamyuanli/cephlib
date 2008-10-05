package fr.cephb.lindenb.ant;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.lindenb.sql.SQLUtilities;




import fr.cephb.lindenb.bio.ucsc.UCSCConstants;
import fr.cephb.lindenb.sql.MySQLConstants;

public class UCSCPojoTask extends  org.apache.tools.ant.Task
{
static public class Identifier
	{
	protected String description=null;
	protected String name=null;
	public Identifier()
		{
		this(null);
		}
	public Identifier(String name)
		{
		this.name=name;
		}
	public String getDescription() { return this.description==null?getName():this.description;}
	public String getName() { return name; }
	public String getJavaName() {return javaName(getName());}
	public String getNormalizedName() {return normalizeName(getName());}
	}

static public class Field
	extends Identifier
	{
	private String type;
	private String NULL;
	private String key;
	private String defaultValue;
	private String extra;
	public Field() { }
	public boolean isStream()
		{
		return
			this.type.equals("blob") ||
			this.type.equals("text") ||
			this.type.equals("longblob");
		}
	public String getType() { return type; }
	public String getNull() {return NULL;}
	public boolean isNull() {return !NULL.equals("NO");}
	public String getKey() {return key;}
	public String getValue()
		{
		if(defaultValue==null) return "null";
		if(isEnum()) return getJavaName()+"."+ new Identifier(this.defaultValue).getNormalizedName();
		if(getJavaType().equals("String")) return "\""+this.defaultValue+"\"";
		if(getJavaType().equals("char")) return "\'"+(this.defaultValue.length()==0?" ":this.defaultValue)+"\'";
		return this.defaultValue;
		}
	public String getExtra() {return extra;}
	
	public String getJavaType()
		{
		int size=-1;
		if(isEnum())
			{
			return getJavaName();
			}
		if(isSet())
			{
			return "java.util.Set<"+getJavaName()+">";
			}
		String s=type;
		int i=s.indexOf('(');
		if(i!=-1)
			{
			int j=s.indexOf(')',i+1);
			size=Integer.parseInt(s.substring(i+1,j));
			s=s.substring(0,i);
			}
		if(s.equals("char") && size==1) return (isNull()?"Character":"char");
		if(s.equals("varchar") || s.equals("char") || 
		   s.equals("blob")|| s.equals("longblob")) return "String";
		if(s.equals("int")) return (isNull()?"Integer":"int");
		if(s.equals("smallint")) return (isNull()?"Short":"short");
		if(s.equals("float")) return (isNull()?"Float":"float");
		throw new IllegalArgumentException(type);
		}
	public boolean isEnum() { return this.type.startsWith("enum(");}
	public boolean isSet() { return this.type.startsWith("set(");}
	public Vector<Identifier> getEnumItems()
		{
		String s= getType();
		if(isSet())
			{
			s=s.substring(4);
			}
		else if(isEnum())
			{
			s=s.substring(5);
			}
		
		s=s.substring(0,s.length()-1);
		Vector<Identifier> e=new Vector<Identifier>();
		for(String tok: s.split("[,]"))
			{
			tok=unquote(tok);
			e.add(new Identifier(tok));
			}
		return e;
		}
	}

static public  class Table
extends Identifier
	{

	private ArrayList<Field> fields= new ArrayList<Field>();
	Table()
		{
		}
	public Collection<Field> getFields()
		{
		return this.fields;
		}
	
	public String getName()
		{
		return name;
		}
	public String getJavaName()
		{
		return javaName(getName());
		}
	}
	
	
private String database=UCSCConstants.DATABASE; 
private String tables="";
private String prefix="";
private String javaPackage="";
private File todir;
private File templatesDir;
private boolean force=true;

public UCSCPojoTask()
	{
	
	}

public void setDatabase(String database) {
	this.database = database;
	}

public void setTables(String tables) {
	this.tables = tables;
	}

public void setTodir(File todir)
	{
	this.todir = todir;
	}

public void setPackage(String javaPackage) {
	this.javaPackage = javaPackage;
	}

public void setTemplatesDir(File templatesDir) {
	this.templatesDir = templatesDir;
	}
private static String normalizeName(String s)
	{
	if(s.equals("+")) return "PLUS";
	if(s.equals("-")) return "MINUS";
	if(s.equals("?")) return "UNKNOWN";
	if(s.equals("class")) return "clazz";
	return s.replace('-', '_').replaceAll("'", "_prime");
	}
private static String javaName(String s)
	{
	s=normalizeName(s);
	if(s.length()<=1) return s.toUpperCase();
	return s.substring(0,1).toUpperCase()+s.substring(1);
	}

private static String unquote(String str)
	{
	String s=str.trim();
	if(s.startsWith("\"") && s.endsWith("\"") && s.length()>1)
		{
		str= s.substring(1,s.length()-1);
		}
	else if(s.startsWith("\'") && s.endsWith("\'") && s.length()>1)
		{
		str =s.substring(1,s.length()-1);
		}
	return str;
	}

public void setForce(boolean force) {
	this.force = force;
	}

public void setPrefix(String prefix) {
	this.prefix = prefix;
	}

@Override
public void execute() throws BuildException
	{
	if(this.templatesDir==null ) throw new BuildException("@templatesDir missing");
	if(!this.templatesDir.exists() ) throw new BuildException(this.templatesDir.toString()+" does not exists");
	if(!this.templatesDir.isDirectory() ) throw new BuildException(this.templatesDir.toString()+" is not a directory");
	if(this.todir==null ) throw new BuildException("@todir missing");
	if(!this.todir.exists() ) throw new BuildException(this.todir.toString()+" does not exists");
	if(!this.todir.isDirectory() ) throw new BuildException(this.todir.toString()+" is not a directory");
	if(this.tables.trim().length()==0) throw new BuildException("@tables is emty");
	try
		{
		Class.forName(MySQLConstants.DRIVER);
		}
	catch(ClassNotFoundException err)
		{
		throw new BuildException(err);
		}
	Connection con=null;
	try
		{
		
		Properties props = new Properties();
        props.put(VelocityEngine.INPUT_ENCODING, "utf-8");
        props.put(VelocityEngine.FILE_RESOURCE_LOADER_PATH,this.templatesDir.toString());
        Velocity.init(props);
        
		
		for(String table: this.tables.split("[ \t,;]+"))
			{
			if(table.length()==0) continue;
			Template template  = Velocity.getTemplate("pojo.java.vm");
			
			File classFile=new File(this.todir,this.prefix+javaName(table)+".java");
			
			if(classFile.exists() && this.force==false) continue;
			
			if(con==null)
				{
				con= DriverManager.getConnection(
					UCSCConstants.URI+"://"+UCSCConstants.HOST+"/"+this.database,
					UCSCConstants.USER,
					UCSCConstants.PASSWORD
					);
				}
			Table struct= new Table();
			struct.name=table;
			Statement stmt=con.createStatement();
			ResultSet row= stmt.executeQuery("desc "+table);
			
			while(row.next())
				{

				Field f=new Field();
				struct.fields.add(f);
				f.name = row.getString(1);
				f.type = row.getString(2);
				f.NULL = row.getString(3);
				f.key = row.getString(4);
				f.defaultValue = row.getString(5);
				f.extra = row.getString(6);
				/*
				for(int i=0;i< row.getMetaData().getColumnCount();++i)
					{
					System.out.println(""+(i+1)+" : "+row.getMetaData().getColumnLabel(i+1)+" : "+row.getString(i+1));
					}
				System.out.println("=");
				*/
				}
			stmt.close();
			
			PreparedStatement pstmt=con.prepareStatement(
				"select autoSqlDef from tableDescriptions where tableName=?");
			pstmt.setString(1, table);
			row= pstmt.executeQuery();
			while(row.next())
				{
				String content=row.getString(1);
				int n=content.indexOf('(');
				if(n!=-1)
					{
					int n2= content.indexOf('\"');
					if(n2< n)
						{
						struct.description= content.substring(n2+1,n).trim();
						}
					content=content.substring(n);
					}
				
				for(String line:content.split("[\n]"))
					{
					line=line.trim();
					if(line.length()==0) continue;
					int n2= line.indexOf(';');
					if(n2==-1) continue;
					int n3=n2;
					while(n3>0 && !Character.isWhitespace(line.charAt(n3-1)))
						{
						--n3;
						}
					String fName=line.substring(n3,n2).trim();
					String fDesc=unquote(line.substring(n2+1).trim());
					for(Field f:struct.fields)
						{
						if(f.name.equals(fName))
							{
							f.description=fDesc;
							break;
							}
						}
					}
				}
			
			pstmt.close();
			
			
			VelocityContext context= new VelocityContext();
			context.put("package",
					this.javaPackage==null || this.javaPackage.length()==0?
					"":"package "+this.javaPackage+";"
					);
			context.put("prefix",this.prefix);
            context.put("struct",struct);
            
            FileWriter w= new FileWriter(classFile);
			template.merge(context, w);
			w.flush();
			w.close();
			}
		}
	catch(Throwable err)
		{
		throw new BuildException(err);
		}
	finally
		{
		SQLUtilities.safeClose(con);
		}
	}

/*
public static void main(String[] args)
	{
	try {
		UCSCPojoTask p=new UCSCPojoTask();
		p.setTodir(new File("/tmp/"));
		p.setTemplatesDir(new File("/home/pierre/cephlib/src/java/fr/cephb/lindenb/ant/"));
		p.setTables("snp129 cytoBand refGene");
		p.setPrefix("Hg18");
		p.setPackage("fr.cephb.lindenb.bio.ucsc.hg18");
		p.execute();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}*/
}
