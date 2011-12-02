/*
*    geomerative example
 *
 *    http://www.ricardmarxer.com/geomerative/
 *
 *    fjenett 20080417
 *    fjenett 20081203 - updated to geomerative 19
 */

import geomerative.*;

RPolygon rpoly;

void setup()
{
    size(400,400);
    frameRate( 20 );
    
    RG.init( this );

    RShape shp1 = new RShape();

    shp1.addMoveTo( 134 , 64 );
    shp1.addLineTo( 184 , 64 );
    shp1.addLineTo( 199 , 63 );
    shp1.addBezierTo( 216 , 63 , 225 , 58 , 225 , 47);
    shp1.addBezierTo( 225 , 40 , 222 , 35 , 216 , 32);
    shp1.addBezierTo( 211 , 30 , 200 , 28 , 184 , 28);
    shp1.addLineTo( 134 , 28 );
    shp1.addLineTo( 134 , 64 );
    shp1.addClose();

    RShape shp2 = new RShape();

    shp2.addMoveTo( 0 , 0  );
    shp2.addLineTo( 185 , 0  );
    shp2.addLineTo( 213 , 0 );
    shp2.addLineTo( 249 , 2 );
    shp2.addBezierTo( 267 , 4 , 281 , 9 , 292 , 17);
    shp2.addBezierTo(  303 , 24 , 309 , 33 , 309 , 43);
    shp2.addBezierTo(  309 , 63 , 287 , 75 , 243 , 80);
    shp2.addBezierTo(  258 , 84 , 269 , 89 , 277 , 96);
    shp2.addBezierTo(  285 , 103 , 293 , 114 , 300 , 130);
    shp2.addLineTo( 300 , 130  );
    shp2.addLineTo( 337 , 152 );
    shp2.addLineTo( 229 , 152 );
    shp2.addBezierTo(  220 , 130 , 211 , 114 , 204 , 104);
    shp2.addBezierTo(  199 , 97 , 189 , 94 , 172 , 94);
    shp2.addLineTo( 172 , 94  );
    shp2.addLineTo( 135 , 94  );
    shp2.addLineTo( 135 , 117  );
    shp2.addLineTo( 191 , 152  );
    shp2.addLineTo( 0 , 152  );
    shp2.addLineTo( 56 , 117  );
    shp2.addLineTo( 56 , 35 );
    shp2.addLineTo( 0 , 0 );
    shp2.addClose();

    rpoly = (RPolygon)(shp2.toPolygon().diff(shp1.toPolygon()));
}

void draw()
{
    background(40);
    translate( 35, 100 );
    noStroke();

    RMesh mesh = rpoly.toMesh();

    beginShape( TRIANGLE_STRIP );

    for ( int i = 0; i < mesh.strips.length; i++ )
    {
        RPoint[] pnts = mesh.strips[i].getPoints();

        float xCntr, yCntr;

        for ( int ii = 2; ii < pnts.length; ii++ )  // "triangles", dreiecke durchlaufen
        {
            // gravitations-zentrum des dreiecks errechnen
            xCntr = (pnts[ii].x + pnts[ii-1].x + pnts[ii-2].x) / 3.0;
            yCntr = (pnts[ii].y + pnts[ii-1].y + pnts[ii-2].y) / 3.0;

            vertex( xCntr, yCntr );
        }
    }

    endShape(CLOSE);
}
