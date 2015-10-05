package exceptions;

/**
 * Exception class for any type of ship not a Space Port.
 * 
 * @author Ola McDaniel
 */
public class ShipException extends Exception {
    
    public ShipException() {
        
    }
    
    public ShipException(String msg) {
        super(msg);
    }
    
}
