package fr.cephb.lindenb.ant;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.tools.ant.BuildException;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.lindenb.sql.SQLUtilities;



import fr.cephb.lindenb.bio.ucsc.UCSCConstants;
import fr.cephb.lindenb.sql.MySQLConstants;

public class UCSCPojoTask extends  org.apache.tools.ant.Task
{
private String database=UCSCConstants.DATABASE; 
private String tables="";
private String javaPackage="";
private File directory;
private File templateDir;
public UCSCPojoTask()
	{
	
	}




public void setDatabase(String database) {
	this.database = database;
	}

public void setTables(String tables) {
	this.tables = tables;
	}

public void setDirectory(File directory)
	{
	this.directory = directory;
	}

public void setPackage(String javaPackage) {
	this.javaPackage = javaPackage;
	}

private static String javaName(String s)
	{
	if(s.length()<=1) return s.toUpperCase();
	return s.substring(0,1).toUpperCase()+s.substring(1);
	}

@Override
public void execute() throws BuildException
	{
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
		VelocityEngine ve = new VelocityEngine();

		ve.setProperty(Velocity.RESOURCE_LOADER,"classpath");
		ve.setProperty("class.resource.loader.class","fr.cephb.lindenb.ant.UCSCPojoTask");
		ve.init();
		Template template  = Velocity.getTemplate("./pojo.java.vm");
		for(String table: this.tables.split("[ \t,;]+"))
			{
			if(table.length()==0) continue;
			File classFile=new File(this.directory,javaName(table)+".java");
			if(con==null)
				{
				con= DriverManager.getConnection(
					UCSCConstants.URI+"://"+UCSCConstants.HOST+"/"+this.database,
					UCSCConstants.USER,
					UCSCConstants.PASSWORD
					);
				}
			
			Statement pstmt=con.createStatement();
			ResultSet row= pstmt.executeQuery("desc "+table);
			while(row.next())
				{
				for(int i=0;i< row.getMetaData().getColumnCount();++i)
					{
					System.out.println(row.getMetaData().getColumnLabel(i+1)+" : "+row.getString(i+1));
					}
				System.out.println("=");
				}
			pstmt.close();
			System.out.println("==========================");
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


public static void main(String[] args)
	{
	try {
		UCSCPojoTask p=new UCSCPojoTask();
		p.setTables("snp129 refGene");
		p.execute();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
}
