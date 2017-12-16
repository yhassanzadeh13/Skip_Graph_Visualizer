package sample;

/**
 * Created by Esin Menceloglu on 03/09/2017.
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
