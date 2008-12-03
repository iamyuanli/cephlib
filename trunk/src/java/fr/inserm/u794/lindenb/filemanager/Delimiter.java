package fr.inserm.u794.lindenb.filemanager;

public enum Delimiter {
tab('\t'),comma(','),space(' '),pipe('|');
private char c;
Delimiter(char c)
	{
	this.c=c;
	}
public char charValue()
	{
	return this.c;
	}
}
