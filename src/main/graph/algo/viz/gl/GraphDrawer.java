package graph.algo.viz.gl;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import graph.Edge;
import graph.algo.viz.Representation;

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

	public static GraphDrawer getInstance(Representation rep) {
		if (GraphDrawer.gd == null) {
			GraphDrawer gd = new GraphDrawer();
			GraphDrawer.gd = gd;
			gd.updateData(rep);
			return gd;
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
	
	public void updateData(Representation rep) {
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
	
	private static Representation rep;
	
	protected static synchronized void setRenderData(Representation r) {
		rep = r;
	}
	
	protected static synchronized void render( GL2 gl2, int width, int height) {
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
		float diameter = 0.01f;
		float scale = 0.8f;
		for (Edge e: rep.getGraph().getEdgeList()) {
			drawEdge(gl2, e, scale);
		}
		for (int i=0; i<rep.getGraph().vertexCount(); i++) {
			drawVertex(gl2, i, diameter, scale);
		}
	}	
	
	private static void drawVertex(GL2 gl2, int vertex, float diameter, float scale) {
		if (rep.getDimensions() == 2) {
			int circle_resolution = 25;
			gl2.glBegin(GL2.GL_POLYGON);
			gl2.glColor3f(0.19f, 0.9f, 0.79f);

			for (int i=0; i<circle_resolution; i++) {
				float x = (float)(diameter*Math.cos(2*Math.PI*i/circle_resolution) + scale*rep.getLayout(vertex, 0));
				float y = (float)(diameter*Math.sin(2*Math.PI*i/circle_resolution) + scale*rep.getLayout(vertex, 1));
				gl2.glVertex3f(x, y, 0f);
			}
			
		} else {
			System.err.println("Functionality not implemented yet");
			System.exit(1);
		}
		gl2.glEnd();
	}
		
	private static void drawEdge(GL2 gl2, Edge e, float scale) {
		gl2.glBegin(GL2.GL_LINE_STRIP);
		gl2.glColor3f(0.75f, 0.75f, 0.75f);
		float z1 = 0f;
		float z2 = 0f;

		if (rep.getDimensions() == 3) {
			z1 = scale* (float)rep.getLayout(e.getVertex(0), 2);
			z2 = scale* (float)rep.getLayout(e.getVertex(1), 2);
		} 

		int x = e.getVertex(0);
		int y = e.getVertex(1);
		gl2.glVertex3f(scale * (float) rep.getLayout(x, 0), scale * (float) rep.getLayout(x, 1), z1);
		gl2.glVertex3f(scale * (float) rep.getLayout(y, 0), scale * (float) rep.getLayout(y, 1), z2);

		gl2.glEnd();
	}
	
}