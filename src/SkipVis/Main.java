package SkipVis;
import Exceptions.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class Main extends Application{
    /**
     * The container that includes the nodes, connectors and levels in visualization
     */
    private static VBox[] vbArray;
    private static int NumID=0;

    /**
     * Colors that are used in connectors for different levels
     */
    private Color[] colors={Color.DARKORANGE, Color.BLUEVIOLET,Color.FUCHSIA,Color.CHARTREUSE,Color.FORESTGREEN,Color.HONEYDEW,Color.BROWN,Color.CORAL,Color.DEEPSKYBLUE,Color.GREENYELLOW,Color.HOTPINK,Color.SALMON};

    /**
     * Width of the main window (primaryStage)
     * @see Stage
     */
    private  double width = 800;
    /**
     * Height of the main window (primaryStage)
     * @see Stage
     */
    private  double height = 400;

    /**
     * Minimum width of the buttons that represent nodes in a Skip Graph.
     */
    private double maxButtonsize=40;

    /**
     * Main start method of the program.
     *
     * {@link SkipNodeDatabase} is configured by reading from database lineup file (plain text file). According to
     * the configuration, visualization of the Skip Graph is done.
     *
     * @param primaryStage
     * @throws Exception
     * @see SkipNodeDatabase
     * @see #setScene(ScrollPane, VBox, HBox, SkipNodeDatabase)
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox root=new VBox();
        //TextFields, "Switch All" and "Add" buttons  will be placed in this container
        HBox buttons=new HBox(10);
        //root container will be embedded inside a ScrollPane
        ScrollPane sp=new ScrollPane();

        //Read from plaintext file and configure SkipNodeDatabase
        SkipNodeDatabase db1= new SkipNodeDatabase();
        db1.read("lineup.txt",3);
        try{

            /*find the longest string in NameId,NumericalId among all of the nodes
            in order to determine the button size that will be used to
            represent a node. This block is necessary for proper alignment.*/
            for(int i=0;i<SkipNodeDatabase.elements;i++){
                SkipNode sn = db1.get(i);
                String[] values={String.valueOf(sn.getNumericID()),sn.getNameID()};
                int maxsize = Math.max(values[0].length(),values[1].length());
                int temp = 11+10*maxsize;
                if(temp>maxButtonsize){
                    maxButtonsize=temp;
                }
            }

            setScene(sp,root,buttons,db1);
        }catch (BitMissMatchException e3){
            System.err.println("Bit MissMatch");

        }

        primaryStage.setTitle("Skip Graph Visualization");
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);

        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();

        //add change listeners to primaryStage to detect the change in the size of the window
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            height = newValue.doubleValue();
            root.setPrefSize(width,height);
            sp.setPrefSize(width,height);
        });
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            width = newValue.doubleValue();
            root.setPrefSize(width,height);
            sp.setPrefSize(width,height);
        });


    }


    public static void main(String[] args) {launch(args);}

    /**
     *Puts everything that is necessary for the visualization and controlling the visualization on the scene.
     *
     * @param sp the scrollpane the database visualization would go onto
     * @param root is the main box everything goes in
     * @param buttons is the HBox that all the buttons ie. add, switch goes on
     * @param db is the database that is to be visualized
     * @see #nextLayer(int, SkipNodeDatabase, Color)
     * @see #reset(SkipNodeDatabase, VBox)
     *
     */
    public void setScene(ScrollPane sp,VBox root, HBox buttons,SkipNodeDatabase db) throws BitMissMatchException{
        //VBox vb (where we put the nodes, levels and visualization)
        VBox vb=new VBox(60);
        vb.setAlignment(Pos.BOTTOM_LEFT);
        vb.setPrefSize(width,height);

        //embed the vb into our scrollpane
        sp.setContent(vb);
        //each layer should have its own Vbox and we need the number of digits present in the length of nameID(for example 3 bits)
        vbArray =new VBox[db.getBits()];

        for (int i=0; i< db.getBits();i++){//there will be #bits layers
            vbArray[i]=new VBox(20);
            vbArray[i].minHeight(width);
            vbArray[i].minWidth(height/2);
            vbArray[i].setAlignment(Pos.CENTER_RIGHT);
            //add those small VBoxes that corresponds to each level to the main Vbox
            vb.getChildren().add(vbArray[i]);
        }

        //"Switch All" Button
        Button Switch =new Button("SWITCH ALL");//Switch button switches the values on the buttons
        Switch.setOnAction((ActionEvent e)->{
            //NumID is defined as a global variable which is used as a pseudo-counter variable to switch between nameid,
            // level and numericalid representation of the nodes
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

        //Textfields for dynamic node insertion ("Numerical ID" and "Name ID")
        TextField namID = new TextField();
        TextField numID = new TextField();
        namID.setPromptText("Name ID");
        namID.setMaxWidth(100);
        numID.setPromptText("Numerical ID");
        numID.setMaxWidth(100);

        //"Add" Button
        Button add=new Button("ADD");
        add.setOnAction((ActionEvent e) -> {
            try {
                //get the text in numerical ID field
                String num=numID.getText();
                String numericalID="";
                for(int i=0;i<num.length();i++){
                    if (isNumeric(num.charAt(i))){
                        numericalID+=Character.toString(num.charAt(i));
                        }
                }
                String finalNumericalID = numericalID;
                if(!finalNumericalID.equals("") && finalNumericalID.length() == num.length()){ //checking for whether it is null or not is not sufficient we should check for its length
                    SkipNode added = new SkipNode(namID.getText(), Integer.parseInt(finalNumericalID));
                    try {
                        db.insert(added);

                        //if the node that is added has longer string than what we have change the sizes of all buttons
                        String[] values={String.valueOf(added.getNumericID()),added.getNameID()};
                        int maxsize = Math.max(values[0].length(),values[1].length());
                        int temp = 10+10*maxsize;
                        if(temp>maxButtonsize){
                            maxButtonsize=temp;
                        }

                    } catch (BitMissMatchException e1) {
                        System.err.println("Bit MissMatch Exception");
                    } finally {
                        reset(db, (VBox) vbArray[db.getBits() - 1].getParent());
                    }
                }else{
                    System.err.println("Error in numericalID field");
                }
            }catch (InvalidIDException e1) {
                System.err.println("NumericalID has to be a number");
            }

        });

        //add those textfields and buttons to our HBox called "buttons", name might be misleading!
        buttons.getChildren().addAll(Switch,namID,numID,add);

        //put those buttons(HBox) and our scrollpane(includes the main Vbox which contains a Vbox for each level) to our "ROOT"(main) VBOX
        root.getChildren().addAll(buttons,sp);

        //places all the nodes on the scene
        for(int k=0;k<db.getBits();k++){
            nextLayer(k,db,colors[k]);
        }

    }

    /**
     * This function erases the skipnode visualization that is in the {@link ScrollPane} sp and replaces it with the updated values
     *
     * @param db with the updated information db (the database array that was initially being visualised) is placed onto the scrollpane
     * @param vb is the vbox that is in the scrollpane that stores all the layers of the given database
     * @see #nextLayer(int, SkipNodeDatabase, Color)
     *
     */
    public void reset(SkipNodeDatabase db, VBox vb){//clears the VBox that is placed inside the ScrollPane and resets the scene with the updated settings
        vb.getChildren().clear();
        for(int i=0;i<db.getElements();i++){
            db.get(i).uncheckAll();//all nodes in Database db need to be unchecked in order to be placed on the scene again
        }//checking is used to keep track of the nodes during the placement process

        //all the VBoxes that layers would go onto need to be recreated
        vbArray =new VBox[db.getBits()];
        for (int i=0; i< db.getBits();i++){
            vbArray[i]=new VBox(20);
            vbArray[i].minHeight(width);
            vbArray[i].minWidth(height/2);
            vbArray[i].setAlignment(Pos.CENTER_RIGHT);
            vb.getChildren().add(vbArray[i]);

        }

        //all the nodes are replaced
        for(int k=0;k<db.getBits();k++){
            nextLayer(k,db,colors[k]);
        }
    }

    /**
     * Takes a {@link SkipNodeDatabase} and visualises the indicated layer (level) by given color.
     *
     * Each node is illustrated by a {@link Button} whose height is 40 pixels, but width is determined by {@link #maxButtonsize}. All
     * nodes on the same layer are connected to each other with {@param color} and each node is created by the method {@link #create(SkipNode, int)}.
     *
     *
     * @param layer is the level that is to be visualized next
     * @param db is the skipnode that is to be visualized
     * @param color connections at each level have different colors to help with distinguishing
     * @see #create(SkipNode, int)
     */
    public void nextLayer(int layer, SkipNodeDatabase db, Color color){
        boolean done=false;
        boolean layered=false;
        boolean setBegin=true;//checks if the current node is the first node in a subset

        //number of subsets in the given layer
        int subsets=(int) Math.pow(2,layer);//at each level, there will be 2^layers subsets
        //we need 2^layer number of HBoxes
        HBox[] subsetArray=new HBox[subsets];//each subset in a layer will have its own HBox
        //assigning HBox object to each element of subsetArray, and add those Hboxes to corresponding level(VBOX)
        for (int i=0; i<subsets;i++) {
            subsetArray[i] = new HBox(2);
            subsetArray[i].minHeight(width);
            subsetArray[i].minWidth(height/4);
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
                            Rectangle connectorLatter= new Rectangle(maxButtonsize,2);//2x as many connectors are needed in place of all the nodes that are before the node that is currently being placed
                            connectorLatter.setStroke(color);
                            connectorLatter.setFill(color);
                            connectorLatter.setVisible(false);

                            Rectangle connectorTemp= new Rectangle(maxButtonsize,2);//first connector is a place holder the nodes that come before the current one, and the second one is the regular connector that we have between all the node representations
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
                            Rectangle connectorTemp = new Rectangle(maxButtonsize, 2);
                            connectorTemp.setStroke(color);
                            connectorTemp.setFill(color);

                            Rectangle connectorLatter = new Rectangle(maxButtonsize, 2);
                            connectorLatter.setStroke(color);
                            connectorLatter.setFill(color);
                            //placeholder connectors are placed between current node and its right neighbor in order to keep a clean alignment
                            subsetArray[j].getChildren().addAll(connectorTemp,connectorLatter);
                        }
                    }if(db.get(temp).getRight(layer) != 0){//if they are neighbors on level 0, single connectors will suffice to keep the alignment
                        Rectangle connectorLatter = new Rectangle(maxButtonsize, 2);
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
     * Creates buttons that will represent a node at a level with a {@link Button}.
     *
     * @param n is the node that is to be represented by the button
     * @param level is the level of n this button will represent
     * @return Button is the visualized version of a node
     * @see Tooltip is used to display numericalID, nameID and level (layer)
     * @see Button
     */
    public Button create(SkipNode n, int level){
        //obtain numericalID, nameID and level of a node
        String[] values={String.valueOf(n.getNumericID()),n.getNameID(),String.valueOf(level)};
        //visualize the node as a button, and its text will depend on NumID (counter for switch button as I discussed early)

        Button node = new Button(String.valueOf(values[NumID]));

        //min button size should depend on the size of numericalId, nameId and level to solve the display problem
        node.setMinSize(maxButtonsize+1,40);

        //In order to display the content of a node when mouse is hovered over the button
        node.setTooltip(new Tooltip("Numerical Id: "+values[0]+"\nName Id: "+values[1]+"\nLevel: "+values[2]));

        //What happens when a Node is clicked
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
