/**
 * 
 */
package snapdeal.mobileAutomation.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Ankit Gupta
 *
 */
public class INsetDataIntoSql {
	
	private static INsetDataIntoSql INSTANCE = new INsetDataIntoSql();
	private static boolean writeToDB = true;
	private Connection conn;
	static final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
	 static final String DB_URL="jdbc:mysql://localhost:3306/anothertestdata";

	    //  Database credentials
//	    static final String USER = "root";
//	    static final String PASS = "12345";
	    static final String USER = "root";
	    static final String PASS = "root";
	    private INsetDataIntoSql(){
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
		
		public static INsetDataIntoSql getInstance(){
			return INSTANCE;
		}
		
		
		public Connection getConnection() throws SQLException{
			if(!conn.isValid(2))
					conn = DriverManager.getConnection(DB_URL, USER, PASS);
			return conn;
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
		
		
		public void insertIntoMysql(){

			
		}
     public static void main(String[] args){
    	 String testCaseName ="", userName="", password="",searchData="",searchData2="",searchData3="";
//			testCaseName = RandomStringUtils.randomAlphabetic(10);
//			userName = RandomStringUtils.randomAlphabetic(8);
//			password = RandomStringUtils.randomAlphabetic(6);
//			searchData = RandomStringUtils.randomAlphabetic(5);
//			searchData1 = RandomStringUtils.randomAlphabetic(5);
//			searchData3 = RandomStringUtils.randomAlphabetic(5);
			testCaseName = "SDListingPage";
			userName = "AnkitGupta3";
			password = "ankitKumar3";
			searchData = "Tshirt3";
			searchData2 = "Mobile3";
			searchData3 = "Laptops3";
			for (int i =1; i<6; i++){
				testCaseName = "LoginPage"+i;
				userName = "AnkitGupta"+i;
				password = "ankitKumar"+i;
				searchData = "Tshirt"+i;
				searchData2 = "Mobile"+i;
				searchData3 = "Laptops"+i;
//				testCaseName = RandomStringUtils.randomAlphabetic(10);
//				userName = RandomStringUtils.randomAlphabetic(8);
//				password = RandomStringUtils.randomAlphabetic(6);
//				searchData = RandomStringUtils.randomAlphabetic(5);
//				searchData1 = RandomStringUtils.randomAlphabetic(5);
//				searchData3 = RandomStringUtils.randomAlphabetic(5);
			String q1 = "insert into Another_table (TestCaseName,userName,password,searchData,searchData2,searchData3) values "
					+	"('"
					+	testCaseName + "','"
					+	userName + "','"
					+	password + "','"
					+	searchData + "','"
					+	searchData2 + "','"
					+	searchData3
					+ "')";
			try {
				INsetDataIntoSql.getInstance().executeUpdate(q1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			}
    	 
     }		

}
