package geomerative ;

class RRectangle
{
  RPoint p;
  RPoint q;
  
  RRectangle()
  {
  	p = new RPoint();
  	q = new RPoint();
  }
  
  RRectangle(float x,float y,float w,float h)
  {
  	p = new RPoint(x,y);
  	q = new RPoint(x+w,y+h);
  	
  }
  
  RRectangle(RPoint np,RPoint nq)
  {
  	this.p = np;
  	this.q = nq;
  } 
  
  float getMaxX()
  {
  	return (p.x > q.x) ? p.x : q.x;
  }
  
  float getMaxY()
  {
  	return (p.y > q.y) ? p.y : q.y;
  }
  
  float getMinX()
  {
  	return (p.x < q.x) ? p.x : q.x;
  }
  
  float getMinY()
  {
  	return (p.y < q.y) ? p.y : q.y;
  }
  
  public String toString()
  {
  	return "";
  }
}