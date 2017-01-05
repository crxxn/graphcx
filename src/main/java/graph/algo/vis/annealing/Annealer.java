package graph.algo.vis.annealing;

import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

import graph.algo.vis.Representation;
import graph.algo.vis.opengl.GraphDrawer;

public class Annealer {

	/**
	 * Simulated annealing, analogous to statistical physics.
	 * A simple genetic approach to improve a graph layout with
	 * decreasing entropy wrt to a given fitness function.
	 *
	 * @param rep encapsulating the layout and graph structure
	 * @param fitFkt evaluates the quality of a layout
	 * @param mutator alternates the ordering of a graph
	 * @param layout redraws the layout stored in representation
	 * @param temperature initial temperature,
	 *        influences the degree of mutation per iteration
	 * @param coolingrate defines how fast the degree of mutation
	 *        per iteration decreases
	 * @param draw enable automatic re-drawing of intermediate layouts,
	 *        useful for animation purposes
	 * @return final graph representation found in the process
	 */
	public static Representation anneal( Representation rep,
						ToDoubleFunction<Representation> fitFkt,
						Consumer<Representation> mutator,
						Consumer<Representation> layout,
						double temperature,
						double coolingrate,
						boolean draw) {

		// initialize layout randomly
		rep.getOrdering().randomize();
		layout.accept(rep);

		if (draw) {
			GraphDrawer gd = GraphDrawer.getInstance(rep);
			gd.updateData(rep);
		}
		
		double current_best = fitFkt.applyAsDouble(rep);
		while (temperature > 0) {
			
			Representation candidate = new Representation(rep, rep.getDimensions());
			double candidate_fitness = current_best;
			
			for (int i=0; i<temperature; i++)
				mutator.accept(candidate);

			layout.accept(candidate);
			candidate_fitness = fitFkt.applyAsDouble(candidate);
			
			if (candidate_fitness < current_best) {
				current_best = candidate_fitness;
				rep = candidate;

				if (draw) {
					GraphDrawer gd = GraphDrawer.getInstance(rep);
					gd.updateData(rep);
				}
			}
			temperature = temperature-coolingrate;
		}
		return rep;
	}
}
