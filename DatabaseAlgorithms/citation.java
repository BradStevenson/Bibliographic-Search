import java.sql.*;
import java.util.ArrayList;


public class citation {
static Statement statement;
public static void main(String[] args){



}

public static ArrayList<ArrayList<String>> findCitationFor(int ID){

	String sql ="SELECT c.* FROM citations AS c, papers AS p WHERE c.paperid = p.id ";
	ArrayList<ArrayList<String>> citedPapers = new ArrayList<ArrayList<String>>();

	try {
		ResultSet results = statement.executeQuery(sql);
		while(results.next()){
			ArrayList<String> citation = new ArrayList<String>();
			citation.add(results.getString("author"));
			citation.add(results.getString("title"));
			citation.add(results.getString("year"));
			citedPapers.add(citation);
		}
		//results.absolute(num);

		return citedPapers;

	} catch (SQLException e) {
		e.printStackTrace();
	}

	return null;
}

public static int searchForPaper(String author, String title, int year) {
	int paperid=0;
	String sql ="SELECT id FROM papers "
			+ "WHERE authors = (SELECT authors FROM citations WHERE authors = "+author+") "
			+ "AND title = (SELECT title FROM citations WHERE title = "+title+" "
			+ "AND year = (SELECT year FROM citations WHERE year = "+year+")";
	try {
		ResultSet results = statement.executeQuery(sql);
		while(results.next())
		paperid = results.getInt("id");
		
		}
	catch (SQLException e) {
		e.printStackTrace();
	}
return paperid;
	
}

public static void addCitedPaper(int paperid){

	String sql ="INSERT INTO citations (citationpaperid)";
	try {
		statement.executeQuery(sql);

	} catch (SQLException e) {
		e.printStackTrace();
	}

}
}

