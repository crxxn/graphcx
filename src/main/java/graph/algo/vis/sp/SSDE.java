package graph.algo.vis.sp;

import java.util.ArrayList;
import java.util.Random;

import graph.Graph;
import graph.algo.connectivity.Dijkstra;
import graph.algo.connectivity.ShortestPath;
import graph.algo.vis.Representation;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.matrix.DenseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;

public class SSDE {
	
	
	public void ssde ( Representation rep, int samples, boolean greedy) {
		
		/* step 1, sampling */
		
		if (samples > rep.getGraph().vertexCount())
			samples = rep.getGraph().vertexCount(); //limit samples to vertex count
		
		ArrayList<Integer> sampleValues = new ArrayList<Integer>();
		
		Random rng = new Random(System.currentTimeMillis()); //seeded rng
		
		if (greedy) { // use maximum-distance greedy sampling
			//FIXME
			//int s = rng.nextInt(rep.getGraph().vertexCount()); //randomly choosen start node
			int s = 0;
			sampleValues.add(s);
			
			for (int i=1; i<samples; i++) { //we already have a random initial vertex, so start by 1
				sampleValues.add(furthestVertex(rep.getGraph(), sampleValues));
			}

		} else { //use random sampling
			for (int i=0; i<samples; i++) {

				int s = rng.nextInt(rep.getGraph().vertexCount());
				while(sampleValues.contains(s)) { 
					 //this may be not the most beautiful collision prevention technique
					 //but it's sufficient since in practice samples << #nodes
					s = rng.nextInt(rep.getGraph().vertexCount());
				}
				sampleValues.add(s);
			}
		}
		
		
		/* step 2, computing the Moore Penrose pseudo inverse of the sampled square distance matrix */
		
		/*
		System.out.print("Sample values: [");
		for (int i = 0; i<sampleValues.size(); i++) {
			System.out.print(sampleValues.get(i) + ", ");
		}
		System.out.println("]");
		*/
		/*
		 * C is a #vertices x samples matrix with distance entries
		 */
		DenseMatrix C = new Basic2DMatrix(rep.getGraph().vertexCount(), samples);
		ShortestPath sp = new Dijkstra();
	
		
		for (int i=0; i<samples; i++) {
			Integer[] distances = sp.shortestPaths(rep.getGraph(), sampleValues.get(i));
			
			for (int j=0; j<rep.getGraph().vertexCount(); j++) {
				C.set(j, i, distances[j]);
			}
		}
		
		//System.out.println(C);
		
		/*
		 * phi is a samples x samples matrix
		 */
		DenseMatrix phi = new Basic2DMatrix(samples, samples);

		for (int k=0; k<samples; k++) {
			for (int j=0; j<samples; j++) {
				phi.set(k, j,  C.get(sampleValues.get(k), j) );
			}
		}
		
		SingularValueDecompositor dc = new SingularValueDecompositor(phi);
		Matrix[] svd = dc.decompose();
		
		
		//Regularization
		int n = Math.min(svd[1].rows(), svd[1].columns());
		
		Double alpha = svd[1].get(0, 0);
		for (int i=1; i<n && svd[1].get(i, i) != 0; i++) {
			if (svd[1].get(i, i) > alpha)
				alpha = svd[1].get(i, i); //find biggest singular value
		}
		
		alpha = Math.pow(alpha, 3);
		
		/*
		System.out.println(svd[0] + "\n");
		System.out.println(svd[1] + "\n");
		System.out.println(svd[2] + "\n");
		*/
		
		//System.out.println("alpha: " + alpha);

		for (int i=0; i<n && svd[1].get(i, i) != 0; i++) {
			
			Double sigma = svd[1].get(i, i);
			Double regularReciprocal = sigma / (Math.pow(sigma,  2) + alpha /Math.pow(sigma, 2));
			svd[1].set(i, i, regularReciprocal);
		}
		
		//System.out.println(svd[1] + "\n");
		
		Matrix phiInverse = svd[2].transpose().multiply(svd[1]).multiply(svd[0].transpose());
		System.out.println(C);
		System.out.println(phiInverse);
		Vector[] coordinates = PowerIteration2D(C, phiInverse, Math.pow(10, -7), rep.getGraph().vertexCount());
		
		//System.out.println(coordinates[0].length());
		
		for(int i=0; i<rep.getGraph().vertexCount(); i++) {
			
			double[] c = {coordinates[0].get(i), coordinates[1].get(i)};
			//System.out.println("i: " + i + " " + c[0] + " " + c[1]);
			rep.setLayout(i, c);
		}
	}

	public Vector[] PowerIteration2D(Matrix C, Matrix PhiInverse, Double epsilon, int vertices) {
		Double current = epsilon;
		Double lambda1, lambda2;
		Double prev;
		Random r = new Random(System.currentTimeMillis());

		Vector y1 = Vector.random(vertices, r), y2 = Vector.random(vertices, r);
		Vector u1, u2;

		//Matrix p = Matrix.identity(vertices).multiply(1/vertices).subtract(Matrix.constant(vertices, vertices, 1));
		//System.out.println(p);
		//Matrix M = p.multiply(C).multiply(PhiInverse).multiply(C.transpose()).multiply(p).multiply(-0.5);
		
		Matrix M = C.multiply(PhiInverse).multiply(C.transpose());
		System.out.println(M);

		y1 = y1.divide(y1.norm());

		do {
			prev = current;
			u1 = y1;

			//System.out.println(y1);
			y1 = M.multiply(u1);

			lambda1 = u1.innerProduct(y1);
			y1 = y1.multiply(1/y1.norm());
			current = u1.innerProduct(y1);
			
		} while (Math.abs(current/prev) > 1+epsilon);
		
		current = epsilon;
		y2 = y2.divide(y2.norm());
		do {
			prev = current;
			u2 = y2;
			u2 = u2.subtract(u1.multiply(u1.innerProduct(u2)/u1.innerProduct(u1)));
			y2 = M.multiply(u2);
			
			lambda2 = u2.innerProduct(y2);

			y2 = y2.divide(y2.norm());
			current = u2.innerProduct(y2);

		} while (Math.abs(current/prev) > 1+epsilon);

		
		Vector[] result = new Vector[2];
		System.out.println("lambda1: " + lambda1 + " lambda2: " + lambda2);
		/*
		System.out.println("y1.length: " + y1.length());
		System.out.println("sqrt(lambda1)" + Math.sqrt(lambda1));
		System.out.println(y1.multiply(Math.sqrt(lambda1)));
		System.out.println("####");
		System.out.println("y2.length: " + y2.length());
		System.out.println("sqrt(lambda2)" + Math.sqrt(lambda2));
		System.out.println(y2);
		
		System.out.println(y1.get(0));
		System.out.println(y1.get(1));

		System.out.println(y2.get(0));
		System.out.println(y2.get(1));
		 */

		result[0] = y1;
		result[1] = y2;
			
		System.out.println("result:" + result[0]);
		System.out.println("result:" + result[1]);
		return result;
	}
	
	
	
	private int furthestVertex(Graph g, ArrayList<Integer> sampleValues) {
		//System.out.println("entering furthestVertex()");
		Dijkstra d = new Dijkstra();
		ArrayList<Integer[]> distances = new ArrayList<Integer[]>();
		//System.out.println("sampleValues.size(): " + sampleValues.size());
		
		for(Integer i : sampleValues) {
			distances.add(d.shortestPaths(g, i));
		}
		
		int result=0; //index of the currently furthest node from sampleValues
		int minimumDistance = 0;

		int tmpDistance;
		for (int i=0; i<g.vertexCount(); i++) {
			tmpDistance = distances.get(0)[i];
			
			for (int j=1; j<sampleValues.size(); j++) {
				if (distances.get(j)[i] < tmpDistance) {
					tmpDistance = distances.get(j)[i];
				}
			}
			if (tmpDistance > minimumDistance) {
				minimumDistance = tmpDistance;
				result = i;
			}
		}
		//System.out.println("Choose node " + result + " with distance " + minimumDistance);
		return result;
	}
	
}
