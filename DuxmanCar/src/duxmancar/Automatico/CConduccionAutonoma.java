/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Automatico;

import duxmancar.CProperties;
import duxmancar.Datos.CDato;
import duxmancar.Datos.CListaDatosProvider;
import duxmancar.Datos.CTelegramasAutomatico;
import duxmancar.Raspberry.Hardware.ControlMotores.CMotorControlPuenteH;
import static duxmancar.Raspberry.Hardware.GPIO.CGpioPines.*;
import duxmancar.Raspberry.Hardware.Sensores.Distancia.CMedidorDistancia;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CObstaculo;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CDetectarCirculos;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CDetectarFormas;
import duxmancar.util.IDatosGenerales;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CConduccionAutonoma extends Thread implements IDatosGenerales
{
    public static int TIEMPO_ENTRE_MEDICIONES = 200;
    public static int TIEMPO_EJECUCION = 1000;
    public static int DISTANACIA_MINIMA = 30;         
     
    private CListaDatosProvider m_listaDatosProvider;
    private CDetectarFormas m_detectorObstaculos;
    private CMedidorDistancia m_medidorDistancia;
     
    private boolean m_salir = true;
    private Logger m_log;
    private CMotorControlPuenteH m_gestorMotoresDc;
     
    public CConduccionAutonoma () throws Exception
    {        
         m_log = Logger.getRootLogger();
         m_listaDatosProvider = CListaDatosProvider.getInstance();
         m_detectorObstaculos = new CDetectarFormas();
         m_medidorDistancia = new CMedidorDistancia(GPIO_PIN_TRIGGER, GPIO_PIN_HECHO);
         m_gestorMotoresDc = CMotorControlPuenteH.getInstance();         
    }
     
    public synchronized void setSalir( boolean  salir )
    {
        m_salir = salir;
    }
     
     public void run()
     {
         while(m_salir == false)
         {
             try
             {                                                                                    
                 
                 comprobar();                                 
                 Thread.sleep( TIEMPO_ENTRE_MEDICIONES  );
                 m_log.info("Paramos y esperamos nueva orden");                                  
                 m_gestorMotoresDc.paroMotor();                 
             }
             catch ( Exception ex)
             {
                 m_log.error(ex.getMessage());
             }
         }
         
         try
         {
             m_log.info("Obstaculo no detectado paramos y giramos 180 grados");
             //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.STOP ,40 ) );                    
             m_gestorMotoresDc.paroMotor();
             Thread.sleep( TIEMPO_EJECUCION / 5  );
         }
         catch (Exception ex)
         {
            m_log.error(ex.getMessage());
         }
     }
     
     public synchronized void comprobar() throws Exception
     {
         wait();
         m_log.info("Decidimos Accion ...");                          
         if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.NONE  )             
         {
              if( m_medidorDistancia.getDistanciaMedia() <= DISTANACIA_MINIMA )
              {
                 m_listaDatosProvider.addMensajeSalida(CDato.CodificaMensajeInfo(CObstaculo.hayObstaculoCentro(), m_medidorDistancia.getDistanciaMedia()));
                 
                 if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.DERECHA )
                 {
                      m_log.info("Giramos Derecha");                      
                      //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      m_gestorMotoresDc.girarRuedas(-40, 40);
                      Thread.sleep( TIEMPO_EJECUCION  );
                      notifyAll();   
                 }
                 if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.IZQUIERDA )
                 {
                      m_log.info("Giramos Derecha");
                      //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      m_gestorMotoresDc.girarRuedas(40, -40);
                      Thread.sleep( TIEMPO_EJECUCION  );
                      notifyAll();   
                 }
                 if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.PARO )
                 {
                      m_log.info("Giramos Derecha");
                      //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      m_gestorMotoresDc.paroMotor();                      
                      notifyAll();   
                 }
                 if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.ATRAS )
                 {
                      m_log.info("Giramos Derecha");
                      //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      m_gestorMotoresDc.marchaMotor(false, 25);
                      Thread.sleep( TIEMPO_EJECUCION/4  );
                      m_gestorMotoresDc.girarRuedas(-40, 40);
                      Thread.sleep( TIEMPO_EJECUCION*2  );
                      m_gestorMotoresDc.marchaMotor(true, 40);
                      Thread.sleep( TIEMPO_EJECUCION );
                      notifyAll();   
                 }
              }
                  /*  if( CObstaculo.hayObstaculoDerecha())
                  {
                      if( CObstaculo.hayObstaculoIzquierda())
                      {                          
                          m_log.info("Giramos 180 Grados");                          
                          m_gestorMotoresDc.girarRuedas(40, -40);
                          //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.GIRO ,40 ) );                    
                          Thread.sleep( TIEMPO_EJECUCION * 2 );
                          notifyAll();
                      }
                      else
                      {
                          m_log.info("Giramos a la izquierda");
                          m_gestorMotoresDc.girarRuedas(-40, 40);
                          //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.IZQUIERDA ,40 ) );                    
                          Thread.sleep( TIEMPO_EJECUCION  );
                          notifyAll();
                      }
                  }
                  else
                  {                      
                      m_log.info("Giramos Derecha");
                      //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      m_gestorMotoresDc.girarRuedas(40, -40);
                      Thread.sleep( TIEMPO_EJECUCION  );
                      notifyAll();
                  }
                      
              }            */ 
         }     
         else
         {             
             m_log.info("Segimos Recto");
             //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.ADELANTE ,40 ) );                    
             m_gestorMotoresDc.marchaMotor(true, 40);
             Thread.sleep( TIEMPO_EJECUCION  );
             notifyAll();
             
             if( m_medidorDistancia.getDistanciaMedia() <= DISTANACIA_MINIMA )
             {
                 m_log.info("Obstaculo no detectado paramos y giramos 180 grados");
                 //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.STOP ,40 ) );                    
                 m_gestorMotoresDc.paroMotor();
                 Thread.sleep( TIEMPO_EJECUCION / 5  );                 
                 m_gestorMotoresDc.girarRuedas(40, -40);                 
                 Thread.sleep( TIEMPO_EJECUCION * 2 );
                 notifyAll();
             }
         }
         
     }
     
     private synchronized  void procesamosSensores()
     {         
         m_log.info("Procesamos Estados");
         CObstaculo.setObstaculo();
         
         m_detectorObstaculos.callDetectar(CProperties.HAAR_DERECHA , CObstaculo.eSimbolo.DERECHA );                        
         m_detectorObstaculos.callDetectar(CProperties.HAAR_IZQUIERDA , CObstaculo.eSimbolo.IZQUIERDA );                        
         //m_detectorObstaculos.callDetectar(CProperties.HAAR_ATRAS , CObstaculo.eSimbolo.ATRAS );                        
         m_detectorObstaculos.callDetectar(CProperties.HAAR_STOP , CObstaculo.eSimbolo.PARO );                        
         
         m_log.info(CObstaculo.texto() );
         
         m_medidorDistancia.medir();
         notify();
     }
     
     private class CAutoDetectarObstaculos implements Runnable
     {

        @Override public void run()
        {
            while( m_salir == false)
            {
                procesamosSensores();
            }
                    
        }
         
     }
    
}
