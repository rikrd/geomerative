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
import processing.core.*;

/**
 * This class allows contains information about an evaluation of the closest points or intersections between shapes/paths/commands.
 */
public class RClosest
{
  public RPoint[] intersects;
  public RPoint[] closest;

  // TODO: get here the max value of an integer
  public float distance = 10000;
  public float[] advancements;

  public RClosest() {}
  
  public void update(RClosest other) {
    if (other.intersects == null) {

      if (other.distance > this.distance) return;

      this.distance = other.distance;
      this.closest = other.closest;
      this.advancements = other.advancements;
      
    } else {

      this.closest = null;
      this.advancements = null;
      this.distance = 0;
      RPoint[] newIntersects = null;
      
      if(this.intersects==null){
        this.intersects = new RPoint[other.intersects.length];
        System.arraycopy(other.intersects, 0, this.intersects, 0, other.intersects.length);
      }else{
        newIntersects = new RPoint[this.intersects.length + other.intersects.length];
        System.arraycopy(this.intersects, 0, newIntersects, 0, this.intersects.length);
        System.arraycopy(other.intersects, 0, newIntersects, this.intersects.length, other.intersects.length);
        this.intersects = newIntersects;
      }
    }
  }
}