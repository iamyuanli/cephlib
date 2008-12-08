package fr.inserm.u794.lindenb.workbench.sql;

import fr.cephb.lindenb.sql.MySQLConstants;

public interface SQLSource
{
public final static SQLSource UCSC_HG18=new SQLSource()
	{
	@Override
	public String getDriver() {
		return MySQLConstants.DRIVER;
		}
	@Override
	public String getJdbcUri() {
		return MySQLConstants.URI+"://genome-mysql.cse.ucsc.edu/hg18";
		}
	@Override
	public String getLabel() {
			return "UCSC hg18";
			}
	@Override
	public String getlogin() {
			return "genome";
			}
	@Override
	public String getPassword() {
			return "";
			}
	@Override
	public String toString() {
			return getLabel();
			}
	};
	
public final static SQLSource LOCALHOST_HG18=new SQLSource()
	{
	@Override
	public String getDriver() {
		return MySQLConstants.DRIVER;
		}
	@Override
	public String getJdbcUri() {
		return MySQLConstants.URI+"://localhost/hg18";
		}
	@Override
	public String getLabel() {
			return "UCSC hg18 (localhost)";
			}
	@Override
	public String getlogin() {
			return "anonymous";
			}
	@Override
	public String getPassword() {
			return "";
			}
	public String toString() {
		return getLabel();
		}
	};
	
public String getDriver();
public String getJdbcUri();
public String getlogin();
public String getPassword();
public String getLabel();
}
