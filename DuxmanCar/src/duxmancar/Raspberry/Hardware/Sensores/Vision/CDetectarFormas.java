/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware.Sensores.Vision;

import duxmancar.Raspberry.Hardware.Sensores.Vision.CObstaculo.eSimbolo;

/**
 *
 * @author duxman
 */
public class CDetectarFormas extends CDetectorObstaculos
{
    

    @Override  public void callDetectar()
    {       
        CObstaculo.setObstaculo();
        m_log.info(" Circulos ");
        m_camara.detectarCirculos();
    }
    
    public void callDetectar( String FicheroHaar, eSimbolo simbol)
    {       
       // CObstaculo.setObstaculo();
       // m_log.info(" Formas " + FicheroHaar);
        //m_camara.detectarForma( FicheroHaar, simbol );
        
        m_camara.capturarImagen();
        
        m_camara.detectarFormas3( false );
    }
    
     public void callDetectar( boolean grabar )
    {       
       // CObstaculo.setObstaculo();
       // m_log.info(" Formas " + FicheroHaar);
        //m_camara.detectarForma( FicheroHaar, simbol );
        
        m_camara.capturarImagen();
        
        m_camara.detectarFormas3( grabar );
    }
    
}
