/*
 *    geomerative example
 *
 *    fjenett 20080417
 *    fjenett 20081203 - updated to geomerative 19
 */

import geomerative.*;

RFont font;

void setup()
{
    size(400,400);
    smooth();
    
    RG.init(this);

    font = new RFont( "lucon.ttf", 100, RFont.CENTER);

    frameRate( 20 );
}

void draw()
{
    background(255);
    translate(width/2,height/2);

    RGroup grp = font.toGroup("Hello!");
    
    // die folgenden einstellungen beinflussen wieviele punkte die
    // polygone am ende bekommen werden.

    //RCommand.setSegmentStep(random(0,3));
    //RCommand.setSegmentator(RCommand.UNIFORMSTEP);
    
    RCommand.setSegmentLength(random(1,5));
    RCommand.setSegmentator(RCommand.UNIFORMLENGTH);
    
    //RCommand.setSegmentAngle(random(0,HALF_PI));
    //RCommand.setSegmentator(RCommand.ADAPTATIVE);

    grp = grp.toPolygonGroup();

    RPoint[] pnts = grp.getPoints();

    ellipse(pnts[0].x, pnts[0].y, 5, 5);
    for ( int i = 1; i < pnts.length; i++ )
    {
        line( pnts[i-1].x, pnts[i-1].y, pnts[i].x, pnts[i].y );
        ellipse(pnts[i].x, pnts[i].y, 5, 5);
    }
}

