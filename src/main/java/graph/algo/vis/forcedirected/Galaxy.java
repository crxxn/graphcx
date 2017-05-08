package graph.algo.vis.forcedirected;

import graph.algo.vis.Representation;
import graph.algo.vis.opengl.GraphDrawer;
import graph.matrix.AdjacencyMatrix;

public class Galaxy {

	/**
	 *
	 * @param rep
	 * @param edgeAttraction
	 * @param vortexRepulsion
	 * @param iterations
	 * @param draw
	 */
	public void galaxy(	Representation rep,
						double edgeAttraction,
						double vortexRepulsion,
						int iterations,
						boolean draw
						) {

		AdjacencyMatrix aMat = rep.getGraph().getAMat();
		int vertices = rep.getGraph().vertexCount();

		for (int k=0; k<iterations; k++) {

			float[][] forces = new float[vertices][rep.getDimensions()];
			
			float cooling = (float)(iterations-k)/(float)iterations;
			/* calculate forces on every vertex */
			for (int i=0; i<vertices; i++) {
				for (int j=0; j<vertices; j++) {
					
					if (i==j)
						continue;

					// vortex repulsion
					for (int d=0; d<rep.getDimensions(); d++)
						forces[i][d] +=  cooling * vortexRepulsion * (rep.getLayout(i, d) - rep.getLayout(j, d)) / rep.distance(i, j);

					// edge attraction
					if (aMat.get(i, j) == 1) {
						for(int d=0; d<rep.getDimensions(); d++)
							forces[i][d] += cooling * edgeAttraction * (rep.getLayout(j, d) - rep.getLayout(i, d));
					}
				}
			}


			/* apply forces */

			for (int i=0; i<vertices; i++) {
				float[] coordinates = new float[rep.getDimensions()];

				for (int d=0; d<rep.getDimensions(); d++)
					coordinates[d] = rep.getLayout(i, d) + forces[i][d];

				rep.setLayout(i, coordinates);
			}
	
			rep.normalizeLayout(1.5f);

			if(draw) {
				GraphDrawer gd = GraphDrawer.getInstance(rep);
				gd.updateData(rep);
			}
		}
	} 
	
}