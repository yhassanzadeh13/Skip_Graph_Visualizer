package sample;

/**
 * Created by esin on 08/09/2017.
 */
public class InvalidIDException extends Throwable {
    private String numericID;
    public InvalidIDException(String numericID){
        this.numericID=numericID;
    }

}
