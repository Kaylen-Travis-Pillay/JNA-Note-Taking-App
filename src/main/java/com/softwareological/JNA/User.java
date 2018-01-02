package com.softwareological.JNA;

public class User {
	
	private String user_name;
	private String user_surname;
	private String user_email;
	private int user_id;
	
	public User(String name, String surname, String email, int user_id)
	{
		user_name = name;
		user_surname = surname;
		user_email = email;
	}

	public String getName() {
		return user_name;
	}

	public String getSurname() {
		return user_surname;
	}

	public String getEmail() {
		return user_email;
	}

	public int getID() {
		return user_id;
	}
}
