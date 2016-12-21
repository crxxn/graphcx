package graph.algo.vis;
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
	
	/* normalize layout to -1/1 viewport coordinates */
	public void normalizeLayout(double scaleFactor) {
		double[] max = new double[dimensions];
		double[] min = new double[dimensions];
		
		/* search maxima / minima */
		for(int d=0; d<dimensions; d++) {
			max[d] = getLayout(1, d);
			min[d] = getLayout(1, d);
		}
		for (int i=1; i<g.vertexCount(); i++) {
			for (int d=0; d<dimensions; d++) {

				if (getLayout(i,d) > max[d])
					max[d] = getLayout(i,d);
				if (getLayout(i,d) < min[d])
					min[d] = getLayout(i,d);
			}
		}
		
		/* translate vertices to the coordinate origin */
		for (int i=0; i<g.vertexCount(); i++) {
			double[] coords = new double[dimensions];
			for (int d=0; d<dimensions; d++) {
				coords[d] = getLayout(i,d) - min[d];
			}
			setLayout(i,coords);
		}

		
		
		double scale = max[0] - min[0];
		for (int d=1; d<dimensions; d++) {
			if (max[d] - min[d] > scale)
				scale = max[d] - min[d];
		}
		
		for (int i=0; i<g.vertexCount(); i++) {
			double[] coords = new double[dimensions];
			for (int d=0; d<dimensions; d++) {
				coords[d] = scaleFactor * getLayout(i,d) / scale; //normalization for viewport
			}
			setLayout(i,coords);
		}
		
		double[] avg = new double[dimensions];
		for (int i=0; i<g.vertexCount(); i++) {
			for (int d=0; d<dimensions; d++) {
				avg[d] += getLayout(i,d);
			}
		}
		
		for (int d=0; d<dimensions; d++) {
			avg[d] = avg[d] / g.vertexCount();
		}
	
		for (int i=0; i<g.vertexCount(); i++) {
			double[] coords = new double[dimensions];
			for (int d=0; d<dimensions; d++) {
				coords[d] = getLayout(i,d) - avg[d];
			}
			setLayout(i,coords);
		}
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
	
	public double distance(int i, int j) {
		double result = 0;
		for (int d=0; d<dimensions; d++) {
			result += Math.pow(getLayout(i, d)-getLayout(j, d), 2);
		}
		return Math.sqrt(result);
	}
	
	/*
	public void draw() {
		GraphDrawer gd = GraphDrawer.getInstance();
		gd.updateData(this);
	}
	*/
	
	
}