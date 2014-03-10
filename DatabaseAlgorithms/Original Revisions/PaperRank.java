import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class PaperRank {
    private static Connection conn;
    private static double[] PRs = new double[1000000];
    private static Statement st = conn.createStatement();
    
    public static void main(String[] args) {
		Arrays.fill(PRs, 1);
		startConnection();
	
		pageRank(1, 0);
	
	//	addPageRankToTable(1, pageRank(1, 0));
	//	addPageRankToTable(2, pageRank(2, 0));
	//	addPageRankToTable(3, pageRank(3, 0));
	
		endConnection();
    }
    
    private static void startConnection() {
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "SciSearcher";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root";
        String password = "password";
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
            st = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void pageRank(int paperID, int counter) {
	double damping = 0.8;
	double pageRank;
	counter += 1;
	
        ArrayList<Integer>incomingCitations = getTOfPaperID(paperID);
        double sumPRT = 0;
        Iterator<Integer> itr = incomingCitations.iterator();
        while(itr.hasNext()) {
    	    int ID = itr.next();
    	    double val = PRs[ID-1]/getNOfPaperID(ID);
    	    sumPRT += val;
        }
        pageRank = (1-damping) + (damping * sumPRT);
	
        PRs[paperID-1] = pageRank;
		addPageRankToTable(paperID, pageRank);
        
        // DEBUGGING USE, SHOWS UPDATED PAGE RANK FOR EACH ALGORITHM.
        // ERROR ON ITERATION 3 PR(C). (Value too small).
        for (int i = 0; i < counter+10; i++) {
        	System.out.println("PR("+i+") = " + PRs[i]);
        }
	System.out.println("---------- iteration " + counter + " complete\n");
	
	// Call recursively or end.
        if (counter < 100*PRs.length) {
            if (PRs.length >= paperID+1)
                pageRank(paperID+1, counter);
            else
                pageRank(1, counter);
        } else {
            System.out.println("Final = " + PRs[paperID-1]);
        }
    }
    
    private static ArrayList<Integer> getTOfPaperID(int paperID) {
	ArrayList<Integer> paperIDs = new ArrayList<Integer>();
        try {
            ResultSet res = st.executeQuery("SELECT Citations.paperID  FROM Papers INNER JOIN Citations ON Papers.paperID=Citations.citedPaperID WHERE Papers.paperID="+paperID);
            while (res.next()) {
        	int citingPaperID = res.getInt("paperID");
        	paperIDs.add(citingPaperID);
            }
            return paperIDs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static int getNOfPaperID(int paperID) {
        try {
            ResultSet res = st.executeQuery("SELECT COUNT(*) FROM Citations WHERE paperID="+paperID);
            int count = 0;
            while (res.next()) {
        	count = res.getInt("Count(*)");
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private static void addPageRankToTable(int paperID, double PR) {
        try {
            int val = st.executeUpdate("UPDATE Papers SET pageRank="+ PR +" WHERE paperID="+paperID);
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
}
