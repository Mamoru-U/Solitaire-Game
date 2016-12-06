import java.awt.*;

public class SolitaireConstants {
//Width and height of JFrame
     public static final int JFRAME_WIDTH = 600;
     public static final int JFRAME_HEIGHT = 550;
     
//Position of the score and feedback
     public static final Point SCORE_POSITION = new Point(10, 26);
     
//Left position for the card display
     public static final int CARD_DISPLAY_LEFT = 60;
     
     public static final int TOTAL_NUMBER_OF_CARDS = 52;//52
     public static final int CARDS_IN_EACH_SUIT = 13; //13
     
//Number of cards on JPanel
     public static final int NUMBER_ROWS = 4;//4
     public static final int NUMBER_COLS = 13;//13
     
     public static final int CARD_WIDTH = JFRAME_WIDTH/8;//4
     public static final int CARD_HEIGHT = (int)(CARD_WIDTH*1.5);//
     
//Number to represent each suit
     public static final int CLUBS = 0;
     public static final int DIAMONDS = CLUBS + 1;
     public static final int HEARTS = DIAMONDS + 1;
     public static final int SPADES = HEARTS + 1;
     
//Colour of the background
     public static final Color BACKGROUND_COLOUR = new Color(245, 227, 247);
}