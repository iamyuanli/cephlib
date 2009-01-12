package org.lindenb.knime.plugins.ucsc.sql;

import org.lindenb.knime.plugins.sql.MySQLNodeDialog;



public class UCSCNodeDialog extends MySQLNodeDialog
	{
	protected UCSCNodeDialog()
		{
		super("dbc:mysql://genome-mysql.cse.ucsc.edu/hg18","genome","");
		}
	}
