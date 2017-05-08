package graph.matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class IncidenceMatrix extends GraphMatrix {

	private IncidenceMatrix(int nodes, int edges){
			super(nodes, edges);
	}
	
	/* parse incidence matrix from the given input file */
	public static IncidenceMatrix parseAsciiMatrix(File file, boolean transposed) {
		ArrayList<ArrayList<Character>> data = new ArrayList<ArrayList<Character>>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			for(String line; (line = br.readLine()) != null; ) {
				/* temporarily store data in an ArrayList */
				ArrayList<Character> tmp = new ArrayList<Character>();
				for (int k = 0; k < line.length(); k++) {
					if (line.charAt(k) == ' ')
							continue; //ignore whitespace
					if (line.charAt(k) == '0') {
						tmp.add('0');
					} else if (line.charAt(k) == '1') {
						tmp.add('1');
					} else {
						System.err.println("Error: Illegal character");
						System.exit(1);
					}
				}
				data.add(tmp);
			}
		} catch (Exception e) {
				System.err.println("Exception: " + e.getMessage());
				System.exit(1);
		}	
		
		if (transposed) {
			ArrayList<ArrayList<Character>> t = new ArrayList<ArrayList<Character>>();
			//FIXME
		}

		/* Done parsing, perform sanity check */		
		sanityCheck(data);

		int nodes = data.size();
		int edges = data.get(0).size();

		IncidenceMatrix result = new IncidenceMatrix(nodes, edges);
		/* copy data in memory efficient byte matrix */
		
		
		for (int k=0; k<nodes; k++) {
			for (int l=0; l<edges; l++){
				/* just set the 1's, as the array is initialized with zeros */
				if (data.get(k).get(l) == '1') {
					result.set(k,l,1);
				} 
			}
		}

		return result;
	}

	/* check the consistency of the parsed matrix */
	private static void sanityCheck(ArrayList<ArrayList<Character>> parsedata) {
		/* check that all lists have the same size */
		int s = parsedata.get(0).size();
		for (ArrayList<Character> line : parsedata) {
			if (line.size() != s) {
				System.err.println("Error: Inconsistent line size");
				System.exit(1);
			}
		}		
		/* check that every edge has exactly two incident nodes */
		for (int k = 0; k < s; k++) {
			int ones = 0;
			for (ArrayList<Character> line : parsedata) {
				if (line.get(k) == '1')
					ones++; 
			}	

			if (ones != 2) {
				System.err.println("Error: Mal-formed edge");
				System.exit(1);
			}
		}
	}
}