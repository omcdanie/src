package utils;

import java.awt.Polygon;

public class PolygonPlus extends Polygon {

    public PolygonPlus() {
        super();
    }

    public PolygonPlus(PolygonPlus pIn) {
        this(pIn.xpoints, pIn.ypoints, pIn.npoints);
    }

    public PolygonPlus(int[] xpoints, int[] ypoints, int npoints) {
        super(xpoints, ypoints, npoints);
    }

    public void rotate(double d) {

        double degrees = d;
        int xCenter = 0, yCenter = 0;
        for (int i = 0; i < this.npoints; i++) {
            xCenter += this.xpoints[i];
            yCenter += this.ypoints[i];
        }
        xCenter /= npoints;//center of X's
        yCenter /= npoints;//center of Y's

        for (int i = 0; i < npoints; i++) {
            int temp = (int) (xCenter + (xpoints[i] - xCenter) * Math.cos(degrees) - (ypoints[i] - yCenter) * Math.sin(degrees));
            ypoints[i] = (int) (yCenter + (xpoints[i] - xCenter) * Math.sin(degrees) + (ypoints[i] - yCenter) * Math.cos(degrees));
            xpoints[i] = temp;
        }
    }

    public double getCenterX() {
        int xCenter = 0;
        for (int i = 0; i < this.npoints; i++) {
            xCenter += this.xpoints[i];
        }
        xCenter /= npoints;
        return xCenter;
    }

    public double getCenterY() {
        int yCenter = 0;
        for (int i = 0; i < this.npoints; i++) {
            yCenter += this.ypoints[i];
        }
        yCenter /= npoints;
        return yCenter;
    }

    public void scale(double factor) {
        for (int i = 0; i < this.npoints; i++) {
            xpoints[i] *= factor;
            ypoints[i] *= factor;
        }
    }
}
