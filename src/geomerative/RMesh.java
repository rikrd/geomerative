/**
    Copyright 2004-2008 Ricard Marxer  <email@ricardmarxer.com>

    This file is part of Geomerative.

    Geomerative is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Geomerative is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with Geomerative.  If not, see <http://www.gnu.org/licenses/>.
*/

package geomerative ;
import processing.core.*;

/**
 * RMesh is a reduced interface for creating, holding and drawing meshes. Meshes are a group of tiangle strips (RStrip).
 * @eexample RMesh
 * @usage Geometry
 * @related RStrip
 */
public class RMesh extends RGeomElem
{
  /**
   * @invisible
   */
  public int type = RGeomElem.MESH;
  
  /**
   * Array of RStrip objects holding the contours of the polygon. 
   * @eexample strips
   * @related RStrip
   * @related countStrips ( )
   * @related addStrip ( )
   */
  public RStrip[] strips;
  int currentStrip=0;
  // ----------------------
  // --- Public Methods ---
  // ----------------------
  
  /**
   * Use this method to create a new empty mesh. 
   * @eexample createaMesh
   * @return RMesh, the mesh newly created
   * @param RMesh m, the object of which to make a copy
   */
  public RMesh(){
    strips = null;
    type = RGeomElem.MESH;
  }
  
  public RMesh(RMesh m){
    for(int i=0;i<m.countStrips();i++){
      this.append(new RStrip(m.strips[i]));
    }
    type = RGeomElem.MESH;

    setStyle(m);
  }
  
  /**
   * Use this method to count the number of strips in the mesh. 
   * @eexample countStrips
   * @return int, the number strips in the mesh
   * @related addStrip ( )
   */
  public int countStrips(){
    if(this.strips==null){
      return 0;
    }
    
    return this.strips.length;
  }
  
  /**
   * Use this method to create a new strip. 
   * @eexample addStrip
   * @param s RStrip, the strip to be added
   * @related addPoint ( )
   */
  public void addStrip(){
    this.append(new RStrip());
  }
  
  public void addStrip(RStrip s){
    this.append(s);
  }
  
  /**
   * Use this method to set the current strip to which append points. 
   * @eexample addStrip
   * @related addPoint ( )
   * @invisible
   */
  public void setCurrent(int indStrip){
    this.currentStrip = indStrip;
  }
  
  /**
   * Use this method to add new points to the current strip. 
   * @eexample addPoint
   * @param indStrip int, the index of the strip to which the point will be added
   * @param p RPoint, the point to be added
   * @param x float, the x coordinate of the point to be added
   * @param y float, the y coordinate of the point to be added
   * @related addStrip ( )
   * @related setCurrent ( )
   * @invisible
   */
  public void addPoint(RPoint p){
    if (strips == null) {
      this.append(new RStrip());
    }
    this.strips[currentStrip].append(p);
  }
  
  public void addPoint(float x, float y){
    if (strips == null) {
      this.append(new RStrip());
    }
    this.strips[currentStrip].append(new RPoint(x,y));
  }
  
  public void addPoint(int indStrip, RPoint p){
    if (strips == null) {
      this.append(new RStrip());
    }
    this.strips[indStrip].append(p);
  }
  
  public void addPoint(int indStrip, float x, float y){
    if (strips == null) {
      this.append(new RStrip());
    }
    this.strips[indStrip].append(new RPoint(x,y));
  }
  
  /**
   * Use this method to draw the mesh.
   * @eexample drawMesh
   * @param g PGraphics, the graphics object on which to draw the mesh
   */
  public void draw(PGraphics g){
    for(int i=0;i<this.countStrips();i++){
      g.beginShape(PConstants.TRIANGLE_STRIP);
      if(this.texture != null)
        {
          g.texture(this.texture);
          for(int j=0;j<this.strips[i].vertices.length;j++)
            {
              float x = this.strips[i].vertices[j].x;
              float y = this.strips[i].vertices[j].y;
              /*
                float u = (x - minx)/(maxx-minx) * this.texture.width;
                float v = (y - miny)/(maxy-miny) * this.texture.height;
              */
              g.vertex(x, y, x, y);
            }
        }else{
        for(int j=0;j<this.strips[i].vertices.length;j++)
          {
            float x = this.strips[i].vertices[j].x;
            float y = this.strips[i].vertices[j].y;
            
            g.vertex(x, y);
          }
      }
      g.endShape(PConstants.CLOSE);
    }
    
    
  }
  
  public void draw(PApplet g){
    for(int i=0;i<this.countStrips();i++){
      g.beginShape(PConstants.TRIANGLE_STRIP);
      if(this.texture != null)
        {
          g.texture(this.texture);
        }
      for(int j=0;j<this.strips[i].vertices.length;j++){
        g.vertex(this.strips[i].vertices[j].x,this.strips[i].vertices[j].y);
      }
      g.endShape(PConstants.CLOSE);
    }
  }		
  
  /**
   * Use this method to know if the mesh is inside a graphics object. This might be useful if we want to delete objects that go offscreen.
   * @eexample RMesh_isIn
   * @usage Geometry
   * @param PGraphics g, the graphics object
   * @return boolean, whether the mesh is in or not the graphics object
   */
  public boolean isIn(PGraphics g){
    RContour c = getBounds();
    float x0 = g.screenX(c.points[0].x,c.points[0].y);
    float y0 = g.screenY(c.points[0].x,c.points[0].y);
    float x1 = g.screenX(c.points[1].x,c.points[1].y);
    float y1 = g.screenY(c.points[1].x,c.points[1].y);
    float x2 = g.screenX(c.points[2].x,c.points[2].y);
    float y2 = g.screenY(c.points[2].x,c.points[2].y);
    float x3 = g.screenX(c.points[3].x,c.points[3].y);
    float y3 = g.screenY(c.points[3].x,c.points[3].y);
    
    return ((((x0 > 0 && x0 < g.width) && (y0 > 0 && y0 < g.height)) ||
             ((x1 > 0 && x1 < g.width) && (y1 > 0 && y1 < g.height)) ||
             ((x2 > 0 && x2 < g.width) && (y2 > 0 && y2 < g.height)) ||
             ((x3 > 0 && x3 < g.width) && (y3 > 0 && y3 < g.height))));
  }
  
  /**
   * Use this method to get the bounding box of the mesh. 
   * @eexample getBounds
   * @return RContour, the bounding box of the mesh in the form of a fourpoint contour
   * @related getCenter ( )
   */
  public RContour getBounds(){
    float xmin =  Float.MAX_VALUE ;
    float ymin =  Float.MAX_VALUE ;
    float xmax = -Float.MAX_VALUE ;
    float ymax = -Float.MAX_VALUE ;
    
    for(int j=0;j<this.countStrips();j++){
      for( int i = 0 ; i < this.strips[j].countVertices() ; i++ )
        {
          float x = this.strips[j].vertices[i].x;
          float y = this.strips[j].vertices[i].y;
          if( x < xmin ) xmin = x;
          if( x > xmax ) xmax = x;
          if( y < ymin ) ymin = y;
          if( y > ymax ) ymax = y;
        }
    }
    
    RContour c = new RContour();
    c.addPoint(xmin,ymin);
    c.addPoint(xmin,ymax);
    c.addPoint(xmax,ymax);
    c.addPoint(xmax,ymin);
    return c;
  }
  
  /**
   * Use this method to get the center point of the mesh. 
   * @eexample RMesh_getCenter
   * @return RPoint, the center point of the mesh
   * @related getBounds ( )
   */
  public RPoint getCenter(){
    RContour c = getBounds();
    return new RPoint((c.points[2].x + c.points[0].x)/2,(c.points[2].y + c.points[0].y)/2);
  }
  
  /**
   * Use this to get the vertices of the mesh.  It returns the points in the way of an array of RPoint.
   * @eexample RMesh_getHandles
   * @return RPoint[], the vertices returned in an array.
   * */
  public RPoint[] getHandles(){
    int numStrips = countStrips();
    if(numStrips == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numStrips;i++){
      RPoint[] newPoints = strips[i].getHandles();
      if(newPoints!=null){
        if(result==null){
          result = new RPoint[newPoints.length];
          System.arraycopy(newPoints,0,result,0,newPoints.length);
        }else{
          newresult = new RPoint[result.length + newPoints.length];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
          result = newresult;
        }
      }
    }
    return result;
  }
  
  /**
   * Use this to get the vertices of the mesh.  It returns the points in the way of an array of RPoint.
   * @eexample RMesh_getPoints
   * @return RPoint[], the vertices returned in an array.
   * */
  public RPoint[] getPoints(){
    int numStrips = countStrips();
    if(numStrips == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numStrips;i++){
      RPoint[] newPoints = strips[i].getPoints();
      if(newPoints!=null){
        if(result==null){
          result = new RPoint[newPoints.length];
          System.arraycopy(newPoints,0,result,0,newPoints.length);
        }else{
          newresult = new RPoint[result.length + newPoints.length];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
          result = newresult;
        }
      }
    }
    return result;
  }
  
  public RPoint getPoint(float t){
    PApplet.println("Feature not yet implemented for this class.");
    return null;
  }

  public RPoint getTangent(float t){
    PApplet.println("Feature not yet implemented for this class.");
    return null;
  }
  
  public RPoint[] getTangents(){
    PApplet.println("Feature not yet implemented for this class.");
    return null;
  }

  public RPoint[][] getPointPaths(){
    PApplet.println("Feature not yet implemented for this class.");
    return null;
  }

  public RPoint[][] getHandlePaths(){
    PApplet.println("Feature not yet implemented for this class.");
    return null;
  }

  public RPoint[][] getTangentPaths(){
    PApplet.println("Feature not yet implemented for this class.");
    return null;
  }
  
  /**
   * Use this method to get the type of element this is.
   * @eexample RMesh_getType
   * @return int, will allways return RGeomElem.MESH
   */
  public int getType(){
    return type;
  }
  
  /**
   * Use this method to transform the mesh.
   * @eexample transformMesh
   * @param m RMatrix, the matrix of the affine transformation to apply to the mesh
   */
  public void transform(RMatrix m){
    int numStrips = countStrips();
    if(numStrips!=0){
      for(int i=0;i<numStrips;i++){
        strips[i].transform(m);
      }
    }
  }
  
  /**
   * @invisible
   */
  public RMesh toMesh(){
    return this;
  }
  
  /**
   * @invisible
   */
  public RPolygon toPolygon() throws RuntimeException{
    throw new RuntimeException("Transforming a Mesh to a Polygon is not yet implemented.");
  }
  
  /**
   * @invisible
   */
  public RShape toShape() throws RuntimeException{
    throw new RuntimeException("Transforming a Mesh to a Shape is not yet implemented.");
  }
  
  /**
   * Remove all of the points.  Creates an empty polygon.
   */
  void clear(){
    this.strips = null;
  }
  
  void append(RStrip nextstrip)
  {
    RStrip[] newstrips;
    if(strips==null){
      newstrips = new RStrip[1];
      newstrips[0] = nextstrip;
      currentStrip = 0;
    }else{
      newstrips = new RStrip[this.strips.length+1];
      System.arraycopy(this.strips,0,newstrips,0,this.strips.length);
      newstrips[this.strips.length]=nextstrip;
      currentStrip++;
    }
    this.strips=newstrips; 
  }
}
