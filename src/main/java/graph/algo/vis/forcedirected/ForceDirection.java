package graph.algo.vis.forcedirected;

import graph.algo.vis.Representation;
import graph.algo.vis.opengl.GraphDrawer;
import graph.matrix.AdjacencyMatrix;

public class ForceDirection {
	
	public static void forceDirection(	Representation rep,
										double edgeAttraction,
										double vortexRepulsion,
										double averageTreshold,
										boolean draw
						) {

		double average = 0;
		AdjacencyMatrix aMat = rep.getGraph().getAMat();
		
		//FIXME remove 

		do {
			double[][] forces = new double[rep.getGraph().vertexCount()][rep.getDimensions()];
			average = 0;
			
			/* calculate forces on every vertex */
			for (int i=0; i<rep.getGraph().vertexCount(); i++) {
				for (int j=0; j<rep.getGraph().vertexCount(); j++) {
					
					if (i==j)
						continue;

					//System.out.println("calculating forces for "+ i + "," + j);
					for (int d=0; d<rep.getDimensions(); d++) {
						/* pay attention to the += */
						forces[i][d] +=  - vortexRepulsion * (rep.getLayout(j, d) - rep.getLayout(i, d))/rep.distance(i, j);
					}
					
					/* edge attraction */
					if (aMat.get(i, j) == 1) {
						for(int d=0; d<rep.getDimensions(); d++) {
							/* pay attention to the += */
							forces[i][d] +=  edgeAttraction * (rep.getLayout(j, d) - rep.getLayout(i, d));
							
						}
					}
				}
			}


			/* apply forces */
			for (int i=0; i<rep.getGraph().vertexCount(); i++) {
				double[] coordinates = new double[rep.getDimensions()];
				for (int d=0; d<rep.getDimensions(); d++) {
					coordinates[d] = rep.getLayout(i, d) + forces[i][d];
				}
				//System.out.println("i-forces: (" + forces[i][0] + "," + forces[i][1] + ")");
				rep.setLayout(i, coordinates);
			}
			
			/* calculate average force */
			for (int i=0; i<rep.getGraph().vertexCount(); i++) {
				double tmp = 0;
				for (int d=0; d<rep.getDimensions(); d++) {
					tmp = tmp + Math.pow(forces[i][d], 2);
				}
				average = average + Math.sqrt(tmp);
			}
			average = average / rep.getGraph().vertexCount();
			
			rep.normalizeLayout(1.5);

			if(draw) {
				GraphDrawer gd = GraphDrawer.getInstance(rep);
				gd.updateData(rep);
			}
			
			System.out.println("average force: " + average);
			
		} while(average > averageTreshold );
	} 
	
}