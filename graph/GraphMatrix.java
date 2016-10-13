package graph;

import java.io.File;
import java.io.PrintWriter;

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