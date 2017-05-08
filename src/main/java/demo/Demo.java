package demo;
import java.io.File;
import java.util.Scanner;

import org.la4j.Matrix;
import org.la4j.Vector;

import graph.*;
import graph.algo.connectivity.Dijkstra;
//import graph.algo.connectivity.Paths;
import graph.algo.vis.Representation;
import graph.algo.vis.annealing.Annealer;
import graph.algo.vis.annealing.Fitness;
import graph.algo.vis.annealing.Layout;
import graph.algo.vis.forcedirected.FruchtermanReingold;
import graph.algo.vis.forcedirected.Galaxy;
import graph.algo.vis.opengl.GraphDrawer;
import graph.algo.vis.spectral.SSDE;
import graph.matrix.IncidenceMatrix;

class Demo {
	public static void main(String[] args) {
		//File input = new File("input.txt");
		//File input = new File("supersimple.txt");
		File input = new File("walther_incidence_matrix.txt");
		/* store graph within an encapsulating representation object */
		Representation rep = new Representation(new Graph(IncidenceMatrix.parseAsciiMatrix(input, false)), 2);
		
		
		
		/*
		float[][] coords = new float[4][2];
		coords[0][0] = 0.0f;
		coords[0][1] = 0.0f;
		rep.setLayout(0, coords[0]);
		
		coords[1][0] = 1.0f;
		coords[1][1] = 1.0f;
		rep.setLayout(1, coords[1]);
		
		coords[2][0] = -1.0f;
		coords[2][1] = -1.0f;
		rep.setLayout(2, coords[2]);

		coords[3][0] = 1.0f;
		coords[3][1] = -1.0f;
		rep.setLayout(3, coords[3]);
		
		g.updateData(rep);
		*/
		
		
		SSDE ssde = new SSDE();
		ssde.ssde(rep, 50, true, 2);
		
		GraphDrawer g = GraphDrawer.getInstance(rep);
		rep.normalizeLayout(1.75f);
		
		rep.perturbateLayout(0.05);
		g.updateData(rep);
	/*	
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	*/


		/* Simulated annealing demos */
		/* circle + edge length */
		//rep = Annealer.anneal(rep, Fitness.edgeLengthSum, Fitness.permute_random, Layout.circle, rep.getGraph().vertexCount(), 0.001, true);
		/* circle + #intersections */
		//rep = Annealer.anneal(rep, Fitness.fit_EdgeCrossings, Fitness.mutate, Layout.circle, rep.getGraph().vertexCount(), 0.0002, true);
		/* square + edge length */
		//rep = Annealer.anneal(rep, Fitness.edgeLengthSum, Fitness.permute_random, Layout.square, rep.getGraph().vertexCount(), 0.000001, true);
		/* square + #intersections */
		//rep = Annealer.anneal(rep, Fitness.fit_EdgeCrossings, Fitness.mutate, Layout.square, rep.getGraph().vertexCount(), 0.0002, true);
		/* Force-direction demos */
		/* bad annealer to speed up FD a little bit */
		//rep = Annealer.anneal(rep, Fitness.edgeLengthSum, Fitness.permute_random, Layout.square, rep.getGraph().vertexCount(), 0.2, true);
		//System.out.println("done annealing / press enter to continue to FD");
		//g.updateData(rep);
		//s.nextLine();
		
		
		Galaxy galaxy = new Galaxy();
		galaxy.galaxy(rep, 4, 1, 1000, true);
		rep.normalizeLayout(1.5f);

		/*
		FruchtermanReingold fr = new FruchtermanReingold();
		fr.fruchtermanReingold(rep, 20000, true);
		rep.normalizeLayout(1.0f);
		System.out.println("done!");
		*/
		
	}
}