package geomerative;
import processing.core.*;

/**
 * RGeomerative is a static class containing all the states, modes, etc..
 */
public class RGeomerative{
  static PApplet parent;
  static boolean ignoreStyles = false;

  public static void init(PApplet _parent){
    parent = _parent;
  }

  public static void ignoreStyles(){
    ignoreStyles = true;
  }

  public static void ignoreStyles(boolean _value){
    ignoreStyles = _value;
  }
}