package fr.inserm.u794.lindenb.workbench.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.SortOrder;

import org.lindenb.util.Cast;
import org.lindenb.util.SmartComparator;

public class SortKey
	implements Comparator<Row>
	{
	public static class Multiple
		implements Comparator<Row>
		{
		private List<SortKey> keys;
		public Multiple(List<SortKey> keys)
			{
			this.keys=new ArrayList<SortKey>(keys);
			}
		
		@Override
		public int compare(Row o1, Row o2) {
			for(SortKey key:this.keys)
				{
				int i= key.compare(o1, o2);
				if(i!=0) return i;
				}
			return 0;
			}
		}
	
	private int index;
	private boolean casesensible=true;
	private SortOrder order;
	private SortType type;
	private SmartComparator smarter = SmartComparator.getInstance();
	public SortKey(int index,SortOrder order,SortType type,boolean casesensible)
		{
		this.index=index;
		this.order=order;
		this.type=type;
		this.casesensible= casesensible;
		}
	public int getIndex() {
		return index;
		}
	public SortOrder getOrder() {
		return order;
		}
	
	public SortType getType() {
		return type;
		}
	
	public boolean isCasesensible() {
		return casesensible;
		}
	@Override
	public int compare(Row o1, Row o2)
		{
		String a= o1.at(index);
		String b= o2.at(index);
		int i=0;
		switch(getType())
			{
			case LITERAL:
				{
				i= compareLiteral(a,b);
				break;
				}
			case NUMERIC:
				{
				i= compareNumeric(a,b);
				break;
				}
			case SMART:
				{
				i= smarter.compare(a,b);
				break;
				}
			}
		if(getOrder()==SortOrder.DESCENDING)
			{
			i*=-1;
			}
		return i;
		}
	
	private int compareLiteral(String a,String b)
		{
		int i=0;
		if(isCasesensible())
			{
			i= a.compareToIgnoreCase(b);
			}
		else
			{
			i= a.compareTo(b);
			}
	
		return i;
		}
	
	private int compareNumeric(String a,String b)
		{
		boolean okA= Cast.BigDecimal.isA(a);
		boolean okB= Cast.BigDecimal.isA(b);
		if(okA && okB)
			{
			return Cast.BigDecimal.cast(a).compareTo(Cast.BigDecimal.cast(b));
			}
		else if(!okA && !okB)
			{
			return compareLiteral(a,b);
			}
		else if(okA)
			{
			return -1;
			}
		else
			{
			return 1;
			}
		}
	}
