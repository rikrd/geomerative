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

import processing.core.PApplet;

/**
 * RMatrix is a very simple interface for creating, holding 3x3 matrices with the most common 2D affine transformations such as translation, rotation, scaling and shearing.  We only have access to the first to rows of the matrix the last row is considered a constant 0, 0, 1 in order to have better performance.
 * @eexample RMatrix
 * @usage Geometry
 * @extended
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
   * Create a new matrix given the coefficients.
   * @eexample RMatrix
   * @param m00  coefficient 00 of the matrix
   * @param m01  coefficient 01 of the matrix
   * @param m02  coefficient 02 of the matrix
   * @param m10  coefficient 10 of the matrix
   * @param m11  coefficient 11 of the matrix
   * @param m12  coefficient 12 of the matrix
   * @usage Geometry
   * @related apply ( )
   * @related translate ( )
   * @related rotate ( )
   * @related scale ( )
   * @related shear ( )
   */
  public RMatrix(float m00, float m01, float m02,
		 float m10, float m11, float m12)
  {
    set(m00, m01, m02,
        m10, m11, m12);
  }

  /**
   * Create a new identity matrix.
   * @eexample RMatrix
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
  
  /**
   * Copy a matrix.
   * @eexample RMatrix
   * @param src  source matrix from where to copy the matrix
   * @usage Geometry
   * @related apply ( )
   * @related translate ( )
   * @related rotate ( )
   * @related scale ( )
   * @related shear ( )
   */
  public RMatrix(RMatrix src)
  {
    set(src.m00, src.m01, src.m02,
        src.m10, src.m11, src.m12);
  }
  
  public RMatrix(String transformationString){
    String[] transfTokens = PApplet.splitTokens(transformationString, ")");
    
    // Loop through all transformations
    for(int i=0; i<transfTokens.length; i++){
      // Check the transformation and the parameters
      String[] transf = PApplet.splitTokens(transfTokens[i], "(");
      String[] params = PApplet.splitTokens(transf[1], ", ");
      float[] fparams = new float[params.length];
      for(int j=0; j<params.length; j++){
        fparams[j] = PApplet.parseFloat(params[j]);
      }
      
      transf[0] = PApplet.trim(transf[0]);
      
      if(transf[0].equals("translate")){
        if(params.length == 1){
          this.translate(fparams[0]);
          
        }else if(params.length == 2){
          this.translate(fparams[0], fparams[1]);
          
        }
      }else if(transf[0].equals("rotate")){
        if(params.length == 1){
          this.rotate(PApplet.radians(fparams[0]));
          
        }else if(params.length == 3){
          this.rotate(PApplet.radians(fparams[0]), fparams[1], fparams[2]);
          
        }
        
      }else if(transf[0].equals("scale")){
        if(params.length == 1){
          this.scale(fparams[0]);
          
        }else if(params.length == 2){
          this.scale(fparams[0], fparams[1]);
        }
        
      }else if(transf[0].equals("skewX")){
        this.skewX(PApplet.radians(fparams[0]));
        
      }else if(transf[0].equals("skewY")){
        this.skewY(PApplet.radians(fparams[0]));
        
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
   * Multiply the matrix with another matrix.  This is mostly use to chain transformations.
   * @eexample RMatrix_apply
   * @param n00  coefficient 00 of the matrix to be applied
   * @param n01  coefficient 01 of the matrix to be applied
   * @param n02  coefficient 02 of the matrix to be applied
   * @param n10  coefficient 10 of the matrix to be applied
   * @param n11  coefficient 11 of the matrix to be applied
   * @param n12  coefficient 12 of the matrix to be applied
   * @usage Geometry
   * @related translate ( )
   * @related rotate ( )
   * @related scale ( )
   * @related shear ( )
   */ 
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
   * Multiply the matrix with another matrix.  This is mostly use to chain transformations.
   * @eexample RMatrix_apply
   * @param rhs  right hand side matrix
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

  /**
   * Apply a translation to the matrix, given the coordinates.
   * @eexample RMatrix_translate
   * @param tx  x coordinate translation
   * @param ty  y coordinate translation
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
  
  /**
   * Apply a translation to the matrix, given a point.
   * @eexample RMatrix_translate
   * @param t  vector translation
   * @usage Geometry
   * @related rotate ( )
   * @related scale ( )
   * @related shear ( )
   */
  public void translate(RPoint t)
  {
    translate(t.x, t.y);
  }
  
  /**
   * Apply a rotation to the matrix, given an angle and optionally a rotation center.
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
    translate(vx,vy);
    rotate(angle);
    translate(-vx,-vy);
  }

  public void rotate(float angle)
  {
    float c = (float)Math.cos(angle);
    float s = (float)Math.sin(angle);
    apply(c, -s, 0,  s, c, 0);
  }

  /**
   * Apply a rotation to the matrix, given an angle and optionally a rotation center.
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
    rotate(angle, v.x, v.y);
  }
  
  /**
   * Apply a scale to the matrix, given scaling factors and optionally a scaling center.
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
  public void scale(float sx, float sy, float x, float y)
  {
    translate(x,y);
    scale(sx,sy);
    translate(-x,-y);
  }

  public void scale(float sx, float sy)
  {
    apply(sx, 0, 0,  0, sy, 0);
  }
   
  /**
   * Apply a scale to the matrix, given scaling factors and optionally a scaling center.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param s  the scaling coefficient for a uniform scaling
   * @param x  x coordinate of the position vector of the center of the scaling
   * @param y  y coordinate of the position vector of the center of the scaling
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale(float s, float x, float y)
  {
    scale(s, s, x, y);
  }
  
  /**
   * Apply a scale to the matrix, given scaling factors and optionally a scaling center.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param sx  the scaling coefficient over the x axis
   * @param sy  the scaling coefficient over the y axis
   * @param p  the position vector of the center of the scaling
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale(float sx, float sy, RPoint p)
  {
    scale(sx, sy, p.x, p.y);
  }

  /**
   * Apply a scale to the matrix, given scaling factors and optionally a scaling center.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param s  the scaling coefficient for a uniform scaling
   * @param p  the position vector of the center of the scaling
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */  
  public void scale(float s, RPoint p)
  {
    scale(s, s, p.x, p.y);
  }
  
  public void scale(float s)
  {
    scale(s, s);
  }

  /**
   * Use this to apply a skewing to the matrix.
   * @eexample RMatrix_skewing
   * @param angle  skewing angle
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
   * @param shx  x coordinate shearing
   * @param shy  y coordinate shearing
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
