package geomerative ;
import processing.core.*;

/**
 * RSubshape is a reduced interface for creating, holding and drawing contours. Subshapes are ordered lists of commands (RCommand) which define the outlines of Shapes.  Subshapes can be self-intersecting.
 * @eexample RSubshape
 * @usage Geometry
 * @related RCommand
 * @related RPolygon
 */
public class RSubshape extends RGeomElem
{
  /**
   * @invisible
   */
  public int type = RGeomElem.SUBSHAPE;
  private float[] lenCommands;
  private float lenSubshape = -1F;
  
  /**
   * Array of RCommand objects holding the commands of the subshape.
   * @eexample commands
   * @related RCommand
   * @related countCommands ( )
   */
  public RCommand[] commands;
  
  /**
   * Last point from where to add the next command.  Initialized to (0, 0).
   * @eexample lastPoint
   * @related RPoint
   * @invisible
   */
  public RPoint lastPoint;
  
  /**
   * Use this method to create a new subshape.
   * @eexample RSubshape
   * @param x float, the x coordinate of the first point of the subshape
   * @param y float, the y coordinate of the first point of the subshape
   * @param RSubshape s, the object from which to make the copy
   * @param RPoint p, the first point of the subshape
   */
  public RSubshape(){
    this.lastPoint = new RPoint(0,0);
  }
  
  public RSubshape(float x, float y){
    this.lastPoint = new RPoint(x,y);
  }
  
  public RSubshape(RPoint p){
    this.lastPoint = p;
  }
  
  public RSubshape(RSubshape s){
    int numCommands = s.countCommands();
    if(numCommands!=0){
      lastPoint = new RPoint(s.commands[0].startPoint);
      for(int i=0;i<numCommands;i++){
        this.append(new RCommand(s.commands[i], lastPoint));
        lastPoint = commands[i].endPoint;
      }
    }
    this.id = s.id;
    this.texture = s.texture;
    this.fillColour = s.fillColour;
    this.strokeColour = s.strokeColour;
    //addClose();
  }
  
  /**
   * Use this method to count the number of commands in the contour. 
   * @eexample countCommands
   * @return int, the number commands in the contour
   */
  public int countCommands(){
    if(this.commands==null){
      return 0;
    }

    return this.commands.length;
  }
  
  /**
   * Use this to return the start, control and end points of the subshape.  It returns the points in the way of an array of RPoint.
   * @eexample getPoints
   * @return RPoint[], the start, control and end points returned in an array.
   * */
  public RPoint[] getPoints(){
    int numCommands = countCommands();
    if(numCommands == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numCommands;i++){
      RPoint[] newPoints = commands[i].getPoints();
      if(newPoints!=null){
        if(newPoints.length!=1){
          int overlap = 1;
          if(result==null){
            result = new RPoint[newPoints.length];
            System.arraycopy(newPoints,0,result,0,newPoints.length);
          }else{
            newresult = new RPoint[result.length + newPoints.length - overlap];
            System.arraycopy(result,0,newresult,0,result.length);
            System.arraycopy(newPoints,overlap,newresult,result.length,newPoints.length - overlap);
            result = newresult;
          }
        }
      }
    }
    
    return newresult;
  }
  
  /**
   * Use this to return the points on the curve.  It returns the points in the way of an array of RPoint.
   * @eexample getCurvePoints
   * @return RPoint[], the vertices returned in an array.
   * */
  public RPoint[] getCurvePoints(){
    int numCommands = countCommands();
    if(numCommands == 0){
      return null;
    }
    
    // Add the curve points of each command
    
    // First set the accumulated offset to the value of the inital offset
    RCommand.segmentAccOffset = RCommand.segmentOffset;
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numCommands;i++){
      RPoint[] newPoints = commands[i].getCurvePoints();
      if(newPoints!=null){
        if(result==null){
          result = new RPoint[newPoints.length];
          System.arraycopy(newPoints,0,result,0,newPoints.length);
        }else{
          // Check for overlapping
          // Overlapping happens when the last point of the last command 
          // is the same as the first point of the current command
          RPoint lastp = result[result.length-1];
          RPoint firstp = newPoints[0];
          int overlap = 0;
          if((lastp.x == firstp.x) && (lastp.y == firstp.y))
            {
              overlap = 1;
            }
          newresult = new RPoint[result.length + newPoints.length - overlap];
          System.arraycopy(result,0,newresult,0,result.length);
          System.arraycopy(newPoints,overlap,newresult,result.length,newPoints.length - overlap);
          result = newresult;
        }
      }
    }

    // Always add last point
    newresult = new RPoint[result.length + 1];
    System.arraycopy(result,0,newresult,0,result.length);
    newresult[newresult.length - 1] = new RPoint(commands[numCommands-1].endPoint);
    
    return newresult;
  }
  
  /**
   * Use this to return the points on the curve.  It returns the points in the way of an array of RPoint.
   * @eexample getCurveLength
   * @return float[], the arclengths of each command of the subshape.
   * */
  public float[] getCurveLengths(){
    int numCommands = countCommands();
    if(numCommands == 0){
      return null;
    }
    
    /* If the cache with the commands lengths is empty, we fill it up */
    if(lenCommands == null){
      lenCommands = new float[numCommands];
      lenSubshape = 0F;
      for(int i=0;i<numCommands;i++){
        lenCommands[i] = commands[i].getCurveLength();
        lenSubshape += lenCommands[i];
      }
    }

    return lenCommands;
  }

  /**
   * Use this to return the points on the curve.  It returns the points in the way of an array of RPoint.
   * @eexample getCurveLength
   * @return float, the arclength of the subshape.
   * */
  public float getCurveLength(){
    int numCommands = countCommands();
    if(numCommands == 0){
      return 0F;
    }
    
    /* If the cache with the commands lengths is empty, we fill it up */
    if(lenSubshape == -1F){
      getCurveLengths();
    }

    return lenSubshape;    
  }
  
  /**
   * Use this to return the tangents on the curve.  It returns the vectors in the way of an array of RPoint.
   * @eexample getCurveTangents
   * @param segments int, the number of segments in which to divide each command.
   * @return RPoint[], the tangent vectors returned in an array.
   * */
  public RPoint[] getCurveTangents(){
    int numCommands = countCommands();
    if(numCommands == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numCommands;i++){
      RPoint[] newPoints = commands[i].getCurveTangents();
      if(newPoints!=null){
        if(newPoints.length!=1){
          int overlap = 1;
          if(result==null){
            result = new RPoint[newPoints.length];
            System.arraycopy(newPoints,0,result,0,newPoints.length);
          }else{
            newresult = new RPoint[result.length + newPoints.length - overlap];
            System.arraycopy(result,0,newresult,0,result.length);
            System.arraycopy(newPoints,overlap,newresult,result.length,newPoints.length - overlap);
            result = newresult;
          }
        }
      }
    }
    return result;
  }
  
  /**
   * Use this to return a specific point on the curve.  It returns the RPoint for a given advancement parameter t on the curve.
   * @eexample getCurvePoint
   * @param t float, the parameter of advancement on the curve. t must have values between 0 and 1.
   * @return RPoint, the vertice returned.
   * */
  public RPoint getCurvePoint(float t){
    float advOfCommand;
    int numCommands = countCommands();
    if(numCommands == 0){
      return new RPoint();
    }
    
    if(t==0.0F){ return commands[0].getCurvePoint(0F); }
    if(t==1.0F){ return commands[numCommands-1].getCurvePoint(1F); }

    float[] lengthsCommands = getCurveLengths();
    float lengthSubshape = getCurveLength();

    /* Calculate the amount of advancement t mapped to each command */
    /* We use a simple algorithm where we give to each command the same amount of advancement */
    /* A more useful way would be to give to each command an advancement proportional to the length of the command */
    /* Old method with uniform advancement per command
       float advPerCommand;
       advPerCommand = 1F / numCommands;
       indCommand = (int)(Math.floor(t / advPerCommand)) % numCommands;
       advOfCommand = (t*numCommands - indCommand);
    */
    
    int indCommand = 0;
    float accumulatedAdvancement = lengthsCommands[indCommand] / lengthSubshape;
    float prevAccumulatedAdvancement = 0F;

    /* Find in what command the advancement point is  */
    while(t > accumulatedAdvancement){
      indCommand++;
      prevAccumulatedAdvancement = accumulatedAdvancement;
      accumulatedAdvancement += (lengthsCommands[indCommand] / lengthSubshape);
    }

    advOfCommand = (t-prevAccumulatedAdvancement) / (lengthsCommands[indCommand] / lengthSubshape);

    return commands[indCommand].getCurvePoint(advOfCommand);
  }
  
  /**
   * Use this to return a specific tangent on the curve.  It returns the RPoint tangent for a given advancement parameter t on the curve.
   * @eexample getCurvePoint
   * @param t float, the parameter of advancement on the curve. t must have values between 0 and 1.
   * @return RPoint, the vertice returned.
   * */
  public RPoint getCurveTangent(float t){

    float advOfCommand;
    int numCommands = countCommands();
    if(numCommands == 0){
      return new RPoint();
    }
    
    if(t==0.0F){ return commands[0].getCurveTangent(0F); }
    if(t==1.0F){ return commands[numCommands-1].getCurveTangent(1F); }
    
    float[] lengthsCommands = getCurveLengths();
    float lengthSubshape = getCurveLength();

    //System.out.println("Length of subshape = " + lenSubshape);

    /* Calculate the amount of advancement t mapped to each command */
    /* We use a simple algorithm where we give to each command the same amount of advancement */
    /* A more useful way would be to give to each command an advancement proportional to the length of the command */
    /* Old method with uniform advancement per command
       float advPerCommand;
       advPerCommand = 1F / numCommands;
       indCommand = (int)(Math.floor(t / advPerCommand)) % numCommands;
       advOfCommand = (t*numCommands - indCommand);
    */

    int indCommand = 0;
    float accumulatedAdvancement = lengthsCommands[indCommand] / lengthSubshape;
    float prevAccumulatedAdvancement = 0F;

    /* Find in what command the advancement point is  */
    while(t > accumulatedAdvancement){
      indCommand++;
      prevAccumulatedAdvancement = accumulatedAdvancement;
      accumulatedAdvancement += (lengthsCommands[indCommand] / lengthSubshape);
    }

    advOfCommand = (t-prevAccumulatedAdvancement) / (lengthsCommands[indCommand] / lengthSubshape);
    
    
    /* This takes the medium between two intersecting commands, sometimes this is not wanted
       if(advOfCommand==1.0F){
       int indNextCommand = (indCommand + 1) % numCommands;
       result = commands[indCommand].getCurveTangent(advOfCommand);
       RPoint tngNext = commands[indNextCommand].getCurveTangent(0.0F);
       result.add(tngNext);
       result.scale(0.5F);
       }else if (advOfCommand==0.0F){
       int indPrevCommand = (indCommand - 1 + numCommands) % numCommands;
       result = commands[indCommand].getCurveTangent(advOfCommand);
       RPoint tngPrev = commands[indPrevCommand].getCurveTangent(1.0F);
       result.add(tngPrev);
       result.scale(0.5F);
       }else{
       result = commands[indCommand].getCurveTangent(advOfCommand);
       }
    */
    
    return commands[indCommand].getCurveTangent(advOfCommand);
  }
  
  /**
   * Use this method to draw the subshape. 
   * @eexample drawSubshape
   * @param g PGraphics, the graphics object on which to draw the subshape
   */
  public void draw(PGraphics g){
    int numCommands = countCommands();

    // By default always draw with an adaptative segmentator
    int lastSegmentator = RCommand.segmentType;
    RCommand.setSegmentator(RCommand.ADAPTATIVE);

    RPoint[] points = getCurvePoints();

    if(points == null){
      return;
    }
    g.beginShape();
    for(int i=0;i<points.length;i++){
      g.vertex(points[i].x,points[i].y);
    }
    g.endShape();

    // Restore the user set segmentator
    RCommand.setSegmentator(lastSegmentator);
  }
  
  public void draw(PApplet g){
    int numCommands = countCommands();

    // By default always draw with an adaptative segmentator
    int lastSegmentator = RCommand.segmentType;
    RCommand.setSegmentator(RCommand.ADAPTATIVE);

    RPoint[] points = getCurvePoints();
    RCommand.setSegmentator(lastSegmentator);
    if(points == null){
      return;
    }
    g.beginShape();
    for(int i=0;i<points.length;i++){
      g.vertex(points[i].x,points[i].y);
    }
    g.endShape();

    // Restore the user set segmentator
    RCommand.setSegmentator(lastSegmentator);
  }
  /**
   * Use this method to get the bounding box of the subshape. 
   * @eexample getBounds
   * @return RContour, the bounding box of the subshape in the form of a fourpoint contour
   * @related draw ( )
   */
  public RContour getBounds(){
    float xmin =  Float.MAX_VALUE ;
    float ymin =  Float.MAX_VALUE ;
    float xmax = -Float.MAX_VALUE ;
    float ymax = -Float.MAX_VALUE ;
    
    for( int i = 0 ; i < this.countCommands() ; i++ )
      {
        RPoint[] points = this.commands[i].getPoints();
        if(points!=null){
          for( int k = 0 ; k < points.length ; k++ ){
            float x = points[k].x;
            float y = points[k].y;
            if( x < xmin ) xmin = x;
            if( x > xmax ) xmax = x;
            if( y < ymin ) ymin = y;
            if( y > ymax ) ymax = y;
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
   * Use this method to add new commands to the contour.
   * @eexample addCommand
   * @invisible
   */
  public void addCommand(RCommand p){
    this.append(p);
    
    lastPoint = commands[commands.length-1].endPoint;
  }
  
  /**
   * Use this method to add a new cubic bezier to the subshape. The first point of the bezier will be the last point added to the subshape.
   * @eexample addBezierTo
   * @param float cp1x, the x coordinate of the first control point
   * @param float cp1y, the y coordinate of the first control point
   * @param float cp2x, the x coordinate of the second control point
   * @param float cp2y, the y coordinate of the second control point
   * @param float endx, the x coordinate of the ending point
   * @param float endy, the y coordinate of the ending point
   */
  public void addBezierTo(RPoint cp1, RPoint cp2, RPoint end){
    this.addCommand(RCommand.createBezier4(lastPoint, cp1, cp2, end));
  }
  
  public void addBezierTo(float cp1x, float cp1y, float cp2x, float cp2y, float endx, float endy){
    RPoint cp1 = new RPoint(cp1x, cp1y);
    RPoint cp2 = new RPoint(cp2x, cp2y);
    RPoint end = new RPoint(endx, endy);
    
    addBezierTo(cp1,cp2,end);
  }
  
  /**
   * Use this method to add a new quadratic bezier to the subshape. The first point of the bezier will be the last point added to the subshape.
   * @eexample addQuadTo
   * @param float cp1x, the x coordinate of the first control point
   * @param float cp1y, the y coordinate of the first control point
   * @param float endx, the x coordinate of the ending point
   * @param float endy, the y coordinate of the ending point
   */
  public void addQuadTo(RPoint cp1, RPoint end){
    this.addCommand(RCommand.createBezier3(lastPoint, cp1, end));
  }
  
  public void addQuadTo(float cp1x, float cp1y, float endx, float endy){
    RPoint cp1 = new RPoint(cp1x, cp1y);
    RPoint end = new RPoint(endx, endy);
    
    addQuadTo(cp1,end);
  }
  
  /**
   * Use this method to add a new line to the subshape. The first point of the line will be the last point added to the subshape.
   * @eexample addLineTo
   * @param float endx, the x coordinate of the ending point
   * @param float endy, the y coordinate of the ending point
   */
  public void addLineTo(RPoint end){
    this.addCommand(RCommand.createLine(lastPoint, end));
  }
  
  public void addLineTo(float endx, float endy){
    RPoint end = new RPoint(endx, endy);
    addLineTo(end);
  }
  
  
  public void addClose(){
    if(commands == null){
      return;
    }

    if((commands[commands.length-1].endPoint.x == commands[0].startPoint.x) && (commands[commands.length-1].endPoint.y == commands[0].startPoint.y))
      {
        commands[commands.length-1].endPoint = new RPoint(commands[0].startPoint.x, commands[0].startPoint.y);
      }else{
      addLineTo(new RPoint(commands[0].startPoint.x,commands[0].startPoint.y));
    }
    
    lastPoint = commands[commands.length-1].endPoint;
  }

  /**
   * @invisible
   */
  public RPolygon toPolygon(){
    return this.toShape().toPolygon();
  }
  
  /**
   * @invisible
   */
  public RShape toShape(){
    return new RShape(this);
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
  
  
  public void print(){
    for(int i=0;i<commands.length;i++){
      String commandType = "";
      switch(commands[i].commandType)
        {
          case RCommand.LINETO:
          commandType = "LINETO";
          break;

          case RCommand.CUBICBEZIERTO:
          commandType = "BEZIERTO";
          break;

          case RCommand.QUADBEZIERTO:
          commandType = "QUADBEZIERTO";
          break;
        }

      System.out.println("cmd type: " + commandType);
      System.out.print("start point: ");
      commands[i].startPoint.print();
      System.out.print("\n");
      System.out.print("end point: ");
      commands[i].endPoint.print();
      System.out.print("\n");
      if(commands[i].controlPoints != null)
        {
          System.out.println("control points: ");
          for(int j=0;j<commands[i].controlPoints.length;j++){
            commands[i].controlPoints[j].print();
            System.out.print(" ");
            System.out.print("\n");
          }
        }
      System.out.print("\n");
    }
  }
  
  /**
   * Use this method to transform the shape. 
   * @eexample RSubshape_transform
   * @param m RMatrix, the matrix defining the affine transformation
   * @related draw ( )
   */
  // OPT: not transform the EndPoint since it's equal to the next StartPoint
  /*
  public void transform(RMatrix m){
    RPoint[] ps = getPoints();
    if(ps!=null){
      for(int i=0;i<ps.length;i++){
        ps[i].transform(m);
      }
    }
    
      int numCommands = countCommands();
      if(numCommands!=0){
      commands[0].startPoint.transform(m);
      for(int i=0;i<numCommands-1;i++){
      for(int j=0;j<commands[i].countControlPoints();j++){
      commands[i].controlPoints[j].transform(m);
      }
      commands[i].endPoint.transform(m);
      }
      }
    
  }
  */
  
  void append(RCommand nextcommand)
  {
    RCommand[] newcommands;
    if(commands==null){
      newcommands = new RCommand[1];
      newcommands[0] = nextcommand;
    }else{
      newcommands = new RCommand[this.commands.length+1];
      System.arraycopy(this.commands,0,newcommands,0,this.commands.length);
      newcommands[this.commands.length]=nextcommand;
    }
    this.commands=newcommands; 
  }
}
