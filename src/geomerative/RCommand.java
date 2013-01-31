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
 * @extended
 */
public class RCommand extends RGeomElem
{
  /**
   * @invisible
   */
  public int type = RGeomElem.COMMAND;

  public RPoint[] controlPoints;
  public RPoint startPoint;
  public RPoint endPoint;
  int commandType;

  RPoint[] curvePoints;

  /**
   * @invisible
   * */
  public static final int LINETO = 0;
  /**
   * @invisible
   * */
  public static final int QUADBEZIERTO = 1;
  /**
   * @invisible
   * */
  public static final int CUBICBEZIERTO = 2;

  /**
   * @invisible
   * */
  public static final int ADAPTATIVE = 0;
  /**
   * @invisible
   * */
  public static final int UNIFORMLENGTH = 1;
  /**
   * @invisible
   * */
  public static final int UNIFORMSTEP = 2;

  public static int segmentType = UNIFORMLENGTH;

  /* Parameters for ADAPTATIVE (dependent of the PGraphics on which drawing) */
  static final int segmentRecursionLimit = 32;
  static final float segmentDistanceEpsilon = 1.192092896e-07F;
  static final float segmentCollinearityEpsilon = 1.192092896e-07F;
  static final float segmentAngleTolEpsilon = 0.01F;

  static float segmentGfxStrokeWeight = 1.0F;
  static float segmentGfxScale = 1.0F;
  static float segmentApproxScale = 1.0F;
  static float segmentDistTolSqr = 0.25F;
  static float segmentDistTolMnhttn = 4.0F;
  public static float segmentAngleTol = 0.0F;
  static float segmentCuspLimit = 0.0F;

  /* Parameters for UNIFORMLENGTH (dependent of the PGraphics on which drawing) */
  static float segmentLength = 4.0F;
  static float segmentOffset = 0.0F;
  static float segmentAccOffset = 0.0F;

  /* Parameters for UNIFORMSTEP */
  static int segmentSteps = 0;
  static boolean segmentLines = false;

  int oldSegmentType = UNIFORMLENGTH;

  /* Parameters for ADAPTATIVE (dependent of the PGraphics on which drawing) */
  float oldSegmentCollinearityEpsilon = 1.192092896e-07F;
  float oldSegmentAngleTolEpsilon = 0.01F;

  float oldSegmentGfxStrokeWeight = 1.0F;
  float oldSegmentGfxScale = 1.0F;
  float oldSegmentApproxScale = 1.0F;
  float oldSegmentDistTolSqr = 0.25F;
  float oldSegmentDistTolMnhttn = 4.0F;
  float oldSegmentAngleTol = 0.0F;
  float oldSegmentCuspLimit = 0.0F;

  /* Parameters for UNIFORMLENGTH (dependent of the PGraphics on which drawing) */
  float oldSegmentLength = 4.0F;
  float oldSegmentOffset = 0.0F;
  float oldSegmentAccOffset = 0.0F;

  /* Parameters for UNIFORMSTEP */
  int oldSegmentSteps = 0;
  boolean oldSegmentLines = false;



  static RCommand createLine(RPoint start, RPoint end){
    RCommand result = new RCommand();
    result.startPoint = start;
    result.endPoint = end;
    result.commandType = LINETO;
    return result;
  }

  static RCommand createLine(float startx, float starty, float endx, float endy){
    return createLine(new RPoint(startx,starty), new RPoint(endx,endy));
  }

  static RCommand createBezier3(RPoint start, RPoint cp1, RPoint end){
    RCommand result = new RCommand();
    result.startPoint = start;
    result.append(cp1);
    result.endPoint = end;
    result.commandType = QUADBEZIERTO;
    return result;
  }

  static RCommand createBezier3(float startx, float starty, float cp1x, float cp1y, float endx, float endy){
    return createBezier3(new RPoint(startx,starty), new RPoint(cp1x,cp1y), new RPoint(endx,endy));
  }

  static RCommand createBezier4(RPoint start, RPoint cp1, RPoint cp2, RPoint end){
    RCommand result = new RCommand();
    result.startPoint = start;
    result.append(cp1);
    result.append(cp2);
    result.endPoint = end;
    result.commandType = CUBICBEZIERTO;
    return result;
  }

  static RCommand createBezier4(float startx, float starty, float cp1x, float cp1y, float cp2x, float cp2y, float endx, float endy){
    return createBezier4(new RPoint(startx,starty), new RPoint(cp1x,cp1y), new RPoint(cp2x,cp2y), new RPoint(endx,endy));
  }

  /**
   * Create an empty command
   * @invisible
   */
  public RCommand(){
    controlPoints = null;
  }

  /**
   * Make a copy of another RCommand object.  This can be useful when wanting to transform one but at the same time keep the original.
   * @param c  the object of which to make the copy
   * @invisible
   */
  public RCommand(RCommand c){
    this.startPoint = new RPoint(c.startPoint);
    for(int i=0;i<c.countControlPoints();i++){
      this.append(new RPoint(c.controlPoints[i]));
    }
    this.endPoint = new RPoint(c.endPoint);
    this.commandType = c.commandType;
  }

  /**
   * Make a copy of another RCommand object with a specific start point.
   * @param c  the object of which to make the copy
   * @param sp  the start point of the command to be created
   */
  public RCommand(RCommand c, RPoint sp){
    this.startPoint = sp;
    for(int i=0;i<c.countControlPoints();i++){
      this.append(new RPoint(c.controlPoints[i]));
    }
    this.endPoint = new RPoint(c.endPoint);
    this.commandType = c.commandType;
  }

  /**
   * Create a LINETO command object with specific start and end points.
   * @param sp  the start point of the command to be created
   * @param ep  the end point of the command to be created
   */
  public RCommand(RPoint sp, RPoint ep){
    this.startPoint = sp;
    this.endPoint = ep;
    this.commandType = LINETO;
  }

  /**
   * Create a LINETO command object with specific start and end point coordinates.
   * @param spx  the x coordinate of the start point of the command to be created
   * @param spy  the y coordinate of the start point of the command to be created
   * @param epx  the x coordinate of the end point of the command to be created
   * @param epy  the y coordinate of the end point of the command to be created
   */
  public RCommand(float spx, float spy, float epx, float epy){
    this(new RPoint(spx, spy), new RPoint(epx, epy));
  }

  /**
   * Create a QUADBEZIERTO command object with specific start, control and end point coordinates.
   * @param sp  the start point of the command to be created
   * @param cp1  the first control point of the command to be created
   * @param ep  the end point of the command to be created
   */
  public RCommand(RPoint sp, RPoint cp1, RPoint ep){
    this.startPoint = sp;
    this.append(cp1);
    this.endPoint = ep;
    this.commandType = QUADBEZIERTO;
  }

  /**
   * Create a QUADBEZIERTO command object with specific start, control and end point coordinates.
   * @param spx  the x coordinate of the start point of the command to be created
   * @param spy  the y coordinate of the start point of the command to be created
   * @param cp1x  the x coordinate of the first control point of the command to be created
   * @param cp1y  the y coordinate of the first control point of the command to be created
   * @param epx  the x coordinate of the end point of the command to be created
   * @param epy  the y coordinate of the end point of the command to be created
   */
  public RCommand(float spx, float spy, float cp1x, float cp1y, float epx, float epy){
    this(new RPoint(spx, spy), new RPoint(cp1x, cp1y), new RPoint(epx, epy));
  }


  /**
   * Create a CUBICBEZIERTO command object with specific start, control and end point coordinates.
   * @param sp  the start point of the command to be created
   * @param cp1  the first control point of the command to be created
   * @param cp2  the second control point of the command to be created
   * @param ep  the end point of the command to be created
   */
  public RCommand(RPoint sp, RPoint cp1, RPoint cp2, RPoint ep){
    this.startPoint = sp;
    this.append(cp1);
    this.append(cp2);
    this.endPoint = ep;
    this.commandType = CUBICBEZIERTO;
  }

  /**
   * Create a CUBICBEZIERTO command object with specific start, control and end point coordinates.
   * @param spx  the x coordinate of the start point of the command to be created
   * @param spy  the y coordinate of the start point of the command to be created
   * @param cp1x  the x coordinate of the first control point of the command to be created
   * @param cp1y  the y coordinate of the first control point of the command to be created
   * @param cp2x  the x coordinate of the second control point of the command to be created
   * @param cp2y  the y coordinate of the second control point of the command to be created
   * @param epx  the x coordinate of the end point of the command to be created
   * @param epy  the y coordinate of the end point of the command to be created
   */
  public RCommand(float spx, float spy, float cp1x, float cp1y, float cp2x, float cp2y, float epx, float epy){
    this(new RPoint(spx, spy), new RPoint(cp1x, cp1y), new RPoint(cp2x, cp2y), new RPoint(epx, epy));
  }

  /**
   * @invisible
   */
  public RShape toShape(){
    return new RShape(new RPath(this));
  }

  public int getType(){
    return this.type;
  }


  /**
   * Use this to set the segmentator type.  ADAPTATIVE segmentator minimizes the number of segments avoiding perceptual artifacts like angles or cusps.  Use this in order to have Polygons and Meshes with the fewest possible vertices.  This can be useful when using or drawing a lot the same Polygon or Mesh deriving from this Shape.  UNIFORMLENGTH segmentator is the slowest segmentator and it segments the curve on segments of equal length.  This can be useful for very specific applications when for example drawing incrementaly a shape with a uniform speed.  UNIFORMSTEP segmentator is the fastest segmentator and it segments the curve based on a constant value of the step of the curve parameter, or on the number of segments wanted.  This can be useful when segmpointsentating very often a Shape or when we know the amount of segments necessary for our specific application.
   * @eexample setSegment
   * */
  public static void setSegmentator(int segmentatorType){
    segmentType = segmentatorType;
  }

  /**
   * Use this to set the segmentator graphic context.
   * @eexample setSegmentGraphic
   * @param g  graphics object too which to adapt the segmentation of the command.
   * */
  public static void setSegmentGraphic(PGraphics g){
    // Set the segmentApproxScale from the graphic context g
    segmentApproxScale = 1.0F;

    // Set all the gfx-context dependent parameters for all segmentators

    segmentDistTolSqr = 0.5F / segmentApproxScale;
    segmentDistTolSqr *= segmentDistTolSqr;
    segmentDistTolMnhttn = 4.0F / segmentApproxScale;
    segmentAngleTol = 0.0F;

    if(g.stroke && (g.strokeWeight * segmentApproxScale > 1.0F))
      {
        segmentAngleTol = 0.1F;
      }
  }

  /**
   * Use this to set the segmentator angle tolerance for the ADAPTATIVE segmentator and set the segmentator to ADAPTATIVE.
   * @eexample setSegmentAngle
   * @param segmentAngleTolerance  an angle from 0 to PI/2 it defines the maximum angle between segments.
   * */
  public static void setSegmentAngle(float segmentAngleTolerance){
    //segmentType = ADAPTATIVE;

    segmentAngleTol = segmentAngleTolerance;
  }

  /**
   * Use this to set the segmentator length for the UNIFORMLENGTH segmentator and set the segmentator to UNIFORMLENGTH.
   * @eexample setSegmentLength
   * @param segmentLngth  the length of each resulting segment.
   * */
  public static void setSegmentLength(float segmentLngth){
    //segmentType = UNIFORMLENGTH;
    if(segmentLngth>=1){
      segmentLength = segmentLngth;
    }else{
      segmentLength = 4;
    }
  }

  /**
   * Use this to set the segmentator offset for the UNIFORMLENGTH segmentator and set the segmentator to UNIFORMLENGTH.
   * @eexample setSegmentOffset
   * @param segmentOffst  the offset of the first point on the path.
   * */
  public static void setSegmentOffset(float segmentOffst){
    //segmentType = UNIFORMLENGTH;
    if(segmentOffst>=0){
      segmentOffset = segmentOffst;
    }else{
      segmentOffset = 0;
    }
  }

  /**
   * Use this to set the segmentator step for the UNIFORMSTEP segmentator and set the segmentator to UNIFORMSTEP.
   * @eexample setSegmentStep
   * @param segmentStps  if a float from +0.0 to 1.0 is passed it's considered as the step, else it's considered as the number of steps.  When a value of 0.0 is used the steps will be calculated automatically depending on an estimation of the length of the curve.  The special value -1 is the same as 0.0 but also turning of the segmentation of lines (faster segmentation).
   * */
  public static void setSegmentStep(float segmentStps){
    //segmentType = UNIFORMSTEP;
    if(segmentStps == -1F){
      segmentLines=false;
      segmentStps=0F;
    }else{
      segmentLines=true;
    }
    // Set the parameters
    segmentStps = Math.abs(segmentStps);
    if(segmentStps>0.0F && segmentStps<1.0F){
      segmentSteps = (int)(1F/segmentStps);
    }else{
      segmentSteps = (int)segmentStps;
    }
  }


  protected void saveSegmentatorContext(){
    oldSegmentType = RCommand.segmentType;

    /* Parameters for ADAPTATIVE (dependent of the PGraphics on which drawing) */
    oldSegmentGfxStrokeWeight = RCommand.segmentGfxStrokeWeight;
    oldSegmentGfxScale = RCommand.segmentGfxScale;
    oldSegmentApproxScale = RCommand.segmentApproxScale;
    oldSegmentDistTolSqr = RCommand.segmentDistTolSqr;
    oldSegmentDistTolMnhttn = RCommand.segmentDistTolMnhttn;
    oldSegmentAngleTol = RCommand.segmentAngleTol;
    oldSegmentCuspLimit = RCommand.segmentCuspLimit;

    /* Parameters for UNIFORMLENGTH (dependent of the PGraphics on which drawing) */
    oldSegmentLength = RCommand.segmentLength;
    oldSegmentOffset = RCommand.segmentOffset;
    oldSegmentAccOffset = RCommand.segmentAccOffset;

    /* Parameters for UNIFORMSTEP */
    oldSegmentSteps = RCommand.segmentSteps;
    oldSegmentLines = RCommand.segmentLines;
  }

  protected void restoreSegmentatorContext(){
    RCommand.segmentType = oldSegmentType;

    /* Parameters for ADAPTATIVE (dependent of the PGraphics on which drawing) */
    RCommand.segmentGfxStrokeWeight = oldSegmentGfxStrokeWeight;
    RCommand.segmentGfxScale = oldSegmentGfxScale;
    RCommand.segmentApproxScale = oldSegmentApproxScale;
    RCommand.segmentDistTolSqr = oldSegmentDistTolSqr;
    RCommand.segmentDistTolMnhttn = oldSegmentDistTolMnhttn;
    RCommand.segmentAngleTol = oldSegmentAngleTol;
    RCommand.segmentCuspLimit = oldSegmentCuspLimit;

    /* Parameters for UNIFORMLENGTH (dependent of the PGraphics on which drawing) */
    RCommand.segmentLength = oldSegmentLength;
    RCommand.segmentOffset = oldSegmentOffset;
    RCommand.segmentAccOffset = oldSegmentAccOffset;

    /* Parameters for UNIFORMSTEP */
    RCommand.segmentSteps = oldSegmentSteps;
    RCommand.segmentLines = oldSegmentLines;
  }

  /**
   * Use this to return the number of control points of the curve.
   * @eexample countControlPoints
   * @return int, the number of control points.
   * */
  public int countControlPoints(){
    if (controlPoints == null){
      return 0;
    }
    return controlPoints.length;
  }

  /**
   * Use this to return the command type.
   * @eexample getCommandType
   * @return int, an integer which can take the following values: RCommand.LINETO, RCommand.QUADBEZIERTO, RCommand.CUBICBEZIERTO.
   * */
  public int getCommandType(){
    return commandType;
  }

  /**
   * Use this to return the start point of the curve.
   * @eexample getStartPoint
   * @return RPoint, the start point of the curve.
   * @invisible
   * */
  RPoint getStartPoint(){
    return startPoint;
  }

  /**
   * Use this to return the end point of the curve.
   * @eexample getEndPoint
   * @return RPoint, the end point of the curve.
   * @invisible
   * */
  RPoint getEndPoint(){
    return endPoint;
  }

  /**
   * Use this to return the control points of the curve.  It returns the points in the way of an array of RPoint.
   * @eexample getControlPoints
   * @return RPoint[], the control points returned in an array.
   * @invisible
   * */
  RPoint[] getControlPoints(){
    return controlPoints;
  }

  /**
   * Use this to return the points on the curve.  It returns the points in the way of an array of RPoint.
   * @eexample getPoints
   * @return RPoint[], the vertices returned in an array.
   * */
  public RPoint[] getPoints(){
    return getPoints(true);
  }

  protected RPoint[] getPoints(boolean resetSegmentator){

    if(resetSegmentator){
      saveSegmentatorContext();
      RCommand.segmentOffset = 0F;
      RCommand.segmentAccOffset = 0F;
    }


    RPoint[] result = null;
    switch(segmentType){
    case ADAPTATIVE:
      switch(commandType){
      case LINETO:
        result = new RPoint[2];
        result[0] = startPoint;
        result[1] = endPoint;
        break;

      case QUADBEZIERTO:
        quadBezierAdaptative();
        result = curvePoints;
        curvePoints = null;
        break;

      case CUBICBEZIERTO:
        cubicBezierAdaptative();
        result = curvePoints;
        curvePoints = null;
        break;
      }
      break;

    case UNIFORMLENGTH:
      switch(commandType){
      case LINETO:
        lineUniformLength();
        result = curvePoints;
        curvePoints = null;
        break;

      case QUADBEZIERTO:
        quadBezierUniformLength();
        result = curvePoints;
        curvePoints = null;
        break;

      case CUBICBEZIERTO:
        cubicBezierUniformLength();
        result = curvePoints;
        curvePoints = null;
        break;
      }
      break;

    case UNIFORMSTEP:
      switch(commandType){
      case LINETO:
        if(segmentLines){
          lineUniformStep();
          result = curvePoints;
          curvePoints = null;
        }else{
          result = new RPoint[2];
          result[0] = startPoint;
          result[1] = endPoint;
        }
        break;

      case QUADBEZIERTO:
        quadBezierUniformStep();
        result = curvePoints;
        curvePoints = null;
        break;

      case CUBICBEZIERTO:
        cubicBezierUniformStep();
        result = curvePoints;
        curvePoints = null;
        break;
      }
      break;
    }


    if(resetSegmentator){
      restoreSegmentatorContext();
    }

    return result;
  }

  /**
   * Use this to return a specific point on the curve.  It returns the RPoint for a given advancement parameter t on the curve.
   * @eexample getPoint
   * @param t float, the parameter of advancement on the curve. t must have values between 0 and 1.
   * @return RPoint, the vertice returned.
   * */
  public RPoint getPoint(float t){
    /* limit the value of t between 0 and 1 */
    t = (t > 1F) ? 1F : t;
    t = (t < 0F) ? 0F : t;
    float ax, bx, cx;
    float ay, by, cy;
    float tSquared, tDoubled, tCubed;
    float dx, dy;

    switch(commandType){
    case LINETO:
      dx = endPoint.x - startPoint.x;
      dy = endPoint.y - startPoint.y;
      return new RPoint(startPoint.x + dx * t, startPoint.y + dy * t);

    case QUADBEZIERTO:
      /* calculate the polynomial coefficients */
      bx = controlPoints[0].x - startPoint.x;
      ax = endPoint.x - controlPoints[0].x - bx;
      by = controlPoints[0].y - startPoint.y;
      ay = endPoint.y - controlPoints[0].y - by;

      /* calculate the curve point at parameter value t */
      tSquared = t * t;
      tDoubled = 2F * t;
      return new RPoint((ax * tSquared) + (bx * tDoubled) + startPoint.x, (ay * tSquared) + (by * tDoubled) + startPoint.y);

    case CUBICBEZIERTO:
      /* calculate the polynomial coefficients */
      cx = 3F * (controlPoints[0].x - startPoint.x);
      bx = 3F * (controlPoints[1].x - controlPoints[0].x) - cx;
      ax = endPoint.x - startPoint.x - cx - bx;
      cy = 3F * (controlPoints[0].y - startPoint.y);
      by = 3F * (controlPoints[1].y - controlPoints[0].y) - cy;
      ay = endPoint.y - startPoint.y - cy - by;

      /* calculate the curve point at parameter value t */
      tSquared = t * t;
      tCubed = tSquared * t;
      return new RPoint((ax * tCubed) + (bx * tSquared) + (cx * t) + startPoint.x, (ay * tCubed) + (by * tSquared) + (cy * t) + startPoint.y);
    }

    return new RPoint();
  }

  /**
   * Use this to return the tangents on the curve.  It returns the vectors in the form of an array of RPoint.
   * @eexample getTangents
   * @param segments int, the number of segments in which to divide the curve.
   * @return RPoint[], the tangent vectors returned in an array.
   * */
  public RPoint[] getTangents(int segments){
    RPoint[] result;
    float dt, t;
    switch(commandType)
      {
      case LINETO:
        result = new RPoint[2];
        result[0] = startPoint;
        result[1] = endPoint;
        return result;
      case QUADBEZIERTO:
      case CUBICBEZIERTO:
        result = new RPoint[segments];
        dt = 1F / segments;
        t = 0F;
        for(int i=0;i<segments;i++){
          result[i] = getTangent(t);
          t += dt;
        }
        return result;
      }
    return null;
  }

  public RPoint[] getTangents(){

    return getTangents(100);
  }

  /**
   * Use this to return a specific tangent on the curve.  It returns the RPoint representing the tangent vector for a given value of the advancement parameter t on the curve.
   * @eexample getTangent
   * @param t float, the parameter of advancement on the curve. t must have values between 0 and 1.
   * @return RPoint, the vertice returned.
   * */
  public RPoint getTangent(float t){
    /* limit the value of t between 0 and 1 */
    t = (t > 1F) ? 1F : t;
    t = (t < 0F) ? 0F : t;

    float dx, dy, tx, ty, t2, t_1, t_12;

    switch(commandType){
    case LINETO:
      dx = endPoint.x - startPoint.x;
      dy = endPoint.y - startPoint.y;
      return new RPoint(dx, dy);

    case QUADBEZIERTO:
      /* calculate the curve point at parameter value t */
      tx = 2F * ((startPoint.x - 2*controlPoints[0].x + endPoint.x) * t + (controlPoints[0].x - startPoint.x));
      ty = 2F * ((startPoint.y - 2*controlPoints[0].y + endPoint.y) * t + (controlPoints[0].y - startPoint.y));
      //float norm = (float)Math.sqrt(tx*tx + ty*ty);
      //return new RPoint(tx/norm,ty/norm);
      return new RPoint(tx, ty);

    case CUBICBEZIERTO:
      /* calculate the curve point at parameter value t */
      t2 = t*t;
      t_1 = 1-t;
      t_12 = t_1*t_1;

      return new RPoint(-3F*t_12*startPoint.x + 3F*(3F*t2 - 4F*t +1F)*controlPoints[0].x + 3F*t*(2F-3F*t)*controlPoints[1].x + 3F*t2*endPoint.x, -3F*t_12*startPoint.y + 3F*(3F*t2 - 4F*t +1F)*controlPoints[0].y + 3F*t*(2F-3F*t)*controlPoints[1].y + 3F*t2*endPoint.y);
    }

    return new RPoint();
  }

  /**
   * Use this to return arc length of a curve.  It returns the float representing the length given the value of the advancement parameter t on the curve. The current implementation of this function is very slow, not recommended for using during frame draw.
   * @eexample RCommand_getCurveLength
   * @param t float, the parameter of advancement on the curve. t must have values between 0 and 1.
   * @return float, the length returned.
   * @invisible
   * */
  public float getCurveLength(float t){

    /* limit the value of t between 0 and 1 */
    t = (t > 1F) ? 1F : t;
    t = (t < 0F) ? 0F : t;

    float dx, dy, dx2, dy2, t2;

    switch(commandType){
    case LINETO:
      dx = endPoint.x - startPoint.x;
      dy = endPoint.y - startPoint.y;
      dx2 = dx*dx;
      dy2 = dy*dy;
      t2 = t*t;
      //RG.parent().println("RCommand/LINETO::: getCurveLength: " + (float)Math.sqrt(dx2*t2 + dy2*t2));
      return (float)Math.sqrt(dx2*t2 + dy2*t2);

    case QUADBEZIERTO:
      /* calculate the curve point at parameter value t */
      return quadBezierLength();

    case CUBICBEZIERTO:
      /* calculate the curve point at parameter value t */
      return cubicBezierLength();
    }

    return -1F;
  }

  /**
   * Use this to return arc length of a curve.  It returns the float representing the length given the value of the advancement parameter t on the curve. The current implementation of this function is very slow, not recommended for using during frame draw.
   * @eexample RCommand_getCurveLength
   * @return float, the length returned.
   * @invisible
   * */
  public float getCurveLength(){
    return getCurveLength(1F);
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
   * Use this method to draw the command.
   * @eexample drawCommand
   * @param g PGraphics, the graphics object on which to draw the command
   */
  public void draw(PGraphics g){
    RPoint[] points = getPoints();
    if(points == null){
      return;
    }
    g.beginShape();
    for(int i=0;i<points.length;i++){
      g.vertex(points[i].x, points[i].y);
    }
    g.endShape();
  }

  /**
   * Use this method to draw the command.
   * @eexample drawCommand
   * @param a  the applet object on which to draw the command
   */
  public void draw(PApplet a){
    RPoint[] points = getPoints();
    if(points == null){
      return;
    }

    a.beginShape();
    for(int i=0;i<points.length;i++){
      a.vertex(points[i].x, points[i].y);
    }
    a.endShape();
  }


  /**
   * Use this to return the start, control and end points of the curve.  It returns the points in the way of an array of RPoint.
   * @eexample getHandles
   * @return RPoint[], the vertices returned in an array.
   * */
  public RPoint[] getHandles(){
    RPoint[] result;
    if(controlPoints==null){
      result = new RPoint[2];
      result[0] = startPoint;
      result[1] = endPoint;
    }else{
      result = new RPoint[controlPoints.length+2];
      result[0] = startPoint;
      System.arraycopy(controlPoints,0,result,1,controlPoints.length);
      result[result.length-1] = endPoint;
    }
    return result;
  }

  /**
   * Returns two commands resulting of splitting the command.
   * @eexample split
   * @param t  the advancement on the curve where command should be split.
   * @return RPoint[], the tangent vectors returned in an array.
   * */
  public RCommand[] split(float t){

    switch(commandType)
      {
      case LINETO:
        return splitLine(t);

      case QUADBEZIERTO:
        return splitQuadBezier(t);

      case CUBICBEZIERTO:
        return splitCubicBezier(t);

      }
    return null;
  }

  /**
   * Taken from:
   * http://steve.hollasch.net/cgindex/curves/cbezarclen.html
   *
   * who took it from:
   * Schneider's Bezier curve-fitter
   *
   */
  private RCommand[] splitCubicBezier(float t){
    RPoint[][] triangleMatrix = new RPoint[4][4];
    for(int i=0; i<=3; i++){
      for(int j=0; j<=3; j++){
        triangleMatrix[i][j] = new RPoint();
      }
    }

    RPoint[] ctrlPoints = this.getHandles();

    // Copy control points to triangle matrix
    for(int i = 0; i <= 3; i++){
      triangleMatrix[0][i].x = ctrlPoints[i].x;
      triangleMatrix[0][i].y = ctrlPoints[i].y;
    }

    // Triangle computation
    for(int i = 1; i <= 3; i++){
      for(int j = 0; j <= 3 - i; j++){
        triangleMatrix[i][j].x = (1-t) * triangleMatrix[i-1][j].x + t * triangleMatrix[i-1][j+1].x;
        triangleMatrix[i][j].y = (1-t) * triangleMatrix[i-1][j].y + t * triangleMatrix[i-1][j+1].y;
      }
    }

    RCommand[] result = new RCommand[2];
    result[0] = createBezier4(startPoint, triangleMatrix[1][0], triangleMatrix[2][0], triangleMatrix[3][0]);
    result[1] = createBezier4(triangleMatrix[3][0], triangleMatrix[2][1], triangleMatrix[1][2], endPoint);
    return result;
  }

  private RCommand[] splitQuadBezier(float t){
    RPoint[][] triangleMatrix = new RPoint[3][3];
    for(int i=0; i<=2; i++){
      for(int j=0; j<=2; j++){
        triangleMatrix[i][j] = new RPoint();
      }
    }

    RPoint[] ctrlPoints = this.getHandles();

    // Copy control points to triangle matrix
    for(int i = 0; i <= 2; i++){
      triangleMatrix[0][i] = ctrlPoints[i];
    }

    // Triangle computation
    for(int i = 1; i <= 2; i++){
      for(int j = 0; j <= 2 - i; j++){
        triangleMatrix[i][j].x = (1-t) * triangleMatrix[i-1][j].x + t * triangleMatrix[i-1][j+1].x;
        triangleMatrix[i][j].y = (1-t) * triangleMatrix[i-1][j].y + t * triangleMatrix[i-1][j+1].y;
      }
    }

    RCommand[] result = new RCommand[2];
    result[0] = createBezier3(startPoint, triangleMatrix[1][0], triangleMatrix[2][0]);
    result[1] = createBezier3(triangleMatrix[2][0], triangleMatrix[1][1], endPoint);
    return result;
  }

  private RCommand[] splitLine(float t){
    RPoint[][] triangleMatrix = new RPoint[2][2];
    for(int i=0; i<=1; i++){
      for(int j=0; j<=1; j++){
        triangleMatrix[i][j] = new RPoint();
      }
    }

    RPoint[] ctrlPoints = this.getHandles();

    // Copy control points to triangle matrix
    for(int i = 0; i <= 1; i++){
      triangleMatrix[0][i] = ctrlPoints[i];
    }

    // Triangle computation
    for(int i = 1; i <= 1; i++){
      for(int j = 0; j <= 1 - i; j++){
        triangleMatrix[i][j].x = (1-t) * triangleMatrix[i-1][j].x + t * triangleMatrix[i-1][j+1].x;
        triangleMatrix[i][j].y = (1-t) * triangleMatrix[i-1][j].y + t * triangleMatrix[i-1][j+1].y;
      }
    }

    RCommand[] result = new RCommand[2];
    result[0] = createLine(startPoint, triangleMatrix[1][0]);
    result[1] = createLine(triangleMatrix[1][0], endPoint);
    return result;
  }

  private void quadBezierAdaptative(){
    addCurvePoint(new RPoint(startPoint));
    quadBezierAdaptativeRecursive(startPoint.x, startPoint.y, controlPoints[0].x, controlPoints[0].y, endPoint.x, endPoint.y, 0);
    addCurvePoint(new RPoint(endPoint));
  }

  private void quadBezierAdaptativeRecursive(float x1, float y1, float x2, float y2, float x3, float y3, int level){

    if(level > segmentRecursionLimit)
      {
        return;
      }

    // Calculate all the mid-points of the line segments
    //----------------------
    float x12   = (x1 + x2) / 2;
    float y12   = (y1 + y2) / 2;
    float x23   = (x2 + x3) / 2;
    float y23   = (y2 + y3) / 2;
    float x123  = (x12 + x23) / 2;
    float y123  = (y12 + y23) / 2;

    float dx = x3-x1;
    float dy = y3-y1;
    float d = Math.abs(((x2 - x3) * dy - (y2 - y3) * dx));

    if(d > segmentCollinearityEpsilon)
      {
        // Regular care
        //-----------------
        if(d * d <= segmentDistTolSqr * (dx*dx + dy*dy))
          {
            // If the curvature doesn't exceed the distance_tolerance value
            // we tend to finish subdivisions.
            //----------------------
            if(segmentAngleTol < segmentAngleTolEpsilon)
              {
                addCurvePoint(new RPoint(x123, y123));
                return;
              }

            // Angle & Cusp Condition
            //----------------------
            float da = Math.abs((float)Math.atan2(y3 - y2, x3 - x2) - (float)Math.atan2(y2 - y1, x2 - x1));
            if(da >= Math.PI) da = 2*(float)Math.PI - da;

            if(da < segmentAngleTol)
              {
                // Finally we can stop the recursion
                //----------------------
                addCurvePoint(new RPoint(x123, y123));
                return;
              }
          }
      }
    else
      {
        if(Math.abs(x1 + x3 - x2 - x2) + Math.abs(y1 + y3 - y2 - y2) <= segmentDistTolMnhttn)
          {
            addCurvePoint(new RPoint(x123, y123));
            return;
          }
      }

    // Continue subdivision
    //----------------------
    quadBezierAdaptativeRecursive(x1, y1, x12, y12, x123, y123, level + 1);
    quadBezierAdaptativeRecursive(x123, y123, x23, y23, x3, y3, level + 1);
  }

  private void cubicBezierAdaptative(){
    addCurvePoint(new RPoint(startPoint));
    cubicBezierAdaptativeRecursive(startPoint.x, startPoint.y, controlPoints[0].x, controlPoints[0].y, controlPoints[1].x, controlPoints[1].y, endPoint.x, endPoint.y, 0);
    addCurvePoint(new RPoint(endPoint));
  }

  private void cubicBezierAdaptativeRecursive(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int level){
    if(level > segmentRecursionLimit)
      {
        return;
      }

    // Calculate all the mid-points of the line segments
    //----------------------
    float x12   = (x1 + x2) / 2;
    float y12   = (y1 + y2) / 2;
    float x23   = (x2 + x3) / 2;
    float y23   = (y2 + y3) / 2;
    float x34   = (x3 + x4) / 2;
    float y34   = (y3 + y4) / 2;
    float x123  = (x12 + x23) / 2;
    float y123  = (y12 + y23) / 2;
    float x234  = (x23 + x34) / 2;
    float y234  = (y23 + y34) / 2;
    float x1234 = (x123 + x234) / 2;
    float y1234 = (y123 + y234) / 2;

    // Try to approximate the full cubic curve by a single straight line
    //------------------
    float dx = x4-x1;
    float dy = y4-y1;

    float d2 = Math.abs(((x2 - x4) * dy - (y2 - y4) * dx));
    float d3 = Math.abs(((x3 - x4) * dy - (y3 - y4) * dx));
    float da1, da2;

    int d2b = (d2 > segmentCollinearityEpsilon)?1:0;
    int d3b = (d3 > segmentCollinearityEpsilon)?1:0;
    switch((d2b << 1) + d3b){
    case 0:
      // All collinear OR p1==p4
      //----------------------
      if(Math.abs(x1 + x3 - x2 - x2) +
         Math.abs(y1 + y3 - y2 - y2) +
         Math.abs(x2 + x4 - x3 - x3) +
         Math.abs(y2 + y4 - y3 - y3) <= segmentDistTolMnhttn)
        {
          addCurvePoint(new RPoint(x1234, y1234));
          return;
        }
      break;

    case 1:
      // p1,p2,p4 are collinear, p3 is considerable
      //----------------------
      if(d3 * d3 <= segmentDistTolSqr * (dx*dx + dy*dy))
        {
          if(segmentAngleTol < segmentAngleTolEpsilon)
            {
              addCurvePoint(new RPoint(x23, y23));
              return;
            }

          // Angle Condition
          //----------------------
          da1 = Math.abs((float)Math.atan2(y4 - y3, x4 - x3) - (float)Math.atan2(y3 - y2, x3 - x2));
          if(da1 >= (float)Math.PI) da1 = 2*(float)Math.PI - da1;

          if(da1 < segmentAngleTol)
            {
              addCurvePoint(new RPoint(x2, y2));
              addCurvePoint(new RPoint(x3, y3));
              return;
            }

          if(segmentCuspLimit != 0.0)
            {
              if(da1 > segmentCuspLimit)
                {
                  addCurvePoint(new RPoint(x3, y3));
                  return;
                }
            }
        }
      break;

    case 2:
      // p1,p3,p4 are collinear, p2 is considerable
      //----------------------
      if(d2 * d2 <= segmentDistTolSqr * (dx*dx + dy*dy))
        {
          if(segmentAngleTol < segmentAngleTolEpsilon)
            {
              addCurvePoint(new RPoint(x23, y23));
              return;
            }

          // Angle Condition
          //----------------------
          da1 = Math.abs((float)Math.atan2(y3 - y2, x3 - x2) - (float)Math.atan2(y2 - y1, x2 - x1));
          if(da1 >= (float)Math.PI) da1 = 2*(float)Math.PI - da1;

          if(da1 < segmentAngleTol)
            {
              addCurvePoint(new RPoint(x2, y2));
              addCurvePoint(new RPoint(x3, y3));
              return;
            }

          if(segmentCuspLimit != 0.0)
            {
              if(da1 > segmentCuspLimit)
                {
                  addCurvePoint(new RPoint(x2, y2));
                  return;
                }
            }
        }
      break;

    case 3:
      // Regular care
      //-----------------
      if((d2 + d3)*(d2 + d3) <= segmentDistTolSqr * (dx*dx + dy*dy))
        {
          // If the curvature doesn't exceed the distance_tolerance value
          // we tend to finish subdivisions.
          //----------------------
          if(segmentAngleTol < segmentAngleTolEpsilon)
            {
              addCurvePoint(new RPoint(x23, y23));
              return;
            }

          // Angle & Cusp Condition
          //----------------------
          float a23 = (float)Math.atan2(y3 - y2, x3 - x2);
          da1 = Math.abs(a23 - (float)Math.atan2(y2 - y1, x2 - x1));
          da2 = Math.abs((float)Math.atan2(y4 - y3, x4 - x3) - a23);
          if(da1 >= (float)Math.PI) da1 = 2*(float)Math.PI - da1;
          if(da2 >= (float)Math.PI) da2 = 2*(float)Math.PI - da2;

          if(da1 + da2 < segmentAngleTol)
            {
              // Finally we can stop the recursion
              //----------------------
              addCurvePoint(new RPoint(x23, y23));
              return;
            }

          if(segmentCuspLimit != 0.0)
            {
              if(da1 > segmentCuspLimit)
                {
                  addCurvePoint(new RPoint(x2, y2));
                  return;
                }

              if(da2 > segmentCuspLimit)
                {
                  addCurvePoint(new RPoint(x3, y3));
                  return;
                }
            }
        }
      break;
    }

    // Continue subdivision
    //----------------------
    cubicBezierAdaptativeRecursive(x1, y1, x12, y12, x123, y123, x1234, y1234, level + 1);
    cubicBezierAdaptativeRecursive(x1234, y1234, x234, y234, x34, y34, x4, y4, level + 1);
  }

  private void lineUniformStep(){
    // If the number of steps is equal to 0 then choose a number of steps adapted to the curve
    int steps = segmentSteps;
    if(segmentSteps==0.0F){
      float dx = endPoint.x - startPoint.x;
      float dy = endPoint.y - startPoint.y;

      float len = (float)Math.sqrt(dx * dx + dy * dy);
      steps = (int)(len * 0.25);

      if(steps < 4) steps = 4;
    }

    float dt = 1F/steps;

    float fx, fy, fdx, fdy;

    fx = startPoint.x;
    fdx = (endPoint.x - startPoint.x) * dt;

    fy = startPoint.y;
    fdy = (endPoint.y - startPoint.y) * dt;

    for (int loop=0; loop < steps; loop++) {
      addCurvePoint(new RPoint(fx,fy));

      fx = fx + fdx;

      fy = fy + fdy;
    }
    addCurvePoint(new RPoint(endPoint));
  }

  private void cubicBezierUniformStep(){

    // If the number of steps is equal to 0 then choose a number of steps adapted to the curve
    int steps = segmentSteps;
    if(segmentSteps==0.0F){
      float dx1 = controlPoints[0].x - startPoint.x;
      float dy1 = controlPoints[0].y - startPoint.y;
      float dx2 = controlPoints[1].x - controlPoints[0].x;
      float dy2 = controlPoints[1].y - controlPoints[0].y;
      float dx3 = endPoint.x - controlPoints[1].x;
      float dy3 = endPoint.y - controlPoints[1].y;

      float len = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1) +
        (float)Math.sqrt(dx2 * dx2 + dy2 * dy2) +
        (float)Math.sqrt(dx3 * dx3 + dy3 * dy3);

      steps = (int)(len * 0.25);

      if(steps < 4)
        {
          steps = 4;
        }
    }

    float dt = 1F/steps;

    float fx, fy, fdx, fdy, fddx, fddy, fdddx, fdddy, fdd_per_2x, fdd_per_2y, fddd_per_2x, fddd_per_2y, fddd_per_6x, fddd_per_6y;
    float temp = dt * dt;

    fx = startPoint.x;
    fdx = 3F * (controlPoints[0].x - startPoint.x) * dt;
    fdd_per_2x = 3F * (startPoint.x - 2F * controlPoints[0].x + controlPoints[1].x) * temp;
    fddd_per_2x = 3F * (3F * (controlPoints[0].x - controlPoints[1].x) + endPoint.x - startPoint.x) * temp * dt;
    fdddx = fddd_per_2x + fddd_per_2x;
    fddx = fdd_per_2x + fdd_per_2x;
    fddd_per_6x = fddd_per_2x * (1.0F / 3F);

    fy = startPoint.y;
    fdy = 3F * (controlPoints[0].y - startPoint.y) * dt;
    fdd_per_2y = 3F * (startPoint.y - 2F * controlPoints[0].y + controlPoints[1].y) * temp;
    fddd_per_2y = 3F * (3F * (controlPoints[0].y - controlPoints[1].y) + endPoint.y - startPoint.y) * temp * dt;
    fdddy = fddd_per_2y + fddd_per_2y;
    fddy = fdd_per_2y + fdd_per_2y;
    fddd_per_6y = fddd_per_2y * (1.0F / 3F);

    for (int loop=0; loop < steps; loop++) {
      addCurvePoint(new RPoint(fx,fy));

      fx = fx + fdx + fdd_per_2x + fddd_per_6x;
      fdx = fdx + fddx + fddd_per_2x;
      fddx = fddx + fdddx;
      fdd_per_2x = fdd_per_2x + fddd_per_2x;

      fy = fy + fdy + fdd_per_2y + fddd_per_6y;
      fdy = fdy + fddy + fddd_per_2y;
      fddy = fddy + fdddy;
      fdd_per_2y = fdd_per_2y + fddd_per_2y;
    }
    addCurvePoint(new RPoint(endPoint));
  }

  private void quadBezierUniformStep(){
    // If the number of steps is equal to 0 then choose a number of steps adapted to the curve
    int steps = segmentSteps;
    if(segmentSteps==0.0F){
      float dx1 = controlPoints[0].x - startPoint.x;
      float dy1 = controlPoints[0].y - startPoint.y;
      float dx2 = endPoint.x - controlPoints[0].x;
      float dy2 = endPoint.y - controlPoints[0].y;

      float len = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1) + (float)Math.sqrt(dx2 * dx2 + dy2 * dy2);
      steps = (int)(len * 0.25);

      if(steps < 4) steps = 4;
    }

    float dt = 1F/steps;

    float fx, fy, fdx, fdy, fddx, fddy, fdd_per_2x, fdd_per_2y;
    float temp = dt * dt;

    fx = startPoint.x;
    fdx = 2F * (controlPoints[0].x - startPoint.x) * dt;
    fdd_per_2x = (startPoint.x - 2F * controlPoints[0].x + endPoint.x) * temp;
    fddx = fdd_per_2x + fdd_per_2x;

    fy = startPoint.y;
    fdy = 2F * (controlPoints[0].y - startPoint.y) * dt;
    fdd_per_2y = (startPoint.y - 2F * controlPoints[0].y + endPoint.y) * temp;
    fddy = fdd_per_2y + fdd_per_2y;

    for (int loop=0; loop < steps; loop++) {
      addCurvePoint(new RPoint(fx,fy));

      fx = fx + fdx + fdd_per_2x;
      fdx = fdx + fddx;

      fy = fy + fdy + fdd_per_2y;
      fdy = fdy + fddy;
    }
    addCurvePoint(new RPoint(endPoint));
  }

  // Use Horner's method to advance
  //----------------------
  private void lineUniformLength(){

    // If the number of steps is equal to 0 then choose a number of steps adapted to the curve
    float dx1 = endPoint.x - startPoint.x;
    float dy1 = endPoint.y - startPoint.y;

    float len = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1);
    float steps = (int)(len * 2);

    if(steps < 4) steps = 4;

    // This holds the amount of steps used to calculate segment lengths
    float dt = 1F/steps;

    // This holds how much length has to bee advanced until adding a point
    float untilPoint = RCommand.segmentAccOffset;

    float fx, fy, fdx, fdy;

    fx = startPoint.x;
    fdx = (endPoint.x - startPoint.x) * dt;

    fy = startPoint.y;
    fdy = (endPoint.y - startPoint.y) * dt;

    for (int loop=0; loop <= steps; loop++) {
      /* Add point to curve if segment length is reached */
      if (untilPoint <= 0) {
        addCurvePoint(new RPoint(fx, fy));
        untilPoint += RCommand.segmentLength;
      }

      /* Add segment differential to segment length */
      untilPoint -= (float)Math.sqrt(fdx*fdx + fdy*fdy);    // Eventually try other distance measures

      fx = fx + fdx;
      fy = fy + fdy;
    }

    //addCurvePoint(new RPoint(endPoint));
    RCommand.segmentAccOffset = untilPoint;
  }

  // Use Horner's method to advance
  //----------------------
  private void quadBezierUniformLength(){

    float dx1 = controlPoints[0].x - startPoint.x;
    float dy1 = controlPoints[0].y - startPoint.y;
    float dx2 = endPoint.x - controlPoints[0].x;
    float dy2 = endPoint.y - controlPoints[0].y;
    float len = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1) + (float)Math.sqrt(dx2 * dx2 + dy2 * dy2);
    float steps = (int)(len * 2);

    if(steps < 4) steps = 4;

    float dt = 1F/steps;
    float untilPoint = RCommand.segmentAccOffset;

    float fx, fy, fdx, fdy, fddx, fddy, fdd_per_2x, fdd_per_2y, fix, fiy;
    float temp = dt * dt;

    fx = startPoint.x;
    fdx = 2F * (controlPoints[0].x - startPoint.x) * dt;
    fdd_per_2x = (startPoint.x - 2F * controlPoints[0].x + endPoint.x) * temp;
    fddx = fdd_per_2x + fdd_per_2x;

    fy = startPoint.y;
    fdy = 2F * (controlPoints[0].y - startPoint.y) * dt;
    fdd_per_2y = (startPoint.y - 2F * controlPoints[0].y + endPoint.y) * temp;
    fddy = fdd_per_2y + fdd_per_2y;

    for (int loop=0; loop <= steps; loop++) {
      /* Add point to curve if segment length is reached */
      if (untilPoint <= 0) {
        addCurvePoint(new RPoint(fx, fy));
        untilPoint += RCommand.segmentLength;
      }

      /* Add segment differential to segment length */
      fix = fdx + fdd_per_2x;
      fiy = fdy + fdd_per_2y;
      untilPoint -= (float)Math.sqrt(fix*fix + fiy*fiy);    // Eventually try other distance measures

      fx = fx + fix;
      fdx = fdx + fddx;

      fy = fy + fiy;
      fdy = fdy + fddy;
    }

    //addCurvePoint(new RPoint(endPoint));
    RCommand.segmentAccOffset = untilPoint;
  }

  // Use Horner's method to advance
  //----------------------
  private void cubicBezierUniformLength(){

    float dx1 = controlPoints[0].x - startPoint.x;
    float dy1 = controlPoints[0].y - startPoint.y;
    float dx2 = controlPoints[1].x - controlPoints[0].x;
    float dy2 = controlPoints[1].y - controlPoints[0].y;
    float dx3 = endPoint.x - controlPoints[1].x;
    float dy3 = endPoint.y - controlPoints[1].y;

    float len = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1) +
      (float)Math.sqrt(dx2 * dx2 + dy2 * dy2) +
      (float)Math.sqrt(dx3 * dx3 + dy3 * dy3);
    float steps = (int)(len * 2);

    if(steps < 4) steps = 4;

    float dt = 1F/steps;
    float untilPoint = RCommand.segmentAccOffset;

    float fx, fy, fdx, fdy, fddx, fddy, fdddx, fdddy, fdd_per_2x, fdd_per_2y, fddd_per_2x, fddd_per_2y, fddd_per_6x, fddd_per_6y, fix, fiy;
    float temp = dt * dt;

    fx = startPoint.x;
    fdx = 3F * (controlPoints[0].x - startPoint.x) * dt;
    fdd_per_2x = 3F * (startPoint.x - 2F * controlPoints[0].x + controlPoints[1].x) * temp;
    fddd_per_2x = 3F * (3F * (controlPoints[0].x - controlPoints[1].x) + endPoint.x - startPoint.x) * temp * dt;
    fdddx = fddd_per_2x + fddd_per_2x;
    fddx = fdd_per_2x + fdd_per_2x;
    fddd_per_6x = fddd_per_2x * (1.0F / 3F);

    fy = startPoint.y;
    fdy = 3F * (controlPoints[0].y - startPoint.y) * dt;
    fdd_per_2y = 3F * (startPoint.y - 2F * controlPoints[0].y + controlPoints[1].y) * temp;
    fddd_per_2y = 3F * (3F * (controlPoints[0].y - controlPoints[1].y) + endPoint.y - startPoint.y) * temp * dt;
    fdddy = fddd_per_2y + fddd_per_2y;
    fddy = fdd_per_2y + fdd_per_2y;
    fddd_per_6y = fddd_per_2y * (1.0F / 3F);

    for (int loop=0; loop < steps; loop++) {
      /* Add point to curve if segment length is reached */
      if (untilPoint <= 0) {
        addCurvePoint(new RPoint(fx, fy));
        untilPoint += RCommand.segmentLength;
      }

      /* Add segment differential to segment length */
      fix = fdx + fdd_per_2x + fddd_per_6x;
      fiy = fdy + fdd_per_2y + fddd_per_6y;
      untilPoint -= (float)Math.sqrt(fix*fix + fiy*fiy);    // Eventually try other distance measures

      fx = fx + fix;
      fdx = fdx + fddx + fddd_per_2x;
      fddx = fddx + fdddx;
      fdd_per_2x = fdd_per_2x + fddd_per_2x;

      fy = fy + fiy;
      fdy = fdy + fddy + fddd_per_2y;
      fddy = fddy + fdddy;
      fdd_per_2y = fdd_per_2y + fddd_per_2y;
    }

    //addCurvePoint(new RPoint(endPoint));
    RCommand.segmentAccOffset = untilPoint;
  }

  private float quadBezierLength(){

    float dx1 = controlPoints[0].x - startPoint.x;
    float dy1 = controlPoints[0].y - startPoint.y;
    float dx2 = endPoint.x - controlPoints[0].x;
    float dy2 = endPoint.y - controlPoints[0].y;
    float len = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1) + (float)Math.sqrt(dx2 * dx2 + dy2 * dy2);
    float steps = (int)(len * 2);

    if(steps < 4) steps = 4;

    float dt = 1F/steps;


    float fx, fy, fdx, fdy, fddx, fddy, fdd_per_2x, fdd_per_2y, fix, fiy;
    float temp = dt * dt;
    float totallen = 0F;

    fx = startPoint.x;
    fdx = 2F * (controlPoints[0].x - startPoint.x) * dt;
    fdd_per_2x = (startPoint.x - 2F * controlPoints[0].x + endPoint.x) * temp;
    fddx = fdd_per_2x + fdd_per_2x;

    fy = startPoint.y;
    fdy = 2F * (controlPoints[0].y - startPoint.y) * dt;
    fdd_per_2y = (startPoint.y - 2F * controlPoints[0].y + endPoint.y) * temp;
    fddy = fdd_per_2y + fdd_per_2y;

    for (int loop=0; loop <= steps; loop++) {
      /* Add segment differential to segment length */
      fix = fdx + fdd_per_2x;
      fiy = fdy + fdd_per_2y;
      totallen += (float)Math.sqrt(fix*fix + fiy*fiy);    // Eventually try other distance measures

      fx = fx + fix;
      fdx = fdx + fddx;

      fy = fy + fiy;
      fdy = fdy + fddy;
    }

    return totallen;
  }


  private float cubicBezierLength(){

    float dx1 = controlPoints[0].x - startPoint.x;
    float dy1 = controlPoints[0].y - startPoint.y;
    float dx2 = controlPoints[1].x - controlPoints[0].x;
    float dy2 = controlPoints[1].y - controlPoints[0].y;
    float dx3 = endPoint.x - controlPoints[1].x;
    float dy3 = endPoint.y - controlPoints[1].y;

    float len = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1) +
      (float)Math.sqrt(dx2 * dx2 + dy2 * dy2) +
      (float)Math.sqrt(dx3 * dx3 + dy3 * dy3);
    float steps = (int)(len * 2);

    if(steps < 4) steps = 4;

    float dt = 1F/steps;

    float fx, fy, fdx, fdy, fddx, fddy, fdddx, fdddy, fdd_per_2x, fdd_per_2y, fddd_per_2x, fddd_per_2y, fddd_per_6x, fddd_per_6y, fix, fiy;
    float temp = dt * dt;
    float totallen = 0F;

    fx = startPoint.x;
    fdx = 3F * (controlPoints[0].x - startPoint.x) * dt;
    fdd_per_2x = 3F * (startPoint.x - 2F * controlPoints[0].x + controlPoints[1].x) * temp;
    fddd_per_2x = 3F * (3F * (controlPoints[0].x - controlPoints[1].x) + endPoint.x - startPoint.x) * temp * dt;
    fdddx = fddd_per_2x + fddd_per_2x;
    fddx = fdd_per_2x + fdd_per_2x;
    fddd_per_6x = fddd_per_2x * (1.0F / 3F);

    fy = startPoint.y;
    fdy = 3F * (controlPoints[0].y - startPoint.y) * dt;
    fdd_per_2y = 3F * (startPoint.y - 2F * controlPoints[0].y + controlPoints[1].y) * temp;
    fddd_per_2y = 3F * (3F * (controlPoints[0].y - controlPoints[1].y) + endPoint.y - startPoint.y) * temp * dt;

    fdddy = fddd_per_2y + fddd_per_2y;
    fddy = fdd_per_2y + fdd_per_2y;
    fddd_per_6y = fddd_per_2y * (1.0F / 3F);

    for (int loop=0; loop < steps; loop++) {
      /* Add segment differential to segment length */
      fix = fdx + fdd_per_2x + fddd_per_6x;
      fiy = fdy + fdd_per_2y + fddd_per_6y;
      totallen += (float)Math.sqrt(fix*fix + fiy*fiy);    // Eventually try other distance measures

      fx = fx + fix;
      fdx = fdx + fddx + fddd_per_2x;
      fddx = fddx + fdddx;
      fdd_per_2x = fdd_per_2x + fddd_per_2x;

      fy = fy + fiy;
      fdy = fdy + fddy + fddd_per_2y;
      fddy = fddy + fdddy;
      fdd_per_2y = fdd_per_2y + fddd_per_2y;
    }

    return totallen;
  }


  /**
   * Use this method to transform the command.
   * @eexample transformCommand
   * @param g PGraphics, the graphics object on which to apply an affine transformation to the command
   */
  /*
    public void transform(RMatrix m){
    int numControlPoints = countControlPoints();
    if(numControlPoints!=0){
    for(int i=0;i<numControlPoints;i++){
    controlPoints[i].transform(m);
    }
    }
    startPoint.transform(m);
    endPoint.transform(m);
    }
  */
  private void append(RPoint nextcontrolpoint)
  {
    RPoint[] newcontrolPoints;
    if(controlPoints==null){
      newcontrolPoints = new RPoint[1];
      newcontrolPoints[0] = nextcontrolpoint;
    }else{
      newcontrolPoints = new RPoint[controlPoints.length+1];
      System.arraycopy(controlPoints,0,newcontrolPoints,0,controlPoints.length);
      newcontrolPoints[controlPoints.length]=nextcontrolpoint;
    }
    this.controlPoints=newcontrolPoints;
  }

  private void addCurvePoint(RPoint nextcurvepoint)
  {
    RPoint[] newcurvePoints;
    if(curvePoints==null){
      newcurvePoints = new RPoint[1];
      newcurvePoints[0] = nextcurvepoint;
    }else{
      newcurvePoints = new RPoint[curvePoints.length+1];
      System.arraycopy(curvePoints,0,newcurvePoints,0,curvePoints.length);
      newcurvePoints[curvePoints.length]=nextcurvepoint;
    }
    this.curvePoints=newcurvePoints;
  }

  public RPoint[] intersectionPoints(RCommand other)
  {
    RPoint[] result = null;

    switch (commandType) {
    case LINETO:
      switch (other.getCommandType()) {
      case LINETO:
        result = lineLineIntersection(this, other);
        break;

      case QUADBEZIERTO:
        result = lineQuadIntersection(this, other);
        break;

      case CUBICBEZIERTO:
        result = lineCubicIntersection(this, other);
        break;
      }
      break;

    case QUADBEZIERTO:
      switch (other.getCommandType()) {
      case LINETO:
        result = lineQuadIntersection(other, this);
        break;

      case QUADBEZIERTO:
        result = quadQuadIntersection(this, other);
        break;

      case CUBICBEZIERTO:
        result = quadCubicIntersection(this, other);
        break;
      }
      break;

    case CUBICBEZIERTO:
      switch (other.getCommandType()) {
      case LINETO:
        result = lineCubicIntersection(other, this);
        break;

      case QUADBEZIERTO:
        result = quadCubicIntersection(other, this);
        break;

      case CUBICBEZIERTO:
        result = cubicCubicIntersection(this, other);
        break;
      }
      break;
    }

    return result;
  }

  public static RPoint[] lineLineIntersection(RCommand c1, RCommand c2) {
    RPoint a = new RPoint(c1.startPoint);
    RPoint b = new RPoint(c1.endPoint);

    RPoint c = new RPoint(c2.startPoint);
    RPoint d = new RPoint(c2.endPoint);

    float epsilon = 1e-9f;

    //test for parallel case
    float denom = (d.y - c.y)*(b.x - a.x) - (d.x - c.x)*(b.y - a.y);
    if(Math.abs(denom) < epsilon)
      return null;

    //calculate segment parameter and ensure its within bounds
    float t1 = ((d.x - c.x)*(a.y - c.y) - (d.y - c.y)*(a.x - c.x))/denom;
    float t2 = ((b.x - a.x)*(a.y - c.y) - (b.y - a.y)*(a.x - c.x))/denom;
    
    if ( t1 < 0.0f || t1 > 1.0f || t2 < 0.0f || t2 > 1.0f )
      return null;

    //store actual intersection
    RPoint[] result = new RPoint[1];

    RPoint temp = new RPoint(b);
    temp.sub(a);
    temp.scale(t1);

    result[0] = new RPoint(a);
    result[0].add(temp);

    return result;
  }

  public static RPoint[] lineQuadIntersection(RCommand c1, RCommand c2) { return null; }
  public static RPoint[] lineCubicIntersection(RCommand c1, RCommand c2) { return null; }
  public static RPoint[] quadQuadIntersection(RCommand c1, RCommand c2) { return null; }
  public static RPoint[] quadCubicIntersection(RCommand c1, RCommand c2) { return null; }
  public static RPoint[] cubicCubicIntersection(RCommand c1, RCommand c2) { return null; }

  public RClosest closestPoints(RCommand other)
  {
    RClosest result = new RClosest();
    result.distance = 0;
    RPoint temp;

    switch (commandType) {
    case LINETO:
      switch (other.getCommandType()) {
      case LINETO:
        result.intersects = lineLineIntersection(this, other);
        if (result.intersects == null) {
          result = lineLineClosest(this, other);
        }
        break;

      case QUADBEZIERTO:
        result.intersects = lineQuadIntersection(this, other);
        if (result.intersects == null) {
          result = lineQuadClosest(this, other);
        }
        break;

      case CUBICBEZIERTO:
        result.intersects = lineCubicIntersection(this, other);
        if (result.intersects == null) {
          result = lineCubicClosest(this, other);
        }
        break;
      }
      break;

    case QUADBEZIERTO:
      switch (other.getCommandType()) {
      case LINETO:
        result.intersects = lineQuadIntersection(other, this);
        if (result.intersects == null) {
          result = lineQuadClosest(other, this);
          temp = result.closest[0];
          result.closest[0] = result.closest[1];
          result.closest[1] = temp;
        }
        break;

      case QUADBEZIERTO:
        result.intersects = quadQuadIntersection(this, other);
        if (result.intersects == null) {
          result = quadQuadClosest(this, other);
        }
        break;

      case CUBICBEZIERTO:
        result.intersects = quadCubicIntersection(this, other);
        if (result.intersects == null) {
          result = quadCubicClosest(this, other);
        }
        break;
      }
      break;

    case CUBICBEZIERTO:
      switch (other.getCommandType()) {
      case LINETO:
        result.intersects = lineCubicIntersection(other, this);
        if (result.intersects == null) {
          result = lineCubicClosest(other, this);
          temp = result.closest[0];
          result.closest[0] = result.closest[1];
          result.closest[1] = temp;
        }
        break;

      case QUADBEZIERTO:
        result.intersects = quadCubicIntersection(other, this);
        if (result.intersects == null) {
          result = quadCubicClosest(other, this);
          temp = result.closest[0];
          result.closest[0] = result.closest[1];
          result.closest[1] = temp;
        }
        break;

      case CUBICBEZIERTO:
        result.intersects = cubicCubicIntersection(this, other);
        if (result.intersects == null) {
          result = cubicCubicClosest(this, other);
        }
        break;
      }
      break;
    }

    return result;
  }

  public static float closestAdvFrom(RCommand c, RPoint p) {
    RPoint a = new RPoint(c.startPoint);
    RPoint b = new RPoint(c.endPoint);

    RPoint ap = new RPoint(p);
    ap.sub(a);

    RPoint ab = new RPoint(b);
    ab.sub(a);

    float denom = ab.sqrnorm();
    float epsilon = 1e-19f;

    if(denom < epsilon)
      return 0.5f;

    float t = (ab.x*ap.x + ab.y*ap.y) / denom;

    t = t > 0.0f ? t : 0.0f;
    t = t < 1.0f ? t : 1.0f;

    return t;

  }

  public static RClosest lineLineClosest(RCommand c1, RCommand c2) {
    RPoint c1b = new RPoint(c1.startPoint);
    RPoint c1e = new RPoint(c1.endPoint);

    float c2t1 = closestAdvFrom(c2, c1b);
    float c2t2 = closestAdvFrom(c2, c1e);

    RPoint c2p1 = c2.getPoint(c2t1);
    RPoint c2p2 = c2.getPoint(c2t2);

    float dist1c2 = c2p1.dist(c1b);
    float dist2c2 = c2p2.dist(c1e);

    RPoint c2b = new RPoint(c2.startPoint);
    RPoint c2e = new RPoint(c2.endPoint);

    float c1t1 = closestAdvFrom(c1, c2b);
    float c1t2 = closestAdvFrom(c1, c2e);

    RPoint c1p1 = c1.getPoint(c1t1);
    RPoint c1p2 = c1.getPoint(c1t2);

    float dist1c1 = c1p1.dist(c2b);
    float dist2c1 = c1p2.dist(c2e);

    
    RClosest result = new RClosest();
    result.distance = Math.min(Math.min(dist1c2, dist2c2), Math.min(dist1c1, dist2c1));
    result.closest = new RPoint[2];
    result.advancements = new float[2];

    if (result.distance == dist1c2) {
      result.closest[0] = c1b;
      result.closest[1] = c2p1;
      result.advancements[0] = 0;
      result.advancements[1] = c2t1;
    } else if (result.distance == dist2c2) {
      result.closest[0] = c1e;
      result.closest[1] = c2p2;
      result.advancements[0] = 1;
      result.advancements[1] = c2t2;
    } else if (result.distance == dist1c1) {
      result.closest[0] = c2b;
      result.closest[1] = c1p1;
      result.advancements[0] = 0;
      result.advancements[1] = c1t1;
    } else /*if (result.distance == dist2c1)*/ {
      result.closest[0] = c2e;
      result.closest[1] = c1p2;
      result.advancements[0] = 1;
      result.advancements[1] = c1t2;
    }


    /*
    RPoint c = new RPoint(c2.startPoint);
    RPoint d = new RPoint(c2.endPoint);

    float t1 = closestAdvFrom(c1, c);
    float t2 = closestAdvFrom(c1, d);

    RPoint p1 = c1.getPoint(t1);
    RPoint p2 = c1.getPoint(t2);

    float dist1 = p1.dist(c);
    float dist2 = p2.dist(d);

    RClosest result = new RClosest();
    result.closest = new RPoint[2];
    result.advancements = new float[2];
    if (dist1 < dist2) {
      result.closest[0] = p1;
      result.closest[1] = c;
      result.distance = dist1;
      result.advancements[0] = t1;
      result.advancements[1] = t2;
    } else {
      result.closest[0] = p2;
      result.closest[1] = d;
      result.distance = dist2;
      result.advancements[0] = t1;
      result.advancements[1] = t2;
    }
    */

    return result;
  }

  public static RClosest lineQuadClosest(RCommand c1, RCommand c2) { return null; }
  public static RClosest lineCubicClosest(RCommand c1, RCommand c2) { return null; }
  public static RClosest quadQuadClosest(RCommand c1, RCommand c2) { return null; }
  public static RClosest quadCubicClosest(RCommand c1, RCommand c2) { return null; }
  public static RClosest cubicCubicClosest(RCommand c1, RCommand c2) { return null; }
}