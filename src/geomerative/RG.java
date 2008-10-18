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
   * @invisible
   */
  public final static int BYPOINT = 0;
  
  /**
   * @invisible
   */
  public final static int BYELEMENTPOSITION = 1;
  
  /**
   * @invisible
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
      super("Must initialize a path by calling RG.beginGroup(), RG.beginShape() or RG.beginPath() first.");
    }
  }


  static RShape shape;
  static RGroup group;
  static RPath path;

  static RFont fntLoader;

  static boolean shapeBegin = false;

  public static void loadFont(String font, int size){
    fntLoader = new RFont(font, size);
  }

  public static RGroup getText(String text, String font, int size, int align){
    RFont tempFntLoader = new RFont(font, size, align);
    return tempFntLoader.toGroup(text);
  }

  public static RGroup getText(String text){
    if(fntLoader == null){
      throw new FontNotLoadedException();      
    }
    
    return fntLoader.toGroup(text);
  }

  public static RGroup loadSVG(String filename){
    RSVG svgLoader = new RSVG();
    return svgLoader.toGroup(filename);    
  }

  // Methods to create shapes
  public static void beginShape(){
    shape = new RShape();
  }

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


  public static void endShape(PGraphics g){
    if(group == null){
      // We are not inside a beginGroup
      shape.draw(g);
    }else{
      // We are inside a beginGroup
      group.addElement(shape);
      
      shape = null;
    }
    
    shape = null;
  }

  public static void endShape(){
    shape.draw();
    shape = null;
  }


  public static RGroup getShape(){
    RGroup returningGroup = new RGroup();
    returningGroup.addElement(shape);

    shape = null;

    return returningGroup;    
  }



  public static RGroup getEllipse(float x, float y, float rx, float ry){
    RGroup ret = new RGroup();
    ret.addElement(RShape.createEllipse(x, y, rx, ry));
    return ret;
  }
  
  public static RGroup getEllipse(float x, float y, float r){
    return getEllipse(x, y, r, r);
  }


  // Transformation methods
  public static RGroup centerIn(RGroup grp, PGraphics g, float margin){
    RGroup ret = new RGroup(grp);
    ret.centerIn(g, margin);
    return ret;
  }

  public static RGroup centerIn(RGroup grp, PGraphics g){
    return centerIn(grp, g, 0);
  }


  public static RGroup[] split(RGroup grp, float t){
    return grp.split(t);
  }

  public static RGroup adapt(RGroup grp, RGroup path){
    RGroup ret = new RGroup(grp);
    ret.adapt(path);
    return ret;
  }

  public static RGroup polygonize(RGroup grp){
    RGroup ret = new RGroup(grp);
    ret.polygonize();
    return ret;
  }
  

  // State methods
  public static void init(PApplet _parent){
    parent = _parent;
    initialized = true;
  }
  
  public static boolean initialized() {
    return initialized;
  }

  protected static PApplet parent(){
    if(parent == null){
      throw new LibraryNotInitializedException();
    }
    
    return parent;
  }

  public static void ignoreStyles(){
    ignoreStyles = true;
  }

  public static void ignoreStyles(boolean _value){
    ignoreStyles = _value;
  }

  /**
   * Use this to set the adaptor type.  RGroup.BYPOINT adaptor adapts the group to a particular shape by adapting each of the groups points.  This can cause deformations of the individual elements in the group.  RGroup.BYELEMENT adaptor adapts the group to a particular shape by adapting each of the groups elements.  This mantains the proportions of the shapes.
   * @eexample RGroup_setAdaptor
   * @param int adptorType, it can take the values RGroup.BYPOINT and RGroup.BYELEMENT
   * */
  public static void setAdaptor(int adptorType){
    adaptorType = adptorType;
  }
  
  /**
   * Use this to set the adaptor scaling.  This scales the transformation of the adaptor.
   * @eexample RGroup_setAdaptor
   * @param float adptorScale, the scaling coefficient
   * */
  public static void setAdaptorScale(float adptorScale){
    adaptorScale = adptorScale;
  }
  
  /**
   * Use this to set the adaptor length offset.  This specifies where to start adapting the group to the shape.
   * @eexample RGroup_setAdaptorLengthOffset
   * @param float adptorLengthOffst, the offset along the curve of the shape. Must be a value between 0 and 1;
   * */
  public static void setAdaptorLengthOffset(float adptorLengthOffset) throws RuntimeException{
    if(adptorLengthOffset>=0F && adptorLengthOffset<=1F)
      adaptorLengthOffset = adptorLengthOffset;
    else
      throw new RuntimeException("The adaptor length offset must take a value between 0 and 1.");
  }


  /**
   * Use this to set the segmentator type.  ADAPTATIVE segmentator minimizes the number of segments avoiding perceptual artifacts like angles or cusps.  Use this in order to have Polygons and Meshes with the fewest possible vertices.  This can be useful when using or drawing a lot the same Polygon or Mesh deriving from this Shape.  UNIFORMLENGTH segmentator is the slowest segmentator and it segments the curve on segments of equal length.  This can be useful for very specific applications when for example drawing incrementaly a shape with a uniform speed.  UNIFORMSTEP segmentator is the fastest segmentator and it segments the curve based on a constant value of the step of the curve parameter, or on the number of segments wanted.  This can be useful when segmpointsentating very often a Shape or when we know the amount of segments necessary for our specific application.
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