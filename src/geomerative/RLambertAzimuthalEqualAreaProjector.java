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
import java.lang.Math.*;

public class RLambertAzimuthalEqualAreaProjector extends RProjector
{   
  float phi1;
  float lamb0;
        
  float cos_phi1;
  float sin_phi1;

  private float asin(float x){
    return (float)java.lang.Math.asin((double)x);
  }

  private float acos(float x){
    return (float)java.lang.Math.acos((double)x);
  }

  private float atan(float x){
    return (float)java.lang.Math.atan((double)x);
  }

  private float sin(float x){
    return (float)java.lang.Math.sin((double)x);
  }

  private float cos(float x){
    return (float)java.lang.Math.cos((double)x);
  }

  private float sqr(float x){
    return (float)java.lang.Math.pow((double)x, 2F);
  }

  private float sqrt(float x){
    return (float)java.lang.Math.sqrt((double)x);
  }

  private float toRadians(float x){
    return (float)java.lang.Math.toRadians((double)x);
  }

  public RLambertAzimuthalEqualAreaProjector(float standardParallel, float centralLongitude){
    this.phi1 = toRadians(standardParallel);
    this.lamb0 = toRadians(centralLongitude);
        
    this.cos_phi1 = cos(this.phi1);
    this.sin_phi1 = sin(this.phi1);
  }
 
  public void project(RPoint p){
    float phi = toRadians(p.x);
    float lamb = toRadians(p.y);
      
    float k = sqrt(2F/(1F + this.sin_phi1 * sin(phi) + this.cos_phi1 * cos(phi) * cos(lamb - this.lamb0)));
      
    float x = k * cos(phi) * sin(lamb - this.lamb0);
    float y = k * (this.cos_phi1 * sin(phi) - this.sin_phi1 * cos(phi) *  cos(lamb - this.lamb0));

    p.setLocation(x, y);
  }

  public void unproject(RPoint p){
    float x = p.x;
    float y = p.y;

    float ro = sqrt(sqr(x) + sqr(y));
    float c = 2F* asin(ro/2F);
    float cos_c = cos(c);
    float sin_c = sin(c);
 
    float phi = asin(cos_c * this.sin_phi1 + y*sin(c)*this.cos_phi1/ro);
    float lamb = this.lamb0 + atan(x*sin_c / (ro*this.cos_phi1*cos_c - y*this.sin_phi1*sin_c));

    p.setLocation(phi, lamb);
  }
}
