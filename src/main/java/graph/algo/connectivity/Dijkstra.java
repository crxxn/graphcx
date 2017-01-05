package graph.algo.connectivity;
import java.util.ArrayList;
import graph.Graph;

/**
 * @author Oliver Wiedemann
 */
public class Dijkstra implements ShortestPath {

	/* see interface comments */
	public Integer shortestPath(Graph g, int startVertex, int goalVertex) {
		return dijkstra(g, startVertex, true, goalVertex)[goalVertex];
	}
	
	public Integer[] shortestPaths(Graph g, int startVertex) {
		return dijkstra(g, startVertex, false, 0);
	}

	
	/**
	 * Dijkstras shortest path algorithm
	 * @param g - Graph to run on
	 * @param startVertex - Vertex with distance 0
	 * @return Integer[] with distances to all vertices
	 */
	public Integer[] dijkstra(Graph g, int startVertex, boolean returnEarly, int goalVertex) {
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
			int u = minDistance(queue, distance);
			int vertex = queue.get(u);
		
			// quitEarly in case we're only interested in a specific shortest path
			if (returnEarly && u == goalVertex)
				return distance;

			queue.remove(u);
			for (Integer v : g.getAList()[vertex]) {
				if (queue.contains(v))
					updateDistance(vertex, v, distance, predecessor);
			}
		}
		return distance;
	}

	
	/**
	 * @param queue vertices not already visited in dijkstra
	 * @param distance array of so-far minimal distances, -1 == infinity
	 * @return index of QUEUE entry [NOT vertex index!] with minimal distance
	 */
	private static int minDistance(ArrayList<Integer> queue, Integer[] distance) {
		int i=0;
		// skip infinity placeholders
		while(distance[queue.get(i)] == -1)
			i++;

		int minIndex = i;
		for (; i<queue.size(); i++) {
			if (distance[queue.get(i)] == -1 )
				continue;
			else if (distance[queue.get(i)] < distance[minIndex])
				minIndex = i;
		}
		return minIndex; //queue, NOT vertex index!
	}

	/**
	 * Update shortest path in case a shorter one was found from startVertex via u to v
	 * @param u predecessor vertex
	 * @param v target vertex, gets it's distance and predecessor updated
	 * @param distance
	 * @param predecessor
	 * @return updated distance 
	 */
	private void updateDistance(int u, int v, Integer[] distance, Integer[] predecessor) {
		//FIXME: assuming all distances are 1. Introduce weighted graphs some day...
		int new_distance = distance[u] + 1;
		if (new_distance < distance[v] || distance[v] == -1) {
			distance[v] = new_distance;
			predecessor[v] = u;
		}
	}
}
