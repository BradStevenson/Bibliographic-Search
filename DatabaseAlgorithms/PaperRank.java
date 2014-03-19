import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class PaperRank {
    private static HashMap<Integer, String> paperIDs;
    private static double[] pageRanks;
    private static int[] outboundCitations;
    public static int maxPapers;
    static Statement statement;
    private static Connection conn;
    private static Statement st;

    public static void main(String[] args) {
	startConnection();
	maxPapers = getNumberOfPapers();

	System.out.println(maxPapers + " papers found");

	pageRanks = new double[maxPapers];
	outboundCitations = new int[maxPapers];
	// Create Hashmap of index to paperid string
	paperIDs = new HashMap<>(maxPapers);
	paperIDs = getPaperIDs(maxPapers);
	// initialise the whole array to 1
	Arrays.fill(pageRanks, 1.0);
	// Fill outboundCitations
	try {
	    for (int i = 0; i < outboundCitations.length; i++) {
		outboundCitations[i] = getOutboundCitation(i);
	    }
	} catch (SQLException e) {
	    System.err.println("Getting outbound citation failed.");
	    e.printStackTrace();
	}

	// Need to call ~20 times.
	for (int i = 1; i <= 20; i++) {
	    pageRank();
	}

	try {
	    insertPageRanks();
	} catch (SQLException e) {
	    System.err.println("Inserting PageRanks failed.");
	    e.printStackTrace();
	}
	endConnection();
    }

    private static void startConnection() {
	String url = "jdbc:mysql://sproj08.cs.nott.ac.uk:3306/";
	String dbName = "SciSearcher";
	String driver = "com.mysql.jdbc.Driver";
	String userName = "root";
	String password = "mNNhv13uBB";
	try {
	    Class.forName(driver).newInstance();
	    conn = DriverManager
		    .getConnection(url + dbName, userName, password);
	    st = conn.createStatement();
	    System.out.println("Connected");
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
	    try {
		for (int paper : papersCiting(i)) {
		    culmativeRank += pageRanks[paper]
			    / outboundCitations[paper];
		}
		pageRanks[i] = (1 - d) + d * (culmativeRank);
	    } catch (SQLException e) {
		System.err
			.println("Error getting papers Citing for paper " + i);
		e.printStackTrace();
	    }
	}
    }

    private static int getOutboundCitation(int id) throws SQLException {
	PreparedStatement selectNC = null;
	int citationCount = 0;
	String paperID = paperIDs.get(id);

	// MYSQL QUERY FIND HOW MANY CITATIONS
	String selectSQL = "SELECT numCites FROM papers WHERE id =?;";
	try {
	    selectNC = conn.prepareStatement(selectSQL);
	    selectNC.setString(1, paperID);
	    ResultSet rs = selectNC.executeQuery();// execute statement
	    rs.next();

	    citationCount = rs.getInt("numCites");

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    if (selectNC != null) {
		selectNC.close();
	    }

	    System.out.println("Outbound citations gathered for "
		    + paperIDs.get(id));

	    return citationCount;
	}
    }

    public static int getNumberOfPapers() {
	int num = 0;
	String sql = "Select COUNT(*) AS count FROM Papers";
	try {
	    ResultSet results = statement.executeQuery(sql);// execute statement
	    results.next();
	    num = results.getInt("count");
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return num;
    }

    private static ArrayList<Integer> papersCiting(int id) throws SQLException {
	// find every paperID that cites our id parameter
	PreparedStatement selectID = null;
	ArrayList<Integer> citesArray = new ArrayList<>();
	String paperID = paperIDs.get(id);

	String selectSQL = "SELECT paperid FROM citations WHERE title LIKE (SELECT title FROM papers WHERE id = ?);";
	try {
	    selectID = conn.prepareStatement(selectSQL);
	    selectID.setString(1, paperID);
	    ResultSet rs = selectID.executeQuery();
	    while (rs.next()) {
		String pID = rs.getString("paperid");
		citesArray.add(getIndexForID(pID));
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    if (selectID != null) {
		selectID.close();
	    }

	    System.out.println("Found all papers citing " + paperIDs.get(id));

	    return citesArray;
	}
    }

    private static HashMap<Integer, String> getPaperIDs(int size) {
	// find every paperID that cites our id parameter
	HashMap<Integer, String> map = new HashMap<>(size);

	String sql = "SELECT paperid FROM papers;";
	try {
	    ResultSet results = statement.executeQuery(sql);
	    int i = 0;
	    while (results.next()) {
		map.put(i, results.getString("paperid"));
		i++;
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	}

	System.out.println("All " + size + " paper IDs collected");

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
		}
	    }
	} finally {
	    if (updatePR != null) {
		updatePR.close();
	    }
	    conn.setAutoCommit(true);
	    System.out.println("PageRanks updated in DB");
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
