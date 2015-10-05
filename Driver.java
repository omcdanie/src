import space.SpacePort;
import space.CargoShip;
import controller.SpaceController;
import display.ViewManager;
import exceptions.DebrisCloudException;
import exceptions.ShipException;
import exceptions.SpaceControllerException;
import exceptions.SpacePortException;
import javax.swing.JOptionPane;
import utils.Point3D;

/* 
 This is the test "driver" program that will test the Display subsystem.
 This driver executes tests as described in the Quarter Programming Project 
 document.
 - You can change the import statements to match your design
 - You can propagate your exceptions out to this driver.
 - Small changes are allowed here to account for your classnames. 
 - The content of the tests themselves cannot be changed.
 */
public class Driver {

    public static void main(String[] args) {

        ViewManager.setup(SpaceController.xSize, SpaceController.ySize, SpaceController.zSize);

        ViewManager.getInstance();
        try {
            initTests();
        } catch (SpaceControllerException |
                SpacePortException |
                DebrisCloudException |
                ShipException |
                InterruptedException ex) {
            System.out.println(ex.getMessage());
           // ex.printStackTrace();
        }

        System.exit(0);

    }

    private static void initTests() throws SpaceControllerException, SpacePortException, DebrisCloudException, ShipException, InterruptedException {

        JOptionPane.showMessageDialog(null, "Test 1: Create Pink Cargo Ship & SpacePort\n(At each test step, verify the timer and ship/port\ncount in the lower-left of the view screen)", "Create Ships & Ports", JOptionPane.INFORMATION_MESSAGE);
        CargoShip cs1 = new CargoShip("CargoShip1", "PINK", new Point3D(100, 300, 800), new Point3D(700, 300, 600), 1000.0, 7.0, 50);
        //CargoShip cs2 = new CargoShip("SpacePort1", "PINK", new Point3D(475, 375, 600), new Point3D(100, 700, 300), 1000.0, 0.03, 100);
        SpacePort sp1 = new SpacePort("SpacePort1", "PINK", new Point3D(475, 375, 600), new Point3D(100, 700, 300), 1000.0, 0.03, 100);
        Thread.sleep(1000);

        JOptionPane.showMessageDialog(null, "Test 2: Create Blue Cargo Ship & SpacePort", "Create Ships & Ports", JOptionPane.INFORMATION_MESSAGE);
        CargoShip cs2 = new CargoShip("CargoShip2", "SKYBLUE", new Point3D(700, 650, 300), new Point3D(200, 650, 600), 1000.0, 7.0, 50);
        SpacePort sp2 = new SpacePort("SpacePort2", "SKYBLUE", new Point3D(475, 575, 700), new Point3D(100, 700, 300), 1000.0, 0.03, 100);
        Thread.sleep(1000);

        JOptionPane.showMessageDialog(null, "Test 3: Create Red Cargo Ship", "Create Ships & Ports", JOptionPane.INFORMATION_MESSAGE);
        CargoShip cs3 = new CargoShip("CargoShip3", "RED", new Point3D(200, 750, 200), new Point3D(700, 50, 700), 1000.0, 7.0, 50);
        Thread.sleep(1000);

        JOptionPane.showMessageDialog(null, "Test 4: Apply Damage to Pink & Blue Cargo Ships\n(Pink and Blue ships should move to their ports for repairs)", "Apply Damage", JOptionPane.INFORMATION_MESSAGE);
        cs1.applyDamage(30);
        cs2.applyDamage(25);
        Thread.sleep(3000);

        JOptionPane.showMessageDialog(null, "Test 5: Reset Pink & Blue Cargo Ship Strength\n(Ships should leave their ports)", "Reset Strength", JOptionPane.INFORMATION_MESSAGE);
        cs1.resetStrength();
        cs2.resetStrength();
        Thread.sleep(2000);

        JOptionPane.showMessageDialog(null, "Test 6: Pink & Blue Cargo Ships React to Radar Lock", "Radar Lock", JOptionPane.INFORMATION_MESSAGE);
        cs1.reactToRadarLock(cs1.getLocation());
        cs2.reactToRadarLock(cs1.getLocation());
        Thread.sleep(2000);

        JOptionPane.showMessageDialog(null, "Test 7: Pink & Blue Space Ports File Rail Guns at Red Cargo Ship Until it is Destroyed", "Fire Rail Gun", JOptionPane.INFORMATION_MESSAGE);
        cs3.applyDamage(500);
        while (!cs3.isDestroyed()) {

            sp1.reactToRadarLock(cs3.getLocation());
            sp2.reactToRadarLock(cs3.getLocation());
            Thread.sleep(1000);
        }
        Thread.sleep(2000);

        JOptionPane.showMessageDialog(null, "Test 8: Apply Damage to Pink Cargo Ship\n(Ship should move to port for repairs)", "Apply Damage", JOptionPane.INFORMATION_MESSAGE);
        cs1.applyDamage(500);
        Thread.sleep(3000);

        JOptionPane.showMessageDialog(null, "Test 9: Blue Space Port Fires Rail Guns at Pink Space Port\nUntil Pink Port and Pink Cargo Ship are Destroyed", "Fire Rail Gun", JOptionPane.INFORMATION_MESSAGE);
        while (!sp1.isDestroyed()) {
            sp2.reactToRadarLock(sp1.getLocation());
            Thread.sleep(200);
        }
        Thread.sleep(2000);

        JOptionPane.showMessageDialog(null, "Test 10: Apply Damage to Blue Cargo Ship\n(Ship should move to port for repairs)", "Apply Damage", JOptionPane.INFORMATION_MESSAGE);
        cs2.applyDamage(950);
        Thread.sleep(3000);

        JOptionPane.showMessageDialog(null, "Test 11: Apply Damage to Destroy Space Port\n(Blue Cargo Ship should also be destroyed by the blast)", "Apply Damage", JOptionPane.INFORMATION_MESSAGE);
        sp2.applyDamage(1000);
        Thread.sleep(3000);

        JOptionPane.showMessageDialog(null, "Test 12: Create many ships & ports, verify display counts", "Verify Counts", JOptionPane.INFORMATION_MESSAGE);

        int i = 0;
        for (; i < 10; i++) {
            new CargoShip("CargoShip" + i, "BROWN", SpaceController.makePoint(), SpaceController.makePoint(), 1000.0, 7.0, 50);
        }

        for (; i < 15; i++) {
            new SpacePort("SpacePort" + i, "BROWN", SpaceController.makePoint(), SpaceController.makePoint(), 1000.0, 0.03, 100);
        }

        for (; i < 22; i++) {
            new CargoShip("CargoShip" + i, "TEAL", SpaceController.makePoint(), SpaceController.makePoint(), 1000.0, 7.0, 50);
        }

        for (; i < 30; i++) {
            new SpacePort("SpacePort" + i, "OLIVE", SpaceController.makePoint(), SpaceController.makePoint(), 1000.0, 0.03, 100);
        }

        Thread.sleep(2000);
        JOptionPane.showMessageDialog(null, "Test 13: Verify lower-left display count shows:\n"
                + "BROWN: Cargo: 10 Port: 5    OLIVE: Port: 8    TEAL: Cargo: 7", "Verify Counts", JOptionPane.INFORMATION_MESSAGE);

        Thread.sleep(3000);
        JOptionPane.showMessageDialog(null, "The Test is Complete.\nClick 'OK' to exit", "Complete", JOptionPane.INFORMATION_MESSAGE);

    }

}