package demo;
import java.io.File;
import graph.*;
import graph.algo.vis.Representation;
import graph.algo.vis.annealing.Annealer;
import graph.algo.vis.circlefit.Circlefit;
import graph.matrix.IncidenceMatrix;

class Demos {
	public static void main(String[] args) {
		File input = new File("input.txt");
		/* store graph within an encapsulating representation object */
		Representation rep = new Representation(new Graph(IncidenceMatrix.parseAsciiMatrix(input)), 2);
		
		rep.getOrdering().randomize();
		Circlefit.layoutCircle(rep);
		
		
		Annealer.anneal(rep, Circlefit.fit_EdgeLengthSum, Circlefit.mutate, rep.getGraph().vertexCount(), 0.0002, true);
		
		//Annealer.anneal(rep, Circlefit.fit_EdgeCrossings, Circlefit.mutate, rep.getGraph().vertexCount(), 0.0002, true);
		
		System.out.println("done");
		
					 
	}
}