package graph.algo.viz.annealing;

import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

import graph.algo.viz.Representation;
import graph.algo.viz.circlefit.Circlefit;
import graph.algo.viz.gl.GraphDrawer;

public class Annealer {

	public static void anneal(	Representation rep, 
						ToDoubleFunction<Representation> fitness,
						Consumer<Representation> mutator,
						double temp, 	//initial temperature
						double coolingrate,
						boolean draw) {

		rep.getOrdering().randomize();
		Circlefit.layoutCircle(rep);

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

			Circlefit.layoutCircle(tmp_rep);
		
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
	}

}