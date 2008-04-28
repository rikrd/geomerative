import geomerative.*;
import processing.opengl.*;

// Declare the objects we are going to use, so that they are accesible from setup() and from draw()
RFont f;
RGroup grp;
RPoint[] points;

void setup(){
  // Initilaize the sketch
  size(600,400,OPENGL);
  frameRate(24);

  // Choice of colors
  background(255);
  fill(255,102,0);
  stroke(0);
  
  // VERY IMPORTANT: Allways initialize the library in the setup
  RGeomerative.init(this);
  
  //  Load the font file we want to use (the file must be in the data folder in the sketch floder), with the size 60 and the alignment CENTER
  f = new RFont("FreeSans.ttf",72,RFont.CENTER);
  grp = f.toGroup("Hola mundo!");

  // Enable smoothing
  smooth();
}

void draw(){
  // Clean frame
  background(255);
  
  // Set the origin to draw in the middle of the sketch
  translate(width/2, 3*height/4);
  
  // Draw the group of shapes
  noFill();
  stroke(0,0,200,150);
  RCommand.setSegmentator(RCommand.ADAPTATIVE);
  grp.draw();
  
  // Get the points on the curve's shape
  //RCommand.setSegmentator(RCommand.UNIFORMSTEP);
  //RCommand.setSegmentStep(map(float(mouseY), 0.0, float(height), 0.0, 1.0));
  
  RCommand.setSegmentator(RCommand.UNIFORMLENGTH);
  RCommand.setSegmentLength(map(mouseY, 0, height, 3, 200));
  points = grp.getCurvePoints();
  
  // If there are any points
  if(points != null){
    noFill();
    stroke(0,200,0);
    beginShape();
    for(int i=0; i<points.length; i++){
      vertex(points[i].x, points[i].y);
    }
    endShape();
  
    fill(0);
    stroke(0);
    for(int i=0; i<points.length; i++){
      ellipse(points[i].x, points[i].y,5,5);  
    }
  }
}
