package fr.inserm.u794.lindenb.workbench.table;

import org.lindenb.swing.table.GenericTableModel;

/**
 * ColumnModel
 * @author pierre
 *
 */
public class ColumnModel extends GenericTableModel<Column>
	{
	private static final long serialVersionUID = 1L;

	public ColumnModel()
		{
		}
	
	
	@Override
	public String getColumnName(int column) {
		switch (column)
			{
			case 0: return "Index";
			case 1: return "Label";
			}
		return null;
		}
	
	@Override
	public Class<?> getColumnClass(int column) {
		switch (column)
			{
			case 0: return Integer.class;
			case 1: return String.class;
			}
		return null;
		}
	
	@Override
	public Object getValueOf(Column c, int column) {
		switch (column)
			{
			case 0: return 1+c.getIndex();
			case 1: return c.getLabel();
			}
		return null;
		}

	@Override
	public int getColumnCount()
		{
		return 2;
		}
	
	
	}
