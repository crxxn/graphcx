package graph.list;

import java.util.ArrayList;

import graph.Edge;
import graph.matrix.AdjacencyMatrix;
import graph.matrix.IncidenceMatrix;

public class EdgeList extends ArrayList<Edge> {

	private static final long serialVersionUID = 1L;

	
	public EdgeList(AdjacencyMatrix aMat) {

		for(int i=0; i<aMat.getColumns(); i++) {
			for(int j=0; j<aMat.getColumns(); j++) {
				if (aMat.get(i,j) == 1) {
					this.add(new Edge(i,j));
				}
			}
		}
	
	}
	
	public EdgeList(IncidenceMatrix iMat) {
		
		for(int col=0; col<iMat.getColumns(); col++) {
			int first = -1;
			for(int line=0; line<iMat.getLines(); line++) {
				if(iMat.get(line, col) == 1) {
					if(first == -1) {
						first = line;
					} else {
						this.add(new Edge(first, line));
					}
					
				}
				
			}
		}
		
	}
	

}
