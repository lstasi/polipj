package polipj;

import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import java.util.*;
/**
 * <p>Title: Pol-IPj</p>
 * <p>Description: Pol-IP Client Java</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Leandro Stasi
 * @version 1.0
 */

class Core extends TimerTask{

  private InetAddress addr = null, local = null;
  private String  MCastIn= "230.0.0.2";
  private String  MCastOut= "230.0.0.1";
  private MulticastSocket MSocketin = null;
  private DatagramSocket Socketout = null;
  private DatagramPacket PacketIn = null ;
  protected byte [] mensaje = new byte[8192];;
  private int count=0;
  public boolean newData;
  public boolean newError;
  public int ActualSpeed=0;
  public int AsingSpeed=0;
  public int LinkSpeed=0;
  public String Error=null;


  public Core() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception  {

    /*
     *Socket de Entrada
     */
    PacketIn = new DatagramPacket(mensaje,mensaje.length);
    try{
       addr = InetAddress.getByName(MCastIn);
    }
    catch (UnknownHostException uhe) {
      System.err.println(uhe.getMessage());
    }
    try{
      MSocketin = new MulticastSocket(20002);
      MSocketin.joinGroup(addr);
      //MSocketin.setSoTimeout(500);
    }
    catch(IOException uhe) {
      System.err.println(uhe.getMessage());
    }
    /*
     *Socket de salida
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

  }
  public void run(){

    while(true){
      try{
        MSocketin.receive(PacketIn);
      }
      catch(IOException uhe){
        mensaje=uhe.getMessage().getBytes();
      }
      this.Parser();
    }
  }
  public void SendM(String mensajes){
     DatagramPacket Paquete  = new DatagramPacket((mensajes).getBytes(),(mensajes).getBytes().length,addr,20002);
   /*
    *Envio el paquete
    */
    try{
      Socketout.send(Paquete);
    }
    catch(IOException uhe){
      System.err.println(uhe.getMessage());
    }
  }
  private void Parser(){

    String data = new String(mensaje);

    int Index1=0;
    int Index2=0;

    if(Character.isDigit(data.charAt(0))){
      Index1=data.indexOf(" ");
      if(Index1>0)//Look For the first blank space
        try{
        LinkSpeed=Integer.parseInt(data.substring(0,Index1));
        }
        catch(NumberFormatException uhe){
          newError=true;
          Error="Error 105";
          return;
        }
      else{//Else if no first blank space
        newError=true;
        Error="Error 105";
        return;
      }
      /*
      *Direccion Local Para buscar en el paquete
      */
      try{
       local = InetAddress.getLocalHost();
      }
      catch (UnknownHostException uhe) {
        System.err.println(uhe.getMessage());
      }

      Index1=data.indexOf(local.getHostAddress());
      if(Index1>0){
        Index1=data.indexOf("/",Index1);
        if(Index1>0){
          Index2=data.indexOf("/",Index1+1);
          try{
            ActualSpeed=Integer.parseInt(data.substring(Index1+1,Index2));
          }
          catch(NumberFormatException uhe){
            newError=true;
            Error="Error 107";
            return;
          }
        }
        Index1=data.indexOf("/",Index2);
        if(Index1>0){
          Index2=data.indexOf("/",Index1+1);
          try{
            AsingSpeed=Integer.parseInt(data.substring(Index1+1,Index2));
          }
          catch(NumberFormatException uhe){
            newError=true;
            Error="Error 108";
            return;
          }
          newData=true;
        }
      }
      else{//Else if no found IP
        newError=true;
        Error="Error 106";
        return;
      }
    }
    else if(data.startsWith("Repor")){
     /*
      *If recive a Report send start
      */
      this.SendM("Start");
    }
    else{//Else from IsDigit
      newError=true;
      Error=data;
    }
  }
}

