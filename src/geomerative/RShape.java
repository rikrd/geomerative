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
 * RShape is a reduced interface for creating, holding and drawing complex Shapes. Shapes are groups of one or more subshapes (RSubshape).  Shapes can be selfintersecting and can contain holes.  This interface also allows you to transform shapes into polygons by segmenting the curves forming the shape.
 * @eexample RShape
 * @usage Geometry
 * @related RSubshape
 */
public class RShape extends RGeomElem
{
  /**
   * @invisible
   */
  public int type = RGeomElem.SHAPE;
  
  /**
   * Array of RSubshape objects holding the subshapes of the polygon. 
   * @eexample subshapes
   * @related RSubshape
   * @related countSubshapes ( )
   * @related addSubshape ( )
   */
  public RSubshape[] subshapes;
  protected int currentSubshape = 0;
  
  // ----------------------
  // --- Public Methods ---
  // ----------------------
  
  /**
   * Use this method to create a new empty shape.
   * @eexample RShape
   */
  public RShape(){
    this.subshapes= null;
    type = RGeomElem.SHAPE;
  }
  
  public RShape(RSubshape newsubshape){
    this.append(newsubshape);
    type = RGeomElem.SHAPE;
  }
  
  public RShape(RShape s){
    for(int i=0;i<s.countSubshapes();i++){
      this.append(new RSubshape(s.subshapes[i]));
    }
    type = RGeomElem.SHAPE;

    setStyle(s);
  }

  /**
   * Use this method to create a new ring polygon. 
   * @eexample createRing
   * @param radiusBig float, the outter radius of the ring polygon
   * @param radiusSmall float, the inner radius of the ring polygon
   * @param detail int, the number of vertices on each contour of the ring
   * @return RShape, the ring polygon newly created
   */
  static public RShape createRing(float x, float y, float radiusBig, float radiusSmall){
    RShape ring = new RShape();
    RShape outer = RShape.createCircle(x, y, radiusBig);
    RShape inner = RShape.createCircle(x, y, -radiusSmall);
    
    ring.addSubshape(outer.subshapes[0]);
    ring.addSubshape(inner.subshapes[0]);

    return ring;
  }

  /**
   * Use this method to create a new starform polygon. 
   * @eexample createStar
   * @param radiusBig float, the outter radius of the star polygon
   * @param radiusSmall float, the inner radius of the star polygon
   * @param spikes int, the amount of spikes on the star polygon
   * @return RShape, the starform polygon newly created
   */
  static public RShape createStar(float x, float y, float radiusBig, float radiusSmall, int spikes){
    RShape star = new RShape();

    star.addMoveTo( x - radiusBig, y );
    star.addLineTo( x - (float)(radiusSmall*Math.cos(Math.PI/spikes)), y - (float)(radiusSmall*Math.sin(Math.PI/spikes)));

    for(int i=2;i<2*spikes;i+=2){
      star.addLineTo( x - (float)(radiusBig*Math.cos(Math.PI*i/spikes)), y - (float)(radiusBig*Math.sin(Math.PI*i/spikes)));
      star.addLineTo( x - (float)(radiusSmall*Math.cos(Math.PI*(i+1)/spikes)), y - (float)(radiusSmall*Math.sin(Math.PI*(i+1)/spikes)));
    }

    star.addClose();

    return star;
  }
  
  /**
   * Use this method to create a new circle shape. 
   * @eexample createRectangle
   * @param x float, the x position of the rectangle
   * @param y float, the y position of the rectangle
   * @param w float, the width of the rectangle
   * @param h float, the height of the rectangle
   * @return RShape, the rectangular shape just created
   */
  static public RShape createRectangle(float x, float y, float w, float h){
    RShape rect = new RShape();
    rect.addMoveTo(x, y);
    rect.addLineTo(x+w, y);
    rect.addLineTo(x+w, y+h);
    rect.addLineTo(x, y+h);
    rect.addLineTo(x, y);
    return rect;
  }
  
  /**
   * Use this method to create a new elliptical shape. 
   * @eexample createEllipse
   * @param x float, the x position of the ellipse
   * @param y float, the y position of the ellipse
   * @param rx float, the horizontal radius of the ellipse
   * @param ry float, the vertical radius of the ellipse
   * @return RShape, the elliptical shape just created
   */
  static public RShape createEllipse(float x, float y, float rx, float ry){
    RPoint center = new RPoint(x,y);
    RShape circle = new RShape();
    float kx = (((8F/(float)Math.sqrt(2F))-4F)/3F) * rx;
    float ky = (((8F/(float)Math.sqrt(2F))-4F)/3F) * ry;
    circle.addMoveTo(center.x, center.y - ry);
    circle.addBezierTo(center.x+kx, center.y-ry, center.x+rx, center.y-ky, center.x+rx, center.y);
    circle.addBezierTo(center.x+rx, center.y+ky, center.x+kx, center.y+ry, center.x, center.y+ry);
    circle.addBezierTo(center.x-kx, center.y+ry, center.x-rx, center.y+ky, center.x-rx, center.y);
    circle.addBezierTo(center.x-rx, center.y-ky, center.x-kx, center.y-ry, center.x, center.y-ry);
    circle.addClose();
    return circle;
  }

  static public RShape createCircle(float x, float y, float r){
    return createEllipse(x, y, r, r);
  }
  
  /**
   * Use this method to get the centroid of the element.
   * @eexample RGroup_getCentroid
   * @return RPoint, the centroid point of the element
   * @related getBounds ( )
   * @related getCenter ( )
   */
  public RPoint getCentroid(){
    RPoint bestCentroid = new RPoint();
    float bestArea = Float.NEGATIVE_INFINITY;
    if(subshapes != null){
      for(int i=0;i<subshapes.length;i++)
        {
          float area = Math.abs(subshapes[i].getArea());
          if(area > bestArea){
            bestArea = area;
            bestCentroid = subshapes[i].getCentroid();
          }
        }
      return bestCentroid;
    }
    return null;
  }
  
  /**
   * Use this method to count the number of subshapes in the polygon. 
   * @eexample countSubshapes
   * @return int, the number countours in the polygon.
   * @related addSubshape ( )
   */
  public int countSubshapes(){
    if(this.subshapes==null){
      return 0;
    }
    
    return this.subshapes.length;
  }
  
  /**
   * Use this method to add a new shape.  The subshapes of the shape we are adding will simply be added to the current shape.
   * @eexample addShape
   * @param s RShape, the shape to be added.
   * @related setSubshape ( )
   * @related addMoveTo ( )
   * @invisible
   */
  public void addShape(RShape s){
    for(int i=0;i<s.countSubshapes();i++){
      this.append(s.subshapes[i]);
    }
  }
  
  /**
   * Use this method to create a new subshape.  The first point of the new subshape will be set to (0,0).  Use addMoveTo ( ) in order to add a new subshape with a different first point.
   * @eexample addSubshape
   * @param s RSubshape, the subshape to be added.
   * @related setSubshape ( )
   * @related addMoveTo ( )
   */
  public void addSubshape(){
    this.append(new RSubshape());
  }
  
  public void addSubshape(RSubshape s){
    this.append(s);
  }
  
  /**
   * Use this method to set the current subshape. 
   * @eexample setSubshape
   * @related addMoveTo ( )
   * @related addLineTo ( )
   * @related addQuadTo ( )
   * @related addBezierTo ( )
   * @related addSubshape ( )
   */
  public void setSubshape(int indSubshape){
    this.currentSubshape = indSubshape;
  }
  
  /**
   * Use this method to add a new moveTo command to the shape.  The command moveTo acts different to normal commands, in order to make a better analogy to its borthers classes Polygon and Mesh.  MoveTo creates a new subshape in the shape.  It's similar to adding a new contour to a polygon.
   * @eexample addMoveTo
   * @param endx float, the x coordinate of the first point for the new subshape.
   * @param endy float, the y coordinate of the first point for the new subshape.
   * @related addLineTo ( )
   * @related addQuadTo ( )
   * @related addBezierTo ( )
   * @related addSubshape ( )
   * @related setSubshape ( )
   */
  public void addMoveTo(float endx, float endy){
    if (subshapes == null){
      this.append(new RSubshape(endx,endy));
    }else if(subshapes[currentSubshape].countCommands() == 0){
      this.subshapes[currentSubshape].lastPoint = new RPoint(endx,endy);
    }else{
      this.append(new RSubshape(endx,endy));
    }
  }

  public void addMoveTo(RPoint p){
    addMoveTo(p.x, p.y);
  }
  
  /**
   * Use this method to add a new lineTo command to the current subshape.  This will add a line from the last point added to the point passed as argument.
   * @eexample addLineTo
   * @param endx float, the x coordinate of the ending point of the line.
   * @param endy float, the y coordinate of the ending point of the line.
   * @related addMoveTo ( )
   * @related addQuadTo ( )
   * @related addBezierTo ( )
   * @related addSubshape ( )
   * @related setSubshape ( )
   */
  public void addLineTo(float endx, float endy){
    if (subshapes == null) {
      this.append(new RSubshape());
    }
    this.subshapes[currentSubshape].addLineTo(endx, endy);
  }

  public void addLineTo(RPoint p){
    addLineTo(p.x, p.y);
  }
  
  /**
   * Use this method to add a new quadTo command to the current subshape.  This will add a quadratic bezier from the last point added with the control and ending points passed as arguments.
   * @eexample addQuadTo
   * @param cp1x float, the x coordinate of the control point of the bezier.
   * @param cp1y float, the y coordinate of the control point of the bezier.
   * @param endx float, the x coordinate of the ending point of the bezier.
   * @param endy float, the y coordinate of the ending point of the bezier.
   * @related addMoveTo ( )
   * @related addLineTo ( )
   * @related addBezierTo ( )
   * @related addSubshape ( )
   * @related setSubshape ( )
   */
  public void addQuadTo(float cp1x, float cp1y, float endx, float endy){
    if (subshapes == null) {
      this.append(new RSubshape());
    }
    this.subshapes[currentSubshape].addQuadTo(cp1x,cp1y,endx,endy);
  }

  public void addQuadTo(RPoint p1, RPoint p2){
    addQuadTo(p1.x, p1.y, p2.x, p2.y);
  }
  
  /**
   * Use this method to add a new bezierTo command to the current subshape.  This will add a cubic bezier from the last point added with the control and ending points passed as arguments.
   * @eexample addArcTo
   * @param cp1x float, the x coordinate of the first control point of the bezier.
   * @param cp1y float, the y coordinate of the first control point of the bezier.
   * @param cp2x float, the x coordinate of the second control point of the bezier.
   * @param cp2y float, the y coordinate of the second control point of the bezier.
   * @param endx float, the x coordinate of the ending point of the bezier.
   * @param endy float, the y coordinate of the ending point of the bezier.
   * @related addMoveTo ( )
   * @related addLineTo ( )
   * @related addQuadTo ( )
   * @related addSubshape ( )
   * @related setSubshape ( )
   */
  public void addBezierTo(float cp1x, float cp1y, float cp2x, float cp2y, float endx, float endy){
    if (subshapes == null) {
      this.append(new RSubshape());
    }
    this.subshapes[currentSubshape].addBezierTo(cp1x,cp1y,cp2x,cp2y,endx,endy);
  }

  public void addBezierTo(RPoint p1, RPoint p2, RPoint p3){
    addBezierTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
  }
  
  public void addClose(){
    if (subshapes == null) {
      this.append(new RSubshape());
    }
    this.subshapes[currentSubshape].addClose();
  }
  
  /**
   * Use this method to create a new mesh from a given polygon. 
   * @eexample toMesh
   * @return RMesh, the mesh made of tristrips resulting of a tesselation of the polygonization followd by tesselation of the shape.
   * @related draw ( )
   */
  public RMesh toMesh(){
    return toPolygon().toMesh();
  }
  
  /**
   * Use this method to create a new polygon from a given shape. 
   * @eexample toPolygon
   * @return RPolygon, the polygon resulting of the segmentation of the commands in each subshape.
   * @related draw ( )
   */
  public RPolygon toPolygon(){
    int numSubshapes = countSubshapes();
    
    RPolygon result = new RPolygon();
    for(int i=0;i<numSubshapes;i++){
      RPoint[] newpoints = this.subshapes[i].getPoints();
      RContour c = new RContour(newpoints);
      c.closed = subshapes[i].closed;
      c.setStyle(subshapes[i]);
      result.addContour(c);
    }
    
    result.setStyle(this);
    return result;
  }
  
  /**
   * @invisible
   */
  public RShape toShape(){
    return this;
  }

  /**
   * Use this method to get the intersection of the given polygon with the polygon passed as atribute.
   * @eexample intersection
   * @param p RShape, the polygon with which to perform the intersection
   * @return RShape, the intersection of the two polygons
   * @related union ( )
   * @related xor ( )
   * @related diff ( )
   */
  public RShape intersection( RShape p ){
    return RClip.intersection( p.toPolygon(), this.toPolygon() ).toShape();
  }
  
  /**
   * Use this method to get the union of the given polygon with the polygon passed as atribute.
   * @eexample union
   * @param p RShape, the polygon with which to perform the union
   * @return RShape, the union of the two polygons
   * @related intersection ( )
   * @related xor ( )
   * @related diff ( )
   */
  public RShape union( RShape p ){
    return RClip.union( p.toPolygon(), this.toPolygon() ).toShape();
  }
  
  /**
   * Use this method to get the xor of the given polygon with the polygon passed as atribute.
   * @eexample xor
   * @param p RShape, the polygon with which to perform the xor
   * @return RShape, the xor of the two polygons
   * @related union ( )
   * @related intersection ( )
   * @related diff ( )
   */
  public RShape xor( RShape p ){
    return RClip.xor( p.toPolygon(), this.toPolygon() ).toShape();
  }
  
  /**
   * Use this method to get the difference of the given polygon with the polygon passed as atribute.
   * @eexample diff
   * @param p RShape, the polygon with which to perform the difference
   * @return RShape, the difference of the two polygons
   * @related union ( )
   * @related xor ( )
   * @related intersection ( )
   */	
  public RShape diff( RShape p ){
    return RClip.diff( this.toPolygon(), p.toPolygon() ).toShape();
  }
    
  /**
   * Use this to return the start, control and end points of the shape.  It returns the points in the way of an array of RPoint.
   * @eexample RShape_getHandles
   * @return RPoint[], the start, control and end points returned in an array.
   * */
  public RPoint[] getHandles(){
    int numSubshapes = countSubshapes();
    if(numSubshapes == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numSubshapes;i++){
      RPoint[] newPoints = subshapes[i].getHandles();
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
   * Use this to return a point on the curve given a certain advancement.  It returns the point in the way of an RPoint.
   * @eexample RShape_getPoints
   * @return RPoint[], the point on the curve.
   * */
  public RPoint getPoint(float t){
    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];

    return subshapes[indOfElement].getPoint(advOfElement);
  }

  /**
   * Use this to return the points on the curve of the shape.  It returns the point in the way of an RPoint.
   * @eexample RShape_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getPoints(){
    int numSubshapes = countSubshapes();
    if(numSubshapes == 0){
      return null;
    }

    RCommand.segmentAccOffset = RCommand.segmentOffset;    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numSubshapes;i++){
      RPoint[] newPoints = subshapes[i].getPoints();
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
   * Use this to return a point on the curve given a certain advancement.  It returns the point in the way of an RPoint.
   * @eexample RShape_getTangents
   * @return RPoint[], the point on the curve.
   * */
  public RPoint getTangent(float t){
    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];

    return subshapes[indOfElement].getTangent(advOfElement);
  }

  /**
   * Use this to return a specific tangent on the curve.  It returns true if the point passed as a parameter is inside the shape.  Implementation taken from: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
   * @param RPoint p, the point for which to test containement..
   * @return bool, true if the point is in the path.
   * */
  public boolean contains(RPoint p){
    float testx = p.x;
    float testy = p.y;

    // Test for containment in bounding box
    RContour bbox = getBounds();
    float xmin = bbox.points[0].x;
    float xmax = bbox.points[2].x;

    float ymin = bbox.points[0].y;
    float ymax = bbox.points[2].y;
    
    if( (testx < xmin) || (testx > xmax) || (testy < ymin) || (testy > ymax)){
      return false;
    }

    // Test for containment in shape
    RPoint[][] pointpaths = getPointPaths();
    
    if(pointpaths == null){
      return false;
    }
    
    RPoint[] verts = pointpaths[0];
    for(int k=1;k<pointpaths.length;k++){
      verts = (RPoint[])RGeomerative.parent().append(verts, new RPoint(0F, 0F));
      verts = (RPoint[])RGeomerative.parent().concat(verts, pointpaths[k]);
    }
    verts = (RPoint[])RGeomerative.parent().append(verts, new RPoint(0F, 0F));
    
    if(verts == null){
      return false;
    }
    
    int nvert = verts.length;
    int i, j = 0;
    boolean c = false;
    for (i = 0, j = nvert-1; i < nvert; j = i++) {
      if ( ((verts[i].y > testy) != (verts[j].y>testy)) &&
           (testx < (verts[j].x-verts[i].x) * (testy-verts[i].y) / (verts[j].y-verts[i].y) + verts[i].x) ){
        c = !c;
      }
    }
    return c;
  }

  /**
   * Use this to return the points on the curve of the shape.  It returns the point in the way of an RPoint.
   * @eexample RShape_getTangents
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getTangents(){
    int numSubshapes = countSubshapes();
    if(numSubshapes == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numSubshapes;i++){
      RPoint[] newPoints = subshapes[i].getTangents();
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
   * Use this to return the points of each path of the group.  It returns the points in the way of an array of array of RPoint.
   * @eexample RGroup_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[][] getPointPaths(){
    int numSubshapes = countSubshapes();
    if(numSubshapes == 0){
      return null;
    }
    
    RPoint[][] result=null;
    RPoint[][] newresult=null;
    for(int i=0;i<numSubshapes;i++){
      RPoint[][] newPointPaths = subshapes[i].getPointPaths();
      if(newPointPaths != null){
        if(result == null){
          result = new RPoint[newPointPaths.length][];
          System.arraycopy(newPointPaths,0,result,0,newPointPaths.length);
        }else{
          newresult = new RPoint[result.length + newPointPaths.length][];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPointPaths,0,newresult,result.length,newPointPaths.length);
          result = newresult;
        }
      }
    }
    return result;    
  }

  public RPoint[][] getHandlePaths(){
    int numSubshapes = countSubshapes();
    if(numSubshapes == 0){
      return null;
    }
    
    RPoint[][] result=null;
    RPoint[][] newresult=null;
    for(int i=0;i<numSubshapes;i++){
      RPoint[][] newHandlePaths = subshapes[i].getHandlePaths();
      if(newHandlePaths != null){
        if(result == null){
          result = new RPoint[newHandlePaths.length][];
          System.arraycopy(newHandlePaths,0,result,0,newHandlePaths.length);
        }else{
          newresult = new RPoint[result.length + newHandlePaths.length][];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newHandlePaths,0,newresult,result.length,newHandlePaths.length);
          result = newresult;
        }
      }
    }
    return result;    
  }

  public RPoint[][] getTangentPaths(){
    int numSubshapes = countSubshapes();
    if(numSubshapes == 0){
      return null;
    }
    
    RPoint[][] result=null;
    RPoint[][] newresult=null;
    for(int i=0;i<numSubshapes;i++){
      RPoint[][] newTangentPaths = subshapes[i].getTangentPaths();
      if(newTangentPaths != null){
        if(result == null){
          result = new RPoint[newTangentPaths.length][];
          System.arraycopy(newTangentPaths,0,result,0,newTangentPaths.length);
        }else{
          newresult = new RPoint[result.length + newTangentPaths.length][];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newTangentPaths,0,newresult,result.length,newTangentPaths.length);
          result = newresult;
        }
      }
    }
    return result;    
  }
  
  public RShape[] splitAll(float t){
    RShape[] result = new RShape[2];
    result[0] = new RShape();
    result[1] = new RShape();
    
    for(int i=0; i<countSubshapes(); i++){
      RSubshape[] splittedSubshapes = subshapes[i].split(t);
      if(splittedSubshapes != null){
        result[0].addSubshape(splittedSubshapes[0]);
        result[1].addSubshape(splittedSubshapes[1]);
      }
    }
    
    result[0].setStyle(this);
    result[1].setStyle(this);
    return result;
  }

  /**
   * Use this to insert a split point into the shape.
   * @eexample insertHandle
   * @param t float, the parameter of advancement on the curve. t must have values between 0 and 1.
   * */
  public void insertHandle(float t){
    if((t == 0F) || (t == 1F)){
      return;
    }

    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];

    subshapes[indOfElement].insertHandle(advOfElement);
    
    // Clear the cache
    lenCurves = null;
    lenCurve = -1F;

    return;
  }

  /**
   * Use this to insert a split point into each command of the shape.
   * @eexample insertHandleAll
   * @param t float, the parameter of advancement on the curve. t must have values between 0 and 1.
   * */
  public void insertHandleAll(float t){
    if((t == 0F) || (t == 1F)){
      return;
    }
    
    int numSubshapes = countSubshapes();
    if(numSubshapes == 0){
      return;
    }
    
    for( int i = 0 ; i < numSubshapes; i++ ) {
      subshapes[i].insertHandleAll(t);
    }

    // Clear the cache
    lenCurves = null;
    lenCurve = -1F;
    
    return;
  }

  public RShape[] split(float t){
    RShape[] result = new RShape[2];
    result[0] = new RShape();
    result[1] = new RShape();

    int numSubshapes = countSubshapes();
    if(numSubshapes == 0){
      return null;
    }

    if(t == 0.0F){ 
      result[0] = new RShape();
      result[0].setStyle(this);

      result[1] = new RShape(this);
      result[1].setStyle(this);

      return result;
    }
    
    if(t == 1.0F){
      result[0] = new RShape(this);
      result[0].setStyle(this);
    
      result[1] = new RShape();
      result[1].setStyle(this);

      return result;
    }
    
    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];
    
    RSubshape[] splittedShapes = subshapes[indOfElement].split(advOfElement);
    
    result[0] = new RShape();
    for(int i = 0; i<indOfElement; i++){
      result[0].addSubshape(new RSubshape(subshapes[i]));
    }
    result[0].addSubshape(new RSubshape(splittedShapes[0]));
    result[0].setStyle(this);

    result[1] = new RShape();
    result[1].addSubshape(new RSubshape(splittedShapes[1]));
    for(int i = indOfElement + 1; i < countSubshapes(); i++){
      result[1].addSubshape(new RSubshape(subshapes[i]));
    }
    result[1].setStyle(this);
    
    return result;
  }

  /**
   * Use this method to adapt a group of of figures to a shape.
   * @eexample RGroup_adapt
   * @param RSubshape sshp, the subshape to which to adapt
   * @return RGroup, the adapted group
   */
  public void adapt(RShape shp, float wght, float lngthOffset) throws RuntimeException{
    RContour c = this.getBounds();
    float xmin = c.points[0].x;
    float xmax = c.points[2].x;
    
    switch(RGeomerative.adaptorType){
    case RGeomerative.BYPOINT:
      RPoint[] ps = this.getHandles();
      if(ps != null){
        for(int k=0;k<ps.length;k++){
          float px = ps[k].x;
          float py = ps[k].y;
          
          float t = ((px-xmin)/(xmax-xmin) + lngthOffset) % 1.001F;
          float amp = (py);
          
          RPoint tg = shp.getTangent(t);
          RPoint p = shp.getPoint(t);
          float angle = (float)Math.atan2(tg.y, tg.x) - (float)Math.PI/2F;
          
          ps[k].x = p.x + wght*amp*(float)Math.cos(angle);
          ps[k].y = p.y + wght*amp*(float)Math.sin(angle);
        }
      }
      break;
    case RGeomerative.BYELEMENTINDEX:
    case RGeomerative.BYELEMENTPOSITION:
      RContour elemc = shp.getBounds();
      
      float px = (elemc.points[2].x + elemc.points[0].x) / 2F;
      float py = (elemc.points[2].y - elemc.points[0].y) / 2F;
      float t = ((px-xmin)/(xmax-xmin) + lngthOffset ) % 1F;
      
      RPoint tg = shp.getTangent(t);
      RPoint p = shp.getPoint(t);
      float angle = (float)Math.atan2(tg.y, tg.x);
      
      RPoint pletter = new RPoint(px,py);
      p.sub(pletter);
      
      RMatrix mtx = new RMatrix();
      mtx.translate(p);
      mtx.rotate(angle,pletter);
      mtx.scale(wght,pletter);
      
      this.transform(mtx);
      break;
      
    default:
      throw new RuntimeException("Unknown adaptor type : "+RGeomerative.adaptorType+". The method RGeomerative.setAdaptor() only accepts RGeomerative.BYPOINT or RGeomerative.BYELEMENT as parameter values.");
    }
  }
  
  public void adapt(RShape shp) throws RuntimeException{
    adapt(shp, RGeomerative.adaptorScale, RGeomerative.adaptorLengthOffset);
  }

  /**
   * Use this method to get the type of element this is.
   * @eexample RShape_getType
   * @return int, will allways return RGeomElem.SHAPE
   */
  public int getType(){
    return type;
  }
  
  public void print(){
    System.out.println("subshapes [count " + this.countSubshapes() + "]: ");
    for(int i=0;i<countSubshapes();i++)
      {
        System.out.println("--- subshape "+i+" ---");
        subshapes[i].print();
        System.out.println("---------------");
      }
  }
  
  /**
   * Use this method to draw the shape. 
   * @eexample drawShape
   * @param g PGraphics, the graphics object on which to draw the shape
   */
  public void draw(PGraphics g){
    try{
      Class declaringClass = g.getClass().getMethod("breakShape", null).getDeclaringClass();
      if(declaringClass != g.getClass()){
        // The backend does not implement breakShape
        drawUsingInternalTesselator(g);
      }else{
        // The backend does implement breakShape
        drawUsingBreakShape(g);        
      }
    }catch(NoSuchMethodException e){   
    }
  }

  public void draw(PApplet g){
    try{
      Class declaringClass = g.g.getClass().getMethod("breakShape", null).getDeclaringClass();
      if(declaringClass != g.g.getClass()){
        // The backend does not implement breakShape
        drawUsingInternalTesselator(g);
      }else{
        // The backend does implement breakShape
        drawUsingBreakShape(g);        
      }
    }catch(NoSuchMethodException e){   
    }
  }
  
  // ----------------------
  // --- Private Methods ---
  // ----------------------

  protected void calculateCurveLengths(){
    lenCurves = new float[countSubshapes()];
    lenCurve = 0F;
    for(int i=0;i<countSubshapes();i++){
      lenCurves[i] = subshapes[i].getCurveLength();
      lenCurve += lenCurves[i];
    }  
  }

  private float[] indAndAdvAt(float t){
    int indOfElement = 0;
    float[] lengthsCurves = getCurveLengths();
    float lengthCurve = getCurveLength();

    /* Calculate the amount of advancement t mapped to each command */
    /* We use a simple algorithm where we give to each command the same amount of advancement */
    /* A more useful way would be to give to each command an advancement proportional to the length of the command */
    /* Old method with uniform advancement per command
       float advPerCommand;
       advPerCommand = 1F / numSubshapes;
       indCommand = (int)(Math.floor(t / advPerCommand)) % numSubshapes;
       advOfCommand = (t*numSubshapes - indCommand);
    */
    
    float accumulatedAdvancement = lengthsCurves[indOfElement] / lengthCurve;
    float prevAccumulatedAdvancement = 0F;
    
    /* Find in what command the advancement point is  */
    while(t > accumulatedAdvancement){
      indOfElement++;
      prevAccumulatedAdvancement = accumulatedAdvancement;
      accumulatedAdvancement += (lengthsCurves[indOfElement] / lengthCurve);
    }
    
    float advOfElement = (t-prevAccumulatedAdvancement) / (lengthsCurves[indOfElement] / lengthCurve);

    float[] indAndAdv = new float[2];

    indAndAdv[0] = indOfElement;
    indAndAdv[1] = advOfElement;
    
    return indAndAdv;
  }
  
  
  
  private void append(RSubshape nextsubshape)
  {
    RSubshape[] newsubshapes;
    if(subshapes==null){
      newsubshapes = new RSubshape[1];
      newsubshapes[0] = nextsubshape;
      currentSubshape = 0;
    }else{
      newsubshapes = new RSubshape[this.subshapes.length+1];
      System.arraycopy(this.subshapes,0,newsubshapes,0,this.subshapes.length);
      newsubshapes[this.subshapes.length]=nextsubshape;
      currentSubshape++;
    }
    this.subshapes=newsubshapes;
  }

  private void drawUsingInternalTesselator(PGraphics g){
    int numSubshapes = countSubshapes();
    
    if(numSubshapes!=0){
      if(isIn(g)) {
        if(!RGeomerative.ignoreStyles){
          saveContext(g);
          setContext(g);
        }

        // Save the information about the current context
        boolean strokeBefore = g.stroke;
        int strokeColorBefore = g.strokeColor;
        float strokeWeightBefore = g.strokeWeight;      
        boolean smoothBefore = g.smooth;
        boolean fillBefore = g.fill;
        int fillColorBefore = g.fillColor;

        // By default always drawy with an ADAPTATIVE segmentator
        int lastSegmentator = RCommand.segmentType;
        RCommand.setSegmentator(RCommand.ADAPTATIVE);
        
        // Check whether to draw the fill or not
        if(g.fill){
          // Since we are drawing the different tristrips we must turn off the stroke or make it the same color as the fill
          // NOTE: there's currently no way of drawing the outline of a mesh, since no information is kept about what vertices are at the edge

          // This is here because when rendering meshes we get unwanted lines between the triangles
          g.noStroke();
          try{
            g.noSmooth();
          }catch(Exception e){}
          
          RMesh tempMesh = this.toMesh();
          tempMesh.draw(g);
          
          // Restore the old context
          g.stroke(strokeColorBefore);
          if(!strokeBefore){
            g.noStroke();
          }
          
          try{
            if(smoothBefore){
              g.smooth();
            }
          }catch(Exception e){}
        }
        
        // Check whether to draw the stroke
        g.noFill();
        if(!strokeBefore){
          // If there is no stroke to draw
          // we will still draw one the color of the fill in order to have antialiasing
          g.stroke(g.fillColor);
          g.strokeWeight(1F);
        }
          
        for(int i=0;i<numSubshapes;i++){
          subshapes[i].draw(g);
        }

        // Restore the fill state and stroke state and color
        if(fillBefore){
          g.fill(fillColorBefore);
        } else {
          g.noFill();
        }
        g.strokeWeight(strokeWeightBefore);
        g.stroke(strokeColorBefore);
        if(!strokeBefore){
          g.noStroke();
        }
        
        // Restore the user set segmentator
        RCommand.setSegmentator(lastSegmentator);

        if(!RGeomerative.ignoreStyles){
          restoreContext(g);
        }
      }
    }
  }

  private void drawUsingInternalTesselator(PApplet p){
    int numSubshapes = countSubshapes();
    
    if(numSubshapes!=0){
      if(isIn(p)) {
        if(!RGeomerative.ignoreStyles){
          saveContext(p);
          setContext(p);
        }

        // Save the information about the current context
        boolean strokeBefore = p.g.stroke;
        int strokeColorBefore = p.g.strokeColor;
        float strokeWeightBefore = p.g.strokeWeight;      
        boolean smoothBefore = p.g.smooth;
        boolean fillBefore = p.g.fill;
        int fillColorBefore = p.g.fillColor;

        // By default always drawy with an ADAPTATIVE segmentator
        int lastSegmentator = RCommand.segmentType;
        RCommand.setSegmentator(RCommand.ADAPTATIVE);
        
        // Check whether to draw the fill or not
        if(p.g.fill){
          // Since we are drawing the different tristrips we must turn off the stroke or make it the same color as the fill
          // NOTE: there's currently no way of drawing the outline of a mesh, since no information is kept about what vertices are at the edge

          // This is here because when rendering meshes we get unwanted lines between the triangles
          p.noStroke();
          try{
            p.noSmooth();
          }catch(Exception e){}
          
          RMesh tempMesh = this.toMesh();
          if ( tempMesh != null ){
            tempMesh.draw(p);
          }
          
          // Restore the old context
          p.stroke(strokeColorBefore);
          p.strokeWeight(strokeWeightBefore);
          if(!strokeBefore){
            p.noStroke();
          }
          
          try{
            if(smoothBefore){
              p.smooth();
            }
          }catch(Exception e){}
        }
        
        
        // Check whether to draw the stroke
        p.noFill();
        if((smoothBefore && fillBefore) || strokeBefore){
          if(!strokeBefore){
            // If there is no stroke to draw
            // we will still draw one the color 
            // of the fill in order to have antialiasing
            p.stroke(fillColorBefore);
            p.strokeWeight(1F);
          }
          
          for(int i=0;i<numSubshapes;i++){
            subshapes[i].draw(p);
          }
          
          // Restore the old context
          if(fillBefore){
            p.fill(fillColorBefore);
          }
          p.strokeWeight(strokeWeightBefore);
          p.stroke(strokeColorBefore);
          if(!strokeBefore){
            p.noStroke();
          }
        }
        
        // Restore the user set segmentator
        RCommand.setSegmentator(lastSegmentator);

        if(!RGeomerative.ignoreStyles){
          restoreContext(p);
        }
      }
    }
  }
  
  private void drawUsingBreakShape(PGraphics g){
    int numSubshapes = countSubshapes();
    if(numSubshapes!=0){
      if(isIn(g)){
        if(!RGeomerative.ignoreStyles){
          saveContext(g);
          setContext(g);
        }

        boolean closed = false;
        g.beginShape();
        for(int i=0;i<numSubshapes;i++){
          RSubshape subshape = subshapes[i];
          closed |= subshape.closed;
          for(int j = 0; j < subshape.countCommands(); j++ ){
            RPoint[] pnts = subshape.commands[j].getHandles();
            if(j==0){
              g.vertex(pnts[0].x, pnts[0].y);
            }
            switch( subshape.commands[j].getCommandType() )
              {
              case RCommand.LINETO:
                g.vertex( pnts[1].x, pnts[1].y );
                break;
              case RCommand.QUADBEZIERTO:
                g.bezierVertex( pnts[1].x, pnts[1].y, pnts[2].x, pnts[2].y, pnts[2].x, pnts[2].y );
                break;
              case RCommand.CUBICBEZIERTO:
                g.bezierVertex( pnts[1].x, pnts[1].y, pnts[2].x, pnts[2].y, pnts[3].x, pnts[3].y );
                break;
              }
          }
          if(i < (numSubshapes - 1)){
            g.breakShape();
          }

        }
        g.endShape(closed ? PConstants.CLOSE : PConstants.OPEN);

        if(!RGeomerative.ignoreStyles){
          restoreContext(g);
        }
      }
    }
  }
  
  private void drawUsingBreakShape(PApplet g){
    int numSubshapes = countSubshapes();
    if(numSubshapes!=0){
      if(isIn(g)){
        if(!RGeomerative.ignoreStyles){
          saveContext(g);
          setContext(g);
        }

        boolean closed = false;
        g.beginShape();
        for(int i=0;i<numSubshapes;i++){
          RSubshape subshape = subshapes[i];
          closed |= subshape.closed;
          for(int j = 0; j < subshape.countCommands(); j++ ){
            RPoint[] pnts = subshape.commands[j].getHandles();
            if(j==0){
              g.vertex(pnts[0].x, pnts[0].y);
            }
            switch( subshape.commands[j].getCommandType() )
              {
              case RCommand.LINETO:
                g.vertex( pnts[1].x, pnts[1].y );
                break;
              case RCommand.QUADBEZIERTO:
                g.bezierVertex( pnts[1].x, pnts[1].y, pnts[2].x, pnts[2].y, pnts[2].x, pnts[2].y );
                break;
              case RCommand.CUBICBEZIERTO:
                g.bezierVertex( pnts[1].x, pnts[1].y, pnts[2].x, pnts[2].y, pnts[3].x, pnts[3].y );
                break;
              }
          }
          if(i < (numSubshapes - 1)){
            g.breakShape();
          }

        }
        g.endShape(closed ? PConstants.CLOSE : PConstants.OPEN);

        if(!RGeomerative.ignoreStyles){
          restoreContext(g);
        }
      }
    }
  }

}
