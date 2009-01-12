package org.lindenb.knime.plugins.berkeley;
import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class Berkeley
	{
	private long ID_GENERATOR = System.currentTimeMillis();
	//public static final String DB_STORED_TABLE_NAME="knime";
	public static final String DEFAULT_BERKELY_FILE=".knime-berkeley-db";
	private static File dbHome=new File(System.getProperty("user.home","."),DEFAULT_BERKELY_FILE);
	private static Berkeley INSTANCE=null;
	private Environment env=null;
	private static int INSTANCE_COUNT=0;
	
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
	
	
	public static Environment pushInstance() throws DatabaseException
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
					
					INSTANCE = new Berkeley();
					INSTANCE.env=new Environment(dbHome,cfg);
					}
				}
			}
		++INSTANCE_COUNT;
		return INSTANCE.env;
		}
	
	public static void popInstance() throws DatabaseException
		{
		synchronized (Berkeley.class)
			{
			--INSTANCE_COUNT;
			if(INSTANCE_COUNT==0 && INSTANCE!=null)
				{
				INSTANCE.close();
				INSTANCE=null;
				}
			}
		}
	

	public String createTmpName()
		{
		return "tmpIdx"+(++ID_GENERATOR);
		}
	
	}
