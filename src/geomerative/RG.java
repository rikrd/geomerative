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
 * RG is a static class containing all the states, modes, etc..
 * Geomerative is mostly used by calling RG methods. e.g.  RShape s = RG.getEllipse(30, 40, 80, 80)
 */
public class RG implements PConstants{
  /**
   * @invisible
   */
  private static boolean initialized = false;

  /**
   * @invisible
   */
  private static PApplet parent;

  /**
   * @invisible
   */
  public static boolean ignoreStyles = false;

  /**
   * @invisible
   */
  public static boolean useFastClip = true;

  /**
   * The adaptor adapts the shape to a particular shape by adapting each of the groups points.  This can cause deformations of the individual elements in the group.
   */
  public final static int BYPOINT = 0;

  /**
   * The adaptor adapts the shape to a particular shape by adapting each of the groups elements positions.  This mantains the proportions of the shapes.
   */
  public final static int BYELEMENTPOSITION = 1;

  /**
   * The adaptor adapts the shape to a particular shape by adapting each of the groups elements indices.  This mantains the proportions of the shapes.
   */
  public final static int BYELEMENTINDEX = 2;

  /**
   * @invisible
   */
  static int adaptorType = BYELEMENTPOSITION;

  /**
   * @invisible
   */
  static float adaptorScale = 1F;

  /**
   * @invisible
   */
  static float adaptorLengthOffset = 0F;

  /**
   * ADAPTATIVE segmentator minimizes the number of segments avoiding perceptual artifacts like angles or cusps.  Use this in order to have polygons and meshes with the fewest possible vertices.
   */
  public static int ADAPTATIVE = RCommand.ADAPTATIVE;

  /**
   * UNIFORMLENGTH segmentator is the slowest segmentator and it segments the curve on segments of equal length.  This can be useful for very specific applications when for example drawing incrementaly a shape with a uniform speed.
   */
  public static int UNIFORMLENGTH = RCommand.UNIFORMLENGTH;

  /**
   * UNIFORMSTEP segmentator is the fastest segmentator and it segments the curve based on a constant value of the step of the curve parameter, or on the number of segments wanted.  This can be useful when segmpointsentating very often a Shape or when we know the amount of segments necessary for our specific application.
   */
  public static int UNIFORMSTEP = RCommand.UNIFORMSTEP;
  
  static int dpi = 72;

  /**
   * @invisible
   */
  public static class LibraryNotInitializedException extends NullPointerException{
    private static final long serialVersionUID = -3710605630786298671L;

    LibraryNotInitializedException(){
      super("Must call RG.init(this); before using this library.");
    }
  }

  /**
   * @invisible
   */
  public static class FontNotLoadedException extends NullPointerException{
    private static final long serialVersionUID = -3710605630786298672L;

    FontNotLoadedException(){
      super("Use RG.loadFont(filename) and RG.textFont(font, size) to load and set fonts first.");
    }
  }

  /**
   * @invisible
   */
  public static class NoPathInitializedException extends NullPointerException{
    private static final long serialVersionUID = -3710605630786298673L;

    NoPathInitializedException(){
      super("Must initialize a path by calling RG.beginShape() first.");
    }
  }


  static RShape shape;

  static RFont fntLoader = null;


  // Font methods
  /**
   * Load and get the font object that can be used in the textFont method.
   * @eexample loadFont
   * @param fontFile  the filename of the font to be loaded
   * @return RFont, the font object
   */
  public static RFont loadFont(String fontFile){
    RFont newFntLoader = new RFont(fontFile);
    if (fntLoader == null) fntLoader = newFntLoader;
    return newFntLoader;

  }

  /**
   * Draw text to the screen using the font set using the textFont method.
   * @eexample text
   * @param text  the string to be drawn on the screen
   */
  public static void text(String text){
    RShape grp = getText(text);
    grp.draw();
  }

  /**
   * Set the font object to be used in all text calls.
   * @eexample textFont
   * @param font  the font object to be set
   * @param size  the size of the font
   */
  public static void textFont(RFont font, int size){
    font.setSize(size);
    fntLoader = font;
  }

  /**
   * Get the shape corresponding to a text.  Use the textFont method to select the font and size.
   * @eexample getText
   * @param font  the filename of the font to be loaded
   * @param text  the string to be created
   * @param size  the size of the font to be used
   * @param align  the alignment. Use RG.CENTER, RG.LEFT or RG.RIGHT
   * @return RShape, the shape created
   */
  public static RShape getText(String text, String font, int size, int align){
    RFont tempFntLoader = new RFont(font, size, align);
    return tempFntLoader.toShape(text);
  }

  public static RShape getText(String text){
    if(fntLoader == null){
      throw new FontNotLoadedException();
    }

    return fntLoader.toShape(text);
  }


  // Shape methods
  /**
   * Draw a shape to a given position on the screen.
   * @eexample shape
   * @param shp  the shape to be drawn
   * @param x  the horizontal coordinate
   * @param y  the vertical coordinate
   * @param w  the width with which we draw the shape
   * @param h  the height with which we draw the shape
   */
  public static void shape(RShape shp, float x, float y, float w, float h){
    RShape tshp = new RShape(shp);

    RMatrix transf = new RMatrix();
    transf.translate(x, y);
    transf.scale(w / tshp.getOrigWidth(), h/ tshp.getOrigHeight());
    tshp.transform(transf);

    tshp.draw();
  }

  public static void shape(RShape shp, float x, float y){
    RShape tshp = new RShape(shp);

    RMatrix transf = new RMatrix();
    transf.translate(x, y);
    tshp.transform(transf);

    tshp.draw();
  }


  public static void shape(RShape shp){
    shp.draw();
  }

  /**
   * Create a shape from an array of point arrays.
   * @eexample createShape
   */
  public static RShape createShape(RPoint[][] points){
    return new RShape(points);
  }


  /**
   * Load a shape object from a file.
   * @eexample loadShape
   * @param filename  the SVG file to be loaded.  Must be in the data directory
   */
  public static RShape loadShape(String filename){
    RSVG svgLoader = new RSVG();
    return svgLoader.toShape(filename);
  }

  /**
   * Save a shape object to a file.
   * @eexample saveShape
   * @param filename  the SVG file to be saved.
   * @param shape  the shape to be saved.
   */
  public static void saveShape(String filename, RShape shape){
    RSVG svgSaver = new RSVG();
    String str = svgSaver.fromShape(shape);
    String[] strs = PApplet.split(str, "\n");
    RG.parent().saveStrings(filename, strs);
  }


  // Methods to create shapes
  /**
   * Begin to create a shape.
   * @eexample createShape
   */
  public static void beginShape(){
    shape = new RShape();
  }

  /**
   * Begin a new path in the current shape.  Can only be called inside beginShape() and endShape().
   * @param endMode  if called with RG.CLOSE it closes the current path before starting the new one.
   * @eexample createShape
   */
  public static void breakShape(int endMode){
    if (endMode == CLOSE) {
      shape.addClose();
    }

    shape.updateOrigParams();

    breakShape();
  }

  public static void breakShape(){
    shape.addPath();
  }

  /**
   * Add a vertex to the shape.  Can only be called inside beginShape() and endShape().
   * @eexample createShape
   * @param x  the x coordinate of the vertex
   * @param y  the y coordinate of the vertex
   */
  public static void vertex(float x, float y){
    if (shape.countPaths() == 0){
      shape.addMoveTo(x, y);
    }else{
      shape.addLineTo(x, y);
    }
  }

  /**
   * Add a bezierVertex to the shape.  Can only be called inside beginShape() and endShape().
   * @eexample createShape
   * @param cx1  the x coordinate of the first control point
   * @param cy1  the y coordinate of the first control point
   * @param cx2  the x coordinate of the second control point
   * @param cy2  the y coordinate of the second control point
   * @param x  the x coordinate of the end point
   * @param y  the y coordinate of the end point
   */
  public static void bezierVertex(float cx1, float cy1, float cx2, float cy2, float x, float y){
    if (shape.countPaths() == 0){
      throw new NoPathInitializedException();
    }else{
      shape.addBezierTo(cx1, cy1, cx2, cy2, x, y);
    }
  }

  /**
   * End the shape being created and draw it to the screen or the PGraphics passed as parameter.
   * @eexample createShape
   * @param g  the canvas on which to draw.  By default it draws on the screen
   */
  public static void endShape(PGraphics g){
    shape.draw(g);
    shape = null;
  }

  public static void endShape(){
    shape.draw();
    shape = null;
  }


  /**
   * End the shape being created and get it as an object.
   * @eexample getShape
   */
  public static RShape getShape(){
    RShape returningGroup = new RShape();
    returningGroup.addChild(shape);

    shape = null;

    returningGroup.updateOrigParams();

    return returningGroup;
  }

  /**
   * Get an ellipse as a shape object.
   * @eexample getEllipse
   * @param x  x coordinate of the center of the shape
   * @param y  y coordinate of the center of the shape
   * @param w  width of the ellipse
   * @param h  height of the ellipse
   * @return RShape, the shape created
   */
  public static RShape getEllipse(float x, float y, float w, float h){
    return RShape.createEllipse(x, y, w, h);
  }

  public static RShape getEllipse(float x, float y, float w){
    return getEllipse(x, y, w, w);
  }

  /**
   * Get a line as a shape object.
   * @eexample getLine
   * @param x1  x coordinate of the first point of the line
   * @param y1  y coordinate of the first point of the line
   * @param x2  x coordinate of the last point of the line
   * @param y2  y coordinate of the last point of the line
   * @return RShape, the shape created
   */
  public static RShape getLine(float x1, float y1, float x2, float y2){
    return RShape.createLine(x1, y1, x2, y2);
  }

  /**
   * Get an rectangle as a shape object.
   * @eexample getRect
   * @param x  x coordinate of the top left corner of the shape
   * @param y  y coordinate of the top left of the shape
   * @param w  width of the rectangle
   * @param h  height of the rectangle
   * @return RShape, the shape created
   */
  public static RShape getRect(float x, float y, float w, float h){
    return RShape.createRectangle(x, y, w, h);
  }

  public static RShape getRect(float x, float y, float w){
    return getRect(x, y, w, w);
  }

  /**
   * Get a star as a shape object.
   * @eexample getStar
   * @param x  x coordinate of the center of the shape
   * @param y  y coordinate of the center of the shape
   * @param widthBig  the outter width of the star polygon
   * @param widthSmall  the inner width of the star polygon
   * @param spikes  the amount of spikes on the star polygon
   * @return RShape, the shape created
   */
  public static RShape getStar(float x, float y, float widthBig, float widthSmall, int spikes){
    return RShape.createStar(x, y, widthBig, widthSmall, spikes);
  }


  /**
   * Get a ring as a shape object.
   * @eexample getRing
   * @param x  x coordinate of the center of the shape
   * @param y  y coordinate of the center of the shape
   * @param widthBig  the outter width of the ring polygon
   * @param widthSmall  the inner width of the ring polygon
   * @return RShape, the shape created
   */
  public static RShape getRing(float x, float y, float widthBig, float widthSmall){
    return RShape.createRing(x, y, widthBig, widthSmall);
  }


  // Transformation methods
  public static RShape centerIn(RShape grp, PGraphics g, float margin){
    RShape ret = new RShape(grp);
    ret.centerIn(g, margin);
    return ret;
  }

  public static RShape centerIn(RShape grp, PGraphics g){
    return centerIn(grp, g, 0);
  }

  /**
   * Split a shape along the curve length in two parts.
   * @eexample split
   * @param shp  the shape to be splited
   * @param t  the proportion (a value from 0 to 1) along the curve where to split
   * @return RShape[], an array of shapes with two elements, one for each side of the split
   */
  public static RShape[] split(RShape shp, float t){
    return shp.split(t);
  }

  /**
   * Adapt a shape along the curve of another shape.
   * @eexample split
   * @param shp  the shape to be adapted
   * @param path  the shape which curve will be followed
   * @return RShape  the adapted shape
   * @related setAdaptor ( )
   */
  public static RShape adapt(RShape shp, RShape path){
    RShape ret = new RShape(shp);
    ret.adapt(path);
    return ret;
  }

  /**
   * Polygonize a shape.
   * @eexample split
   * @param shp  the shape to be polygonized
   * @return RShape, the polygonized shape
   * @related setPolygonizer ( )
   */
  public static RShape polygonize(RShape shp){
    RShape ret = new RShape(shp);
    ret.polygonize();
    return ret;
  }


  // State methods
  /**
   * Initialize the library.  Must be called before any call to Geomerative methods.  Must be called by passing the PApplet.  e.g. RG.init(this)
   */
  public static void init(PApplet _parent){
    parent = _parent;
    initialized = true;
  }

  /**
   * @invisible
   */
  public static boolean initialized() {
    return initialized;
  }

  /**
   * @invisible
   */
  protected static PApplet parent(){
    if(parent == null){
      throw new LibraryNotInitializedException();
    }

    return parent;
  }
  
  /**
   * @invisible
   */
  protected static int dpi() {
    return dpi;
  }
  
  /**
   * Use this to set the resolution of the display.  This specifies the Dots Per Inch of the display.
   * @param _dpi  the dots per inch of the display
   * */
  public static void setDpi(int _dpi) {
    dpi = _dpi;
  }

  /**
   * Binary difference between two shapes.
   * @eexample binaryOps
   * @param a  first shape to operate on
   * @param b  second shape to operate on
   * @return RShape, the result of the operation
   * @related diff ( )
   * @related union ( )
   * @related intersection ( )
   * @related xor ( )
   */
  public static RShape diff(RShape a, RShape b){
    return a.diff(b);
  }

  /**
   * Binary union between two shapes.
   * @eexample binaryOps
   * @param a  first shape to operate on
   * @param b  second shape to operate on
   * @return RShape, the result of the operation
   * @related diff ( )
   * @related union ( )
   * @related intersection ( )
   * @related xor ( )
   */
  public static RShape union(RShape a, RShape b){
    return a.union(b);
  }

  /**
   * Binary intersection between two shapes.
   * @eexample binaryOps
   * @param a  first shape to operate on
   * @param b  second shape to operate on
   * @return RShape, the result of the operation
   * @related diff ( )
   * @related union ( )
   * @related intersection ( )
   * @related xor ( )
   */
  public static RShape intersection(RShape a, RShape b){
    return a.intersection(b);
  }

  /**
   * Binary xor between two shapes.
   * @eexample binaryOps
   * @param a  first shape to operate on
   * @param b  second shape to operate on
   * @return RShape, the result of the operation
   * @related diff ( )
   * @related union ( )
   * @related intersection ( )
   * @related xor ( )
   */
  public static RShape xor(RShape a, RShape b){
    return a.xor(b);
  }

  /**
   * Ignore the styles of the shapes when drawing and use the Processing style methods.
   * @eexample ignoreStyles
   * @param value  value to which the ignoreStyles state should be set
   */
  public static void ignoreStyles(boolean value){
    ignoreStyles = value;
  }

  public static void ignoreStyles(){
    ignoreStyles = true;
  }


  /**
   * Use this to set the adaptor type.
   * @eexample RShape_setAdaptor
   * @param adptorType  it can be RG.BYPOINT, RG.BYELEMENTPOSITION or RG.BYELEMENTINDEX
   * @related BYPOINT
   * @related BYELEMENTPOSITION
   * @related BYELEMENTINDEX
   */
  public static void setAdaptor(int adptorType){
    adaptorType = adptorType;
  }

  /**
   * Use this to set the adaptor scaling.  This scales the transformation of the adaptor.
   * @eexample RShape_setAdaptor
   * @param adptorScale  the scaling coefficient
   */
  public static void setAdaptorScale(float adptorScale){
    adaptorScale = adptorScale;
  }

  /**
   * Use this to set the adaptor length offset.  This specifies where to start adapting the group to the shape.
   * @eexample RShape_setAdaptorLengthOffset
   * @param adptorLengthOffset  the offset along the curve of the shape. Must be a value between 0 and 1;
   * */
  public static void setAdaptorLengthOffset(float adptorLengthOffset) throws RuntimeException{
    if(adptorLengthOffset>=0F && adptorLengthOffset<=1F)
      adaptorLengthOffset = adptorLengthOffset;
    else
      throw new RuntimeException("The adaptor length offset must take a value between 0 and 1.");
  }


  /**
   * Use this to set the polygonizer type.
   *
   * @param segmenterMethod  can be RG.ADAPTATIVE, RG.UNIFORMLENGTH or RG.UNIFORMSTEP.
   *
   * @eexample setPolygonizer
   * @related ADAPTATIVE
   * @related UNIFORMLENGTH
   * @related UNIFORMSTEP
   * */
  public static void setPolygonizer(int segmenterMethod){
    RCommand.setSegmentator(segmenterMethod);
  }

  /**
   * Use this to set the segmentator angle tolerance for the ADAPTATIVE segmentator and set the segmentator to ADAPTATIVE.
   * @eexample setPolygonizerAngle
   * @param angle  an angle from 0 to PI/2 it defines the maximum angle between segments.
   * @related ADAPTATIVE
   * */
  public static void setPolygonizerAngle(float angle){
    RCommand.setSegmentAngle(angle);
  }

  /**
   * Use this to set the segmentator length for the UNIFORMLENGTH segmentator and set the segmentator to UNIFORMLENGTH.
   * @eexample setPolygonizerLength
   * @param length  the length of each resulting segment.
   * @related UNIFORMLENGTH
   * @related polygonize ( )
   */
  public static void setPolygonizerLength(float length){
    RCommand.setSegmentLength(length);
  }

  /**
   * Use this to set the segmentator step for the UNIFORMSTEP segmentator and set the segmentator to UNIFORMSTEP.
   * @eexample setSegmentStep
   * @param step  if a float from +0.0 to 1.0 is passed it's considered as the step, else it's considered as the number of steps.  When a value of 0.0 is used the steps will be calculated automatically depending on an estimation of the length of the curve.  The special value -1 is the same as 0.0 but also turning of the segmentation of lines (faster segmentation).
   * @related UNIFORMSTEP
   * @related polygonize ( )
   */
  public static void setPolygonizerStep(float step){
    RCommand.setSegmentStep(step);
  }

}
