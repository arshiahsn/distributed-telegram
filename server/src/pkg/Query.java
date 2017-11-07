package pkg;
import java.sql.*;

public class Query {

	  public static String query_user(String userId)
	  {
		  String userIp;
		  Boolean hasResult = false;
		  userIp = new String();

		  try
	    {
	      // create our mysql database connection
	      String myDriver = "com.mysql.jdbc.Driver";
	      String myUrl = "jdbc:mysql://localhost/sip?autoReconnect=true&useSSL=false";
	      Class.forName(myDriver);

	      Connection conn = DriverManager.getConnection(myUrl, "root", "@dminCronos2008");
	      
	      // our SQL SELECT query. 
	      // if you only need a few columns, specify them by name instead of using "*"
	      String query = "SELECT ip FROM users WHERE user = '"+userId+"'";

	      // create the java statement
	      Statement st = conn.createStatement();
	      
	      // execute the query, and get a java resultset
	      ResultSet rs = st.executeQuery(query);
	      
	      // iterate through the java resultset

	      hasResult = rs.next();	 
		  userIp = rs.getString("ip");
	      st.close();

	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception! ");
	      System.err.println(e.getMessage());
	    }
		  if(hasResult)
			  return userIp;
		  else
			  return null;
	  }
	  
	  public static String query_parent()
	  {
	    String parentIp;
		Boolean hasResult = false;
	    parentIp = new String();
		  try
	    {
	      // create our mysql database connection
	      String myDriver = "com.mysql.jdbc.Driver";
	      String myUrl = "jdbc:mysql://localhost/sip?autoReconnect=true&useSSL=false";
	      Class.forName(myDriver);
	      Connection conn = DriverManager.getConnection(myUrl, "root", "@dminCronos2008");
	      
	      // our SQL SELECT query. 
	      // if you only need a few columns, specify them by name instead of using "*"
	      String query = "SELECT IP FROM parents WHERE flag = true";

	      // create the java statement
	      Statement st = conn.createStatement();
	      
	      // execute the query, and get a java resultset
	      ResultSet rs = st.executeQuery(query);
	      
	      // iterate through the java resultset
	      hasResult = rs.next();
	      parentIp = rs.getString("IP");
	      st.close();

	      
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception! ");
	      System.err.println(e.getMessage());
	    }
	      if(hasResult)
	    	  return parentIp;
	      else
	    	  return null;
	    
	  }
	
	
}