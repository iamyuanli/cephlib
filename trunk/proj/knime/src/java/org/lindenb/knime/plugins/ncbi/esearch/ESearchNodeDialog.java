package org.lindenb.knime.plugins.ncbi.esearch;


import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.lindenb.knime.plugins.MyNodeDialog;

public class ESearchNodeDialog extends MyNodeDialog
	{
	@SuppressWarnings("unchecked")
	public ESearchNodeDialog()
		{
		DialogComponentStringSelection db= new DialogComponentStringSelection(
			new SettingsModelString(ESearchNodeModel.KEY_DATABASE,"pubmed"),
			"Database",
			ESearchNodeModel.getDatabases()
			);
		addDialogComponent(db);
		
		DialogComponentColumnNameSelection col= new DialogComponentColumnNameSelection(
			new SettingsModelColumnName(ESearchNodeModel.KEY_TERM,""),
			"Query",0,true,
			new Class[]{org.knime.core.data.StringValue.class}
			);
		addDialogComponent(col);
		
		
		DialogComponentNumber start= new DialogComponentNumber(
			new SettingsModelInteger(ESearchNodeModel.KEY_START,0),
			"Start",
			1
			);
		addDialogComponent(start);
		
		
		DialogComponentNumber limit= new DialogComponentNumber(
				new SettingsModelInteger(ESearchNodeModel.KEY_LIMIT,50),
				"Limit",
				1
				);
		addDialogComponent(limit);
		
		}
	}
