import java.awt.*;
import javax.swing.*;

public class Card {
     private int suit;
     private int value;
     private Rectangle cardArea;
     private boolean isFaceUp;
     private boolean isSelected;
     
     public Card(int value, int suit) {
          this.value = value;
          this.suit = suit;
          cardArea = new Rectangle (0, 0, 0, 0);
          isFaceUp = false;
          isSelected = false;
     }
     
     public void setIsFaceUp(boolean faceUp) {
          this.isFaceUp = faceUp;
     }
     
     public boolean getIsFaceUp() {
          return isFaceUp;
     }
     
     public boolean getIsSelected() {
          return isSelected;
     }
     
     public void setIsSelected(boolean selected) {
          this.isSelected = selected;
     }
     
     public int getSuit() {
          return suit;
     }
     
     public int getValue() {
          return value;
     }
     
     public void setCardArea(int x, int y, int w, int h) {
          cardArea.x = x;
          cardArea.y = y;
          cardArea.width = w;
          cardArea.height = h;
     }
     
     public Rectangle getCardArea() {
          return cardArea;
     }
     
     public void translate(int x, int y) {
          cardArea.x = cardArea.x + x;
          cardArea.y = cardArea.y + y;
     }
     
     public boolean isSameSuit(Card other) {
          return suit == other.suit;
     }
     
     public boolean hasSmallerValue(Card other) {
          return value < other.value;
     }
     
     public Point getCentreOfCard() {
          Point centerOfCard = new Point (cardArea.x + cardArea.width / 2, cardArea.y + cardArea.height / 2);
          return centerOfCard;
     }
     
     public boolean isInCardArea(Point pressPt) {
          return cardArea.contains(pressPt);
     }
     
     public String getCardStatusInfo() {
          String status = value + " " + suit + " " + cardArea.x + " " + cardArea.y + " " + isFaceUp + " " + isSelected;
          return status;
     }
     
//Draw the Card object.
     public void drawCard(Graphics g, JComponent theJPanelInstance) {
          Image cardPic;
          int fileIndex;
          
          if (isFaceUp) {
               fileIndex = suit * SolitaireConstants.CARDS_IN_EACH_SUIT + value;
               cardPic = CardImageLoadUp.getACardImage(fileIndex);
          } else {
               cardPic = CardImageLoadUp.getFaceDownCardImage();
          }
          if (isSelected) {
               g.setColor(Color.WHITE);
          } else {
               g.setColor(Color.BLUE);
          }
          
          g.fillRect(cardArea.x - 2, cardArea.y - 2, cardArea.width + 4, cardArea.height + 4);
          g.drawImage(cardPic, cardArea.x, cardArea.y, SolitaireJPanel.cardWidth, SolitaireJPanel.cardHeight, theJPanelInstance);
     }
     
//Debugging purposes
     public String toString() {
          final String[] SUITS = {"CLUBS", "DIAMONDS", "HEARTS", "SPADES"};
          if (value == 0) {
               return "A" + " " + SUITS[suit];
          } else if (value == 12) {
               return "K" + " " + SUITS[suit];
          } else if (value == 11) {
               return "Q" + " " + SUITS[suit];
          } else if (value == 10) {
               return "J" + " " + SUITS[suit];
          }
          
          return (value + 1)  + " " + SUITS[suit];
     }
}