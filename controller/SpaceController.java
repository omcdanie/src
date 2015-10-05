package controller;

import display.ConsoleItemImpl;
import display.ViewManager;
import exceptions.DebrisCloudException;
import exceptions.ShipException;
import exceptions.SpaceControllerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import space.DebrisCloud;
import space.SpaceEntity;
import utils.Point3D;

/**
 * Contains functionality for space. Includes, processing ships and debris
 * clouds, processing damage, and assigning new points in space.
 * 
 * @author Ola McDaniel
 */
public class SpaceController {

    public static final int xSize = 800, ySize = 900, zSize = 700;
    //SpacePort is a CargoShip
    private static final HashMap<String, SpaceEntity> activeShips = new HashMap<>();

    private static final HashMap<String, DebrisCloud> activeDebrisClouds = new HashMap<>();
    
    //SpaceEntity cargoShip = new CargoShip();
    public SpaceController() {
    }

    // static initializer for InfoThread
    static {
        InfoThread info = new InfoThread();
        new Thread(info).start();
    }

    public static Point3D makePoint() throws SpaceControllerException {

        int xCoord = (int) (Math.random() * xSize);
        int yCoord = (int) (Math.random() * ySize);
        int zCoord = (int) (Math.random() * zSize);
        return new Point3D(xCoord, yCoord, zCoord);
    }

    public static Point3D makePointNear(Point3D somePoint, double percent) throws SpaceControllerException {

        int xVar = (int) (SpaceController.xSize * percent);
        int yVar = (int) (SpaceController.ySize * percent);
        int zVar = (int) (SpaceController.zSize * percent);
        int xCoord = (int) (somePoint.getX() * (1 - (percent / 2))) + (int) (Math.random() * xVar);
        int yCoord = (int) (somePoint.getY() * (1 - (percent / 2))) + (int) (Math.random() * yVar);
        int zCoord = (int) (somePoint.getZ() * (1 - (percent / 2))) + (int) (Math.random() * zVar);
        return new Point3D(xCoord, yCoord, zCoord);

    }

    public static void processDetonation(String id, Point3D detonationLocation, int detonationRange, int damageMax) throws SpaceControllerException, DebrisCloudException {

        if (id != null) {
            if (detonationLocation == null || detonationRange < 0 || damageMax < 0) {
                throw new SpaceControllerException("Detonation type invalid. Reason: " + detonationLocation + ", " + detonationRange + ", " + damageMax + ".");
            }

            ArrayList<String> ships = new ArrayList(activeShips.keySet());
            for (String key : ships) {
                SpaceEntity cargoShip = activeShips.get(key);

                double distance = cargoShip.getLocation().distance(detonationLocation);

                if ((distance <= detonationRange)
                        && !(id.equals(key))
                        && (cargoShip.isDestroyed() == false)
                        && distance > 0.0) {
                    double damagePercent = (detonationRange - distance) / detonationRange;
                    double damage = (.5 * damageMax) + (Math.random() * .5 * damageMax);
                    double damageToApply = damage * damagePercent;

                    cargoShip.applyDamage(damageToApply);
                } else {
                    if ((distance == 0.0)
                            && !(id.equals(key))
                            && cargoShip.isDestroyed() == false) {
                        double damagePercent = 1.0;
                        double damage = (.5 * damageMax) + (Math.random() * .5 * damageMax);
                        cargoShip.applyDamage(damage * damagePercent);
                    }

                }

            }

        }
    }

    public static void addShip(SpaceEntity ship) throws SpaceControllerException {

        synchronized (activeShips) {
            if (ship != null) {

                activeShips.put(ship.getIdentifier(), ship);
                System.out.println(ship.getIdentifier());
                ViewManager.getInstance().updateItem(new ConsoleItemImpl(
                        ship.getIdentifier(),
                        ship.getLocation(),
                        ship.getColor(),
                        ship.getAngle(),
                        ship.getShape(),
                        ship.getInfoText(),
                        ship.isDestroyed(),
                        ship.isDamaged()));
                new Thread(ship).start();
            } else {
                throw new SpaceControllerException("Can't add ship to space, value is empty.");
            }
        }
    }

    public static void removeShip(String identifier) throws SpaceControllerException {

        synchronized (activeShips) {
            if (identifier != null) {
                activeShips.remove(identifier);
                System.out.println(activeShips.remove(identifier));
            } else {
                throw new SpaceControllerException("Ship not removed from space. The id is invalid. " + identifier);
            }
        }
    }

    public static void addCloud(DebrisCloud debrisCloud) throws SpaceControllerException, DebrisCloudException {

        synchronized (activeDebrisClouds) {
            if (debrisCloud != null) {

                activeDebrisClouds.put(debrisCloud.getIdentifier(), debrisCloud);
                ViewManager.getInstance().updateItem(
                        new ConsoleItemImpl(
                                debrisCloud.getIdentifier(),
                                debrisCloud.getLocation(),
                                debrisCloud.getColor(),
                                debrisCloud.getAngle(),
                                debrisCloud.getShape(),
                                debrisCloud.getInfoText(),
                                false,
                                false));

            } else {
                throw new SpaceControllerException("Can't add cloud to space, value is empty.");
            }
        }
    }
    
    //new for phase 3
    public static boolean isDestroyed(String id) throws SpaceControllerException {

        boolean destroyed = false;
        if (id == null) {
            throw new SpaceControllerException("ID passed to isDestroyed in SpaceController is " + id);
        } else {
            SpaceEntity ship;
            for (String key : activeShips.keySet()) {
                ship = activeShips.get(key);
                if (ship.getIdentifier().matches(id)) {
                    return ship.isDestroyed();
                } else {
                    destroyed = true;
                }
            }
        }
        return destroyed;
    }
    
    public static boolean fightersRemain() throws SpaceControllerException {
        SpaceEntity ship;
        boolean remaining = false;
        for (String key : activeShips.keySet()) {
            ship = activeShips.get(key);
            if (ship.getIdentifier().contains("Fighter")) {
                remaining = true;
            }
        }
        return remaining;

    }
    
    public static String reaquireRadarLock(String id, Point3D p3Din,
            String targetIn, String clrNm, double rangeIn) throws SpaceControllerException {

        if (id == null) {
            throw new SpaceControllerException("Can't reaquire radar lock.  Reason id is: " + id);
        }
        if (p3Din == null) {
            throw new SpaceControllerException("Can't reaquire radar lock.  Reason point is: " + p3Din);
        }
        if (targetIn == null) {
            throw new SpaceControllerException("Can't reaquire radar lock.  Reason target is: " + targetIn);
        }
        if (clrNm == null) {
            throw new SpaceControllerException("Can't reaquire radar lock.  Reason color is: " + clrNm);
        }
        if (rangeIn < 0.0) {
            throw new SpaceControllerException("Can't reaquire radar lock.  Reason range is: " + rangeIn);
        }
        ArrayList<String> list;
        int idx;
        if (Math.random() < .9) {
            return targetIn;
        } else {
            list = performRadarSweep(id, p3Din, clrNm, rangeIn);
            list.remove(id);
            if (list.isEmpty()) {
                return null;
            } else {
                idx = (int) (Math.random() * list.size());
            }

        }

        return list.get(idx);
    }
    
    @SuppressWarnings("null")
    public static ArrayList<String> performRadarSweep(String id, Point3D p3Din, String clrNm, double rangeIn) throws SpaceControllerException {
        ArrayList<String> result = new ArrayList<>();
        SpaceEntity ships = null;
        DebrisCloud clouds;

        if (id == null) {
            throw new SpaceControllerException("Can't perform radar sweep.  Reason id is: " + id);
        }
        if (p3Din == null) {
            throw new SpaceControllerException("Can't perform radar sweep.  Reason point is: " + p3Din);
        }
        if (clrNm == null) {
            throw new SpaceControllerException("Can't perform radar sweep.  Reason color is: " + clrNm);
        }
        if (rangeIn == 0.0) {
            throw new SpaceControllerException("Can't perform radar sweep.  Reason range is: " + rangeIn);
        }

        for (String key : activeShips.keySet()) {

            ships = activeShips.get(key);

            if (!(ships.getIdentifier().matches(id))
                    && (!ships.getColorName().equals(clrNm))
                    && (ships.getLocation().distance(p3Din) < rangeIn)) {

                result.add(ships.getIdentifier());

            }
        } // end for loop
        for (String key : activeDebrisClouds.keySet()) {
            clouds = activeDebrisClouds.get(key);

            if (clouds.getLocation().distance(p3Din) < rangeIn) {
                result.add(ships.getIdentifier());

            }
        } // end for loop
        return result;
    }
    
     public static void setRadarLock(String id, Point3D pln) throws SpaceControllerException, ShipException, DebrisCloudException {
        SpaceEntity ship;

        if (id == null) {
            throw new SpaceControllerException("Unable to set radar lock.  Reason id is: " + id);
        }
        if (pln == null) {
            throw new SpaceControllerException("Unable to set radar lock.  Reason point is: " + pln);
        }

        for (String key : activeShips.keySet()) {
            ship = activeShips.get(key); 
            if (ship.getIdentifier() == null) {
                continue;
            }
            if (!ship.getIdentifier().matches(id)) {              
                ship.reactToRadarLock(pln);

            }
        }
    }
    // end new phase 3 code

    public static void removeCloud(String identifier) throws SpaceControllerException {

        synchronized (activeDebrisClouds) {
            if (identifier != null) {

                activeDebrisClouds.remove(identifier);
            } else {
                throw new SpaceControllerException("Cloud cannot be removed from space. The id is invalid.");
            }
        }
    }

    public static Point3D getLocation(String id) throws SpaceControllerException {
        SpaceEntity ship;
        DebrisCloud cloud;
        if (id != null) {
            for (String key : activeShips.keySet()) {
                if (key.matches(id)) {
                    ship = activeShips.get(id);
                    return ship.getLocation();
                }
            }
            for (String key : activeDebrisClouds.keySet()) {
                if (key.matches(id)) {
                    cloud = activeDebrisClouds.get(id);
                    return cloud.getLocation();
                }
            }
        } else {
            throw new SpaceControllerException("Space Controller isn't able to get ship or cloud's location. The id is: " + id);
        }
        return null;
    }

    public static String getNearestSpacePort(Point3D loc, String clrNm) throws SpaceControllerException {
        if (loc == null) {
            throw new SpaceControllerException("SpacePort's location is invalid. Reason: " + loc);
        }
        if (clrNm == null) {
            throw new SpaceControllerException("SpacePort's color is invalid. Reason: " + clrNm);
        }
        for (String key : activeShips.keySet()) {
            SpaceEntity ship = activeShips.get(key);
            // Space Port Found
            if (ship.getIdentifier().contains("SpacePort")
                    && ship.getColorName().equals(clrNm)) {
                return key;
            }
        }
        // No SpacePort Found
        return null;
    }

    public static HashMap getElementCounts() throws SpaceControllerException {
        HashMap<String, HashMap<String, Integer>> colorMap = new HashMap<>();
        String shipColor, elementType = null;
        for (String key : activeShips.keySet()) {
            // changed for abstract impl
            SpaceEntity ship = activeShips.get(key);

            shipColor = ship.getColorName();
            // check for ships and ports 
            if (ship.getIdentifier().contains("CargoShip")) {
                elementType = ship.getDisplayName();
            }
            if (ship.getIdentifier().contains("SpacePort")) {
                elementType = ship.getDisplayName();
            }
            if (ship.getIdentifier().contains("FighterShip")) {
                elementType = ship.getDisplayName();
            }
            // create new HashMap containing a HashMap
            if (colorMap.get(shipColor) == null) {
                colorMap.put(shipColor, new HashMap<String, Integer>());
            }
            HashMap<String, Integer> elementMap = colorMap.get(shipColor);
            if (elementMap.get(elementType) == null) {
                // initiate count for a single type in map
                elementMap.put(elementType, 1);
            } else {
                //increment count for each type in map
                elementMap.put(elementType, elementMap.get(elementType) + 1);
            }
        }

        return colorMap;
    }

    static class InfoThread implements Runnable {

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            while (true) {
                try {
                    long loopStart = System.currentTimeMillis();
                    HashMap<String, HashMap<String, Integer>> counts = getElementCounts();
                    StringBuilder sb = new StringBuilder();
                    ArrayList<String> ar = new ArrayList<>(counts.keySet());
                    Collections.sort(ar);
                    for (String side : ar) {
                        if (sb.length() != 0) {
                            sb.append("     ");
                        }
                        sb.append(side).append(": ");
                        HashMap<String, Integer> hm = counts.get(side);
                        ArrayList<String> k = new ArrayList<>(hm.keySet());
                        Collections.sort(k);
                        for (String field : k) {
                            sb.append(field).append(": ").append(hm.get(field)).append(" ");
                        }
                    }
                    long timePassed = System.currentTimeMillis() - start;
                    String timeString = makeTimeString(timePassed);
                    ViewManager.getInstance().updateInfo("[" + timeString + "]  " + sb.toString());
                    try {
                        Thread.sleep(1000 - (loopStart - System.currentTimeMillis()));
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                } // end while
                catch (SpaceControllerException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } // end run

        private String makeTimeString(long timePassed) {

            String convertedTime = "";

            long h = TimeUnit.MILLISECONDS.toHours(timePassed);
            long m = TimeUnit.MILLISECONDS.toMinutes(timePassed)
                    - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timePassed));
            long s = TimeUnit.MILLISECONDS.toSeconds(timePassed)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timePassed));

            convertedTime += String.format("%02d:%02d:%02d", h, m, s);

            return convertedTime;

        }

    }
}
