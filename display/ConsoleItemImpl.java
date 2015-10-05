package display;

import utils.PolygonPlus;
import utils.Point3D;
import java.awt.Color;

public class ConsoleItemImpl implements ConsoleItem {

    private final String id;
    private Point3D location;
    public final Color color;
    public final double angle;
    private final PolygonPlus polygon;
    private final String infoText;
    private final boolean destroyed;
    private boolean damaged = false;

    public ConsoleItemImpl(String idIn, Point3D loc, Color c, double ang, PolygonPlus poly, String txt, boolean des, boolean dam) {
        id = idIn;
        location = loc;
        color = c;
        angle = ang;
        polygon = poly;
        infoText = txt;
        destroyed = des;
        damaged = dam;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Point3D getLocation() {
        return location;
    }

    @Override
    public void setLocation(Point3D p) {
        location = p;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public PolygonPlus getPolygon(double zSize) {
        PolygonPlus pp = new PolygonPlus(polygon);
        pp.scale(location.getZ() / zSize + 0.2);
        pp.translate((int) location.getX(), (int) location.getY());
        pp.rotate(angle);
        return pp;
    }

    @Override
    public String getInfoText() {
        return infoText;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getInfoText()).append("\n");

        return sb.toString();

    }

    @Override
    public boolean isDamaged() {
        return damaged;
    }
}
