package geomerative ;

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
   * Use this to create a new point.
   * @eexample RPoint_constructor
   * @usage Geometry
   * @param x float, the x coordinate of the new point
   * @param y float, the y coordinate of the new point
   * @param p RPoint, the point we wish to make a copy of
   * @related x
   * @related y
   */
  public RPoint()
  {
  	x = 0;
  	y = 0;
  }
  
  public RPoint(float x,float y)
  {
  	this.x = x;
  	this.y = y;
  }
  
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
   * @param m RMatrix, the transformation matrix to be applied
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
   * Use this to apply a translation to the point.
   * @eexample RPoint_translate
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
	x += tx;
	y += ty;
  }

  public void translate(RPoint t)
  {
	x += t.x;
	y += t.y;
  }

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
	float c = (float)Math.cos(angle);
	float s = (float)Math.sin(angle);

	float tempx = x;
	float tempy = y;

	x = tempx*c - tempy*s;
	y = tempx*s + tempy*c;
  }

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
	x *= sx;
	y *= sy;
  }

  public void scale (float s)
  {
	x *= s;
	y *= s;
  }

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
   * Use this to substract a vector to this point.
   * @eexample RPoint_sub
   * @usage Geometry
   * @param p RPoint, the vector to substract
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
   * @param p RPoint, the vector to add
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
   * @param p RPoint, the vector to multiply
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
   * @param p RPoint, the vector to perform the cross product with
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
   * Use this to obtain the angle between the vector and another vector
   * @eexample RPoint_angle
   * @usage Geometry
   * @param p RPoint, the vector relative to which we want to evaluate the angle
   * @return float, the angle between the two vectors
   * @related norm ( )
   */
  public float angle (RPoint p)
  {
	float normp = p.norm();
	float normthis = norm();
	return (float)Math.acos(mult(p)/(normp*normthis));
  }

  public void print(){
    System.out.print("("+x+","+y+")");
  }
}
