package fr.inserm.u794.lindenb.workbench.table;



public class SortKey
	{
	private static final long serialVersionUID = 1L;

	
	private int index;
	private boolean casesensible=true;
	private SortType type;
	public SortKey(int index,SortType type,boolean casesensible)
		{
		this.index=index;
		this.type=type;
		this.casesensible= casesensible;
		}
	public int getIndex() {
		return index;
		}

	
	public SortType getType() {
		return type;
		}
	
	public boolean isCasesensible() {
		return casesensible;
		}
	
	
	public String createIndexedString(Row row)
		{
		String src= row.at(getIndex());
		if(isCasesensible()) src=src.toLowerCase();
		if(getType()==SortType.NUMERIC)
			{
			int nZero= 50-src.length();
			StringBuilder b= new StringBuilder(nZero+src.length());
			for(int i=0;i<nZero;++i) b.append('0');
			b.append(src);
			src=b.toString();
			}
		return src;
		}
	
	

	}
