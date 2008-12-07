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
 * R is a static class containing all the states, modes, etc..
 */
public class RG implements PConstants{
  private static boolean initialized = false;
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
  
  static int adaptorType = BYELEMENTPOSITION;
  static float adaptorScale = 1F;
  static float adaptorLengthOffset = 0F;

  public static int ADAPTATIVE = RCommand.ADAPTATIVE;
  public static int UNIFORMLENGTH = RCommand.UNIFORMLENGTH;
  public static int UNIFORMSTEP = RCommand.UNIFORMSTEP;
  
  public static class LibraryNotInitializedException extends NullPointerException{
    private static final long serialVersionUID = -3710605630786298671L;

    LibraryNotInitializedException(){
      super("Must call RG.init(this); before using this library.");
    }
  }

  public static class FontNotLoadedException extends NullPointerException{
    private static final long serialVersionUID = -3710605630786298672L;

    FontNotLoadedException(){
      super("Must load a font using RG.loadFont(filename, size) first.");
    }
  }

  public static class NoPathInitializedException extends NullPointerException{
    private static final long serialVersionUID = -3710605630786298673L;

    NoPathInitializedException(){
      super("Must initialize a path by calling RG.beginShape() or RG.beginPath() first.");
    }
  }


  static RShape shape;
  static RShape group;
  static RPath path;

  static RFont fntLoader;

  static boolean shapeBegin = false;


  // Font methods
  /**
   * Load and get the font object that can be used in the textFont method.
   * @eexample loadFont
   * @param String fontFile, the filename of the font to be loaded
   * @return RFont, the font object
   */
  public static RFont loadFont(String fontFile){
    return new RFont(fontFile);
  }

  /**
   * Draw text to the screen using the font set using the textFont method.
   * @eexample text
   * @param String text, the string to be drawn on the screen
   */
  public static void text(String text){
    RShape grp = getText(text);
    grp.draw();
  }

  /**
   * Set the font object to be used in all text calls.
   * @eexample textFont
   * @param RFont font, the font object to be set
   * @param int size, the size of the font
   */
  public static void textFont(RFont font, int size){
    font.setSize(size);
    fntLoader = font;
  }

  /**
   * Get the shape corresponding to a text.  Use the textFont method to select the font and size.
   * @eexample getText
   * @param String fontFile, the filename of the font to be loaded
   * @param String text, the string to be created
   * @param int size, the size of the font to be used
   * @param int align, the alignment. Use RG.CENTER, RG.LEFT or RG.RIGHT
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
   * @param RShape shp, the shape to be drawn
   * @param float x, the horizontal coordinate
   * @param float y, the vertical coordinate
   * @param float w, the width with which we draw the shape
   * @param float h, the height with which we draw the shape
   */
  public static void shape(RShape shp, float x, float y, float w, float h){
    RShape tshp = new RShape(shp);
    tshp.transform(x, y, w, h);
    tshp.draw();
  }

  public static void shape(RShape shp, float x, float y){
    RShape tshp = new RShape(shp);
    tshp.transform(x, y, tshp.getWidth(), tshp.getHeight());
    tshp.draw();
  }


  public static void shape(RShape shp){
    shp.draw();
  }


  /**
   * Load a shape object from a file.
   * @eexample loadShape
   * @param String filename, the SVG file to be loaded.  Must be in the data directory
   */  
  public static RShape loadShape(String filename){
    RSVG svgLoader = new RSVG();
    return svgLoader.toShape(filename);    
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
   * @param int endMode, if called with RG.CLOSE it closes the current path before starting the new one. 
   * @eexample createShape
   */  
  public static void breakShape(){
    shape.addPath();
    shapeBegin = true;
  }

  public static void breakShape(int endMode){
    if (endMode == CLOSE) {
      shape.addClose();
    }
    breakShape();
  }

  /**
   * Add a vertex to the shape.  Can only be called inside beginShape() and endShape().
   * @param int endMode, if called with RG.CLOSE it closes the current path before starting the new one. 
   * @eexample createShape
   * @param float x, the x coordinate of the vertex
   * @param float y, the y coordinate of the vertex
   */  
  public static void vertex(float x, float y){
    if(path == null){
      if (shape.countPaths() == 0){
        shape.addMoveTo(x, y);
      }else{
        if (shapeBegin){
          shape.addMoveTo(x, y);
          shapeBegin = false;
        }
        shape.addLineTo(x, y);
      }
    }else{
      path.addLineTo(x, y);
    }
  }

  /**
   * Add a bezierVertex to the shape.  Can only be called inside beginShape() and endShape().
   * @param int endMode, if called with RG.CLOSE it closes the current path before starting the new one. 
   * @eexample createShape
   * @param float cx1, the x coordinate of the first control point
   * @param float cy1, the y coordinate of the first control point
   * @param float cx2, the x coordinate of the second control point
   * @param float cy2, the y coordinate of the second control point
   * @param float x, the x coordinate of the end point
   * @param float y, the y coordinate of the end point
   */  
  public static void bezierVertex(float cx1, float cy1, float cx2, float cy2, float x, float y){
    if(path == null){
      if (shape.countPaths() == 0){
        throw new NoPathInitializedException();
      }else{
        shape.addBezierTo(cx1, cy1, cx2, cy2, x, y);
      }
    }else{
      path.addBezierTo(cx1, cy1, cx2, cy2, x, y);
    }
  }

  /**
   * End the shape being created and draw it to the screen or the PGraphics passed as parameter.
   * @eexample createShape
   * @param PGraphics g, the canvas on which to draw.  By default it draws on the screen
   */
  public static void endShape(PGraphics g){
    if(group == null){
      // We are not inside a beginGroup
      shape.draw(g);
    }else{
      // We are inside a beginGroup
      group.addChild(shape);
      
      shape = null;
    }
    
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

    return returningGroup;    
  }

  /**
   * Get an ellipse as a shape object.
   * @eexample getEllipse
   * @param float x, x coordinate of the center of the ellipse
   * @param float y, y coordinate of the center of the ellipse
   * @param float rx, horizontal radius of the ellipse 
   * @param float ry, vertical radius of the ellipse
   */
  public static RShape getEllipse(float x, float y, float rx, float ry){
    RShape ret = new RShape();
    ret.addChild(RShape.createEllipse(x, y, rx, ry));
    return ret;
  }
  
  public static RShape getEllipse(float x, float y, float r){
    return getEllipse(x, y, r, r);
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
   * @param RShape shp, the shape to be splited
   * @param float t, the proportion (a value from 0 to 1) along the curve where to split
   * @return RShape[], an array of shapes with two elements, one for each side of the split
   */
  public static RShape[] split(RShape shp, float t){
    return shp.split(t);
  }

  /**
   * Adapt a shape along the curve of another shape.
   * @eexample split
   * @param RShape shp, the shape to be adapted
   * @param RShape path, the shape which curve will be followed
   * @return RShape, the adapted shape
   */
  public static RShape adapt(RShape grp, RShape path){
    RShape ret = new RShape(grp);
    ret.adapt(path);
    return ret;
  }

  /**
   * Polygonize a shape.
   * @eexample split
   * @param RShape shp, the shape to be polygonized
   * @return RShape, the polygonized shape
   */
  public static RShape polygonize(RShape grp){
    RShape ret = new RShape(grp);
    ret.polygonize();
    return ret;
  }
  

  // State methods
  /**
   * @invisible
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
   * Polygonize a shape.
   * @eexample split
   * @param RShape shp, the shape to be polygonized
   * @return RShape, the polygonized shape
   */
  public static RShape diff(RShape a, RShape b){
    return a.diff(b);
  }

  /**
   * Polygonize a shape.
   * @eexample split
   * @param RShape shp, the shape to be polygonized
   * @return RShape, the polygonized shape
   */
  public static RShape union(RShape a, RShape b){
    return a.union(b);
  }

  /**
   * Polygonize a shape.
   * @eexample split
   * @param RShape shp, the shape to be polygonized
   * @return RShape, the polygonized shape
   */
  public static RShape intersection(RShape a, RShape b){
    return a.intersection(b);
  }

  /**
   * Polygonize a shape.
   * @eexample split
   * @param RShape shp, the shape to be polygonized
   * @return RShape, the polygonized shape
   */
  public static RShape xor(RShape a, RShape b){
    return a.xor(b);
  }
  
  /**
   * Ignore the styles of the shapes when drawing and use the processing style methods.
   * @eexample ignoreStyles
   * @param boolean value, set the ignoreStyles state to the value passed
   */
  public static void ignoreStyles(){
    ignoreStyles = true;
  }

  public static void ignoreStyles(boolean value){
    ignoreStyles = value;
  }

  /**
   * Use this to set the adaptor type.
   * @eexample RShape_setAdaptor
   * @param int adptorType, it can be RG.BYPOINT, RG.BYELEMENTPOSITION or RG.BYELEMENTINDEX
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
   * @param float adptorScale, the scaling coefficient
   */
  public static void setAdaptorScale(float adptorScale){
    adaptorScale = adptorScale;
  }
  
  /**
   * Use this to set the adaptor length offset.  This specifies where to start adapting the group to the shape.
   * @eexample RShape_setAdaptorLengthOffset
   * @param float adptorLengthOffst, the offset along the curve of the shape. Must be a value between 0 and 1;
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
   * ADAPTATIVE segmentator minimizes the number of segments avoiding perceptual artifacts like angles or cusps.  Use this in order to have Polygons and Meshes with the fewest possible vertices.
   * UNIFORMLENGTH segmentator is the slowest segmentator and it segments the curve on segments of equal length.  This can be useful for very specific applications when for example drawing incrementaly a shape with a uniform speed.
   * UNIFORMSTEP segmentator is the fastest segmentator and it segments the curve based on a constant value of the step of the curve parameter, or on the number of segments wanted.  This can be useful when segmpointsentating very often a Shape or when we know the amount of segments necessary for our specific application.
   *
   * @param int segmenterMethod, can be RG.ADAPTATIVE, RG.UNIFORMLENGTH or RG.UNIFORMSTEP.
   *
   * @eexample setPolygonizer
   * */
  public static void setPolygonizer(int segmenterMethod){
    RCommand.setSegmentator(segmenterMethod);
  }

  /**
   * Use this to set the segmentator angle tolerance for the ADAPTATIVE segmentator and set the segmentator to ADAPTATIVE.
   * @eexample setPolygonizerAngle
   * @param float angle, an angle from 0 to PI/2 it defines the maximum angle between segments.
   * */
  public static void setPolygonizerAngle(float angle){
    RCommand.setSegmentAngle(angle);
  }

  /**
   * Use this to set the segmentator length for the UNIFORMLENGTH segmentator and set the segmentator to UNIFORMLENGTH.
   * @eexample setPolygonizerLength
   * @param float length, the length of each resulting segment.
   * */
  public static void setPolygonizerLength(float length){
    RCommand.setSegmentLength(length);
  }

  /**
   * Use this to set the segmentator step for the UNIFORMSTEP segmentator and set the segmentator to UNIFORMSTEP.
   * @eexample setSegmentStep
   * @param float step, if a float from +0.0 to 1.0 is passed it's considered as the step, else it's considered as the number of steps.  When a value of 0.0 is used the steps will be calculated automatically depending on an estimation of the length of the curve.  The special value -1 is the same as 0.0 but also turning of the segmentation of lines (faster segmentation).
   * */
  public static void setPolygonizerStep(float step){
    RCommand.setSegmentStep(step);
  }

}
