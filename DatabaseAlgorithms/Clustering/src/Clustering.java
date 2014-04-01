import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Clustering implements java.io.Serializable {
    private static Connection conn;
    private static final long serialVersionUID = 1L;
    private static DirectedGraph<String, DefaultEdge> graph;

    public static void main(String[] args) {
	startConnection();
	
	graph = createCitationGraph();
	
	serializeGraph();
	
	ConnectivityInspector<String, DefaultEdge> conInspect = new ConnectivityInspector<String, DefaultEdge>(graph);
	List<Set<String>> clusters = conInspect.connectedSets();
	Iterator<Set<String>> itr = clusters.iterator();
	
	int clusterID = 1;
	while(itr.hasNext()) {
	    Set<String> cluster = itr.next();
	    try {
		setClusterIDs(cluster, clusterID);
	    } catch (SQLException e) {
		System.err.println("Error updating cluster " + clusterID +".");
		e.printStackTrace();
	    }
	    clusterID++;
	}
	
	endConnection();
    }
    
    private static DirectedGraph<String, DefaultEdge> createCitationGraph()
    {
        DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        try {
            fillVertexes(g);
        } catch (SQLException e) {
            System.err.println("Error filling vertexes.");
            e.printStackTrace();
        }
        
        try {
            addEdges(g);
        } catch (SQLException e) {
            System.err.println("Error adding edges.");
            e.printStackTrace();
        }

        return g;
    }

    private static void fillVertexes(DirectedGraph<String, DefaultEdge> g) throws SQLException {
	System.out.println("importing papers as vertexes..");
	
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
	} finally {
	    if(selectID != null) {
		selectID.close();
	    }

	    System.out.println("All paper IDs imported as vertexes.");
	}
    }
    
    private static void addEdges(DirectedGraph<String, DefaultEdge> g) throws SQLException {
	// find every paperID that cites our id parameter
	System.out.println("Adding citations as edges..");
	Statement selectID = null;
	String selectSQL = "SELECT citations.paperid, papers.id FROM papers INNER JOIN citations ON citations.title = papers.title AND citations.year = papers.year;";
	
	// GET ALL INCOMING LINKS TO PAPERS
	
	try {
	    selectID = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
	    selectID.setFetchSize(Integer.MIN_VALUE);
	    ResultSet rs = selectID.executeQuery(selectSQL);
	    String from;
	    String to;
	    int i = 1;
	    System.out.println("iterating");
	    while(rs.next()) {
		from = rs.getString("paperid");
		to = rs.getString("id");
		g.addEdge(from, to);
		System.out.println("Added edge " + from + "\t->\t" + to + " \t  "+ (i/1956035)*100 +"%");
		i++;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    if (selectID != null) {
		try {
		    selectID.close();
	        } catch (Exception e) {
	            System.out.println("Error closing statement");
	        }
	    }
	    System.out.println("All citations added as edges.");
	}
    }
    
    private static void setClusterIDs(Set<String> cluster, int id) throws SQLException {
	System.out.println("Beginning DB update of ClusterIDs for cluster " + id+"..");
	PreparedStatement updateCID = null;
	String updateString = "UPDATE papers SET pagerank=? WHERE id = ?;";
	try {
	    conn.setAutoCommit(false);
	    updateCID = conn.prepareStatement(updateString);
	    Iterator<String> itr = cluster.iterator();
	    while (itr.hasNext()) {
		String paperID = itr.next();
		updateCID.setInt(1, id);
		updateCID.setString(2, paperID); 
		updateCID.executeUpdate();
		conn.commit();   
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	    if (conn != null) {
		try {
		    System.err.print("Transaction is being rolled back");
		    conn.rollback();
		} catch (SQLException excep) {
		    excep.printStackTrace();
		}
	    }
	} finally {
	    if (updateCID != null) {
		updateCID.close();
	    }
	    conn.setAutoCommit(true);
	    System.out.println("Cluster "+id+" updated in DB..");
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
    
    public static void serializeGraph() {
	System.out.println("Serializing graph..");
	try {
	FileOutputStream fileOut = new FileOutputStream("./CitationGraph.ser");
	ObjectOutputStream out = new ObjectOutputStream(fileOut);
	out.writeObject(graph);
	out.close();
	fileOut.close();
	} catch (IOException i) {
	    i.printStackTrace();
	}
	System.out.println("Graph serialized.");
    }
    
    public static DirectedGraph<String, DefaultEdge> deserializeGraph() {
	try {
        	FileInputStream fileIn = new FileInputStream("./CitationGraph.ser");
        	ObjectInputStream in = new ObjectInputStream(fileIn);
        	DirectedGraph<String, DefaultEdge> graph = (DirectedGraph<String, DefaultEdge>) in.readObject();
        	in.close();
        	fileIn.close();
        	return graph;
	} catch (IOException i) {
	    i.printStackTrace();
	return null;
	} catch (ClassNotFoundException c) {
	    c.printStackTrace();
	return null;
	}
    }
}
