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
 * RMesh is a reduced interface for creating, holding and drawing meshes. A mesh is a group of triangular strips (RStrip).
 * @eexample RMesh
 * @usage Geometry
 * @related RStrip
 * @extended
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
   * Create a new empty mesh. 
   * @eexample createaMesh
   */
  public RMesh(){
    strips = null;
    type = RGeomElem.MESH;
  }

  /**
   * Copy a mesh. 
   * @eexample createaMesh
   * @param m  the object of which to make a copy
   */  
  public RMesh(RMesh m){
    if(m == null){
      return;
    }
    
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
   * Add a new strip. 
   * @eexample addStrip
   * @param s  the strip to be added
   * @related addPoint ( )
   */  
  public void addStrip(RStrip s){
    this.append(s);
  }

  public void addStrip(){
    this.append(new RStrip());
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
   * Add a new point to the current strip. 
   * @eexample addPoint
   * @param p  the point to be added
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

  /**
   * Add a new point to the current strip. 
   * @eexample addPoint
   * @param x  the x coordinate of the point to be added
   * @param y  the y coordinate of the point to be added
   * @related addStrip ( )
   * @related setCurrent ( )
   * @invisible
   */  
  public void addPoint(float x, float y){
    if (strips == null) {
      this.append(new RStrip());
    }
    this.strips[currentStrip].append(new RPoint(x,y));
  }
  
  /**
   * Add a new point to the given strip. 
   * @eexample addPoint
   * @param indStrip  the index of the strip to which the point will be added
   * @param p  the point to be added
   * @related addStrip ( )
   * @related setCurrent ( )
   * @invisible
   */
  public void addPoint(int indStrip, RPoint p){
    if (strips == null) {
      this.append(new RStrip());
    }
    this.strips[indStrip].append(p);
  }
  
  /**
   * Add a new point to the given strip. 
   * @eexample addPoint
   * @param indStrip  the index of the strip to which the point will be added
   * @param x  the x coordinate of the point to be added
   * @param y  the y coordinate of the point to be added
   * @related addStrip ( )
   * @related setCurrent ( )
   * @invisible
   */
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
      if(this.style.texture != null)
        {
          g.texture(this.style.texture);
          for(int j=0;j<this.strips[i].vertices.length;j++)
            {
              float x = this.strips[i].vertices[j].x;
              float y = this.strips[i].vertices[j].y;
              /*
                float u = (x - minx)/(maxx-minx) * this.style.texture.width;
                float v = (y - miny)/(maxy-miny) * this.style.texture.height;
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
      if(this.style.texture != null)
        {
          g.texture(this.style.texture);
        }
      for(int j=0;j<this.strips[i].vertices.length;j++){
        g.vertex(this.strips[i].vertices[j].x,this.strips[i].vertices[j].y);
      }
      g.endShape(PConstants.CLOSE);
    }
  }		
  
  /**
   * Use this to get the vertices of the mesh.  It returns the points as an array of RPoint.
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
   * Use this to get the vertices of the mesh.  It returns the points as an array of RPoint.
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

  public RPoint[][] getPointsInPaths(){
    PApplet.println("Feature not yet implemented for this class.");
    return null;
  }

  public RPoint[][] getHandlesInPaths(){
    PApplet.println("Feature not yet implemented for this class.");
    return null;
  }

  public RPoint[][] getTangentsInPaths(){
    PApplet.println("Feature not yet implemented for this class.");
    return null;
  }

  public boolean contains(RPoint p){
    PApplet.println("Feature not yet implemented for this class.");
    return false;
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
