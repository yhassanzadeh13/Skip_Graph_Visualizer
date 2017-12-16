package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import sample.SkipNodeDatabase;
import sample.SkipNode;

import java.util.Arrays;

import static sample.SkipNodeDatabase.databaseArray;

public class Main extends Application{

    private static VBox[] vbArray;
    private static int NumID=0;
    private Color[] colors={Color.DARKORANGE, Color.BLUEVIOLET,Color.FUCHSIA,Color.CHARTREUSE,Color.FORESTGREEN,Color.HONEYDEW,Color.BROWN,Color.CORAL,Color.DEEPSKYBLUE,Color.GREENYELLOW,Color.HOTPINK,Color.SALMON};


    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox root=new VBox();
        HBox buttons=new HBox(10);//all the buttons are saved in this HBox
        ScrollPane sp=new ScrollPane();//the visualization happens here

        //Test 0
        /*SkipNodeDatabase db0=new SkipNodeDatabase();
        try{
            setScene(sp,root,buttons,db0);
        }catch(BitMissMatchException e){
            System.out.println("bit missmatch");
        }*/


        //Test 1
        /*SkipNodeDatabase db1= new SkipNodeDatabase();
        db1.read("database1_lineup",3);
        try{
            setScene(sp,root,buttons,db1);
        }catch (BitMissMatchException e3){
            System.out.println("Bit MissMatch");

        }*/

        //Test 2
        SkipNodeDatabase db2=new SkipNodeDatabase(3);
        try{
            SkipNode s1=new SkipNode("110",71);
            SkipNode s2=new SkipNode("111",99);
            SkipNode s3=new SkipNode("000",12);
            SkipNode s4=new SkipNode("001",39);
            SkipNode s5=new SkipNode("100",28);
            SkipNode s6=new SkipNode("010",49);
            SkipNode s7=new SkipNode("011",55);
            //SkipNode s8=new SkipNode("101",93);
            
            try{
                db2.insert(s1);
                db2.insert(s2);
                db2.insert(s3);
                db2.insert(s4);
                db2.insert(s5);
                db2.insert(s6);
                db2.insert(s7);
                //db2.insert(s8);
                setScene(sp,root,buttons,db2);
            }catch(BitMissMatchException e){
                System.out.println("Bit MissMatch");
            }
        }catch(InvalidIDException e){
            System.out.println("SkipNode not valid");
        }

        //Test 3
        /*SkipNodeDatabase db3= new SkipNodeDatabase();
        System.out.println(db3.read("database2_lineup ",16,4));
        setScene(db3.getBits(),root);*/

        primaryStage.setTitle("Skip Graph Visualization");
        Scene scene = new Scene(root, 800, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {launch(args);}

    /**
     *this function puts everything that is necessary for the visualization and controlling the visualization on the scene
     * @param sp the scrollpane the database visualization would go onto
     * @param root is the main box everything goes in
     * @param buttons is the HBox that all the buttons ie. add, switch goes on
     * @param db is the database that is to be visualized
     */
    public void setScene(ScrollPane sp,VBox root, HBox buttons,SkipNodeDatabase db) throws BitMissMatchException{//sets up the scene
        VBox vb=new VBox(60);
        vb.setAlignment(Pos.BOTTOM_LEFT);
        vb.setPrefSize(800,400);
        sp.setContent(vb);

        vbArray =new VBox[db.getBits()];//each layer of the skipGraph has its own VBox
        for (int i=0; i< db.getBits();i++){//there will be #bits layers
            vbArray[i]=new VBox(20);
            vbArray[i].minHeight(800);
            vbArray[i].minWidth(300);
            vbArray[i].setAlignment(Pos.CENTER_RIGHT);
            vb.getChildren().add(vbArray[i]);
        }

        Button Switch =new Button("SWITCH ALL");//Switch button switches the values on the buttons
        Switch.setOnAction((ActionEvent e)->{
            if(NumID==0){
                NumID=1;//first click to NameID
                reset(db,vb);
            }else if (NumID==1){
                NumID=2;//second click to current level
                reset(db,vb);
            }else if(NumID==2){
                NumID=0;
                reset(db,vb);//third click to NumericalID again
            }
        });

        //textfields for dynamic node insertion
        TextField namID = new TextField();
        TextField numID = new TextField();


        namID.setText("Name ID");
        namID.setMaxWidth(100);

        numID.setText("Numerical ID");
        String num=numID.getText();
        String numericalID=null;

        numID.setMaxWidth(100);
        Button add=new Button("ADD");

        for(int i=0;i<num.length();i++){
            if (isNumeric(num.charAt(i))){
                numericalID+=Character.toString(num.charAt(i));
            }
        }
        String finalNumericalID = numericalID;

        if (finalNumericalID!="") {
            add.setOnAction((ActionEvent e) -> {
                try {
                    SkipNode added = new SkipNode(namID.getText(), 1);
                    try {
                        db.insert(added);
                        System.out.println(finalNumericalID);

                    } catch (BitMissMatchException e1) {
                        System.out.println("Bit MissMatch Exception");
                    } finally {
                        reset(db, (VBox) vbArray[db.getBits() - 1].getParent());
                    }
                } catch (InvalidIDException e1) {
                    System.out.println("NumericalID has to be a number");
                }

            });
        }


        buttons.getChildren().addAll(Switch,namID,numID,add);
        root.getChildren().addAll(buttons,sp);


        for(int k=0;k<db.getBits();k++){//places all the nodes on the scene
            nextLayer(k,db,colors[k]);
        }

    }

    /**
     * this function erases the skipnode visualization that is in the scrollpane sp and replaces it with the updated values
     * @param db with the updated information db (the database array that was initially being visualised) is placed onto the scrollpane
     * @param vb is the vbox that is in the scrollpane that stores all the layers of the given database
     */
    public void reset(SkipNodeDatabase db, VBox vb){//clears the VBox that is placed inside the ScrollPane and resets the scene with the updated settings
        vb.getChildren().clear();
        for(int i=0;i<db.getElements();i++){
            db.get(i).uncheckAll();//all nodes in Database db need to be unchecked in order to be placed on the scene again
        }//checking is used to keep track of the nodes during the placement process

        vbArray =new VBox[db.getBits()];//all the VBoxes that layers would go onto need to be recreated
        for (int i=0; i< db.getBits();i++){
            vbArray[i]=new VBox(20);
            vbArray[i].minHeight(800);
            vbArray[i].minWidth(300);
            vbArray[i].setAlignment(Pos.CENTER_RIGHT);
            vb.getChildren().add(vbArray[i]);

        }

        for(int k=0;k<db.getBits();k++){//all the nodes are replaced
            nextLayer(k,db,colors[k]);
        }
    }

    /**
     * this function takes a skipnode database and visualises the way it's supposed to be set up at a certain using individual buttons
     * @param layer is the level that is to be visualized next
     * @param db is the skipnode that is to be visualized
     * @param color connections at each level have different colors to help with distinguishing
     */
    public void nextLayer(int layer, SkipNodeDatabase db, Color color){
        boolean done=false;
        boolean layered=false;
        boolean setBegin=true;//checks if the current node is the first node in a subset

        int subsets=(int) Math.pow(2,layer);//at each level, there will be 2^layers subsets
        HBox[] subsetArray=new HBox[subsets];//each subset will have its own HBox
        for (int i=0; i<subsets;i++) {
            subsetArray[i] = new HBox(2);
            subsetArray[i].minHeight(800);
            subsetArray[i].minWidth(100);
            subsetArray[i].setAlignment(Pos.CENTER_LEFT);
            vbArray[Math.abs(db.getBits()-1-layer)].getChildren().add(subsetArray[i]);
        }

        int j=0;
        for (int i=0;i<db.getElements();i++){
            int temp=i;//temp and i are indexes of database db
            while(!done) {//done checks if a subset is placed on completely
                if (!db.get(temp).isChecked(layer)) {
                    if(setBegin){
                        for(int m=0; m<temp;m++){//if temp is greater than zero and setBegin is true, it means this node is the first node of a particular subset, but not the first in the database
                            Rectangle connectorLatter= new Rectangle(40,2);//2x as many connectors are needed in place of all the nodes that are before the node that is currently being placed
                            connectorLatter.setStroke(color);
                            connectorLatter.setFill(color);
                            connectorLatter.setVisible(false);

                            Rectangle connectorTemp= new Rectangle(40,2);//first connector is a place holder the nodes that come before the current one, and the second one is the regular connector that we have between all the node representations
                            connectorTemp.setStroke(color);
                            connectorTemp.setFill(color);
                            connectorTemp.setVisible(false);//these connectors are made visible for a clearer visualization

                            subsetArray[j].getChildren().addAll(connectorLatter,connectorTemp);
                        }
                    }

                    subsetArray[j].getChildren().add(create(db.get(temp),layer));//the node representation is placed on the scene
                    db.get(temp).check(layer);//and the node at level #layer is checked so that there wouldn't be any duplicates of this node on level #layer
                    setBegin=false;

                    int m=db.findIndex(db.get(temp).getRight(layer));//m value is updated to the index value of temp's right neighbor
                    if(temp+1<=m && db.get(temp).getRight(layer) != 0) {//if current node and its right neighbor aren't neighbors on level 0, extra placeholders are needed
                        for (int n = temp + 1; n < m; n++) {
                            Rectangle connectorTemp = new Rectangle(40, 2);
                            connectorTemp.setStroke(color);
                            connectorTemp.setFill(color);

                            Rectangle connectorLatter = new Rectangle(40, 2);
                            connectorLatter.setStroke(color);
                            connectorLatter.setFill(color);
                            //placeholder connectors are placed between current node and its right neighbor in order to keep a clean alignment
                            subsetArray[j].getChildren().addAll(connectorTemp,connectorLatter);
                        }
                    }if(db.get(temp).getRight(layer) != 0){//if they are neighbors on level 0, single connectors will suffice to keep the alignment
                        Rectangle connectorLatter = new Rectangle(40, 2);
                        connectorLatter.setStroke(color);
                        connectorLatter.setFill(color);
                        subsetArray[j].getChildren().addAll(connectorLatter);
                    }

                    if(db.get(temp).getRight(layer)==0){
                        layered=true;//if the placement code has been executed(not been skipped), layered is set to true
                    }
                }

                if (db.get(temp).getRight(layer) != 0) {//if the subset placement is complete, done
                    temp = db.findIndex(db.get(temp).getRight(layer));//else, continue on with temp's right neighbor on layer
                } else done = true;//this step is necessary because some nodes are bound to be checked before the outermost for loop executes the placement code on them
            }

            if(j<subsets-1 && layered){//j represents the HBox index which a subset of nodes will be placed upon
                j++;                    //layered and done need to be separated because the placement code can be completely skipped over and done be checked at the same time
                layered=false;
            }done=false;
            setBegin=true;
        }
    }

    /**
     * this function creates buttons that will represent a node at a level
     * @param n is the node that is to be represented by the button
     * @param level is the level of n this button will represent
     * @return
     */
    public Button create(SkipNode n, int level){
        String[] values={String.valueOf(n.getNumericID()),n.getNameID(),String.valueOf(level)};
        Button node = new Button(String.valueOf(values[NumID]));
        node.setMinSize(40,40);
        node.setAlignment(Pos.CENTER);

        node.setOnAction((ActionEvent e)->{
            if(NumID==0){
                node.setText(values[NumID]);
                NumID=1;
            }else if (NumID==1){
                node.setText(values[NumID]);
                NumID=2;
            }else if(NumID==2){
                node.setText(values[NumID]);
                NumID=0;
            }
        });
        return node;

    }

    public boolean isNumeric(char a){
        boolean digit=false;
        if (a=='0'||a=='1'||a=='2'||a=='3'||a=='4'||a=='5'||a=='6'||a=='7'||a=='8'||a=='9'){
            digit=true;
        }
        return digit;
    }

}
