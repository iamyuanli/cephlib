package fr.inserm.u794.lindenb.workbench.table;


import org.lindenb.berkeley.SingleMapDatabase;

public class Table extends TableRef
	{
	private SingleMapDatabase<Integer, Row> database=null;
	
	public Table()
		{
		}
	
	public Table(TableRef cp)
		{
		super(cp);
		}
	
	public SingleMapDatabase<Integer, Row> getDatabase() {
		return database;
		}
	
	public void setDatabase(SingleMapDatabase<Integer, Row> database) {
		this.database = database;
		}
	
	
	}
