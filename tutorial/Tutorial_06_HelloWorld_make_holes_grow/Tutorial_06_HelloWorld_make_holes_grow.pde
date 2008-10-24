import geomerative.*;
import processing.opengl.*;

// Declare the objects we are going to use, so that they are accesible from setup() and from draw()
RFont f;
RGroup grp, grpshapes;
RMatrix mat;

// Declare the time counter variable and initialize it to 0
float t = 0;
float FREQ = 0.08;

void setup(){
  // Initilaize the sketch
  size(600, 400);
  frameRate(24);

  // VERY IMPORTANT: Allways initialize the library in the setup
  RG.init(this);

  // Choice of colors
  background(255);
  fill(0);
  stroke(0);
  
  // Load the font file we want to use (the file must be in the data folder in the sketch floder), with the size 60 and the alignment CENTER
  grpshapes = RG.getText("Hello world!", "FreeSans.ttf", 72, CENTER);

  // Enable smoothing
  smooth();
  
}

void draw(){
  // Clean frame
  background(255);
  
  // Set the origin to draw in the middle of the sketch
  translate(width/2, height/2);
  
  // If we want access to the holes we want are objects to be polygons.  So we convert all the elements on the group to polygons.
  grp = grpshapes.toPolygonGroup();
  
  // Access each element in the group in the form of a polygon
  for(int i=0;i<grp.countElements();i++){
      RPolygon p = (RPolygon)(grp.elements[i]);
      
      // This is a very important line.  In order to know if a contour is a hole or not we must first update it
      p = p.update();
      
      // Access each contour of the polygon
      for(int j=0;j<p.countContours();j++){
        RContour c = p.contours[j];
        if(c.isHole()){
          // Define a scaling transformation respect to the center of the hole
          RPoint center = p.getCenter();
          mat = new RMatrix();
          mat.scale(1 + 0.06*sin(TWO_PI*FREQ*(t - i)), center);
          
          c.transform(mat);
        } else {
          // Define a scaling transformation respect to the center of the contour
          RPoint center = p.getCenter();
          mat = new RMatrix();
          mat.scale(1 + 0.06*cos(TWO_PI*FREQ*(t - i)), center);
          
          c.transform(mat);
        }
      }
      
      grp.elements[i] = p;
  }
  
  // Draw the group of shapes representing "Hola mundo!" on the PGraphics canvas g (which is the default canvas of the sketch)
  grp.draw();
  
  // Increment the time counter
  t++;
}
