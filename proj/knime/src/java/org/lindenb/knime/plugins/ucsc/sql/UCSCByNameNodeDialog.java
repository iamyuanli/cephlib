package org.lindenb.knime.plugins.ucsc.sql;

import org.knime.core.data.DataValue;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;


public class UCSCByNameNodeDialog extends UCSCNodeDialog {

	protected UCSCByNameNodeDialog(String columnName,Class<? extends DataValue>...classes)
		{
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelColumnName(UCSCByNameNodeModel.KEY_COLUMN,columnName),
				columnName,0,true,
				classes
				));
		}
	}
