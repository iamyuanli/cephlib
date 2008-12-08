package fr.inserm.u794.lindenb.workbench.table;

import java.util.logging.Logger;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import com.sleepycat.je.DatabaseException;


/**
 * AbstractSpreadSheetModel
 * @author pierre
 *
 */
public class TableModel
	extends javax.swing.table.AbstractTableModel
	{
	private static Logger LOGGER= Logger.getAnonymousLogger();
	private static final long serialVersionUID = 1L;
	private Row buffer=null;
	private int bufferIndex=-1;
	private Table table;
	public TableModel(
			Table table
			)
		{
		this.table = table;
		this.addTableModelListener(new TableModelListener()
			{
			@Override
			public void tableChanged(TableModelEvent e) {
				bufferIndex=-1;
				}
			});
		}
	
	public Table getTable()
		{
		return this.table;
		}
	
	public int getRowCount()
		{
		return getTable().getRowCount();
		}
	
	@Override
	public int getColumnCount() {
		return getTable().getColumns().size();
		}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
		}
	
	@Override
	public String getColumnName(int column) {
		return getTable().getColumns().get(column).getLabel();
		}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
		Row row= getRowAt(rowIndex);
		String tokens[]=new String[getColumnCount()];	
		for(int i=0;i< tokens.length;++i)
			{
			tokens[i]=(row!=null?row.at(i):"");
			}
		tokens[columnIndex]=String.valueOf(value);
		row=new Row(tokens);
		try {
			getTable().getDatabase().put(rowIndex, row);
			fireTableCellUpdated(rowIndex, columnIndex); //table event
		} catch (DatabaseException e) {
			e.printStackTrace();
			}
		}
	
	protected Row getRowAt(int rowIndex)
		{
		try {
			if(bufferIndex==rowIndex)
				{
				return this.buffer;
				}
			bufferIndex= rowIndex;
			this.buffer= getTable().getDatabase().get(rowIndex);
			if(this.buffer==null) return null;
			return this.buffer;			
		} catch (Exception e)
			{
			LOGGER.info(e.getMessage());
			return null;
			}
		}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
		{
		Row row= getRowAt(rowIndex);
		if(row==null) return null;
		return row.at(columnIndex);			
		}
	
	public void close() throws DatabaseException
		{
		getTable().getDatabase().close();
		}
}	
