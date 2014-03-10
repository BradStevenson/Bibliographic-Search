package citations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DecimalFormat;
public class pagerank2 {
	private static double[] pageRanks;
	private static int[] outboundCitations;
	public static int maxPapers;
	static Statement statement;
	public static void main(String[] args) {
		//simMatrix();
		
		// HOW MANY PAPERS
		
		maxPapers = getNumberOfPapers();
		pageRanks = new double[maxPapers];
		outboundCitations = new int[maxPapers];
		// init pageranks to 1
		Arrays.fill(pageRanks, 1.0);
		// Fill outboundCitations
		for (int i = 0; i < outboundCitations.length; i++) {
			outboundCitations[i] = getOutboundCitation(i);
		}
		System.out.print("\nOutbound Citations \n");
		//testOutboundCitations();
		System.out.print("\nPapers Citing \n");
		//testpapersCiting();
		System.out.print("\nPage Rank \n");
		//testPageRank();
	}
	private static void pageRank() {
		double d = 0.8;
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
			String sql = "Select ncites from papers where paperID ='"+id;
			try {
				ResultSet results = statement.executeQuery(sql);//execute statement
					results.next();
						 
				//return 3;
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
	
	private static String getAuthor(int id) {
		String sql ="SELECT authors FROM papers WHERE id ="+id+"";
		try {
			ResultSet results = statement.executeQuery(sql);
			results.next();
			return results.getString(1);
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getTitle(int id) {
		String sql ="SELECT title FROM papers WHERE id ="+id+"";
		try {
			ResultSet results = statement.executeQuery(sql);
			results.next();
			return results.getString(1);
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static int getYear(int id) {
		String sql ="SELECT year FROM papers WHERE id ="+id+"";
		try {
			ResultSet results = statement.executeQuery(sql);
			results.next();
			return results.getInt(1);
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private static ArrayList<Integer> papersCiting (int id) {
		// find every paperID that cites our id parameter
		ArrayList<Integer> citesArray = new ArrayList<Integer>();
		int paperid=0;
		String sql ="SELECT id FROM papers "
				+ "WHERE authors = (SELECT authors FROM citations WHERE authors = "+getAuthor(id)+") "
				+ "AND title = (SELECT title FROM citations WHERE title = "+getTitle(id)+" "
				+ "AND year = (SELECT year FROM citations WHERE year = "+getYear(id)+")";
		try {
			ResultSet results = statement.executeQuery(sql);
			while(results.next())
			paperid = results.getInt("id");
			citesArray.add(paperid);

			}
		catch (SQLException e) {
			e.printStackTrace();
		}
		// get title, author, year of id
		
		// pass this into search for papers
		
		// append results into our array
		
		// return array
		return citesArray;
	}
}
