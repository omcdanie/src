package exceptions;

/**
 * Exception class for Space Controller class.
 * 
 * @author Ola McDaniel
 */
public class SpaceControllerException extends Exception {
    
    public SpaceControllerException() {
        
    }
    
    public SpaceControllerException(String msg) {
        super(msg);
    }
    
}
