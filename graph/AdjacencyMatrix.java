package graph;
public class AdjacencyMatrix extends GraphMatrix {
	private AdjacencyMatrix(int nodes) {
		super(nodes, nodes);
	}
	
	public static AdjacencyMatrix convertIncidenceMatrix(IncidenceMatrix i)	{
		
		int nodes = i.getLines();
		int edges = i.getColumns();

		AdjacencyMatrix result = new AdjacencyMatrix(nodes);
	
		for (int n = 0; n<edges; n++) {
			/* store indices of incident nodes */
			int a = -1;
			int b = -1;
			boolean first = true;
			
			for(int m=0; m<nodes; m++) {
				if(i.get(m,n)==1 && first) {
					a = m; /* index of first node encountered on edge n */
					first = false;
				} else if (i.get(m,n)==1) {
					b = m; /* index of second node encountered on edge n */
				}
			}
			
			if (a == -1 || b == -1 || a == b) {
				System.err.println("Error: Constrcting Adjacency Matrix from malformed Incidence Matrix!");
				System.exit(1);
			}
			
			result.set(a, b, 1); /* the matrix shall be symmetric */
			result.set(b, a, 1);
		}
		return result;
	}
	
}