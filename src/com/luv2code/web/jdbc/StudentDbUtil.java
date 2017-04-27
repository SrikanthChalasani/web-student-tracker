package com.luv2code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {
	private DataSource dataSource;

	public StudentDbUtil(DataSource theDataSource) {
		dataSource = theDataSource;
	}

	public List<Student> getStudents() throws Exception {
		List<Student> students = new ArrayList<>();
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		try {
			myConn = dataSource.getConnection();
			String sql = "select * from student order by last_name";
			myStmt = myConn.createStatement();
			myRs = myStmt.executeQuery(sql);

			while (myRs.next()) {
				int id = myRs.getInt("id");
				String lastName = myRs.getString("last_name");
				String firstName = myRs.getString("first_name");
				String email = myRs.getString("email");

				Student tempStudent = new Student(id, firstName, lastName,
						email);
				students.add(tempStudent);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			close(myConn, myStmt, myRs);
		}
		return students;
	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		try {
			if (myRs != null) {
				myRs.close();
			}
			if (myStmt != null) {
				myStmt.close();
			}
			if (myConn != null) {
				myConn.close(); // doesnt really close it... just puts back in
								// connection pool for some one else to use it
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void addStudent(Student student) {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
			// create sql for insert
			myConn = dataSource.getConnection();
			// set the param values for the student
			String sql = "insert into student "
					+ "(first_name, last_name, email) " + "values (?,?,?)";
			myStmt = myConn.prepareCall(sql);

			// set the param values for the student
			myStmt.setString(1, student.getFirstName());
			myStmt.setString(2, student.getLastName());
			myStmt.setString(3, student.getEmail());

			// execute the sql insert

			myStmt.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// clean up jdbc objects
			close(myConn, myStmt, null);
		}

	}

	public Student getStudent(String id) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		Student tempStudent = null;
		int studentId;
		try {
			studentId = Integer.parseInt(id);
			myConn = dataSource.getConnection();
			String sql = "select * from student where id = ?";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setInt(1, studentId);
			myRs = myStmt.executeQuery();

			while (myRs.next()) {
				String lastName = myRs.getString("last_name");
				String firstName = myRs.getString("first_name");
				String email = myRs.getString("email");

				tempStudent = new Student(studentId, firstName, lastName, email);
			}
		} catch (Exception ex) {
			throw new Exception("Could not find student id: " + id);
		} finally {
			close(myConn, myStmt, myRs);
		}
		return tempStudent;
	}

	public void updateStudent(Student theStudent) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
			myConn = dataSource.getConnection();
			String sql = "update student "
					+ "set first_name=?, last_name=?, email=? " + "where id=?";

			myStmt = myConn.prepareStatement(sql);

			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());
			myStmt.execute();
		} finally {
			close(myConn, myStmt, null);
		}

	}

	public void deleteStudent(int id) throws SQLException {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
			myConn = dataSource.getConnection();
			String sql = "delete from student where id=? ";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setInt(1, id);
			myStmt.execute();
		} finally {
			close(myConn, myStmt, null);
		}

	}
}
