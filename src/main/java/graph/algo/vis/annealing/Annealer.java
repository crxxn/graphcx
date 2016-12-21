package graph.algo.vis.annealing;

import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

import graph.algo.vis.Representation;
import graph.algo.vis.opengl.GraphDrawer;

public class Annealer {

	public static Representation anneal(	Representation rep, 
						ToDoubleFunction<Representation> fitness,
						Consumer<Representation> mutator,
						Consumer<Representation> layout,
						double temp, 	//initial temperature
						double coolingrate,
						boolean draw) {

		rep.getOrdering().randomize();
		layout.accept(rep);
		if (draw) {
		GraphDrawer gd = GraphDrawer.getInstance(rep);
		gd.updateData(rep);
		}
		
		double cur_fit = fitness.applyAsDouble(rep);
		
		while (temp > 0) {
			
			Representation tmp_rep = new Representation(rep, rep.getDimensions());

			double tmp_fit = cur_fit;
			
			for (int i=0; i<temp; i++) {
				mutator.accept(tmp_rep);
			}

			layout.accept(tmp_rep);
		
			tmp_fit = ((ToDoubleFunction<Representation>)fitness).applyAsDouble(tmp_rep);
			
			if(tmp_fit < cur_fit) {
				cur_fit = tmp_fit;
				rep = tmp_rep;
				if(draw) {
					/* the next line is actually needed & annoying, but not really harmful... */
					GraphDrawer gd = GraphDrawer.getInstance(rep);
					gd.updateData(rep);
				}
			}
			temp = temp-coolingrate;
		}
		return rep;
	}

}