import java.net.*;
import java.sql.*;
import java.util.Iterator;

import org.jgrapht.*;
import org.jgrapht.graph.*;


public class Clustering {
    private static Connection conn;
    private static Statement st;

    public static void main(String[] args) {
	startConnection();
	
	UndirectedGraph<String, DefaultEdge> graph = createGraph();
	
	endConnection();
    }
    
    private static UndirectedGraph<String, DefaultEdge> createGraph()
    {
        UndirectedGraph<String, DefaultEdge> g =
            new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        try {
            fillVertexes(g);
        } catch (SQLException e) {
            System.err.println("Error filling vertexes.");
            e.printStackTrace();
            System.exit(1);
        }
        
        try {
            addEdges(g);
        } catch (SQLException e) {
            System.err.println("Error filling vertexes.");
            e.printStackTrace();
            System.exit(1);
        }

        return g;
    }

    private static void fillVertexes(UndirectedGraph<String, DefaultEdge> g) throws SQLException {
	PreparedStatement selectID = null;
	String selectSQL = "SELECT id FROM papers;";
	try {
	    selectID = conn.prepareStatement(selectSQL);
	    ResultSet rs = selectID.executeQuery();

	    while (rs.next()) {
		g.addVertex(rs.getString("id"));
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	    System.exit(1);
	} finally {
	    if(selectID != null) {
		selectID.close();
	    }

	    System.out.println("All paper IDs imported as vertexes..");
	}
    }
    
    private static void addEdges(UndirectedGraph<String, DefaultEdge> g) throws SQLException {
	// find every paperID that cites our id parameter
	PreparedStatement selectID = null;
	String selectSQL = "SELECT paperid FROM citations WHERE title LIKE (SELECT title FROM papers WHERE id = ?) AND year = (SELECT year FROM papers WHERE id = ?);";
	
	Iterator<String> itr = g.vertexSet().iterator();
	while(itr.hasNext()) {
	    String toID = itr.next();
	    String fromID = "";
	    // GET ALL INCOMING LINKS TO PAPERS
	    try {
		    selectID = conn.prepareStatement(selectSQL);
		    selectID.setString(1, toID);
		    selectID.setString(2, toID);
		    ResultSet rs = selectID.executeQuery();
		    rs.next();
		    fromID = rs.getString("paperid");
		    g.addEdge(fromID, rs.getString("paperid"));

	    } catch (SQLException e) {
		    e.printStackTrace();
		    System.exit(1);
	    } finally {
		if (selectID != null) {
		    selectID.close();
		}
		
		System.out.println("Added edge from "+fromID+" to "+toID);
	    }
	}
    }
    
    private static void startConnection() {
	String url = "jdbc:mysql://127.0.0.1:3306/";
	String dbName = "SciSearcher";
	String driver = "com.mysql.jdbc.Driver";
	String userName = "root";
	String password = "mNNhv13uBB";
	try {
	    System.out.println("Starting Connection..");
	    Class.forName(driver).newInstance();
	    conn = DriverManager
		    .getConnection(url + dbName, userName, password);
	    st = conn.createStatement();
	    System.out.println("Connected.");
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
