package graph.circlefit;

import java.util.Random;

public class Ordering {
	private static Random rg = new Random(System.currentTimeMillis());
	public int[] data;
	public int n; /* size of the data array */


	/* initialise an ascending n-ordering */
	public Ordering(int n) {

		this.n = n;
		data = new int[n];
		for (int k=0; k<n; k++) {
			data[k] = k;
		}
	}

	/* copy constructor */
	public Ordering(Ordering o) {

		this.data = new int[o.n];
		/* the next line was missing and caused a serious bug */
		System.arraycopy(o.data, 0, this.data, 0, o.n);
		this.n = o.n;
	}

	public void randomize() {
		/* this algo is known as fisher-yates shuffle */ 
		for (int k=n-1; k>0; k--) {
			int l = rg.nextInt(k+1); /* generate a random integer in [0,k] */
			int tmp = data[k];
			data[k] = data[l];
			data[l] = tmp;
		}
	}
	
	public void mutate() {
		int a = rg.nextInt(n);
		int b = rg.nextInt(n);
		int tmp = data[a];
		data[a] = data[b];
		data[b] = tmp;
	}
	
	public String toString() {
		String s = "[";
		for(int n : data) {
			s = s + String.valueOf(n) + ",";
		}
		return s + "]";
	}
	
	
}