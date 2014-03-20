import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class PaperRank {
    private static HashMap<Integer, String> paperIDs;
    private static ArrayList<ArrayList<Integer>> papersCiting;
    private static double[] pageRanks;
    private static int[] outboundCitations;
    public static int maxPapers;
    static Statement statement;
    private static Connection conn;
    private static Statement st;

    public static void main(String[] args) {	
	startConnection();
	try {
	    maxPapers = getNumberOfPapers();
	} catch (SQLException e) {
	    System.err.println("Failed getting number of papers");
	    e.printStackTrace();
	    System.exit(1);
	}
	System.out.println(maxPapers + " papers found..");

	pageRanks = new double[maxPapers];
	outboundCitations = new int[maxPapers];
	papersCiting = new ArrayList<ArrayList<Integer>>(maxPapers);
	
	// Create Hashmap of index to paperid string
	paperIDs = new HashMap<>(maxPapers);
	try {
	    paperIDs = getPaperIDs(maxPapers);
	} catch (SQLException e) {
	    System.out.println("Error getting paperIDs.");
	    e.printStackTrace();
	    System.exit(1);
	}
	
	// initialise the whole array to 1
	Arrays.fill(pageRanks, 1.0);
	
	
	// Fill outboundCitations
	try {
	    setOutboundCitations();
	} catch (SQLException e) {
	    System.err.println("Getting outbound citation failed.");
	    e.printStackTrace();
	    System.exit(1);
	}
	
	try {
	    for (int i=0; i < maxPapers; i++) {
		papersCiting.add(papersCiting(i));
	    }
	} catch (SQLException e) {
	    System.err.println("Error gathering papers citing.");
	    e.printStackTrace();
	    System.exit(1);
	}
	System.out.println("Found all incoming citations to papers..");
	
	
	// Need to call ~20 times.
	for (int i = 1; i <= 20; i++) {
	    pageRank();
	}

	try {
	    insertPageRanks();
	} catch (SQLException e) {
	    System.err.println("Inserting PageRanks failed.");
	    e.printStackTrace();
	    System.exit(1);
	}
	endConnection();
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

    private static void pageRank() {
	// TODO decide on damping factor
	double d = 0.80;
	double culmativeRank = 0;
	// for every pagerank
	for (int i = 0; i < pageRanks.length; i++) {
	    culmativeRank = 0;
	    for (int paper : papersCiting.get(i)) {
		culmativeRank += pageRanks[paper] / outboundCitations[paper];
	    }
	    pageRanks[i] = (1 - d) + d * (culmativeRank);
	}
    }

    private static void setOutboundCitations() throws SQLException {
	PreparedStatement selectNC = null;

	String selectSQL = "SELECT numCites FROM papers;";
	try {
	    selectNC = conn.prepareStatement(selectSQL);
	    ResultSet rs = selectNC.executeQuery();// execute statement
	    int i = 0;
	    while (rs.next()) {
		outboundCitations[i] = rs.getInt("numCites");
		i++;
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	    System.exit(1);
	} finally {
	    if (selectNC != null) {
		selectNC.close();
	    }

	    System.out.println("Outbound citations gathered..");
	}
    }

    public static int getNumberOfPapers() throws SQLException {
	PreparedStatement selectCount = null;
	int num = 0;
	
	String selectSQL = "Select COUNT(*) AS count FROM papers";
	try {
	    selectCount = conn.prepareStatement(selectSQL);
	    ResultSet rs = selectCount.executeQuery();// execute statement
	    rs.next();
	    num = rs.getInt("count");
	} catch (SQLException e) {
	    e.printStackTrace();
	    System.exit(1);
	} finally {
	    if(selectCount != null) {
		selectCount.close();
	    }
	}
	return num;
    }

    private static ArrayList<Integer> papersCiting(int id) throws SQLException {
	// find every paperID that cites our id parameter
	PreparedStatement selectID = null;
	ArrayList<Integer> citesArray = new ArrayList<>();
	String paperID = paperIDs.get(id);

	String selectSQL = "SELECT paperid FROM citations WHERE title LIKE (SELECT title FROM papers WHERE id = ?) AND year = (SELECT year FROM papers WHERE id = ?);";
	try {
	    selectID = conn.prepareStatement(selectSQL);
	    selectID.setString(1, paperID);
	    selectID.setString(2, paperID);
	    ResultSet rs = selectID.executeQuery();
	    while (rs.next()) {
		String pID = rs.getString("paperid");
		citesArray.add(getIndexForID(pID));
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	    System.exit(1);
	} finally {
	    if (selectID != null) {
		selectID.close();
	    }

	    System.out.println("Found "+citesArray.size()+" papers citing " + paperIDs.get(id) + "\t (" + id + " of "+maxPapers+")");
	}
	return citesArray;
    }

    private static HashMap<Integer, String> getPaperIDs(int size) throws SQLException {
	// find every paperID that cites our id parameter
	PreparedStatement selectID = null;
	HashMap<Integer, String> map = new HashMap<>(size);

	String selectSQL = "SELECT id FROM papers;";
	try {
	    selectID = conn.prepareStatement(selectSQL);
	    ResultSet rs = selectID.executeQuery();
	    int i = 0;
	    while (rs.next()) {
		map.put(i, rs.getString("id"));
		i++;
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	    System.exit(1);
	} finally {
	    if(selectID != null) {
		selectID.close();
	    }

	    System.out.println("All " + size + " paper IDs collected..");
	}
	return map;
    }

    private static void insertPageRanks() throws SQLException {
	System.out.println("Beginning DB update of PageRanks");
	PreparedStatement updatePR = null;
	String updateString = "UPDATE papers SET pagerank=? WHERE id = ?;";
	try {
	    conn.setAutoCommit(false);
	    updatePR = conn.prepareStatement(updateString);
	    for (int i = 0; i < maxPapers; i++) {
		updatePR.setDouble(1, pageRanks[i]);
		updatePR.setString(2, paperIDs.get(i));
		updatePR.executeUpdate();
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
		    System.exit(1);
		}
	    }
	    System.exit(1);
	} finally {
	    if (updatePR != null) {
		updatePR.close();
	    }
	    conn.setAutoCommit(true);
	    System.out.println("PageRanks updated in DB..");
	}
    }

    /* Finds key for value in paperIDs, returns null if not found. */
    private static Integer getIndexForID(String paperID) {
	for (Entry<Integer, String> entry : paperIDs.entrySet()) {
	    if (paperID.equals(entry.getValue())) {
		return entry.getKey();
	    }
	}

	return null;
    }
}
