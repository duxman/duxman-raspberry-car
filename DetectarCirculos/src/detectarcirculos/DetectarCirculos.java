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
            m_Camara.capturarImagen();
            m_imagen = m_Camara.detectarCirculos2("Stop","cascadeStop.xml", m_Camara.m_ultimaImagen);
            m_imagen =  m_Camara.detectarCirculos2("Triangulo","cascadeTriangulo.xml", m_Camara.m_ultimaImagenGris);
            m_imagen =  m_Camara.detectarCirculos2("Cuadrado","cascadeCuadrado.xml", m_Camara.m_ultimaImagen);
            m_imagen =  m_Camara.detectarCirculos2("Circulo","cascadeCirculo.xml", m_Camara.m_ultimaImagen);
            //m_imagen =  m_Camara.detectarCirculos2("GIRO","cascade_giro.xml", m_Camara.m_ultimaImagen);
            if( m_imagen != null )
                m_ventana.setImage(m_imagen,m_ventana.getLabelImagen());
        }
    }
           
   
    
}
