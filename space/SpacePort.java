package space;

import controller.SpaceController;
import display.ConsoleItemImpl;
import display.ViewManager;
import exceptions.*;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import utils.Point3D;
import utils.PolygonPlus;
import utils.SoundUtility;

/**
 * Creates additional functionality for Space Ports not in Cargo Ship.
 * 
 * @see space.CargoShip
 * 
 * @author Ola McDaniel
 */
public class SpacePort extends CargoShip implements Runnable {

    private final double angle = 0.0;
    private int railGunShots;
    private int shotInterval;
    private DebrisCloud dc;

    public SpacePort(
            String id,
            String colorName,
            Point3D loc,
            Point3D des,
            double mStr,
            double spd,
            int numShots
    ) throws SpacePortException, ShipException, SpaceControllerException {
        super(id, colorName, loc, des, mStr, spd, 1);
        setupShape();
        setShots(numShots);

    }

    SpacePort() {

    }

    public int getShots() {
        return railGunShots;
    }

    private void setShots(int rlGnShts) throws SpacePortException {
        if (rlGnShts < 0) {
            throw new SpacePortException("Rail Gun Shots are less than zero." + rlGnShts);
        }
        railGunShots = rlGnShts;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    private void setupShape() {
        ArrayList<Point> sp = new ArrayList<>();
        sp.add(new Point(0, -20));
        sp.add(new Point(6, -6));
        sp.add(new Point(20, 0));
        sp.add(new Point(6, 6));
        sp.add(new Point(0, 20));
        sp.add(new Point(-6, 6));
        sp.add(new Point(-20, 0));
        sp.add(new Point(-6, -6));
        // Now create the PolygonPlus – data member is “shape” in this example
        shape = new PolygonPlus();
        for (Point sp1 : sp) {
            shape.addPoint(sp1.x, sp1.y);
        }
    }

    @Override
    public void applyDamage(double damage) throws SpaceControllerException, DebrisCloudException {
        strength -= damage;
        if (strength <= 0) {
            SpaceController.removeShip(getIdentifier());
            ViewManager.getInstance().removeItem(getIdentifier());
            SpaceController.processDetonation(getIdentifier(), getLocation(), 100, 400);

            dc = new DebrisCloud("Ship's Debris Cloud from " + getIdentifier(), getLocation(), getColorName(), 4000, false, 3.0);
            new Thread(dc).start();
            SoundUtility.getInstance().playSound("sounds" + File.separator + "Blast.wav");
        }
    }

    @Override
    public void reactToRadarLock(Point3D loc) throws SpaceControllerException, DebrisCloudException {
        if (railGunShots == 0) {
            return;
        }
        if (shotInterval > 0) {
            return;
        }
        SoundUtility.getInstance().playSound("sounds" + File.separator + "Rail.wav");

        Point3D newP;
        double newX, newY, newZ;
        for (int i = 0; i < 10; i++) {

            newX = loc.getX() * (0.975 + (Math.random() * 0.05));
            newY = loc.getY() * (0.975 + (Math.random() * 0.05));
            newZ = loc.getZ() * (0.975 + (Math.random() * 0.05));
            newP = new Point3D(newX, newY, newZ);

            SpaceController.processDetonation(getIdentifier() + railGunShots, newP, 50, 30);
            dc = new DebrisCloud("Rail Shot " + railGunShots + " from " + getIdentifier(), newP, getColorName(), 3000, true, .25);
            new Thread(dc).start();
            railGunShots -= 1;
        }
        shotInterval = 50;
    }

    @Override
    public void resetStrength() {
        strength = maxStrength;
    }

    @Override
    public boolean isDestroyed() {
        boolean destroyed = false;
        try {
            if (strength > maxStrength) {
                throw new SpacePortException("Strength can't be larger than maxStrength in method, Space Port not destroyed.");
            }
        } catch (SpacePortException ex) {
            SpacePortException portException
                    = new SpacePortException(ex.toString());
            System.out.println(portException.getMessage());
        }
        if (strength <= 0) {
            destroyed = true;
        }
        return destroyed;
    }

    @Override
    public String getDisplayName() {
        if (getIdentifier().contains("SpacePort")) {
            return "Space Port";
        }
        return "Not Available";
    }

    @Override
    public boolean isDamaged() {
        boolean damage = false;
        try {
            if (strength > maxStrength) {
                throw new SpacePortException("Strength can't be larger than maxStrength, Space Port not damaged. " + maxStrength + " < " + strength);
            }
        } catch (SpacePortException ex) {
            SpacePortException portException
                    = new SpacePortException(ex.toString());
            System.out.println(portException.getMessage());
        }
        if (strength < maxStrength) {
            damage = true;
        }
        return damage;
    }

    @Override
    public String getInfoText() {
        String s = "";

        s += String.format("%40s%40s\n\n", "Color:           ", getColorName());
        s += String.format("%40s%40s\n", "Location:        ", getLocation());
        s += String.format("%40s%40s\n", "Destination:     ", getDestination());
        s += String.format("%40s%40.3f\n", "Speed:           ", getSpeed());
        s += String.format("%40s%40.1f\n\n", "Angle:           ", getAngle());
        s += String.format("%40s%40.1f\n", "Strength:        ", getStrength());
        s += String.format("%40s%40.1f\n", "Max Strength:    ", getMaxStrength());
        s += String.format("%40s%40s\n", "Damaged:         ", isDamaged());
        s += String.format("%40s%40d\n", "Shots:           ", getShots());

        return s;
    }

    @Override
    public void run() {
        while (isDestroyed() != true) {

            try {
                if (shotInterval > 0) {
                    shotInterval -= 1;
                }
                move(1);
                if (getStrength() < getMaxStrength()) {
                    strength += 0.2;
                }
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
            } catch (SpaceControllerException | InterruptedException ex) {
                SpaceControllerException controller
                        = new SpaceControllerException(ex.toString());
                System.out.println(controller.getMessage());
            }
        }
    }
}
