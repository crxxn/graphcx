import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import java.lang.Exception;
import java.lang.Math;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



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
		
		rep.simulated_annealing(rep.ord.n * 2, 0.001, 0, true);
	
		
		rep.draw(); //actually not necessary if we call simulated_annealing with draw_progress=true
		
	}
}


class GraphRepresentation {

	/* just keep them public to be able to manipulate them more directly */
	public AdjacencyMatrix mat;
	public Ordering ord;
	private double chunk;

	public void simulated_annealing(double temperature, double cr, double min_temp, boolean draw_progress) {
		ord.randomize(); /* randomize current ordering */
		if(draw_progress) {
			draw();
		}
		double fit = fitness();
		
		while (temperature > min_temp) {
			Ordering tmp_ord = new Ordering(ord);
			double tmp_fit = fit;
			
			for (int i = 0; i < temperature; i++) {
				tmp_ord.mutate();
			}
			tmp_fit = fitness(tmp_ord);
			
			if(tmp_fit < fit) {
				fit = tmp_fit;
				this.ord = new Ordering(tmp_ord);
				if(draw_progress) {
					draw();
				}
			}
			temperature = temperature-cr;
		}
		
	}
	
	
	public GraphRepresentation(AdjacencyMatrix mat) {
		this.mat = mat;
		this.ord = new Ordering(mat.getColumns());
		chunk = 2 * Math.PI / ord.n;
	}

	
	public double fitness() {
		return fitness(this.ord);
	}

	/* return fitness value for the current representation */
	public double fitness(Ordering ord) {
		double[] x = new double[ord.n];
		double[] y = new double[ord.n];
		
		/* calculate cartesian coordinates for each node of
		 * the current representation */
		for (int k=0; k<ord.n; k++) {
			x[k] = Math.cos(ord.data[k] * chunk);
			y[k] = Math.sin(ord.data[k] * chunk);
		}
		
		/* sum up the distance between adjacent nodes */
		double result = 0;
		/* iterate over the upper triangular matrix */
		for (int k=0; k<ord.n; k++) {
			for (int l=k+1; l<ord.n; l++) {
				if (mat.get(k, l) == 1) {
					/* add the distance between to adjacent nodes to the current fitness result */
					result = result + Math.sqrt( Math.pow(x[k]-x[l],2) + Math.pow(y[k]-y[l],2) );
				}
			}
		}
		return result;
	}
	
	public void draw() {
		GraphDrawer gd = GraphDrawer.getInstance();
		gd.updateData(this);
	}
	
	
}

class Ordering {
	private static Random rg = new Random(System.currentTimeMillis());
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

class GraphDrawer  {

	private static GraphDrawer gd = null;
	final GLProfile glprofile;
	final GLCapabilities glcapabilities;
	final GLCanvas glcanvas;
	GLEventListener gleventlistener;
	final JFrame frame;

	public static GraphDrawer getInstance() {
		if (GraphDrawer.gd == null) {
			return new GraphDrawer();
		} else {
			return gd;
		}
		
	}
	
	private GraphDrawer() {
		glprofile = GLProfile.get(GLProfile.GL2);
		glcapabilities = new GLCapabilities( glprofile );
		glcanvas = new GLCanvas( glcapabilities );
		gleventlistener = new VisEventListener();
		glcanvas.addGLEventListener(gleventlistener);
		frame = new JFrame("Graph Visualization");
		frame.getContentPane().add(glcanvas);
		frame.setSize(frame.getContentPane().getPreferredSize());
		//frame.setPreferredSize(new Dimension(800, 600));
		frame.setResizable(false);
		frame.setMinimumSize(new Dimension(1024, 768));

		frame.setVisible(true);
		//close on exit window event 
		frame.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent windowevent ) {
				frame.remove( glcanvas );
				frame.dispose();
				System.exit(0);
			}
		}); 
		GraphDrawer.gd = this;
		
	}
	
	public void updateData(GraphRepresentation rep) {
		GraphViz.setRenderData(rep);
		glcanvas.display();
	}
	
	
}

/* EventListener for the visualization */
class VisEventListener implements GLEventListener {
	public void display(GLAutoDrawable glautodrawable) {
		GraphViz.render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight());
	}
	public void dispose(GLAutoDrawable arg0) {}
	public void init(GLAutoDrawable arg0) {}
	public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {}
}

class GraphViz {
	
	private static GraphRepresentation rep;

	protected static void setRenderData(GraphRepresentation r) {
		rep = r;
	}

	
	protected static void render( GL2 gl2, int width, int height ) {

		double radius = 0.8;
		double scale = 0.01;
		
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
	
		
		for (int i=0; i<rep.ord.n; i++) {
			for (int j=i+1; j<rep.ord.n; j++) {
				if (rep.mat.get(i, j) == 1) {
					float x1 = (float) (radius * Math.cos(2*Math.PI*rep.ord.data[i]/rep.ord.n));
					float y1 = (float) (radius * Math.sin(2*Math.PI*rep.ord.data[i]/rep.ord.n));
					float x2 = (float) (radius * Math.cos(2*Math.PI*rep.ord.data[j]/rep.ord.n));
					float y2 = (float) (radius * Math.sin(2*Math.PI*rep.ord.data[j]/rep.ord.n));
					drawEdge(gl2, x1, y1, x2, y2);
				}
			}
		}

		for(int i=0; i<rep.ord.n; i++) {
			gl2.glLoadIdentity();
			double x = radius * Math.cos(2*Math.PI*rep.ord.data[i]/rep.ord.n);
			double y = radius * Math.sin(2*Math.PI*rep.ord.data[i]/rep.ord.n);
			drawNode(gl2, x, y, scale);
		}
	}

	/* draw an edge between the coordinates (x1,y1) and (x2,y2) */
	private static void drawEdge(GL2 gl2, float x1, float y1, float x2, float y2 ) {
		gl2.glBegin( GL2.GL_LINE_STRIP );
		gl2.glColor3f(0.75f, 0.75f, 0.75f);
		gl2.glVertex3f(x1, y1, 0f);
		gl2.glVertex3f(x2, y2, 0f);
		gl2.glEnd();
		
	}
	/* draw a node at the coordinate (x,y) */
	private static void drawNode(GL2 gl2, double x, double y, double scale) {
		gl2.glBegin( GL2.GL_POLYGON );
		gl2.glColor3f(0.19f,0.9f,0.79f);
	
		int circle_resolution = 30;
		for(int i=0; i<circle_resolution; i++) {
			float norm_x = (float)(scale*Math.cos(2*Math.PI*i/circle_resolution) + x);
			float norm_y = (float)(scale*Math.sin(2*Math.PI*i/circle_resolution) + y);
			gl2.glVertex3f( norm_x, norm_y, 0f );
		}
		gl2.glEnd();
	}
}