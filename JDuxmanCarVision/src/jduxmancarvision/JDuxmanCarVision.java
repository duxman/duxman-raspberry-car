/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision;

import duxmancar.log.CLog;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class JDuxmanCarVision
{

    /**
     * @param args the command line arguments
     */
    static CLog claselog;
    static Logger m_log;
    static JVision vision;
    public static void main(String[] args)
    {
        claselog =   new CLog(false, "DuxmanCarVision.log", 10000, 5);
        m_log = Logger.getRootLogger();        
        vision = new JVision();
        vision.init();        
    }
    
}
