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
 * RGeomElem is an interface to any geometric element that can be drawn and transformed, such as shapes, polygons or meshes.
 * @extended
 */
public abstract class RGeomElem
{
  /**
   * @invisible
   */
  public static final int SHAPE = 0;
  /**
   * @invisible
   */
  public static final int SUBSHAPE = 1;
  /**
   * @invisible
   */
  public static final int COMMAND = 2;
  
  /**
   * @invisible
   */
  public static final int POLYGON = 3;
  /**
   * @invisible
   */
  public static final int CONTOUR = 4;
  
  /**
   * @invisible
   */
  public static final int MESH = 5;
  /**
   * @invisible
   */
  public static final int TRISTRIP = 6;
  
  /**
   * @invisible
   */
  public static final int GROUP = 7;
  
  /**
   * @invisible
   */
  public static final int UNKNOWN = 8;

  /**
   * Shape document width.
   */
  public float width;

  /**
   * Shape document height.
   */
  public float height;

  float origWidth;
  float origHeight;
  
  // Functions dependent of the type of element
  // They must be overrided
  public abstract void draw(PGraphics g);
  public abstract void draw(PApplet g);
  
  public void draw(){
    this.draw(RG.parent());
  }


  public abstract RPoint getPoint(float t);

  public abstract RPoint getTangent(float t);

  public abstract RPoint[] getHandles();

  public abstract RPoint[] getPoints();

  public abstract RPoint[] getTangents();

  public abstract RPoint[][] getHandlesInPaths();

  public abstract RPoint[][] getPointsInPaths();

  public abstract RPoint[][] getTangentsInPaths();

  public abstract boolean contains(RPoint p);

  /**
   * Use this method to test if the shape contains all the points of another shape. 
   * @eexample contains
   * @return boolean, true if the shape contains all the points of the other shape
   * @related containsBounds ( )
   * @related containsHandles ( )
   */
  public boolean contains(RGeomElem shp) {
    return contains(shp.getPoints());
  }

  /**
   * Use this method to test if the shape contains the bounding box of another shape. 
   * @eexample contains
   * @return boolean, true if the shape contains the bounding box of the other shape
   * @related contains ( )
   * @related containsHandles ( )
   */
  public boolean containsBounds(RGeomElem shp) {
    RPoint tl = shp.getTopLeft();
    RPoint tr = shp.getTopRight();
    RPoint bl = shp.getBottomRight();
    RPoint br = shp.getBottomLeft();
    
    if(this.contains(tl) 
       && this.contains(tr) 
       && this.contains(bl) 
       && this.contains(br)){
      
      return true;
    }

    return false;
  }

  /**
   * Use this method to test if the shape contains the handles of another shape. This method is faster than contains(), but the results might not be perfect.
   * @eexample contains
   * @return boolean, true if the shape contains all the handles of the other shape
   * @related containsBounds ( )
   * @related contains ( )
   */
  public boolean containsHandles(RGeomElem shp) {
    return contains(shp.getHandles());
  }

  /**
   * Use this method to test if the shape contains an array of points. 
   * @eexample contains
   * @return boolean, true if the shape contains all the points
   * @related contains ( )
   * @related containsBounds ( )
   * @related containsHandles ( )
   */
  public boolean contains(RPoint[] pts) {
          if (pts.length == 0) {
            return false;
        }
        boolean inside = true;
        for (RPoint pt : pts) {
            if (!contains(pt)) {
                inside = false;
                break;
            }
        }
        return inside;
  }

  
  /**
   * Use this method to test if the shape intersects another shape. 
   * @eexample intersects
   * @return boolean, true if the shape intersects all the points of the other shape
   * @related intersectsBounds ( )
   * @related intersectsHandles ( )
   */
  public boolean intersects(RGeomElem shp) {
    return intersects(shp.getPoints());
  }

  /**
   * Use this method to test if the shape intersects the bounding box of another shape. 
   * @eexample intersects
   * @return boolean, true if the shape intersects the bounding box of the other shape
   * @related intersects ( )
   * @related intersectsHandles ( )
   */
  public boolean intersectsBounds(RGeomElem shp) {
    RPoint tl = shp.getTopLeft();
    RPoint tr = shp.getTopRight();
    RPoint bl = shp.getBottomRight();
    RPoint br = shp.getBottomLeft();
    
    if(this.contains(tl) 
       || this.contains(tr) 
       || this.contains(bl) 
       || this.contains(br)){
      
      return true;
      
    }

    return false;
  }

  /**
   * Use this method to test if the shape intersects the handles of another shape. This method is faster than intersects(), but the results might not be perfect.
   * @eexample intersects
   * @return boolean, true if the shape intersects all the handles of the other shape
   * @related intersectsBounds ( )
   * @related intersects ( )
   */
  public boolean intersectsHandles(RGeomElem shp) {
    return intersects(shp.getHandles());
  }

  /**
   * Use this method to test if the shape intersects an array of points. 
   * @eexample intersects
   * @return boolean, true if the shape intersects all the points
   * @related intersects ( )
   * @related intersectsBounds ( )
   * @related intersectsHandles ( )
   */
  public boolean intersects(RPoint[] ps) {
    boolean intersects = false;
    if(ps != null){
      for(int i=0; i < ps.length; i++) {
        intersects |= this.contains(ps[i]);
      }
    }
    return intersects;
  }


  public abstract int getType();

  //public abstract RMesh toMesh();
  //public abstract RPolygon toPolygon();
  public abstract RShape toShape();
  public void print(){};
  
  protected float[] lenCurves;
  protected float lenCurve = -1F;

  public String name = "";  

  protected RStyle style = new RStyle();

  public void setFill(boolean _fill){
    style.setFill(_fill);
  }

  public void setFill(int _fillColor){
    style.setFill(_fillColor);
  }

  public void setFill(String str){
    style.setFill(str);
  }

  public void setStroke(boolean _stroke){
    style.setStroke(_stroke);
  }

  public void setStroke(int _strokeColor){
    style.setStroke(_strokeColor);
  }

  public void setStroke(String str){
    style.setStroke(str);
  }

  public void setStrokeWeight(float value){
    style.setStrokeWeight(value);
  }

  public void setStrokeWeight(String str){
    style.setStrokeWeight(str);
  }

  public void setStrokeCap(String str){
    style.setStrokeCap(str);
  }

  public void setStrokeJoin(String str){
    style.setStrokeJoin(str);
  }

  public void setStrokeAlpha(int opacity){
    style.setStrokeAlpha(opacity);
  }

  public void setStrokeAlpha(String str){
    style.setStrokeAlpha(str);
  }

  public void setFillAlpha(int opacity){
    style.setFillAlpha(opacity);
  }

  public void setFillAlpha(String str){
    style.setFillAlpha(str);
  }  

  public void setAlpha(float opacity){
    style.setAlpha(opacity);
  }

  public void setAlpha(int opacity){
    style.setAlpha(opacity);
  }

  public void setAlpha(String str){
    style.setAlpha(str);
  }

  public RStyle getStyle() {
    return this.style;
  }

  protected void saveContext(PGraphics g){
    style.saveContext(g);
  }

  protected void saveContext(PApplet p){
    style.saveContext(p);
}

  protected void saveContext(){
    style.saveContext();
  }

  protected void restoreContext(PGraphics g){
    style.restoreContext(g);
  }

  protected void restoreContext(PApplet p){
    style.restoreContext(p);
  }

  protected void restoreContext(){
    style.restoreContext();
  }

  protected void setContext(PGraphics g){
    style.setContext(g);
  }

  protected void setContext(PApplet p){
    style.setContext(p);
  }

  protected void setContext(){
    style.setContext();
  }

  public void setStyle(RStyle s){
    style = s;
  }

  protected void setStyle(RGeomElem p){
    name = p.name;
    width = p.width;
    height = p.height;
    origWidth = p.origWidth;
    origHeight = p.origHeight;

    style = new RStyle(p.style);
  }

  protected void setStyle(String styleString){
    style.setStyle(styleString);
  }

  public void setName(String str){
    this.name = str;
  }

  protected void calculateCurveLengths(){
    PApplet.println("Feature not yet implemented for this class.");
  }

  /**
   * Use this to return arclengths of each command on the curve.
   * @eexample getCurveLength
   * @return float[], the arclengths of each command on the curve.
   * */
  public float[] getCurveLengths(){
    /* If the cache with the commands lengths is empty, we fill it up */
    if(lenCurves == null){
      calculateCurveLengths();
    }
    
    return lenCurves;
  }
  
  /**
   * Use this to return the length of the curve.
   * @eexample getCurveLength
   * @return float, the arclength of the path.
   * */
  public float getCurveLength(){    
    /* If the cache with the commands lengths is empty, we fill it up */
    if(lenCurve == -1F){
      calculateCurveLengths();
    }
    
    return lenCurve;
  }

  public RPolygon toPolygon(){
    return toShape().toPolygon();
  }

  public RMesh toMesh(){
    return toShape().toPolygon().toMesh();
  }

  // Functions independent of the type of element
  // No need of being overrided
  public void transform(RMatrix m){   
    RPoint[] ps = getHandles();
    
    if(ps == null) return;
    
    for(int i=0; i<ps.length; i++){
      ps[i].transform(m);
    }
  }

  /**
   * Transform the geometric object to fit in a rectangle defined by the parameters passed.
   * @eexample getBounds
   * @related getCenter ( )
   */
  public void transform(float x, float y, float w, float h, boolean keepAspectRatio){
    RMatrix mtx = new RMatrix();
    RRectangle orig = this.getBounds();
    float orig_w = orig.getMaxX() - orig.getMinX();
    float orig_h = orig.getMaxY() - orig.getMinY();
    
    mtx.translate(-orig.getMinX(), -orig.getMinY());
    if(keepAspectRatio){
      mtx.scale(Math.min(w/orig_w, h/orig_h));
    }else{
      mtx.scale(w/orig_w, h/orig_h);
    }
    mtx.translate(x, y);
    
    this.transform(mtx);
    return;
  }

  public void transform(float x, float y, float w, float h){
    this.transform(x, y, w, h, true);
    return;
  }
  
  /**
   * Use this method to get the bounding box of the element. 
   * @eexample getBounds
   * @return RRectangle, the bounding box of the element in the form of a four-point contour
   * @related getCenter ( )
   */
  public RRectangle getBounds(){
    float xmax = Float.NEGATIVE_INFINITY ;
    float ymax = Float.NEGATIVE_INFINITY ;
    float xmin = Float.POSITIVE_INFINITY ;
    float ymin = Float.POSITIVE_INFINITY ;

    RPoint[] points = getHandles();
    
    if(points!=null){
      for(int i=0;i<points.length;i++){
        float tempx = points[i].x;
        float tempy = points[i].y;
        
        if( tempx < xmin )
          {
            xmin = tempx;
          }
          
        if( tempx > xmax )
          {
            xmax = tempx;
          }
          
        if( tempy < ymin )
          {
            ymin = tempy;
          }
          
        if( tempy > ymax )
          {
            ymax = tempy;
          }
      }
    }
    
    RRectangle c = new RRectangle(new RPoint(xmin, ymin), new RPoint(xmax, ymax));
    return c;
  }

  /**
   * Use this method to get the points of the bounding box of the element. 
   * @eexample getBounds
   * @return RRectangle, the bounding box of the element in the form of a four-point contour
   * @related getCenter ( )
   */
  public RPoint[] getBoundsPoints(){
    return getBounds().getPoints();
  }  

  /**
   * Use this method to get the top left position of the element. 
   * @eexample getX
   * @related getTopRight ( )
   * @related getBottomLeft ( )
   * @related getBottomRight ( )
   * @related getWidth ( )
   * @related getHeight ( )
   * @related getCenter ( )
   */
  public RPoint getTopLeft(){
    RRectangle orig = this.getBounds();
    return new RPoint(orig.getMinX(), orig.getMinY());
  }


  /**
   * Use this method to get the top right position of the element. 
   * @eexample getX
   * @related getTopRight ( )
   * @related getBottomLeft ( )
   * @related getBottomRight ( )
   * @related getWidth ( )
   * @related getHeight ( )
   * @related getCenter ( )
   */
  public RPoint getTopRight(){
    RRectangle orig = this.getBounds();
    return new RPoint(orig.getMaxX(), orig.getMinY());
  }


  /**
   * Use this method to get the bottom left position of the element. 
   * @eexample getX
   * @related getTopRight ( )
   * @related getBottomLeft ( )
   * @related getBottomRight ( )
   * @related getWidth ( )
   * @related getHeight ( )
   * @related getCenter ( )
   */
  public RPoint getBottomLeft(){
    RRectangle orig = this.getBounds();
    return new RPoint(orig.getMinX(), orig.getMaxY());
  }


  /**
   * Use this method to get the bottom right position of the element. 
   * @eexample getX
   * @related getTopRight ( )
   * @related getBottomLeft ( )
   * @related getBottomRight ( )
   * @related getWidth ( )
   * @related getHeight ( )
   * @related getCenter ( )
   */
  public RPoint getBottomRight(){
    RRectangle orig = this.getBounds();
    return new RPoint(orig.getMaxX(), orig.getMaxY());
  }


  /**
   * Use this method to get the x (left side) position of the element. 
   * @eexample getX
   * @return float, the x coordinate of the element
   * @related getY ( )
   * @related getWidth ( )
   * @related getHeight ( )
   * @related getCenter ( )
   */
  public float getX(){
    RRectangle orig = this.getBounds();
    return orig.getMinX();
  }

  /**
   * Use this method to get the y position of the element. 
   * @eexample getY
   * @return float, the y coordinate of the element
   * @related getY ( )
   * @related getWidth ( )
   * @related getHeight ( )
   * @related getCenter ( )
   */
  public float getY(){
    RRectangle orig = this.getBounds();
    return orig.getMinY();
  }

  /**
   * Use this method to get the original height of the element. 
   * @eexample getOrigHeight
   * @return float, the original height of the element before applying any transformations
   * @related getCenter ( )
   */
  public float getOrigHeight(){
    return origHeight != 0.0 ? origHeight : getHeight();
  }

  /**
   * Use this method to get the original width of the element. 
   * @eexample getOrigWidth
   * @return float, the original width of the element before applying any transformations
   * @related getCenter ( )
   */
  public float getOrigWidth(){
    return origWidth != 0.0 ? origWidth : getWidth();
  }

  protected void updateOrigParams(){
    this.origWidth = this.getWidth();
    this.origHeight = this.getHeight();
  }

  /**
   * Use this method to get the width of the element. 
   * @eexample getWidth
   * @return float, the width of the element
   * @related getCenter ( )
   */
  public float getWidth(){
    RRectangle orig = this.getBounds();
    return orig.getMaxX()-orig.getMinX();
  }


  /**
   * Use this method to get the height of the element. 
   * @eexample getHeight
   * @return float, the height of the element
   * @related getCenter ( )
   */
  public float getHeight(){
    RRectangle orig = this.getBounds();
    return orig.getMaxY()-orig.getMinY();
  }

  
  /**
   * Use this method to get the center point of the element.
   * @eexample RGroup_getCenter
   * @return RPoint, the center point of the element
   * @related getBounds ( )
   */
  public RPoint getCenter(){
    RRectangle c = getBounds();
    return new RPoint((c.getMaxX() + c.getMinX())/2,(c.getMaxY() + c.getMinY())/2);
  }
  
  /**
   * Use this method to get the centroid of the element.
   * @eexample RGroup_getCentroid
   * @return RPoint, the centroid point of the element
   * @related getBounds ( )
   * @related getCenter ( )
   */
  public RPoint getCentroid(){
    RPoint[] ps = getPoints();
    
    float areaAcc = 0.0f;
    float xAcc = 0.0f;
    float yAcc = 0.0f;
    
    for(int i=0;i<ps.length-1;i++)
      {
        areaAcc += ps[i].x*ps[i+1].y - ps[i+1].x*ps[i].y;
        xAcc += (ps[i].x + ps[i+1].x)*(ps[i].x*ps[i+1].y - ps[i+1].x*ps[i].y);
        yAcc += (ps[i].y + ps[i+1].y)*(ps[i].x*ps[i+1].y - ps[i+1].x*ps[i].y);
      }
    areaAcc /= 2.0f;
    RPoint p = new RPoint(xAcc/(6.0f*areaAcc), yAcc/(6.0f*areaAcc));
    return p;
  }
  
  /**
   * Use this method to get the area of an element.
   * @eexample RGroup_getArea
   * @return float, the area point of the element
   * @related getBounds ( )
   * @related getCenter ( )
   * @related getCentroid ( )
   */
  public float getArea(){
    RPoint[] ps = getPoints();
    
    float areaAcc = 0.0f;
    for(int i=0;i<ps.length-1;i++)
      {
        areaAcc += ps[i].x*ps[i+1].y - ps[i+1].x*ps[i].y;
      }
    areaAcc /= 2.0f;
    return Math.abs(areaAcc);
  }

  /**
   * Use this method to know if the shape is inside a graphics object. This might be useful if we want to delete objects that go offscreen.
   * @eexample RShape_isIn
   * @usage Geometry
   * @param g  the graphics object
   * @return boolean, whether the shape is in or not the graphics object
   */
  public boolean isIn(PGraphics g){
    RRectangle c = getBounds();
    float x0 = g.screenX(c.topLeft.x, c.topLeft.y);
    float y0 = g.screenY(c.topLeft.x, c.topLeft.y);
    float x1 = g.screenX(c.bottomRight.x, c.topLeft.y);
    float y1 = g.screenY(c.bottomRight.x, c.topLeft.y);
    float x2 = g.screenX(c.bottomRight.x, c.bottomRight.y);
    float y2 = g.screenY(c.bottomRight.x, c.bottomRight.y);
    float x3 = g.screenX(c.topLeft.x, c.bottomRight.y);
    float y3 = g.screenY(c.topLeft.x, c.bottomRight.y);
    
    float xmax = Math.max(Math.max(x0,x1),Math.max(x2,x3));
    float ymax = Math.max(Math.max(y0,y1),Math.max(y2,y3));
    float xmin = Math.min(Math.min(x0,x1),Math.min(x2,x3));
    float ymin = Math.min(Math.min(y0,y1),Math.min(y2,y3));
    
    return !((xmax < 0 || xmin > g.width) && (ymax < 0 || ymin > g.height));
  }
  
  public boolean isIn(PApplet g){
    RRectangle c = getBounds();
    float x0 = g.screenX(c.topLeft.x, c.topLeft.y);
    float y0 = g.screenY(c.topLeft.x, c.topLeft.y);
    float x1 = g.screenX(c.bottomRight.x, c.topLeft.y);
    float y1 = g.screenY(c.bottomRight.x, c.topLeft.y);
    float x2 = g.screenX(c.bottomRight.x, c.bottomRight.y);
    float y2 = g.screenY(c.bottomRight.x, c.bottomRight.y);
    float x3 = g.screenX(c.topLeft.x, c.bottomRight.y);
    float y3 = g.screenY(c.topLeft.x, c.bottomRight.y);
    
    float xmax = Math.max(Math.max(x0,x1),Math.max(x2,x3));
    float ymax = Math.max(Math.max(y0,y1),Math.max(y2,y3));
    float xmin = Math.min(Math.min(x0,x1),Math.min(x2,x3));
    float ymin = Math.min(Math.min(y0,y1),Math.min(y2,y3));
    
    return !((xmax < 0 || xmin > g.width) && (ymax < 0 || ymin > g.height));
  }


  /**
   * Use this method to get the transformation matrix in order to fit and center the element on the canvas. Scaling and translation damping parameters are available, in order to create animations.
   * @eexample RGeomElem_getCenteringTransf
   * @return RMatrix, the transformation matrix
   * @param g  the canvas to which to fit and center the path
   * @param margin  the margin to take into account when fitting
   * @param sclDamping  a value from 0 to 1. The damping coefficient for the scale, if the value is 0, then no scaling is applied.
   * @param trnsDamping  a value from 0 to 1. The damping coefficient for the translation, if the value is 0, then no translation is applied.
   * @related getBounds ( )
   */
  public RMatrix getCenteringTransf(PGraphics g, float margin, float sclDamping, float trnsDamping) throws RuntimeException{
    RMatrix transf;
    
    float mrgn = margin*2;
    RRectangle c = getBounds();
    float scl = (float)Math.min((g.width-mrgn)/(float)Math.abs(c.getMinX() - c.getMaxX()),(g.height-mrgn)/(float)Math.abs(c.getMinY()-c.getMaxY()));
    RPoint trns = getCenter();
    transf = new RMatrix();
    
    if(sclDamping!=0){
      transf.scale(1+(scl-1)*sclDamping);
    }
    
    if(trnsDamping!=0){
      transf.translate(-trns.x*trnsDamping, -trns.y*trnsDamping);
    }
    
    return transf;
  }
  
  public RMatrix getCenteringTransf(PGraphics g) throws RuntimeException{
    return getCenteringTransf(g, 0, 1, 1);
  }
  
  public RMatrix getCenteringTransf(PGraphics g, float margin) throws RuntimeException{
    return getCenteringTransf(g, margin, 1, 1);
  }
  
  public void centerIn(PGraphics g){
    transform(getCenteringTransf(g));
  }

  public void centerIn(PGraphics g, float margin){
    transform(getCenteringTransf(g, margin, 1, 1));
  }
  
  public void centerIn(PGraphics g, float margin, float sclDamping, float trnsDamping) throws RuntimeException{
    transform(getCenteringTransf(g, margin, sclDamping, trnsDamping));
  }
  
  /**
   * Apply a translation to the element, given translation coordinates.
   * @eexample RGeomElem_translate
   * @usage Geometry
   * @param tx  the coefficient of x translation
   * @param ty  the coefficient of y translation
   * @related transform ( )
   * @related rotate ( )
   * @related scale ( )
   */
  public void translate(float tx, float ty)
  {
    RMatrix transf = new RMatrix();
    transf.translate(tx, ty);
    transform(transf);
  }

  /**
   * Apply a translation to the element, given a point.
   * @eexample RGeomElem_translate
   * @usage Geometry
   * @param t  the translation vector to be applied
   * @related transform ( )
   * @related rotate ( )
   * @related scale ( )
   */  
  public void translate(RPoint t)
  {
    RMatrix transf = new RMatrix();
    transf.translate(t);
    transform(transf);  }
  
  /**
   * Apply a rotation to the element, given an angle and optionally a rotation center.
   * @eexample RPoint_rotate
   * @usage Geometry
   * @param angle  the angle of rotation to be applied
   * @param vx  the x coordinate of the center of rotation
   * @param vy  the y coordinate of the center of rotation
   * @related transform ( )
   * @related translate ( )
   * @related scale ( )
   */
  public void rotate(float angle, float vx, float vy)
  {
    RMatrix transf = new RMatrix();
    transf.rotate(angle, vx, vy);
    transform(transf);
  }

  public void rotate(float angle)
  {
    RMatrix transf = new RMatrix();
    transf.rotate(angle);
    transform(transf);
  }
  
  /**
   * Apply a rotation to the element, given an angle and optionally a rotation center.
   * @eexample RPoint_rotate
   * @usage Geometry
   * @param angle  the angle of rotation to be applied
   * @param v  the position vector of the center of rotation
   * @related transform ( )
   * @related translate ( )
   * @related scale ( )
   */  
  public void rotate(float angle, RPoint v)
  {
    RMatrix transf = new RMatrix();
    transf.rotate(angle, v);
    transform(transf);
  }
  
  /**
   * Apply a scale to the element, given scaling factors and optionally a scaling center.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param sx  the scaling coefficient over the x axis
   * @param sy  the scaling coefficient over the y axis
   * @param p  the position vector of the center of the scaling
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale (float sx, float sy, RPoint p)
  {
    RMatrix transf = new RMatrix();
    transf.scale(sx, sy, p);
    transform(transf);
  }

  public void scale (float sx, float sy)
  {
    RMatrix transf = new RMatrix();
    transf.scale(sx, sy);
    transform(transf);
  }

  /**
   * Apply a scale to the element, given scaling factors and optionally a scaling center.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param sx  the scaling coefficient over the x axis
   * @param sy  the scaling coefficient over the y axis
   * @param x  x coordinate of the position vector of the center of the scaling
   * @param y  y coordinate of the position vector of the center of the scaling
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale (float sx, float sy, float x, float y)
  {
    RMatrix transf = new RMatrix();
    transf.scale(sx, sy, x, y);
    transform(transf);
  }
  
  /**
   * Apply a scale to the element, given scaling factors and optionally a scaling center.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param s  the scaling coefficient for a uniform scaling
   * @param p  the position vector of the center of the scaling
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale (float s, RPoint p)
  {
    RMatrix transf = new RMatrix();
    transf.scale(s, p);
    transform(transf);
  }

  public void scale (float s)
  {
    RMatrix transf = new RMatrix();
    transf.scale(s);
    transform(transf);
  }

  /**
   * Apply a scale to the element, given scaling factors and optionally a scaling center.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param s  the scaling coefficient for a uniform scaling
   * @param x  x coordinate of the position vector of the center of the scaling
   * @param y  y coordinate of the position vector of the center of the scaling
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale (float s, float x, float y)
  {
    RMatrix transf = new RMatrix();
    transf.scale(s, x, y);
    transform(transf);
  }

  /**
   * Apply a horizontal skew to the element, given skewing angle
   * @eexample RMatrix_skewing
   * @param angle  skewing angle
   * @usage Geometry
   * @related rotate ( )
   * @related scale ( )
   * @related translate ( )
   */
  public void skewX(float angle)
  {
    RMatrix transf = new RMatrix();
    transf.skewY(angle);
    transform(transf);
  }
  
  /**
   * Apply a vertical skew to the element, given skewing angle
   * @eexample RMatrix_skewing
   * @param angle  skewing angle
   * @usage Geometry
   * @related rotate ( )
   * @related scale ( )
   * @related translate ( )
   */
  public void skewY(float angle)
  {
    RMatrix transf = new RMatrix();
    transf.skewY(angle);
    transform(transf);
  }
  
  /**
   * Apply a shear to the element, given shearing factors
   * @eexample RMatrix_translate
   * @param shx  x coordinate shearing
   * @param shy  y coordinate shearing
   * @usage Geometry
   * @related rotate ( )
   * @related scale ( )
   * @related translate ( )
   */
  public void shear(float shx, float shy)
  {
    RMatrix transf = new RMatrix();
    transf.shear(shx, shy);
    transform(transf);
  }
}
