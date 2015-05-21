/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioProvider;
import duxmancar.CProperties;
import duxmancar.Raspberry.Hardware.Vision.CCirculos;
import duxmancar.Raspberry.Hardware.Vision.CDetectarCirculos;
import duxmancar.log.CLog;
//import duxmancar.Raspberry.Hardware.Vision.DetectarCirculos;
import java.util.Scanner;
import org.apache.log4j.Logger;

/**
 *
 * @author aduce
 */
public class L298NTest
{

  /**
   * @param args the command line arguments
   */
  private static CMotorControlPuenteH m_motores;
  //private static CMedidorDistancia m_sensorDistancia;
  final static GpioController GPIO_CONTROLLER = GpioFactory.getInstance ();
  final static GpioProvider GPIO_PROVIDER = GpioFactory.getDefaultProvider ();
  static int velocidad=25;
  static CDetectarCirculos m_circulos;
  static Logger m_log;
  static CMedidorDistancia m_sensorDistancia;
  public static void main (String[] args)
  {
    CLog claselog = new CLog(false, CProperties.FICHERO_LOG, CProperties.MAX_SIZE_LOG, CProperties.MAX_FILES_LOG);
    m_log = Logger.getRootLogger();
    try
    {        
        //m_circulos.start();
        
        //System.load("/home/pi/v1/lib/libdio.so");
        m_sensorDistancia =  new CMedidorDistancia(27,17);
        System.out.print ("Probamos PWM SOFT");
        m_motores = new CMotorControlPuenteH ();
        m_motores.inicializa (GPIO_CONTROLLER, true);
        
        System.load("/home/pi/v1/lib/libopencv_java300.so" );                        
        m_circulos = new CDetectarCirculos();
        pruebas ();
    }
    catch(Exception e)
    {
       m_log.info(e.getMessage());
    }
    
    GPIO_CONTROLLER.shutdown ();
  }

  private static void showHelp ()
  {
    System.out.print ("Comando : \r\n(Marcha adelante +/- W)\r\n(Marcha atras +/- S)\r\n");
    System.out.print ("(Paro X)\r\n(Punto muerto Z)\r\n");
    System.out.print ("(Giro derecha 1 D1)\r\n(Giro derecha 2 D2)\r\n(Giro derecha 3 D3)\r\n");
    System.out.print ("(Giro Izquierda 1 A1)\r\n(Giro Izquierda 2 A2)\r\n(Giro Izquierda 3 A3)\r\n");
    System.out.print ("\r\n(Distancia P)\r\n");
    System.out.print ("\r\n(Circulos C)\r\n");
    System.out.print ("\r\n(Ayuda H)\r\n");
    System.out.print ("\r\n(Salir Q)\r\n");
  } 
 
  public static void pruebas () throws Exception
  {
    
    Scanner sc = new Scanner (System.in);
    String comando = "";

    showHelp ();
    while (comando.equals ("Q") == false)
    {
      if (sc.hasNextLine ())
      {
        comando = sc.nextLine ().toUpperCase ();
        if (comando.equals ("H"))
        {
          showHelp ();
        }
        if (comando.equals ("W"))
        {
          m_motores.marchaMotor (true, velocidad);                           
          while (comando.equals ("Q") == false)
          {
              if (sc.hasNextLine ())
              {
                comando = sc.nextLine ().toUpperCase ();
                if (comando.equals ("+"))
                {
                  velocidad +=5;
                }
                if (comando.equals ("-"))
                {
                  velocidad -=5;             
                }
                System.out.println("Velocidad " + velocidad + "\r\n");
                m_motores.marchaMotor (true, velocidad);
              }
          }
          comando="";
          
        }
        if (comando.equals ("S"))
        {
          m_motores.marchaMotor (false, velocidad);
           while (comando.equals ("Q") == false)
          {
              if (sc.hasNextLine ())
              {
                comando = sc.nextLine ().toUpperCase ();
                if (comando.equals ("+"))
                {
                  velocidad +=5;
                }
                if (comando.equals ("-"))
                {
                  velocidad -=5;             
                }
                System.out.println("Velocidad " + velocidad + "\r\n");
                m_motores.marchaMotor (false, velocidad);
              }
          }
          comando="";
            
        }
        if (comando.equals ("Z"))
        {
          m_motores.puntoMuertoMotor ();
        }
        if (comando.equals ("X"))
        {
          m_motores.paroMotor ();
        }
        if (comando.equals ("A1"))
        {
          m_motores.girarIzquierda (1);
        }
        if (comando.equals ("A2"))
        {
          m_motores.girarIzquierda (2);
        }
        if (comando.equals ("A3"))
        {
          m_motores.girarIzquierda (3);
        }
        if (comando.equals ("D1"))
        {
          m_motores.girarDerecha (1);
        }
        if (comando.equals ("D2"))
        {
          m_motores.girarDerecha (2);
        }
        if (comando.equals ("D3"))
        {
          m_motores.girarDerecha (3);
        }
        
        if (comando.equals ("P"))
        {            
         
            double d = m_sensorDistancia.pulse();
            m_log.info("Distancia :" + d );                                   
        }        
        
        if (comando.equals ("C"))
        {                     
            m_circulos.callDetectar();
            m_log.info(CCirculos.texto());
        }   
      }       
    }
    m_motores.puntoMuertoMotor ();
    m_sensorDistancia.close();
  }
}
;