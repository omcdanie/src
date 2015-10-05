package space;

import controller.SpaceController;
import display.ConsoleItemImpl;
import display.ViewManager;
import exceptions.DebrisCloudException;
import exceptions.ShipException;
import exceptions.SpaceControllerException;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import utils.Point3D;
import utils.PolygonPlus;
import utils.SoundUtility;

/**
 * Defines cargo cargoShip functionality and size. Implements Runnable.
 * 
 * @see space.SpaceStructure
 *
 * @author omcdaniel1
 */
public class CargoShip extends SpaceEntity {

   
    private DebrisCloud dci;
    //new 
    private SpacePort spacePort;
    private boolean atPort;
    private String currentPort;
    private int waitCount;
    //end new
    

    public CargoShip(
            String id,
            String clrName,
            Point3D loc,
            Point3D dest,
            double mStrength,
            double spd,
            int dbrsClds
    ) throws ShipException, SpaceControllerException {
    super(id,clrName,makeColor(clrName),loc,dest,spd,mStrength,mStrength);
        
        setupShape();
        setDebrisClouds(dbrsClds);

        SpaceController.addShip(this);
    }

    CargoShip(
            String id,
            Point3D loc,
            Color clr,
            double ang,
            PolygonPlus shap,
            String info,
            boolean dest,
            boolean dam
    ) throws ShipException, SpaceControllerException {
        super(id,loc,clr,ang,shap,info,dest,dam);
    }

    public CargoShip() {

    }

    private void setupShape() {
        ArrayList<Point> sp = new ArrayList<>();
        sp.add(new Point(+0, -20));
        sp.add(new Point(+12, +16));
        sp.add(new Point(+0, +30));
        sp.add(new Point(-12, +16));

        shape = new PolygonPlus();
        for (Point sp1 : sp) {
            shape.addPoint(sp1.x, sp1.y);
        }
    }

    private void setDebrisClouds(int dbrsClouds) throws ShipException {
        if (dbrsClouds < 0) {
            throw new ShipException("Cargo Ship can't have less than 0 debris clouds. " + dbrsClouds);
        }
        debrisClouds = dbrsClouds;
    }

    public void resetStrength() {
        strength = maxStrength;
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
        s += String.format("%40s%40s\n", "Damaged:         ", isDamaged());

        return s;
    }

    @Override
    public String getDisplayName() {
        if (getIdentifier().contains("CargoShip")) {
            return "CargoShip";
        }
        return "Not Available";
    }

   
    public boolean isMissile() {
        if (!getIdentifier().contains("Missile")) {
            return false;
        } else {
            return true;
        }
    }
    
    @Override
    public boolean isDestroyed() {
        boolean destroyed = false;
        try {
            if (strength > maxStrength) {
                throw new ShipException("Strength can't be larger than maxStrength in method, Cargo Ship not destroyed.");
            }
        } catch (ShipException ex) {
            ShipException shipException
                    = new ShipException(ex.toString());
            System.out.println(shipException.getMessage());
        }
        if (strength <= 0) {
            destroyed = true;
        }
        return destroyed;
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
    public void reactToRadarLock(Point3D point) throws SpaceControllerException, ShipException, DebrisCloudException {
        //new
        if (waitCount > 0) {
            return;
        }
        //end new
        if ((debrisClouds != 0) && (atPort == false)) {

            destination = SpaceController.makePoint();
            SoundUtility.getInstance().playSound("sounds" + File.separator + "Cloud.wav");
            Point3D newP;

            for (int i = 0; i < 2; i++) {
                setLocation(point);
                newP = SpaceController.makePointNear(getLocation(), 0.05);

                dci = new DebrisCloud("Debris Cloud " + getDebrisClouds() + " from " + getIdentifier(), newP, "GRAY", 3000, true, 1.0);
                new Thread(dci).start();
                debrisClouds -= 1;
            }
        }
        //new
        waitCount = 25;
        //end new
    }

    public void updateStatus() throws SpaceControllerException, ShipException {
        Point3D p;
        String portId;
        if (isDamaged()) {

            if (isSpacePort()) {
                if (SpaceController.getLocation(currentPort) == null) {
                    currentPort = null;
                    atPort = false;
                    setDestination(SpaceController.makePoint());
                } else {
                    portId = SpaceController.getNearestSpacePort(getLocation(), getColorName());
                    p = SpaceController.getLocation(portId);
                    if (p != null) {
                        spacePort.strength += .25;
                        location = new Point3D(p);
                        setDestination(p);
                    } 
                }
            } else { // is not space port
                portId = SpaceController.getNearestSpacePort(getLocation(), getColorName());
                if (portId == null) {
                    return;
                } else {
                    p = SpaceController.getLocation(portId);
                }
                if (p == null) {
                    return;
                } else {
                    setDestination(p);
                }
                if (location.distance(destination) <= 10.0) {
                    destination = location;
                    atPort = true;
                    currentPort = getIdentifier();
                }
            }
        } else {
            if (atPort) {
                currentPort = null;
                atPort = false;
                setDestination(SpaceController.makePoint());
            }
        }
    }

    @Override
    public void run() {
        while (isDestroyed() != true) {
            try {
                //new
                // when at zero cargoShip deploys debris clouds
                if (waitCount > 0) {
                    waitCount -= 1;
                }
                //end new
                updateStatus();
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

                Thread.sleep(50);
            } catch (SpaceControllerException | ShipException | InterruptedException ex) {
                SpaceControllerException controller
                        = new SpaceControllerException(ex.toString());
                System.out.println(controller.getMessage());

            }
        }
    }
}
