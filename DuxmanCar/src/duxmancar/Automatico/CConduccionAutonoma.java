/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Automatico;

import duxmancar.Datos.CListaDatosProvider;
import duxmancar.Datos.CTelegramasAutomatico;
import static duxmancar.Raspberry.Hardware.GPIO.CGpioPines.*;
import duxmancar.Raspberry.Hardware.Sensores.Distancia.CMedidorDistancia;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CCirculos;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CDetectarCirculos;
import duxmancar.util.IDatosGenerales;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CConduccionAutonoma extends Thread implements IDatosGenerales
{
    public static int TIEMPO_ENTRE_MEDICIONES = 100;
    public static int TIEMPO_EJECUCION = 1000;
    public static int DISTANACIA_MINIMA = 30;         
     
    private CListaDatosProvider m_listaDatosProvider;
    private CDetectarCirculos m_detectorObstaculos;
    private CMedidorDistancia m_medidorDistancia;
     
    private boolean m_salir = true;
    private Logger m_log;
     
    public CConduccionAutonoma () throws Exception
    {
         m_log = Logger.getRootLogger();
         m_listaDatosProvider = CListaDatosProvider.getInstance();
         m_detectorObstaculos = new CDetectarCirculos();
         m_medidorDistancia = new CMedidorDistancia(GPIO_PIN_TRIGGER, GPIO_PIN_HECHO);
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
                 m_log.info("Procesamos Estados");
                 
                 m_detectorObstaculos.callDetectar();
                 m_log.info( CCirculos.texto() );
                 m_medidorDistancia.medir();
                 
                 while( m_detectorObstaculos.isAlive() || m_medidorDistancia.isAlive() );                 
                 m_log.info("Decidimos Accion ...");                 
                 comprobar();    
                 wait();                 
                 Thread.sleep( TIEMPO_EJECUCION  );
                 m_log.info("Paramos y esperamos nueva orden");                                  
                 m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.STOP ,40 ) );                                     
             }
             catch ( Exception ex)
             {
                 m_log.error(ex.getMessage());
             }
         }
         
         try
         {
             m_log.info("Obstaculo no detectado paramos y giramos 180 grados");
             m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.STOP ,40 ) );                    
             Thread.sleep( TIEMPO_EJECUCION / 5  );
         }
         catch (Exception ex)
         {
            m_log.error(ex.getMessage());
         }
     }
     
     public void comprobar() throws Exception
     {
         if( CCirculos.hayCirculoCentro() )
         {
              if( m_medidorDistancia.getDistanciaMedia() <= DISTANACIA_MINIMA )
              {
                  if( CCirculos.hayCirculoDerecha())
                  {
                      if( CCirculos.hayCirculoIzquierda())
                      {                          
                          m_log.info("Giramos 180 Grados");                          
                          m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.GIRO ,40 ) );                    
                          Thread.sleep( TIEMPO_EJECUCION * 2 );
                          notifyAll();
                      }
                      else
                      {
                          m_log.info("Giramos a la izquierda");
                          m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.IZQUIERDA ,40 ) );                    
                          Thread.sleep( TIEMPO_EJECUCION  );
                          notifyAll();
                      }
                  }
                  else
                  {                      
                      m_log.info("Giramos Derecha");
                      m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      Thread.sleep( TIEMPO_EJECUCION  );
                      notifyAll();
                  }
                      
              }             
         }     
         else
         {             
             m_log.info("Segimos Recto");
             m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.ADELANTE ,40 ) );                    
             Thread.sleep( TIEMPO_EJECUCION  );
             notifyAll();
             
             if( m_medidorDistancia.getDistanciaMedia() <= DISTANACIA_MINIMA )
             {
                 m_log.info("Obstaculo no detectado paramos y giramos 180 grados");
                 m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.STOP ,40 ) );                    
                 Thread.sleep( TIEMPO_EJECUCION / 5  );                 
                 
                 m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.GIRO ,40 ) );                    
                 Thread.sleep( TIEMPO_EJECUCION * 2 );
                 notifyAll();
             }
         }
         
     }
    
}
