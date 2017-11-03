package com.group7.hms.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.group7.hms.DatabaseConfig;
import com.group7.hms.ApplicationConstants;
import com.group7.hms.Users.*;
import com.group7.hms.Users.User.MailingAddress;
import com.group7.hms.appointment.Appointment;


@Service
public class UserDAOImpl implements UserDAO {

	private DataSource dataSource;	
	private JdbcTemplate jdbcTemplateObject;
	/*public ApplicationConstants constants=new ApplicationConstants();*/
	//private databaseconfig databaseconfig=new databaseconfig();
	
	String confFile = "Spring-Datasource.xml";
    ConfigurableApplicationContext context 
                            = new ClassPathXmlApplicationContext(confFile);
    DatabaseConfig databaseconfig = (DatabaseConfig) context.getBean("dbConfig");
    
    
	/*@Autowired
	public void setDataSource(DataSource dataSource) {
		System.out.println("Datasource set");
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}*/

@Override
	public void updateUser(String emailaddress,String password, String fName, String lName,
			String phoneNumber, String street, String state,String zip)
	{
	String sql = "UPDATE USERS SET password =" + password +" , fName = " + fName +", lName = "
	+ lName +" , phoneNumber = "+ phoneNumber +", street = "+street +", state = "+state +
	", zip = " +zip +" WHERE email = "+ emailaddress;
	
	Connection conn = null;

	try {
		conn = dataSource.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);		
		ps.executeUpdate();
		ps.close();

	} catch (SQLException e) {
		throw new RuntimeException(e);

	} finally {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
	}


	//new user
	@Override
	public void setUser(User user) throws SQLException {
		
		 String deletetablesql="DROP TABLE IF EXISTS user";
		/*String createDB="CREATE DATABASE hospitalmanagement";*/
		String createTableSQL = "CREATE table if not exists user(username varchar(255),emailid varchar(255),password varchar(255),role varchar(255),name varchar(255),status varchar(255),PhoneNumber varchar(255) ,  secondaryPhone varchar(255),  secondaryEmail varchar(255),  mailingAddress varchar(255),  ecFirstName varchar(255),  ecLastName varchar(255),  ecEmailAddress varchar(255),  ecPhoneNumber varchar(255),  Age varchar(255),  DateOfBirth varchar(255),  affiliation varchar(255),  availableDays varchar(255),  availableHours varchar(255),  certification varchar(255),  Degree varchar(255),  department varchar(255),  experience varchar(255),  specialization varchar(255), insuranceEndDate varchar(255) , insuranceStartDate varchar(255) , insuranceProvider varchar(255) , insuranceID varchar(255) , medicalHistory varchar(255) , prescriptions varchar(255),middleName varchar(255),lastName varchar(255),sex varchar(255),maritalStatus varchar(255), mobilePhoneNumber varchar(255))";

    	String sql = "INSERT INTO user "
				+ "(username, emailid, password, role,name,status) VALUES (?, ?, ?, ?,?,?)";
		/*String sql = "INSERT INTO users "
				+ "(username, emailid, password, role,name,status) VALUES (?, ?, ?, ?,?,?)";*/
		Connection conn = null;
		Statement stmt = null;

		try {
		     	       
			//conn = dataSource.getConnection();
          	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			stmt=conn.createStatement();
		/*	stmt.executeQuery(createDB);*/
			stmt.executeUpdate(deletetablesql);
			stmt.executeUpdate(createTableSQL);
			PreparedStatement ps = conn.prepareStatement(sql);
			System.out.println("username*****"+user.getUsername());
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPrimaryEmail());
			ps.setString(3, user.getPassword());
			ps.setString(4, user.getJobTitle());
			ps.setString(5, user.getName());
			ps.setString(6, "InActive");

			ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			throw e;

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	@Override
	public User getUserName(String email) {

		String sql = "SELECT * FROM user WHERE emailId = ?";

		Connection conn = null;

		try {
			System.out.println("trying to get connection");
			 //User user = jdbcTemplateObject.queryForObject(sql, 
                  //   email, new StudentMapper());
			//conn = dataSource.getConnection();
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, email);
			//String[] sendInfo = new String[3];
			
			ResultSet rs = ps.executeQuery();
			String role = null;
			if (rs.next()) {
				role = rs.getString("Role");
			
			if(role.equalsIgnoreCase("Doctor")||role.equalsIgnoreCase("Nurse")){
				Providers user = new Providers();
				user.setName(rs.getString("Name"));
				user.setPrimaryEmail(rs.getString("emailId"));
				user.setJobTitle(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				return user;
			}else if (role.equalsIgnoreCase("Admin")){
				Administrator user = new Administrator();
				user.setName(rs.getString("Name"));
				user.setPrimaryEmail(rs.getString("emailId"));
				user.setJobTitle(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				return user;
			}else if (role.equalsIgnoreCase("Patient")){
				Patient user=  new Patient();
				user.setName(rs.getString("Name"));
				user.setPrimaryEmail(rs.getString("emailId"));
				user.setJobTitle(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				user.setInsuranceEndDate(rs.getDate("insuranceEndDate"));
				user.setInsuranceStartDate(rs.getDate("insuranceStartDate"));
				user.setInsuranceProvider(rs.getString("insuranceProvider"));
				user.setInsuranceID(rs.getString("insuranceID"));
				user.setMedicalHistory(rs.getString("medicalHistory"));
				user.setPresciptions(rs.getString("prescriptions"));
				return user;
			}
			}
		
			rs.close();
			ps.close();
			return null;
			 
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	

		// return "Test Name";
	}
	
	@Override
	public List<Providers> getDoctorInfo(String dept) {

		String sql = "SELECT * FROM user WHERE department = ? and Role = ?";
		List<Providers> docList = new ArrayList<Providers>();
		Connection conn = null;

		try {
			System.out.println("trying to get connection");
			 //User user = jdbcTemplateObject.queryForObject(sql, 
                  //   email, new StudentMapper());
			//conn = dataSource.getConnection();
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, dept);
			ps.setString(2,"Doctor");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()){
				Providers doc = new Providers();
				doc.setPrimaryEmail(rs.getString("emailId"));
				doc.setName(rs.getString("name"));
				doc.setAffiliation(rs.getString("affiliation"));
				doc.setDegree(rs.getString("degree"));
				doc.setSpecialization(rs.getString("specialization"));
				//doc.setExperience(rs.getString("experience"));
				doc.setAvailableDays(rs.getString("availableDays"));
				docList.add(doc);
				
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		for(Providers a:docList){
			System.out.println(a);
		}
		return docList;
	}
	@Override
	public Providers getDoctorDetails(String email){
		String sql = "SELECT * FROM user WHERE emailId = ?";

		Connection conn = null;
		Providers doc = new Providers();
		try {
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()){
				
				doc.setName(rs.getString("name"));
				doc.setAvailableDays(rs.getString("availableDays"));
				doc.setPrimaryEmail(email);
		}}catch(SQLException e){
			e.printStackTrace();
		}
		return doc;
	}
	
	@Override
	public void resetPassword(String emailAddress, String password) {
		String sql = "UPDATE USERS SET password = ? WHERE email = ?";
		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, emailAddress);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
		} catch (SQLException e) {
			throw new RuntimeException(e);
	}

}

	@Override
	public User getUser(String email) {
		String sql = "SELECT * FROM user WHERE emailId = ?";
		Connection conn = null;
		System.out.println("getUser**************");
		try {
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()){
				if(rs.getString("Role").equalsIgnoreCase("Doctor")||rs.getString("Role").equalsIgnoreCase("Nurse")){
					System.out.println("if**************");
					Providers user = new Providers(email,rs.getString("Password"),rs.getString("Role"),rs.getString("Name"));
					// general user data
					user.setMiddleName(rs.getString("middleName"));
					user.setLastName(rs.getString("lastName"));
					user.setSex(rs.getString("sex"));
					user.setMaritialStatus(rs.getString("maritalStatus"));
					user.setMobilePhoneNumber(rs.getString("mobilePhoneNumber"));
					user.setPrimaryPhone(rs.getString("PhoneNumber"));
					user.setSecondaryPhone(rs.getString("secondaryPhone"));
					user.setSecondaryEmail(rs.getString("secondaryEmail"));
					user.setUserAddress(rs.getString("mailingAddress"));
					user.setEcFirstName(rs.getString("ecFirstName"));
					user.setEcLastName(rs.getString("ecLastName"));
					user.setEcEmailAddress(rs.getString("ecEmailAddress"));
					user.setEcPhoneNuber(rs.getString("ecPhoneNumber"));
					user.setStatus(rs.getString("status"));
					user.setAge(rs.getInt("Age"));
					user.setDateOfBirth(rs.getDate("DateOfBirth"));
					
					
					// provider specific information
					user.setAffiliation(rs.getString("affiliation"));
					user.setAvailableDays(rs.getString("availableDays"));
					user.setAvailableHours(rs.getString("availableHours"));
					user.setCertification(rs.getString("certification"));
					user.setDegree(rs.getString("Degree"));
					user.setDepartment(rs.getString("department"));
					user.setExperience(rs.getInt("experience"));
					user.setSpecialization(rs.getString("specialization"));
					
					return user;
				}
				else if (rs.getString("Role").equalsIgnoreCase("Patient")){
					System.out.println("else patient**************");
					Patient user = new Patient(email,rs.getString("Password"),rs.getString("Role"),rs.getString("Name"));
					
					// general user data
					user.setMiddleName(rs.getString("middleName"));
					user.setLastName(rs.getString("lastName"));
					user.setSex(rs.getString("sex"));
					user.setMaritialStatus(rs.getString("maritalStatus"));
					user.setMobilePhoneNumber(rs.getString("mobilePhoneNumber"));
					user.setPrimaryPhone(rs.getString("PhoneNumber"));
					user.setSecondaryPhone(rs.getString("secondaryPhone"));
					user.setSecondaryEmail(rs.getString("secondaryEmail"));
					user.setUserAddress(rs.getString("mailingAddress"));
					user.setEcFirstName(rs.getString("ecFirstName"));
					user.setEcLastName(rs.getString("ecLastName"));
					user.setEcEmailAddress(rs.getString("ecEmailAddress"));
					user.setEcPhoneNuber(rs.getString("ecPhoneNumber"));
					user.setStatus(rs.getString("status"));
					user.setAge(rs.getInt("Age"));
					user.setDateOfBirth(rs.getDate("DateOfBirth"));
					
					//patient Specific infromation
					user.setInsuranceEndDate(rs.getDate("insuranceEndDate"));
					user.setInsuranceStartDate(rs.getDate("insuranceStartDate"));
					user.setInsuranceProvider(rs.getString("insuranceProvider"));
					user.setInsuranceID(rs.getString("insuranceID"));
					user.setMedicalHistory(rs.getString("medicalHistory"));
					user.setPresciptions(rs.getString("prescriptions"));
					
					return user;
				}
				else if (rs.getString("Role").equalsIgnoreCase("Admin")){
					System.out.println("else admin**************");
					Administrator user = new Administrator(email,rs.getString("Password"),rs.getString("Role"),rs.getString("Name"));
					
					// general user data
					user.setMiddleName(rs.getString("middleName"));
					user.setLastName(rs.getString("lastName"));
					user.setSex(rs.getString("sex"));
					user.setMaritialStatus(rs.getString("maritalStatus"));
					user.setMobilePhoneNumber(rs.getString("mobilePhoneNumber"));
					user.setPrimaryPhone(rs.getString("PhoneNumber"));
					user.setSecondaryPhone(rs.getString("secondaryPhone"));
					user.setSecondaryEmail(rs.getString("secondaryEmail"));
					user.setUserAddress(rs.getString("mailingAddress"));
					user.setEcFirstName(rs.getString("ecFirstName"));
					user.setEcLastName(rs.getString("ecLastName"));
					user.setEcEmailAddress(rs.getString("ecEmailAddress"));
					user.setEcPhoneNuber(rs.getString("ecPhoneNumber"));
					user.setStatus(rs.getString("status"));
					user.setAge(rs.getInt("Age"));
					user.setDateOfBirth(rs.getDate("DateOfBirth"));
					
					// Administrative specific data
					user.setCertifications(rs.getString("certification"));
					user.setDegree(rs.getString("Degree"));
					user.setDepartment(rs.getString("department"));
					user.setSpecializations(rs.getString("specialization"));
					
					return user;
				}
				
				
				
				
		}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	public void createAppointmemt(Appointment app){

		String sql = "INSERT INTO Appointment "
				+ "(startTime, endTime, appDate, attendingDoc,attendingNurse,patient) VALUES (?, ?, ?, ?,?,?)";
		String sqlNurse = "Select * from user" 
				+"where role =?";
		Connection conn = null;

		try {
			//conn = dataSource.getConnection();
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			
			PreparedStatement ps = conn.prepareStatement(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		}

	}public void updatePatient(Patient patient) throws SQLException{
		updateUser(patient);
		String sql = "update user "+
					 "set insuranceEndDate = ? ,"+
					 "insuranceID = ? , "+
					 "insuranceProvider = ? , "+
					 "insuranceStartDate = ? , "+
					 "medicalHistory = ? , " +
					 "status = ?  " +
					 "where EMailID = ? ";
		Connection conn = null;
		try{
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setDate(1, patient.getInsuranceEndDate());
			ps.setString(2, patient.getInsuranceID());
			ps.setString(3, patient.getInsuranceProvider());
			ps.setDate(4, patient.getInsuranceStartDate());
			ps.setString(5, patient.getMedicalHistory());
			ps.setString(6, "Active");
			ps.setString(7, patient.getUsername());
			System.out.println(ps.toString());
			ps.executeUpdate();
			ps.close();
		}catch (SQLException e) {
			throw e;

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
			
		
	}
	public void updateAdmin(Administrator admin) throws SQLException{
		updateUser(admin);
		String sql = "update user "+
					 "set certification = ? , "+
					 "Degree = ? ," +
					 "department = ? ,"+
					 "specialization = ? , "+
					 "status = ?  " +
					 "where EmailID = ? ";
		Connection conn = null;
		try{
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, admin.getCertifications());
			ps.setString(2, admin.getDegree());
			ps.setString(3, admin.getDepartment());
			ps.setString(4, admin.getSpecializations());
			ps.setString(5, "Active");
			ps.setString(6, admin.getUsername());
			System.out.println(ps.toString());
			ps.executeUpdate();
			ps.close();
		}catch (SQLException e) {
			throw e;

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
					 

	public void updateProvider(Providers provider) throws SQLException{
		updateUser(provider);
		String sql = "update user "+
					 "set affiliation = ? , "+
					 "Degree = ? , "+
					 "availableDays = ? , " +
					 "availableHours = ? , "+ 
					 "certification = ? , "+
					 "department = ? , "+
					 "experience = ? ,"+
					 "specialization = ? , "+
					 "status = ?  " +
					 "where EmailID = ? ";
		Connection conn = null;
		try{
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, provider.getAffiliation());
			ps.setString(2, provider.getDegree());
			ps.setString(3, provider.getAvailableDays());
			ps.setString(4, provider.getAvailableHours() );
			ps.setString(5, provider.getCertification());
			ps.setString(6, provider.getDepartment());
			ps.setInt(7, provider.getExperience());
			ps.setString(8, provider.getSpecialization());
			ps.setString(9, "Active");
			ps.setString(10, provider.getUsername());
			System.out.println(ps.toString());
			ps.executeUpdate();
			ps.close();
		}catch (SQLException e) {
			throw e;

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	public void updateUser(User user) throws SQLException{
		String sql = "update user "+
					"set DateOfBirth= ? , "+
					"MailingAddress= ? , "+
					"ecPhoneNumber = ? , "+
					"MaritalStatus= ? , "+
					"PhoneNumber= ? , "+
					"sex= ? , "+
					"secondaryPhone= ? , "+
					"secondaryEmail= ? , "+
					"mobilePhoneNumber= ? , "+
					"middleName= ? , "+
					"lastName= ? , "+
					"ecEmailAddress= ? , "+
					"ecFirstName= ? , "+
					"ecLastName= ? "+
					"where EmailID = ? ";
		
		Connection conn = null;

		try {
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setDate(1, user.getDateOfBirth());
			ps.setString(2, user.getUserAddress().toString());
			ps.setString(3, user.getEcPhoneNuber());
			ps.setString(4, user.getMaritialStatus());
			ps.setString(5, user.getPrimaryPhone());
			ps.setString(6,user.getSex());
			ps.setString(7, user.getSecondaryPhone());
			ps.setString(8, user.getSecondaryEmail());
			ps.setString(9, user.getMobilePhoneNumber());
			ps.setString(10,user.getMiddleName());
			ps.setString(11, user.getLastName());
			ps.setString(12, user.getEcEmailAddress());
			ps.setString(13, user.getEcFirstName());
			ps.setString(14, user.getEcLastName());
			ps.setString(15,user.getUsername());
			System.out.println(ps.toString());
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			throw e;

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	public int getNumberAppointments(User user) {
		
			// TODO get the Appointments from the database default is 5
			Calendar cal = Calendar.getInstance();
			java.sql.Date today = new java.sql.Date(cal.getTimeInMillis());
			cal.set(Calendar.DAY_OF_MONTH, 1);
			java.sql.Date beginMonth = new java.sql.Date(cal.getTimeInMillis());
			//String providerStr;
			// providerStr="Doc";
			 String sql = null;
			if (user.getJobTitle().equalsIgnoreCase("doctor"))
				sql = "Select COUNT(*) from Appointment "
					+ "where (appDate between ? and ?) and attendingDoc = ? ";
			else 
				sql =  "Select COUNT(*) from appointment "
						+ "where (appDate between ? and ?) and attendingNurse = ? ";
			
			Connection conn = null;
			ResultSet rs = null;
			int numApp=0;
			try {
				 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
				PreparedStatement ps = conn.prepareStatement(sql);
				
				ps.setDate(1, beginMonth);
				ps.setDate(2, today);
				//ps.setString(3, providerStr);
				ps.setString(3, user.getPrimaryEmail());
				
				System.out.println(ps.toString());
				rs = ps.executeQuery();
				if(rs.next())
					numApp = rs.getInt(1);
				ps.close();
				
				
			} catch (SQLException e) {
				e.printStackTrace();

			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}
			}
			return numApp;
		
	}
	public Providers getNurse(){
		String sql = "SELECT * FROM user WHERE Role = ?";
		Connection conn = null;
		
		try {
			 	conn = DriverManager.getConnection(databaseconfig.getDburl(),databaseconfig.getDbusername(),databaseconfig.getDbPassword());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "Nurse");
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()){
					Providers user = new Providers(rs.getString("EmailId"),rs.getString("Password"),rs.getString("Role"),rs.getString("Name"));
					// general user data
					user.setMiddleName(rs.getString("middleName"));
					user.setLastName(rs.getString("lastName"));
					user.setSex(rs.getString("sex"));
					user.setMaritialStatus(rs.getString("maritalStatus"));
					user.setMobilePhoneNumber(rs.getString("mobilePhoneNumber"));
					user.setPrimaryPhone(rs.getString("PhoneNumber"));
					user.setSecondaryPhone(rs.getString("secondaryPhone"));
					user.setSecondaryEmail(rs.getString("secondaryEmail"));
					user.setUserAddress(rs.getString("mailingAddress"));
					user.setEcFirstName(rs.getString("ecFirstName"));
					user.setEcLastName(rs.getString("ecLastName"));
					user.setEcEmailAddress(rs.getString("ecEmailAddress"));
					user.setEcPhoneNuber(rs.getString("ecPhoneNumber"));
					user.setStatus(rs.getString("status"));
					user.setAge(rs.getInt("Age"));
					user.setDateOfBirth(rs.getDate("DateOfBirth"));
					
					
					// provider specific information
					user.setAffiliation(rs.getString("affiliation"));
					user.setAvailableDays(rs.getString("availableDays"));
					user.setAvailableHours(rs.getString("availableHours"));
					user.setCertification(rs.getString("certification"));
					user.setDegree(rs.getString("Degree"));
					user.setDepartment(rs.getString("department"));
					user.setExperience(rs.getInt("experience"));
					user.setSpecialization(rs.getString("specialization"));
					
					return user;
				}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
		
	}

	
}
