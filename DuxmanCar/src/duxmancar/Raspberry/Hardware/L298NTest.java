/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware;

import duxmancar.Raspberry.Hardware.ControlMotores.CMotorControlPuenteH;
import duxmancar.Raspberry.Hardware.Sensores.Distancia.CMedidorDistancia;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioProvider;
import duxmancar.Automatico.CAutoDetectarObstaculos;
import duxmancar.CProperties;
import static duxmancar.CProperties.HAAR_DERECHA;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CObstaculo;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CDetectarCirculos;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CDetectarFormas;
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
  static CDetectarFormas m_formas;
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
        m_sensorDistancia =  CMedidorDistancia.getInstance();
        System.out.print ("Probamos PWM SOFT");
        m_motores = CMotorControlPuenteH.getInstance();
        m_motores.inicializa (GPIO_CONTROLLER, true);
        
        System.load("/home/pi/v1/lib/libopencv_java2410.so" );                        
        m_formas = new CDetectarFormas();
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
            CAutoDetectarObstaculos m_detectorObstaculos = new CAutoDetectarObstaculos();
            m_detectorObstaculos.setPause(false);
            m_detectorObstaculos.setSalir(false);
            m_detectorObstaculos.start();
            while(true) CObstaculo.texto();
        }   
      }       
    }
    m_motores.puntoMuertoMotor ();
    m_sensorDistancia.close();
  }
}
;