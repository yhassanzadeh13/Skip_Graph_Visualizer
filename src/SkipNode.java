package sample;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.Node;

import java.util.Arrays;


/**
 * Created by Esin menceloglu on 18/07/2017.
 */
public class SkipNode extends Node {
    private String NameID;
    private int NumberID;

    private SkipNode[] leftNode;//this array keeps track of all the left/right neighbors of an array throughout levels
    private SkipNode[] rightNode; //indexing could be used to retrieve a neighbor in a specific level

    private int[] left;//this array keeps track of all the numericalIDs left/right neighbors of an array throughout levels
    private int[] right; //keeping numericalIDs on different arrays are necessary for the read function

    private boolean[] checked;//this array is used during the visualization process, in nextLayer()

    //Constructor
    public SkipNode(String name, int num) throws InvalidIDException{//bits represent the number of bits in a node's nameID
        NameID = name;
        NumberID = num;
        int bits=name.length();
        left=new int[bits]; //the number of bits correspond with the number of layers that the skipGraph has,
        right=new int[bits];//which is why it is used for indexing

        leftNode=new SkipNode[bits];
        rightNode=new SkipNode[bits];
        checked=new boolean[bits];

    }

    //Getters &setters
    public void setNameID(String nameID) {NameID = nameID;}
    public void setNumericID(int numberID) {NumberID = numberID;}

    public void setLeft(int level, int prev) {left[level] = prev; }
    public void setRight(int level, int next) {right[level] = next;}

    public void setLeftNode(int level, SkipNode prev) {leftNode[level] = prev; }
    public void setRightNode(int level, SkipNode next) {rightNode[level] = next;}

    public void check(int level){checked[level]=true;}
    public void uncheckAll(){ checked=new boolean[this.getNameID().length()];}//needed in reset() method in main
    public boolean isChecked(int level) {return checked[level];}

    public String getNameID() {return NameID;}
    public int getNumericID() {return NumberID;}

    public boolean hasLeft(int layer){return leftNode[layer]!=null;}
    public boolean hasRight(int layer){return rightNode[layer]!=null;}

    public SkipNode getLeftNode(int layer) {return leftNode[layer];}
    public SkipNode getRightNode(int layer) {return rightNode[layer];}

    public int getLeft(int layer) {return left[layer];}
    public int getRight(int layer) {return right[layer];}


    public String toString(){
        String sn="Name ID: "+getNameID()+"\n"+"NumericalID: "+getNumericID();
        sn+="\n"+"Right neighbors: "+ Arrays.toString(right);
        sn+="\n"+"Left neighbors: "+ Arrays.toString(left)+"\n"+"\n";
        return sn;
    }

    //
    @Override
    public BaseBounds impl_computeGeomBounds(BaseBounds bounds, BaseTransform tx) {
        return null;
    }

    @Override
    protected boolean impl_computeContains(double localX, double localY) {
        return false;
    }

    @Override
    public Object impl_processMXNode(MXNodeAlgorithm alg, MXNodeAlgorithmContext ctx) {
        return null;
    }
    @Override
    protected NGNode impl_createPeer() {
        return null;
    }

}
