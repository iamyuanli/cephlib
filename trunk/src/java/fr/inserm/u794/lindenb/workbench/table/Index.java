package fr.inserm.u794.lindenb.workbench.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.lindenb.berkeley.SingleMapDatabase;
import org.lindenb.util.Pair;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;

import fr.inserm.u794.lindenb.workbench.Berkeley;

public class Index
	{
	private Random RAND= new Random(System.currentTimeMillis());
	
			
	
	
	private SingleMapDatabase.STRING<Index> database;
	public Index(
		long documentId,
		int columnIndexed
		) throws DatabaseException
		{
		DatabaseConfig cfg= new DatabaseConfig();
		cfg.setAllowCreate(true);
		cfg.setExclusiveCreate(false);
		cfg.setReadOnly(false);
		cfg.setTemporary(true);
		Database db= Berkeley.getInstance().getEnvironment().openDatabase(null,"tmp"+System.currentTimeMillis()+"_"+RAND.nextInt(), cfg);
		this.database= new SingleMapDatabase.STRING(db,Indexes.BINDING);
		
		}
	
	public void close() throws DatabaseException
		{
		this.database.close();
		}
	
	public static void main(String[] args) {
		try {
			Index idx= new Index(1228509028470L,1);
		} catch (Exception e) {
			e.printStackTrace();
			}
		}
	}
