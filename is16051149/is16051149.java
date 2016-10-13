package is16051149;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import graph.*;
import graph.circlefit.GraphRepresentation;

class is16051149 {
	public static void main(String[] args) {
	
		
		
		File input = new File("input.txt");
		/* parse an incidence matrix from the given input file */
		IncidenceMatrix inc= IncidenceMatrix.parseAsciiMatrix(input);
	
		/* just to check diffs - had a moment to think about 
		 * here because of DOS vs UNIX line endings... */
		//File diff = new File("diff.txt");
		//inc.saveAsciiMatrix(diff);
		
		AdjacencyMatrix ad = AdjacencyMatrix.convertIncidenceMatrix(inc);

		/* write adjacency matrix in ascii representation to output.txt */
		File output = new File("AI16.txt");
		ad.saveAsciiMatrix(output);
	
		/* store the adjacency matrix together with a permutation */
		GraphRepresentation rep = new GraphRepresentation(ad);
		/* randomize the ordering */
		rep.ord.randomize();
	
		/* write initial ordering in the same output file */
		try {
			FileWriter fw = new FileWriter(output, true);
			fw.write("\n" + rep.ord.toString() + "\n");
			fw.close();
		} catch (IOException e) {
			System.err.println("Error writing to output file");
			System.exit(1);
		}
		
		rep.simulated_annealing(rep.ord.n, 0.001, 0, true);
	
		
		rep.draw(); //actually not necessary if we call simulated_annealing with draw_progress=true
		
	}
}














