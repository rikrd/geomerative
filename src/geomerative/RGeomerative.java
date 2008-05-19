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

/**
 * RGeomerative is a static class containing all the states, modes, etc..
 */
public class RGeomerative{
  static PApplet parent;
  static boolean ignoreStyles = false;

  public static class LibraryNotInitializedException extends NullPointerException{
    LibraryNotInitializedException(){
      super("Must call RGeomerative.init(this); before using this library.");
    }
    
  }

  public static void init(PApplet _parent){
    parent = _parent;
  }

  protected static PApplet parent(){
    if(parent == null){
      throw new LibraryNotInitializedException();
    }

    return parent;
  }

  public static void ignoreStyles(){
    ignoreStyles = true;
  }

  public static void ignoreStyles(boolean _value){
    ignoreStyles = _value;
  }
}