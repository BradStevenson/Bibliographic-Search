import java.util.ArrayList;
import java.util.Arrays;


public class pagerank {
	private static double[][] paperMatrix;
	private static double[] pageRanks;

	public static void main(String[] args) {
		simMatrix();

		pageRanks = new double[paperMatrix.length];

		// init pageranks to 1
		Arrays.fill(pageRanks, 1.0);
		
		// Fill outboundCitations
		for (int i =0; i < outboundCitations.length; i++) {
			
		}

		System.out.print("\nOutbound Citations \n");

		testOutboundCitations();

		System.out.print("\nPapers Citing \n");

		testpapersCiting();

		System.out.print("\nPage Rank \n");

		testPageRank();
	}

	private static void pageRank() {
		double d = 0.8;
		double culmativeRank = 0;
		int counter = 0;
		// for every pagerank
		for (int i = 0; i<pageRanks.length; i++) {
			culmativeRank = 0;
			for (int paper : papersCiting(i)) {
				counter++;
				culmativeRank += pageRanks[paper] / outboundCitation(paper);
			}
			pageRanks[i] = (1 - d) + d*(culmativeRank);
		}
		System.out.println("iters " +counter);
	}

	private static double outboundCitation(int id) {
		double count = 0;

		// Loop over array of citations
		for (int row=0; row < paperMatrix.length; row++){
			// Increment if citations exists. ( != 0 )
			if (paperMatrix[row][id] > 0)
				count++;
		}

		return count;
	}

	private static ArrayList<Integer> papersCiting (int id) {
		ArrayList<Integer> citesArray = new ArrayList<Integer>();

		for (int column=0; column < paperMatrix[id].length; column++){
			if (paperMatrix[id][column] > 0)
				citesArray.add(column);
		}
		return citesArray;
	}

	// TESTING
	private static void testOutboundCitations() {
		for (int i = 0; i < paperMatrix.length; i++) {
			System.out.println("Paper " + (i) + " has " + outboundCitation(i) + " outbound citations");
		}
	}

	private static void testpapersCiting() {
		for (int i = 0; i < paperMatrix.length; i++) {
			//ArrayList<Integer> papers = papersCiting(i);
			// loop over papers, print i then all elements in papers
			System.out.println("Paper " + (i) + " was cited by papers " + papersCiting(i));
		}
	}

	private static void testPageRank() {
		for (int i = 0; i < 20; i++) {
			pageRank();	
		}
		double totalRank = 0;
		for (int i = 0; i < pageRanks.length; i++) {
			totalRank += pageRanks[i];
			System.out.println("Paper " + (i) + "'s page rank is " + pageRanks[i]);
		}
		System.out.println("\nCulmative Ranking is " + totalRank + " should be " + pageRanks.length);
	}

	private static void simMatrix() {
		paperMatrix = new double[8][8];

		// Row 1
		paperMatrix[0][0] = 0;
		paperMatrix[0][1] = 0;
		paperMatrix[0][2] = 0;
		paperMatrix[0][3] = 0;
		paperMatrix[0][4] = 0;
		paperMatrix[0][5] = 0;
		paperMatrix[0][6] = 0;
		paperMatrix[0][7] = 0;

		// Row 2
		paperMatrix[1][0] = 1.0/2.0;
		paperMatrix[1][1] = 0;
		paperMatrix[1][2] = 1.0/2.0;
		paperMatrix[1][3] = 1.0/3.0;
		paperMatrix[1][4] = 0;
		paperMatrix[1][5] = 0;
		paperMatrix[1][6] = 0;
		paperMatrix[1][7] = 0;

		// Row 3
		paperMatrix[2][0] = 1.0/2.0;
		paperMatrix[2][1] = 0;
		paperMatrix[2][2] = 0;
		paperMatrix[2][3] = 0;
		paperMatrix[2][4] = 0;
		paperMatrix[2][5] = 0;
		paperMatrix[2][6] = 0;
		paperMatrix[2][7] = 0;

		// Row 4
		paperMatrix[3][0] = 0;
		paperMatrix[3][1] = 1.0;
		paperMatrix[3][2] = 0;
		paperMatrix[3][3] = 0;
		paperMatrix[3][4] = 0;
		paperMatrix[3][5] = 0;
		paperMatrix[3][6] = 0;
		paperMatrix[3][7] = 0;

		// Row 5
		paperMatrix[4][0] = 0;
		paperMatrix[4][1] = 0;
		paperMatrix[4][2] = 1.0/2.0;
		paperMatrix[4][3] = 1.0/3.0;
		paperMatrix[4][4] = 0;
		paperMatrix[4][5] = 0;
		paperMatrix[4][6] = 1.0/2.0;
		paperMatrix[4][7] = 0;		


		// Row 6
		paperMatrix[5][0] = 0;
		paperMatrix[5][1] = 0;
		paperMatrix[5][2] = 0;
		paperMatrix[5][3] = 1.0/3.0;
		paperMatrix[5][4] = 1.0/3.0;
		paperMatrix[5][5] = 0;
		paperMatrix[5][6] = 0;
		paperMatrix[5][7] = 1.0/2.0;

		// Row 7
		paperMatrix[6][0] = 0;
		paperMatrix[6][1] = 0;
		paperMatrix[6][2] = 0;
		paperMatrix[6][3] = 0;
		paperMatrix[6][4] = 1.0/3.0;
		paperMatrix[6][5] = 0;
		paperMatrix[6][6] = 0;
		paperMatrix[6][7] = 1.0/2.0;

		// Row 8
		paperMatrix[7][0] = 0;
		paperMatrix[7][1] = 0;
		paperMatrix[7][2] = 0;
		paperMatrix[7][3] = 0;
		paperMatrix[7][4] = 1.0/3.0;
		paperMatrix[7][5] = 1.0;
		paperMatrix[7][6] = 1.0/2.0;
		paperMatrix[7][7] = 0;		
	}
}
