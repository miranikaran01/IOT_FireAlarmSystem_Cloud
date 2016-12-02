package myPackage;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/Receiver")
public class Receiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String inString = "";
		try {  
			   int len = request.getContentLength();
			   byte[] input = new byte[len];
			   ServletInputStream sin = request.getInputStream();
			   int c, count = 0 ;
			   while ((c = sin.read(input, count, input.length-count)) != -1) {
					count += c;
				}
				sin.close();
				inString = new String(input);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		request.setAttribute("data",inString);
		RequestDispatcher rd = request.getRequestDispatcher("/UpdateDb");
		rd.forward(request,response);
	}
}
