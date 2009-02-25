/*
*    geomerative example
 *
 *    http://www.ricardmarxer.com/geomerative/
 *
 *    fjenett 20080417
 *    fjenett 20081203 - updated to geomerative 19
 */

import geomerative.*;


RFont font;
String inp = "";


void setup()
{
    size(800,250);
    frameRate( 20 );
    
    RG.init( this );

    font = new RFont( "lucon.ttf", 200, RFont.LEFT);
}


void draw()
{
    background(40);
    translate( 30, 180 );
    stroke( 255 );

    if ( !inp.equals( "" ) )
    {

        RGroup grp = font.toGroup( inp );
        
        RCommand.setSegmentLength(50);
        RCommand.setSegmentator(RCommand.UNIFORMLENGTH);

        float xCntr, yCntr;
        float lxC=0, lyC=0;
        float ang, lAng = 0;
        
        for ( int s = 0; s < grp.elements.length; s++ )
        {
            RPolygon rpoly = grp.elements[s].toPolygon();
            RMesh mesh = rpoly.toMesh();

            for ( int i = 0; i < mesh.strips.length; i++ )
            {
                RPoint[] pnts = mesh.strips[i].getPoints();

                for ( int ii = 2; ii < pnts.length; ii++ )  // "triangles", dreiecke durchlaufen
                {
                    // gravitations-zentrum des dreiecks errechnen
                    xCntr = (pnts[ii].x + pnts[ii-1].x + pnts[ii-2].x) / 3.0;
                    yCntr = (pnts[ii].y + pnts[ii-1].y + pnts[ii-2].y) / 3.0;
                    
                    pushMatrix();
                        translate( lxC, lyC );
                        // winkel in radianten ausrechnen
                        ang = atan2(yCntr-lyC, xCntr-lxC);
                    popMatrix();
                    
                    strokeWeight( abs(ang-lAng)*2 );
                    line( lxC, lyC, xCntr, yCntr );
                    
                    lxC = xCntr;
                    lyC = yCntr;
                    lAng = ang;
                }
            }
        }
    }
}


void keyPressed ()
{
    // enth�lt "inp" zeichen und ist es eine l�schen-taste?
    if (  keyCode == DELETE || keyCode == BACKSPACE )
    {
        if ( inp.length() > 0 )
            inp = inp.substring(0,inp.length()-1);    // um 1 char verk�rzen
    }
    // ist es eine taste mit einem zeichen?
    else if ( key != CODED )
    {
        inp = inp + key;    // char anh�ngen
    }
}
