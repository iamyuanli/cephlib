package org.lindenb.knime.plugins.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.lindenb.knime.plugins.MyNodeModel;

public abstract class MySQLNodeModel extends MyNodeModel
	{
	public static final String KEY_JDBC_DRIVER="jdbc.driver";
	public static final String KEY_JDBC_URI="jdbc.uri";
	public static final String KEY_JDBC_LOGIN="jdbc.login";
	public static final String KEY_JDBC_PASSWORD="jdbc.password";
	
	protected final SettingsModelString m_driver;
	protected final SettingsModelString m_uri;
	protected final SettingsModelString m_login;
	protected final SettingsModelString m_password;
	
	
	public MySQLNodeModel(int nrInDataPorts, int nrOutDataPorts,
			String driver,
			String jdbcuri,
			String login,
			String password
			)
		{
		super(nrInDataPorts, nrOutDataPorts);
		addSettings(m_driver= new SettingsModelString(KEY_JDBC_DRIVER,driver));
		addSettings(m_uri= new SettingsModelString(KEY_JDBC_URI,jdbcuri));
		addSettings(m_login= new SettingsModelString(KEY_JDBC_LOGIN,login));
		addSettings(m_password= new SettingsModelString(KEY_JDBC_PASSWORD,password));
		}
	
	protected Connection getConnection()
		throws SQLException,ClassNotFoundException
		{
		Class.forName(m_driver.getStringValue());
		return DriverManager.getConnection(m_uri.getStringValue(),m_login.getStringValue(),m_password.getStringValue());		
		}
	
	
	}
