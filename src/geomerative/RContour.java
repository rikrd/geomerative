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
 * RContour is a reduced interface for creating, holding and drawing contours. Contours are ordered lists of points (RPoint) which define the outlines of polygons.  Contours can be self-intersecting.
 * @eexample RContour
 * @usage Geometry
 * @related RPoint
 * @related RPolygon
 */
public class RContour extends RGeomElem
{
  /**
   * @invisible
   */
  public int type = RGeomElem.CONTOUR;
  
  /**
   * Array of RPoint objects holding the points of the contour.
   * @eexample points
   * @related RPoint
   * @related countPoints ( )
   * @related addPoint ( )
   */
  public RPoint[] points;
  boolean isContributing = true;
  boolean isHole = false;
  boolean closed = true;
  /**
   * Use this method to count the number of points in the contour. 
   * @eexample countPoints
   * @return int, the number points in the contour
   */
  public int countPoints(){
    if(this.points==null){
      return 0;
    }
    
    return this.points.length;
  }
  
  /**
   * Use this constructor to make a copy of another RCommand object.  This can be useful when wanting to transform one but at the same time keep the original.
   * @param  RContour c, the object of which to make the copy
   * @invisible
   */
  public RContour(RPoint[] contourpoints){
    this.points = contourpoints;
  }
  
  public RContour(){
  }
  
  public RContour(RContour c){
    for(int i=0;i<c.countPoints();i++){
      this.append(new RPoint(c.points[i]));
    }
    isHole = c.isHole;
    isContributing = c.isContributing;
    
    setStyle(c);
  }
  
  
  /**
   * Use this method to draw the contour. 
   * @eexample drawContour
   * @param g PGraphics, the graphics object on which to draw the mesh
   */
  public void draw(PGraphics g){
    int numPoints = countPoints();
    boolean beforeFill = g.fill;
    g.noFill();
    g.beginShape();
    for(int i=0;i<numPoints;i++){
      g.vertex(points[i].x,points[i].y);
    }
    g.endShape(closed ? RGeomerative.parent().CLOSE : RGeomerative.parent().OPEN);
    if(beforeFill)
      g.fill(g.fillColor);
  }
  
  public void draw(PApplet g){
    int numPoints = countPoints();
    boolean beforeFill = g.g.fill;
    g.noFill();
    g.beginShape();
    for(int i=0;i<numPoints;i++){
      g.vertex(points[i].x,points[i].y);
    }
    g.endShape(closed ? RGeomerative.parent().CLOSE : RGeomerative.parent().OPEN);
    if(beforeFill)
      g.fill(g.g.fillColor);
  }
  
  /**
   * Use this method to add new points to the contour.
   * @eexample addPoint ( )
   */
  public void addPoint(RPoint p){
    this.append(p);
  }
  
  public void addPoint(float x, float y){
    this.append(new RPoint(x,y));
  }
  
  /**
   * Use this method to get the bounding box of the contour. 
   * @eexample getBounds
   * @return RContour, the bounding box of the contour in the form of a fourpoint contour
   * @related draw ( )
   */
  public RContour getBounds(){
    float xmin =  Float.MAX_VALUE ;
    float ymin =  Float.MAX_VALUE ;
    float xmax = -Float.MAX_VALUE ;
    float ymax = -Float.MAX_VALUE ;
    
    for( int i = 0 ; i < this.countPoints() ; i++ )
      {
        float x = this.points[i].x;
        float y = this.points[i].y;
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
   * Use this method to get the center point of the contour. 
   * @eexample RPolygon_getCenter
   * @return RPoint, the center point of the contour
   * @related getBounds ( )
   */
  public RPoint getCenter(){
    RContour c = getBounds();
    return new RPoint((c.points[2].x + c.points[0].x)/2,(c.points[2].y + c.points[0].y)/2);
  }
  
  /**
   * Use this to return the points of the contour.  It returns the points in the way of an array of RPoint.
   * @eexample RContour_getHandles
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getHandles(){
    return points;
  }
  
  /**
   * Use this to return the points of the contour.  It returns the points in the way of an array of RPoint.
   * @eexample RContour_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getPoints(){
    return points;
  }
  
  
  /**
   * Use this method to know if the contour is a hole. Remember to use the method update() on the polygon before using this method.
   * @eexample RPolygon_isHole
   * @return boolean, true if it is a hole
   * @related update ( )
   */
  public boolean isHole(){
    return isHole;
  }
  
  /**
   * Use this method to transform the contour.
   * @eexample transformContour
   * @param m RMatrix, the matrix of the affine transformation to apply to the contour
   */
  /*
    public void transform(RMatrix m){
    int numPoints = countPoints();
    if(numPoints!=0){
    for(int i=0;i<numPoints;i++){
    points[i].transform(m);
    }
    }
    }
  */

  public void print(){
    System.out.println("contour: ");
    for(int i=0;i<countPoints();i++)
      {
        System.out.println("---  point "+i+" ---");
        points[i].print();
        System.out.println("---------------");
      }
  }
  
  public void addClose(){
    if(points == null){
      return;
    }
    
    if((points[0].x == points[points.length-1].x) && (points[0].y == points[points.length-1].y))
      {
        return;
      }
    
    addPoint(new RPoint(points[0].x, points[0].y));
    closed = true;
  }
  
  /**
   * @invisible
   */
  public RPolygon toPolygon(){
    return new RPolygon(this);
  }
  
  /**
   * @invisible
   */
  public RShape toShape() throws RuntimeException{
    throw new RuntimeException("Transforming a Contour to a Shape is not yet implemented.");
  }
  
  /**
   * @invisible
   */
  public RMesh toMesh(){
    return this.toPolygon().toMesh();
  }
  
  /**
   * Use this method to get the type of element this is.
   * @eexample RPolygon_getType
   * @return int, will allways return RGeomElem.POLYGON
   */
  public int getType(){
    return type;
  }
  
  void append(RPoint nextpoint)
  {
    RPoint[] newpoints;
    if(points==null){
      newpoints = new RPoint[1];
      newpoints[0] = nextpoint;
    }else{
      newpoints = new RPoint[this.points.length+1];
      System.arraycopy(this.points,0,newpoints,0,this.points.length);
      newpoints[this.points.length]=nextpoint;
    }
    this.points=newpoints; 
  }
}
