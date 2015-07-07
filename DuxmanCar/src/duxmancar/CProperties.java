/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author duxman
 */
public class CProperties
{
   public static  int PUERTO_CONTROLADOR_SERVOS = 0x40;
   public static  int PUERTO_CONTROLADOR_LED    = 0x20;
   public static  int FREQ_CONTROLADOR_SERVOS   = 60;
   public static  int FREQ_CONTROLADOR_LED      = 60;   
   public static  int POSICION_SERVO_DER     =   0;
   public static  int POSICION_SERVO_IZQ     =   1;   
   public static  int POS_OFF_SERVO          =   0;   
   public static  int POS_CERO               =   0;      
   public static  int POS_MAX_DELANTE        =   0;
   public static  int POS_MAX_DETRAS         =   0;        
   public static  int POS_MED_DELANTE        =   0;
   public static  int POS_MED_DETRAS         =   0;        
   public static  int POS_MIN_DELANTE        =   0;
   public static  int POS_MIN_DETRAS         =   0;     
   
   public static  String    FICHERO_LOG         =   "DuxmanCar.log";
   public static  int       MAX_SIZE_LOG        =    10000;
   public static  int       MAX_FILES_LOG       =    5;
   public static  String    HAAR_DERECHA        =    "HarrDerecha.xml";
   public static  String    HAAR_IZQUIERDA      =    "HarrIzquierda.xml";
   public static  String    HAAR_STOP           =    "HarrStop.xml";
   public static  String    HAAR_ATRAS          =    "HarrAtras.xml";
   
   
   
   
   
   	
   public CProperties(String sFile)
   {
       Properties prop = new Properties();
       FileInputStream  input = null;
        
        try 
        {
           input = new FileInputStream("config.properties");           
           prop.load(input);
           
           PUERTO_CONTROLADOR_SERVOS = Integer.valueOf(prop.getProperty("PUERTO_CONTROLADOR_SERVOS","0x40"));
           PUERTO_CONTROLADOR_LED   = Integer.valueOf(prop.getProperty("PUERTO_CONTROLADOR_LED","0x20"));
           FREQ_CONTROLADOR_SERVOS  = Integer.valueOf(prop.getProperty("FREQ_CONTROLADOR_SERVOS","60"));
           FREQ_CONTROLADOR_LED     = Integer.valueOf(prop.getProperty("FREQ_CONTROLADOR_LED","60"));
           POSICION_SERVO_DER       = Integer.valueOf(prop.getProperty("POSICION_SERVO_DER","0"));
           POSICION_SERVO_IZQ       = Integer.valueOf(prop.getProperty("POSICION_SERVO_IZQ","1"));
           POS_OFF_SERVO            = Integer.valueOf(prop.getProperty("POS_OFF_SERVO","0"));
           POS_CERO                 = Integer.valueOf(prop.getProperty("POS_CERO_SERVO_DER","380"));           
           POS_MAX_DELANTE          = Integer.valueOf(prop.getProperty("POS_MAX_DELANTE","50"));
           POS_MAX_DETRAS           = Integer.valueOf(prop.getProperty("POS_MAX_DETRAS","620"));
           POS_MED_DELANTE          = Integer.valueOf(prop.getProperty("POS_MED_DELANTE","320"));
           POS_MED_DETRAS           = Integer.valueOf(prop.getProperty("POS_MED_DETRAS","480"));
           POS_MIN_DELANTE          = Integer.valueOf(prop.getProperty("POS_MIN_DELANTE","340"));
           POS_MIN_DETRAS           = Integer.valueOf(prop.getProperty("POS_MIN_DETRAS","410"));           
           
           FICHERO_LOG           = prop.getProperty("FICHERO_LOG","./DuxmanCar.log");
           MAX_SIZE_LOG          = Integer.valueOf(prop.getProperty("POS_MIN_DELANTE","10000"));
           MAX_FILES_LOG         = Integer.valueOf(prop.getProperty("POS_MIN_DETRAS","5"));           
           
           HAAR_DERECHA           = prop.getProperty("HAAR_DERECHA","HarrDerecha.xml");
           HAAR_IZQUIERDA         = prop.getProperty("HAAR_IZQUIERDA","HarrIzquierda.xml");
           HAAR_STOP              = prop.getProperty("HAAR_STOP","HarrStop.xml");
           //HAAR_ATRAS             = prop.getProperty("HAAR_ATRAS","HarrAtras.xml");
           
           
                      
           
        }
        catch(Exception e)
        {
            System.out.println("Error : leyendo fichero properties");
        }
        finally 
        {
            if (input != null) {
            try 
            {
		input.close();
            } 
            catch (IOException e) 
            {
		 System.out.println("Error : cerrando fichero properties");
            }
	}
    }
 
 
       
   }
               
}
