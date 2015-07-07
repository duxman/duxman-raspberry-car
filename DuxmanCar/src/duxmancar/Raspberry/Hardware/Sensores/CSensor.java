/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.Sensores;

import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public abstract class CSensor extends Thread
{    
    public static enum eSensor {NINGUNO,DISTANCIA,VISION};
    public static int NUMERO_MEDICIONES = 5;
    
    protected eSensor m_tipoSesor;           
    protected Logger  m_log;
    
    public CSensor()
    {
        m_log = Logger.getRootLogger();
        m_tipoSesor = eSensor.NINGUNO;
    }
    
    @Override  public  void run()
    {
        try
        {
            medir();
        }
        catch (Exception ex)
        {
           m_log.error(ex.getMessage());
        }
    }    
    
    public eSensor getTipoSensor()
    {
        return m_tipoSesor; 
    }
    
    public abstract void medir() throws Exception;
}
