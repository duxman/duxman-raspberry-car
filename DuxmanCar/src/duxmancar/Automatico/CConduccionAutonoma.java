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
import static duxmancar.Raspberry.Hardware.Sensores.Vision.CObstaculo.setObstaculo;
import duxmancar.util.IDatosGenerales;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CConduccionAutonoma extends Thread implements IDatosGenerales
{
    public static int TIEMPO_ENTRE_MEDICIONES = 500;
    public static int TIEMPO_EJECUCION = 1000;
    public static int DISTANCIA_MINIMA = 40;         
     
    private CListaDatosProvider m_listaDatosProvider;
       
    private boolean m_salir = true;
    private boolean m_pausado = false;
    private Logger m_log;
    private CMotorControlPuenteH m_gestorMotoresDc;
    private CDetectarFormas m_detectorObstaculos;
    private CMedidorDistancia m_medidorDistancia;
     
     
    public CConduccionAutonoma () throws Exception
    {        
         m_log = Logger.getRootLogger();
         m_listaDatosProvider = CListaDatosProvider.getInstance();                 
         m_gestorMotoresDc =    CMotorControlPuenteH.getInstance();         
         m_medidorDistancia =  CMedidorDistancia.getInstance();            
         m_detectorObstaculos = new CDetectarFormas();
    }
     
    public synchronized void setSalir( boolean  salir )
    {
        m_salir = salir;
    }
    
    public synchronized void setPause( boolean  Pause )
    {
        m_pausado = Pause;
    }
    
    private synchronized  void procesamosSensores()
        {         
            m_log.info("Procesamos Estados");
            CObstaculo.setObstaculo();
         
            m_detectorObstaculos.callDetectar( false );                                 
            m_medidorDistancia.medir();         
         
            m_log.info(CObstaculo.texto() + " distancia " + m_medidorDistancia.getDistanciaMedia() );                                   
        }            
     
     public void run()
     {
         boolean bPrimerCicloPause = true;
                
         while( m_salir == false )
         {                                            
             if( m_pausado== false )
             {    
                bPrimerCicloPause = true;
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
            else
            {
                 try
                 {
                     if( bPrimerCicloPause == true)
                     {
                        bPrimerCicloPause = false; 
                        try
                        {
                            m_log.info("Pausamos ejecución bucle");
                            //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.STOP ,40 ) );
                            m_gestorMotoresDc.paroMotor();                             
                        }
                        catch (Exception ex)
                        {
                            m_log.error(ex.getMessage());
                        }
                         
                    }
                    Thread.sleep( TIEMPO_EJECUCION );
                 }
                 catch (Exception ex)
                 {
                    m_log.error(ex.getMessage());
                 }
            }
         }                
     }
     
     public synchronized void comprobar() throws Exception
     {                          
         
         //procesamosSensores();
         m_log.info("Decidimos Accion ...");                          
         if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.NONE  )             
         {
              if( m_medidorDistancia.getDistanciaMedia() <= DISTANCIA_MINIMA )
              {
                 m_listaDatosProvider.addMensajeSalida(CDato.CodificaMensajeInfo(CObstaculo.hayObstaculoCentro(), m_medidorDistancia.getDistanciaMedia()));
                 
                 if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.DERECHA )
                 {
                      m_log.info("Giramos Derecha");                      
                      //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      m_gestorMotoresDc.girarRuedas(-40, 40);
                      Thread.sleep( TIEMPO_EJECUCION  );
                      setObstaculo();                      
                 }
                 else if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.IZQUIERDA )
                 {
                      m_log.info("Giramos Izquierda");
                      //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      m_gestorMotoresDc.girarRuedas(40, -40);
                      Thread.sleep( TIEMPO_EJECUCION  );
                      setObstaculo();                      
                 }
                 else if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.PARO )
                 {
                      m_log.info("paramos motores");
                      //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      m_gestorMotoresDc.paroMotor();  
                      setObstaculo();                       
                 }
                 else if( CObstaculo.hayObstaculoCentro() != CObstaculo.eSimbolo.ATRAS )
                 {
                      m_log.info("marcaha atras");
                      //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.DERECHA ,40 ) );                    
                      m_gestorMotoresDc.marchaMotor(false, 25);
                      Thread.sleep( TIEMPO_EJECUCION/4  );
                      m_gestorMotoresDc.girarRuedas(-40, 40);
                      Thread.sleep( TIEMPO_EJECUCION*2  );
                      m_gestorMotoresDc.marchaMotor(true, 40);
                      Thread.sleep( TIEMPO_EJECUCION );
                      setObstaculo();                      
                 }
              }
         }     
         else
         {             
             
             
             if( m_medidorDistancia.getDistanciaMedia() <= DISTANCIA_MINIMA/2 )
             {
                 m_log.info("Obstaculo detectado distancia de seguridad paramos y giramos 180 grados");
                 //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.STOP ,40 ) );                    
                 m_gestorMotoresDc.paroMotor();
                 Thread.sleep( TIEMPO_EJECUCION / 5  );                                 
                 setObstaculo();                 
             }
             else
             {
                 m_log.info("Segimos Recto");
                //m_listaDatosProvider.addMensajeEntrada( CTelegramasAutomatico.dameDato( eMovimientosAutonomos.ADELANTE ,40 ) );                    
                m_gestorMotoresDc.marchaMotor(true, 40);
                Thread.sleep( TIEMPO_EJECUCION  );                
             }
         }
                  
     }          
}
