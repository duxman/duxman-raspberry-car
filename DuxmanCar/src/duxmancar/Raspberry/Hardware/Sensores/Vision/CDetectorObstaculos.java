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
public abstract class CDetectorObstaculos extends Thread
{
    protected CCamara m_camara;
    protected Logger  m_log;
    public CDetectorObstaculos()
    {
        m_log = Logger.getRootLogger();
        m_camara = new CCamara();
        m_camara.iniciaCamara(0);
    }
    
    public abstract void callDetectar();
    
    @Override public void run()
    {
        m_log.info("Buscamos obstaculos ... ");
        callDetectar();
    }
    
}
