package Exceptions;

/**
 * If number of bits in the Node is different than what is set in SkipNodeDataBase,
 * exception can be thrown by using this class.
 * @author Esin Menceloglu
 */
public class BitMissMatchException extends Throwable {
    private int bits;
    public BitMissMatchException(int bits){
        this.bits=bits;
    }
    public int getBits(){
        return this.bits;
    }

}
