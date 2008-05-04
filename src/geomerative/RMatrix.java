package geomerative ;

/**
 * RMatrix is a very simple interface for creating, holding 3x3 matrices with the most common 2D affine transformations such as translation, rotation, scaling and shearing.  We only have access to the first to rows of the matrix the last row is considered a constant 0, 0, 1 in order to have better performance.
 * @eexample RMatrix
 * @usage Geometry
 */
public class RMatrix
{
  
  float m00 = 1F;
  float m01 = 0F;
  float m02 = 0F;
  
  float m10 = 0F;
  float m11 = 1F;
  float m12 = 0F;
  
  float m20 = 0F;
  float m21 = 0F;
  float m22 = 1F;
  
  /**
   * Use this to create a new identity matrix.
   * @eexample RMatrix
   * @param m00 float, coefficient 00 of the matrix
   * @param m01 float, coefficient 01 of the matrix
   * @param m02 float, coefficient 02 of the matrix
   * @param m10 float, coefficient 10 of the matrix
   * @param m11 float, coefficient 11 of the matrix
   * @param m12 float, coefficient 12 of the matrix
   * @param src RMatrix, source matrix from where to copy the matrix
   * @usage Geometry
   * @related apply ( )
   * @related translate ( )
   * @related rotate ( )
   * @related scale ( )
   * @related shear ( )
   */
  public RMatrix()
  {
    m00 = 1F;
    m01 = 0F;
    m02 = 0F;
    
    m10 = 0F;
    m11 = 1F;
    m12 = 0F;
  }
  
  public RMatrix(float m00, float m01, float m02,
		 float m10, float m11, float m12)
  {
    set(m00, m01, m02,
        m10, m11, m12);
  }
  
  public RMatrix(RMatrix src)
  {
    set(src.m00, src.m01, src.m02,
        src.m10, src.m11, src.m12);
  }
  
  public RMatrix(String transformationString){
    String[] transfTokens = RGeomerative.parent.splitTokens(transformationString, ")");
    
    // Loop through all transformations
    for(int i=0; i<transfTokens.length; i++){
      // Check the transformation and the parameters
      String[] transf = RGeomerative.parent.splitTokens(transfTokens[i], "(");
      String[] params = RGeomerative.parent.splitTokens(transf[1], ", ");
      float[] fparams = new float[params.length];
      for(int j=0; j<params.length; j++){
        fparams[j] = RGeomerative.parent.parseFloat(params[j]);
      }
      
      transf[0] = RGeomerative.parent.trim(transf[0]);
      
      if(transf[0].equals("translate")){
        if(params.length == 1){
          this.translate(fparams[0]);
          
        }else if(params.length == 2){
          this.translate(fparams[0], fparams[1]);
          
        }
      }else if(transf[0].equals("rotate")){
        if(params.length == 1){
          this.rotate(RGeomerative.parent.radians(fparams[0]));
          
        }else if(params.length == 3){
          this.rotate(RGeomerative.parent.radians(fparams[0]), fparams[1], fparams[2]);
          
        }
        
      }else if(transf[0].equals("scale")){
        if(params.length == 1){
          this.scale(fparams[0]);
          
        }else if(params.length == 2){
          this.scale(fparams[0], fparams[1]);
        }
        
      }else if(transf[0].equals("skewX")){
        this.skewX(RGeomerative.parent.radians(fparams[0]));
        
      }else if(transf[0].equals("skewY")){
        this.skewY(RGeomerative.parent.radians(fparams[0]));
        
      }else if(transf[0].equals("matrix")){
        this.apply(fparams[0], fparams[2], fparams[4], fparams[1], fparams[3], fparams[5]);
        
      }else{
        throw new RuntimeException("Transformation unknown. '"+ transf[0]  +"'");
      }
    }
  }
  
  private void set(float m00, float m01, float m02,
		   float m10, float m11, float m12)
  {
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
  }
  
  /**
   * Use this to multiply the matrix with another matrix.  This is mostly use to chain transformations.
   * @eexample RMatrix_apply
   * @param n00 float, coefficient 00 of the matrix to be applied
   * @param n01 float, coefficient 01 of the matrix to be applied
   * @param n02 float, coefficient 02 of the matrix to be applied
   * @param n10 float, coefficient 10 of the matrix to be applied
   * @param n11 float, coefficient 11 of the matrix to be applied
   * @param n12 float, coefficient 12 of the matrix to be applied
   * @param src RMatrix, source matrix from where to copy the matrix
   * @usage Geometry
   * @related translate ( )
   * @related rotate ( )
   * @related scale ( )
   * @related shear ( )
   */
  public void apply(RMatrix rhs) {
    apply(rhs.m00, rhs.m01, rhs.m02,
          rhs.m10, rhs.m11, rhs.m12);
  }
  
  public void apply(float n00, float n01, float n02,
                    float n10, float n11, float n12) {
    
    float r00 = m00*n00 + m01*n10;
    float r01 = m00*n01 + m01*n11;
    float r02 = m00*n02 + m01*n12 + m02;
    
    float r10 = m10*n00 + m11*n10;
    float r11 = m10*n01 + m11*n11 ;
    float r12 = m10*n02 + m11*n12 + m12;
    
    m00 = r00; m01 = r01; m02 = r02;
    m10 = r10; m11 = r11; m12 = r12;
  }
  
  /**
   * Use this to apply a translation to the matrix.
   * @eexample RMatrix_translate
   * @param tx float, x coordinate translation
   * @param ty float, y coordinate translation
   * @param t RPoint, vector translation
   * @usage Geometry
   * @related rotate ( )
   * @related scale ( )
   * @related shear ( )
   */
  public void translate(float tx, float ty)
  {
    apply(1, 0, tx, 0, 1, ty);
  }
  
  public void translate(float tx)
  {
    translate(tx, 0);
  }
  
  public void translate(RPoint t)
  {
    translate(t.x, t.y);
  }
  
  /**
   * Use this to apply a rotation to the matrix.
   * @eexample RMatrix_rotate
   * @param angle float, the angle of rotation
   * @param vx float, x coordinate of the center of the rotation
   * @param vy float, y coordinate of the center of the rotation
   * @param v float, point of the center of the rotation
   * @usage Geometry
   * @related translate ( )
   * @related scale ( )
   * @related shear ( )
   */
  public void rotate(float angle)
  {
    float c = (float)Math.cos(angle);
    float s = (float)Math.sin(angle);
    apply(c, -s, 0,  s, c, 0);
  }
  
  public void rotate(float angle, float vx, float vy)
  {
    translate(vx,vy);
    rotate(angle);
    translate(-vx,-vy);
  }
  
  public void rotate(float angle, RPoint v)
  {
    rotate(angle, v.x, v.y);
  }
  
  /**
   * Use this to apply a scaling to the matrix.
   * @eexample RMatrix_translate
   * @param sx float, x coordinate scaling
   * @param sy float, y coordinate scaling
   * @param s float, scaling
   * @param vx float, x coordinate of the center of the scaling
   * @param vy float, y coordinate of the center of the scaling
   * @param v float, point of the center of the scaling
   * @usage Geometry
   * @related rotate ( )
   * @related translate ( )
   * @related shear ( )
   */
  public void scale(float sx, float sy)
  {
    apply(sx, 0, 0,  0, sy, 0);
  }
  
  public void scale(float s)
  {
    scale(s, s);
  }
  
  public void scale(float sx, float sy, float vx, float vy)
  {
    translate(vx,vy);
    scale(sx,sy);
    translate(-vx,-vy);
  }
  
  public void scale(float s, float vx, float vy)
  {
    scale(s, s, vx, vy);
  }
  
  public void scale(float sx, float sy, RPoint v)
  {
    scale(sx, sy, v.x, v.y);
  }
  
  public void scale(float s, RPoint v)
  {
    scale(s, s, v.x, v.y);
  }
  
  /**
   * Use this to apply a skewing to the matrix.
   * @eexample RMatrix_skewing
   * @param angle float, skewing angle
   * @usage Geometry
   * @related rotate ( )
   * @related scale ( )
   * @related translate ( )
   */
  public void skewX(float angle)
  {
    apply(1, (float)Math.tan(angle), 0,  0, 1, 0);
  }
  
  public void skewY(float angle)
  {
    apply(1, 0, 0, (float)Math.tan(angle), 1, 0);
  }
  
  /**
   * Use this to apply a shearing to the matrix.
   * @eexample RMatrix_translate
   * @param shx float, x coordinate shearing
   * @param shy float, y coordinate shearing
   * @usage Geometry
   * @related rotate ( )
   * @related scale ( )
   * @related translate ( )
   */
  public void shear(float shx, float shy)
  {
    apply(1, -shx, 0,  shy, 1, 0);
  }
}