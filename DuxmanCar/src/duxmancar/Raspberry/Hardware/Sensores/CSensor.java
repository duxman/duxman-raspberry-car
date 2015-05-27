/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.Sensores;

import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public abstract class CSensor extends Thread
{    
    public static int NUMERO_MEDICIONES = 10;
    protected Logger  m_log;
    public CSensor()
    {
        m_log = Logger.getRootLogger();
    }
    
    @Override  public  void run()
    {
        medir();
    }    
    
    public abstract void medir();
}
