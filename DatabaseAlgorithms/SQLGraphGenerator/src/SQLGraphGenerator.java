import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class Main {
	public static int maxPapers;
	public static double [][] graphMatrix;
	static Statement statement;
	
public static void main(String[] args){
	
	 maxPapers = getNumberOfPapers();
	 graphMatrix = new double [maxPapers][maxPapers];
	 insertIntoGraph();
	 printGraph();
	
}



public static int getNumberOfPapers(){ //not including marked deleted mails
	String sql = "Select Count(*) AS test FROM Papers";
	try {
		ResultSet results = statement.executeQuery(sql);//execute statement
			while(results.next()){
				

			return results.getInt("test");
			}
	} catch (SQLException e) {
		e.printStackTrace();
	}

	
	return 10;
}	


public static void printGraph(){
	for(int row =0; row < maxPapers; row ++){
		System.out.print("\n");
		for(int column =0; column < maxPapers; column ++){
			System.out.print(graphMatrix[row][column]+",");
		}
		
	}
	
}

public static void insertIntoGraph(){
	DecimalFormat myFormat = new DecimalFormat("0.000"); // format
	
	for(int row = 0; row < maxPapers; row++){
		 ArrayList<Integer> citedArray = citedPaperID(row);
			if(citedArray.size() != 0){	
				for(int column = 0; column < citedArray.size(); column++){
					System.out.print(","+ citedArray.get(column).shortValue());
					graphMatrix[row][citedArray.get(column)] = 1.0/getnCites(citedArray.get(column));
				}
			}
		
	}
	
	
	
}

public static int getnCites(int ID){
	String sql = "Select ncites from papers where paperID ='"+ID;
	int total = 0;
	try {
		ResultSet results = statement.executeQuery(sql);//execute statement
			results.next();
				 
		//	return 3;
			return  results.getInt(1);
			
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return 0;
}



public static ArrayList<Integer> citedPaperID(int ID){
	// ADD CHECK FOR YEAR AND AUTHOR.
	String sql ="Select id FROM paper WHERE title = (Select title FROM citation WHERE paperid = "+ID+")";
	ArrayList<Integer> citedPapers = new ArrayList<Integer>();
	int i=0;
	try {
		ResultSet results = statement.executeQuery(sql);
		while(results.next()){
			citedPapers.add(results.getInt(i+1));
		}
		//results.absolute(num);

		return citedPapers;
			
	} catch (SQLException e) {
		e.printStackTrace();
	}

	return null;
	//test data
	//citedPapers.add(2);
	//citedPapers.add(3);
	//citedPapers.add(4);
	//citedPapers.add(5);
	//citedPapers.add(7);
	//citedPapers.add(9);
	//return citedPapers;
}
}





