package com.group7.hms.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.group7.hms.Users.*;
import com.group7.hms.appointment.Appointment;


@Service
public class AppointmentDAO{

	private DataSource dataSource;	
	private JdbcTemplate jdbcTemplateObject;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		System.out.println("Datasource set");
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public void createAppointment(Appointment app){
		String sql = "INSERT INTO hospitalmanagement.appointments "
				+ "(startTime, endTime, appDate, appDay,attendingDoc, DoctorName, attendingNurse, NurseName, patient, patientName, statusApp, DoctorsNotes, Cost) VALUES (?, ?, ?, ?,?,?,? ,?, ?, ?, ?, ?, ?)";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","Success@274");
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setTime(1, app.getStartTime());
			ps.setTime(2, app.getEndTime());
			ps.setDate(3, app.getAppDate());
			ps.setString(4, app.getDay());
			ps.setString(5, app.getDoctor());
			ps.setString(6, app.getDoctorName());
			ps.setString(7, app.getNurse());
			ps.setString(8, app.getNurseName());
			ps.setString(9,app.getPatient());
			ps.setString(10, app.getPatientName());
			ps.setString(11, "Created");
			ps.setString(12,"");
			ps.setDouble(13, app.getCost());
			ps.executeUpdate();
			ps.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
	}

	public List<Appointment> getAppointments(String emailaddress, String role)
	{
		String sql =  null;
		List<Appointment> appList = new ArrayList<Appointment>();
		if (role.equalsIgnoreCase("Patient"))
			sql = "Select * from HospitalManagement.appointments where Patient = ? and statusApp = ? "
					+ "ORDER BY appDate DESC ";	
		else if (role.equalsIgnoreCase("Doctor"))
			sql = "Select * from HospitalManagement.appointments where attendingDoc = ? and statusApp = ? "
					+ "ORDER BY appDate DESC ";	
		else if (role.equalsIgnoreCase("Nurse"))
			sql = "Select * from HospitalManagement.appointments where attendingNurse = ? and statusApp = ? "
					+ "ORDER BY appDate DESC ";
		else 
			sql = "Select * from HospitalManagement.appointments where statusApp <> ? order by appDate ASC";
	
	Connection conn = null;

	try {
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","Success@274"); 
		PreparedStatement ps = conn.prepareStatement(sql);
		System.out.println(ps);
		if(!(role.equalsIgnoreCase("Admin"))){
			ps.setString(1, emailaddress);
			ps.setString(2, "Created");
		}
		if(role.equalsIgnoreCase("Admin")){
			ps.setString(1, "Paid");
		}
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()){
			Appointment app = new Appointment();
			app.setDay(rs.getString("appDay"));
			app.setStartTime(rs.getTime("startTime"));
			app.setEndTime(rs.getTime("endTime"));
			app.setAppDate(rs.getDate("appDate"));
			app.setDoctor(rs.getString("attendingDoc"));
			app.setDoctorName(rs.getString("DoctorName"));
			app.setNurse(rs.getString("attendingNurse"));
			app.setNurseName(rs.getString("NurseName"));
			app.setPatient(rs.getString("patient"));
			app.setPatientName(rs.getString("patientName"));
			app.setAppId(rs.getInt("idAppointments"));
			appList.add(app);
			
		}

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
	return appList;
	}
	public List<Appointment> getBilledAppointments(String emailAddress)
	{
		String sql =  null;
		System.out.println(emailAddress +"in get billed app");
		List<Appointment> billedList = new ArrayList<Appointment>();
			sql = "Select * from HospitalManagement.appointments where Patient = ? and statusApp = ? "
					+ "ORDER BY appDate DESC ";	
		Connection conn = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","Success@274"); 
			PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, emailAddress);
				ps.setString(2, "ReleasedBill");		
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()){
				Appointment app = new Appointment();
				app.setDay(rs.getString("appDay"));
				app.setStartTime(rs.getTime("startTime"));
				app.setEndTime(rs.getTime("endTime"));
				app.setAppDate(rs.getDate("appDate"));
				app.setDoctor(rs.getString("attendingDoc"));
				app.setDoctorName(rs.getString("DoctorName"));
				app.setNurse(rs.getString("attendingNurse"));
				app.setNurseName(rs.getString("NurseName"));
				app.setPatient(rs.getString("patient"));
				app.setPatientName(rs.getString("patientName"));
				app.setAppId(rs.getInt("idAppointments"));
				app.setDocNotes(rs.getString("DoctorsNotes"));
				app.setCost(rs.getInt("Cost"));
				billedList.add(app);
				
			}

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
		return billedList;
	}
	public void releasePatient(String patientEmail){
		String sql = "UPDATE hospitalManagement.appointments SET statusApp = ? where patient= ? and statusApp = ? "
				+ " OR statusApp = ?";
		Connection conn = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","Success@274"); 
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "ReleasedByDoc");
			ps.setString(2, patientEmail);
			ps.setString(3, "InCare");
			ps.setString(4, "Created");
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
	}
	}
	public void releaseBill(String patientEmail){
		String sql = "UPDATE hospitalManagement.appointments SET statusApp = ? where patient= ? and statusApp = ?";
		Connection conn = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","Success@274");
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "ReleasedBill");
			ps.setString(2, patientEmail);
			ps.setString(3, "ReleasedByDoc");
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
	}
	}
	public void payBill(String patientEmail){
		String sql = "UPDATE hospitalManagement.appointments SET statusApp = ? where patient= ? and statusApp = ?";
		Connection conn = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","Success@274");
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "Paid");
			ps.setString(2, patientEmail);
			ps.setString(3, "ReleasedBill");
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
	}
	}
		
	public void saveAppointmentRecord(String doctorNotes,int cost, int appId){
		String sql = "UPDATE hospitalManagement.appointments SET statusApp = ? , cost = ?"
				+ " , doctorsNotes = ? where idAppointments = ?";
		Connection conn = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","Success@274");
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "InCare");
			ps.setInt(2, cost);
			ps.setString(3, doctorNotes);
			ps.setInt(4, appId);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
	}
		
	}
	public List<String> getReleasedPatient(){
		String sql = "select distinct patient from hospitalManagement.appointments where statusApp = ?;";
		Connection conn = null;
		List<String> userNames = new ArrayList<String>();
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","Success@274");
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "ReleasedByDoc");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()){
				userNames.add(rs.getString("patient"));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
	}
		//System.out.println(userNames.toString());
		return userNames;
	}
}
