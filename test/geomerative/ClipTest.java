package geomerative;

import junit.framework.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

/**
 *
 * This tests if Mark broke anything during his refactoring of RClip,
 * and if the performance is any better!
 *
 */
public class ClipTest extends TestCase {
  
  public static void main(String[] args) {
    TestSuite suite = new TestSuite(ClipTest.class);
    TestResult result = new TestResult();
    suite.run(result);
    
    
    if(result.failureCount() == 0 && result.errorCount() == 0) {
      System.out.println("Tests Passed!");
    }
    
    for(Enumeration failures = result.failures(); failures.hasMoreElements();) {
      TestFailure failure = (TestFailure)failures.nextElement();
      System.out.println(failure);
    }
    for(Enumeration errors = result.errors(); errors.hasMoreElements();) {
      TestFailure error = (TestFailure)errors.nextElement();
      System.out.println(error);
    }
    
  }
  
  public void testCorrectness() {
    System.out.println("running test");
    
    RPolygon a = makeShape(50);
    RPolygon b = makeShape(60);
    
    Fastness f = new Fastness();
    int n = 10;
    RPolygon slow_int=null,slow_union=null,slow_xor=null,slow_diff=null;
    RPolygon fast_int=null,fast_union=null,fast_xor=null,fast_diff=null;
    
    RG.useFastClip = false;
    f.record("slow intersection");
    for(int i = 0; i < n; i++) { slow_int   = RClip.intersection(a,b); }
    f.record("slow union");
    for(int i = 0; i < n; i++) { slow_union = RClip.union(a,b); }
    f.record("slow xor");
    for(int i = 0; i < n; i++) { slow_xor   = RClip.xor(a,b); }
    f.record("slow diff");
    for(int i = 0; i < n; i++) { slow_diff  = RClip.diff(a,b); }
    
    RG.useFastClip = true;
    f.record("fast int");
    for(int i = 0; i < n; i++) { fast_int   = RClip.intersection(a,b); }
    f.record("fast union");
    for(int i = 0; i < n; i++) { fast_union = RClip.union(a,b); }
    f.record("fast xor");
    for(int i = 0; i < n; i++) { fast_xor   = RClip.xor(a,b); }
    f.record("fast diff");
    for(int i = 0; i < n; i++) { fast_diff  = RClip.diff(a,b); }
    f.stop();
    f.print();
    
    checkPoints(slow_int, fast_int);
    checkPoints(slow_union, fast_union);
    checkPoints(slow_xor, fast_xor);
    checkPoints(slow_diff, fast_diff);
  }
  
  private void checkPoints(RPolygon slow, RPolygon fast) {
    RPoint[] slowp = slow.getPoints();
    RPoint[] fastp = fast.getPoints();
    assertEquals(slowp.length, fastp.length);
    int minlen = Math.min(slowp.length, fastp.length); // robustness is pessimism
    for(int i = 0; i < minlen; i++) {
      assertClose(slowp[i].x, fastp[i].x);
      assertClose(slowp[i].y, fastp[i].y);
    }
  }
  
  private void assertClose(float v1, float v2) {
    assertTrue(Math.abs(v1-v2) < 0.0000000000001); // scientifix
  }
  
  RPolygon makeShape(int numPoints) {
    Random r = new Random();
    r.setSeed(numPoints);
    RPoint[] points = new RPoint[numPoints];
    for(int i = 0; i < numPoints; i++) {
      points[i] = new RPoint(r.nextFloat()*100,r.nextFloat()*100);
    }
    return new RPolygon(points).update();
  }
  
  private class Fastness {
    long lastRecordStart;
    String lastName;
    List<Recording> recordings = new ArrayList<Recording>();
    
    public void record(String name) {
        long now = System.currentTimeMillis();
        if(lastName != null) {
            recordings.add(new Recording(lastName,now-lastRecordStart));
        }
        lastName = name;
        lastRecordStart = now;
    }
    public void stop() {
        record("");
        lastName = null;
    }
    public void clear() {
        recordings.clear();
        lastName = null;
    }
    public void print() {
        stop();
        for(Recording r : recordings) {
            System.out.println(r);
        }
    }
  }
  private class Recording {
     Recording(String name, long length) {
         this.name = name;
         this.length = length;
     }
     long length;
     String name;
     public String toString() {
         return name+" "+length;
     }
  }
}
