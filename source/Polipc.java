import javax.swing.*;	
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;


public class Polipc {
    
    public static void main (String args[]) {
		
		/*
		 Graphics Enviroment for Frame Size
		 */
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		Rectangle bounds = gc.getBounds();
	    
	    /*
	     *Main Object Frame,Label,Button,ProgressBar,Core.
	     */
	    PolipcFrame MainFrame = new PolipcFrame(bounds);
	    MainFrame.show();
	    while(true){
	    	try{
	    		MainFrame.run();
	    	}
	    	catch(IndexOutOfBoundsException uhe){
	    		
	    	}
	    } 
	    
 }
}

class PolipcFrame extends JFrame implements ActionListener{
	
	private Timer Tiempo = null;
	
	private InetAddress addr = null, local = null;
	private DatagramPacket PStart = null,PStop = null;
 	private DatagramSocket Socketout = null;
 	private MulticastSocket MSocketin = null;
    private DatagramPacket PacketIn = null ;
 	private String  MCastIn= "230.0.0.2";
 	private String  MCastOut= "230.0.0.1";
 	private JLabel  Speed= new JLabel();
	private	JButton Boton= new JButton("Start");
	private	JPanel  TopPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
	private	JProgressBar PrgBar = new JProgressBar();
	
	public PolipcFrame(Rectangle bounds){
		/*
		 *Parametros para el Frame
		 */
	    setResizable(false);
		setSize(200,90);	
	    setLocation(bounds.width -300  ,bounds.y + 30);
		setTitle("Pol-IPc");
		
		TopPanel.add(Speed);
		TopPanel.add(Boton);
		
		getContentPane().add ("South",Box.createRigidArea(new Dimension(5,5)));
		getContentPane().add ("West",new JLabel("  "));
		getContentPane().add ("East",new JLabel("  "));
		
		getContentPane().add ("North",TopPanel);
		getContentPane().add("Center",PrgBar);
		
		PrgBar.setMinimum(0);
		PrgBar.setMaximum(10000);
		
		Boton.addActionListener(this);
	    Boton.setActionCommand("Start");
		Tiempo = new Timer(300000,this);
		/*
		 *Mensaje de Stop al salir
		 */
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				InetAddress addr = null;
				DatagramPacket paqueteout = null;
 				DatagramSocket out = null;
 				String MCast= "230.0.0.1";
		        try{
					addr = InetAddress.getByName(MCast);
					paqueteout = new DatagramPacket(("Stop").getBytes(),("Stop").getBytes().length,addr,20002);	
					out = new DatagramSocket();
				   	out.send(paqueteout);
				}
				catch (UnknownHostException uhe) {
					    System.err.println(uhe.getMessage());
					}
			    catch(SocketException uhe) {
				    	System.err.println("Create Socket");
				    }
				catch(IOException uhe){
				    	System.err.println("Sending");
				    } 	
				System.exit(0);}
			});

		/*
		 *Socket de Salida
		 */
		try{
			addr = InetAddress.getByName(MCastOut);
			Socketout = new DatagramSocket();
			
		}
		catch(SocketException uhe) {
			System.err.println(uhe.getMessage());
		}	
		catch (UnknownHostException uhe) {
		    System.err.println(uhe.getMessage());
		}
		/*
		 *Paquetes de Salida
		 */
		PStart = new DatagramPacket(("Start").getBytes(),("Start").getBytes().length,addr,20002);	
		PStop  = new DatagramPacket(("Stop").getBytes(),("Stop").getBytes().length,addr,20002);	
	    
        /*
         *Socket de Entrada
         */
        try{
			addr = InetAddress.getByName(MCastIn);
		}
		catch (UnknownHostException uhe) {
			    System.err.println(uhe.getMessage());
		}
		try{
	    	MSocketin = new MulticastSocket(20002);
	    	MSocketin.joinGroup(addr);
	    	MSocketin.setSoTimeout(1000);
   	   	}
	   	catch(IOException uhe) {
		    	System.err.println(uhe.getMessage());
	    }
	    /*
	     *Direccion Local
	     */
	    try{
			local = InetAddress.getLocalHost();
		}
		catch (UnknownHostException uhe) {
		    System.err.println(uhe.getMessage());
		}
	}
	
	
	public void actionPerformed(ActionEvent Evento){
		
		if(Evento.getActionCommand()==null)
			Evento = new ActionEvent(Tiempo,0,"Start");
		
		if (Evento.getActionCommand().equals("Start")){
			
			Tiempo.start();
			try{
				Socketout.send(PStart);
			}
			catch(IOException uhe){
				    	System.err.println(uhe.getMessage());
			} 	
			Boton.setText("Stop ");
			Boton.setActionCommand("Stop ");
			
		}
		if (Evento.getActionCommand().equals("Stop ")){
			
			Tiempo.stop();
			try{
				Socketout.send(PStop);
			}
			catch(IOException uhe){
				    	System.err.println(uhe.getMessage());
		    }
		    Boton.setText("Start");
			Boton.setActionCommand("Start");
		 } 	
			
	}
	
	
	public void run() throws IndexOutOfBoundsException{
	
		DatagramPacket PacketIn = null;
		
		byte [] mensaje = new byte[8192];
	   	String datos = null;
		
		int Index1;
		int Index2;
		int Index3; 
		int Index4;
		int count=0;
		
		PacketIn = new DatagramPacket(mensaje,mensaje.length);
		
		while(true){
			
			if(Boton.getText().equals("Stop ")){
			
				try{	
					Arrays.fill(mensaje,Byte.parseByte("0"));
					MSocketin.receive(PacketIn);
					count=0;
				}
				catch(SocketTimeoutException uhe){
					Arrays.fill(mensaje,Byte.parseByte("0"));
		   			count++;
		    		if(count>5){
		    			actionPerformed(new ActionEvent(Boton,1001,"Start"));
		 	    		count=0;
		 	    		Speed.setText("Disconnected!!!");
		 	    		if(isFocused())
		   					setTitle("Pol-IPc");
		   				else
		   					setTitle("Disconnected!!!");
		    		}
				
				}
				catch(IOException uhe){
					System.err.println(uhe.getMessage());
		 			
				}
				datos = new String(mensaje);
		    
			    if(datos.indexOf(0)<=0){
			    	datos="0".toString();
			    }
			    else
			    {
				    datos = datos.substring(0,datos.indexOf(0));
		   			
		   			if(datos.indexOf(" ")>0)
		   				PrgBar.setMaximum(Integer.parseInt(datos.substring(0,datos.indexOf(" "))));
		   			
		   			Index1=datos.indexOf(local.getHostAddress());
		   			if(Index1>0){
		   				Index2=datos.indexOf("/",Index1);
		   				if(Index2>0){
		   					Index3=datos.indexOf("/",Index2+1);
		   					if(Index3>0){
		   						datos=datos.substring(Index2+1,Index3);
			   					PrgBar.setValue(Integer.parseInt(datos));	
			   					datos = Float.toString(Float.parseFloat(datos)/1024);
	
		   						if(datos.indexOf(".")!=-1)
		   							if(datos.length() > (datos.indexOf(".")+3))
		   					 			datos = datos.substring(0,datos.indexOf(".")+3);
		   						Speed.setText(datos + " Kb/s ");
		   						if(isFocused()){
		   							setTitle("Pol-IPc");
		   						}
		   						else
		   							setTitle(datos + " Kb/s ");
		   					}
		   				}
		   			}
		   			TopPanel.validate();
	   			}
	   			
	   		}
			else if(Boton.getText().equals("Start")){
				
				setTitle("Pol-IPc");	 		
		 		Speed.setText("");
	   			//TopPanel.validate();
			}
	
		}//Fin del While
	}//Fin de Run()
}//Fin de la clase
