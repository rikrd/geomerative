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
 * RShape is a reduced interface for creating, holding and drawing complex shapes. Shapes are groups of one or more paths (RPath).  Shapes can be selfintersecting and can contain holes.  This interface also allows you to transform shapes into polygons by segmenting the curves forming the shape.
 * @eexample RShape
 * @usage Geometry
 * @related RPath
 */
public class RShape extends RGeomElem
{
  /**
   * @invisible
   */
  public int type = RGeomElem.SHAPE;

  /**
   * Array of RPath objects holding the paths of the polygon.
   * @eexample paths
   * @related RPath
   * @related countPaths ( )
   * @related addPath ( )
   */
  public RPath[] paths = null;
  protected int currentPath = 0;

  public RShape[] children = null;
  protected int currentChild;

  // ----------------------
  // --- Public Methods ---
  // ----------------------

  /**
   * Use this method to create a new empty shape.
   * @eexample RShape
   */
  public RShape(){
    type = RGeomElem.SHAPE;
  }

  public RShape(RPath newpath){
    this.append(newpath);
    type = RGeomElem.SHAPE;
  }

  public RShape(RPath[] newpaths){
    this.paths = newpaths;
    type = RGeomElem.SHAPE;
  }

  public RShape(RPoint[][] points){
    if(points == null) return;

    RPath[] newpaths = new RPath[points.length];
    for(int i=0; i<points.length; i++){
      newpaths[i] = new RPath(points[i]);
    }

    this.paths = newpaths;
    type = RGeomElem.SHAPE;
  }

  public RShape(RShape s){
    for(int i=0;i<s.countPaths();i++){
      this.append(new RPath(s.paths[i]));
    }

    for(int i=0;i<s.countChildren();i++){
      this.appendChild(new RShape(s.children[i]));
    }

    type = RGeomElem.SHAPE;

    setStyle(s);
  }

  /**
   * Use this method to create a new line.
   * @eexample createRing
   * @param x1  x coordinate of the first point of the line
   * @param y1  y coordinate of the first point of the line
   * @param x2  x coordinate of the last point of the line
   * @param y2  y coordinate of the last point of the line
   * @return RShape, the ring polygon newly created
   */
  static public RShape createLine(float x1, float y1, float x2, float y2){
    RShape line = new RShape();
    RPath path = new RPath();

    RCommand lineCommand = new RCommand(x1, y1, x2, y2);
    path.addCommand(lineCommand);
    line.addPath(path);

    return line;
  }

  /**
   * Use this method to create a new ring polygon.
   * @eexample createRing
   * @param x  x coordinate of the center of the shape
   * @param y  y coordinate of the center of the shape
   * @param widthBig  the outer width of the ring polygon
   * @param widthSmall  the inner width of the ring polygon
   * @return RShape, the ring polygon newly created
   */
  static public RShape createRing(float x, float y, float widthBig, float widthSmall){
    RShape ring = new RShape();
    RShape outer = RShape.createCircle(x, y, widthBig);
    RShape inner = RShape.createCircle(x, y, -widthSmall);

    ring.addPath(outer.paths[0]);
    ring.addPath(inner.paths[0]);

    return ring;
  }

  /**
   * Use this method to create a new starform polygon.
   * @eexample createStar
   * @param widthBig  the outer width of the star polygon
   * @param widthSmall  the inner width of the star polygon
   * @param spikes  the amount of spikes on the star polygon
   * @return RShape, the starform polygon newly created
   */
  static public RShape createStar(float x, float y, float widthBig, float widthSmall, int spikes){
    float radiusBig = widthBig/2F;
    float radiusSmall = widthSmall/2F;
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
   * @param x  the x position of the rectangle
   * @param y  the y position of the rectangle
   * @param w  the width of the rectangle
   * @param h  the height of the rectangle
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
   * @param x  the x position of the ellipse
   * @param y  the y position of the ellipse
   * @param w  the width of the ellipse
   * @param h  the height of the ellipse
   * @return RShape, the elliptical shape just created
   */
  static public RShape createEllipse(float x, float y, float w, float h){
    float rx = w/2F;
    float ry = h/2F;
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

  static public RShape createCircle(float x, float y, float d){
    return createEllipse(x, y, d, d);
  }

  /**
   *
   * Extracts a shape by its name. The shape is returned as an RShape object, or null is returned if no shape with the name has been found.
   * @return RShape or null, the target shape or null if not found
   *
   */
  public RShape getChild(String target){
    if (this.name.equals(target)) {
      return this;
    }

    for (int i = 0; i < countChildren(); i++) {
      RShape shp = children[i].getChild(target);
      if (shp != null) return shp;
    }

    return null;
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
    if(paths != null){
      for(int i=0;i<paths.length;i++)
        {
          float area = Math.abs(paths[i].getArea());
          if(area > bestArea){
            bestArea = area;
            bestCentroid = paths[i].getCentroid();
          }
        }
      return bestCentroid;
    }
    return null;
  }

  /**
   * Use this method to count the number of paths in the shape.
   * @eexample countPaths
   * @related addPath ( )
   */
  public int countPaths(){
    if(this.paths==null){
      return 0;
    }

    return this.paths.length;
  }


  public int countChildren(){
    if(this.children==null){
      return 0;
    }

    return this.children.length;
  }

  /**
   * Use this method to add a new shape.  The paths of the shape we are adding will simply be added to the current shape.
   * @eexample addShape
   * @param s RShape, the shape to be added.
   * @related setPath ( )
   * @related addMoveTo ( )
   * @invisible
   */
  public void addShape(RShape s){
    for(int i=0;i<s.countPaths();i++){
      this.append(s.paths[i]);
    }
  }

  /**
   * Use this method to create a new path.  The first point of the new path will be set to (0,0).  Use addMoveTo ( ) in order to add a new path with a different first point.
   * @eexample addPath
   * @param s  the path to be added.
   * @related setPath ( )
   * @related addMoveTo ( )
   */
  public void addPath(RPath s){
    this.append(s);
  }

  public void addPath(){
    this.append(new RPath());
  }


  public void addChild(){
    this.appendChild(new RShape());
  }

  public void addChild(RShape s){
    this.appendChild(s);
  }

  /**
   * Use this method to set the current path.
   * @eexample setPath
   * @related addMoveTo ( )
   * @related addLineTo ( )
   * @related addQuadTo ( )
   * @related addBezierTo ( )
   * @related addPath ( )
   */
  public void setPath(int indPath){
    this.currentPath = indPath;
  }

  /**
   * Use this method to add a new moveTo command to the shape.  The command moveTo acts different to normal commands, in order to make a better analogy to its borthers classes Polygon and Mesh.  MoveTo creates a new path in the shape.  It's similar to adding a new contour to a polygon.
   * @eexample addMoveTo
   * @param endx  the x coordinate of the first point for the new path.
   * @param endy  the y coordinate of the first point for the new path.
   * @related addLineTo ( )
   * @related addQuadTo ( )
   * @related addBezierTo ( )
   * @related addPath ( )
   * @related setPath ( )
   */
  public void addMoveTo(float endx, float endy){
    if (paths == null){
      this.append(new RPath(endx,endy));
    }else if(paths[currentPath].countCommands() == 0){
      this.paths[currentPath].lastPoint = new RPoint(endx,endy);
    }else{
      this.append(new RPath(endx,endy));
    }
  }

  public void addMoveTo(RPoint p){
    addMoveTo(p.x, p.y);
  }

  /**
   * Use this method to add a new lineTo command to the current path.  This will add a line from the last point added to the point passed as argument.
   * @eexample addLineTo
   * @param endx  the x coordinate of the ending point of the line.
   * @param endy  the y coordinate of the ending point of the line.
   * @related addMoveTo ( )
   * @related addQuadTo ( )
   * @related addBezierTo ( )
   * @related addPath ( )
   * @related setPath ( )
   */
  public void addLineTo(float endx, float endy){
    if (paths == null) {
      this.append(new RPath());
    }
    this.paths[currentPath].addLineTo(endx, endy);
  }

  public void addLineTo(RPoint p){
    addLineTo(p.x, p.y);
  }

  /**
   * Use this method to add a new quadTo command to the current path.  This will add a quadratic bezier from the last point added with the control and ending points passed as arguments.
   * @eexample addQuadTo
   * @param cp1x  the x coordinate of the control point of the bezier.
   * @param cp1y  the y coordinate of the control point of the bezier.
   * @param endx  the x coordinate of the ending point of the bezier.
   * @param endy  the y coordinate of the ending point of the bezier.
   * @related addMoveTo ( )
   * @related addLineTo ( )
   * @related addBezierTo ( )
   * @related addPath ( )
   * @related setPath ( )
   */
  public void addQuadTo(float cp1x, float cp1y, float endx, float endy){
    if (paths == null) {
      this.append(new RPath());
    }
    this.paths[currentPath].addQuadTo(cp1x,cp1y,endx,endy);
  }

  public void addQuadTo(RPoint p1, RPoint p2){
    addQuadTo(p1.x, p1.y, p2.x, p2.y);
  }

  /**
   * Use this method to add a new bezierTo command to the current path.  This will add a cubic bezier from the last point added with the control and ending points passed as arguments.
   * @eexample addArcTo
   * @param cp1x  the x coordinate of the first control point of the bezier.
   * @param cp1y  the y coordinate of the first control point of the bezier.
   * @param cp2x  the x coordinate of the second control point of the bezier.
   * @param cp2y  the y coordinate of the second control point of the bezier.
   * @param endx  the x coordinate of the ending point of the bezier.
   * @param endy  the y coordinate of the ending point of the bezier.
   * @related addMoveTo ( )
   * @related addLineTo ( )
   * @related addQuadTo ( )
   * @related addPath ( )
   * @related setPath ( )
   */
  public void addBezierTo(float cp1x, float cp1y, float cp2x, float cp2y, float endx, float endy){
    if (paths == null) {
      this.append(new RPath());
    }
    this.paths[currentPath].addBezierTo(cp1x,cp1y,cp2x,cp2y,endx,endy);
  }

  public void addBezierTo(RPoint p1, RPoint p2, RPoint p3){
    addBezierTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
  }

  public void addClose(){
    if (paths == null) {
      this.append(new RPath());
    }
    this.paths[currentPath].addClose();
  }

  /**
   * Use this method to create a new mesh from a given polygon.
   * @eexample toMesh
   * @return RMesh, the mesh made of tristrips resulting of a tesselation of the polygonization followed by tesselation of the shape.
   * @related draw ( )
   */
  public RMesh toMesh(){
    return toPolygon().toMesh();
  }

  /**
   * Use this method to create a new polygon from a given shape.
   * @eexample toPolygon
   * @return RPolygon, the polygon resulting of the segmentation of the commands in each path.
   * @related draw ( )
   */
  public RPolygon toPolygon ( )
  {
    int numPnts = this.countPaths();

    RPolygon poly = new RPolygon();

    if ( this.children != null )
      {
        for ( int i = 0; i < this.children.length; i++ )
          {
            RPolygon childPoly = this.children[i].toPolygon();
            for ( int ii = 0; ii < childPoly.contours.length; ii++ )
              {
                poly.addContour( childPoly.contours[ii] );
              }
          }
      }

    for ( int i = 0; i < numPnts; i++ )
      {
        RPoint[] pnts = this.paths[i].getPoints();
        RContour c = new RContour(pnts);
        c.closed = this.paths[i].closed;
        c.setStyle( this.paths[i] );
        poly.addContour(c);
      }

    return poly;
  }


  public void polygonize(){
    int numPaths = countPaths();

    for(int i=0;i<numPaths;i++){
      this.paths[i].polygonize();
    }

    for(int i=0;i<countChildren();i++){
      this.children[i].polygonize();
    }
  }


  /**
   * @invisible
   */
  public RShape toShape(){
    return this;
  }

  /**
   * Use this method to get the intersection of this polygon with the polygon passed in as a parameter.
   * @eexample intersection
   * @param p RShape, the polygon with which to perform the intersection
   * @return RShape, the intersection of the two polygons
   * @related union ( )
   * @related xor ( )
   * @related diff ( )
   */
  public RShape intersection( RShape p ){
      int numPaths = countPaths();
    RPolygon ppoly = p.toPolygon();

    RShape result = new RShape();

    RShape temp = new RShape();
    for(int i=0; i<numPaths; i++){
      temp.addPath(this.paths[i]);
    }

    RPolygon resPolPaths = RClip.intersection( temp.toPolygon(), ppoly );
    if (resPolPaths != null) {
      RShape resPaths = resPolPaths.toShape();
      for(int i=0; i<resPaths.countPaths(); i++){
        result.addPath(resPaths.paths[i]);
      }
    }

    for(int i=0; i<countChildren(); i++){
      RShape resChildren = this.children[i].intersection(p);
      if (resChildren != null) {
        result.addChild(resChildren);
      }
    }

    if (result != null) {
      result.setStyle(this);
    }

    return result;

    /*
    RPolygon result = RClip.intersection( this.toPolygon(),p.toPolygon() );

    if (result == null) return null;

    return result.toShape();
    */
  }

  /**
   * Use this method to get the union of this polygon with the polygon passed in as a parameter.
   * @eexample union
   * @param p RShape, the polygon with which to perform the union
   * @return RShape, the union of the two polygons
   * @related intersection ( )
   * @related xor ( )
   * @related diff ( )
   */
  public RShape union( RShape p ){
    int numPaths = countPaths();
    RPolygon ppoly = p.toPolygon();

    RShape result = new RShape();

    RShape temp = new RShape();
    for(int i=0; i<numPaths; i++){
      temp.addPath(this.paths[i]);
    }

    RPolygon resPolPaths = RClip.union( temp.toPolygon(), ppoly );
    if (resPolPaths != null) {
      RShape resPaths = resPolPaths.toShape();
      for(int i=0; i<resPaths.countPaths(); i++){
        result.addPath(resPaths.paths[i]);
      }
    }

    for(int i=0; i<countChildren(); i++){
      RShape resChildren = this.children[i].union(p);
      if (resChildren != null) {
        result.addChild(resChildren);
      }
    }

    if (result != null) {
      result.setStyle(this);
    }

    return result;

    /*
    RPolygon result = RClip.union( this.toPolygon(), p.toPolygon() );

    if (result == null) return null;

    return result.toShape();
    */
  }

  /**
   * Use this method to get the xor of this polygon with the polygon passed in as a parameter.
   * @eexample xor
   * @param p RShape, the polygon with which to perform the xor
   * @return RShape, the xor of the two polygons
   * @related union ( )
   * @related intersection ( )
   * @related diff ( )
   */
  public RShape xor( RShape p ){
    int numPaths = countPaths();
    RPolygon ppoly = p.toPolygon();

    RShape result = new RShape();

    RShape temp = new RShape();
    for(int i=0; i<numPaths; i++){
      temp.addPath(this.paths[i]);
    }

    RPolygon resPolPaths = RClip.xor( temp.toPolygon(), ppoly );
    if (resPolPaths != null) {
      RShape resPaths = resPolPaths.toShape();
      for(int i=0; i<resPaths.countPaths(); i++){
        result.addPath(resPaths.paths[i]);
      }
    }

    for(int i=0; i<countChildren(); i++){
      RShape resChildren = this.children[i].xor(p);
      if (resChildren != null) {
        result.addChild(resChildren);
      }
    }

    if (result != null) {
      result.setStyle(this);
    }

    return result;

    /*
    RPolygon result = RClip.xor( this.toPolygon(), p.toPolygon() );

    if (result == null) return null;

    return result.toShape();
    */
  }

  /**
   * Use this method to get the difference between this polygon and the polygon passed in as a parameter.
   * @eexample diff
   * @param p RShape, the polygon with which to perform the difference
   * @return RShape, the difference of the two polygons
   * @related union ( )
   * @related xor ( )
   * @related intersection ( )
   */
  public RShape diff( RShape p ){
    int numPaths = countPaths();
    RPolygon ppoly = p.toPolygon();

    RShape result = new RShape();

    RShape temp = new RShape();
    for(int i=0; i<numPaths; i++){
      temp.addPath(this.paths[i]);
    }

    RPolygon resPolPaths = RClip.diff( temp.toPolygon(), ppoly );
    if (resPolPaths != null) {
      RShape resPaths = resPolPaths.toShape();
      for(int i=0; i<resPaths.countPaths(); i++){
        result.addPath(resPaths.paths[i]);
      }
    }

    for(int i=0; i<countChildren(); i++){
      RShape resChildren = this.children[i].diff(p);
      if (resChildren != null) {
        result.addChild(resChildren);
      }
    }

    if (result != null) {
      result.setStyle(this);
    }

    return result;

    /*
    RPolygon result = RClip.diff( this.toPolygon(), p.toPolygon() );

    if (result == null) return null;

    return result.toShape();
    */
  }

  /**
   * Use this to return the start, control and end points of the shape.  It returns the points as an array of RPoint.
   * @eexample RShape_getHandles
   * @return RPoint[], the start, control and end points returned in an array.
   * */
  public RPoint[] getHandles(){
    int numPaths = countPaths();

    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numPaths;i++){
      RPoint[] newPoints = paths[i].getHandles();
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

    for(int i=0;i<countChildren();i++){
      RPoint[] newPoints = children[i].getHandles();
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
   * Use this to return a point on the curve given a certain advancement.  It returns the point as an RPoint.
   * @eexample RShape_getPoints
   * @return RPoint[], the point on the curve.
   * */
  public RPoint getPoint(float t){
    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];

    if ( indOfElement < countPaths() ){
      return paths[indOfElement].getPoint(advOfElement);
    }else{
      return children[indOfElement - countPaths()].getPoint(advOfElement);
    }
  }

  /**
   * Use this to return the points on the curve of the shape.  It returns the points as an array of RPoint.
   * @eexample RShape_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getPoints(){
    int numPaths = countPaths();

    RCommand.segmentAccOffset = RCommand.segmentOffset;
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numPaths;i++){
      RPoint[] newPoints = paths[i].getPoints();
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

    for(int i=0;i<countChildren();i++){
      RPoint[] newPoints = children[i].getPoints();
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
   * Use this to return a point on the curve given a certain advancement.  It returns the point as an RPoint.
   * @eexample RShape_getTangents
   * @return RPoint[], the point on the curve.
   * */
  public RPoint getTangent(float t){
    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];


    if ( indOfElement < countPaths() ){
      return paths[indOfElement].getTangent(advOfElement);
    }else{
      return children[indOfElement - countPaths()].getTangent(advOfElement);
    }
  }

  /**
   * Use this to return a specific tangent on the curve.  It returns true if the point passed as a parameter is inside the shape.  Implementation taken from: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
   * @param x  the X coordinate of the point for which to test containment.
   * @param y  the Y coordinate of the point for which to test containment.
   * @return boolean, true if the point is in the path.
   * */
  public boolean contains(float x, float y){
    return contains(new RPoint(x, y));
  }

  /**
   * Use this to return a specific tangent on the curve.  It returns true if the point passed as a parameter is inside the shape.  Implementation taken from: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
   * @param p  the point for which to test containment.
   * @return boolean, true if the point is in the path.
   * */
  public boolean contains(RPoint p){
    float testx = p.x;
    float testy = p.y;

    // Test for containment in bounding box
    RRectangle bbox = getBounds();
    float xmin = bbox.getMinX();
    float xmax = bbox.getMaxX();

    float ymin = bbox.getMinY();
    float ymax = bbox.getMaxY();

    if( (testx < xmin) || (testx > xmax) || (testy < ymin) || (testy > ymax)){
      return false;
    }

    // Test for containment in shape
    RPoint[][] pointpaths = getPointsInPaths();

    if(pointpaths == null){
      return false;
    }

    RPoint[] verts = pointpaths[0];
    for(int k=1;k<pointpaths.length;k++){
      verts = (RPoint[])RG.parent().append(verts, new RPoint(0F, 0F));
      verts = (RPoint[])RG.parent().concat(verts, pointpaths[k]);
    }
    verts = (RPoint[])RG.parent().append(verts, new RPoint(0F, 0F));

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
   * Use this to return the points on the curve of the shape.  It returns the point as an RPoint.
   * @eexample RShape_getTangents
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getTangents(){
    int numPaths = countPaths();

    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numPaths;i++){
      RPoint[] newPoints = paths[i].getTangents();
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

    for(int i=0;i<countChildren();i++){
      RPoint[] newPoints = children[i].getTangents();
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
   * Use this to return the points of each path of the group.  It returns the points as an array of arrays of RPoint.
   * @eexample RGroup_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[][] getPointsInPaths(){
    int numPaths = countPaths();

    RPoint[][] result=null;
    RPoint[][] newresult=null;
    for(int i=0;i<numPaths;i++){
      RPoint[][] newPointPaths = paths[i].getPointsInPaths();
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

    for(int i=0;i<countChildren();i++){
      RPoint[][] newPoints = children[i].getPointsInPaths();
      if(newPoints!=null){
        if(result==null){
          result = new RPoint[newPoints.length][];
          System.arraycopy(newPoints,0,result,0,newPoints.length);
        }else{
          newresult = new RPoint[result.length + newPoints.length][];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
          result = newresult;
        }
      }
    }

    return result;
  }

  public RPoint[][] getHandlesInPaths(){
    int numPaths = countPaths();

    RPoint[][] result=null;
    RPoint[][] newresult=null;
    for(int i=0;i<numPaths;i++){
      RPoint[][] newHandlePaths = paths[i].getHandlesInPaths();
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

    for(int i=0;i<countChildren();i++){
      RPoint[][] newPoints = children[i].getHandlesInPaths();
      if(newPoints!=null){
        if(result==null){
          result = new RPoint[newPoints.length][];
          System.arraycopy(newPoints,0,result,0,newPoints.length);
        }else{
          newresult = new RPoint[result.length + newPoints.length][];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
          result = newresult;
        }
      }
    }

    return result;
  }

  public RPoint[][] getTangentsInPaths(){
    int numPaths = countPaths();
    if(numPaths == 0){
      return null;
    }

    RPoint[][] result=null;
    RPoint[][] newresult=null;
    for(int i=0;i<numPaths;i++){
      RPoint[][] newTangentPaths = paths[i].getTangentsInPaths();
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

    for(int i=0;i<countChildren();i++){
      RPoint[][] newPoints = children[i].getTangentsInPaths();
      if(newPoints!=null){
        if(result==null){
          result = new RPoint[newPoints.length][];
          System.arraycopy(newPoints,0,result,0,newPoints.length);
        }else{
          newresult = new RPoint[result.length + newPoints.length][];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
          result = newresult;
        }
      }
    }

    return result;
  }

  public RShape[] splitPaths(float t){
    RShape[] result = new RShape[2];
    result[0] = new RShape();
    result[1] = new RShape();

    for(int i=0; i<countPaths(); i++){
      RPath[] splittedPaths = paths[i].split(t);
      if(splittedPaths != null){
        result[0].addPath(splittedPaths[0]);
        result[1].addPath(splittedPaths[1]);
      }
    }

    for(int i=0; i<countChildren(); i++){
      RShape[] splittedPaths = children[i].splitPaths(t);
      if(splittedPaths != null){
        result[0].addChild(splittedPaths[0]);
        result[1].addChild(splittedPaths[1]);
      }
    }

    result[0].setStyle(this);
    result[1].setStyle(this);
    return result;
  }

  /**
   * Use this to insert a split point into the shape.
   * @eexample insertHandle
   * @param t  the parameter of advancement on the curve. t must have values between 0 and 1.
   * */
  public void insertHandle(float t){
    if((t == 0F) || (t == 1F)){
      return;
    }

    float[] indAndAdv = indAndAdvAt(t);
    int indOfElement = (int)(indAndAdv[0]);
    float advOfElement = indAndAdv[1];

    if ( indOfElement < countPaths() ){
      paths[indOfElement].insertHandle(advOfElement);
    }else{
      children[indOfElement - countPaths()].insertHandle(advOfElement);
    }

    // Clear the cache
    lenCurves = null;
    lenCurve = -1F;

    return;
  }

  /**
   * Use this to insert a split point into each command of the shape.
   * @eexample insertHandleInPaths
   * @param t  the parameter of advancement on the curve. t must have values between 0 and 1.
   * */
  public void insertHandleInPaths(float t){
    if((t == 0F) || (t == 1F)){
      return;
    }

    int numPaths = countPaths();
    if(numPaths == 0){
      return;
    }

    for( int i = 0 ; i < numPaths; i++ ) {
      paths[i].insertHandleInPaths(t);
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

    if ( indOfElement < countPaths() ){
      RPath[] splittedShapes = paths[indOfElement].split(advOfElement);

      result[0] = new RShape();
      for(int i = 0; i<indOfElement; i++){
        result[0].addPath(new RPath(paths[i]));
      }
      result[0].addPath(new RPath(splittedShapes[0]));
      result[0].setStyle(this);

      result[1] = new RShape();
      result[1].addPath(new RPath(splittedShapes[1]));
      for(int i = indOfElement + 1; i < countPaths(); i++){
        result[1].addPath(new RPath(paths[i]));
      }

      for (int i = 0; i < countChildren(); i++){
        result[1].appendChild(new RShape(this.children[i]));
      }
      result[1].setStyle(this);

      return result;
    }else{
      indOfElement -= countPaths();

      // Add the elements before the cut point
      for(int i=0; i<indOfElement; i++){
        result[0].addChild(new RShape(children[i]));
      }

      // Add the cut point element cutted
      RShape[] splittedChild = children[indOfElement].split(advOfElement);
      result[0].addChild(new RShape(splittedChild[0]));
      result[1].addChild(new RShape(splittedChild[1]));

      // Add the elements after the cut point
      for(int i=indOfElement+1; i<countChildren(); i++){
        result[1].addChild(new RShape(children[i]));
      }

      result[0].setStyle(this);
      result[1].setStyle(this);

      return result;
    }
  }

  /**
   * Use this method to get the points of intersection between this shape and another shape passed in as a parameter.
   * @param other  the path with which to check for intersections
   */
  public RPoint[] getIntersections(RShape other) {
    // TODO: when we will be able to intersect between all
    //       geometric elements the polygonization will not be necessary
    RShape shp = new RShape(this);
    shp.polygonize();

    RShape otherPol = new RShape(other);
    otherPol.polygonize();
    return shp.polygonIntersectionPoints(otherPol);
  }


  RPoint[] getIntersections(RCommand other) {
    // TODO: when we will be able to intersect between all
    //       geometric elements the polygonization will not be necessary
    RShape shp = new RShape(this);
    shp.polygonize();

    return shp.polygonIntersectionPoints(other);
  }

  RPoint[] polygonIntersectionPoints(RCommand other){
    int numPaths = countPaths();

    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numPaths;i++){
      RPoint[] newPoints = paths[i].intersectionPoints(other);
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

    for(int i=0;i<countChildren();i++){
      RPoint[] newPoints = children[i].polygonIntersectionPoints(other);
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

  RPoint[] polygonIntersectionPoints(RPath other){
    int numChildren = countChildren();
    int numPaths = countPaths();

    RPoint[] result=null;
    RPoint[] newresult=null;

    for(int i=0;i<numPaths;i++){
      RPoint[] newPoints = paths[i].intersectionPoints(other);
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

    for(int i=0;i<numChildren;i++){
      RPoint[] newPoints = children[i].polygonIntersectionPoints(other);
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

  RPoint[] polygonIntersectionPoints(RShape other){
    int numChildren = countChildren();
    int numPaths = countPaths();

    RPoint[] result=null;
    RPoint[] newresult=null;

    for(int i=0;i<numPaths;i++){
      RPoint[] newPoints = other.polygonIntersectionPoints(paths[i]);
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

    for(int i=0;i<numChildren;i++){
      RPoint[] newPoints = other.polygonIntersectionPoints(children[i]);
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
   * Use this method to get the closest or intersection points of the shape with another shape passed as argument.
   * @param other  the path with which to check for intersections
   */
  public RClosest getClosest(RShape other) {
    // TODO: when we will be able to intersect between all
    //       geometric elements the polygonization will not be necessary
    RShape shp = new RShape(this);
    shp.polygonize();

    RShape otherPol = new RShape(other);
    otherPol.polygonize();
    return shp.polygonClosestPoints(otherPol);
  }


  RClosest getClosest(RCommand other) {
    // TODO: when we will be able to intersect between all
    //       geometric elements the polygonization will not be necessary
    RShape shp = new RShape(this);
    shp.polygonize();

    return shp.polygonClosestPoints(other);
  }

  RClosest polygonClosestPoints(RCommand other){
    int numPaths = countPaths();

    RClosest result = new RClosest();

    for(int i=0;i<numPaths;i++){
      RClosest currResult = paths[i].closestPoints(other);
      result.update(currResult);
    }

    for(int i=0;i<countChildren();i++){
      RClosest currResult = children[i].polygonClosestPoints(other);
      result.update(currResult);
    }

    return result;
  }

  RClosest polygonClosestPoints(RPath other){
    int numChildren = countChildren();
    int numPaths = countPaths();

    RClosest result = new RClosest();

    for(int i=0;i<numPaths;i++){
      RClosest currClosest = paths[i].closestPoints(other);
      result.update(currClosest);
    }

    for(int i=0;i<numChildren;i++){
      RClosest currClosest = children[i].polygonClosestPoints(other);
      result.update(currClosest);
    }

    return result;
  }

  RClosest polygonClosestPoints(RShape other){
    int numChildren = countChildren();
    int numPaths = countPaths();

    RClosest result = new RClosest();

    for(int i=0;i<numPaths;i++){
      RClosest currClosest = other.polygonClosestPoints(paths[i]);
      result.update(currClosest);
    }

    for(int i=0;i<numChildren;i++){
      RClosest currClosest = other.polygonClosestPoints(children[i]);
      result.update(currClosest);
    }

    return result;
  }

  /**
   * Use this method to adapt a group of of figures to a shape.
   * @eexample RGroup_adapt
   * @param shp  the path to which to adapt
   */
  public void adapt(RShape shp, float wght, float lngthOffset) throws RuntimeException{
    RRectangle c = this.getBounds();
    float xmin = c.getMinX();
    float xmax = c.getMaxX();

    int numChildren = countChildren();

    switch(RG.adaptorType){
    case RG.BYPOINT:
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
    case RG.BYELEMENTINDEX:
      for(int i=0;i<numChildren;i++){
        RShape elem = this.children[i];
        RRectangle elemc = elem.getBounds();

        float px = (elemc.bottomRight.x + elemc.topLeft.x) / 2F;
        float py = (elemc.bottomRight.y - elemc.topLeft.y) / 2F;
        float t = ((float)i/(float)numChildren + lngthOffset ) % 1F;

        RPoint tg = shp.getTangent(t);
        RPoint p = shp.getPoint(t);
        float angle = (float)Math.atan2(tg.y, tg.x);

        RPoint pletter = new RPoint(px,py);
        p.sub(pletter);

        RMatrix mtx = new RMatrix();
        mtx.translate(p);
        mtx.rotate(angle,pletter);
        mtx.scale(wght,pletter);

        elem.transform(mtx);
      }
      break;

    case RG.BYELEMENTPOSITION:
      for(int i=0;i<numChildren;i++){
        RShape elem = this.children[i];
        RRectangle elemc = elem.getBounds();

        float px = (elemc.bottomRight.x + elemc.topLeft.x) / 2F;
        float py = (elemc.bottomRight.y - elemc.topLeft.y) / 2F;
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

        elem.transform(mtx);
      }
      break;

    default:
      throw new RuntimeException("Unknown adaptor type : "+RG.adaptorType+". The method RG.setAdaptor() only accepts RG.BYPOINT or RG.BYELEMENT as parameter values.");
    }
  }

  public void adapt(RShape shp) throws RuntimeException{
    adapt(shp, RG.adaptorScale, RG.adaptorLengthOffset);
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
    System.out.println("paths [count " + this.countPaths() + "]: ");
    for(int i=0;i<countPaths();i++)
      {
        System.out.println("--- path "+i+" ---");
        paths[i].print();
        System.out.println("---------------");
      }
    System.out.println("children [count " + this.countChildren() + "]: ");
    for(int i=0;i<countChildren();i++)
      {
        System.out.println("--- child "+i+" ---");
        children[i].print();
        System.out.println("---------------");
      }

  }

  public void draw(PGraphics g){
    if(!RG.ignoreStyles){
      saveContext(g);
      setContext(g);
    }

    this.drawPaths(g);

    for(int i=0;i<countChildren();i++){
      this.children[i].draw(g);
    }

    if(!RG.ignoreStyles){
      restoreContext(g);
    }
  }

  public void draw(PApplet g){
    if(!RG.ignoreStyles){
      saveContext(g);
      setContext(g);
    }

    this.drawPaths(g);

    for(int i=0;i<countChildren();i++){
      this.children[i].draw(g);
    }

    if(!RG.ignoreStyles){
      restoreContext(g);
    }
  }

  /**
   * Use this method to draw the shape.
   * @eexample drawShape
   * @param g PGraphics, the graphics object on which to draw the shape
   */
  private void drawPaths(PGraphics g){
    /*
    try{
      Class declaringClass = g.getClass().getMethod("breakShape", new Class[0]).getDeclaringClass();
      if(declaringClass == Class.forName("processing.core.PGraphics")){

        // The backend does not implement breakShape
        // HACK: Drawing twice, so that it also works on backends that support breakShape
        // such as when recording to a PDF from a OPENGL backend
        //drawUsingBreakShape(g);
        drawUsingInternalTesselator(g);

      }else{

        // The backend does implement breakShape
        drawUsingBreakShape(g);
      }
    }catch(NoSuchMethodException e){

      // The backend does implement breakShape
      drawUsingInternalTesselator(g);

    }catch(ClassNotFoundException e){

      // The backend does implement breakShape
      drawUsingInternalTesselator(g);

    }
    */
    drawUsingBreakShape(g);
  }

  private void drawPaths(PApplet g){
    /*
    try{
      Class declaringClass = g.g.getClass().getMethod("breakShape", new Class[0]).getDeclaringClass();
      if(declaringClass == Class.forName("processing.core.PGraphics")){

        // The backend does not implement breakShape
        // HACK: Drawing twice, so that it also works on backends that support breakShape
        // such as when recording to a PDF from a OPENGL backend
        //drawUsingBreakShape(g);
        drawUsingInternalTesselator(g);

      }else{

        // The backend does implement breakShape
        drawUsingBreakShape(g);

      }
    }catch(NoSuchMethodException e){

      // The backend does implement breakShape
      drawUsingInternalTesselator(g);

    }catch(ClassNotFoundException e){

      // The backend does implement breakShape
      drawUsingInternalTesselator(g);

    }
    */
    drawUsingBreakShape(g);
  }

  // ----------------------
  // --- Private Methods ---
  // ----------------------

  protected void calculateCurveLengths(){
    lenCurves = new float[countPaths() + countChildren()];
    lenCurve = 0F;
    for(int i=0;i<countPaths();i++){
      lenCurves[i] = paths[i].getCurveLength();
      lenCurve += lenCurves[i];
    }

    for(int i=0;i<countChildren();i++){
      lenCurves[i + countPaths()] = children[i].getCurveLength();
      lenCurve += lenCurves[i + countPaths()];
    }
  }

  private float[] indAndAdvAt(float t){
    int indOfElement = 0;
    float[] lengthsCurves = getCurveLengths();
    float lengthCurve = getCurveLength();

    /* Calculate the amount of advancement t mapped to each command */
    /* We use a simple algorithm where we give to each command the same amount of advancement */
    /* A more useful way would be to give to each command an advancement proportional to the length of the command */

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
    indAndAdv[1] = RG.parent().constrain(advOfElement, 0.0f, 1.0f);

    return indAndAdv;
  }


  private void appendChild(RShape nextshape)
  {
    RShape[] newshapes;
    if(children==null){
      newshapes = new RShape[1];
      newshapes[0] = nextshape;
      currentChild = 0;
    }else{
      newshapes = new RShape[this.children.length+1];
      System.arraycopy(this.children,0,newshapes,0,this.children.length);
      newshapes[this.children.length]=nextshape;
      currentChild++;
    }
    this.children = newshapes;
  }


  private void append(RPath nextpath)
  {
    RPath[] newpaths;
    if(paths==null){
      newpaths = new RPath[1];
      newpaths[0] = nextpath;
      currentPath = 0;
    }else{
      newpaths = new RPath[this.paths.length+1];
      System.arraycopy(this.paths,0,newpaths,0,this.paths.length);
      newpaths[this.paths.length]=nextpath;
      currentPath++;
    }
    this.paths=newpaths;
  }

  private void drawUsingInternalTesselator(PGraphics g){
    int numPaths = countPaths();

    if(numPaths!=0){
      if(isIn(g)) {

        // Save the information about the current context
        boolean strokeBefore = g.stroke;
        int strokeColorBefore = g.strokeColor;
        float strokeWeightBefore = g.strokeWeight;
        int smoothBefore = g.smooth;
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
            if(smoothBefore > 0){
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

        for(int i=0;i<numPaths;i++){
          paths[i].draw(g);
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
      }
    }
  }

  private void drawUsingInternalTesselator(PApplet p){
    int numPaths = countPaths();

    if(numPaths!=0){
      if(isIn(p)) {
        // Save the information about the current context
        boolean strokeBefore = p.g.stroke;
        int strokeColorBefore = p.g.strokeColor;
        float strokeWeightBefore = p.g.strokeWeight;
        int smoothBefore = p.g.smooth;
        boolean fillBefore = p.g.fill;
        int fillColorBefore = p.g.fillColor;

        // By default always drawy with an ADAPTATIVE segmentator
        int lastSegmentator = RCommand.segmentType;
        RCommand.setSegmentator(RCommand.ADAPTATIVE);

        // Check whether to draw the fill or not
        if(p.g.fill){
          // Since we are drawing the different tristrips we must turn off the stroke
          // or make it the same color as the fill
          // NOTE: there's currently no way of drawing the outline of a mesh,
          // since no information is kept about what vertices are at the edge

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
            if(smoothBefore > 0){
              p.smooth();
            }
          }catch(Exception e){}
        }


        // Check whether to draw the stroke
        p.noFill();
        if((smoothBefore > 0 && fillBefore) || strokeBefore){
          if(!strokeBefore){
            // If there is no stroke to draw
            // we will still draw one the color
            // of the fill in order to have antialiasing
            p.stroke(fillColorBefore);
            p.strokeWeight(1F);
          }

          for(int i=0;i<numPaths;i++){
            paths[i].draw(p);
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
      }
    }
  }

  private void drawUsingBreakShape(PGraphics g){
    int numPaths = countPaths();
    if(numPaths!=0){
      if(isIn(g)){
        boolean closed = false;
        boolean useContours = (numPaths>1);
        g.beginShape();
        for(int i=0;i<numPaths;i++){
          if (useContours && i>0) {
            g.beginContour();
          }

          RPath path = paths[i];
          closed |= path.closed;

          for(int j = 0; j < path.countCommands(); j++ ){
            RPoint[] pnts = path.commands[j].getHandles();
            if(j==0){
              g.vertex(pnts[0].x, pnts[0].y);
            }
            switch( path.commands[j].getCommandType() )
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

          if (useContours && i>0) {
            g.endContour();
          }

        }
        g.endShape(closed ? PConstants.CLOSE : PConstants.OPEN);

      }
    }
  }

  private void drawUsingBreakShape(PApplet g){
    int numPaths = countPaths();
    if(numPaths!=0){
      if(isIn(g)){
        boolean closed = false;
        boolean useContours = (numPaths>1);
        g.beginShape();
        for(int i=0;i<numPaths;i++){
          if (useContours && i>0) g.beginContour();

          RPath path = paths[i];
          closed |= path.closed;
          float firstx = 0;
          float firsty = 0;
          for(int j = 0; j < path.countCommands(); j++ ){
            RPoint[] pnts = path.commands[j].getHandles();
            if (j==0) {
              g.vertex(pnts[0].x, pnts[0].y);
            }
            switch( path.commands[j].getCommandType() )
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
          if (useContours && i>0) {
              g.endContour();
          }

        }
        g.endShape(closed ? PConstants.CLOSE : PConstants.OPEN);

      }
    }
  }

}
