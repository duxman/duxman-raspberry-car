/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.Sensores.Vision;

import duxmancar.Raspberry.Hardware.Sensores.CSensor;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public abstract class CDetectorObstaculos 
{    
    protected Logger  m_log;
    protected CCamara m_camara = null;
    public CDetectorObstaculos()
    {                
        m_log = Logger.getRootLogger();
        m_camara = CCamara.getInstance();
        m_camara.iniciaCamara( 0 );
    }
    
    public abstract void callDetectar();            
}
