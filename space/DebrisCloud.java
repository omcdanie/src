package space;

import controller.SpaceController;
import display.ConsoleItemImpl;
import display.ViewManager;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import utils.Point3D;
import utils.PolygonPlus;
import exceptions.DebrisCloudException;
import exceptions.SpaceControllerException;
import utils.ColorMaker;

/**
 * Defines debris cloud size and functionality. Implements Runnable.
 *
 * @author McDaniel
 */
public class DebrisCloud implements Runnable {

    private String identifier;
    private Point3D location;
    private PolygonPlus shape;
    private int duration;
    private Color color;
    private boolean visibility;
    private double sizeFactor;
    private final double angle = Math.toRadians(Math.random() * 360);

    public DebrisCloud(
            String id,
            Point3D loc,
            PolygonPlus shp,
            int dur,
            Color clr,
            boolean rdrVisible,
            double szFctr) throws DebrisCloudException, SpaceControllerException {
    }

    DebrisCloud(
            String shipId,
            Point3D point,
            String colorName,
            int cycle,
            boolean visible,
            double multiplier) throws DebrisCloudException, SpaceControllerException {

        shape = setShape();
        setIdentifier(shipId);
        setLocation(point);
        setColor(makeColor(colorName));
        setDuration(cycle);
        setRadarVisible(visible);
        setSizeFactor(multiplier);

        if (visibility == true) {
            SpaceController.addCloud(this);
        }
    }

    public String getInfoText() {
        String s = "";

        s += String.format("%40s%40s\n", "Location:          ", getLocation());
        s += String.format("%40s%40d\n", "Duration:          ", getDuration());
        s += String.format("%40s%40s\n", "Radar Visible:     ", isRadarVisible());
        return s;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Point3D getLocation() {
        return location;
    }

    public PolygonPlus getShape() {
        return shape;
    }

    public int getDuration() {
        return duration;
    }

    public double getAngle() {
        return angle;
    }

    public Color getColor() {
        return color;
    }

    public double getSizeFactor() {
        return sizeFactor;
    }

    private boolean isRadarVisible() {
        return visibility;
    }

    private PolygonPlus setShape() throws DebrisCloudException, SpaceControllerException {

        ArrayList<Point> sp = new ArrayList<>();
        sp.add(new Point(0, -13));
        sp.add(new Point(4, -15));
        sp.add(new Point(7, -14));
        sp.add(new Point(9, -9));
        sp.add(new Point(14, -7));
        sp.add(new Point(15, -4));
        sp.add(new Point(13, 0));
        sp.add(new Point(15, 4));
        sp.add(new Point(14, 7));
        sp.add(new Point(9, 9));
        sp.add(new Point(7, 14));
        sp.add(new Point(4, 15));
        sp.add(new Point(0, 13));
        sp.add(new Point(-4, 15));
        sp.add(new Point(-7, 14));
        sp.add(new Point(-9, 9));
        sp.add(new Point(-14, 7));
        sp.add(new Point(-15, 4));
        sp.add(new Point(-13, 0));
        sp.add(new Point(-15, -4));
        sp.add(new Point(-14, -7));
        sp.add(new Point(-9, -9));
        sp.add(new Point(-7, -14));
        sp.add(new Point(-4, -15));

        shape = new PolygonPlus();
        for (Point sp1 : sp) {
            shape.addPoint(sp1.x, sp1.y);
        }
        return shape;
    }

    private void setRadarVisible(boolean aRadarVisible) throws DebrisCloudException {

        visibility = aRadarVisible;
    }

    private void setIdentifier(String id) throws DebrisCloudException {
        if (id == null) {
            throw new DebrisCloudException("Debris clouds id must have a unique value.");
        }
        identifier = id;
    }

    private void setLocation(Point3D loc) throws DebrisCloudException {
        if (loc == null) {
            throw new DebrisCloudException("Debris cloud must have a location.");
        }
        Point3D newLocation = new Point3D(loc);
        location = newLocation;
    }

    private void setDuration(int durtn) throws DebrisCloudException {
        if (durtn <= 0) {
            throw new DebrisCloudException("Debris clouds must last longer than 0.");
        }
        duration = durtn;
    }

    private void setColor(Color clr) throws DebrisCloudException {
        if (clr == null) {
            throw new DebrisCloudException("Debris clouds must have a color.");
        }
        color = clr;
    }

    private void setSizeFactor(double szFctr) throws DebrisCloudException {
        if (szFctr <= 0) {
            throw new DebrisCloudException("Debris clouds size factor can't be negative or less than zero.");
        }
        sizeFactor = szFctr;
        shape.scale(sizeFactor);
    }

    public static Color makeColor(String colorName) throws DebrisCloudException {
        if (colorName == null) {
            throw new DebrisCloudException("Debris cloud has a null color.");
        }
        Color aColor = ColorMaker.makeColor(colorName);
        return aColor;
    }

    @Override
    public void run() {
        try {
            while (duration > 0) {

                ViewManager.getInstance().updateItem(
                        new ConsoleItemImpl(
                                getIdentifier(),
                                getLocation(),
                                getColor(),
                                Math.toRadians(Math.random() * 360),
                                getShape(),
                                getInfoText(),
                                false,
                                false));
                Thread.sleep(50);
                duration -= 50;
            }

            ViewManager.getInstance().removeItem(getIdentifier());

            if (isRadarVisible()) {
                SpaceController.removeCloud(getIdentifier());
            }

        } catch (SpaceControllerException | InterruptedException ex) {
            SpaceControllerException controller
                    = new SpaceControllerException(ex.toString());
            System.out.println(controller.getMessage());
        }
    }
}
