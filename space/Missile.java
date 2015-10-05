package space;

import controller.SpaceController;
import display.ConsoleItemImpl;
import display.ViewManager;
import exceptions.DebrisCloudException;
import exceptions.ShipException;
import exceptions.SpaceControllerException;
import exceptions.SpacePortException;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import utils.Point3D;
import utils.PolygonPlus;
import utils.SoundUtility;

/**
 * Provides Fighter Ships with offensive capabilities.
 * Extends Cargo Ship.
 *
 * @author Ola McDaniel
 */
public class Missile extends SpaceEntity implements Runnable {

    private String target;
    private double detonationRange;
    private int flightDuration;
    private double maxDamage;
    private double radarRange;
    
    //private DebrisCloud debrisCloud;

    public Missile(
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
        super(identifier,colorName,location,destination,maxStrength,speed,trgt,detRange,flightDtn,maxDam,rdrRng);
        
        setShape();
        setTarget(trgt);
        setDetonationRange(detRange);
        setFlightDuration(flightDtn);
        setMaxDamage(maxDam);
        setRadarRange(rdrRng);
        
        SpaceController.addShip(this);
    }

    public Missile() {

    }

    @Override
    public String getInfoText() {
        String s = "";

        s += String.format("%40s%40s\n\n", "Color:           ", getColorName());
        s += String.format("%40s%40s\n", "Location:        ", getLocation());
        s += String.format("%40s%40s\n", "Destination:     ", getDestination());
        s += String.format("%40s%40.1f\n", "Speed:           ", getSpeed());
        s += String.format("%40s%40.1f\n\n", "Angle:           ", getAngle());
        s += String.format("%40s%40.1f\n", "Strength:        ", getStrength());
        s += String.format("%40s%40.1f\n", "Max Strength:    ", getMaxStrength());
        s += String.format("%40s%40s\n\n", "Damaged:         ", isDamaged());
        s += String.format("%40s%40s\n", "Target:          ", getTarget());
        s += String.format("%40s%40s\n", "Duration:        ", getFlightDuration());
        s += String.format("%40s%40s\n", "Detonation Range:", getDetonationRange());
        s += String.format("%40s%40s\n", "Radar Range:     ", getRadarRange());
        s += String.format("%40s%40s\n", "Max Damage:      ", getMaxDamage());

        return s;
    }

    public String getTarget() {
        return target;
    }

    public double getDetonationRange() {
        return detonationRange;
    }

    public int getFlightDuration() {
        return flightDuration;
    }

    public double getMaxDamage() {
        return maxDamage;
    }

    public double getRadarRange() {
        return radarRange;
    }

    private void setTarget(String trgt) throws ShipException {
        if (trgt == null) {
            throw new ShipException("Missile is not able to set target.  Reason: target is " + trgt);
        }
        target = trgt;
    }

    private void setDetonationRange(double detRange) throws ShipException {
        if (detRange < 0.0) {
            throw new ShipException("Missile is not able to set detonation range.  Reason: detonation range is " + detRange);
        }
        detonationRange = detRange;
    }

    private void setFlightDuration(int flightDtn) throws ShipException {
        if (flightDtn < 0) {
            throw new ShipException("Missile is not able to set flight duration.  Reason: flight duration is " + flightDtn);
        }
        flightDuration = flightDtn;
    }

    private void setMaxDamage(double maxDam) throws ShipException {
        if (maxDam <= 0.0) {
            throw new ShipException("Missile is not able to set maximum damage.  Reason: max damage is " + maxDam);
        }
        maxDamage = maxDam;
    }

    private void setRadarRange(double rdrRng) throws ShipException {
        if (rdrRng < 0.0) {
            throw new ShipException("Missile is not able to set radar range.  Reason: radar range is " + rdrRng);
        }
        radarRange = rdrRng;
    }

    private void detonate() throws SpaceControllerException, DebrisCloudException {
        strength = 0;
        SpaceController.removeShip(getIdentifier());
        ViewManager.getInstance().removeItem(getIdentifier());
        DebrisCloud debrisCloud = new DebrisCloud(getIdentifier(), getLocation(), getColorName(), 2000, false, 0.5);
        SoundUtility.getInstance().playSound("sounds" + File.separator + "MissileBlast.wav");
        SpaceController.processDetonation(getIdentifier(), getLocation(), (int) getDetonationRange(), (int) getMaxDamage());
    }

    @Override
    public void reactToRadarLock(Point3D p) {
        //unused for now
    }

    @Override
    public void applyDamage(double dmg) throws SpaceControllerException, DebrisCloudException {
        if (dmg <= 0.0) {
            throw new SpaceControllerException("Missile is unable to apply damge.  Reason damage applied is: " + dmg);
        }
        strength -= dmg;
        if (getStrength() <= 0) {
            detonate();
        }
    }

    @Override
    public String getDisplayName() {
        if (getIdentifier().contains("Missile")) {
            return "Missile";
        }
        return "Not Available";
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
    public boolean isDestroyed() {
        boolean destroyed = false;

        try {
            if (getStrength() > getMaxStrength()) {
                throw new ShipException("Strength can't be larger than maxStrength in method, Missile not destroyed.");
            }
        } catch (ShipException ex) {

            System.out.println(ex.getMessage());
        }
        if (getStrength() <= 0) {
            destroyed = true;
        }
        return destroyed;
    }

    private void setShape() {
        shape = null;
        
        ArrayList<Point> sp = new ArrayList<>();
        sp.add(new Point(0, -10));
        sp.add(new Point(2, -6));
        sp.add(new Point(2, 3));
        sp.add(new Point(4, 9));
        sp.add(new Point(2, 9));
        sp.add(new Point(2, 7));
        sp.add(new Point(0, 8));
        sp.add(new Point(-2, 7));
        sp.add(new Point(-2, 9));
        sp.add(new Point(-4, 9));
        sp.add(new Point(-2, 3));
        sp.add(new Point(-2, -6));// Now create the PolygonPlus – data member is

        shape = new PolygonPlus();
        for (Point sp1 : sp) {
            shape.addPoint(sp1.x, sp1.y);
        }
    }

    public void move(int cycles) throws SpaceControllerException {

        flightDuration -= 1;
        if (getFlightDuration() == 0) {
            try {
                detonate();
                return;
            } catch (DebrisCloudException ex) {
                System.out.println(ex.getMessage());
            }

        }
        Point3D p = getLocation();
        if (p == null) {
            try {
                detonate();
                //exit out of the move method
            } catch (DebrisCloudException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            try {
                // needs testing
                setDestination(p);
            } catch (ShipException ex) {
                System.out.println(ex.getMessage());
            }
        }

        Point3D locOld = new Point3D(getLocation());

        double distanceTravelled = getSpeed() * cycles;

        double distanceToDestination = getLocation().distance(getDestination());

        if (distanceTravelled >= distanceToDestination) {
            location = destination;
            try {
                detonate();
            } catch (DebrisCloudException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            double delta = distanceTravelled / distanceToDestination;
            double newXCoord = getLocation().getX() + (getDestination().getX() - getLocation().getX()) * delta;
            double newYCoord = getLocation().getY() + (getDestination().getY() - getLocation().getY()) * delta;
            double newZCoord = getLocation().getZ() + (getDestination().getZ() - getLocation().getZ()) * delta;

            Point3D newPoint = new Point3D(newXCoord, newYCoord, newZCoord);
            double nx = newPoint.getX() - locOld.getX();
            double ny = newPoint.getY() - locOld.getY();
            angle = Math.atan2(ny, nx) + (Math.PI / 2.0);
            location = newPoint;
        }

    }

    @Override
    public void run() {
        
        String newTarget;
        Point3D p;
        while (isDestroyed() != true) {

            try {
                newTarget = SpaceController.reaquireRadarLock(
                        getIdentifier(),
                        getLocation(),
                        getTarget(),
                        getColorName(),
                        getRadarRange());
                p = SpaceController.getLocation(getTarget());
                if (p != null) {
                    setTarget(newTarget);
                    setDestination(p);
                    
                }
                move(1);
                if (isDestroyed() != true) {
                    
                    ViewManager.getInstance().updateItem(
                            new ConsoleItemImpl(
                                    getIdentifier(),
                                    getLocation(),
                                    getColor(),
                                    getAngle(),
                                    getShape(),
                                    getInfoText(),
                                    isDestroyed(),
                                    isDamaged()));
                    
                    Thread.sleep(50);

                }
            } catch (SpaceControllerException | InterruptedException | ShipException ex) {

                System.out.println(ex.getMessage());
            }
        }
    }

}
