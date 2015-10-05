package utils;

public class Point3D {

    private double x;
    private double y;
    private double z;
    
    public Point3D(double xIn, double yIn, double zIn) {
        setCoordinates(xIn, yIn, zIn);
    }

    public Point3D(Point3D aPoint) {
        setCoordinates(aPoint.getX(), aPoint.getY(), aPoint.getZ());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    private void setCoordinates(double xIn, double yIn, double zIn) {
        x = xIn;
        y = yIn;
        z = zIn;
    }

    @Override
    public String toString() {
        return String.format("[%.1f, %.1f, %.1f]", x, y, z);
    }

    public double distance(double xIn, double yIn, double zIn) {
        double xD = xIn - getX();
        double yD = yIn - getY();
        double zD = zIn - getZ();

        return Math.sqrt(xD * xD + yD * yD + zD * zD);
    }

    public double distance(Point3D aPoint) {
        return distance(aPoint.getX(), aPoint.getY(), aPoint.getZ());
    }

    public boolean equals(Point3D aPoint3D) {
        return (getX() == aPoint3D.getX())
                && (getY() == aPoint3D.getY())
                && (getZ() == aPoint3D.getZ());
    }
}
