package fr.inserm.u794.lindenb.filemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class FileDescriptor
	{
	private File file;
	private Delimiter delimiter;
	private boolean tmp;
	public FileDescriptor(File file,Delimiter delimiter,boolean tmp)
		{
		this.file=file;
		this.delimiter=delimiter;
		this.tmp=tmp;
		}
	
	public File getFile() {
		return file;
		}
	
	public boolean isTmp() {
		return tmp;
		}
	
	public Delimiter getDelimiter() {
		return delimiter;
		}
	
	public void setDelimiter(Delimiter delimiter)
		{
		this.delimiter = delimiter;
		}
	
	public BufferedReader open() throws IOException
		{
		if(getFile().getName().toLowerCase().endsWith(".gz"))
			{
			return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(getFile()))));
			}
		else
			{
			return new BufferedReader(new FileReader(getFile()));
			}
		}
	
	public String getFirstLine() throws IOException
		{
		BufferedReader r= open();
		String line=r.readLine();
		r.close();
		return line;
		}
	}
