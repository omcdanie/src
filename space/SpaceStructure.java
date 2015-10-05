package space;

import exceptions.DebrisCloudException;
import exceptions.ShipException;
import exceptions.SpaceControllerException;
import java.awt.Color;
import utils.Point3D;
import utils.PolygonPlus;

/**
 * Defines the minimum implementations of Space Structures.
 * 
 * @author Ola McDaniel
 */
public interface SpaceStructure extends Runnable {

    Color getColor();

    String getDisplayName();

    String getIdentifier();

    String getInfoText();

    Point3D getLocation();

    PolygonPlus getShape();

    boolean isDamaged();

    boolean isDestroyed();

    boolean isSpacePort();

    double getMaxStrength();

    double getAngle();

    double getSpeed();

    double getStrength();

    void reactToRadarLock(Point3D point) throws SpaceControllerException, ShipException, DebrisCloudException;

    void applyDamage(double dmg) throws SpaceControllerException, DebrisCloudException;

}
