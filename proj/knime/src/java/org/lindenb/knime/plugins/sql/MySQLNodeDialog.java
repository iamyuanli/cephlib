package org.lindenb.knime.plugins.sql;

import org.knime.core.node.defaultnodesettings.DialogComponentPasswordField;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.lindenb.knime.plugins.MyNodeDialog;

public class MySQLNodeDialog extends MyNodeDialog
{
protected MySQLNodeDialog(
		String jdbcuri,
		String login,
		String password
		)
		{
		this("com.mysql.jdbc.Driver",jdbcuri,login,password);
		}
	
	
protected MySQLNodeDialog(
		String driver,
		String jdbcuri,
		String login,
		String password
		)
	{
	this.createNewGroup("JDBC parameters");
	addDialogComponent(new DialogComponentString(
		new SettingsModelString(MySQLNodeModel.KEY_JDBC_DRIVER,driver),
		"JDBC Driver Class",
		true,
		20
		));
	
	addDialogComponent(new DialogComponentString(
			new SettingsModelString(MySQLNodeModel.KEY_JDBC_URI,jdbcuri),
			"JDBC URI",
			true,
			50
			));
	
	addDialogComponent(new DialogComponentString(
			new SettingsModelString(MySQLNodeModel.KEY_JDBC_LOGIN,login),
			"Login",
			true,
			20
			));
	addDialogComponent(new DialogComponentPasswordField(
			new SettingsModelString(MySQLNodeModel.KEY_JDBC_PASSWORD,login),
			"Password",
			20
			));
	this.closeCurrentGroup();
	}
}
