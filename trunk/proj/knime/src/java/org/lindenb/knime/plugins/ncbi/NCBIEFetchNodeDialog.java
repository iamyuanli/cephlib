package org.lindenb.knime.plugins.ncbi;

import org.knime.core.data.IntValue;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.lindenb.knime.plugins.MyNodeDialog;


public class NCBIEFetchNodeDialog extends MyNodeDialog
	{
	@SuppressWarnings("unchecked")
	public NCBIEFetchNodeDialog(String label)
		{
		DialogComponentColumnNameSelection col= new DialogComponentColumnNameSelection(
				new SettingsModelColumnName(NCBIEFetchNodeModel.CFG_KEY_COLUMN_ID,""),
				label,0,true,
				new Class[]{IntValue.class}
				);
		addDialogComponent(col);
		}
	}
