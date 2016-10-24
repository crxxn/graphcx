package graph.algo.vis.annealing;

import java.util.function.Consumer;

import graph.algo.vis.Representation;

public class Layout {

	public static Consumer<Representation> circle = rep -> {
		for (int i=0; i<rep.getGraph().vertexCount(); i++) {
			double[] coord = new double[2];
			coord[0] = Math.cos(rep.getChunk()*rep.getOrdering().getData()[i]);
			coord[1] = Math.sin(rep.getChunk()*rep.getOrdering().getData()[i]);
			rep.setLayout(i, coord);
		}
	};

	public static Consumer<Representation> square = rep -> {
		int n = rep.getGraph().vertexCount();
		int size = (int) Math.ceil(Math.sqrt(n));
		
		for (int i=0; i<rep.getGraph().vertexCount(); i++) {
			double x = rep.getOrdering().getData()[i] % size;
			double y = rep.getOrdering().getData()[i] / size;
			
			double[] coord = new double[2];
			/* (size-1) for graphical normalization to get the right coordinates */
			coord[0] = 2 * x /  (size-1) - 1;
			coord[1] = 2 * y / (size-1) - 1;
			rep.setLayout(i,  coord);
		}
	};
}
