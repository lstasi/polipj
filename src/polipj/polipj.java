package polipj;

import javax.swing.UIManager;
import java.awt.*;

/**
 * <p>Title: Pol-IPj</p>
 * <p>Description: Pol-IP Client Java</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Leandro Stasi
 * @version 1.0
 */

public class polipj {
  private boolean packFrame = false;

  //Construct the application
  public polipj() {
    FrameMain frame = new FrameMain();
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    //frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setLocation(screenSize.width -200  , 30);
    frame.setVisible(true);
  }
  //Main method
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new polipj();
  }
}