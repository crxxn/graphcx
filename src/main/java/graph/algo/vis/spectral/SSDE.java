package graph.algo.vis.spectral;

import java.util.ArrayList;
import java.util.Random;

import graph.Graph;
import graph.algo.connectivity.Dijkstra;
import graph.algo.connectivity.ShortestPath;
import graph.algo.vis.Representation;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;

public class SSDE {

	/**
	 * Sampled Spectral Decomposition Embedding
	 *  
	 * @param rep
	 * @param samples
	 * @param greedy
	 */
	public void ssde(Representation rep, int samples, boolean greedy, int dimensions) {

		int vertices = rep.getGraph().vertexCount();
		/* Phase 1: Vertex Sampling */
		// limit #samples to #vertices
		if (samples > vertices)
			samples = vertices;
	
		// store samples, vertices are addressed as integers 
		ArrayList<Integer> sampleValues = new ArrayList<Integer>();
		Random rng = new Random(System.currentTimeMillis());
		
		if (greedy) {
			// maximum-distance greedy sampling
			// random start vertex
			sampleValues.add(rng.nextInt(vertices));
			// always choose the vertex with the biggest distance to the current sample set
			for (int i=1; i<samples; i++)
				sampleValues.add(furthestVertex(rep.getGraph(), sampleValues));

		} else {
			// simple random sampling
			for (int i=0; i<samples; i++) {
				int s = rng.nextInt(vertices);
				// avoid collisions - may be inefficient but note that usually #samples << #vertices
				while (sampleValues.contains(s))
					s = rng.nextInt(vertices);

				sampleValues.add(s);
			}
		}
		
		/* Phase 2: Computing the Moore-Penrose pseudo inverse */
		
		// C is made of column samples of the squared-distances matrix L,
		// where L_{i,j} = D_{i,j}² = Dijkstra(i, j)²
		Matrix C = new Basic2DMatrix(vertices, samples);
		ShortestPath sp = new Dijkstra();
	
		
		for (int i=0; i<samples; i++) {
			Integer[] distances = sp.shortestPaths(rep.getGraph(), sampleValues.get(i));
			for (int j=0; j<vertices; j++)
				C.set(j, i, Math.pow(distances[j], 2));
		}
	
		// phi is the intersection of C on C' on L
		Matrix phi = new Basic2DMatrix(samples, samples);

		for (int k=0; k<samples; k++) {
			for (int j=0; j<samples; j++)
				phi.set(k, j,  C.get(sampleValues.get(k), j) );
		}
		
		SingularValueDecompositor dc = new SingularValueDecompositor(phi);
		Matrix[] svd = dc.decompose();
		
		/* Phase 3: Regularize svd[1] to numerically stabilize the pseudo inverse */
		int diagonalLength = Math.min(svd[1].rows(), svd[1].columns());
		
		Double alpha = svd[1].get(0, 0);
		for (int i=1; i<diagonalLength && svd[1].get(i, i) != 0; i++) {
			if (svd[1].get(i, i) > alpha)
				alpha = svd[1].get(i, i); //find biggest singular value
		}
		
		alpha = Math.pow(alpha, 3);
		
		/* The Moore-Penrose pseudo inverse of a matrix A with singular value decomposed
		 * representation BCE is given as E'*pinv(C)*B', where pinv(C) is the diagonal matrix
		 * given by the svd where all non-zero elements C_ij are the reciprocal 1/C_ij.
		 * The regularization is given as σ <- σ/(σ²+ alpha/σ²) */
		for (int i=0; i<diagonalLength && svd[1].get(i, i) != 0; i++) {
			Double sigma = svd[1].get(i, i);
			Double regularReciprocal = sigma / (Math.pow(sigma,  2) + alpha /Math.pow(sigma, 2));
			svd[1].set(i, i, regularReciprocal);
		}
		
		// MP pseudo inverse
		Matrix phiInverse = svd[2].transpose().multiply(svd[1]).multiply(svd[0].transpose());

		// see PowerIteration
		Vector[] coordinates = PowerIteration(C, phiInverse, Math.pow(10, -7), rep.getGraph().vertexCount(), dimensions);
		
		// set coordinates as representation layout
		for(int i=0; i<vertices; i++) {
			float [] c = {(float) coordinates[0].get(i), (float) coordinates[1].get(i)};
			rep.setLayout(i, c);
		}
	}

	/**
	 * PowerInteration - calculate the d absolutely biggest eigenvalues and corresponding eigenvectors
	 * of g*C*phiInverse*C'*g to approximate a square-distance matrix L, where Lij = Dij² = dijkstra(i, j).
	 * @param C
	 * @param phiInverse
	 * @param epsilon
	 * @param vertices
	 * @param dimensions
	 * @return
	 */
	public Vector[] PowerIteration(Matrix C, Matrix phiInverse, Double epsilon, int vertices, int dimensions) {
		Random rng = new Random(System.currentTimeMillis());

		Double prev;
		Double[] lambda = new Double[dimensions];	//eigenvalues
		Vector[] y = new Vector[dimensions];		//eigenvectors
		Vector[] u = new Vector[dimensions];		//temporary vectors
	
		// initialize eigenvectors randomly
		for (int i=0; i<dimensions; i++)
			y[i] = Vector.random(vertices, rng);
		
		// projection matrix
		Matrix g = Matrix.identity(vertices).subtract(Matrix.constant(vertices, vertices, 1/vertices));
		Matrix M = C.multiply(phiInverse).multiply(C.transpose());
		Matrix L = g.multiply(M).multiply(g).multiply(-0.5);

		
		for (int i=0; i<dimensions; i++) {
			Double current = epsilon;
			do {
				prev = current;
				u[i] = y[i];
				Vector ortho = Vector.constant(vertices, 0.0);

				// no need to divide by <u[k], u[k]>, as all previous vectors have unit length
				for (int k=0; k<i; k++)
					ortho = ortho.subtract(u[k].multiply(u[k].innerProduct(u[i])));

				//orthogonalize u[i] against u[0],...,u[k]
				u[i] = u[i].subtract(ortho);
				y[i] = L.multiply(u[i]);
				lambda[i] = u[i].innerProduct(y[i]);
				y[i] = y[i].divide(y[i].norm());
				current = u[i].innerProduct(y[i]);
			} while (Math.abs(current/prev) > 1+epsilon);
		}

		for (int i=0; i<dimensions; i++)
			y[i] = y[i].divide(Math.sqrt(Math.abs(lambda[i])));
		
		return y;
	}

	/**
	 * @param g Graph on which this algorithm is run.
	 * @param sampleValues already chosen vertices.
	 * @return a vertex with the highest graphtheoretical distance to the current sampleValues.
	 */
	private int furthestVertex(Graph g, ArrayList<Integer> sampleValues) {
		Dijkstra d = new Dijkstra();
		ArrayList<Integer[]> distances = new ArrayList<Integer[]>();
		
		for (Integer i : sampleValues)
			distances.add(d.shortestPaths(g, i));
		
		int result=0; //index of the currently furthest node from sampleValues
		int minimumDistance = 0;

		int tmpDistance;
		for (int i=0; i<g.vertexCount(); i++) {
			tmpDistance = distances.get(0)[i];
			
			for (int j=1; j<sampleValues.size(); j++) {
				if (distances.get(j)[i] < tmpDistance)
					tmpDistance = distances.get(j)[i];
			}
			if (tmpDistance > minimumDistance) {
				minimumDistance = tmpDistance;
				result = i;
			}
		}
		return result;
	}
}
