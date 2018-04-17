package geomerative;

import processing.core.PApplet;
import junit.framework.TestCase;

public class HelloSplittingShapesTest extends TestCase
{
    RPolygon poly;
    
    public void setUp() {
      PApplet applet = new PApplet();
      RG.init(applet);
      RSVG svgLoader = new RSVG();
      // need to be in the geomerative directory for this to work
      poly = svgLoader.toGroup("./tutorial/Tutorial_13_HelloSplittingShapes/data/bot1.svg").toPolygon();        
    }
    
    public void testOldMeshing() {
      RG.useFastClip = false;
      RMesh mesh = RClip.polygonToMesh(poly);
      assertNotNull(mesh);
    }

    public void testNewMeshing() {
      RG.useFastClip = true;
      RMesh mesh = RClip.polygonToMesh(poly);
      assertNotNull(mesh);
    }

}
