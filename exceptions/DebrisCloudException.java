package exceptions;

/**
 * Exception class for Debris Cloud.
 * 
 * @author Ola McDaniel
 */
public class DebrisCloudException extends Exception {
    
    public DebrisCloudException() {
        
    }
    
    public DebrisCloudException(String msg) {
        super(msg);
    }
}
