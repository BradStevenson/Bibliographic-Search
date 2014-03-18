
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DecimalFormat;
public class PageRank {
	private static double[] pageRanks;
	private static int[] outboundCitations;
	public static int maxPapers;
	static Statement statement;
	 private static Connection conn;
	 private static Statement st;
	 
	 
	public static void main(String[] args) {
		startConnection();
		maxPapers = getNumberOfPapers();
		pageRanks = new double[maxPapers];
		outboundCitations = new int[maxPapers];
		//initialise the whole array to 1
		Arrays.fill(pageRanks, 1.0);
		// Fill outboundCitations
		for (int i = 0; i < outboundCitations.length; i++) {
			outboundCitations[i] = getOutboundCitation(i);
		}

		// Need to call ~20 times.
		pageRank();
		
		endConnection();
	}
	
	 private static void startConnection() {
	        String url = "jdbc:mysql://sproj09.cs.nott.ac.uk:3306";
	        String dbName = "SciSearcher";
	        String driver = "com.mysql.jdbc.Driver";
	        String userName = "root";
	        String password = "mNNhv13uBB";
	        try {
	            Class.forName(driver).newInstance();
	            conn = DriverManager.getConnection(url+dbName,userName,password);
	            st = conn.createStatement();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	  private static void endConnection() {
	        try {
		    conn.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	 

	private static void pageRank() {
		// TODO decide on damping factor
		double d = 0.80;
		double culmativeRank = 0;
		// for every pagerank
		for (int i = 0; i<pageRanks.length; i++) {
			culmativeRank = 0;
			for (int paper : papersCiting(i)) {
				culmativeRank += pageRanks[paper] / outboundCitations[paper];
			}
			pageRanks[i] = (1 - d) + d*(culmativeRank);
		}
	}
	

	private static int getOutboundCitation(int id) {
		int citationCount = 0;
		
		// MYSQL QUERY FIND HOW MANY CITATIONS
			String sql = "Select numcites from papers where paperID ='"+id+"';";
			try {
				ResultSet results = statement.executeQuery(sql);//execute statement
					results.next();

					return  results.getInt(1);
					
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return citationCount;
	}
	
	
	public static int getNumberOfPapers(){ //not including marked deleted mails
		//String sql = "Select Count(*) AS test FROM Papers";
		//try {
		//	ResultSet results = statement.executeQuery(sql);//execute statement
		//		while(results.next()){
					
		//		return results.getInt("test");
		//		}
		//} catch (SQLException e) {
		//	e.printStackTrace();
		//}
		
		return 3000000;
	}	
	
	
	private static ArrayList<String> papersCiting (String id) {
		// find every paperID that cites our id parameter
		ArrayList<String> citesArray = new ArrayList<String>();

		String sql ="SELECT paperid FROM citations WHERE title LIKE (SELECT title FROM papers WHERE id = "+ id +");";
		try {
			ResultSet results = statement.executeQuery(sql);
			while(results.next()) {
				citesArray.add(results.getString("paperid"));
			}
		
			}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return citesArray;
	}
}
