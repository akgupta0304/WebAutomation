/**
 * 
 */
package snapdeal.mobileAutomation.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * @author Ankit Gupta
 *
 */
public class DataBaseUtils {
	
	private static DataBaseUtils INSTANCE = new DataBaseUtils();
	private static boolean writeToDB = true;
	private Connection conn;
	static final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
	 static final String DB_URL="jdbc:mysql://10.41.111.17:3306/MOBILE_APP_AUTOMATION";
 //     Database credentials
//	    static final String USER = "root";
//	    static final String PASS = "12345";
	    static final String USER = "newuser";
	    static final String PASS = "password";
	    private DataBaseUtils(){
				try {
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection(DB_URL, USER, PASS);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	    
//	    public boolean getWriteToDB(){
//	    	return writeToDB;
//	    }
		
		public static DataBaseUtils getInstance(){
			return INSTANCE;
		}
		
		public Connection getConnection() throws SQLException{
			if(!conn.isValid(2))
					conn = DriverManager.getConnection(DB_URL, USER, PASS);
			return conn;
		}

		public ResultSet executeSQL(String sql) throws SQLException{
			Statement stmt;
			stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs;
		}
		
		public int executeUpdate(String sql) throws SQLException{
			Statement stmt;
//			if(writeToDB){
				stmt = getConnection().createStatement();
				int status = stmt.executeUpdate(sql);
				return status;
//			}
//			return 0;
		}
}
