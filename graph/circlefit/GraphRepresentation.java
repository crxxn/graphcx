package graph.circlefit;

import graph.AdjacencyMatrix;
import graph.circlefit.viz.*;

public class GraphRepresentation {

	/* just keep them public to be able to manipulate them more directly */
	public AdjacencyMatrix mat;
	public Ordering ord;
	double chunk;

	public void simulated_annealing(double temperature, double cr, double min_temp, boolean draw_progress) {
		ord.randomize(); /* randomize current ordering */
		if(draw_progress) {
			draw();
		}
		double fit = fitness();
		
		while (temperature > min_temp) {
			Ordering tmp_ord = new Ordering(ord);
			double tmp_fit = fit;
			
			for (int i = 0; i < temperature; i++) {
				tmp_ord.mutate();
			}
			tmp_fit = fitness(tmp_ord);
			
			if(tmp_fit < fit) {
				fit = tmp_fit;
				this.ord = new Ordering(tmp_ord);
				if(draw_progress) {
					draw();
				}
			}
			temperature = temperature-cr;
		}
		
	}
	
	
	public GraphRepresentation(AdjacencyMatrix mat) {
		this.mat = mat;
		this.ord = new Ordering(mat.getColumns());
		chunk = 2 * Math.PI / ord.n;
	}

	
	public double fitness() {
		return fitness(this.ord);
	}

	/* return fitness value for the current representation */
	public double fitness(Ordering ord) {
		double[] x = new double[ord.n];
		double[] y = new double[ord.n];
		
		/* calculate cartesian coordinates for each node of
		 * the current representation */
		for (int k=0; k<ord.n; k++) {
			x[k] = Math.cos(ord.data[k] * chunk);
			y[k] = Math.sin(ord.data[k] * chunk);
		}
		
		/* sum up the distance between adjacent nodes */
		double result = 0;
		/* iterate over the upper triangular matrix */
		for (int k=0; k<ord.n; k++) {
			for (int l=k+1; l<ord.n; l++) {
				if (mat.get(k, l) == 1) {
					/* add the distance between to adjacent nodes to the current fitness result */
					result = result + Math.sqrt( Math.pow(x[k]-x[l],2) + Math.pow(y[k]-y[l],2) );
				}
			}
		}
		return result;
	}
	
	public void draw() {
		GraphDrawer gd = GraphDrawer.getInstance();
		gd.updateData(this);
	}
	
	
}