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