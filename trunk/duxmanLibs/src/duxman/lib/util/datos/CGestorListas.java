package duxman.lib.util.datos;

import duxman.lib.util.CSubProceso;
import java.util.ArrayList;
import java.util.Collections;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aduce
 */
public class CGestorListas<T> extends CSubProceso
{  
    private CLista<T> m_lDatosNuevos;
    private CLista<T> m_lDatosProceso;
    public CGestorListas(String sName)
    {
        super(sName);
        m_lDatosNuevos  = new CLista<T> (sName+"_Lista_Nuevos");
        m_lDatosProceso = new CLista<T> (sName+"_Lista_Procesando");
    }
    
    public boolean ProcesoEjecucion() throws Exception
    {
      
      if(m_lDatosNuevos.hayElementos ())
      {
        T elemento = m_lDatosNuevos.damePrimerElemento ();
        m_lDatosProceso.addElemento (elemento);        
      }
      
      if(m_lDatosProceso.hayElementos ())
      {
        
      }
      
      return false;   
    }
  
  
}
