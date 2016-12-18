package graph.list;

import java.util.ArrayList;

import graph.matrix.AdjacencyMatrix;

public class AdjacencyList extends ArrayList<Integer>{
	
	private static final long serialVersionUID = -6972447550631375669L;

	
	/* create AdjacencyLists from AjacencyMatrix */
	public static AdjacencyList[] createAdjacencyLists(AdjacencyMatrix aMat) {
		int n = aMat.getColumns();
		AdjacencyList[] result = new AdjacencyList[n];

		for (int i=0; i<n; i++) {
			result[i] = new AdjacencyList();
			for (int j=0; j<n; j++) {
				if(aMat.get(i, j) == 1) {
					result[i].add(j);
				}
			}
		}
		
		return result;
	}
	

}
