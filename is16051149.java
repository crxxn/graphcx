import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Exception;
import java.lang.Math;

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
	
		
		
		
		
	}
}


class GraphRepresentation {

	/* just keep them public to be able to manipulate them more directly */
	public AdjacencyMatrix mat;
	public Ordering ord;
	
	public GraphRepresentation(AdjacencyMatrix mat) {
		this.mat = mat;
		this.ord = new Ordering(mat.getColumns());
	}
	
	public void draw() {
		
	}
	
	
}

class Ordering {
	
	int[] data;
	int n; /* size of the data array */

	/* initialise an ascending n-ordering */
	public Ordering(int n) {
		this.n = n;
		data = new int[n];
		for (int k=0; k<n; k++) {
			data[k] = k;
		}
	}

	public void randomize() {
		/* seed rng with current time */
		Random rg = new Random(System.currentTimeMillis());
		
		/* this algo is known as fisher-yates shuffle */ 
		for (int k=n-1; k>0; k--) {
			int l = rg.nextInt(k+1); /* generate a random integer in [0,k] */
			int tmp = data[k];
			data[k] = data[l];
			data[l] = tmp;
		}
	}
	
	public String toString() {
		String s = "[";
		for(int n : data) {
			s = s + String.valueOf(n) + ",";
		}
		return s + "]";
	}
	
	
}



/* General GraphMatrix implementation, provides a quite memory-efficient internal
 * representation of BINARY graph relations, thus not allowing any multi- or hyperedges! */
class GraphMatrix {
	
	private byte[][] mat;
	private int m; /* lines */
	private int n; /* columns */

	public GraphMatrix(int m, int n) {
		/* 
		 * To store a m x n matrix of binary graph information,
		 * we only need an [m][ceil(n/8)]-array of bytes -
		 * this will save quite some memory 
		 */
		mat = new byte[m][(int)Math.ceil((double)n/8)];
		this.m = m;
		this.n = n;
	}

	public int getLines() {
		return m;
	}

	public int getColumns() {
		return n;
	}
	
	
	/* get value at index m,n */
	public short get(int m, int n) {
		/* select the n%8th bit from the n/8th byte in the mth line */
		return (short)(mat[m][n/8] >> n%8 & 1);
	}

	/* set value at index m,n */
	public void set(int m, int n, int val) {
		if (val == 1) {
			mat[m][n/8] =  (byte)(mat[m][n/8] |  1 << (n%8) );
		} else if (val == 0) {
			mat[m][n/8] =  (byte)(mat[m][n/8] &  ~(1 << (n%8)) );
		} else {
			System.err.println("Error: Trying to store illegal value in matrix");
			System.exit(1);
		}
	}

	/* save matrix as ascii file */
	public void saveAsciiMatrix(File file) {
		try (PrintWriter pw = new PrintWriter(file)) {

			for (int k=0; k<m; k++) {
				String s = "";
				for (int l=0; l<n; l++) {
					if (get(k, l)==1) {
						s = s + "1 ";
					} else {
						s = s + "0 ";
					}
				}
				pw.println(s);
			}
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
		}
	}

}

class AdjacencyMatrix extends GraphMatrix {
	private AdjacencyMatrix(int nodes) {
		super(nodes, nodes);
	}
	
	public static AdjacencyMatrix convertIncidenceMatrix(IncidenceMatrix i)	{
		
		int nodes = i.getLines();
		int edges = i.getColumns();

		AdjacencyMatrix result = new AdjacencyMatrix(nodes);
	
		for (int n = 0; n<edges; n++) {
			/* store indices of incident nodes */
			int a = -1;
			int b = -1;
			boolean first = true;
			
			for(int m=0; m<nodes; m++) {
				if(i.get(m,n)==1 && first) {
					a = m; /* index of first node encountered on edge n */
					first = false;
				} else if (i.get(m,n)==1) {
					b = m; /* index of second node encountered on edge n */
				}
			}
			
			if (a == -1 || b == -1 || a == b) {
				System.err.println("Error: Constrcting Adjacency Matrix from malformed Incidence Matrix!");
				System.exit(1);
			}
			
			result.set(a, b, 1); /* the matrix shall be symmetric */
			result.set(b, a, 1);
		}
		return result;
	}
	
}

class IncidenceMatrix extends GraphMatrix {

	private IncidenceMatrix(int nodes, int edges){
			super(nodes, edges);
	}


	/* parse incidence matrix from the given input file */
	public static IncidenceMatrix parseAsciiMatrix(File file) {
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

