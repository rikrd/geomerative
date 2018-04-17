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

package geomerative;
import processing.core.*;


/**
 * RPolygon is a reduced interface for creating, holding and drawing complex polygons. Polygons are groups of one or more contours (RContour).  This interface allows us to perform binary operations (difference, xor, union and intersection) on polygons.
 * @eexample RPolygon
 * @usage Geometry
 * @related RContour
 * @related createCircle ( )
 * @related createRing ( )
 * @related createStar ( )
 * @related diff ( )
 * @related xor ( )
 * @related union ( )
 * @related intersection ( )
 * @extended
 */
public class RPolygon extends RGeomElem
{
  /**
   * @invisible
   */
  public int type = RGeomElem.POLYGON;
  public static int defaultDetail = 50;

  /**
   * Array of RContour objects holding the contours of the polygon.
   * @eexample contours
   * @related RContour
   * @related countContours ( )
   * @related addContour ( )
   */
  public RContour[] contours;
  int currentContour = 0;

  // ----------------------
  // --- Public Methods ---
  // ----------------------

  /**
   * Make a copy of the given polygon.
   * @eexample createPolygon
   * @param p  the object of which to make a copy
   */
  public RPolygon(RPolygon p){
    if (p == null){
      return;
    }

    for(int i=0;i<p.countContours();i++){
      this.append(new RContour(p.contours[i]));
    }
    type = RGeomElem.POLYGON;

    setStyle(p);
  }

  /**
   * Create a new polygon given an array of points.
   * @eexample createPolygon
   * @param points  the points for the new polygon.
   */
  public RPolygon(RPoint[] points){
    this(new RContour(points));
  }

  /**
   * Create a new polygon given a contour.
   * @param newcontour  the contour for the new polygon.
   */
  public RPolygon(RContour newcontour){
    this.append(newcontour);
    type = RGeomElem.POLYGON;
  }

  /**
   * Create an empty polygon.
   */
  public RPolygon(){
    contours = null;
    type = RGeomElem.POLYGON;
  }

  /**
   * Use this method to create a new circle polygon.
   * @eexample createCircle
   * @param radius  the radius of the circle
   * @param detail  the number of vertices of the polygon
   * @return RPolygon, the circular polygon newly created
   */
  static public RPolygon createCircle(float x, float y, float radius,int detail){
    RPoint[] points = new RPoint[detail];
    double radiansPerStep = 2*Math.PI/detail;
    for(int i=0;i<detail;i++){
      points[i] = new RPoint(
        radius*Math.cos(i*radiansPerStep) + x,
        radius*Math.sin(i*radiansPerStep) + y
      );
    }
    return new RPolygon(points);
  }

  static public RPolygon createCircle(float radius, int detail){
    return createCircle(0,0,radius,detail);
  }

  static public RPolygon createCircle(float x, float y, float radius){
    return createCircle(x,y,radius,defaultDetail);
  }

  static public RPolygon createCircle(float radius){
    return createCircle(0,0,radius,defaultDetail);
  }

  /**
   * Use this method to create a new rectangle polygon.
   * @eexample createRectangle
   * @param x  the upper-left corner x coordinate
   * @param y  the upper-left corner y coordinate
   * @param w  the width
   * @param h  the height
   * @return RPolygon, the circular polygon newly created
   */
  static public RPolygon createRectangle(float x, float y, float w,float h){
    RPolygon rectangle = new RPolygon();
    rectangle.addPoint(x,y);
    rectangle.addPoint(x+w,y);
    rectangle.addPoint(x+w,y+h);
    rectangle.addPoint(x,y+h);
    rectangle.addPoint(x,y);
    return rectangle;
  }

  static public RPolygon createRectangle(float w, float h){
    return createRectangle(0,0, w, h);
  }

  /**
   * Use this method to create a new starform polygon.
   * @eexample createStar
   * @param radiusBig  the outter radius of the star polygon
   * @param radiusSmall  the inner radius of the star polygon
   * @param spikes  the amount of spikes on the star polygon
   * @return RPolygon, the starform polygon newly created
   */
  static public RPolygon createStar(float x, float y, float radiusBig, float radiusSmall, int spikes){
    int numPoints = spikes*2;
    RPoint[] points = new RPoint[numPoints];
    double radiansPerStep = Math.PI/spikes;
    for(int i=0;i<numPoints;i+=2){
      points[i] = new RPoint(
        radiusBig*Math.cos(i*radiansPerStep) + x,
        radiusBig*Math.sin(i*radiansPerStep) + y
      );
      points[i+1] = new RPoint(
        radiusSmall*Math.cos(i*radiansPerStep) + x,
        radiusSmall*Math.sin(i*radiansPerStep) + y
      );
    }
    return new RPolygon(points);
  }

  static public RPolygon createStar(float radiusBig, float radiusSmall, int spikes){
    return createStar(0,0,radiusBig, radiusSmall, spikes);
  }

  /**
   * Use this method to create a new ring polygon.
   * @eexample createRing
   * @param radiusBig  the outter radius of the ring polygon
   * @param radiusSmall  the inner radius of the ring polygon
   * @param detail  the number of vertices on each contour of the ring
   * @return RPolygon, the ring polygon newly created
   */
  static public RPolygon createRing(float x, float y, float radiusBig, float radiusSmall, int detail){
    RPoint[] inner = new RPoint[detail];
    RPoint[] outer = new RPoint[detail];
    double radiansPerStep = 2*Math.PI/detail;
    for(int i=0;i<detail;i++){
      inner[i] = new RPoint(
        radiusSmall*Math.cos(i*radiansPerStep) + x,
        radiusSmall*Math.sin(i*radiansPerStep) + y
      );
      outer[i] = new RPoint(
        radiusBig*Math.cos(i*radiansPerStep) + x,
        radiusBig*Math.sin(i*radiansPerStep) + y
      );
    }
    RPolygon ring = new RPolygon();
    ring.addContour(outer);
    ring.addContour(inner);
    return ring;
  }

  static public RPolygon createRing(float radiusBig, float radiusSmall, int detail){
    return createRing(0, 0, radiusBig, radiusSmall, detail);
  }

  static public RPolygon createRing(float x, float y, float radiusBig, float radiusSmall){
    return createRing(x, y, radiusBig, radiusSmall, defaultDetail);
  }

  static public RPolygon createRing(float radiusBig, float radiusSmall){
    return createRing(0, 0, radiusBig, radiusSmall, defaultDetail);
  }

  /**
   * Use this method to get the centroid of the element.
   * @eexample RGroup_getCentroid
   * @return RPo the centroid point of the element
   * @related getBounds ( )
   * @related getCenter ( )
   */
  public RPoint getCentroid(){
    RPoint bestCentroid = new RPoint();
    float bestArea = Float.NEGATIVE_INFINITY;
    if(contours != null){
      for(int i=0;i<contours.length;i++)
        {
          float area = Math.abs(contours[i].getArea());
          if(area > bestArea){
            bestArea = area;
            bestCentroid = contours[i].getCentroid();
          }
        }
      return bestCentroid;
    }
    return null;
  }

  /**
   * Use this method to count the number of contours in the polygon.
   * @eexample countContours
   * @return int  the number contours in the polygon
   * @related addContour ( )
   */
  public int countContours(){
    if(this.contours==null){
      return 0;
    }

    return this.contours.length;
  }

  /**
   * Add a new contour to the polygon.
   * @eexample addContour
   * @param c  the contour to be added
   * @related addPoint ( )
   */
  public void addContour(RContour c){
    this.append(c);
  }

  /**
   * Add an empty contour to the polygon.
   * @eexample addContour
   * @related addPoint ( )
   */
  public void addContour(){
    this.append(new RContour());
  }

  /**
   * Add a new contour to the polygon given an array of points.
   * @eexample addContour
   * @param points  the points of the new contour to be added
   * @related addPoint ( )
   */
  public void addContour(RPoint[] points){
    this.append(new RContour(points));
  }



  /**
   * Use this method to set the current contour to which append points.
   * @eexample addContour
   * @related addPoint ( )
   */
  public void setContour(int indContour){
    this.currentContour = indContour;
  }

  /**
   * Add a new point to the current contour.
   * @eexample addPoint
   * @param p  the point to be added
   * @related addContour ( )
   * @related setCurrent ( )
   */
  public void addPoint(RPoint p){
    if (contours == null) {
      this.append(new RContour());
    }
    this.contours[currentContour].append(p);
  }

  /**
   * Add a new point to the current contour.
   * @eexample addPoint
   * @param x  the x coordinate of the point to be added
   * @param y  the y coordinate of the point to be added
   * @related addContour ( )
   * @related setCurrent ( )
   */
  public void addPoint(float x, float y){
    if (contours == null) {
      this.append(new RContour());
    }
    this.contours[currentContour].append(new RPoint(x,y));
  }

  /**
   * Add a new point to the selected contour.
   * @eexample addPoint
   * @param indContour  the index of the contour to which the point will be added
   * @param p  the point to be added
   * @related addContour ( )
   * @related setCurrent ( )
   */
  public void addPoint(int indContour, RPoint p){
    if (contours == null) {
      this.append(new RContour());
    }
    this.contours[indContour].append(p);
  }

  /**
   * Add a new point to the selected contour.
   * @eexample addPoint
   * @param indContour  the index of the contour to which the point will be added
   * @param x  the x coordinate of the point to be added
   * @param y  the y coordinate of the point to be added
   * @related addContour ( )
   * @related setCurrent ( )
   */
  public void addPoint(int indContour, float x, float y){
    if (contours == null) {
      this.append(new RContour());
    }
    this.contours[indContour].append(new RPoint(x,y));
  }

  public void addClose(){
    if(contours == null){
      return;
    }

    contours[contours.length - 1].addClose();
  }

  /**
   * Use this method to create a new mesh from a given polygon.
   * @eexample toMesh
   * @return RMesh, the mesh made of tristrips resulting of a tesselation of the polygon
   * @related draw ( )
   */
  public RMesh toMesh(){
    if ( contours == null ){
      return new RMesh();
    }

    RMesh mesh = RClip.polygonToMesh( this );
    if ( mesh == null ) {
      return null;
    }

    mesh.setStyle( this ) ;
    return mesh;
  }

  public void print(){
    System.out.println("polygon: ");
    for( int i = 0 ; i < countContours() ; i++ )
      {
        System.out.println("---  contour "+i+" ---");
        contours[i].print();
        System.out.println("---------------");
      }
  }


  /**
   * Removes contours with less than 3 points.  These are contours that are open.
   * Since close polygons have points[0] == points[-1] and two more points to form a triangle at least.
   * This is useful to avoid the clipping algorithm from breaking.
   * @invisible
   */
  protected RPolygon removeOpenContours(){
    RPolygon clean = new RPolygon();
    for(int i=0;i<countContours();i++)
      {
        if(contours[i].countPoints() > 3){
          clean.addContour(contours[i]);
        }
      }
    clean.setStyle(this);
    return clean;
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
  public RShape toShape(){
    int numContours = countContours();

    RShape result = new RShape();
    for(int i=0;i<numContours;i++){
      RPoint[] newpoints = this.contours[i].getHandles();

      if(newpoints != null){
        result.addMoveTo(newpoints[0]);

        for(int j = 1; j < newpoints.length; j++){
          result.addLineTo(newpoints[j]);
        }

        if(contours[i].closed){
          result.addClose();
        }

        result.paths[i].setStyle(contours[i]);
      }
    }

    result.setStyle(this);
    return result;
  }

  /**
   * Use this to return the points of the polygon.  It returns the points in the way of an array of RPoint.
   * @eexample RPolygon_getHandles
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getHandles(){
    int numContours = countContours();
    if(numContours == 0){
      return null;
    }

    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numContours;i++){
      RPoint[] newPoints = contours[i].getHandles();
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
   * Use this to return the points of the polygon.  It returns the points in the way of an array of RPoint.
   * @eexample RPolygon_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getPoints(){
    int numContours = countContours();
    if(numContours == 0){
      return null;
    }

    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numContours;i++){
      RPoint[] newPoints = contours[i].getPoints();
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
   * Use this method to get the type of element this is.
   * @eexample RPolygon_getType
   * @return int, will allways return RGeomElem.POLYGON
   */
  public int getType(){
    return type;
  }

  /**
   * Use this method to get the area covered by the polygon.
   * @eexample getArea
   * @return float, the area covered by the polygon
   * @related draw ( )
   */
  public float getArea()
  {
    if( getNumPoints() < 3 )
      {
        return 0.0F ;
      }
    float ax = getX(0);
    float ay = getY(0);
    float area = 0.0F ;
    for( int i = 1 ; i < (getNumPoints()-1) ; i++ )
      {
        float bx = getX(i);
        float by = getY(i);
        float cx = getX(i+1);
        float cy = getY(i+1);
        float tarea = ((cx - bx)*(ay - by)) - ((ax - bx)*(cy - by));
        area += tarea ;
      }
    area = 0.5F*Math.abs(area);
    return area ;
  }

  /**
   * Use this method to draw the polygon.
   * @eexample drawPolygon
   * @param g PGraphics, the graphics object on which to draw the polygon
   * @related draw ( )
   */
  public void draw(PGraphics g){
    int numContours = countContours();
    if(numContours!=0){
      if(isIn(g)){
        if(!RG.ignoreStyles){
          saveContext(g);
          setContext(g);
        }

        // Check whether to draw the fill or not
        if(g.fill){
          // Since we are drawing the different tristrips we must turn off the stroke or make it the same color as the fill
          // NOTE: there's currently no way of drawing the outline of a mesh, since no information is kept about what vertices are at the edge

          // Save the information about the current stroke color and turn off
          boolean stroking = g.stroke;
          g.noStroke();

          // Save smoothing state and turn off
          int smoothing = g.smooth;
          try{
            if(smoothing > 0){
              g.noSmooth();
            }
          }catch(Exception e){
          }

          RMesh tempMesh = this.toMesh();
          tempMesh.draw(g);

          // Restore the old stroke color
          if(stroking) g.stroke(g.strokeColor);

          // Restore the old smoothing state
          try{
            if(smoothing > 0){
              g.smooth();
            }
          }catch(Exception e){
          }
        }

        // Check whether to draw the stroke or not
        if(g.stroke){
          for(int i=0;i<numContours;i++){
            contours[i].draw(g);
          }
        }

        if(!RG.ignoreStyles){
          restoreContext(g);
        }
      }
    }
  }

  public void draw(PApplet g){
    int numContours = countContours();
    if(numContours!=0){
      if(isIn(g)){
        if(!RG.ignoreStyles){
          saveContext(g);
          setContext(g);
        }

        // Check whether to draw the fill or not
        if(g.g.fill){
          // Since we are drawing the different tristrips we must turn off the stroke or make it the same color as the fill
          // NOTE: there's currently no way of drawing the outline of a mesh, since no information is kept about what vertices are at the edge

          // Save the information about the current stroke color and turn off
          boolean stroking = g.g.stroke;
          g.noStroke();

          // Save smoothing state and turn off
          int smoothing = g.g.smooth;
          try{
            if(smoothing > 0){
              g.noSmooth();
            }
          }catch(Exception e){
          }

          RMesh tempMesh = this.toMesh();
          if(tempMesh != null)
            tempMesh.draw(g);

          // Restore the old stroke color
          if(stroking) g.stroke(g.g.strokeColor);

          // Restore the old smoothing state
          try{
            if(smoothing > 0){
              g.smooth();
            }
          }catch(Exception e){
          }
        }

        // Check whether to draws the stroke or not
        if(g.g.stroke){
          for(int i=0;i<numContours;i++){
            contours[i].draw(g);
          }
        }

        if(!RG.ignoreStyles){
          restoreContext(g);
        }
      }
    }
  }


  /**
   * Use this method to get the intersection of this polygon with the polygon passed in as a parameter.
   * @eexample intersection
   * @param p RPolygon, the polygon with which to perform the intersection
   * @return RPolygon, the intersection of the two polygons
   * @related union ( )
   * @related xor ( )
   * @related diff ( )
   */
  public RPolygon intersection( RPolygon p ){
    RPolygon res = RClip.intersection( p, this );
    res.setStyle(this.getStyle());
    return res;
  }

  /**
   * Use this method to get the union of this polygon with the polygon passed in as a parameter.
   * @eexample union
   * @param p RPolygon, the polygon with which to perform the union
   * @return RPolygon, the union of the two polygons
   * @related intersection ( )
   * @related xor ( )
   * @related diff ( )
   */
  public RPolygon union( RPolygon p ){
    RPolygon res = RClip.union( p, this );
    res.setStyle(this.getStyle());
    return res;
  }

  /**
   * Use this method to get the xor of this polygon with the polygon passed in as a parameter.
   * @eexample xor
   * @param p RPolygon, the polygon with which to perform the xor
   * @return RPolygon, the xor of the two polygons
   * @related union ( )
   * @related intersection ( )
   * @related diff ( )
   */
  public RPolygon xor( RPolygon p ){
    RPolygon res = RClip.xor( p, this );
    res.setStyle(this.getStyle());
    return res;
  }

  /**
   * Use this method to get the difference between this polygon and the polygon passed in as a parameter.
   * @eexample diff
   * @param p RPolygon, the polygon with which to perform the difference
   * @return RPolygon, the difference of the two polygons
   * @related union ( )
   * @related xor ( )
   * @related intersection ( )
   */
  public RPolygon diff( RPolygon p ){
    RPolygon res = RClip.diff( this, p );
    res.setStyle(this.getStyle());
    return res;
  }

  /**
   * Use this method to get a rebuilt version of a given polygon by removing extra points and solving intersecting contours or holes.
   * @eexample RPolygon_update
   * @return RPolygon, the updated polygon
   * @related diff ( )
   * @related union ( )
   * @related xor ( )
   * @related intersection ( )
   */
  public RPolygon update(){
    return RClip.update( this );
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
   * Use this method to transform the polygon.
   * @eexample RPolygon_transform
   * @param m RMatrix, the matrix of the affine transformation to apply to the polygon
   */
  /*
    public void transform(RMatrix m){
    int numContours = countContours();
    if(numContours!=0){
    for(int i=0;i<numContours;i++){
    contours[i].transform(m);
    }
    }
    }
  */
  // ----------------------
  // --- Private Methods ---
  // ----------------------


  /**
   * Remove all of the points.  Creates an empty polygon.
   */
  protected void clear(){
    this.contours = null;
  }

  /**
   * Add a point to the first inner polygon.
   */
  protected void add( float x, float y ){
    if (contours == null) {
      this.append(new RContour());
    }
    this.contours[0].append(new RPoint(x,y));
  }

  /**
   * Add a point to the first inner polygon.
   */
  protected void add( RPoint p ){
    if (contours == null) {
      this.append(new RContour());
    }
    this.contours[0].append(p);
  }

  /**
   * Add an inner polygon to this polygon - assumes that adding polygon does not
   * have any inner polygons.
   */
  protected void add( RPolygon p ){
    /*if (this.contours.length > 0 && this.isHole){
      throw new IllegalStateException("Cannot add polys to something designated as a hole.");
      }*/
    RContour c = new RContour();
    for(int i=0;i<p.getNumPoints();i++){
      c.addPoint(p.getX(i),p.getY(i));
    }
    this.append(c);
  }

  /**
   * Add an inner polygon to this polygon - assumes that adding polygon does not
   * have any inner polygons.
   */
  protected void add( RContour c ){
    /*if (this.contours.length > 0 && this.isHole){
      throw new IllegalStateException("Cannot add polys to something designated as a hole.");
      }*/
    this.append(c);
  }

  /**
   * Return true if the polygon is empty
   */
  protected boolean isEmpty(){
    return (this.contours == null);
  }

  /**
   * Returns the bounding box of the polygon.
   */
  protected RRectangle getBBox(){
    if( this.contours == null )
      {
        return new RRectangle();
      }
    else if( this.contours.length == 1 )
      {

        float xmin =  Float.MAX_VALUE ;
        float ymin =  Float.MAX_VALUE ;
        float xmax = -Float.MAX_VALUE ;
        float ymax = -Float.MAX_VALUE ;

        if ( this.contours[0].points == null )
          {
            return new RRectangle();
          }

        for( int i = 0 ; i < this.contours[0].points.length ; i++ )
          {
            float x = this.contours[0].points[i].getX();
            float y = this.contours[0].points[i].getY();
            if( x < xmin ) xmin = x;
            if( x > xmax ) xmax = x;
            if( y < ymin ) ymin = y;
            if( y > ymax ) ymax = y;
          }

        return new RRectangle( xmin, ymin, (xmax-xmin), (ymax-ymin) );
      }
    else
      {
        throw new UnsupportedOperationException("getBounds not supported on complex poly.");
      }
  }

  /**
   * Returns the polygon at this index.
   */
  protected RPolygon getInnerPoly( int polyIndex ){
    return new RPolygon(this.contours[polyIndex]);
  }

  /**
   * Returns the number of inner polygons - inner polygons are assumed to return one here.
   */
  protected int getNumInnerPoly(){
    if (this.contours == null){
      return 0;
    }
    return this.contours.length;
  }

  /**
   * Return the number points of the first inner polygon
   */
  protected int getNumPoints(){
    if (this.contours == null){
      return 0;
    }
    if (this.contours[0].points == null){
      return 0;
    }
    return this.contours[0].points.length;
  }

  /**
   * Return the X value of the point at the index in the first inner polygon
   */
  protected float getX( int index ){
    if (this.contours == null){
      return 0;
    }
    return this.contours[0].points[index].x;
  }

  /**
   * Return the Y value of the point at the index in the first inner polygon
   */
  protected float getY( int index ){
    if (this.contours == null){
      return 0;
    }
    return this.contours[0].points[index].y;
  }

  /**
   * Return true if this polygon is a hole.  Holes are assumed to be inner polygons of
   * a more complex polygon.
   *
   * @throws IllegalStateException if called on a complex polygon.
   */
  public boolean isHole(){
    if( this.contours == null || this.contours.length > 1 )
      {
        throw new IllegalStateException( "Cannot call on a poly made up of more than one poly." );
      }
    return this.contours[0].isHole ;
  }

  /**
   * Set whether or not this polygon is a hole.  Cannot be called on a complex polygon.
   *
   * @throws IllegalStateException if called on a complex polygon.
   */
  protected void setIsHole( boolean isHole ){
    if( this.contours==null || this.contours.length > 1 )
      {
        throw new IllegalStateException( "Cannot call on a poly made up of more than one poly." );
      }
    this.contours[0].isHole = isHole ;
  }

  /**
   * Return true if the given inner polygon is contributing to the set operation.
   * This method should NOT be used outside the Clip algorithm.
   */
  protected boolean isContributing( int polyIndex ){
    return this.contours[polyIndex].isContributing;
  }

  /**
   * Set whether or not this inner polygon is constributing to the set operation.
   * This method should NOT be used outside the Clip algorithm.
   */
  protected void setContributing( int polyIndex, boolean contributes ){
    /*
    if( this.contours.length != 1 )
      {
        throw new IllegalStateException( "Only applies to polys of size 1" );
      }
    */
    this.contours[polyIndex].isContributing = contributes;
  }

  private void append(RContour nextcontour)
  {
    RContour[] newcontours;
    if(contours==null){
      newcontours = new RContour[1];
      newcontours[0] = nextcontour;
      currentContour = 0;
    }else{
      newcontours = new RContour[this.contours.length+1];
      System.arraycopy(this.contours,0,newcontours,0,this.contours.length);
      newcontours[this.contours.length]=nextcontour;
      currentContour++;
    }
    this.contours=newcontours;
  }
}
