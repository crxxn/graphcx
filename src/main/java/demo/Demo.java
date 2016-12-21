package demo;
import java.io.File;
import java.util.Scanner;

import graph.*;
import graph.algo.connectivity.Dijkstra;
//import graph.algo.connectivity.Paths;
import graph.algo.vis.Representation;
import graph.algo.vis.annealing.Annealer;
import graph.algo.vis.annealing.Fitness;
import graph.algo.vis.annealing.Layout;
import graph.algo.vis.forcedirected.ForceDirection;
import graph.algo.vis.opengl.GraphDrawer;
import graph.matrix.IncidenceMatrix;

class Demo {
	public static void main(String[] args) {
		File input = new File("tree82inc.txt");
		//File input = new File("supersimple.txt");
		/* store graph within an encapsulating representation object */
		Representation rep = new Representation(new Graph(IncidenceMatrix.parseAsciiMatrix(input)), 2);

		/*
		for (int i=0; i<rep.getGraph().vertexCount(); i++) {
			System.out.println(Dijkstra.dijkstra(rep.getGraph(), i));
		}
		*/
		Layout.circle.accept(rep);
		//GraphDrawer g = GraphDrawer.getInstance(rep);

		System.out.println(rep.getGraph().getAMat());
		/*
		for (int i=0; i<rep.getGraph().vertexCount(); i++) {
			System.out.print("Vertex " + i + " has AList " + "[");
			for (Integer j : rep.getGraph().getAList()[i] ) {
				System.out.print(j + ", ");
			}
			System.out.println("]");
		}*/

		Dijkstra d = new Dijkstra();
		for (int i=0; i<rep.getGraph().vertexCount(); i++) {
			System.out.println("Distances starting from node " + i);
			
			Integer[] distances = d.dijkstra(rep.getGraph(), i);
		
			System.out.print("[");
			for (int j=0; j<rep.getGraph().vertexCount(); j++) {
				System.out.print(distances[j] + " ");
			}
			System.out.println("]");
		}
		
/*		Paths p = new Paths();
		
		System.out.println(p.fullDijkstra(rep.getGraph(), 0));
		System.out.println(p.fullDijkstra(rep.getGraph(), 1));
		System.out.println(p.fullDijkstra(rep.getGraph(), 2));
		*/
		//@SuppressWarnings("resource")
		//Scanner s = new Scanner(System.in);
		
		//rep.getOrdering().randomize();
		//Layout.circle.accept(rep);
		//Layout.square.accept(rep);
		//GraphDrawer g = GraphDrawer.getInstance(rep);
		//s.nextLine();
		/* Simulated annealing demos */
		/* circle + edge length */
		//rep = Annealer.anneal(rep, Fitness.edgeLengthSum, Fitness.permute_random, Layout.circle, rep.getGraph().vertexCount(), 0.001, true);
		/* circle + #intersections */
		//rep = Annealer.anneal(rep, Fitness.fit_EdgeCrossings, Fitness.mutate, Layout.circle, rep.getGraph().vertexCount(), 0.0002, true);
		/* square + edge length */
		//rep = Annealer.anneal(rep, Fitness.edgeLengthSum, Fitness.permute_random, Layout.square, rep.getGraph().vertexCount(), 0.001, true);
		/* square + #intersections */
		//rep = Annealer.anneal(rep, Fitness.fit_EdgeCrossings, Fitness.mutate, Layout.square, rep.getGraph().vertexCount(), 0.0002, true);
		/* Force-direction demos */
		/* bad annealer to speed up FD a little bit */
		//rep = Annealer.anneal(rep, Fitness.edgeLengthSum, Fitness.permute_random, Layout.square, rep.getGraph().vertexCount(), 0.2, true);
		//System.out.println("done annealing / press enter to continue to FD");
		//g.updateData(rep);
		//s.nextLine();
		//ForceDirection.forceDirection(rep, 0.03, 0.002, 0.005, true);
		
		
	}
}