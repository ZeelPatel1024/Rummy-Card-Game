package com.example.clienttemplateforcardsupdate2122;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import socketfx.Constants;
import socketfx.FxSocketClient;
import socketfx.SocketListener;

public class HelloController implements Initializable {
    boolean areReady = false;
    boolean serverReady = false;
    int goalAmount = 0;

    @FXML
    private Button sendButton, ready,discardAc;
    @FXML
    private TextField sendTextField;
    @FXML
    private Button connectButton,butRetCard1,butRetCard2,butRetCard3,butDone,butShow1,butShow2,butShow3,butCheck,butResume;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField hostTextField;

    @FXML
    private Label lblName1, lblName2, lblName3, lblName4, lblMessages,lblYouChoose,lblShowTime;

    @FXML
    private GridPane gPaneServer, gPaneClient;


    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private boolean isConnected, serverUNO = false, clientUNO = false;


    public enum ConnectionDisplayState {
        DISCONNECTED, WAITING, CONNECTED, AUTOCONNECTED, AUTOWAITING
    }

    private FxSocketClient socket;

    private void connect() {
        socket = new FxSocketClient(new FxSocketListener(),
                hostTextField.getText(),
                Integer.valueOf(portTextField.getText()),
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }

    private void displayState(ConnectionDisplayState state) {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isConnected = false;
        displayState(ConnectionDisplayState.DISCONNECTED);
        Runtime.getRuntime().addShutdownHook(new ShutDownThread());
    }

    class ShutDownThread extends Thread {
        @Override
        public void run() {
            if (socket != null) {
                if (socket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                    LOGGER.info("ShutdownHook: Shutting down Server Socket");
                }
                socket.shutdown();
            }
        }
    }

    public HelloController(){
        try {
            back1 = new FileInputStream("src/main/resources/Images/BACK-1.jpg");
            imageBack = new Image(back1);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            System.out.println("message received server");
//            lblMessages.setText(line);
            if (line.equals("ready") && areReady){
                ready.setVisible(false);
            } else if(line.equals("ready")){
                serverReady=true;
            }
            else if(line.equals("dealt")){


//                imgS0.setImage(imageBack);
//                imgS1.setImage(imageBack);
//                imgS2.setImage(imageBack);
//                imgS3.setImage(imageBack);
//                imgS4.setImage(imageBack);
//                imgS5.setImage(imageBack);
//                imgS6.setImage(imageBack);
//                imgS7.setImage(imageBack);

            }else if(line.equals("cCardStart")){
                hand2D.clear();
            }
            else if (line.startsWith("cCards")){
                hand2D.add(new Card(line.substring(6)));

            } else if(line.startsWith("sCardNum")){
                numInServerHand = Integer.parseInt(line.substring(8));
                for (ImageView x: hand1I){
                    x.setImage(null);
                }
                for (ImageView x: hand2I){
                    x.setImage(null);
                }
                for(int i=0;i<numInServerHand;i++){
                    hand1I.get(i).setImage(imageBack);
                }

                printCCards();
                System.out.println(numInServerHand);

            }
            else if(line.equals("serverDrawCard")){
                turn =1;
                draw.setDisable(false);
                discardAc.setDisable(false);
                butRetCard1.setDisable(false);
                butRetCard2.setDisable(false);
                butRetCard3.setDisable(false);
                butDone.setDisable(false);
                butShow1.setDisable(false);
                butShow2.setDisable(false);
                butShow3.setDisable(false);
                butCheck.setDisable(false);
                butResume.setDisable(false);
            }
            else if(line.startsWith("dis")){
                imgDiscard.setImage(null);
                discardPileTop = new Card(line.substring(3));
                try {
                    tempCard = new FileInputStream(discardPileTop.getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                imgDiscard.setImage(imageFront);
            }else if (line.startsWith("diNull")){
                imgDiscard.setImage(null);
            }
            if (line.equals("chosen1000")){

                goalAmount = 1000;
                System.out.println("Server choose a $1,000 game");
                lblYouChoose.setText("$1000");

            }else if (line.equals("chosen10000")){

                goalAmount = 10000;
                System.out.println("Server choose a $10,000 game");
                lblYouChoose.setText("$10000");

            }else if (line.equals("chosen100000")){

                goalAmount = 100000;
                System.out.println("Server choose a $100,000 game");
                lblYouChoose.setText("$100000");
                lblShowTime.setText(" ");

            }

            if (line.equals("cardDiscardedSetBolToFalse")){
                clientDisable = false;
            }

            if (line.equals("serverDone")){
                draw.setDisable(true);
                discardAc.setDisable(true);
            }

            if (line.startsWith("sendCMagicCard")){
                imgMagicCard.setImage(null);
                magicCard = new Card(line.substring(14));
                try {
                    tempCard = new FileInputStream(magicCard.getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                imgMagicCard.setImage(imageFront);
            }

            if (line.startsWith("show1")){
                show1Ins++;

//                for (int i = 0; i < show1.size(); i++) {
//                    show1.get(i).setImage(null);
//                }

                tempShowCard = new Card(line.substring(5));
                try {
                    tempCard = new FileInputStream(tempShowCard.getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                show1.get(show1Ins).setImage(imageFront);
            }

            if (line.startsWith("show2")){
                show2Ins++;

//                for (int i = 0; i < show1.size(); i++) {
//                    show1.get(i).setImage(null);
//                }

                tempShowCard = new Card(line.substring(5));
                try {
                    tempCard = new FileInputStream(tempShowCard.getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                show2.get(show2Ins).setImage(imageFront);
            }

            if (line.startsWith("show3")){
                show3Ins++;

//                for (int i = 0; i < show1.size(); i++) {
//                    show1.get(i).setImage(null);
//                }

                tempShowCard = new Card(line.substring(5));
                try {
                    tempCard = new FileInputStream(tempShowCard.getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                show3.get(show3Ins).setImage(imageFront);
            }

            ///////////////////////////////////////

            if (line.startsWith("s1Server")){
                show1InsServ++;

                tempShowCard = new Card(line.substring(8));
                try {
                    tempCard = new FileInputStream(tempShowCard.getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                show1.get(show1InsServ).setImage(imageFront);
            }

            if (line.equals("ClearShow1")){
                for (int i = 0; i < show1.size(); i++) {
                    show1.get(i).setImage(null);
                }
                show1InsServ = -1;
            }
/////////////////////////////////////////////////////////////////////////////
            if (line.startsWith("s2Server")){
                show2InsServ++;

                tempShowCard = new Card(line.substring(8));
                try {
                    tempCard = new FileInputStream(tempShowCard.getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                show2.get(show2InsServ).setImage(imageFront);
            }

            if (line.equals("secondClearShow1")){
                for (int i = 0; i < show2.size(); i++) {
                    show2.get(i).setImage(null);
                }
                show2InsServ = -1;
            }

            /////////////////////////////////////////////////////////////////////////////
            if (line.startsWith("s3Server")){
                show3InsServ++;

                tempShowCard = new Card(line.substring(8));
                try {
                    tempCard = new FileInputStream(tempShowCard.getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                show3.get(show3InsServ).setImage(imageFront);
            }

            if (line.equals("forthClearShow1")){
                for (int i = 0; i < show3.size(); i++) {
                    show3.get(i).setImage(null);
                }
                show3InsServ = -1;
            }

            if (line.equals("ClearedShow1Here")){
                clearShow1();
                show1Ins = -1;
            }

            if (line.equals("ClearedShow2Here")){
                clearShow2();
                show2Ins = -1;
            }

            if (line.equals("ClearedShow3Here")){
                clearShow3();
                show3Ins = -1;
            }

            if (line.equals("ServerWins")){
                lblMessages.setText("YOU LOST!");
            }
            if (line.equals("doneTime")){
                lblMessages.setText("TIME RAN OUT, BOTH LOSE!");
            }
            if (line.equals("serverLostTime")){
                lblMessages.setText("YOU WON!");
            }
            if (line.equals("clientLostTime")){
                lblMessages.setText("TIME RAN OUT, YOU LOSE!");
            }
            if (line.equals("clientWins")){
                lblMessages.setText("YOU WIN!");
            }
            if (line.startsWith("timeAt")){
                clientTime = Integer.parseInt(line.substring(6));
                lblShowTime.setText(clientTime +"");
            }

            if (line.startsWith("inputTimeLVL3")){
                lblShowTime.setText(line.substring(13));
            }


        }

        int clientTime = 0;
        int show1Ins = -1;
        int show2Ins = -1;
        int show3Ins = -1;

        int show1InsServ = -1;
        int show2InsServ = -1;
        int show3InsServ = -1;
        boolean clientAddToFirst3Set = false;
        boolean clientAddToSecond3Set = false;
        boolean clientAddTo4Set = false;

        public void clearShow1(){
            for (int i = 0; i < show1.size(); i++) {
                show1.get(i).setImage(null);
            }
        }
        public void clearShow2(){
            for (int i = 0; i < show2.size(); i++) {
                show2.get(i).setImage(null);
            }
        }
        public void clearShow3(){
            for (int i = 0; i < show3.size(); i++) {
                show3.get(i).setImage(null);
            }
        }

        public void printCCards(){
            for (int i=0;i<hand2D.size();i++){
                System.out.println(hand2D.get(i).getCardPath());
                try {
                    tempCard = new FileInputStream(hand2D.get(i).getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                hand2I.get(i).setImage(imageFront);
            }
        }
        @Override
        public void onClosedStatus(boolean isClosed) {

        }
    }


    @FXML
    private void handleReady(ActionEvent event) {
//        if (!sendTextField.getText().equals("")) {
//            String x = sendTextField.getText();
//            socket.sendMessage(x);
//            System.out.println("sent message client");
//        }
        areReady=true;
        socket.sendMessage("ready");
        if (serverReady){
            ready.setVisible(false);
        }else{
            ready.setDisable(true);
        }

        hand1I.add(imgS0);
        hand1I.add(imgS1);
        hand1I.add(imgS2);
        hand1I.add(imgS3);
        hand1I.add(imgS4);
        hand1I.add(imgS5);
        hand1I.add(imgS6);
        hand1I.add(imgS7);
        hand1I.add(imgS8);
        hand1I.add(imgS9);
        hand1I.add(imgS10);
        hand1I.add(imgS12);

        show1.add(imgF0);
        show1.add(imgF1);
        show1.add(imgF2);

        show2.add(imgF3);
        show2.add(imgF4);
        show2.add(imgF5);

        show3.add(imgF6);
        show3.add(imgF7);
        show3.add(imgF8);
        show3.add(imgF9);


        hand2I.add(imgC0);
        hand2I.add(imgC1);
        hand2I.add(imgC2);
        hand2I.add(imgC3);
        hand2I.add(imgC4);
        hand2I.add(imgC5);
        hand2I.add(imgC6);
        hand2I.add(imgC7);
        hand2I.add(imgC8);
        hand2I.add(imgC9);
        hand2I.add(imgC10);
        hand2I.add(imgC12);
    }

    public void handleDraw(ActionEvent actionEvent) {
        if (turn ==1){
            turn =0;
            draw.setDisable(true);
            socket.sendMessage("clientDrawCard");

        }
    }

    @FXML
    public void btnShowFirst3Set(){
        socket.sendMessage("cClickedOnShowFirst3Set");
    }
    @FXML
    public void btnShowSecond3Set(){
        socket.sendMessage("cClickedOnShowSecond3Set");
    }

    @FXML
    public void btnShowFinal4Set(){
        socket.sendMessage("cClickedOnShow4Set");
    }

    @FXML
    public void btnCheckHand(){
        socket.sendMessage("checkCards");
    }

    @FXML
    public void btnReturnCards3SetOne(){
        socket.sendMessage("returnShow1");
    }

    @FXML
    public void btnReturnCards3SetTwo(){
        socket.sendMessage("returnShow2");
    }

    @FXML
    public void btnReturnCards4Set(){
        socket.sendMessage("returnShow3");
    }


    boolean clientDisable = false;

    @FXML
    public void btnDone(){
        draw.setDisable(true);
        discardAc.setDisable(true);
        clientDone = true;
        socket.sendMessage("clientDone");
    }

    @FXML
    public void btnDiscardCard(){
        clientDisable = true;
    }

    int tempSpot = -1;
    int newSpot = -1;

    int clickedOnNewSpot = 0;
    boolean clientDone = false;

    public void handleClientImgClicked(MouseEvent mouseEvent) {
        System.out.println("click");

        imgClicked = GridPane.getColumnIndex((ImageView) mouseEvent.getSource());
        System.out.println("imgClicked " + imgClicked);

        socket.sendMessage("cic"+ imgClicked);
        if (clientDisable == true){
            socket.sendMessage("clientDisTrue");
            turn = 0;
            draw.setDisable(true);
            butRetCard1.setDisable(true);
            butRetCard2.setDisable(true);
            butRetCard3.setDisable(true);
            butDone.setDisable(true);
            butShow1.setDisable(true);
            butShow2.setDisable(true);
            butShow3.setDisable(true);
            butCheck.setDisable(true);
            butResume.setDisable(true);
            socket.sendMessage("endCTurn");
        }else{

            if (clientDone == false){
                if (clickedOnNewSpot == 0){
                    tempSpot = imgClicked;
                    clickedOnNewSpot++;
                }else{
                    newSpot = imgClicked;
                    hand2D.add(newSpot,hand2D.remove(tempSpot));
                    clickedOnNewSpot = 0;
                    socket.sendMessage("preClear");
                    for (int i = 0; i < hand2D.size(); i++) {
                        socket.sendMessage("newHand2D" + hand2D.get(i).getCardName());
                    }
                }

                for (int i=0;i<hand2D.size();i++){
                    System.out.println(hand2D.get(i).getCardPath());
                    try {
                        tempCard = new FileInputStream(hand2D.get(i).getCardPath());
                        imageFront = new Image(tempCard);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                    hand2I.get(i).setImage(imageFront);
                }
            }


        }



        System.out.println("message sent");
    }

    @FXML
    public void btnResume(){
        clientDone = false;
        turn = 0;
        draw.setDisable(true);
        butRetCard1.setDisable(true);
        butRetCard2.setDisable(true);
        butRetCard3.setDisable(true);
        butDone.setDisable(true);
        butShow1.setDisable(true);
        butShow2.setDisable(true);
        butShow3.setDisable(true);
        butCheck.setDisable(true);
        butResume.setDisable(true);
        socket.sendMessage("endCTurn");
        socket.sendMessage("sResume");
    }


    @FXML
    public void handleFinalShowGridImgClicked3SetOne(){
        socket.sendMessage("clickedOn3SetOne");
    }
    @FXML
    public void handleFinalShowGridImgClicked3SetTwo(){
        socket.sendMessage("secondclickedOn3Set");
    }
    @FXML
    public void handleFinalShowGridImgClicked4Set(){
        socket.sendMessage("secondclickedOn4Set");
    }

    @FXML
    public void clickedOnImgDiscard(){
        socket.sendMessage("preClear");
        for (int i = 0; i < hand2D.size(); i++) {
            socket.sendMessage("newNewCHand" + hand2D.get(i).getCardName());
        }

        socket.sendMessage("cClickedOnDiscard");



//        for (int i = 0; i < hand2D.size(); i++) {
//            socket.sendMessage("newHand2D" + hand2D.get(i).getCardName());
//        }
    }

    @FXML
    Button draw;


    @FXML
    private void handleConnectButton(ActionEvent event) {
        connectButton.setDisable(true);
        ready.setDisable(false);
        displayState(ConnectionDisplayState.WAITING);
        connect();
    }

    @FXML
    private ImageView imgS0,imgS1,imgS2,imgS3,imgS4,imgS5,imgS6,imgS7,imgS8,imgS9,imgS10,imgS12,
            imgC0,imgC1,imgC2,imgC3,imgC4, imgC5,imgC6,imgC7,imgC8,imgC9,imgC10,imgC12,imgDiscard,imgMagicCard,imgF0,imgF1,imgF2,imgF3,imgF4,imgF5,imgF6,imgF7,imgF8,imgF9;
    FileInputStream back1,tempCard;
    Image imageBack;
    Image imageFront;
    List<ImageView> hand1I = new ArrayList<>();
    List<ImageView> hand2I = new ArrayList<>();
    List<Card> hand1D = new ArrayList<>();
    List<Card> hand2D = new ArrayList<>();
    Card discardPileTop;
    Card magicCard;
    Card tempShowCard;
    int numInServerHand=0;
    private int turn = 0;
    int imgClicked;

    List<Card> clientSetOf3One = new ArrayList<>();
    List<Card> clientSetOf3Two = new ArrayList<>();
    List<Card> clientSetOf4 = new ArrayList<>();

    List<ImageView> show1 = new ArrayList<>();
    List<ImageView> show2 = new ArrayList<>();
    List<ImageView> show3 = new ArrayList<>();

}