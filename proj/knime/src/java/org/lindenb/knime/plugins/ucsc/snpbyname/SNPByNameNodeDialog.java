package org.lindenb.knime.plugins.ucsc.snpbyname;



import org.knime.core.data.IntValue;
import org.knime.core.data.StringValue;
import org.lindenb.knime.plugins.ucsc.sql.UCSCByNameNodeDialog;

public class SNPByNameNodeDialog extends UCSCByNameNodeDialog
	{
	@SuppressWarnings("unchecked")
	public SNPByNameNodeDialog()
		{
		super("RS-Name",StringValue.class,IntValue.class);
		}
	}
