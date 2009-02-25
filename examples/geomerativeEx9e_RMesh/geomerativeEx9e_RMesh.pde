/*
*    geomerative example
 *
 *    http://www.ricardmarxer.com/geomerative/
 *
 *    fjenett 20080419
 *    fjenett 20081203 - updated to geomerative 19
 */

import geomerative.*;


RFont font;
String inp = "";

float[] xx, yy, aa;


void setup()
{
    size(1000,250);
    frameRate( 20 );
    
    RG.init( this );

    font = new RFont( "lucon.ttf", 200, RFont.LEFT);
}


void draw()
{
    background(40);
    translate( 30, 180 );
    noFill();

    if ( !inp.equals( "" ) )
    {
        stroke( 80 );

        RGroup grp = font.toGroup( inp );

        RCommand.setSegmentLength(20);
        RCommand.setSegmentator(RCommand.UNIFORMLENGTH);

        xx = new float[]{
            0        };
        yy = new float[]{
            0        };
        aa = new float[0];

        float xCntr, yCntr;
        float lxC=0, lyC=0;
        float ang;

        for ( int s = 0; s < grp.elements.length; s++ )
        {
            RPolygon rpoly = grp.elements[s].toPolygon();
            RMesh mesh = rpoly.toMesh();

            for ( int i = 0; i < mesh.strips.length; i++ )
            {
                RPoint[] pnts = mesh.strips[i].getPoints();

                for ( int ii = 2; ii < pnts.length; ii++ )  // "triangles", dreiecke durchlaufen
                {
                    // gravitations-zentrum (mittelpunkt) des dreiecks errechnen
                    xCntr = (pnts[ii].x + pnts[ii-1].x + pnts[ii-2].x) / 3.0;
                    yCntr = (pnts[ii].y + pnts[ii-1].y + pnts[ii-2].y) / 3.0;

                    pushMatrix();
                    translate( lxC, lyC );
                    // winkel in radianten ausrechnen
                    ang = atan2(yCntr-lyC, xCntr-lxC) + PI;
                    popMatrix();

                    // linie zwischen den mittelpunkten der dreiecke
                    //line(lxC,lyC,xCntr,yCntr);

                    lxC = xCntr;
                    lyC = yCntr;

                    xx = append( xx, xCntr );
                    yy = append( yy, yCntr );
                    aa = append( aa, ang );
                }
            }
        }


        float at = 0;
        float thi = 0;
        float dl = 0;

        for ( int i = 1; i < xx.length; i++ )
        {
            float d  = dist( xx[i-1], yy[i-1], xx[i], yy[i] );

            stroke( 255 );
            
            float ths = (d - dl)/100;
            
            float xtl = xx[i-1], ytl = yy[i-1], xt, yt;
            
            for ( int t = 0; t <= 100; t+=10 )
            {
                thi += ths;
                xt = bezierPoint( xx[i-1], xx[i-1]+sin(at)*d, xx[i]-sin(aa[i-1])*d, xx[i], (t/100.0) );
                yt = bezierPoint( yy[i-1], yy[i-1]+cos(at)*d, yy[i]-cos(aa[i-1])*d, yy[i], (t/100.0) );
                
                strokeWeight( thi );
                line( xtl, ytl, xt, yt );
                
                xtl = xt;
                ytl = yt;
            }
            
            // die bezier "anfasser"
            /*stroke(0,0,0);
            line( xx[i-1], yy[i-1], xx[i-1]+sin(at)*d, yy[i-1]+cos(at)*d);
            line( xx[i]-sin(aa[i-1])*d, yy[i]-cos(aa[i-1])*d, xx[i], yy[i]);*/

            at = aa[i-1];
            dl = d;
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
