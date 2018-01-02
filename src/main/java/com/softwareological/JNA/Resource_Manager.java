package com.softwareological.JNA;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Resource_Manager {
	
	private Connection database_connection;
	private String database_url;
	private String database_username;
	private String database_password;
	
	public Resource_Manager(String url, String username, String password)
	{
		database_url = url;
		database_username = username;
		database_password = password;
		
		try 
		{
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) 
		{
        	App_Control.displayException(e);
			System.exit(1);
        }
		
		try
		{
			database_connection = DriverManager.getConnection(database_url, database_username, database_password);
			
		}
		catch(java.sql.SQLException e)
		{
			App_Control.displayException(e);
			System.exit(1);
		}
		
	}
	
	public boolean cleanResources()
	{
		try {
			database_connection.close();
			return true;
		} catch (SQLException e) {
			App_Control.displayException(e);
			return false;
		}
	}
	
	public Object[] executeQuery(String sql_query) throws SQLException
	{
		try
		{
			Statement statement = database_connection.createStatement();
			ResultSet result_set = statement.executeQuery(sql_query);
			
			return new Object[]{result_set, statement};
		}
		catch(SQLException e)
		{
			throw new SQLException();
		}
	}
	
	public void execute(String sql_query) throws SQLException
	{
		try
		{
			Statement statement = database_connection.createStatement();
			statement.execute(sql_query);
			
			statement.close();
		}
		catch(SQLException e)
		{
			throw new SQLException();
		}
	}

}
