package geomerative ;
import processing.core.*;


/**
 * RGroup is a holder for a group of geometric element that can be drawn and transformed, such as Shapes, Polygons or Meshes.
 * @usage geometry
 */
public class RGroup extends RGeomElem
{
  /**
   * @invisible
   */
  public int type = RGeomElem.GROUP;
  
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
  
  /**
   * Array of RGeomElem objects holding the elements of the group. When accessing theses elements we must cast them to their class in order to get all the functionalities of each representation. e.g. RShape s = group.elements[i].toShape()  If the element cannot be converted to the target class it will throw a RuntimeException, to ignore these, use try-catch syntax.
   * @eexample RGroup_elements
   * @related RShape
   * @related RPolygon
   * @related RMesh
   * @related countElements ( )
   * @related addElement ( )
   * @related removeElement ( )
   */
  public RGeomElem[] elements;
  
  /**
   * Use this method to create a new empty group.
   * @eexample RGroup
   */
  public RGroup(){
    elements = null;
  }
  
  /**
   * Use this method to create a copy of a group.
   * @eexample RGroup
   */
  public RGroup(RGroup grp){
    for(int i=0;i<grp.countElements();i++){
      //System.out.println(grp.elements[i].getType());
      switch(grp.elements[i].getType()){
      case RGeomElem.MESH:
        this.addElement(new RMesh((RMesh)grp.elements[i]));
        break;
        
      case RGeomElem.GROUP:
        this.addElement(new RGroup((RGroup)grp.elements[i]));
        break;
        
      case RGeomElem.POLYGON:
        this.addElement(new RPolygon((RPolygon)grp.elements[i]));
        break;
        
      case RGeomElem.SHAPE:
        this.addElement(new RShape((RShape)grp.elements[i]));
        break;
        
      }
    }
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
    if(elements != null){
      for(int i=0;i<elements.length-1;i++)
        {
          float area = elements[i].getArea();
          if(area > bestArea){
            bestArea = area;
            bestCentroid = elements[i].getCentroid();
          }
        }
      return bestCentroid;
    }
    return null;
  }
  
  /**
   * Use this method to count the number of elements in the group.
   * @eexample RGroup_countElements
   * @return int, the number elements in the group.
   * @related addElement ( )
   * @related removeElement ( )
   */
  public int countElements(){
    if(elements==null) return 0;
    return elements.length;
  }
  
  public void print(){
    System.out.println("group: ");
    for(int i=0;i<countElements();i++)
      {
        System.out.println("---  "+i+" ---");
        elements[i].print();
        System.out.println("---------------");
      }
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
   * Use this method to draw the group.  This will draw each element at a time, without worrying about intersections or holes.  This is the main difference between having a shape with multiple subshapes and having a group with multiple shapes.
   * @eexample RGroup_draw
   * @param g PGraphics, the graphics object on which to draw the group
   */
  public void draw(PGraphics g){
    if(!RGeomerative.ignoreStyles){
      saveContext(g);
      setContext(g);
    }

    for(int i=0; i<countElements(); i++){
      elements[i].draw(g);
    }

    if(!RGeomerative.ignoreStyles){
      restoreContext(g);
    }
  }
  
  public void draw(PApplet a){
    if(!RGeomerative.ignoreStyles){
      saveContext(a);
      setContext(a);
    }

    for(int i=0; i<countElements(); i++){
      elements[i].draw(a);
    }
    
    if(!RGeomerative.ignoreStyles){
      restoreContext(a);
    }
  }

  /**
   * Use this method to transform the group.
   * @eexample RGroup_transform
   * @param m RMatrix, the affine transformation to apply to the group
   * @related draw ( )
   */
  /*
    public void transform(RMatrix m){
    for(int i=0; i<countElements(); i++){
    elements[i].transform(m);
    }
    }
  */
  
  /**
   * Use this method to add a new element.
   * @eexample RGroup_addElement
   * @param elem RGeomElem, any kind of RGeomElem to add.  It accepts the classes RShape, RPolygon and RMesh.
   * @related removeElement ( )
   */
  public void addElement(RGeomElem elem){
    this.append(elem);
  }
  
  /**
   * Use this method to add a new element.
   * @eexample RGroup_addGroup
   * @param grupo RGroup, A group of elements to add to this group.
   * @related removeElement ( )
   */
  public void addGroup(RGroup grupo){
    for(int i=0;i<grupo.countElements();i++){
      this.addElement(grupo.elements[i]);
    }
  }
  
  /**
   * Use this method to remove an element.
   * @eexample RGroup_removeElement
   * @param i int, the index of the element to remove from the group.
   * @related addElement ( )
   */
  public void removeElement(int i) throws RuntimeException{
    this.extract(i);
  }
  
  /**
   * Use this method to get a new group whose elements are the corresponding Meshes of the elemnts in the current group.  This can be used for increasing performance in exchange of losing abstraction.
   * @eexample RGroup_toMeshGroup
   * @return RGroup, the new group made of RMeshes
   * @related toPolygonGroup ( )
   * @related toShapeGroup ( )
   */
  public RGroup toMeshGroup() throws RuntimeException{
    RGroup result = new RGroup();
    for(int i=0;i<countElements();i++){
      result.addElement(elements[i].toMesh());
    }
    return result;
  }
  
  /**
   * Use this method to get a new group whose elements are the corresponding Polygons of the elemnts in the current group.  At this moment there is no implementation for transforming aMesh to a Polygon so applying this method to groups holding Mesh elements will generate an exception.
   * @eexample RGroup_toPolygonGroup
   * @return RGroup, the new group made of RPolygons
   * @related toMeshGroup ( )
   * @related toShapeGroup ( )
   */
  public RGroup toPolygonGroup() throws RuntimeException{
    RGroup result = new RGroup();
    for(int i=0;i<countElements();i++){
      result.addElement(elements[i].toPolygon());
    }
    return result;
  }
  
  /**
   * Use this method to get a new group whose elements are all the corresponding Shapes of the elemnts in the current group.  At this moment there is no implementation for transforming a Mesh or a Polygon to a Shape so applying this method to groups holding Mesh or Polygon elements will generate an exception.
   * @eexample RGroup_toShapeGroup
   * @return RGroup, the new group made of RShapes
   * @related toMeshGroup ( )
   * @related toPolygonGroup ( )
   */
  public RGroup toShapeGroup() throws RuntimeException{
    RGroup result = new RGroup();
    for(int i=0;i<countElements();i++){
      result.addElement(elements[i].toShape());
    }
    return result;
  }
  
  /**
   * @invisible
   */
  public RMesh toMesh() throws RuntimeException{
    //throw new RuntimeException("Transforming a Group to a Mesh is not yet implemented.");
    RGroup meshGroup = toMeshGroup();
    RMesh result = new RMesh();
    for(int i=0;i<countElements();i++){
      RMesh currentMesh = (RMesh)(meshGroup.elements[i]);
      for(int j=0;j<currentMesh.countStrips();j++){
        result.addStrip(currentMesh.strips[j]);
      }
    }
    result.setStyle(this);
    return result;
  }
  
  /**
   * @invisible
   */
  public RPolygon toPolygon() throws RuntimeException{
    //throw new RuntimeException("Transforming a Group to a Polygon is not yet implemented.");
    RGroup polygonGroup = toPolygonGroup();
    RPolygon result = new RPolygon();
    for(int i=0;i<countElements();i++){
      RPolygon currentPolygon = (RPolygon)(polygonGroup.elements[i]);
      for(int j=0;j<currentPolygon.countContours();j++){
        result.addContour(currentPolygon.contours[j]);
      }
    }
    result.setStyle(this);
    return result;
  }
  
  /**
   * @invisible
   */
  public RShape toShape() throws RuntimeException{
    //throw new RuntimeException("Transforming a Group to a Shape is not yet implemented.");
    RGroup shapeGroup = toShapeGroup();
    RShape result = new RShape();
    for(int i=0;i<countElements();i++){
      RShape currentShape = (RShape)(shapeGroup.elements[i]);
      for(int j=0;j<currentShape.countSubshapes();j++){
        result.addSubshape(currentShape.subshapes[j]);
      }
    }
    result.setStyle(this);
    return result;
  }
  
  /**
   * Use this method to get the bounding box of the group. 
   * @eexample getBounds
   * @return RContour, the bounding box of the group in the form of a fourpoint contour
   * @related getCenter ( )
   */
  public RContour getBounds(){
    float xmin =  Float.MAX_VALUE ;
    float ymin =  Float.MAX_VALUE ;
    float xmax = -Float.MAX_VALUE ;
    float ymax = -Float.MAX_VALUE ;
    for(int j=0;j<this.countElements();j++){
      RContour bbox = this.elements[j].getBounds();
      float tempxmin = bbox.points[0].x;
      float tempxmax = bbox.points[2].x;
      float tempymin = bbox.points[0].y;
      float tempymax = bbox.points[2].y;
      if( tempxmin < xmin ) xmin = tempxmin;
      if( tempxmax > xmax ) xmax = tempxmax;
      if( tempymin < ymin ) ymin = tempymin;
      if( tempymax > ymax ) ymax = tempymax;
    }
    RContour c = new RContour();
    c.addPoint(xmin,ymin);
    c.addPoint(xmin,ymax);
    c.addPoint(xmax,ymax);
    c.addPoint(xmax,ymin);
    return c;
  }
  
  /**
   * Use this method to get the center point of the group.
   * @eexample RGroup_getCenter
   * @return RPoint, the center point of the group
   * @related getBounds ( )
   */
  public RPoint getCenter(){
    RContour c = getBounds();
    return new RPoint((c.points[2].x + c.points[0].x)/2,(c.points[2].y + c.points[0].y)/2);
  }
  
  /**
   * Use this to return the points of the group.  It returns the points in the way of an array of RPoint.
   * @eexample RGroup_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getPoints(){
    int numElements = countElements();
    if(numElements == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numElements;i++){
      RPoint[] newPoints = elements[i].getPoints();
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
   * Use this to return the points of the group.  It returns the points in the way of an array of RPoint.
   * @eexample RGroup_getPoints
   * @return RPoint[], the points returned in an array.
   * */
  public RPoint[] getCurvePoints(){
    int numElements = countElements();
    if(numElements == 0){
      return null;
    }
    
    RPoint[] result=null;
    RPoint[] newresult=null;
    for(int i=0;i<numElements;i++){
      RPoint[] newPoints = elements[i].getCurvePoints();
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
   * Use this method to adapt a group of of figures to a shape.
   * @eexample RGroup_adaptTo
   * @param RSubshape sshp, the subshape to which to adapt
   * @return RGroup, the adapted group
   */
  RGroup adaptTo(RSubshape sshp, float wght, float lngthOffset) throws RuntimeException{
    RGroup result = new RGroup(this);
    RContour c = result.getBounds();
    float xmin = c.points[0].x;
    float xmax = c.points[2].x;
    float ymin = c.points[0].y;
    float ymax = c.points[2].y;
    
    int numElements = result.countElements();
    
    switch(adaptorType){
    case BYPOINT:
      for(int i=0;i<numElements;i++){
        RGeomElem elem = result.elements[i];
        RPoint[] ps = elem.getPoints();
        if(ps != null){
          for(int k=0;k<ps.length;k++){
            float px = ps[k].x;
            float py = ps[k].y;
            
            float t = ((px-xmin)/(xmax-xmin) + lngthOffset ) % 1F;
            float amp = (ymax-py);
            
            RPoint tg = sshp.getCurveTangent(t);
            RPoint p = sshp.getCurvePoint(t);
            float angle = (float)Math.atan2(tg.y, tg.x) - (float)Math.PI/2F;
            
            ps[k].x = p.x + wght*amp*(float)Math.cos(angle);
            ps[k].y = p.y + wght*amp*(float)Math.sin(angle);
          }
        }
      }
      break;
    case BYELEMENTPOSITION:
      
      for(int i=0;i<numElements;i++){
        RGeomElem elem = result.elements[i];
        RContour elemc = elem.getBounds();
        
        float px = (elemc.points[2].x + elemc.points[0].x) / 2;
        float py = elemc.points[2].y;
        float t = ((px-xmin)/(xmax-xmin) + lngthOffset ) % 1F;
        
        RPoint tg = sshp.getCurveTangent(t);
        RPoint p = sshp.getCurvePoint(t);
        float angle = (float)Math.atan2(tg.y, tg.x);
        
        RPoint pletter = new RPoint(px,0);
        p.sub(pletter);
        
        RMatrix mtx = new RMatrix();
        mtx.translate(p);
        mtx.rotate(angle,pletter);
        mtx.scale(wght,pletter);
        
        elem.transform(mtx);
      }
      break;
      
    case BYELEMENTINDEX:
      
      for(int i=0;i<numElements;i++){
        RGeomElem elem = result.elements[i];
        RContour elemc = elem.getBounds();
        
        float px = (elemc.points[2].x + elemc.points[0].x) / 2;
        float py = elemc.points[2].y;
        float t = ((float)i/(float)numElements + lngthOffset ) % 1F;
        
        RPoint tg = sshp.getCurveTangent(t);
        RPoint p = sshp.getCurvePoint(t);
        float angle = (float)Math.atan2(tg.y, tg.x);
        
        RPoint pletter = new RPoint(px,0);
        p.sub(pletter);
        
        RMatrix mtx = new RMatrix();
        mtx.translate(p);
        mtx.rotate(angle,pletter);
        mtx.scale(wght,pletter);
        
        elem.transform(mtx);
      }
      break;
      
    default:
      throw new RuntimeException("Unknown adaptor type : "+adaptorType+". The method setAdaptor() only accepts RGroup.BYPOINT or RGroup.BYELEMENT as parameter values.");
    }
    return result;
  }
  
  public RGroup adaptTo(RSubshape sshp) throws RuntimeException{
    return adaptTo(sshp, adaptorScale, adaptorLengthOffset);
  }
  
  public RGroup adaptTo(RShape shp) throws RuntimeException{
    RGroup result = new RGroup();
    int numSubshapes = shp.countSubshapes();
    for(int i=0;i<numSubshapes;i++){
      RGroup tempresult = adaptTo(shp.subshapes[i]);
      int numElements = tempresult.countElements();
      for(int j=0;j<numElements;j++){
        result.addElement(tempresult.elements[j]);
      }
    }
    return result;
  }
  
  void append(RGeomElem elem){
    RGeomElem[] newelements;
    if(elements==null){
      newelements = new RGeomElem[1];
      newelements[0] = elem;
    }else{
      newelements = new RGeomElem[this.elements.length+1];
      System.arraycopy(this.elements,0,newelements,0,this.elements.length);
      newelements[this.elements.length]=elem;
    }
    this.elements=newelements;
  }
  
  void extract(int i) throws RuntimeException{
    RGeomElem[] newelements;
    if(elements==null){
      throw new RuntimeException("The group is empty. No elements to remove.");
    }else{
      if(i<0){
        throw new RuntimeException("Negative values for indexes are not valid.");
      }
      if(i>elements.length-1){
        throw new RuntimeException("Index out of the bounds of the group.  You are trying to erase an element with an index higher than the number of elements in the group.");
      }
      if(elements.length==1){
        newelements = null;
      }else if(i==0){
        newelements = new RGeomElem[this.elements.length-1];
        System.arraycopy(this.elements,1,newelements,0,this.elements.length-1);
      }else if(i==elements.length-1){
        newelements = new RGeomElem[this.elements.length-1];
        System.arraycopy(this.elements,0,newelements,0,this.elements.length-1);
      }else{
        newelements = new RGeomElem[this.elements.length-1];
        System.arraycopy(this.elements,0,newelements,0,i);
        System.arraycopy(this.elements,i+1,newelements,i,this.elements.length-i-1);
      }
    }
    this.elements=newelements;
  }
}
