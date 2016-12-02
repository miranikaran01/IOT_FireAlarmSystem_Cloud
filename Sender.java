package myPackage;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/Sender")
public class Sender extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String state = (String)request.getParameter("state");
	    if(state != null){
	    	System.out.println(state);
	    	Socket socket = new Socket(InetAddress.getByName("localhost"),9595);
			PrintWriter out = new PrintWriter (socket.getOutputStream(), true);
			out.println(state);
			out.flush();
			out.close();
			socket.close();
	    }
	    else{
	    final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
		final String DB_URL="jdbc:mysql://localhost/database";
		final String USER = "root";
		final String PASS = "Lambo*123";	
		String topicData =(String) request.getAttribute("data");
		System.out.println("Data in sender " + topicData);
		String topic = topicData.split("_")[0];
		String data= topicData.split("_")[1];		
		try{
			Class.forName(JDBC_DRIVER);
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			String sql = "";
			sql = "SELECT subscriber from topicsub where topic = '" +topic+"'";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				String subscriberIp = rs.getString("subscriber");	
				Socket socket = new Socket(InetAddress.getByName(subscriberIp),9595);
				System.out.println("Sending "+ data + " to "+subscriberIp);
				PrintWriter out = new PrintWriter (socket.getOutputStream(), true);
				out.println(topicData);
				out.flush();
				out.close();
				socket.close();
			}
		}
	    
		catch(Exception e){
			System.out.print("Error while sending data to edge");
		}		
	    }
	    RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request,response);
	}

}
