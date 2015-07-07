package duxmancar.Automatico;

import duxmancar.Automatico.CConduccionAutonoma;
import static duxmancar.Raspberry.Hardware.GPIO.CGpioPines.GPIO_PIN_HECHO;
import static duxmancar.Raspberry.Hardware.GPIO.CGpioPines.GPIO_PIN_TRIGGER;
import duxmancar.Raspberry.Hardware.Sensores.Distancia.CMedidorDistancia;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CDetectarFormas;
import duxmancar.Raspberry.Hardware.Sensores.Vision.CObstaculo;
import org.apache.log4j.Logger;

public class CAutoDetectarObstaculos extends Thread
{
        private boolean m_salir = true;
        private boolean m_pausado = false;
        private Logger m_log;
        private CDetectarFormas m_detectorObstaculos;
        private CMedidorDistancia m_medidorDistancia;
         
        public  CAutoDetectarObstaculos() throws Exception
        {
            m_log = Logger.getRootLogger();
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
         
        @Override public void run()
        {
            while( m_salir == false)
            {
                if( m_pausado == false )
                {
                    procesamosSensores();
                }
            }
                    
        }
         
}