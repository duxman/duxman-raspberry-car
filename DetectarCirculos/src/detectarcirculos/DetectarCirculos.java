/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package detectarcirculos;

import org.opencv.core.Mat;

/**
 *
 * @author duxman
 */
public class DetectarCirculos extends Thread
{
    
    CCamara m_Camara;
    Mat m_imagen;
    CVentana m_ventana;
    
    public DetectarCirculos( CVentana ventana )
    {    
        m_Camara = new CCamara();
        m_Camara.iniciaCamara(0);
        m_ventana = ventana;
    }
    
    public void run()
    {
        while(true)
        {
            m_imagen =  m_Camara.detectarCirculos();
            m_ventana.setImage(m_imagen,m_ventana.getLabelImagen());
        }
    }
           
   
    
}
