package fr.inserm.u794.lindenb.workbench;

import java.io.File;
import org.lindenb.berkeley.SingleMapDatabase;

import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import fr.inserm.u794.lindenb.workbench.table.Indexes;
import fr.inserm.u794.lindenb.workbench.table.Row;
import fr.inserm.u794.lindenb.workbench.table.TableRef;



/** Singleton */
public class Berkeley
	{
	private long ID_GENERATOR = System.currentTimeMillis();
	public static final String DEFAULT_BERKELY_FILE=".inserm-workbench-db";
	private static File dbHome=new File(System.getProperty("user.home","."),DEFAULT_BERKELY_FILE);
	private static Berkeley INSTANCE=null;
	private Environment env=null;
	private SingleMapDatabase<Long, TableRef> tableRefDB;
	
	private Berkeley()
		{
		
		}

	@Override
	protected void finalize() throws Throwable {
		try {
			close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			}
		super.finalize();
		}
	
	public static void setDatabaseHome(File dbHome)  throws DatabaseException
		{
		synchronized (Berkeley.class)
			{
			if(INSTANCE!=null) throw new DatabaseException("getInstance already called");
			Berkeley.dbHome = dbHome;
			}
		}
	
	public Environment getEnvironment() {
		return env;
		}
	
	public void close() throws DatabaseException
		{
		if(getEnvironment()==null) return;
		getEnvironment().compress();
		getEnvironment().cleanLog();
		getEnvironment().close();
		this.env=null;
		}
	
	
	public static Berkeley getInstance() throws DatabaseException
		{
		if(INSTANCE==null)
			{
			synchronized (Berkeley.class)
				{
				if(INSTANCE==null)
					{
					File f= Berkeley.dbHome;
					if(!f.exists())
						{
						if(!f.mkdir()) throw new DatabaseException("Cannot create BerkeleyDB:"+f);
						}
					EnvironmentConfig cfg= new EnvironmentConfig();
					cfg.setAllowCreate(true);
					cfg.setReadOnly(false);
					cfg.setTransactional(true);
					
					Berkeley berkeley= new Berkeley();
					berkeley.env=new Environment(dbHome,cfg);
					
					DatabaseConfig dbcfg= new DatabaseConfig();
					dbcfg.setAllowCreate(true);
					dbcfg.setExclusiveCreate(false);
					dbcfg.setReadOnly(false);
					dbcfg.setTransactional(true);
					berkeley.tableRefDB= new SingleMapDatabase<Long, TableRef>(
						berkeley.env.openDatabase(null, "tableref", dbcfg), 
						new LongBinding(),
						TableRef.BINDING
						);
					
					
					INSTANCE=berkeley;
					}
				}
			}
		return INSTANCE;
		}
	
	public SingleMapDatabase<Long, TableRef> getTableRefDB() {
		return tableRefDB;
		}
	
	public SingleMapDatabase<Integer, Row> createTable()
		throws DatabaseException
		{
		DatabaseConfig dbcfg= new DatabaseConfig();
		dbcfg.setAllowCreate(true);
		dbcfg.setExclusiveCreate(false);
		dbcfg.setReadOnly(false);
		dbcfg.setTemporary(true);
		Database db= getEnvironment().openDatabase(null,
				"tmp"+(++ID_GENERATOR),
				dbcfg
				);
		
		return new SingleMapDatabase<Integer, Row>(db,
				new IntegerBinding(),
				Row.BINDING
				);
		}

	
	public SingleMapDatabase<Row, Indexes> createIndex()
	throws DatabaseException
		{
		DatabaseConfig dbcfg= new DatabaseConfig();
		dbcfg.setAllowCreate(true);
		dbcfg.setBtreeComparator(Row.COMPARATOR.class);
		dbcfg.setExclusiveCreate(false);
		dbcfg.setReadOnly(false);
		dbcfg.setTemporary(true);
		Database db= getEnvironment().openDatabase(null,
				"tmpIdx"+(++ID_GENERATOR),
				dbcfg
				);
		
		return new SingleMapDatabase<Row, Indexes>(db,
				Row.BINDING,
				Indexes.BINDING
				);
		}
	
	}
