package geomerative ;
import processing.core.*;

/**
 * RStrip is a reduced interface for creating, holding and drawing triangle strips. Triangle strips are ordered lists of points (RPoint) which define the vertices of a mesh.
 * @eexample RStrip
 * @usage Geometry
 * @related RPoint
 * @related RMesh
 */
public class RStrip
{
	/**
	* @invisible
	*/
	public int type = RGeomElem.TRISTRIP;

	/**
	* Array of RPoint objects holding the vertices of the strip. 
	* @eexample vertices
	* @related RPoint
	* @related countVertices ( )
	* @related addVertex ( )
	*/
	public RPoint vertices[];

	// ----------------------
	// --- Public Methods ---
	// ----------------------

	/**
	* Use this method to create a new strip.
	* @eexample RStrip ( )
	* @related addVertex ( )
	* @param RStrip s, the object of which to make a copy
	*/
	public RStrip(){
		vertices=null;
	}

	public RStrip(RStrip s){
		for(int i=0;i<s.countVertices();i++){
			this.append(new RPoint(s.vertices[i]));
		}
	}

	/**
	* Use this method to count the number of vertices in the strip. 
 	* @eexample countVertices
 	* @return int, the number vertices in the strip
 	*/
	public int countVertices(){
		if(this.vertices==null){
			return 0;
		}
		
		return this.vertices.length;
	}

	/**
	* Use this method to draw the strip. 
	* @eexample drawStrip
	* @param g PGraphics, the graphics object on which to draw the strip
 	*/
	public void draw(PGraphics g){
		int numVertices = countVertices();
		g.beginShape(g.TRIANGLE_STRIP);
		for(int i=0;i<numVertices;i++){
			g.vertex(vertices[i].x,vertices[i].y);
		}
		g.endShape();
	}

	/**
	* Use this method to add new vertices to the strip.
	* @eexample addVertex ( )
	*/
	public void addVertex(RPoint p){
		this.append(p);
	}
	
	public void addVertex(float x, float y){
		this.append(new RPoint(x,y));
	}

	/**
	* Use this method to get the bounding box of the strip. 
	* @eexample getBounds
	* @return RContour, the bounding box of the strip in the form of a fourpoint contour
 	* @related draw ( )
 	*/
	public RContour getBounds(){
      		float xmin =  Float.MAX_VALUE ;
      		float ymin =  Float.MAX_VALUE ;
      		float xmax = -Float.MAX_VALUE ;
      		float ymax = -Float.MAX_VALUE ;
      		
		for( int i = 0 ; i < this.countVertices() ; i++ )
		{
			float x = this.vertices[i].x;
			float y = this.vertices[i].y;
			if( x < xmin ) xmin = x;
			if( x > xmax ) xmax = x;
			if( y < ymin ) ymin = y;
			if( y > ymax ) ymax = y;
		}
      			
      		RContour c = new RContour();
      		c.addPoint(xmin,ymin);
      		c.addPoint(xmin,ymax);
      		c.addPoint(xmax,ymax);
      		c.addPoint(xmax,ymin);
      		return c;
	}

	/**
	 * Use this to get the vertices of the strip.  It returns the points in the way of an array of RPoint.
	 * @eexample RStrip_getPoints
	 * @return RPoint[], the vertices returned in an array.
	 * */
	public RPoint[] getPoints(){
		return vertices;
	}

	/**
	 * Use this to get the vertices of the strip.  It returns the points in the way of an array of RPoint.
	 * @eexample RStrip_getCurvePoints
	 * @return RPoint[], the vertices returned in an array.
	 * */
        public RPoint[] getCurvePoints(){
		return vertices;
	}


   	/**
	* Use this method to transform the strip.
	* @eexample transformStrip
	* @param m RMatrix, the matrix of the affine transformation to apply to the strip
 	*/
	public void transform(RMatrix m){
		int numVertices = countVertices();
		if(numVertices!=0){
			for(int i=0;i<numVertices;i++){
				vertices[i].transform(m);
			}
		}
	}

	void add(RPoint p){
		this.append(p);
	}
	
	void add(float x, float y){
		this.append(new RPoint(x,y));
	}
	
	/**
	* Remove all of the points.  Creates an empty polygon.
	*/
   	void clear(){
   		this.vertices = null;
	}
	
	void append(RPoint nextvertex)
	{
	  RPoint[] newvertices;
	  if(vertices==null){
	  	newvertices = new RPoint[1];
	  	newvertices[0] = nextvertex;
	  }else{
	  	newvertices = new RPoint[this.vertices.length+1];
	  	System.arraycopy(this.vertices,0,newvertices,0,this.vertices.length);
	  	newvertices[this.vertices.length]=nextvertex;
	  }
	  this.vertices=newvertices;
	}
	
}