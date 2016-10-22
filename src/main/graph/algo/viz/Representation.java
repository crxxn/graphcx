package graph.algo.viz;
import graph.Graph;

public class Representation {

	private Graph g;
	private Ordering ord;
	private double chunk;
	private int dimensions;
	private double[][] Layout;


	public Ordering getOrdering() {
		return ord;
	}
	public Graph getGraph() {
		return g;
	}
	public double getChunk() {
		return chunk;
	}
	public int getDimensions() {
		return dimensions;
	}
	
	public void rescaleLayout() {
		
	}
	
	public Representation(Representation r, int dimensions) {
		this.g = r.g;
		this.ord = new Ordering(r.ord);
		this.chunk = r.chunk;
		this.dimensions = dimensions;
		this.Layout = new double[g.vertexCount()][dimensions];
	}
	
	public Representation (Graph g, int dimensions) {
		this.g = g;
		this.ord = new Ordering(g.vertexCount());
		this.chunk = 2 * Math.PI / g.vertexCount();
		this.dimensions = dimensions;
		this.Layout = new double[g.vertexCount()][dimensions];
	}
	
	public void setLayout(int vertex, double[] coordinates) {
		for (int i=0; i<dimensions; i++) {
			this.Layout[vertex][i] = coordinates[i];
		}
	}
	
	public double getLayout(int vertex, int component) {
		return Layout[vertex][component];
	}
	
	
	/*
	public void draw() {
		GraphDrawer gd = GraphDrawer.getInstance();
		gd.updateData(this);
	}
	*/
	
	
}