import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;

public class SolitaireJPanel extends JPanel implements ActionListener, MouseListener, KeyListener {
     //public static final int JFRAME_WIDTH = SolitaireConstants.JFRAME_WIDTH;
     //public static final int JFRAME_HEIGHT = SolitaireConstants.JFRAME_HEIGHT;
     
     public static int JFRAME_WIDTH = SolitaireConstants.JFRAME_WIDTH;
     public static int JFRAME_HEIGHT = SolitaireConstants.JFRAME_HEIGHT;
     
     private static final int CARDS_IN_EACH_SUIT = SolitaireConstants.CARDS_IN_EACH_SUIT;
     private static final int TOTAL_NUMBER_OF_CARDS = SolitaireConstants.TOTAL_NUMBER_OF_CARDS;
     
     private static final int NUMBER_ROWS = SolitaireConstants.NUMBER_ROWS;
     private static final int NUMBER_COLS = SolitaireConstants.NUMBER_COLS;
     
     private static final Color BACKGROUND_COLOUR = SolitaireConstants.BACKGROUND_COLOUR;
     
     public static final Point SCORE_POSITION = SolitaireConstants.SCORE_POSITION;
     private static final int CARD_DISPLAY_LEFT = SolitaireConstants.CARD_DISPLAY_LEFT;
     
     private ArrayList<Card> cardStack;
     private Card[][] cardsOnTable;
     private int[] howManyInEachRow;
     
     private Card lastRemovedCard;
     
     private Card currentlySelectedCard;
     private boolean selectedCardIsMoving;
     private int cardMoveX, cardMoveY;
     
     private Card faceDownCard;
     
     public static int cardWidth = SolitaireConstants.CARD_WIDTH;
     public static int cardHeight = SolitaireConstants.CARD_HEIGHT;
     
     private boolean cardStackIsEmpty;
     private boolean gameHasEnded;
     private boolean gameStarted;
     
     private Timer t;
     
     private long startTime;
     private long endTime;
     private long savedTime = 0;
     
     private int cardsRemoved;
     private int numberOfCardsOnTable;
     
     public SolitaireJPanel() {
          setBackground(BACKGROUND_COLOUR);
          loadAllCardImagesAndSetUpCardDimensions();
          
          t = new Timer(30, this);
          
          addKeyListener(this);
          addMouseListener(this);
          
          reset();
     }
     private Boolean getCustomDimensionsChanges(){
          if ((int)(super.getParent().getBounds().width) == JFRAME_WIDTH){
               if ((int)(super.getParent().getBounds().height) == JFRAME_HEIGHT){
                    return true;
               }
          }
          return false;
     }
     private void setCustomDimensions(){
          JFRAME_WIDTH = (int)(super.getParent().getBounds().width);
          JFRAME_HEIGHT = (int)(super.getParent().getBounds().height);
          int widthThis = (int)(JFRAME_WIDTH/8);
          int widthThat = (int)(JFRAME_HEIGHT/7.2);
          
          if (widthThis <= widthThat){
               cardWidth = widthThis;
               cardHeight = (int)(cardWidth*1.5);
          }else{
               cardWidth = widthThat;
               cardHeight = (int)(cardWidth*1.5);
          }
          reset();
     }
     
     private void reset() {
          howManyInEachRow = new int[NUMBER_ROWS];
          for (int i = 0; i < howManyInEachRow.length; i++) {
               howManyInEachRow[i] = 0;
          }
          
          cardStack = createTheFullPack();
          cardStackIsEmpty = false;
          
          cardsOnTable = new Card[NUMBER_ROWS][NUMBER_COLS];
          addNextColOfCards(cardStack, cardsOnTable, howManyInEachRow);
          
          faceDownCard = createFaceDownCard();
          
          lastRemovedCard = null;
          currentlySelectedCard = null;
          selectedCardIsMoving = false;
          gameHasEnded = false;
          cardsRemoved = 0;
          numberOfCardsOnTable = 4;
          t.stop();
     }
     
     private Card createFaceDownCard() {
          final Point FACE_DOWN_CARD_POSITION = new Point(JFRAME_WIDTH - cardWidth - 10, cardHeight + 20);
          Card faceDownCard = new Card(0, 0);
          faceDownCard.setCardArea(FACE_DOWN_CARD_POSITION.x, FACE_DOWN_CARD_POSITION.y, cardWidth, cardHeight);
          faceDownCard.setIsFaceUp(false);
          return faceDownCard;
     }
     
//adds a col of random cards from the card stack to the table
     private void addNextColOfCards(ArrayList<Card> cards, Card[][] cardsOnTable, int[] numInEachRow) {
          if (!cardStackIsEmpty){
               for (int i = 0; i < numInEachRow.length; i++){
                    int cardSelected = (int)(Math.random() * cards.size());
                    cardsOnTable[i][numInEachRow[i]] = cards.remove(cardSelected);
                    cardsOnTable[i][numInEachRow[i]].setIsFaceUp(true);
                    setupIndividualCardPositionAndSize (cardsOnTable[i][numInEachRow[i]], i, numInEachRow[i]);
                    numInEachRow[i]++;
               }
               numberOfCardsOnTable = numberOfCardsOnTable + 4;
          }
     }
     
     private void setupIndividualCardPositionAndSize(Card card, int row, int col) {
          final int CARD_DISPLAY_TOP = 65;
          final int DISPLAY_GAP = 6;
          
          int y = CARD_DISPLAY_TOP + (cardHeight + DISPLAY_GAP - 1) * row;
          int x = CARD_DISPLAY_LEFT + (cardWidth / 3) * col;
          
          card.setCardArea(x, y, cardWidth, cardHeight);
     }
//Handle ActionEvents
//Moves the card which is currently selected towards
//the REMOVED_CARDS_POSITION in the JPanel.
//Once the card intersects the REMOVED_CARDS_POSITION
//the card is placed exactly in position and the timer stops.
     public void actionPerformed(ActionEvent e) {
          final Point REMOVED_CARDS_POSITION = new Point(JFRAME_WIDTH - cardWidth - 10, JFRAME_HEIGHT / 2);
          Rectangle removedCardsArea = new Rectangle(REMOVED_CARDS_POSITION.x, REMOVED_CARDS_POSITION.y, cardWidth, cardHeight);
          Rectangle cardArea;
          
          if (selectedCardIsMoving) {
               currentlySelectedCard.translate(cardMoveX, cardMoveY);
               cardArea = currentlySelectedCard.getCardArea();
               
               if (cardArea.intersects(removedCardsArea)) {
                    selectedCardIsMoving = false;
                    lastRemovedCard = currentlySelectedCard;
                    lastRemovedCard.setCardArea(REMOVED_CARDS_POSITION.x, REMOVED_CARDS_POSITION.y, cardArea.width, cardArea.height);
                    currentlySelectedCard = null;
                    t.stop();
               }
          }
          
          repaint();
     }
     
     public void mousePressed(MouseEvent e) {
          Card clickedOnCard;
          int emptyRowNumber;
          boolean cardHasBeenRemoved;
//boolean thereIsAUsableEmptyCol;
          Point rowColOfSelectedCard;
          Point pressPt = e.getPoint();
          
          if (selectedCardIsMoving || gameHasEnded) {
               return;
          }
//Take action when pressed inside the face down card
          if (!cardStackIsEmpty && faceDownCard.isInCardArea(pressPt)) {
               addNextColOfCards(cardStack, cardsOnTable, howManyInEachRow);
               
               if (currentlySelectedCard != null) {
                    currentlySelectedCard.setIsSelected(false);
                    currentlySelectedCard = null;
               }
               
               if (cardStack.size() == 0) {
                    cardStackIsEmpty = true;
               }
          } else {
//Take action when pressed inside one of the left-most cards
               rowColOfSelectedCard = getRowColOfSelectedCard(cardsOnTable, pressPt, howManyInEachRow);
               
               if (rowColOfSelectedCard != null) {
                    clickedOnCard = cardsOnTable[rowColOfSelectedCard.x][rowColOfSelectedCard.y];
                    
                    if (currentlySelectedCard == null) {
//previously selected card has been processednand the user has selected another card.
                         currentlySelectedCard = clickedOnCard;
                         currentlySelectedCard.setIsSelected(true);
//Check if the card needs to be removed
                         cardHasBeenRemoved = setUpCardRemovalIfAppropriate(currentlySelectedCard, howManyInEachRow, rowColOfSelectedCard.x);
                         
//If the card wasn't remove check if the card should be moved to an empty row.
                         if (cardHasBeenRemoved == false) {
                              emptyRowNumber = getEmptyRowNumber(howManyInEachRow);
                              if (emptyRowNumber > -1) {
                                   moveSelectedCardToEmptyRow(cardsOnTable, rowColOfSelectedCard, howManyInEachRow, emptyRowNumber);
                              }
                              
                              currentlySelectedCard.setIsSelected(false);
                              currentlySelectedCard = null;
                         }
                    }
               }
          }
          
          if (cardStackIsEmpty && getEmptyRowNumber(howManyInEachRow) == -1) {
               if (!thereAreMoreAvailableMoves(cardsOnTable, howManyInEachRow)) {
                    gameHasEnded = true;
                    endTime = System.currentTimeMillis();
               }
          }
          
          repaint();
     }
     
     private Point getRowColOfSelectedCard(Card[][] cards, Point pressPt, int[] howManyInEachRow) {
          for (int i = 0; i < howManyInEachRow.length; i++){
               if (howManyInEachRow[i] - 1 >= 0 && cards[i][howManyInEachRow[i] - 1] != null){
                    if ((cards[i][howManyInEachRow[i]-1].getCardArea()).contains(pressPt)){
                         Point card = new Point (i, howManyInEachRow[i]-1);
                         return card;
                    }
               }
          }
          
          return null;
     }
     
     private boolean cardCanBeRemoved(Card cardToCheck, Card[][] cardsOnTable, int[] howManyInEachRow) {
          if (cardToCheck.getValue() == 0){
               return false;
          }
          
          for (int i = 0; i < howManyInEachRow.length; i++){
               if (howManyInEachRow[i]-1 >= 0){
                    if (cardToCheck.isSameSuit(cardsOnTable[i][howManyInEachRow[i]-1])){
                         if (cardToCheck.getValue() != cardsOnTable[i][howManyInEachRow[i]-1].getValue()){
                              if (cardToCheck.hasSmallerValue(cardsOnTable[i][howManyInEachRow[i]-1])){
                                   return true;
                              }
                              if (cardsOnTable[i][howManyInEachRow[i]-1].getValue() == 0){
                                   return true;
                              }
                         }
                    }
               }
          }
          
          return false;
     }
     
     private int getEmptyRowNumber(int[] howManyInEachRow) {
          for (int i = 0; i < howManyInEachRow.length; i++){
               if (howManyInEachRow[i] == 0){
                    return i;
               }
          }
          
          return -1;
     }
     
     private void moveSelectedCardToEmptyRow(Card[][] cardsOnTable, Point rowColOfCardToMove, int[] howManyInEachRow, int whichRow) {
          for (int i = 0; i < howManyInEachRow.length; i++){
               if (i != whichRow && howManyInEachRow[i] > 0){
                    if (cardsOnTable[i][howManyInEachRow[i]-1] != null){
                         if (i == rowColOfCardToMove.x && howManyInEachRow[i]-1 == rowColOfCardToMove.y){
                              cardsOnTable[whichRow][howManyInEachRow[whichRow]] = cardsOnTable[i][howManyInEachRow[i]-1];
                              setupIndividualCardPositionAndSize(cardsOnTable[whichRow][howManyInEachRow[whichRow]], whichRow, howManyInEachRow[whichRow]);
                              howManyInEachRow[whichRow]++; howManyInEachRow[i]--;
                              break;
                         }
                    }
               }
          }
          
     }
     
     private boolean setUpCardRemovalIfAppropriate(Card cardToCheck, int[] howManyInEachRow, int whichColumn) {
          if (cardToCheck == null || selectedCardIsMoving || !cardToCheck.getIsSelected()) {
               return false;
          }
          
          final Point REMOVED_CARDS_CENTRE_POSITION = new Point(JFRAME_WIDTH - cardWidth - 10 + cardWidth / 2, JFRAME_HEIGHT / 2 + cardHeight / 2);
          
          if (! cardCanBeRemoved(cardToCheck, cardsOnTable, howManyInEachRow)) {
               return false;
          }else{
               numberOfCardsOnTable = numberOfCardsOnTable - 1;
               cardsRemoved = cardsRemoved + 1;
          }
          
          Point cardCentre = cardToCheck.getCentreOfCard();
          setUpCardMoveAmts(REMOVED_CARDS_CENTRE_POSITION, cardCentre);
          
          selectedCardIsMoving = true;
          howManyInEachRow[whichColumn]--;
          t.start();
          
          return true;
     }
     
     private void setUpCardMoveAmts(Point destinationCentre, Point cardCentre) {
          int xDistance = destinationCentre.x - cardCentre.x;
          int yDistance = destinationCentre.y - cardCentre.y;
          int xYDistance = (int) (Math.pow(xDistance * xDistance + yDistance * yDistance, 0.5));
          
          double howManyTimesMoves = xYDistance / 50.0;
          
          cardMoveX = (int) (xDistance / howManyTimesMoves);
          cardMoveY = (int) (yDistance / howManyTimesMoves);
     }
     
     private boolean thereAreMoreAvailableMoves(Card[][] cardsOnTable, int[] howManyInEachRow) {
          for (int i = 0; i < howManyInEachRow.length; i++){
               if (cardCanBeRemoved(cardsOnTable[i][howManyInEachRow[i]-1], cardsOnTable, howManyInEachRow)){
                    return true;
               }
          }
          
          return false;
     }
     
     public void mouseClicked(MouseEvent e) {}
     public void mouseReleased(MouseEvent e) {}
     public void mouseEntered(MouseEvent e) {}
     public void mouseExited(MouseEvent e) {}
     
     public void keyPressed(KeyEvent e) {
          if (e.getKeyChar() == 'n' || e.getKeyChar() == 'N'){
               gameStarted = true;
               startTime = System.currentTimeMillis();
               savedTime = 0;
               reset();
          }
          
          if (e.getKeyChar() == 's' || e.getKeyChar() == 'S'){
               saveToFile("SavedGame.txt");
          }
          
          if (e.getKeyChar() == 'l' || e.getKeyChar() == 'L'){
               loadFromFile("SavedGame.txt");
               startTime = System.currentTimeMillis();
          }
          
          if (e.getKeyChar() == 'm' || e.getKeyChar() == 'M'){
               gameStarted = false;
          }
          
          repaint();
     }
     
     public void keyReleased(KeyEvent e) {}
     public void keyTyped(KeyEvent e) {}
     
     public void paintComponent(Graphics g) {
          super.paintComponent(g);
          if (!getCustomDimensionsChanges()){
               setCustomDimensions();
          }
          if (gameStarted) {
               setBackground(BACKGROUND_COLOUR);
               drawTableCards(g);
               drawRestOfJPanelDisplay(g);
          }else{
               setBackground(Color.BLUE);
               g.setColor(Color.YELLOW);
               drawTitleScreen(g);
          }
     }
     
     private void drawTitleScreen(Graphics g) {
          Font thisFont = new Font("TIMES", Font.BOLD, 25);
          drawCenteredString(g, "Try Not to Lose", 30, thisFont);
          drawCenteredString(g, "To Start/Restart, press the N button", 60, thisFont);
          drawCenteredString(g, "To Save, press the S button", 90, thisFont);
          drawCenteredString(g, "To Load, press the L button", 120, thisFont);
          drawCenteredString(g, "To Comeback to the Menu, press the M button", 150, thisFont);
          drawCenteredString(g, "Instructions:", 285, thisFont);
          thisFont = new Font("TIMES", Font.BOLD, 50);
          drawCenteredString(g, "Solitaire", 230, thisFont);
          thisFont = new Font("TIMES", Font.BOLD, 20);
          drawCenteredString(g, "Remove all the cards from the table.", 310, thisFont);
          drawCenteredString(g, "A card can be removed from the table cards if:", 335, thisFont);
          thisFont = new Font("TIMES", Font.BOLD, 15);
          drawCenteredString(g, "it is not an Ace,", 355, thisFont);
          drawCenteredString(g, "it is the rightmost card in the row,", 375, thisFont);
          drawCenteredString(g, "and it is the same suit and is smaller in value than", 395, thisFont);
          drawCenteredString(g, "the rightmost card in any one of the other three rows.", 415, thisFont);
     }
     
     public void drawCenteredString(Graphics g, String text, int height, Font font) {
          FontMetrics metrics = g.getFontMetrics(font);
          int x = (JFRAME_WIDTH - metrics.stringWidth(text)) / 2;
          int y = height;
          g.setFont(font);
          g.drawString(text, x, y);
     }
     
     private void drawTableCards(Graphics g) {
          for (int i = 0; i < howManyInEachRow.length; i++){
               for (int j = 0; j < howManyInEachRow[i]; j++){
                    cardsOnTable[i][j].drawCard(g, this);
               }
          }
     }
     
     private void drawRestOfJPanelDisplay(Graphics g) {
          
          if (lastRemovedCard != null) {
               lastRemovedCard.drawCard(g, this);
          }
          if (selectedCardIsMoving) {
               currentlySelectedCard.drawCard(g, this);
          }
          
          drawGameInformation(g);
          if(!cardStackIsEmpty){
               drawFaceDownCard(g);
          }
     }
     
     private void drawFaceDownCard(Graphics g) {
          faceDownCard.drawCard(g, this);
          Rectangle cardArea = faceDownCard.getCardArea();
          
          int numberLeftInPack = cardStack.size();
          
          g.setFont(new Font("Times", Font.BOLD, (int)(cardWidth*0.65)));
          if (numberLeftInPack < 10) {
               g.drawString("" + numberLeftInPack, cardArea.x + cardArea.width / 3, cardArea.y + cardArea.height * 2 / 3);
          } else {
               g.drawString("" + numberLeftInPack, cardArea.x + cardArea.width / 6, cardArea.y + cardArea.height * 2 / 3);
          }
     }
     
     private void drawGameInformation(Graphics g) {
          g.setFont(new Font("Times", Font.BOLD, 26));
          g.setColor(Color.BLUE);
          String gameEndMessage = "";
          String statusMessage = "Removed: " + cardsRemoved;
          statusMessage = statusMessage + ", On table: " + numberOfCardsOnTable;
          
          if (gameHasEnded) {
               gameEndMessage = "GAME OVER! "; 
               if (numberOfCardsOnTable < 5) {
                    gameEndMessage = gameEndMessage + " You won! ";
               } else if (numberOfCardsOnTable < 11) {
                    gameEndMessage = gameEndMessage + "Excellent Result! ";
               } else {
                    gameEndMessage = gameEndMessage + "Bad luck! (n - new game)";
               }
               DecimalFormat df = new DecimalFormat("0.00");
               String timetaken = df.format((endTime - startTime + savedTime)/1000.0);
               
               statusMessage = statusMessage + "   Time: " + timetaken + "sec";
          }
          g.drawString(statusMessage, SCORE_POSITION.x, SCORE_POSITION.y);
          g.drawString(gameEndMessage, SCORE_POSITION.x, SCORE_POSITION.y + 27);
          
     }
     
//Save Game
     private void saveToFile(String fileName) {
          PrintWriter pW = null;
          Card card;
          try {
               pW = new PrintWriter(fileName);
               pW.println(cardWidth);
               pW.println(cardHeight);
               
               pW.println(howManyInEachRow.length);
               for (int row = 0; row < cardsOnTable.length; row++) {
                    pW.println(howManyInEachRow[row]);
               }
               pW.println(cardsOnTable.length);
               pW.println(cardsOnTable[0].length);
               for (int row = 0; row < cardsOnTable.length; row++) {
                    for (int down = 0; down < howManyInEachRow[row]; down++) {
                         pW.println(cardsOnTable[row][down].getCardStatusInfo());
                    }
               }
               
               pW.println(cardStack.size());
               
               for (int i = 0; i < cardStack.size(); i++) {
                    card = cardStack.get(i);
                    pW.println(card.getCardStatusInfo());
               }
               
               if (lastRemovedCard == null) {
                    pW.println("null");
               } else {
                    pW.println(lastRemovedCard.getCardStatusInfo());
               }
               if (currentlySelectedCard == null) {
                    pW.println("null");
               } else {
                    pW.println(currentlySelectedCard.getCardStatusInfo());
               }
               
               pW.println(selectedCardIsMoving);
               pW.println(cardMoveX);
               pW.println(cardMoveY);
               pW.println(faceDownCard.getCardStatusInfo());
               pW.println(cardsRemoved);
               pW.println(numberOfCardsOnTable);
               pW.println(cardStackIsEmpty);
               pW.println(gameHasEnded);
               pW.println(System.currentTimeMillis() - startTime + savedTime);
               
               pW.close();
          } catch(IOException e) {
               System.out.println("Error saving game to " + fileName);
          }
     }
     
//Load Game
     public void loadFromFile(String fileName) {
          Scanner scan = null;
          //Card card; 
          String cardInfo1, cardInfo2;
          int /*value, suitIndex,*/ xPos, yPos, cardStackSize;
          //boolean hasBeenGuessed;
          
          try {
               File savedGame = new File(fileName);
               scan = new Scanner(savedGame);
               cardWidth = scan.nextInt();
               cardHeight = scan.nextInt();
               
               howManyInEachRow = new int[scan.nextInt()];
               for (int i = 0; i < howManyInEachRow.length; i++) {
                    howManyInEachRow[i] = scan.nextInt();
               }
               
               cardsOnTable = new Card[scan.nextInt()][scan.nextInt()];
               for (int i = 0; i < howManyInEachRow.length; i++) {
                    for (int j = 0; j < howManyInEachRow[i]; j++){
                         cardInfo1 = scan.next();
                         cardInfo2 = scan.nextLine();
                         cardsOnTable[i][j] = createACard(cardInfo1 + cardInfo2, cardWidth, cardHeight);
                    }
               }
               
               cardStackSize = scan.nextInt();
               cardStack = new ArrayList<Card>();
               for (int i = 0; i < cardStackSize; i++) {
                    cardInfo1 = scan.next();
                    cardInfo2 = scan.nextLine();
                    cardStack.add(createACard(cardInfo1 + cardInfo2, cardWidth, cardHeight));
               }
               
               String theLastCardRemoved1 = scan.next();
               if (!(theLastCardRemoved1.equals("null"))) {
                    String theLastCardRemoved2 = scan.nextLine();
                    lastRemovedCard = createACard(theLastCardRemoved1 + theLastCardRemoved2, cardWidth, cardHeight);
               }
               
               String selectedCard1 = scan.next();
               String selectedCard2 = scan.nextLine();
               
               String selectedCardIsMoving1 = scan.next();
               String selectedCardIsMoving2 = scan.nextLine();
               xPos = scan.nextInt();
               yPos = scan.nextInt();
               faceDownCard = createACard(scan.next() + scan.nextLine(), cardWidth, cardHeight);
               cardsRemoved = scan.nextInt();
               numberOfCardsOnTable = scan.nextInt();
               cardStackIsEmpty = scan.nextBoolean();
               gameHasEnded = scan.nextBoolean();
               savedTime = scan.nextLong();
               
               scan.close();
          } catch(IOException e) {
               System.out.println("Error loading game from " + fileName);
          }
          
     }
     
     private Card createACard(String info, int width, int height) {
          Card card;
          int suit, value, x, y;
          boolean selected, faceUp;
          
          Scanner scanInfo = new Scanner(info);
          
          value = scanInfo.nextInt();
          suit = scanInfo.nextInt();
          x = scanInfo.nextInt();
          y = scanInfo.nextInt();
          
          
          faceUp = scanInfo.nextBoolean();
          selected = scanInfo.nextBoolean();
          
          card = new Card(value, suit);
          
          card.setCardArea(x, y, width, height);
          card.setIsFaceUp(faceUp);
          card.setIsSelected(selected);
          scanInfo.close();
          return card;
     }
     
     private ArrayList<Card> createTheFullPack() {
          ArrayList<Card> theCards = new ArrayList <Card> (TOTAL_NUMBER_OF_CARDS);
          int suitNum = SolitaireConstants.CLUBS;
          int cardValue = 0;
          
          for (int i = 0; i < TOTAL_NUMBER_OF_CARDS; i++) {
               theCards.add(new Card(cardValue, suitNum));
               
               if(cardValue >= CARDS_IN_EACH_SUIT - 1) {
                    suitNum++;
               }
               
               cardValue = (cardValue + 1) % (CARDS_IN_EACH_SUIT);
          }
          
          return theCards;
     }
     
     /*private void printTableCards(Card[][] cards) {
      for (int row = 0; row < cardsOnTable.length; row++) {
      for (int col = 0; col < cardsOnTable[row].length; col++) {
      if (cardsOnTable[row][col] != null) {
      System.out.printf("%6s ",cardsOnTable[row][col].toString());
      }
      }
      System.out.println();
      }
      }*/
     
     private void loadAllCardImagesAndSetUpCardDimensions() {
          CardImageLoadUp.loadAndSetUpAllCardImages(this);
          
          /*Dimension d = CardImageLoadUp.getDimensionOfACard();
           cardWidth = d.width;
           cardHeight = d.height;*/
     }
}