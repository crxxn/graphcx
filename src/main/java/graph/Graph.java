package graph;

import graph.list.AdjacencyList;
import graph.list.EdgeList;
import graph.matrix.*;

public class Graph {

	int v; //number of vertices
	int e; //number of edges

	private boolean aMatValid = false;
	AdjacencyMatrix aMat;
	
	private boolean iMatValid = false;
	IncidenceMatrix iMat;
	
	private boolean eListValid = false;
	EdgeList eList;
	
	private boolean aListValid = false;
	AdjacencyList[] aList;
	

	public Graph(IncidenceMatrix iMat) {
		this.iMat = iMat;
		iMatValid = true;
		this.v = iMat.getLines();
		this.e = iMat.getColumns();
	}
	
	public int vertexCount() {
		return v;
	}

	public int edgeCount() {
		return e;
	}
	
	public AdjacencyList[] getAList() {
		if (!aListValid)
			aList = AdjacencyList.createAdjacencyLists(this.getAMat());
		return aList;
	}
	
	public AdjacencyMatrix getAMat() {
		if (aMatValid) 
			return aMat;

		//@FIXME
		//if (iMatValid) {
			aMat = AdjacencyMatrix.convertIncidenceMatrix(iMat);
			aMatValid = true;
			return aMat;
		//}
	}
	
	public IncidenceMatrix getIMat() {
		//if (iMatValid)
			return iMat;
		
		//iMat = new IncidenceMatrix()
		//@FIXME: Missing IncidenceMatrix Constructor
		
	}
	
	public EdgeList getEdgeList() {
		if (eListValid)
			return eList;
		if (aMatValid) {
			eList = new EdgeList(aMat);
		} else if (iMatValid) {
			eList = new EdgeList(iMat);
		}
		eListValid = true;
		return eList;
	}
	

}
