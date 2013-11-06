import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;


public class MockDBGenerator {
    private static Connection conn;
    private static int lastIdentifier = 1;
    
    public static void main(String[] args) {
	startConnection();
	
	for (int i = 2; i < 1000000; i++) {
	    addPaperToTable();
	    Random r = new Random();
	    int randAmountOfCitations;
	    if (lastIdentifier<50) {
		randAmountOfCitations = r.nextInt(lastIdentifier-1) + 1;
	    } else {
		randAmountOfCitations = r.nextInt(50-1) + 1;
	    }
	    for (int j=0; j < randAmountOfCitations; j++) {
		addCitationsForPaper(i);
	    }
	    System.out.println("Added Paper " + i);
	}
	
	endConnection();
    }
    
    private static void startConnection() {
        String url = "jdbc:mysql://localhost:3306/SciSearcher";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root";
        String password = "t4k34ch4nc3";
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url,userName,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void addPaperToTable() {
	lastIdentifier += 1;
	String randomIdentifier = Integer.toString(lastIdentifier);
	Random r = new Random();
	int randYear = r.nextInt(2013-1900) + 1900;
	
	try {
	    Statement st = conn.createStatement();
	    String insertQuery = "INSERT INTO Papers"
		    + "(title, author, year) VALUES "
		    + "('"+randomIdentifier+"Title', '"+randomIdentifier+"Author', "+randYear+")";
	    int val = st.executeUpdate(insertQuery);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
    
    private static void addCitationsForPaper(int paperID) {
	lastIdentifier += 1;
	Random r = new Random();
	int randPaperID = r.nextInt(lastIdentifier-1) + 1;
	
	try {
	    Statement st = conn.createStatement();
	    int val = st.executeUpdate("INSERT INTO Citations (paperID, citedPaperID) VALUES ("+paperID+", "+randPaperID+")");
	} catch (SQLException e) {
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
}
