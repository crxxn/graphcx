package graph.circlefit.viz;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import graph.circlefit.GraphRepresentation;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GraphDrawer  {

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