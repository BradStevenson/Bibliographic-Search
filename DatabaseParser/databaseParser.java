import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class parser {
    private static Connection conn;
    private static double[] PRs = {1,1,1};
    
    public static void main(String[] args) {
    startConnection();
    
    pageRank(1, 0);
    
//  addPageRankToTable(1, pageRank(1, 0));
//  addPageRankToTable(2, pageRank(2, 0));
//  addPageRankToTable(3, pageRank(3, 0));
    
    endConnection();
    }
    
    private static void startConnection() {
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "SciSearcher";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root";
        String password = "t4k34ch4nc3";
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
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
        
        // DEBUGGING USE, SHOWS UPDATED PAGE RANK FOR EACH ALGORITHM.
        // ERROR ON ITERATION 3 PR(C). (Value too small).
        for (int i = 0; i < PRs.length; i++) {
            if (i==0) { 
            System.out.println("PR(A) = " + PRs[i]);
            } else if (i == 1) {
            System.out.println("PR(B) = " + PRs[i]);
            } else {
            System.out.println("PR(C) = " + PRs[i]);
            }
        }
    System.out.println("---------- iteration " + counter + " complete\n");
    
    // Call recursively or end.
        if (counter < 100) {
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
            Statement st = conn.createStatement();
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
            Statement st = conn.createStatement();
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
            Statement st = conn.createStatement();
            int val = st.executeUpdate("INSERT INTO Papers (pageRank) VALUES ("+ PR +")");
            if(val==1)
                System.out.print("Successfully inserted value");
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
