package demo;
import java.io.File;
import java.util.Scanner;

import graph.*;
import graph.algo.vis.Representation;
import graph.algo.vis.annealing.Annealer;
import graph.algo.vis.annealing.Fitness;
import graph.algo.vis.annealing.Layout;
import graph.algo.vis.fd.ForceDirection;
import graph.algo.vis.gl.GraphDrawer;
import graph.matrix.IncidenceMatrix;

class Demos {
	public static void main(String[] args) {
		File input = new File("input.txt");
		/* store graph within an encapsulating representation object */
		Representation rep = new Representation(new Graph(IncidenceMatrix.parseAsciiMatrix(input)), 2);

		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);
		
		rep.getOrdering().randomize();
		
		//Layout.circle.accept(rep);
		Layout.circle.accept(rep);
		GraphDrawer g = GraphDrawer.getInstance(rep);
		s.nextLine();
	
		/* Simulated annealing demos */
		/* circle + edge length */
		//rep = Annealer.anneal(rep, Fitness.fit_EdgeLengthSum, Fitness.mutate, Layout.circle, rep.getGraph().vertexCount(), 0.0002, true);

		/* circle + #intersections */
		//rep = Annealer.anneal(rep, Fitness.fit_EdgeCrossings, Fitness.mutate, Layout.circle, rep.getGraph().vertexCount(), 0.0002, true);

		/* square + edge length */
		//rep = Annealer.anneal(rep, Fitness.edgeLengthSum, Fitness.permute_random, Layout.square, rep.getGraph().vertexCount(), 0.0002, true);
		
		/* square + #intersections */
		//rep = Annealer.anneal(rep, Fitness.fit_EdgeCrossings, Fitness.mutate, Layout.square, rep.getGraph().vertexCount(), 0.0002, true);

	
		/* Force-direction demos */
		/* bad annealer to speed up FD a little bit */
		rep = Annealer.anneal(rep, Fitness.edgeLengthSum, Fitness.permute_random, Layout.square, rep.getGraph().vertexCount(), 0.2, true);

		System.out.println("done annealing / press enter to continue to FD");
		g.updateData(rep);

		s.nextLine();
		ForceDirection.forceDirection(rep, 0.001, 0.0005, 0.005, true);
	}
}