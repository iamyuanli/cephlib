package org.lindenb.knime.plugins.ucsc.sql;

import org.knime.core.data.IntValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;


public class UCSCByPositionNodeDialog extends UCSCNodeDialog {

	@SuppressWarnings("unchecked")
	protected UCSCByPositionNodeDialog()
		{
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelColumnName(UCSCByPositionNodeModel.KEY_CHROM,"chrom"),
				"Chromosome",0,true,
				StringValue.class
				));
		
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelColumnName(UCSCByPositionNodeModel.KEY_CHROM_START,"chromStart"),
				"chromStart",0,true,
				IntValue.class
				));
		
		addDialogComponent(new DialogComponentColumnNameSelection(
				new SettingsModelColumnName(UCSCByPositionNodeModel.KEY_CHROM_END,"chromEnd"),
				"chromEnd",0,true,
				IntValue.class
				));
		
		addDialogComponent(new DialogComponentNumber(
			new SettingsModelInteger(UCSCByPositionNodeModel.KEY_EXTEND,0),"Extends",1));
		
		addDialogComponent(new DialogComponentBoolean(
				new SettingsModelBoolean(UCSCByPositionNodeModel.KEY_ONLY_ONE,true),
				"Only One"
				));
		
		}
	}
