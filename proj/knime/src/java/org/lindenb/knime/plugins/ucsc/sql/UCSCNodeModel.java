package org.lindenb.knime.plugins.ucsc.sql;

import org.lindenb.knime.plugins.sql.MySQLNodeModel;


public class UCSCNodeModel extends MySQLNodeModel
	{
	public UCSCNodeModel(int inDataPort,int outDataPort)
		{
		super(inDataPort,outDataPort,"com.mysql.jdbc.Driver","jdbc:mysql://genome-mysql.cse.ucsc.edu/hg18","genome","");
		}
	}
