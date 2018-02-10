package Exceptions;

/**
 *If numericalID includes characters other than numerical ones,
 * exception can be thrown by using this class.
 * @author Esin Menceloglu
 */
public class InvalidIDException extends Throwable {
    private String numericID;
    public InvalidIDException(String numericID){
        this.numericID=numericID;
    }

}
