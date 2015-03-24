/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emu;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.swing.ImageIcon;

/**
 *
 * @author duxman
 */
public class CGPIOEmu
{
   

    
   static Map<String, String> m_hPortNameGPIO;
   static Properties     m_properties;
   
  static ImageIcon m_iconoOFF;
  static ImageIcon m_iconoVerdeOFF;
  static ImageIcon m_iconoRojoOFF;
  static ImageIcon m_iconoVerdeON;
  static ImageIcon m_iconoRojoON;
   
   public static void main (String args[])
   {

      m_hPortNameGPIO = ( Map<String, String> )  Collections.synchronizedMap (new HashMap<String, String>() );
      leerProperties();
      cargarIconos();
      
       java.awt.EventQueue.invokeLater (new Runnable ()
    {
      public void run ()
      {
        new CFormEmulator ().setVisible (true);
      }
    });
   }
   
   private static void cargarIconos()
   {
        m_iconoVerdeOFF =   crearIcono ("./img/verdeOFF.png", "VerdeOff");
        m_iconoVerdeON  =   crearIcono ("./img/verdeON.png", "VerdeOn");
        m_iconoRojoOFF  =   crearIcono ("./img/rojoOFF.png", "rojoOff");
        m_iconoRojoON   =   crearIcono ("./img/rojoON.png", "rojoOn");
        m_iconoOFF      =   crearIcono ("./img/OFF.png", "Off");    
   }
   
   private static ImageIcon crearIcono(String path, String description) 
   {
     
       return new ImageIcon(path, description);     
   }
   
   private static void leerProperties()
   {
      m_properties = new Properties();
      InputStream input = null;      
      try 
      {

        input = new FileInputStream("RaspiEmu.properties"); 
        // load a properties file
        m_properties.load(input);        
      } 
      catch (Exception ex) 
      {
        ex.printStackTrace();
      } 
      finally 
      {
        if (input != null) 
        {
          try 
          {
            input.close();
          } 
          catch (Exception e) 
          {
            e.printStackTrace();
          }
        }
      }       
   }
   
    public Map<String,String> obtenerListaGPIO()
   {
      Enumeration em = m_properties.keys();
      while(em.hasMoreElements())
      {
        String key    = (String)em.nextElement();
        String value  = (String)m_properties.get(key);        
        m_hPortNameGPIO.put (key, value);                
      }
      return  m_hPortNameGPIO;    
   }
    
}
