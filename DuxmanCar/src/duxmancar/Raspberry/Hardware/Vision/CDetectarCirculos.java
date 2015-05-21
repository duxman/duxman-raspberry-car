/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.Vision;

/**
 *
 * @author duxman
 */
public class CDetectarCirculos extends Thread
{
    CCamara m_camara;
    public CDetectarCirculos()
    {
        m_camara = new CCamara();
        m_camara.iniciaCamara(0);
    }
    
    public void callDetectar()
    {
        m_camara.detectarCirculos();    
    }
    
    public void run()
    {
        callDetectar();
    }
    
}
