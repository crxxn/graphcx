package graph.algo.connectivity;

import java.util.ArrayList;

import graph.Graph;

public class Dijkstra {

	/**
	 * Dijkstra algorithm implementation
	 * @param g - Graph to run on
	 * @param startVertex - Vertex with distance 0
	 * @return Integer[] with distances to all vertices
	 */
	public Integer[] dijkstra(Graph g, int startVertex) {
		
		Integer distance[] = new Integer[g.vertexCount()];
		Integer predecessor[] = new Integer[g.vertexCount()];
		ArrayList<Integer> queue = new ArrayList<Integer>();
		for (int i=0; i<g.vertexCount(); i++) {
			distance[i] = -1;
			predecessor[i] = -1;
			queue.add(i);
		}
		distance[startVertex] = 0;
		
		while(!queue.isEmpty()) {
			
			int u = minDistance(queue, distance);//u has minimal distance to the known part of g
			
			int vertex = queue.get(u);
			queue.remove(u);
			for (Integer v : g.getAList()[vertex]) { //for each neighbour v of u...
			
				
				if (queue.contains(v)) {
					distance = updateDistance(vertex, v, distance, predecessor);
				}
			}
			
			
		}
		return distance;
	}

	/**
	 * @param queue - vertices not already visited in dijkstra
	 * @param distance - 
	 * @return index of queue entry with minimal distance
	 */
	private static int minDistance(ArrayList<Integer> queue, Integer[] distance) {

		int i=0;
		while(distance[queue.get(i)] == -1)
			i++; //skip infinity placeholders

		int minIndex = i;

		for (; i<queue.size(); i++) {
			if (distance[queue.get(i)] == -1 )
				continue; 	//skip place holder elements
			else if (distance[queue.get(i)] < distance[minIndex])
				minIndex = i;
		}
		return minIndex;
	}

	/* update distance to v if a path through u is faster than any previously found path */
	private static Integer[] updateDistance(int u, int v, Integer[] distance, Integer[] predecessor) {
		//FIXME: assuming all distances are 1. Introduce weighted graphs some day...
		int new_distance = distance[u] + 1;
		if (new_distance < distance[v] || distance[v] == -1) {
			distance[v] = new_distance;
			predecessor[v] = u;
		}
		return distance;
	}
}
