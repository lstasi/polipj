package polipj;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import java.net.*;
import java.io.*;
import java.util.*;



/**
 * <p>Title: Pol-IPj</p>
 * <p>Description: Pol-IP Client Java</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Leandro Stasi
 * @version 1.0
 */

public class FrameMain extends JFrame implements ActionListener{

  private JPanel contentPanePanel;
  private JPanel jPanelBar = new JPanel();
  private JLabel jLabelSpeed = new JLabel();
  private JLabel jLabelStatus = new JLabel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanelStatus = new JPanel();
  private JProgressBar jProgressBarSpeed = new JProgressBar();
  private PaneLayout paneLayout1 = new PaneLayout();
  private javax.swing.Timer Tiempo = null;
  private XYLayout xYLayout1 = new XYLayout();
  private JLabel jLabelMark = new JLabel();
  private int count;//Counter for no recive data
  private Point Punto;//Used to get progressbar position
  private java.util.Timer TiempoThread=new java.util.Timer();
  private Core Centro = new Core();

  //Construct the frame
  public FrameMain() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    enableEvents(AWTEvent.FOCUS_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  //Component initialization
  private void jbInit() throws Exception  {

    jPanelStatus.setLayout(paneLayout1);
    jLabelStatus.setBorder(BorderFactory.createEtchedBorder());
    jLabelStatus.setHorizontalAlignment(SwingConstants.CENTER);
    jLabelStatus.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabelSpeed.setBorder(BorderFactory.createEtchedBorder());
    jLabelSpeed.setToolTipText("");
    //setIconImage(Toolkit.getDefaultToolkit().createImage(FrameMain.class.getResource("[Your Icon]")));
    contentPanePanel = (JPanel) this.getContentPane();
    jLabelSpeed.setHorizontalAlignment(SwingConstants.LEFT);
    jLabelSpeed.setHorizontalTextPosition(SwingConstants.LEFT);
    jLabelSpeed.setText(" 0.0 Kb/s");
    contentPanePanel.setLayout(borderLayout1);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setResizable(false);
    this.setSize(new Dimension(148, 72));
    this.setTitle("Pol-IP");
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowOpened(WindowEvent e) {
        this_windowOpened(e);
      }
      public void windowClosing(WindowEvent e) {
        this_windowClosing(e);
      }
    });
    jPanelBar.setLayout(xYLayout1);
    jProgressBarSpeed.setBorder(BorderFactory.createLoweredBevelBorder());
    jProgressBarSpeed.setMaximum(1000);
    jProgressBarSpeed.setString("");
    jProgressBarSpeed.setStringPainted(true);
    jLabelMark.setBackground(Color.red);
    jLabelMark.setForeground(Color.red);
    jLabelMark.setBorder(BorderFactory.createLineBorder(Color.black));
    jLabelMark.setOpaque(true);
    jLabelMark.setRequestFocusEnabled(false);
    jPanelStatus.setMinimumSize(new Dimension(109, 18));
    jPanelStatus.setPreferredSize(new Dimension(109, 18));
    contentPanePanel.add(jPanelBar,  BorderLayout.NORTH);
    jPanelBar.add(jProgressBarSpeed,         new XYConstraints(1, 4, 142, 9));
    jPanelBar.add(jLabelMark,           new XYConstraints(1, 0, 8, 18));
    contentPanePanel.add(jPanelStatus, BorderLayout.SOUTH);
    jPanelStatus.add(jLabelSpeed, new PaneConstraints("jLabelSpeed", "jLabelSpeed", PaneConstraints.ROOT, 0.5f));
    jPanelStatus.add(jLabelStatus, new PaneConstraints("jLabelStatus", "jLabelSpeed", PaneConstraints.RIGHT, 0.5202703f));
    /*
    *Timer to update form data
    */

    Tiempo = new javax.swing.Timer(1000,this);
    Tiempo.start();
    /*
    *Timer for the thread
    */
    TiempoThread.schedule(Centro,500);

  }
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }


  void this_windowOpened(WindowEvent e) {

    this.Centro.SendM("Start");
  }

  void this_windowClosing(WindowEvent e) {
    this.Centro.SendM("Stop");
  }
  public void actionPerformed(ActionEvent Evento){
    /*
    *Check For new Data
    */
    if(Centro.newData){

      String datos=null;
      float fSpeed;
      //Recive new data reset the counter
      count=0;

      fSpeed=Centro.ActualSpeed;
      fSpeed=fSpeed/1024;
      datos=Float.toString(fSpeed);
      if(datos.indexOf(".")!=-1)
        if(datos.length() > (datos.indexOf(".")+3))
          datos = datos.substring(0,datos.indexOf(".")+3);
      if(this.getState()==this.NORMAL)
        setTitle("Pol-IP");
      else
        setTitle(datos + " Kb/s ");

      jLabelSpeed.setText(" " + datos + " Kb/s");

      jProgressBarSpeed.setMaximum(Centro.LinkSpeed);
      Punto=jProgressBarSpeed.getLocation();

      jLabelMark.setLocation(Punto.x + ((Centro.AsingSpeed * jProgressBarSpeed.getWidth())
		/Centro.LinkSpeed),0);

      if(Centro.ActualSpeed > Centro.LinkSpeed)
        jProgressBarSpeed.setValue(Centro.LinkSpeed);
      else
        jProgressBarSpeed.setValue(Centro.ActualSpeed);

      Centro.newData=false;

    }
    else{
      Centro.newError=true;
      Centro.Error="Error 101";
    }
    /*
    *Check For New Errors
    */
    if(Centro.newError){
      //If error is socket unblock with no data
      if(Centro.Error=="Error 101")
      {
        //If it has been 10 time probably server is off
        if(count>10)
        {
          jLabelStatus.setText("No Server");
          jLabelSpeed.setText("");
          jProgressBarSpeed.setValue(0);
          Punto=jProgressBarSpeed.getLocation();
          jLabelMark.setLocation(Punto.x ,0);
          Centro.SendM("Start");
        }
        else
        {
          if(count==0)//If 0 means data arrived so clean errors
            jLabelStatus.setText("");
          else if(count==3)//three time say no data warning
            jLabelStatus.setText("No Data");
          else if(count>4)//More times draw points
            jLabelStatus.setText(jLabelStatus.getText() + ".");
          count++;
        }
      }
      else
      {
        //Another error show it
        jLabelStatus.setText(Centro.Error);
        jLabelSpeed.setText("");
        jProgressBarSpeed.setValue(0);
        Punto=jProgressBarSpeed.getLocation();
        jLabelMark.setLocation(Punto.x ,0);
        Centro.SendM("Start");
      }
        Centro.newError=false;
    }

  }
}