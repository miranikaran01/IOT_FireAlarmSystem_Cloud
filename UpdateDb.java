package myPackage;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

@WebServlet("/UpdateDb")
public class UpdateDb extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
    final String DB_URL="jdbc:mysql://localhost/database";
    final String USER = "root";
    final String PASS = "Lambo*123";	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  String ipAddress = request.getHeader("X-FORWARDED-FOR");
		  if (ipAddress == null) {
			   ipAddress = request.getRemoteAddr();
		  }
		  String topicData = "";
		  topicData= (String)request.getAttribute("data");
		  if(topicData.charAt(0) == '#'){
			  register(ipAddress,topicData.substring(1,topicData.length()));
		  }		  
		  else{
			  String topic = topicData.split("_")[0];
			  String data= topicData.split("_")[1];
			  update(topic,data);
			  RequestDispatcher rd = request.getRequestDispatcher("/Sender");
			  rd.forward(request,response);
		  }	
}
	private void update(String topic, String data){
		try{
		   Class.forName(JDBC_DRIVER);
		   Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		   Statement stmt = conn.createStatement();
		   String sql = "";
		   sql = "INSERT INTO topicdata (topic,data,ts) values ('" + topic + "','"+data + "',now())" ;
		   stmt.executeUpdate(sql);
		   stmt.close();
		   conn.close();
		}
		catch(Exception e){
			System.out.println("Error while updating");			
		}
	}
	private void register(String ipAddress, String data){
		try{
		   String topic = data.substring(1,data.length());	 
		   char type = data.charAt(0);	
		   Class.forName(JDBC_DRIVER);
		   Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		   Statement stmt = conn.createStatement();
		   ResultSet rs;
		   String sql;
		   if(type == 'P'){
			  sql = "SELECT * from topicpub where topic = '" + topic + "' and publisher = '" + ipAddress + "' limit 1";
	          rs = stmt.executeQuery(sql);
	          if(!rs.next()){
	        	  sql = "INSERT into topicpub(topic,publisher)"; 
	        	  sql += " values('"+ topic + " ', '" + ipAddress + "')";
	        	  stmt.executeUpdate(sql);
	          }
		   }
	       else{
	    	   sql = "SELECT * from topicsub where topic = '" + topic + "' and subscriber = '" + ipAddress + "' limit 1";
		       rs = stmt.executeQuery(sql);
		       if(!rs.next()){
		    	   sql = "INSERT into topicsub (topic,subscriber)";
		    	   sql += " values('"+ topic + " ', '" + ipAddress + "')";
		    	   stmt.executeUpdate(sql);
		       }
	       }	   
		   rs.close();
		   stmt.close();
		   conn.close();		   
		}
		catch(Exception e){
			System.out.println("Error while registering");
		}
	}

	
	

}
