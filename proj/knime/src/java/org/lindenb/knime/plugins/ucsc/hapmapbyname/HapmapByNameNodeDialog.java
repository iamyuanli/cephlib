package org.lindenb.knime.plugins.ucsc.hapmapbyname;



import org.knime.core.data.IntValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.lindenb.knime.plugins.ucsc.sql.UCSCByNameNodeDialog;

public class HapmapByNameNodeDialog extends UCSCByNameNodeDialog
	{
	@SuppressWarnings("unchecked")
	public HapmapByNameNodeDialog()
		{
		super("RS-Name",StringValue.class,IntValue.class);
		
		DialogComponentButtonGroup db= new DialogComponentButtonGroup(
				new SettingsModelString(HapmapByNameNodeModel.KEY_POPULATION,HapmapByNameNodeModel.POPULATIONS[0]),
				true,
				"Population",
				HapmapByNameNodeModel.POPULATIONS
				);
			addDialogComponent(db);
		}
	}
