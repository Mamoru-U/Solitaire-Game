import java.awt.*;
import javax.swing.*;

public class CardImageLoadUp {
     private static final int CARDS_IN_EACH_SUIT = SolitaireConstants.CARDS_IN_EACH_SUIT;
     private static final int TOTAL_NUMBER_OF_CARDS = SolitaireConstants.TOTAL_NUMBER_OF_CARDS;
     
     private static String[] cardImageNames;
     private static boolean imagesIsLoaded;
     private static Dimension dimensionOfACard;
     
     public static boolean getImagesIsLoaded() {
          return imagesIsLoaded;
     }
     
     public static Dimension getDimensionOfACard() {
          return dimensionOfACard;
     }
     
     public static Image getACardImage(int index) {
          String curDir = System.getProperty("user.dir");
          String pathName = curDir + "/classic_cards/";
          
          Image pic = Toolkit.getDefaultToolkit().getImage(pathName + cardImageNames[index]);
          return pic;
     }
     
     public static Image getFaceDownCardImage() {
          String curDir = System.getProperty("user.dir");
          String pathName = curDir + "/classic_cards/";
          
          Image pic = Toolkit.getDefaultToolkit().getImage(pathName + cardImageNames[cardImageNames.length - 1]);
          return pic;
     }
     
//Load cards into MediaTracker
     public static void loadAndSetUpAllCardImages(JComponent theJPanelInstance) {
          imagesIsLoaded = false;
          
          MediaTracker tracker = new MediaTracker(theJPanelInstance);
          
          cardImageNames = getArrayOfCardFileNames();
          loadAllTheCardImagesFromFileNames(cardImageNames, tracker);
          
          String curDir = System.getProperty("user.dir");
          String pathName = curDir + "/classic_cards/";
          
          Image singlePic = Toolkit.getDefaultToolkit().getImage(pathName + cardImageNames[0]);
          //singlePic.setWidth(72);
          //singlePic.setHeight(96);
          dimensionOfACard = new Dimension(singlePic.getWidth(theJPanelInstance), singlePic.getHeight(theJPanelInstance));
     }
//Helper methods to load the images using MediaTracker
     private static String[] getArrayOfCardFileNames() {
          String[] cardImageNames = new String[TOTAL_NUMBER_OF_CARDS + 1];
          int suitNum = 0;
          int cardValue = 1;
          
          for (int i = 0; i < cardImageNames.length; i++) {
               if( cardValue < 10 ) {
                    cardImageNames[i] = new String("c" + suitNum + "_0" + cardValue + ".png");
               } else {
                    cardImageNames[i] = new String("c" + suitNum + "_" + cardValue + ".png");
               }
               
               if( cardValue >= CARDS_IN_EACH_SUIT) {
                    suitNum++;
                    cardValue = 1;
               }else{
                    cardValue++;
               }
          }
          
          cardImageNames[TOTAL_NUMBER_OF_CARDS] = new String("faceDown.png");
          
          return cardImageNames;
     }
     
     private static void loadAllTheCardImagesFromFileNames(String[] cardImageNames, MediaTracker tracker) {
          String curDir = System.getProperty("user.dir");
          Image pic;
          
          for (int i=0; i < cardImageNames.length; i++) {
               pic = Toolkit.getDefaultToolkit().getImage(curDir + "/classic_cards/" + cardImageNames[i]);
               tracker.addImage(pic, i);
          }
          
          try {
               tracker.waitForAll();
               imagesIsLoaded = true;
          } catch(java.lang.InterruptedException e) {
               
          }
     }
}