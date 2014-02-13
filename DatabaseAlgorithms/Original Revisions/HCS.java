public class kMeansAlgorithm {
    public boolean[][] graph;
    
    public static void main(String[] args) {
	MatrixGraph g = new MatrixGraph();
	this.graph = g.graph;
	HCS(this.graph);
    }
    
    private void HCS(boolean[][] graph) {
	
	ArrayList<boolean[][]> subGraphs = mincut(Graph);
	
	for (graph : subGraphs) {
	    if (edgeConnectivity(graph) > numberOfNodes(graph)/2f) {
		return graph;
	    } else {
		HCS(graph);
	    }
	}
    }
    
    private ArrayList<> mincut(boolean[][] graph) {
	Random r = new Random();
	while (amountOfEdges(graph) > 2) {
	    int rand = r.nextInt(amountOfEdges(graph));
	    int[][] edge = extractEdges(graph).get(rand);
	    Graph = Graph/edge
	}
	return the only cut in 
    }
    
    private double edgeConnectivity(graph) {
	
    }
    
    private HashMap<Integer, Integer> extractEdges(graph) {
	HashMap<Integer, Integer> edges = new HashMap<Integer, Integer>();
	for (int i = 0; i < graph.length; i++) {
	    for (int j = 0; j < graph[].length; j++) {
		if (graph[i][j])
		 edges.add(i,j);
	    }
	}
	return edges;
    }
    
    private double amountOfEdges(graph) {
	double edgeCount = 0;
	for (int i = 0; i < graph.length; i++) {
	    for (int j = 0; j < graph[].length; j++) {
		if (graph[i][j])
		 edgeCount++;
	    }
	}
	return edgeCount;
    }
    
    private double numberOfNodes(boolean[][] graph) {
	return graph.size();
    }
}
