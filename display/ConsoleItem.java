package display;

import java.awt.Color;
import utils.Point3D;
import utils.PolygonPlus;

public interface ConsoleItem {

    double getAngle();

    Color getColor();

    String getId();

    Point3D getLocation();

    // AKA Shape
    PolygonPlus getPolygon(double zSize);

    void setLocation(Point3D p);

    String getInfoText();

    boolean isDestroyed();

    boolean isDamaged();

}
