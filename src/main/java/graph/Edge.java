package graph;

public class Edge {
	
	//Node[] nodes;
	private int[] vertices;

	public Edge(int i, int j) {
		//this.nodes = new Node[2];
		this.vertices= new int[2];
		vertices[0] = i;
		vertices[1] = j;
	}
	
	public int getVertex(int i) {
		return vertices[i];
	}
}