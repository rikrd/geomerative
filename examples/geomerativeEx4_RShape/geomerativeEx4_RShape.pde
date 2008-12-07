/*
 *    geomerative example
 *    http://www.ricardmarxer.com/geomerative/
 *
 *    fjenett 20080417
 *    fjenett 20081203 - updated to geomerative 19
 */

import geomerative.*;

RShape shp;

void setup()
{
    size(400,400);
    smooth();
    
    RG.init( this );
    
    shp = new RShape();
    
    shp.addMoveTo( 0 , 141 );        // zeichenbefehle, vergleiche beginShape, vertex, bezierVertex
    shp.addLineTo( 2 , 133 );
    shp.addLineTo( 34 , 133 );
    shp.addLineTo( 60 , 9 );
    shp.addLineTo( 38 , 9 );
    shp.addBezierTo( 20 , 9 , 16 , 12 , 12 , 30 );
    shp.addLineTo( 9 , 46 );
    shp.addLineTo( 0 , 46 );
    shp.addLineTo( 10 , 0 );
    shp.addLineTo( 123 , 0 );
    shp.addLineTo( 113 , 46 );
    shp.addLineTo( 104 , 46 );
    shp.addLineTo( 107 , 30 );
    shp.addBezierTo( 111 , 12 , 109 , 9 , 91 , 9 );
    shp.addLineTo( 69 , 9 );
    shp.addLineTo( 43 , 133 );
    shp.addLineTo( 75 , 133 );
    shp.addLineTo( 73 , 141 );
    shp.addLineTo( 0 , 141 );
    shp.addClose();
}

void draw()
{
    background(255);
    translate(140,120);
    
    stroke( 120 );
    strokeWeight( 3 );
    fill( 220 );

    shp.draw();
}

