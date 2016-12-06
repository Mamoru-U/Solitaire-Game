import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Solitaire {
     private static int JFRAME_WIDTH = SolitaireJPanel.JFRAME_WIDTH;
     private static int JFRAME_HEIGHT = SolitaireJPanel.JFRAME_HEIGHT;;
     
     public static void main(String[] args) {
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          int width = (int)screenSize.getWidth() - JFRAME_WIDTH;
          int height = (int)screenSize.getHeight() - JFRAME_HEIGHT;
          
          JFrame gui = new SolitaireJFrame("Solitaire", width/2, height/2, JFRAME_WIDTH, JFRAME_HEIGHT);
     }
}

class SolitaireJFrame extends JFrame {
     public SolitaireJFrame(String title, int x, int y, int width, int height) {
          setTitle(title);
          setLocation(x, y);
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          
//content of the window
          JPanel frameContent = new SolitaireJPanel();
          Container visibleArea = getContentPane();
          visibleArea.add(frameContent);
          
//Set the size of the content pane of the window, resize and validate the
//window to suit, obtain keyboard focus, and then make the window visible.
          frameContent.setPreferredSize(new Dimension(width, height));
          pack();
          frameContent.requestFocusInWindow();
          setVisible(true);
     }
}