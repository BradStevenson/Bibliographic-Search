import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class MatrixGraph implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private static Connection conn;
    public static boolean[][] graph;
    
    public MatrixGraph() {
	startConnection();
	for (int paper : getAllPaperIDs()) {
	    for(int citedPaper : getPapersCitedByPaperID(paper)) {
		addEdge(paper, citedPaper);
	    }
	}
	
	endConnection();
    }
    
    private static void startConnection() {
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "SciSearcher";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "root";
        String password = "";
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
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
    
    void addEdge(int i, int j) {
        graph[i][j] = true;
    }
    
    private static ArrayList<Integer> getPapersCitedByPaperID(int paperID) {
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
    
    private static ArrayList<Integer> getAllPaperIDs() {
	ArrayList<Integer> papers = new ArrayList<Integer>();
        try {
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT paperID FROM Papers");
            while (res.next()) {
        	int citingPaperID = res.getInt("paperID");
        	papers.add(citingPaperID);
            }
            return papers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean[][] deserializeGraph() {
	try {
	    FileInputStream fileIn = new FileInputStream("/matrixGraph.ser");
	    ObjectInputStream in = new ObjectInputStream(fileIn);
	    boolean[][] users = (boolean[][]) in.readObject();
	    in.close();
	    fileIn.close();
	    return users;
	} catch (IOException i) {
	    i.printStackTrace();
	    return null;
	} catch (ClassNotFoundException c) {
	    c.printStackTrace();
	    return null;
	}
    }

    public static void serializeGraph() {
	try {
	    FileOutputStream fileOut = new FileOutputStream("/matrixGraph.ser");
	    ObjectOutputStream out = new ObjectOutputStream(fileOut);
	    out.writeObject(graph);
	    out.close();
	    fileOut.close();
	} catch (IOException i) {
	    i.printStackTrace();
	}
    }
}
