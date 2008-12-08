package fr.inserm.u794.lindenb.workbench.table;

import java.util.ArrayList;
import java.util.List;

import org.lindenb.berkeley.SingleMapDatabase;

public class Table
	{
	private SingleMapDatabase<Integer, Row> database=null;
	private List<Column> columnLabels=new ArrayList<Column>();
	private int rowCount=0;
	private String name;
	
	public Table()
		{
		}
	
	public SingleMapDatabase<Integer, Row> getDatabase() {
		return database;
		}
	
	public void setDatabase(SingleMapDatabase<Integer, Row> database) {
		this.database = database;
		}
	
	public List<Column> getColumns()
		{
		return columnLabels;
		}
	
	public void setColumnLabels(List<Column> columnLabels) {
		this.columnLabels = columnLabels;
		}
	
	public int getRowCount() {
		return rowCount;
		}
	
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
		}
	
	public String getName() {
		return name==null?"":name;
		}
	
	public void setName(String name) {
		this.name = name;
		}
	
	
	}
