
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
import java.util.HashMap;
import java.util.HashSet;

import utils.Point3D;
import utils.PolygonPlus;
import utils.SoundUtility;

/**
 * A space structure with offensive capabilities.
 * 
 * @author Ola McDaniel
 */
public class FighterShip extends SpaceEntity {

    private int numberOfMissiles;
    private double radarRange;
    private final HashSet<String> targeted = new HashSet<>();
    private final HashMap<String, String> missilesToTargets = new HashMap<>();
    public double MISSILE_SPEED = 8.5;

    public FighterShip(
            String identifier,
            String colorName,
            Point3D location,
            Point3D destination,
            double maxStrength,
            double speed,
            int numOfMissiles,
            double rdrRange
    ) throws ShipException, SpaceControllerException {
        super(identifier, colorName, location, destination, maxStrength, speed, numOfMissiles, rdrRange);

        setShape();
        setNumberOfMissiles(numOfMissiles);
        setRadarRange(rdrRange);

        SpaceController.addShip(this);
       
    }
    
    public FighterShip() {

    }

    private void setNumberOfMissiles(int nbrOfMissiles) throws ShipException {
        if (nbrOfMissiles == 0) {
            throw new ShipException("Can't set the number of missiles. Reason, number of missiles is: " + nbrOfMissiles);
        }
        numberOfMissiles = nbrOfMissiles;
    }

    private void setRadarRange(double rdrRange) throws ShipException {
        if (rdrRange < 0.0) {
            throw new ShipException("Can't set radar range. Reason, radar range is " + rdrRange);
        }
        radarRange = rdrRange;
    }

    private void setShape() {
        shape = null;

        ArrayList<Point> sp = new ArrayList<>();
        sp.add(new Point(+0, -16));
        sp.add(new Point(+4, -8));
        sp.add(new Point(+10, -12));
        sp.add(new Point(+16, +8));
        sp.add(new Point(+8, +0));
        sp.add(new Point(+6, +2));
        sp.add(new Point(+0, +25));
        sp.add(new Point(-6, +2));
        sp.add(new Point(-8, +0));
        sp.add(new Point(-16, +8));
        sp.add(new Point(-10, -12));
        sp.add(new Point(-4, -8));

        shape = new PolygonPlus();
        for (Point sp1 : sp) {
            shape.addPoint(sp1.x, sp1.y);
        }
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
        s += String.format("%43s%37.1f\n", "Radar Range:        ", getRadarRange());
        s += String.format("%45s%35s\n", "Num Missiles:         ", getNumberOfMissiles());
        s += String.format("%45s%35s\n", "Missiles in Flight:   ", getMissilesToTargets());

        return s;
    }

    public int getNumberOfMissiles() {
        return numberOfMissiles;
    }

    public double getRadarRange() {
        return radarRange;
    }

    public HashSet<String> getTargeted() {
        return targeted;
    }

    public HashMap<String, String> getMissilesToTargets() {
        return missilesToTargets;
    }
    
    @Override
    public boolean isDestroyed() {
        boolean destroyed = false;

        try {
            if (getStrength() > getMaxStrength()) {
                throw new ShipException("Strength can't be larger than maxStrength in method, Fighter Ship not destroyed.");
            }
        } catch (ShipException ex) {
            ShipException shipException
                    = new ShipException(ex.toString());
            System.out.println(shipException.getMessage());
        }
        if (getStrength() <= 0) {
            destroyed = true;
        }
        return destroyed;
    }

    @Override
    public String getDisplayName() {
        if (getIdentifier().contains("FighterShip")) {
            return "FighterShip";
        }
        return "Not Available";
    }

    @Override
    public void reactToRadarLock(Point3D p) {
        // unused for now
    }

    public void updateMissiles() throws SpaceControllerException {

        if (getTargeted() == null || getMissilesToTargets() == null) {
            throw new SpaceControllerException("Unable to update missiles.");
        }

        if (getTargeted().isEmpty()) {
            return;
        }
        ArrayList<String> tmpList;
        synchronized (missilesToTargets) {
            tmpList = new ArrayList<>(getMissilesToTargets().keySet());
        }

        for (String s : tmpList) {

            if (SpaceController.isDestroyed(s) == true) {

                getTargeted().remove(getMissilesToTargets().get(s));
               
                getMissilesToTargets().remove(s);

            }
        }
    }

    private void scanForTargets() throws SpaceControllerException, SpacePortException, ShipException, DebrisCloudException {
        String missile;
        Missile missileToTarget;
        Point3D pT = null;
        if (getNumberOfMissiles() != 0) {
            ArrayList<String> targets
                    = SpaceController.performRadarSweep(
                            getIdentifier(),
                            getLocation(),
                            getColorName(),
                            getRadarRange());

            for (String tName : targets) {
                if (targets.isEmpty()) {
                    return;
                }

                if (!(getTargeted().contains(tName))) {

                    pT = SpaceController.getLocation(tName);
                }
                if (pT != null) {

                    if (getLocation().distance(pT) > 50.0) {

                        getTargeted().add(tName);
                        
                        //unique identifier for missiles
                        int missilesRemaining = getNumberOfMissiles();
                        missile = getColorName() + "_Missile_" + missilesRemaining;
                        missilesRemaining -= 1;

                        getMissilesToTargets().put(missile, tName);
                         
                        SoundUtility.getInstance().playSound("sounds" + File.separator + "Launch.wav");
                        
                        pT = SpaceController.getLocation(tName);
                        if (pT != null) {

                            missileToTarget
                                    = new Missile(
                                            getIdentifier(),
                                            getColorName(),
                                            getLocation(),
                                            pT,
                                            1.0,
                                            MISSILE_SPEED,
                                            tName,
                                            30.0,
                                            100,
                                            500.0,
                                            getRadarRange());
                            numberOfMissiles -= 1;
                            SpaceController.setRadarLock(tName, getLocation());
                           
                        }  
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        Missile missile = null;
        while (isDestroyed() != true) {
            try {
                
                move(1);
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

                updateMissiles();
                scanForTargets();
                
                Thread.sleep(50);

            } catch (SpaceControllerException |
                    InterruptedException |
                    SpacePortException |
                    ShipException |
                    DebrisCloudException ex) {
                System.out.println(ex.getMessage());
            }

        }
    }

    
}

    

