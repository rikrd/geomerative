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
 * RGeomElem is an interface to any Geometric element that can be drawn and transformed, such as Shapes, Polygons or Meshes.
 * @invisible
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
  
  // Functions dependent of the type of element
  // They must be overrided
  public abstract void draw(PGraphics g);
  public abstract void draw(PApplet g);
  
  public void draw(){
    this.draw(RGeomerative.parent());
  }


  public abstract RPoint getPoint(float t);

  public abstract RPoint getTangent(float t);

  public abstract RPoint[] getHandles();

  public abstract RPoint[] getPoints();

  public abstract RPoint[] getTangents();

  public abstract RPoint[][] getHandlePaths();

  public abstract RPoint[][] getPointPaths();

  public abstract RPoint[][] getTangentPaths();

  public abstract boolean contains(RPoint p);

  public abstract int getType();

  //public abstract RMesh toMesh();
  //public abstract RPolygon toPolygon();
  public abstract RShape toShape();
  public void print(){};
  
  protected float[] lenCurves;
  protected float lenCurve = -1F;

  public String id = "";
  public PImage texture = null;
  
  public boolean fillDef = false;
  public boolean fill = false;
  public int fillColor = 0xff000000;

  public boolean fillAlphaDef = false;
  public int fillAlpha = 0xff000000;

  public boolean strokeDef = false;
  public boolean stroke = false;
  public int strokeColor = 0xff000000;

  public boolean strokeAlphaDef = false;
  public int strokeAlpha = 0xff000000;
  
  public boolean strokeWeightDef = false;
  public float strokeWeight = 1F;

  public boolean strokeCapDef = false;
  public int strokeCap = RGeomerative.PROJECT;

  public boolean strokeJoinDef = false;
  public int strokeJoin = RGeomerative.MITER;
  
  private boolean oldFill = false;
  private int oldFillColor = 0;
  
  private boolean oldStroke = false;
  private int oldStrokeColor = 0;
  private float oldStrokeWeight = 1F;
  private int oldStrokeCap = RGeomerative.PROJECT;
  private int oldStrokeJoin = RGeomerative.MITER;
  
  protected void saveContext(PGraphics g){
    oldFill = g.fill;
    oldFillColor = g.fillColor;
    oldStroke = g.stroke;
    oldStrokeColor = g.strokeColor;
    oldStrokeWeight = g.strokeWeight;
    oldStrokeCap = g.strokeCap;
    oldStrokeJoin = g.strokeJoin;
  }

  protected void saveContext(PApplet p){
    oldFill = p.g.fill;
    oldFillColor = p.g.fillColor;
    oldStroke = p.g.stroke;
    oldStrokeColor = p.g.strokeColor;
    oldStrokeWeight = p.g.strokeWeight;    
    oldStrokeCap = p.g.strokeCap;
    oldStrokeJoin = p.g.strokeJoin;
  }

  protected void saveContext(){
    saveContext(RGeomerative.parent());
  }

  protected void restoreContext(PGraphics g){
    g.fill(oldFillColor);
    if(!oldFill){
      g.noFill();
    }

    g.stroke(oldStrokeColor);
    g.strokeWeight(oldStrokeWeight);

    try{
      g.strokeCap(oldStrokeCap);
      g.strokeJoin(oldStrokeJoin);
    }catch(RuntimeException e){}

    if(!oldStroke){
      g.noStroke();
    }
  }

  protected void restoreContext(PApplet p){
    p.fill(oldFillColor);
    if(!oldFill){
      p.noFill();
    }

    p.stroke(oldStrokeColor);
    p.strokeWeight(oldStrokeWeight);

    try{
      p.strokeCap(oldStrokeCap);
      p.strokeJoin(oldStrokeJoin);
    }catch(RuntimeException e){}

    if(!oldStroke){
      p.noStroke();
    }    
  }

  protected void restoreContext(){
    restoreContext(RGeomerative.parent());
  }

  protected void setContext(PGraphics g){
    /*
    RGeomerative.parent().println("Setting context");
    RGeomerative.parent().println("  fillDef: ");
    RGeomerative.parent().println(fillDef);
    RGeomerative.parent().println("  fillColor: ");
    RGeomerative.parent().println(RGeomerative.parent().hex(fillColor));

    RGeomerative.parent().println("  strokeDef: ");
    RGeomerative.parent().println(strokeDef);
    RGeomerative.parent().println("  strokeColor: ");
    RGeomerative.parent().println(RGeomerative.parent().hex(strokeColor));
    RGeomerative.parent().println("  strokeWeight: ");
    RGeomerative.parent().println(strokeWeight);
    */

    if(fillAlphaDef){
      if(fillDef){
        fillColor = ((fillAlpha << 24) & 0xff000000) | (fillColor & 0x00ffffff);
      }else{
        if(g.fill){
          g.fill(((fillAlpha << 24) & 0xff000000) | (g.fillColor & 0x00ffffff));
        }
      }
    }

    if(fillDef){
      g.fill(fillColor);
      if(!fill){
        g.noFill();
      }
    }

    if(strokeWeightDef){      
      g.strokeWeight(strokeWeight);
    }

    try{
      if(strokeCapDef){      
        g.strokeCap(strokeCap);
      }
      
      if(strokeJoinDef){      
        g.strokeJoin(strokeJoin);
      }
    }catch(RuntimeException e){}

    if(strokeAlphaDef){
      if(strokeDef){
        strokeColor = ((strokeAlpha << 24) & 0xff000000) | (strokeColor & 0x00ffffff);
      }else{
        if(g.stroke){
          g.stroke(((strokeAlpha << 24) & 0xff000000) | (g.strokeColor & 0x00ffffff));
        }
      }
    }
    
    if(strokeDef){      
      g.stroke(strokeColor);
      if(!stroke){
        g.noStroke();
      }
    }
  }

  protected void setContext(PApplet p){
    
    if(fillAlphaDef){
      if(fillDef){
        fillColor = ((fillAlpha << 24) & 0xff000000) | (fillColor & 0x00ffffff);
      }else{
        if(p.g.fill){
          p.fill(((fillAlpha << 24) & 0xff000000) | (p.g.fillColor & 0x00ffffff));
        }
      }
    }
    
    if(fillDef){
      p.fill(fillColor);
      if(!fill){
        p.noFill();
      }
    }

    if(strokeWeightDef){      
      p.strokeWeight(strokeWeight);
    }

    try{
      if(strokeCapDef){      
        p.strokeCap(strokeCap);
      }
      
      if(strokeJoinDef){      
        p.strokeJoin(strokeJoin);
      }
    }catch(RuntimeException e){}

    if(strokeAlphaDef){
      if(strokeDef){
        strokeColor = ((strokeAlpha << 24) & 0xff000000) | (strokeColor & 0x00ffffff);
      }else{
        if(p.g.stroke){
          p.stroke(((strokeAlpha << 24) & 0xff000000) | (p.g.strokeColor & 0x00ffffff));
        }
      }
    }

    if(strokeDef){
      p.stroke(strokeColor);
      if(!stroke){
        p.noStroke();
      }
    }
  }

  protected void setContext(){
    setContext(RGeomerative.parent());
  }

  protected void setStyle(RGeomElem p){
    id = p.id;
    texture = p.texture;
    
    fillDef = p.fillDef;
    fill = p.fill;
    fillColor = p.fillColor;
    fillAlphaDef = p.fillAlphaDef;
    fillAlpha = p.fillAlpha;
    
    strokeDef = p.strokeDef;
    stroke = p.stroke;
    strokeColor = p.strokeColor;
    strokeAlphaDef = p.strokeAlphaDef;
    strokeAlpha = p.strokeAlpha;

    strokeWeightDef = p.strokeWeightDef;
    strokeWeight = p.strokeWeight;

    strokeCapDef = p.strokeCapDef;
    strokeCap = p.strokeCap;

    strokeJoinDef = p.strokeJoinDef;
    strokeJoin = p.strokeJoin;
  }

  protected void setStyle(String styleString){
    //RGeomerative.parent().println("Style parsing: " + styleString);
    String[] styleTokens = PApplet.splitTokens(styleString, ";");
    
    for(int i = 0; i < styleTokens.length; i++){
      String[] tokens = PApplet.splitTokens(styleTokens[i], ":");
      
      tokens[0] = PApplet.trim(tokens[0]);
      
      if(tokens[0].equals("fill")){
        setFill(tokens[1]);
        
      }else if(tokens[0].equals("fill-opacity")){
        setFillAlpha(tokens[1]);        
        
      }else if(tokens[0].equals("stroke")){
        setStroke(tokens[1]);
      
      }else if(tokens[0].equals("stroke-width")){
        setStrokeWeight(tokens[1]);

      }else if(tokens[0].equals("stroke-linecap")){
        setStrokeCap(tokens[1]);

      }else if(tokens[0].equals("stroke-linejoin")){
        setStrokeJoin(tokens[1]); 
       
      }else if(tokens[0].equals("stroke-opacity")){
        setStrokeAlpha(tokens[1]);
     
      }else if(tokens[0].equals("opacity")){
        setAlpha(tokens[1]);
        
      }else{
        PApplet.println("Attribute '" + tokens[0] + "' not known.  Ignoring it.");
      }
    }
  }

  public void setFill(boolean _fill){
    fillDef = true;
    fill = _fill;
  }

  public void setFill(int _fillColor){
    //RGeomerative.parent().println("Setting fill by int: " + RGeomerative.parent().hex(_fillColor));
    setFill(true);
    fillColor = (fillColor & 0xff000000) | (_fillColor & 0x00ffffff);
  }

  public void setFill(String str){
    //RGeomerative.parent().println("id: " + id);
    //RGeomerative.parent().println("  set fill: " + str);
    if(str.equals("none")){
      setFill(false);

    }else{
      setFill(getColor(str));

    }
    //RGeomerative.parent().println("  fillColor after: " + RGeomerative.parent().hex(fillColor));
  }

  public void setStroke(boolean _stroke){
    strokeDef = true;
    stroke = _stroke;
  }

  public void setStroke(int _strokeColor){
    setStroke(true);
    strokeColor = (strokeColor & 0xff000000) | (_strokeColor & 0x00ffffff);
  }

  public void setStroke(String str){
    //RGeomerative.parent().println("  set stroke: " + str);
    if(str.equals("none")){
      setStroke(false);

    }else{
      setStroke(getColor(str));
      
    }
  }

  public void setStrokeWeight(float value){
    //RGeomerative.parent().println("  set strokeWeight by float: " + value);
    strokeWeightDef = true;
    strokeWeight = value;
  }

  public void setStrokeWeight(String str){
    //RGeomerative.parent().println("  set strokeWeight by String: " + str);
    if(str.endsWith("px")){
      setStrokeWeight(PApplet.parseFloat(str.substring(0, str.length() - 2)));
    }else{
      setStrokeWeight(PApplet.parseFloat(str));
    }

    
  }

  public void setStrokeCap(String str){
    //RGeomerative.parent().println("  set stroke-cap: " + str);
    strokeCapDef = true;

    if(str.equals("butt")){
      strokeCap = RGeomerative.PROJECT;

    }else if(str.equals("round")){
      strokeCap = RGeomerative.ROUND;

    }else if(str.equals("square")){
      strokeCap = RGeomerative.SQUARE;

    }
  }

  public void setStrokeJoin(String str){
    //RGeomerative.parent().println("  set stroke-cap: " + str);
    strokeJoinDef = true;

    if(str.equals("miter")){
      strokeJoin = RGeomerative.MITER;

    }else if(str.equals("round")){
      strokeJoin = RGeomerative.ROUND;

    }else if(str.equals("bevel")){
      strokeJoin = RGeomerative.BEVEL;

    }
  }

  public void setStrokeAlpha(int opacity){
    strokeAlphaDef = true;
    strokeAlpha = opacity;
  }

  public void setStrokeAlpha(String str){
    setStrokeAlpha((int)(PApplet.parseFloat(str) * 255F));
  }

  public void setFillAlpha(int opacity){
    fillAlphaDef = true;
    fillAlpha = opacity;
  }

  public void setFillAlpha(String str){
    //RGeomerative.parent().println("  set fillOpacity: " + str);
    setFillAlpha((int)(PApplet.parseFloat(str) * 255F));
    //RGeomerative.parent().println("  fillColor after: " + RGeomerative.parent().hex(fillColor));
  }  

  public void setAlpha(float opacity){
    //RGeomerative.parent().println("Setting float opacity: " + opacity);
    setAlpha((int)(opacity * 255F));
  }

  public void setAlpha(int opacity){
    /*
    RGeomerative.parent().println("setting opacity: " + RGeomerative.parent().hex(opacity));    
    
    RGeomerative.parent().println("  fillColor before: " + RGeomerative.parent().hex(fillColor));
    RGeomerative.parent().println("  strokeColor before: " + RGeomerative.parent().hex(fillColor));
    */

    setFillAlpha(opacity);
    setStrokeAlpha(opacity);

    //fillColor = ((opacity << 24) & 0xff000000) | (fillColor & 0x00ffffff);
    //strokeColor = ((opacity << 24) & 0xff000000) | (strokeColor & 0x00ffffff);

    /*    
    RGeomerative.parent().println("  fillColor now: " + RGeomerative.parent().hex(fillColor));
    RGeomerative.parent().println("  strokeColor now: " + RGeomerative.parent().hex(fillColor));
    */
  }

  public void setAlpha(String str){
    //RGeomerative.parent().println("Setting string opacity: " + str);
    setAlpha(PApplet.parseFloat(str));
  }

  public void setId(String str){
    this.id = str;
  }

  protected void calculateCurveLengths(){
    PApplet.println("Feature not yet implemented for this class.");
  }

  /**
   * Use this to return the points on the curve.  It returns the points in the way of an array of RPoint.
   * @eexample getCurveLength
   * @return float[], the arclengths of each command of the subshape.
   * */
  public float[] getCurveLengths(){
    /* If the cache with the commands lengths is empty, we fill it up */
    if(lenCurves == null){
      calculateCurveLengths();
    }
    
    return lenCurves;
  }
  
  /**
   * Use this to return the points on the curve.  It returns the points in the way of an array of RPoint.
   * @eexample getCurveLength
   * @return float, the arclength of the subshape.
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

  private int getColor(String colorString){
    colorString = PApplet.trim(colorString);
    
    if(colorString.startsWith("#")){
      return PApplet.unhex("FF"+colorString.substring(1));
    }else if(colorString.startsWith("rgb")){
      String[] rgb = PApplet.splitTokens(colorString, "rgb( , )");
      return (int)RGeomerative.parent().color(PApplet.parseInt(rgb[0]), PApplet.parseInt(rgb[1]), PApplet.parseInt(rgb[2]));
    }else{
      if(colorString.equals("black")){
        return 0;

      }else if(colorString.equals("red")){
        return RGeomerative.parent().color(255, 0, 0);

      }else if(colorString.equals("green")){
        return RGeomerative.parent().color(0, 255, 0);

      }else if(colorString.equals("blue")){
        return RGeomerative.parent().color(0, 0, 255);

      }else if(colorString.equals("yellow")){
        return RGeomerative.parent().color(0, 255, 255);

      }
    }
    return 0;
  }

  // Functions independent of the type of element
  // No need of being overrided
  public void transform(RMatrix m){   
    RPoint[] ps = getHandles();
    if(ps != null){
      for(int i=0; i<ps.length; i++){
        ps[i].transform(m);
      }
    }
  }

  /**
   * Transform the geometric object to fit in a rectangle defined by the parameters passed.
   * @eexample getBounds
   * @return RContour, the bounding box of the element in the form of a fourpoint contour
   * @related getCenter ( )
   */
  public void transform(float x, float y, float w, float h, boolean keepAspectRatio){
    RMatrix mtx = new RMatrix();
    RContour orig = this.getBounds();
    float orig_w = orig.points[2].x-orig.points[0].x;
    float orig_h = orig.points[2].y-orig.points[0].y;
    
    mtx.translate(-orig.points[0].x, -orig.points[0].y);
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
   * @return RContour, the bounding box of the element in the form of a fourpoint contour
   * @related getCenter ( )
   */
  public RContour getBounds(){
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
        else if( tempx > xmax )
          {
            xmax = tempx;
          }
        if( tempy < ymin )
          {
          ymin = tempy;
          }
       else if( tempy > ymax )
          {
            ymax = tempy;
          }
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
   * Use this method to get the width of the element. 
   * @eexample getWidth
   * @return float, the width of the element
   * @related getCenter ( )
   */
  public float getWidth(){
    RContour orig = this.getBounds();
    return orig.points[2].x-orig.points[0].x;
  }


  /**
   * Use this method to get the height of the element. 
   * @eexample getHeight
   * @return float, the height of the element
   * @related getCenter ( )
   */
  public float getHeight(){
    RContour orig = this.getBounds();
    return orig.points[2].y-orig.points[0].y;
  }

  
  /**
   * Use this method to get the center point of the element.
   * @eexample RGroup_getCenter
   * @return RPoint, the center point of the element
   * @related getBounds ( )
   */
  public RPoint getCenter(){
    RContour c = getBounds();
    return new RPoint((c.points[2].x + c.points[0].x)/2,(c.points[2].y + c.points[0].y)/2);
  }
  
  /**
   * Use this method to get the centroid of the element.
   * @eexample RGroup_getCentroid
   * @return RPoint, the centroid point of the element
   * @related getBounds ( )
   * @related getCenter ( )
   */
  public RPoint getCentroid(){
    RPoint[] ps = getHandles();
    
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
    RPoint[] ps = getHandles();
    
    float areaAcc = 0.0f;
    for(int i=0;i<ps.length-1;i++)
      {
        areaAcc += ps[i].x*ps[i+1].y - ps[i+1].x*ps[i].y;
      }
    areaAcc /= 2.0f;
    return areaAcc;
  }

  /**
   * Use this method to know if the shape is inside a graphics object. This might be useful if we want to delete objects that go offscreen.
   * @eexample RShape_isIn
   * @usage Geometry
   * @param PGraphics g, the graphics object
   * @return boolean, whether the shape is in or not the graphics object
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
    
    float xmax = Math.max(Math.max(x0,x1),Math.max(x2,x3));
    float ymax = Math.max(Math.max(y0,y1),Math.max(y2,y3));
    float xmin = Math.min(Math.min(x0,x1),Math.min(x2,x3));
    float ymin = Math.min(Math.min(y0,y1),Math.min(y2,y3));
    
    return !((xmax < 0 || xmin > g.width) && (ymax < 0 || ymin > g.height));
  }
  
  public boolean isIn(PApplet g){
    RContour c = getBounds();
    float x0 = g.screenX(c.points[0].x,c.points[0].y);
    float y0 = g.screenY(c.points[0].x,c.points[0].y);
    float x1 = g.screenX(c.points[1].x,c.points[1].y);
    float y1 = g.screenY(c.points[1].x,c.points[1].y);
    float x2 = g.screenX(c.points[2].x,c.points[2].y);
    float y2 = g.screenY(c.points[2].x,c.points[2].y);
    float x3 = g.screenX(c.points[3].x,c.points[3].y);
    float y3 = g.screenY(c.points[3].x,c.points[3].y);
    
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
   * @param PGraphics g, the canvas to which to fit and center the path
   * @param float margin, the margin to take into account when fitting
   * @param float sclDamping, a value from 0 to 1. The damping coefficient for the scale, if the value is 0, then no scaling is applied.
   * @param float trnsDamping, a value from 0 to 1. The damping coefficient for the translation, if the value is 0, then no translation is applied.
   * @related getBounds ( )
   */
  public RMatrix getCenteringTransf(PGraphics g, float margin, float sclDamping, float trnsDamping) throws RuntimeException{
    RMatrix transf;
    
    float mrgn = margin*2;
    RContour c = getBounds();
    float scl = (float)Math.min((g.width-mrgn)/(float)Math.abs(c.points[0].x-c.points[2].x),(g.height-mrgn)/(float)Math.abs(c.points[0].y-c.points[2].y));
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
  
  public void centerIn(PGraphics g, float margin, float sclDamping, float trnsDamping) throws RuntimeException{
    transform(getCenteringTransf(g, margin, sclDamping, trnsDamping));
  }
  
  /**
   * Use this to apply a translation to the point.
   * @eexample RGeomElem_translate
   * @usage Geometry
   * @param tx float, the coefficient of x translation
   * @param ty float, the coefficient of y translation
   * @param t RPoint, the translation vector to be applied
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
  
  public void translate(RPoint t)
  {
    RMatrix transf = new RMatrix();
    transf.translate(t);
    transform(transf);  }
  
  /**
   * Use this to apply a rotation to the point.
   * @eexample RPoint_rotate
   * @usage Geometry
   * @param angle float, the angle of rotation to be applied
   * @param vx float, the x coordinate of the center of rotation
   * @param vy float, the y coordinate of the center of rotation
   * @param v RPoint, the position vector of the center of rotation
   * @related transform ( )
   * @related translate ( )
   * @related scale ( )
   */
  public void rotate(float angle)
  {
    RMatrix transf = new RMatrix();
    transf.rotate(angle);
    transform(transf);
  }
  
  public void rotate(float angle, float vx, float vy)
  {
    RMatrix transf = new RMatrix();
    transf.rotate(angle, vx, vy);
    transform(transf);
  }
  
  public void rotate(float angle, RPoint v)
  {
    RMatrix transf = new RMatrix();
    transf.rotate(angle, v);
    transform(transf);
  }
  
  /**
   * Use this to scale the point.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param sx float, the scaling coefficient over the x axis
   * @param sy float, the scaling coefficient over the y axis
   * @param s float, the scaling coefficient for a uniform scaling
   * @param s RPoint, the scaling vector
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale (float sx, float sy)
  {
    RMatrix transf = new RMatrix();
    transf.scale(sx, sy);
    transform(transf);
  }
  
  public void scale (float s)
  {
    RMatrix transf = new RMatrix();
    transf.scale(s);
    transform(transf);
  }
}
