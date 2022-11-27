package com.example.servertemplateforcardsupdate2122;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
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
import socketfx.FxSocketServer;
import socketfx.SocketListener;

public class HelloController implements Initializable {
    boolean areReady = false;
    boolean clientReady = false;

    int goalAmount = 0;

    @FXML
    private Button sendButton,ready;
    @FXML
    private TextField sendTextField;
    @FXML
    private Button connectButton,btnOneThousand,btnTenThousand,btnHundThousand,discardCardAtr,butRetCardToHand1,butRetCardToHand2,butRetCardToHand3,butDone,butShow1,butShow2,butShow3,butCheck,butResume;
    @FXML
    private TextField portTextField;
    @FXML
    private Label lblMessages,lblYouChoose,lblShowTime;

    private FxSocketServer socket;


    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private boolean isConnected;
    private int counter = 0;
    private String color;


    @FXML
    public void clickOnOneThousandDollars(){
        goalAmount = 1000;
        level1Timer = System.nanoTime();
        socket.sendMessage("chosen1000");
        lblYouChoose.setText("$1000");
        btnTenThousand.setDisable(true);
        btnHundThousand.setDisable(true);
    }

    @FXML
    public void clickOnTenThousandDollars(){
        goalAmount = 10000;
        level2Timer = System.nanoTime();
        socket.sendMessage("chosen10000");
        lblYouChoose.setText("$10000");
        btnOneThousand.setDisable(true);
        btnHundThousand.setDisable(true);
    }

    @FXML
    public void clickOnHundredThousandDollars(){
        goalAmount = 100000;
        lblShowTime.setText(" ");
        level3Timer = System.nanoTime();
        socket.sendMessage("chosen100000");
        lblYouChoose.setText("$100000");
        btnTenThousand.setDisable(true);
        btnOneThousand.setDisable(true);
    }


    public enum ConnectionDisplayState {

        DISCONNECTED, WAITING, CONNECTED, AUTOCONNECTED, AUTOWAITING
    }

    private void connect() {
        System.out.println("connect");
        ready.setDisable(false);
        socket = new FxSocketServer(new FxSocketListener(),
                Integer.valueOf(portTextField.getText()),
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }

    private void displayState(ConnectionDisplayState state) {
//        switch (state) {
//            case DISCONNECTED:
//                connectButton.setDisable(false);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
//                break;
//            case WAITING:
//            case AUTOWAITING:
//                connectButton.setDisable(true);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
//                break;
//            case CONNECTED:
//                connectButton.setDisable(true);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
//                break;
//            case AUTOCONNECTED:
//                connectButton.setDisable(true);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
//                break;
//        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("initialize");
        isConnected = false;
        displayState(ConnectionDisplayState.DISCONNECTED);




        Runtime.getRuntime().addShutdownHook(new ShutDownThread());

        /*
         * Uncomment to have autoConnect enabled at startup
         */
//        autoConnectCheckBox.setSelected(true);
//        displayState(ConnectionDisplayState.WAITING);
//        connect();
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
    @FXML
    private void handleConnectButton(ActionEvent event) {
        System.out.println("handleConnectButton");

        connectButton.setDisable(true);

        displayState(ConnectionDisplayState.WAITING);
        connect();
    }
    //****************************************************************
    class FxSocketListener implements SocketListener {

        //client Sends
        @Override
        public void onMessage(String line) {
            System.out.println("message received client");
            lblMessages.setText(line);
            if (line.equals("ready") && areReady){
                deal.setDisable(false);
                ready.setVisible(false);

            }else if(line.equals("ready")){
                clientReady=true;

            }else if(line.equals(("clientDrawCard"))){

                clientDrawCard();
            }
            else if(line.startsWith("cic")){
                clientImgClicked = Integer.parseInt(line.substring(3));//index in client hand that it clicked
                System.out.println("message received");

                if (clientDone == true){//thirdf thing that happebns when the clent clicks on tgheir image

                    if (clientAddToFirst3Set == true){

                        if (clientSetOf3One.size() <= 3 && clientAddToFirst3Set == true){
                            clientSetOf3One.add(hand2D.get(clientImgClicked));//card is added to this array
                            hand2D.remove(clientImgClicked);

                            printServerScreen();
                            sendHandClient();
                        }


                    }else if (clientAddToSecond3Set == true){

                        if (clientSetOf3Two.size() <= 3 && clientAddToSecond3Set == true){
                            clientSetOf3Two.add(hand2D.get(clientImgClicked));//card is added to this array
                            hand2D.remove(clientImgClicked);

                            printServerScreen();
                            sendHandClient();
                        }

                    }else if (clientAddTo4Set == true){

                        if (clientSetOf3Two.size() <= 4 && clientAddTo4Set == true){
                            clientSetOf4.add(hand2D.get(clientImgClicked));//card is added to this array
                            hand2D.remove(clientImgClicked);

                            printServerScreen();
                            sendHandClient();
                        }

                    }

                }

            }

            if (line.equals("clickedOn3SetOne")){//forth thing that happens when teh 3 set is clicked iwth the 3 selcted cards
                clientFinalShowGirdClicked3SetOne();
                clientAddToFirst3Set = false;
//                clientDisplayFinal();
            }

            if (line.equals("secondclickedOn3Set")){//forth thing that happens when teh 3 set is clicked iwth the 3 selcted cards
                clientFinalShowGirdClicked3SetTwo();
                clientAddToSecond3Set = false;
//                clientDisplayFinal();
            }

            if (line.equals("secondclickedOn4Set")){//forth thing that happens when teh 3 set is clicked iwth the 3 selcted cards
                clientFinalShowGirdClicked4Set();
                clientAddTo4Set = false;
//                clientDisplayFinal();
            }



            if (line.equals("clientDisTrue")){
                doClientDiscard();
            }

            if (line.equals("clientDone")){
                draw.setDisable(true);
                discardCardAtr.setDisable(true);
                deal.setDisable(true);
                clientDone = true;//first thing that happens when client presses done to server

            }

            if (line.equals("cClickedOnDiscard")){
                clientClickedOnDiscardPile();
            }
            if (line.equals("preClear")){
                hand2D.clear();
            }
            if (line.startsWith("newNewCHand")){

                hand2D.add(new Card(line.substring(11)));
//                sendHandClient();
            }

            if (line.startsWith("newHand2D")){
//                hand2D.clear();
                hand2D.add(new Card(line.substring(9)));
            }


            if (line.equals("cClickedOnShowFirst3Set")){
                clientClickedOnBtnShowFirst3Set();//second thing when this buttob us clicied to show
            }
            if (line.equals("cClickedOnShowSecond3Set")){
                clientClickedOnBtnShowSecond3Set();
            }

            if (line.equals("cClickedOnShow4Set")){
                clientClickedOnBtnShow4Set();
            }

            if (line.equals("returnShow1")){
                btnReturnClientCardsToHandOne();
            }
            if (line.equals("returnShow2")){
                btnReturnClientCardsToHandTwo();
            }
            if (line.equals("returnShow3")){
                btnReturnClientCardsToHandThree();
            }

            if (line.equals("checkCards")){
                checkClientsHand();
            }

            if (line.equals("endCTurn")){
                turn = 0;
                butRetCardToHand1.setDisable(false);
                butRetCardToHand2.setDisable(false);
                butRetCardToHand3.setDisable(false);
                butDone.setDisable(false);
                butShow1.setDisable(false);
                butShow2.setDisable(false);
                butShow3.setDisable(false);
                butCheck.setDisable(false);
                butResume.setDisable(false);
                discardCardAtr.setDisable(false);
                draw.setDisable(false);
            }

        }

        @Override
        public void onClosedStatus(boolean isClosed) {

        }
    }

    @FXML
    public void btnResume(){
        isDone = false;
        turn = 1;
        butRetCardToHand1.setDisable(true);
        butRetCardToHand2.setDisable(true);
        butRetCardToHand3.setDisable(true);
        butDone.setDisable(true);
        butShow1.setDisable(true);
        butShow2.setDisable(true);
        butShow3.setDisable(true);
        butCheck.setDisable(true);
        butResume.setDisable(true);
        discardCardAtr.setDisable(true);
        draw.setDisable(true);
        socket.sendMessage("cResume");
    }


    @FXML
    private void handleSendMessageButton(ActionEvent event) {
        if (!sendTextField.getText().equals("")) {
            socket.sendMessage(sendTextField.getText());
            System.out.println("Message sent client");
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

        btnOneThousand.setDisable(false);
        btnTenThousand.setDisable(false);
        btnHundThousand.setDisable(false);

        socket.sendMessage("ready");
        if (clientReady){
            ready.setVisible(false);
            deal.setDisable(false);

        }else{
            ready.setDisable(true);
        }
    }

    Card magicCard;

    @FXML
    private void handleDeal(ActionEvent event){
        start();
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

        show1.add(imgf0);
        show1.add(imgf1);
        show1.add(imgf2);

        show2.add(imgf3);
        show2.add(imgf4);
        show2.add(imgf5);

        show3.add(imgf6);
        show3.add(imgf7);
        show3.add(imgf8);
        show3.add(imgf9);



        imgC0.setImage(imageBack);
        imgC1.setImage(imageBack);
        imgC2.setImage(imageBack);
        imgC3.setImage(imageBack);
        imgC4.setImage(imageBack);
        imgC5.setImage(imageBack);
        imgC6.setImage(imageBack);
        imgC7.setImage(imageBack);
        imgC8.setImage(imageBack);
        imgC9.setImage(imageBack);
        imgC10.setImage(imageBack);


        deck.clear();
        //populate deck
        for(int i = 1;i<14;i++)
        {

            deck.add(new Card("C" +Integer.toString(i+1)));
            deck.add(new Card("S"+Integer.toString(i+1)));
            deck.add(new Card("H"+Integer.toString(i+1)));
            deck.add(new Card("D"+Integer.toString(i+1)));


        }

        int y1 = (int)(Math.random()*(52));
        magicCard = deck.get(y1);
        System.out.println("Magic Card " + magicCard.getCardNumber());
        try {
            tempCard = new FileInputStream(deck.get(y1).getCardPath());
            imageFront = new Image(tempCard);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        imgMagicCard.setImage(imageFront);
        socket.sendMessage("sendCMagicCard" + deck.get(y1).getCardName());
        deck.remove(y1);


        //deal each hand
        hand2D.clear();
        hand1D.clear();
        //deal server hand
        numCardsInDeck = 51;
        System.out.println("Server hand");
        if (goalAmount == 100000){
            for(int i = 0;i<3;i++)
            {
                int y = (int)(Math.random()*(51-i));
                try {
                    tempCard = new FileInputStream(deck.get(y).getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                hand1I.get(i).setImage(imageFront);
                System.out.println(deck.get(y).getCardPath());
                hand1D.add(deck.remove(y));
                numCardsInDeck--;

            }
            //deal client hand
            System.out.println("Client hand");
            for(int i = 0;i<3;i++)
            {
                int y = (int)(Math.random()*(48-i));
                System.out.println(deck.get(y).getCardPath());
                hand2D.add(deck.remove(y));
                numCardsInDeck--;

            }
        }else{
            for(int i = 0;i<11;i++)
            {
                int y = (int)(Math.random()*(51-i));
                try {
                    tempCard = new FileInputStream(deck.get(y).getCardPath());
                    imageFront = new Image(tempCard);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                hand1I.get(i).setImage(imageFront);
                System.out.println(deck.get(y).getCardPath());
                hand1D.add(deck.remove(y));
                numCardsInDeck--;

            }
            //deal client hand
            System.out.println("Client hand");
            for(int i = 0;i<11;i++)
            {
                int y = (int)(Math.random()*(40-i));
                System.out.println(deck.get(y).getCardPath());
                hand2D.add(deck.remove(y));
                numCardsInDeck--;

            }
        }

        System.out.println("Number of cards left in deck " + deck.size());

        socket.sendMessage("dealt");
        sendHandClient();
        lblMessages.setText("server draw a card");
        turn =0;
//        //deal discard
//        discardPile.add(deck.get((int)(Math.random()*numCardsInDeck)));
//        numCardsInDeck--;
//        try {
//            tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
//            imageFront = new Image(tempCard);
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }
//        imgDiscard.setImage(imageFront);
//        sendDiscardPile();
        draw.setDisable(false);

    }

    public void drawDiscardPile(){
        if (discardPile.size() <= 0){
            imgDiscard.setImage(null);
            sendDiscardPile();
            draw.setDisable(false);
        }else{
            try {
                tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
                imageFront = new Image(tempCard);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            imgDiscard.setImage(imageFront);
            sendDiscardPile();
            draw.setDisable(false);
        }

    }

    public void reDrawDiscardPile(){
        if (discardPile.size() == 0){
            imgDiscard.setImage(null);
        }
        for (int i = 0; i < discardPile.size(); i++) {
            try {
                tempCard = new FileInputStream(discardPile.get(i).getCardPath());
                imageFront = new Image(tempCard);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            imgDiscard.setImage(imageFront);
            sendDiscardPile();
//            draw.setDisable(false);
        }
    }

    public void clickedOnDiscardPile(){
        hand1D.add(discardPile.remove(discardPile.size()-1));
        drawDiscardPile();
//        reDrawDiscardPile();
        printServerScreen();

    }

    public void clientClickedOnDiscardPile(){
        hand2D.add(discardPile.remove(discardPile.size()-1));
        drawDiscardPile();
        printServerScreen();
        sendHandClient();

    }

    public void handleDraw(){
        if (turn ==0){
            if (hand1D.size() < 12 && deck.size() > 0){
                int cardDraw = (int)(Math.random()*numCardsInDeck);
                hand1D.add(hand1D.size(),deck.get(cardDraw));
                deck.remove(cardDraw);
                numCardsInDeck--;
                printServerScreen();
                draw.setDisable(true);
            }

        }else{
            System.out.println("clients turn");
        }

    }

    public void clientDrawCard(){
        if (hand2D.size() < 12 && deck.size() > 0){
            int cardDraw = (int)(Math.random()*numCardsInDeck);
            hand2D.add(hand2D.size(),deck.get(cardDraw));
            deck.remove(cardDraw);
            numCardsInDeck--;
//            turn = 0;
            draw.setDisable(false);
            printServerScreen();
            sendHandClient();
        }

    }


    boolean goodCheck1 = true;
    boolean goodCheckCons1 = true;


    boolean goodCheck2 = true;
    boolean goodCheckCons2 = true;


    boolean goodCheck3 = true;
    boolean goodCheckCons3 = true;

    ///////////////////////////////

    boolean goodCheck1Cli = true;
    boolean goodCheckCons1Cli  = true;


    boolean goodCheck2Cli  = true;
    boolean goodCheckCons2Cli  = true;


    boolean goodCheck3Cli  = true;
    boolean goodCheckCons3Cli  = true;

    public void checkClientsHand(){
        if (goalAmount == 1000){

            int checkValue = 0;
            checkIfAllTheSame(clientSetOf3One,true,checkValue);

            int checkValue2 = 0;
            checkIfAllTheSame(clientSetOf3Two,true,checkValue2);

            int checkValue3 = 0;
            checkIfAllTheSame(clientSetOf4,true,checkValue3);

            checkConsecative(clientSetOf3One,true);
            checkConsecative(clientSetOf3Two,true);
            checkConsecative(clientSetOf4,true);


        }else if (goalAmount == 10000){
            checkConsecative(clientSetOf3One,true);
            checkConsecative(clientSetOf3Two,true);
            checkConsecative(clientSetOf4,true);
        }else if (goalAmount == 100000){
            int checkValue = 0;
            checkIfAllTheSame(clientSetOf3One,true,checkValue);

            int checkValue2 = 0;
            checkIfAllTheSame(clientSetOf3Two,true,checkValue2);

            int checkValue3 = 0;
            checkIfAllTheSame(clientSetOf4,true,checkValue3);
        }

        checkDone();
    }

    @FXML
    public void btnChcekHand(){

        if (goalAmount == 1000){

            int checkValue = 0;
            System.out.println("checksame 1");
            checkIfAllTheSame(setOf3One,goodCheck1,checkValue);

            int checkValue2 = 0;
            System.out.println("checksame 2");
            checkIfAllTheSame(setOf3Two,goodCheck2,checkValue2);

            int checkValue3 = 0;
            System.out.println("checksame 3");
            checkIfAllTheSame(setOf4,goodCheck3,checkValue3);

            System.out.println("checksame 1 #");
            checkConsecative(setOf3One,goodCheckCons1);
            System.out.println("checksame 2 #");
            checkConsecative(setOf3Two,goodCheckCons2);
            System.out.println("checksame 3 #");
            checkConsecative(setOf4,goodCheckCons3);


        }else if (goalAmount == 10000){

            System.out.println("checksame 1");
            checkConsecative(setOf3One,goodCheckCons1);
            System.out.println("checksame 2");
            checkConsecative(setOf3Two,goodCheckCons2);
            System.out.println("checksame 3");
            checkConsecative(setOf4,goodCheckCons3);

        }else if (goalAmount == 100000){
            int checkValue = 0;
            checkIfAllTheSame(setOf3One,goodCheck1,checkValue);

            int checkValue2 = 0;
            checkIfAllTheSame(setOf3Two,goodCheck2,checkValue2);

            int checkValue3 = 0;
            checkIfAllTheSame(setOf4,goodCheck3,checkValue3);
        }

        checkDone();


    }

    public void checkConsecative(List<Card> cardSet,boolean checkType){
        if (cardSet.size() > 0){
            for (int i = 0; i < cardSet.size()-1; i++) {

                int consNum = cardSet.get(i).getCardNumber();
                consNum += 1;
                System.out.println("check +1: " + consNum);
                System.out.println("check : " + cardSet.get(i+1).getCardNumber());
                System.out.println("goodCheckCons1 " + checkType);
                System.out.println(cardSet.get(i).getCardColor());
                System.out.println(cardSet.get(i+1).getCardColor());
//                System.out.println(consNum == cardSet.get(i+1).getCardNumber() || cardSet.get(i).getCardNumber() == magicCard.getCardNumber() && checkType == true);
                if (consNum == cardSet.get(i+1).getCardNumber() && checkType == true){

                    if (goalAmount == 100000 || goalAmount == 10000){
                        System.out.println("ifCheck " + cardSet.get(i).getCardColor().equals(cardSet.get(i+1).getCardColor()));
                        if (cardSet.get(i).getCardColor().equals(cardSet.get(i+1).getCardColor())){
                            checkType = true;
                        }else{
                            checkType = false;
                        }
                    }

                }else{
                    checkType = false;
//                    break;
                }


            }

            if (checkType == true){
                System.out.println("Sets concecative good");
                System.out.println(setCor);
                setCor++;
//                checkDone();
//                if (turn == 0){
//                    socket.sendMessage("ServerWins");
//                    lblMessages.setText("YOU WIN!");
//                }else{
//                    socket.sendMessage("clientWins");
//                    lblMessages.setText("YOU LOST!");
//                }
            }
        }

    }

    int countCheck = 0;

    public void checkIfAllTheSame(List<Card> cardSet,boolean checkType,int checkValue){
        countCheck = 0;
        String cardCol = " ";

        if (cardSet.size() > 0){
            for (int i = 0; i < cardSet.size(); i++) {
                if (cardSet.get(i).getCardNumber() != magicCard.getCardNumber()){
                    checkValue = cardSet.get(i).getCardNumber();
                    cardCol = cardSet.get(i).getCardColor();
                    break;
                }
            }


            for (int i = 0; i < cardSet.size(); i++) {

//            if (cardSet.get(1).getCardNumber() == checkValue || cardSet.get(1).getCardNumber() == magicCard.getCardNumber()){
//                checkType = true;
//            }
                System.out.println((cardSet.get(i).getCardNumber() == checkValue || cardSet.get(i).getCardNumber() == magicCard.getCardNumber() )+ " lolz");
                if (cardSet.get(i).getCardNumber() == checkValue || cardSet.get(i).getCardNumber() == magicCard.getCardNumber()){

                    if (goalAmount == 100000){
                        if (cardSet.get(i).getCardColor().equals(cardCol)){
                            countCheck++;
                        }
                    }else{

                        countCheck++;
                        System.out.println(countCheck);

                    }


                    checkType = true;
                }
            }

            if (countCheck >= cardSet.size()){
                System.out.println("Sets good");
                setCor++;
//                checkDone();
//                if (turn == 0){
//                    socket.sendMessage("ServerWins");
//                    lblMessages.setText("YOU WIN!");
//                }else{
//                    socket.sendMessage("clientWins");
//                    lblMessages.setText("YOU LOST!");
//                }
            }
        }

    }

    int setCor = 0;

    public void checkDone(){
        if (setCor >= 3){
            if (turn == 0){
                socket.sendMessage("ServerWins");
                System.out.println("You win Ser");
                lblMessages.setText("YOU WIN!");
            }else{
                socket.sendMessage("clientWins");
                lblMessages.setText("YOU LOST!");
                System.out.println("You lost Ser");
            }
        }else{
            if (turn == 0){
                socket.sendMessage("clientWins");
                lblMessages.setText("YOU LOST!");
                System.out.println("You lost Ser");
            }else{
                socket.sendMessage("ServerWins");
                lblMessages.setText("YOU WIN!");
                System.out.println("You win Ser");
            }
        }


    }

    public void printServerScreen(){
        for (ImageView x: hand1I){
            x.setImage(null);
        }
        for (ImageView x: hand2I){
            x.setImage(null);
        }
        for (int i = 0;i<hand1D.size();i++){
            try {
                tempCard = new FileInputStream(hand1D.get(i).getCardPath());
                imageFront = new Image(tempCard);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            hand1I.get(i).setImage(imageFront);
        }
        for(int i =0;i<hand2D.size();i++){
            hand2I.get(i).setImage(imageBack);
        }
        sendHandClient();

    }

    public void sendHandClient(){
        String temp = "cCards";
        socket.sendMessage("cCardStart");
        for (Card x: hand2D){
//            temp+="," + x.getCardName();
            socket.sendMessage("cCards"+ x.getCardName());
        }
        socket.sendMessage("sCardNum"+hand1D.size());

    }

    public void sendDiscardPile(){

        if (discardPile.size() <= 0){
            socket.sendMessage("diNull");
        }else{
            socket.sendMessage("dis"+discardPile.get(discardPile.size()-1).getCardName());

        }
    }

    @FXML
    public void btnDone(){

        draw.setDisable(true);
        discardCardAtr.setDisable(true);
        deal.setDisable(true);

        isDone = true;

        socket.sendMessage("serverDone");

    }
    boolean clientDone = false;

//    public void clientClickedOnDone(){
//        clientDone = true;
//
//    }

    int tempSpot = -1;
    int newSpot = -1;

    int clickedOnNewSpot = 0;
    boolean isDone = false;

    public void handleServerImgClicked(MouseEvent mouseEvent) {
        serverImgClicked = GridPane.getColumnIndex((ImageView) mouseEvent.getSource());
        System.out.println("BLLHHLHLHLHLHLH " + (ImageView) mouseEvent.getSource());

        if (discardTF == true){
            doServerDiscard();
        }else if (discardTF == false){
            if (isDone){

//            tempCardPicked.add(hand1D.get(serverImgClicked));
                if (setOf3One.size() <= 3 && addToFirst3Set == true){
                    setOf3One.add(hand1D.get(serverImgClicked));
                    hand1D.remove(serverImgClicked);
                }else if (setOf3Two.size() <= 3 && addToSecond3Set == true){
                    setOf3Two.add(hand1D.get(serverImgClicked));
                    hand1D.remove(serverImgClicked);
                }else if (setOf4.size() <= 4 && addTo4Set == true){
                    setOf4.add(hand1D.get(serverImgClicked));
                    hand1D.remove(serverImgClicked);
                }


                printServerScreen();

            }else{
                if (clickedOnNewSpot == 0){
                    tempSpot = serverImgClicked;
                    clickedOnNewSpot++;
                }else{
                    newSpot = serverImgClicked;
                    hand1D.add(newSpot,hand1D.remove(tempSpot));
                    clickedOnNewSpot = 0;
                }
                printServerScreen();
            }





        }



    }



    boolean addToFirst3Set = false;
    boolean addToSecond3Set = false;
    boolean addTo4Set = false;

    boolean clientAddToFirst3Set = false;
    boolean clientAddToSecond3Set = false;
    boolean clientAddTo4Set = false;

    @FXML
    public void btnClickedOnShowFirst3Set(){

        addToFirst3Set = true;
        addToSecond3Set = false;
        addTo4Set = false;
    }

    public void clientClickedOnBtnShowFirst3Set(){//varivales are set part of step 2
        clientAddToFirst3Set = true;
        clientAddToSecond3Set = false;
        clientAddTo4Set = false;
    }

    @FXML
    public void btnClickedOnShowSecond3Set(){

        addToSecond3Set = true;
        addToFirst3Set = false;
        addTo4Set = false;

    }

    public void clientClickedOnBtnShowSecond3Set(){//varivales are set part of step 2
        clientAddToSecond3Set = true;
        clientAddToFirst3Set = false;
        clientAddTo4Set = false;
    }

    @FXML
    public void btnClickedOnShow4Set(){

        addTo4Set = true;
        addToFirst3Set = false;
        addToSecond3Set = false;
    }

    public void clientClickedOnBtnShow4Set(){//varivales are set part of step 2
        clientAddTo4Set = true;
        clientAddToFirst3Set = false;
        clientAddToSecond3Set = false;
    }

    @FXML
    public void btnReturnCardToHandOne(){

        for (int i = 0; i < setOf3One.size(); i++) {
            hand1D.add(setOf3One.get(i));
            show1.get(i).setImage(null);
        }

        setOf3One.clear();

        socket.sendMessage("ClearShow1");

        printServerScreen();

    }

    public void btnReturnClientCardsToHandOne(){

        for (int i = 0; i < clientSetOf3One.size(); i++) {
            hand2D.add(clientSetOf3One.get(i));
            show1.get(i).setImage(null);
        }

        clientSetOf3One.clear();

        socket.sendMessage("ClearedShow1Here");

        printServerScreen();
        sendHandClient();

    }

    public void btnReturnClientCardsToHandTwo(){

        for (int i = 0; i < clientSetOf3Two.size(); i++) {
            hand2D.add(clientSetOf3Two.get(i));
            show2.get(i).setImage(null);
        }

        clientSetOf3Two.clear();

        socket.sendMessage("ClearedShow2Here");

        printServerScreen();
        sendHandClient();

    }

    public void btnReturnClientCardsToHandThree(){

        for (int i = 0; i < clientSetOf4.size(); i++) {
            hand2D.add(clientSetOf4.get(i));
            show3.get(i).setImage(null);
        }

        clientSetOf4.clear();

        socket.sendMessage("ClearedShow3Here");

        printServerScreen();
        sendHandClient();

    }


    @FXML
    public void btnReturnCardToHandTwo(){
        for (int i = 0; i < setOf3Two.size(); i++) {
            hand1D.add(setOf3Two.get(i));
            show2.get(i).setImage(null);
        }

        setOf3Two.clear();

        socket.sendMessage("secondClearShow1");

        printServerScreen();
    }
    @FXML
    public void btnReturnCardToHandThree(){
        for (int i = 0; i < setOf4.size(); i++) {
            hand1D.add(setOf4.get(i));
            show3.get(i).setImage(null);
        }

        setOf4.clear();

        socket.sendMessage("forthClearShow1");

        printServerScreen();
    }

    int indMaxGrid1Len = 3;
    int cardsIn1 = 0;

    public void clientFinalShowGirdClicked3SetOne(){
        if (clientDone){

            if (clientSetOf3One.size() <= 3){
                for (int i = 0; i < clientSetOf3One.size(); i++) {
                    try {
                        tempCard = new FileInputStream(clientSetOf3One.get(i).getCardPath());
                        imageFront = new Image(tempCard);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }

                    show1.get(i).setImage(imageFront);
                    socket.sendMessage("show1" + clientSetOf3One.get(i).getCardName());

                }

            }

        }
    }

    public void clientFinalShowGirdClicked3SetTwo(){
        if (clientDone){

            if (clientSetOf3Two.size() <= 3){
                for (int i = 0; i < clientSetOf3Two.size(); i++) {
                    try {
                        tempCard = new FileInputStream(clientSetOf3Two.get(i).getCardPath());
                        imageFront = new Image(tempCard);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }

                    show2.get(i).setImage(imageFront);
                    socket.sendMessage("show2" + clientSetOf3Two.get(i).getCardName());

                }

            }

        }
    }

    public void clientFinalShowGirdClicked4Set(){
        if (clientDone){

            if (clientSetOf4.size() <= 4){
                for (int i = 0; i < clientSetOf4.size(); i++) {
                    try {
                        tempCard = new FileInputStream(clientSetOf4.get(i).getCardPath());
                        imageFront = new Image(tempCard);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }

                    show3.get(i).setImage(imageFront);
                    socket.sendMessage("show3" + clientSetOf4.get(i).getCardName());

                }

            }

        }
    }

//    public void clientDisplayFinal(){
//        socket.sendMessage("c3SetOneStart");
//        for (Card x: clientSetOf3One){
////            temp+="," + x.getCardName();
//            socket.sendMessage("3SetOne"+ x.getCardName());
//        }
//        socket.sendMessage("s3SetOneNum");
//    }

    @FXML
    public void handleFinalShowGridImgClicked3SetOne(){

//        indShowClick = (ImageView) mouseEvent.
//        System.out.println("blayfgaergi " + (ImageView) mouseEvent.getSource());

        System.out.println("clicked");

        if (isDone){

            if (setOf3One.size() <= 3){
                for (int i = 0; i < setOf3One.size(); i++) {
                    try {
                        tempCard = new FileInputStream(setOf3One.get(i).getCardPath());
                        imageFront = new Image(tempCard);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }

                    show1.get(i).setImage(imageFront);
                    socket.sendMessage("s1Server" + setOf3One.get(i).getCardName());

                }

//
//                if (cardsIn1 < 3){
//                    show1.get(cardsIn1).setImage(imageFront);
//                    cardsIn1++;
//                }

//            hand1D.remove(serverImgClicked);
//                tempCardPicked.clear();
            }




        }


    }

    int cardsIn2 = 0;

    public void handleFinalShowGridImgClicked3SetTwo(MouseEvent mouseEvent){
        System.out.println("clicked2");

        if (isDone){

            if (setOf3Two.size() <= 3){
                for (int i = 0; i < setOf3Two.size(); i++) {
                    try {
                        tempCard = new FileInputStream(setOf3Two.get(i).getCardPath());
                        imageFront = new Image(tempCard);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }

                    show2.get(i).setImage(imageFront);
                    socket.sendMessage("s2Server" + setOf3Two.get(i).getCardName());

                }

//
//                if (cardsIn1 < 3){
//                    show1.get(cardsIn1).setImage(imageFront);
//                    cardsIn1++;
//                }

//            hand1D.remove(serverImgClicked);
//                tempCardPicked.clear();
            }

        }
    }

    int cardsIn3 = 0;

    public void handleFinalShowGridImgClicked4Set(MouseEvent mouseEvent){
        System.out.println("clicked3");

        if (isDone){

            if (setOf4.size() <= 4){
                for (int i = 0; i < setOf4.size(); i++) {
                    try {
                        tempCard = new FileInputStream(setOf4.get(i).getCardPath());
                        imageFront = new Image(tempCard);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }

                    show3.get(i).setImage(imageFront);
                    socket.sendMessage("s3Server" + setOf4.get(i).getCardName());

                }

//
//                if (cardsIn1 < 3){
//                    show1.get(cardsIn1).setImage(imageFront);
//                    cardsIn1++;
//                }

//            hand1D.remove(serverImgClicked);
//                tempCardPicked.clear();
            }

        }
    }

    boolean discardTF = false;

//2020 fr arraylist, class, loops, arraus
    @FXML
    public void btnDiscardCard(){
        discardTF = true;
    }

    public void doClientDiscard(){
        discardPile.add(hand2D.remove(clientImgClicked));
        try {
            tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
            imageFront = new Image(tempCard);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        imgDiscard.setImage(imageFront);
        sendDiscardPile();
        printServerScreen();
        socket.sendMessage("cardDiscardedSetBolToFalse");
    }
//    List<Card> switchCardsLst = new ArrayList<>();
    public void switchCards(){
//        for (ImageView x: hand1I){
//            x.setImage(null);
//        }
//        switchCardsLst.clear();
        hand1D.add(tempSpot,hand1D.remove(serverImgClicked));

        printServerScreen();
    }

    long level1Timer = 0;
    long level2Timer = 0;
    long level3Timer = 0;

    int serverTime = 800;
    int clientTime = 800;

    int indServerTime = 300;
    int intClientTime = 300;

    //level 1 the game in total is 5 minutes long, level 2 the players individual time, level 3 you start with a less amount of cards

    public void start() {

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - level1Timer > 1000000000.0 && goalAmount == 1000){
                    serverTime--;
                    if (serverTime <= 0){
                        socket.sendMessage("doneTime");
                        lblMessages.setText("TIME RAN OUT, BOTH LOSE!");
                        serverTime = 0;
                    }
                    socket.sendMessage("timeAt" + serverTime);
                    lblShowTime.setText(serverTime + "");
                    level1Timer = System.nanoTime();

                }

                if (now - level2Timer > 1000000000.0 && goalAmount == 10000){
                    System.out.println(turn);
                    if (turn == 0){
                        serverTime--;
                    }else{
                        clientTime--;
                    }
                    if (serverTime <= 0){
                        socket.sendMessage("serverLostTime");
                        lblMessages.setText("YOU RAN OUT OF TIME! YOU LOSE");
                        serverTime = 0;
                    }else if (clientTime <= 0){
                        socket.sendMessage("clientLostTime");
                        lblMessages.setText("YOU WIN!");
                        clientTime = 0;
                    }
                    socket.sendMessage("timeAt" + clientTime);
                    lblShowTime.setText(serverTime + "");
                    level2Timer = System.nanoTime();
                }

                if (now - level3Timer > 1000000000.0 && goalAmount == 100000){
                    if (turn == 0){
                        serverTime--;
                    }else{
                        clientTime--;
                    }
                    if (serverTime <= 0){
                        socket.sendMessage("serverLostTime");
                        lblMessages.setText("YOU RAN OUT OF TIME! YOU LOSE");
                        serverTime = 0;
                    }else if (clientTime <= 0){
                        socket.sendMessage("clientLostTime");
                        lblMessages.setText("YOU WIN!");
                        clientTime = 0;
                    }
                    socket.sendMessage("timeAt" + clientTime);
                    lblShowTime.setText(serverTime + "");
                    level3Timer = System.nanoTime();
                }


            }
        }.start();
    }

    @FXML
    public void btnRequestTime(){
        int timeLvl3 = Integer.parseInt(sendTextField.getText());
        lblShowTime.setText(timeLvl3 + "");
        socket.sendMessage("inputTimeLVL3" + timeLvl3);
    }

    public void doServerDiscard(){
        discardPile.add(hand1D.remove(serverImgClicked));
        try {
            tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
            imageFront = new Image(tempCard);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        imgDiscard.setImage(imageFront);
        sendDiscardPile();
        printServerScreen();

        discardTF = false;
        turn = 1;
        butRetCardToHand1.setDisable(true);
        butRetCardToHand2.setDisable(true);
        butRetCardToHand3.setDisable(true);
        butDone.setDisable(true);
        butShow1.setDisable(true);
        butShow2.setDisable(true);
        butShow3.setDisable(true);
        butCheck.setDisable(true);
        discardCardAtr.setDisable(true);
        butResume.setDisable(true);

        socket.sendMessage("serverDrawCard");

    }

    public HelloController(){
        try {
            back1 = new FileInputStream("src/main/resources/Images/BACK-1.jpg");
            imageBack = new Image(back1);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    @FXML
    private ImageView imgS0,imgS1,imgS2,imgS3,imgS4,imgS5,imgS6,imgS7,imgS8,imgS9,imgS10,imgS12,
    imgC0,imgC1,imgC2,imgC3,imgC4, imgC5,imgC6,imgC7,imgC8,imgC9,imgC10,imgC12,imgDiscard,imgMagicCard,imgf0,imgf1,imgf2,imgf3,imgf4,imgf5,imgf6,imgf7,imgf8,imgf9;

    @FXML
    Button deal,draw;
    FileInputStream back1,tempCard;
    Image imageBack;
    Image imageFront;
    List<Card> deck = new ArrayList<>();
    Card discard;
    List<ImageView> hand1I = new ArrayList<>();
    List<ImageView> hand2I = new ArrayList<>();

    List<ImageView> show1 = new ArrayList<>();
    List<ImageView> show2 = new ArrayList<>();
    List<ImageView> show3 = new ArrayList<>();

    List<Card> hand1D = new ArrayList<>();
    List<Card> hand2D = new ArrayList<>();
    List<Card> discardPile = new ArrayList<>();

    List<Card> tempCardPicked = new ArrayList<>();
    //final set
    List<Card> setOf3One = new ArrayList<>();
    List<Card> setOf3Two = new ArrayList<>();
    List<Card> setOf4 = new ArrayList<>();

    List<Card> clientSetOf3One = new ArrayList<>();
    List<Card> clientSetOf3Two = new ArrayList<>();
    List<Card> clientSetOf4 = new ArrayList<>();

    private int numCardsInDeck;
    private int turn = 0;
    int serverImgClicked;
    int serverImgGridShowClicked;
    int clientImgClicked;
}
