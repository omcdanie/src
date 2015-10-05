
package space;

import controller.SpaceController;
import display.ViewManager;
import exceptions.DebrisCloudException;
import exceptions.ShipException;
import exceptions.SpaceControllerException;
import exceptions.SpacePortException;
import java.awt.Color;
import java.io.File;
import utils.ColorMaker;
import utils.Point3D;
import utils.PolygonPlus;
import utils.SoundUtility;

/**
 * Defines basic functionality required of CargoShips,
 * FighterShips and Missiles.
 * 
 * @see space.CargoShip
 * @see space.FighterShip
 * @see space.Missile
 * @see space.SpaceStructure
 * 
 * @author Ola McDaniel
 */
public abstract class SpaceEntity implements SpaceStructure {

    protected double maxStrength;
    protected double strength;
    protected Point3D destination;
    protected double speed;
    protected int debrisClouds;
    protected String identifier;
    protected Point3D location;
    protected String colorName;
    protected double angle;
    protected PolygonPlus shape;
    protected Color color;

    public SpaceEntity(
            String id,
            String clrName,
            Color clr,
            Point3D loc,
            Point3D dest,
            double spd,
            double mStrength,
            double mStrngth
    ) throws ShipException, SpaceControllerException {
        setIdentifier(id);
        setColorName(clrName);
        setColor(makeColor(clrName));
        setLocation(loc);
        setDestination(dest);
        setSpeed(spd);
        setMaxStrength(mStrength);
        setStrength(mStrength);
        
    }
    
    public SpaceEntity(
            String identifier,
            String colorName,
            Point3D location,
            Point3D destination,
            double maxStrength,
            double speed,
            String trgt,
            double detRange,
            int flightDtn,
            double maxDam,
            double rdrRng
    ) throws SpacePortException, ShipException, SpaceControllerException {
        setIdentifier(identifier);
        setColorName(colorName);
        setLocation(location);
        setDestination(destination);
        setMaxStrength(maxStrength);
        setSpeed(speed);
        
    }
        
    
    SpaceEntity(
            String id,
            Point3D loc,
            Color clr,
            double ang,
            PolygonPlus shap,
            String info,
            boolean dest,
            boolean dam
    ) throws ShipException, SpaceControllerException {
 
        setAngle(ang);
    }
    
    //fighters
    public SpaceEntity(
            String identifier,
            String colorName,
            Point3D location,
            Point3D destination,
            double maxStrength,
            double speed,
            int numOfMissiles,
            double rdrRange
    ) throws ShipException, SpaceControllerException {
        setIdentifier(identifier);
        setColorName(colorName);
        setColor(makeColor(colorName));
        setLocation(location);
        setDestination(destination);
        setMaxStrength(maxStrength);
        setSpeed(speed);
        
    }
    
    SpaceEntity() {
        
    }

    @Override
    public double getMaxStrength() {
        return maxStrength;
    }

    @Override
    public double getStrength() {
        return strength;
    }

    protected Point3D getDestination() {
        return destination;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    public int getDebrisClouds() {
        return debrisClouds;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Point3D getLocation() {
        return location;
    }

    public String getColorName() {
        return colorName;
    }

    private void setColorName(String colorName) {
        this.colorName = colorName;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    @Override
    public PolygonPlus getShape() {
        return shape;
    }

    @Override
    public Color getColor() {
        return color;
    }

    private void setColor(Color clr) throws ShipException {
        if (clr == null) {
            throw new ShipException("Entity's team color is invalid. " + clr);
        }
        color = clr;
    }

    protected static Color makeColor(String colorName) throws ShipException {
        if (colorName == null) {
            throw new ShipException("Entity has a null color. " + colorName);
        }
        Color aColor = ColorMaker.makeColor(colorName);
        return aColor;
    }

    private void setMaxStrength(double mxStrength) throws ShipException {
        if (mxStrength <= 0) {
            throw new ShipException("Strength must be greater than 0. " + mxStrength);
        }
        maxStrength = mxStrength;
    }

    private void setStrength(double str) throws ShipException {
        if (str > maxStrength) {
            throw new ShipException("Strength can't be larger than Maximum Strength. " + str);
        }
        strength = str;
    }

    private void setIdentifier(String id) throws ShipException {
        if (id == null) {
            throw new ShipException("Must have a unique id. " + id);
        }
        identifier = id;
    }

    protected void setLocation(Point3D loc) throws ShipException {
        if (loc == null) {
            throw new ShipException("Location is invalid. " + loc);
        }
        Point3D newLocation = new Point3D(loc);
        location = newLocation;
    }

    public void setDestination(Point3D des) throws ShipException {
        if (des == null) {
            throw new ShipException("Destination is invalid. " + des);
        }
        Point3D newDestination = new Point3D(des);
        destination = newDestination;
    }

    private void setAngle(double ang) throws ShipException {
        if (ang > 180.0 || ang < -179.0) {
            throw new ShipException("The angle is invalid. " + ang);
        }
        angle = ang;
    }

    private void setSpeed(double spd) throws ShipException {
        if (spd < 0) {
            throw new ShipException("Speed must be greater than 0. " + spd);
        }
        speed = spd;
    }
    
    @Override
    public boolean isSpacePort() {
        boolean port = false;
        if (identifier.contains("SpacePort")) {
            port = true;
        }
        return port;
    }
    
    @Override
    public boolean isDamaged() {
        boolean damage = false;
        try {
            if (strength > maxStrength) {
                throw new ShipException("Strength can't be larger than maxStrength, Cargo Ship not damaged. " + maxStrength + " < " + strength);
            }
        } catch (ShipException ex) {
            ShipException shipException
                    = new ShipException(ex.toString());
            System.out.println(shipException.getMessage());
        }
        if (strength < maxStrength) {
            damage = true;
        }
        return damage;
    }
    
    @Override
    public void applyDamage(double dmg) throws SpaceControllerException, DebrisCloudException {
        strength -= dmg;
        
        if (strength <= 0) {
            SpaceController.removeShip(getIdentifier());
            ViewManager.getInstance().removeItem(getIdentifier());
            SpaceController.processDetonation(getIdentifier(), getLocation(), 50, 200);

            DebrisCloud dci = new DebrisCloud("Ship Debris Cloud from " + getIdentifier(), getLocation(), getColorName(), 4000, false, 1.5);
            new Thread(dci).start();
            SoundUtility.getInstance().playSound("sounds" + File.separator + "Blast.wav");
        }
    }
    
    public void move(int cycles) throws SpaceControllerException {

        Point3D locOld = new Point3D(location);

        double distanceTravelled = speed * cycles;
        double distanceToDestination = location.distance(destination);

        if (distanceToDestination != 0.0) {

            if (distanceTravelled >= distanceToDestination) {
                location = destination;
                destination = SpaceController.makePoint();
            } else {
                double delta = distanceTravelled / distanceToDestination;
                double newXCoord = location.getX() + (destination.getX() - location.getX()) * delta;
                double newYCoord = location.getY() + (destination.getY() - location.getY()) * delta;
                double newZCoord = location.getZ() + (destination.getZ() - location.getZ()) * delta;

                Point3D newPoint = new Point3D(newXCoord, newYCoord, newZCoord);
                double nx = newPoint.getX() - locOld.getX();
                double ny = newPoint.getY() - locOld.getY();
                angle = Math.atan2(ny, nx) + (Math.PI / 2.0);
                location = newPoint;
            }
        }
    }

}
