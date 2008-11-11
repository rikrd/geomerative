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

import processing.core.*;

public class RProjector
{ 
  public void unproject(RPoint[] points){
    if (points == null) return;

    float xmax = Float.NEGATIVE_INFINITY ;
    float ymax = Float.NEGATIVE_INFINITY ;
    float xmin = Float.POSITIVE_INFINITY ;
    float ymin = Float.POSITIVE_INFINITY ;

    for(int i=0;i<points.length;i++){
      float tempx = points[i].x;
      float tempy = points[i].y;
      if( tempx < xmin )
        {
          xmin = tempx;
        }
      else if( tempx > xmax )
        {
          xmax = tempx;
        }
      if( tempy < ymin )
        {
          ymin = tempy;
        }
      else if( tempy > ymax )
        {
          ymax = tempy;
        }
    }
    
    for(int i=0;i<points.length;i++){
      points[i].setLocation((points[i].x - xmin)/(xmax - xmin), (points[i].y - ymin)/(ymax - ymin));
      this.unproject(points[i]);
    }
  }

  public void project(RPoint[] points){
    if (points == null) return;
    
    for(int i=0;i<points.length;i++){
      this.project(points[i]);
    }
  }

  public void project(RPoint p){
    return;
  }

  public void unproject(RPoint p){
    return;
  }
}
