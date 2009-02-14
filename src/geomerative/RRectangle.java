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
  RPoint topLeft;
  RPoint bottomRight;
  
  RRectangle()
  {
    topLeft = new RPoint();
    bottomRight = new RPoint();
  }
  
  RRectangle(float x,float y,float w,float h)
  {
    topLeft = new RPoint(x,y);
    bottomRight = new RPoint(x+w,y+h);
    
  }
  
  RRectangle(RPoint _topLeft,RPoint _bottomRight)
  {
    this.topLeft = _topLeft;
    this.bottomRight = _bottomRight;
  } 

  RPoint[] getPoints() {
    RPoint[] ps = new RPoint[4];

    ps[0] = new RPoint(topLeft);
    
    ps[1] = new RPoint(topLeft);
    ps[1].x = bottomRight.x;
    
    ps[2] = new RPoint(bottomRight);
    
    ps[3] = new RPoint(bottomRight);
    ps[3].x = topLeft.x;
    
    return ps;
  }
  
  float getMaxX()
  {
    //return (topLeft.x > bottomRight.x) ? topLeft.x : bottomRight.x;
    return bottomRight.x;
  }
  
  float getMaxY()
  {
    //return (topLeft.y > bottomRight.y) ? topLeft.y : bottomRight.y;
    return bottomRight.y;
  }
  
  float getMinX()
  {
    //return (topLeft.x < bottomRight.x) ? topLeft.x : bottomRight.x;
    return topLeft.x;
  }
  
  float getMinY()
  {
    //return (topLeft.y < bottomRight.y) ? topLeft.y : bottomRight.y;
    return topLeft.y;
  }
  
  public String toString()
  {
    return "";
  }
}
