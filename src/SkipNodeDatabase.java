package sample;

/**
 * Created by Esin Menceloglu on 19/07/2017.
 */
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;

import sample.SkipNode;

public class SkipNodeDatabase {
    private static int elements; //the total number of elements in the database
    public static SkipNode[] databaseArray;
    private int bits; //represents the number of digits that the nameIDs are


    //constructor
    public SkipNodeDatabase(int bits,int size){
        this.bits=bits;
        databaseArray=new SkipNode[size];
    }
    public SkipNodeDatabase(int bits){
        this.bits=bits; //there are #bits number of levels in a skipgraph
        databaseArray=new SkipNode[20];
    }
    public SkipNodeDatabase(){
        this.bits=4;
        databaseArray=new SkipNode[20];
    }
    //getters & setters
    public int getBits() {return bits;}
    public int getElements(){return elements;}
    public SkipNode get(int index){     return databaseArray[index];}
    public int size(){return databaseArray.length;}
    public void setBits(int bits) {this.bits = bits;}

    public static void main(String[] args){
        SkipNodeDatabase db1=new SkipNodeDatabase();

    }

    /**
     * This function takes a SkipNode element and adds it onto a specific slot on the databaseArray
     * @param element: is the SkipNode that is added onto the database
     * @param index: the slot is determined by the integer input value index
     */
    public void put(SkipNode element,int index){
        if (databaseArray[index]==null){
            elements++;
        }
        databaseArray[index] = element;
    }


    /**
     *  this function sets up a database from a lookup table that fileName will provide
     * @param fileName: Sting fileName is where the information that is to be read is stored
     * @param bits:each node will have #bits many left and right neighbors
     * @return: if the file was successfully read, return true
     */
    public boolean read(String fileName, int bits){//reads documents that only contain the lookup tables for each node
        Scanner s = null;
        setBits(bits);
        try {
            s = new Scanner(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        int m=0;
        while (s.hasNext()&& m<size()){
            try{
                SkipNode sn=new SkipNode(s.next(),s.nextInt());//this function uses NumericIDs as representatives of its left and right nodes
                put(sn,m);
                for(int j=0; j<getBits();j++){
                    get(m).setLeft(j,s.nextInt());
                    get(m).setRight(j,s.nextInt());
                }
                m++;
            }catch (InvalidIDException e2){
                System.out.println("SkipNode Invalid");
            }

        }
        s.close();
        return true;
    }

    /**
     * This function helps us search for a node by its numericID in a database
     * @param numericID: is the numericID of the node we want to find
     * @return: the index which the node is placed on on the database. Note that if the node is not find, size of the database will be returned
     */
    public int findIndex(int numericID){//returns the index of a node that has the input numerical ID
        boolean found=false;
        for(int i=0; i<elements&&!found;i++){
            if (get(i).getNumericID()==numericID){
                found=true;
                return i;
            }
        }return size();
    }

    /**
     * This function simply doubles the size of a database
     */
    public void grow(){
        int length= databaseArray.length*2;//doubles the length of the given array
        SkipNode[] arrayNew=new SkipNode[length];//creates new array with doubled length
        System.out.println(length);
        for (int i = 0; i < elements; i++) {
            arrayNew[i]=databaseArray[i];//copies the contents into the new array
        }
        databaseArray=arrayNew;

    }


    /**
     * This function adds a node s to the databaseArray
     * @param s: the node that is to be added onto the databaseArray
     * @return true if node s was added successfully
     */
    public boolean add(SkipNode s) {
        boolean added=false;
        if(elements==size()-1){
            grow();
        }
        if (s!=null){
            int i = 0;
            for(i=0;i<size()&&!added ;i++) {
                if (databaseArray[i]==null) {
                    databaseArray[i] = s;
                    added = true;
                    elements++;
                }
            }
        }return added;
    }

    /**
     * Different from the add() function, this function inserts a SkipNode sn, sets up all of its left/right neighbors on all levels, then adds it to the databaseArray using the add() function
     * @param sn: is the skipnode that is to be inserted
     * @return true if sn was successfully inserted
     */
    public boolean insert(SkipNode sn) throws BitMissMatchException{
        SkipNode tempPrev;
        SkipNode tempNext;
        boolean placed=false;
        int level=0;
        if (sn.getNameID().length()!=getBits()){
            throw new BitMissMatchException(bits);
        }else{
            if (elements==0){//if sn is the first node in the database, directly add it onto it
                add(sn);
                placed=true;
            }else if (get(0)!=null && get(0).getNumericID()>sn.getNumericID()){//if it's not the first node but its numerical id is less than the first node in the database;
                placed=true;
                sn.setRightNode(level,get(0));//set the first node on the database as the right node of input node at level 0
                sn.setRight(level,get(0).getNumericID());

                get(0).setLeftNode(level,sn);//set the input node as the first node's left node at level 0
                get(0).setLeft(level, sn.getNumericID());
                add(sn);//add the node to the database
            }else{
                for (int i=0;i<elements-1 &&!placed;i++){
                    if (get(i).getNumericID()<sn.getNumericID()&& get(i+1).getNumericID()>sn.getNumericID()){//check if input node should go anywhere between the first and the last node in the sorted database
                        placed=true;

                        sn.setLeftNode(level,get(i));//set get(i) as input node's left node at level (0)
                        sn.setLeft(level,get(i).getNumericID());

                        sn.setRightNode(level,get(i+1));//set get(i+1) as input node's right node at level (0)
                        sn.setRight(level,get(i+1).getNumericID());

                        get(i).setRightNode(level,sn);//update sn's left and right neighbors' right and left neighbors
                        get(i).setRight(level, sn.getNumericID());

                        get(i+1).setLeftNode(level,sn);
                        get(i+1).setLeft(level, sn.getNumericID());

                        add(sn);

                    }

                }
                if(!placed){//if input node's numerical id is greater than all the nodes in the database;
                    sn.setLeftNode(level,get(elements-1));//set the greatest node in the database as sn's left node
                    sn.setLeft(level,get(elements-1).getNumericID());

                    get(elements-1).setRightNode(level,sn);//set sn as the last node's right node
                    get(elements-1).setRight(level, sn.getNumericID());

                    add(sn);
                    placed=true;

                }
            }
            if(placed){
                sort(true);
            }

            for (level=1;level<getBits();level++) {//starting form the next level, set input node's left and right neighbors
                setLeft(sn,sn.getLeftNode(level-1),level);
                setRight(sn,sn.getRightNode(level-1),level);
            }

        }

        return placed;
    }

    /**
     * this function finds the node sn should connect to as its left neighbor on a given level
     * @param self is the node that is to be connected
     * @param leftNeighbor
     * @param level indicates which level of the node to find the neighbor for
     */
    public void setLeft(SkipNode self,SkipNode leftNeighbor, int level){
        if((leftNeighbor!=null) ){
            if(commonBits(leftNeighbor,self)>=level){//if your left neighbor on level-1 fulfills this statement;
                self.setLeftNode(level,leftNeighbor);//set it as your left neighbor on level as well
                self.setLeft(level, leftNeighbor.getNumericID());//update the numericalID based arrays

                leftNeighbor.setRightNode(level,self);//set yourself as your left neighbor's right neighbor
                leftNeighbor.setRight(level,self.getNumericID());

            }else{
                setLeft(self,leftNeighbor.getLeftNode(level - 1), level);
            }
        }
    }


    public void setRight(SkipNode self, SkipNode rightNeighbor, int level){
        if (rightNeighbor!=null){
            if(commonBits(rightNeighbor,self)>=level){
                self.setRightNode(level,rightNeighbor);
                self.setRight(level,rightNeighbor.getNumericID());

                rightNeighbor.setLeftNode(level,self);
                rightNeighbor.setLeft(level,self.getNumericID());

            }else{
                setRight(self,rightNeighbor.getRightNode(level-1),level);

            }
        }
    }

    /**
     * this function puts the skipnode array in order
     * @param order if order is true, the skipnodes are sorted(based on their numerical id) from the smallest to the greatest
     */
    public void sort(boolean order){  //
        boolean swapped = true;
        SkipNode temp;
        if (order){
            for (int i = 0; i < databaseArray.length && swapped; i++) {
                swapped = false;      //if everything is sorted, dont go through it again
                for (int j = 1; j < elements - i&&j<size(); j++) {     //length-i because the bottom has already been sorted>>>
                    if (get(j)!=null&&get(j).getNumericID() <get(j-1).getNumericID()) {
                        swapped = true;
                        temp = databaseArray[j];
                        databaseArray[j] = databaseArray[j-1];
                        databaseArray[j-1]= temp;
                    }
                }
            }
        }else{
            for (int i = 0; i < databaseArray.length && swapped; i++) {
                swapped = false;
                for (int j = 1; j > elements - i; j++) {
                    if (get(j).getNumericID()<get(j-1).getNumericID()) {
                        swapped = true;
                        temp = databaseArray[j];
                        databaseArray[j] = databaseArray[j-1];
                        databaseArray[j-1]= temp;
                    }
                }
            }
        }
    }

    /**
     * returns the number of bits two skipnodes have in common
     * @param s1 first node that is to be compared
     * @param s2 second node that is to be compared
     * @return the number of common bits the two skipnodes have
     */
    protected int commonBits(SkipNode s1, SkipNode s2){
        int k=0;
        while(s1.getNameID().charAt(k)==s2.getNameID().charAt(k)){
            k++;
            if (k>= s1.getNameID().length() || k>=s2.getNameID().length()) break;
        }
        return k;
    }

    @Override
    public String toString() {
        String dtb="[";
        for (int i=0;i<elements && i<size()&&get(i)!=null;i++){
            dtb+=String.valueOf(get(i).getNumericID());
            if(i<elements-1){
                dtb+=", ";
            }else dtb+="]";
        }return dtb;
    }

    private class InvalidIDInputException extends Exception {
    }
}
