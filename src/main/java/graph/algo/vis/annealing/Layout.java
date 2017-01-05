package graph.algo.vis.annealing;

import java.util.function.Consumer;

import graph.algo.vis.Representation;

/**
 * Functionality to construct cartesian layouts from vertex orderings
 */
public class Layout {

	/**
	 * creates an equal-angeled unit circle layout
	 */
	public static Consumer<Representation> circle = rep -> {
		for (int i=0; i<rep.getGraph().vertexCount(); i++) {
			float[] coord = new float[2];
			coord[0] = (float) Math.cos(rep.getChunk()*rep.getOrdering().getData()[i]);
			coord[1] = (float) Math.sin(rep.getChunk()*rep.getOrdering().getData()[i]);
			rep.setLayout(i, coord);
		}
	};

	/**
	 * creates a discrete grid layout
	 */
	public static Consumer<Representation> square = rep -> {
		int n = rep.getGraph().vertexCount();
		int size = (int) Math.ceil(Math.sqrt(n));
		
		for (int i=0; i<rep.getGraph().vertexCount(); i++) {
			float x = rep.getOrdering().getData()[i] % size;
			float y = rep.getOrdering().getData()[i] / size;
			
			float[] coord = new float[2];
			/* (size-1) for graphical normalization to get the right coordinates */
			coord[0] = 2 * x /  (size-1) - 1;
			coord[1] = 2 * y / (size-1) - 1;
			rep.setLayout(i,  coord);
		}
	};
}
