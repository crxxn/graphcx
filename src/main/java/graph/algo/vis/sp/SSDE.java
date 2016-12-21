package graph.algo.vis.sp;

import java.util.ArrayList;
import java.util.Random;

import graph.Graph;
import graph.algo.connectivity.Dijkstra;
import graph.algo.vis.Representation;

public class SSDE {
	
	
	public void ssde ( Representation rep, int samples, boolean greedy) {
		
		if (samples > rep.getGraph().vertexCount())
			samples = rep.getGraph().vertexCount(); //limit #sampling to vertex count
		
		ArrayList<Integer> sampleValues = new ArrayList<Integer>();
		
		Random rng = new Random(System.currentTimeMillis()); //seeded rng
		if (greedy) { // use maximum-distance greedy sampling
			int s = rng.nextInt(rep.getGraph().vertexCount()); //randomly choosen start node
			
			for (int i=0; i<samples; i++) {
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
		
	}

	public int furthestVertex(Graph g, ArrayList<Integer> sampleValues) {
		Dijkstra d = new Dijkstra();
		ArrayList<Integer[]> distances = new ArrayList<Integer[]>();
		
		for(Integer i : sampleValues) {
			distances.add(d.dijkstra(g, i));
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
		return result;
	}
	
}
