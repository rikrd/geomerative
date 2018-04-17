import geomerative.*;

// Declare the objects we are going to use, so that they are accesible from setup() and from draw()
RFont f;
RShape grp;

void setup(){
  // Initilaize the sketch
  size(600,400);
  frameRate(24);

  // VERY IMPORTANT: Allways initialize the library in the setup
  RG.init(this);

  // Choice of colors
  background(255);
  fill(255,102,0);
  stroke(0);
  
  //  Load the font file we want to use (the file must be in the data folder in the sketch floder), with the size 60 and the alignment CENTER
  grp = RG.getText("Hello world!", "FreeSans.ttf", 72, CENTER);

  // Enable smoothing
  smooth();
}

void draw(){
  // Clean frame
  background(255);
  
  // Set the origin to draw in the middle of the sketch
  translate(width/2, height/2);
  
  // Transform at each frame the first letter with a PI/20 radians 
  // rotation around the center of the first letter's center
  grp.children[0].rotate(PI/20, grp.children[0].getCenter());
  
  // Draw the group of shapes representing "Hola mundo!" on the PGraphics canvas g (which is the default canvas of the sketch)
  grp.draw();
}
