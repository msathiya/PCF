package com.group7.hms.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.group7.hms.Users.*;

public interface UserDAO {



	public User getUserName(String email);

	//public void setUser(String email, String password, String fName,
			//String lName, String phoneNumber, String street, String state,
		//	String zip, String answer1, String answer2, String answer3);

	public void updateUser(String email, String password, String fName,
			String lName, String phoneNumber, String street, String state,
			String zip);
	
	//public Map getUserPreferences(String email);

	//public void blockUser(String emailAddress);

	public void resetPassword(String emailAddress, String password);

	public void setUser(User user)throws SQLException;

	public List<Providers> getDoctorInfo(String dept);
	
	public Providers getDoctorDetails(String email);
	
	public User getUser(String email);


}
