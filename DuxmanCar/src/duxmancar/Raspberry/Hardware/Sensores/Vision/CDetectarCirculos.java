/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.Sensores.Vision;

import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class CDetectarCirculos extends CDetectorObstaculos
{    
    public CDetectarCirculos()
    {
        super();
        m_log = Logger.getRootLogger();
    }
    
    @Override   public void callDetectar()
    {
        m_log.info(" Circulos ");
        m_camara.detectarCirculos();
    }           
}
