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

/**
 * RPoint is a very simple interface for creating, holding and drawing 2D points.
 * @eexample RPoint
 * @usage Geometry
 * @related x
 * @related y
 */
public class RPoint
{
  /**
   * The x coordinate of the point.
   * @eexample RPoint_x
   * @usage Geometry
   * @related y
   */
  public float x;

  /**
   * The y coordinate of the point.
   * @eexample RPoint_y
   * @usage Geometry
   * @related x
   */
  public float y;

  /**
   * Create a new point, given the coordinates.
   * @eexample RPoint_constructor
   * @usage Geometry
   * @param x  the x coordinate of the new point
   * @param y  the y coordinate of the new point
   * @related x
   * @related y
   */
  public RPoint(float x,float y)
  {
    this.x = x;
    this.y = y;
  }

  public RPoint(double x, double y)
  {
    this.x = (float)x;
    this.y = (float)y;
  }

  /**
   * Create a new point at (0, 0).
   * @eexample RPoint_constructor
   * @usage Geometry
   * @related x
   * @related y
   */
  public RPoint()
  {
    x = 0;
    y = 0;
  }

  /**
   * Copy a point.
   * @eexample RPoint_constructor
   * @usage Geometry
   * @param p  the point we wish to make a copy of
   * @related x
   * @related y
   */
  public RPoint(RPoint p)
  {
    this.x = p.x;
    this.y = p.y;
  }

  /**
   * @invisible
   */
  float getX()
  {
    return this.x;
  }

  /**
   * @invisible
   */
  float getY()
  {
    return this.y;
  }


  /**
   * @invisible
   */
  void setLocation(float nx, float ny)
  {
    this.x = nx;
    this.y = ny;
  }

  /**
   * Use this to apply a transformation to the point.
   * @eexample RPoint_transform
   * @usage Geometry
   * @param m  the transformation matrix to be applied
   * @related translate ( )
   * @related rotate ( )
   * @related scale ( )
   */
  public void transform(RMatrix m)
  {
    float tempx = m.m00*x + m.m01*y + m.m02;
    float tempy = m.m10*x + m.m11*y + m.m12;

    x = tempx;
    y = tempy;
  }

  /**
   * Apply a translation to the point.
   * @eexample RPoint_translate
   * @usage Geometry
   * @param tx  the coefficient of x translation
   * @param ty  the coefficient of y translation
   * @related transform ( )
   * @related rotate ( )
   * @related scale ( )
   */
  public void translate(float tx, float ty)
  {
    x += tx;
    y += ty;
  }

  /**
   * Apply a translation to the point.
   * @eexample RPoint_translate
   * @usage Geometry
   * @param t  the translation vector to be applied
   * @related transform ( )
   * @related rotate ( )
   * @related scale ( )
   */
  public void translate(RPoint t)
  {
    x += t.x;
    y += t.y;
  }

  /**
   * Apply a rotation to the point, given the angle and optionally the coordinates of the center of rotation.
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
    float c = (float)Math.cos(angle);
    float s = (float)Math.sin(angle);

    x -= vx;
    y -= vy;

    float tempx = x;
    float tempy = y;

    x = tempx*c - tempy*s;
    y = tempx*s + tempy*c;

    x += vx;
    y += vy;
  }

  public void rotate(float angle)
  {
    float c = (float)Math.cos(angle);
    float s = (float)Math.sin(angle);

    float tempx = x;
    float tempy = y;

    x = tempx*c - tempy*s;
    y = tempx*s + tempy*c;
  }

  /**
   * Apply a rotation to the point, given the angle and optionally the point of the center of rotation.
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
    float c = (float)Math.cos(angle);
    float s = (float)Math.sin(angle);

    x -= v.x;
    y -= v.y;

    float tempx = x;
    float tempy = y;

    x = tempx*c - tempy*s;
    y = tempx*s + tempy*c;

    x += v.x;
    y += v.y;
  }

  /**
   * Apply a scaling to the point, given the scaling factors.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param sx  the scaling coefficient over the x axis
   * @param sy  the scaling coefficient over the y axis
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale (float sx, float sy)
  {
    x *= sx;
    y *= sy;
  }

  /**
   * Apply a scaling to the point, given a scaling factor.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param s  the scaling coefficient for a uniform scaling
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale (float s)
  {
    x *= s;
    y *= s;
  }

  /**
   * Apply a scaling to the point, given a scaling vector.
   * @eexample RPoint_scale
   * @usage Geometry
   * @param s  the scaling vector
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   */
  public void scale (RPoint s)
  {
    x *= s.x;
    y *= s.y;
  }


  /**
   * Use this to normalize the point. This means that after applying, it's norm will be equal to 1.
   * @eexample RPoint_normalize
   * @usage Geometry
   * @related transform ( )
   * @related translate ( )
   * @related rotate ( )
   * @related scale ( )
   */
  public void normalize ()
  {
    float norma = norm();
    if(norma!=0) scale(1/norma);
  }

  /**
   * Use this to subtract a vector from this point.
   * @eexample RPoint_sub
   * @usage Geometry
   * @param p  the vector to substract
   * @related add ( )
   * @related mult ( )
   * @related cross ( )
   */
  public void sub (RPoint p)
  {
    x -= p.x;
    y -= p.y;
  }

  /**
   * Use this to add a vector to this point.
   * @eexample RPoint_add
   * @usage Geometry
   * @param p  the vector to add
   * @related sub ( )
   * @related mult ( )
   * @related cross ( )
   */
  public void add (RPoint p)
  {
    x += p.x;
    y += p.y;
  }

  /**
   * Use this to multiply a vector to this point. This returns a float corresponding to the scalar product of both vectors.
   * @eexample RPoint_mult
   * @usage Geometry
   * @param p  the vector to multiply
   * @return float, the result of the scalar product
   * @related add ( )
   * @related sub ( )
   * @related cross ( )
   */
  public float mult (RPoint p)
  {
    return (x * p.x + y * p.y);
  }

  /**
   * Use this to perform a cross product of the point with another point.  This returns a RPoint corresponding to the cross product of both vectors.
   * @eexample RPoint_cross
   * @usage Geometry
   * @param p  the vector to perform the cross product with
   * @return RPoint, the resulting vector of the cross product
   * @related add ( )
   * @related sub ( )
   * @related mult ( )
   */
  public RPoint cross (RPoint p)
  {
    return new RPoint(x * p.y - p.x * y, y * p.x - p.y * x);
  }

  /**
   * Use this to obtain the norm of the point.
   * @eexample RPoint_norm
   * @usage Geometry
   * @return float, the norm of the point
   * @related angle ( )
   */
  public float norm ()
  {
    return (float)Math.sqrt(mult(this));
  }

  /**
   * Use this to obtain the square norm of the point.
   * @eexample RPoint_norm
   * @usage Geometry
   * @return float, the norm of the point
   * @related angle ( )
   */
  public float sqrnorm ()
  {
    return (float)mult(this);
  }

  /**
   * Use this to obtain the angle between the vector and another vector
   * @eexample RPoint_angle
   * @usage Geometry
   * @param p  the vector relative to which we want to evaluate the angle
   * @return float, the angle between the two vectors
   * @related norm ( )
   */
  public float angle (RPoint p)
  {
    float normp = p.norm();
    float normthis = norm();
    return (float)Math.acos(mult(p)/(normp*normthis));
  }

  /**
   * Use this to obtain the distance between the vector and another vector
   * @eexample RPoint_dist
   * @usage Geometry
   * @param p  the vector relative to which we want to evaluate the distance
   * @return float, the distance between the two vectors
   * @related norm ( )
   */
  public float dist (RPoint p)
  {
    float dx = (p.x-this.x);
    float dy = (p.y-this.y);
    return (float)Math.sqrt(dx*dx + dy*dy);
  }


  public void print(){
    System.out.print("("+x+","+y+")\n");
  }
}
