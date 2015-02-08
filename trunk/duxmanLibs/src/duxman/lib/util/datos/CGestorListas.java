package duxman.lib.util.datos;

import duxman.lib.util.CSubProceso;
import java.util.ArrayList;
import java.util.Collections;
import duxman.lib.log.CLog;

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
    public CGestorListas(String sName, CLog log)
    {
        super(sName, log);       
        m_lDatosNuevos  = new CLista<T> (log ,sName+"_Lista_Nuevos");
        m_lDatosProceso = new CLista<T> (log, sName+"_Lista_Procesando");
    }
    
    public boolean ProcesoEjecucion() throws Exception
    {
      
      if(m_lDatosNuevos.hayElementos ())
      {
        try
        {
            CDatoProvider elemento = (CDatoProvider) m_lDatosNuevos.damePrimerElemento ();
            m_lDatosProceso.addElemento ((T) elemento);        
            m_lDatosNuevos.eliminaPrimerElemento();
        }
        catch(Exception e)
        {
            
        }
      }
      
      if(m_lDatosProceso.hayElementos ())
      {
        CDatoProvider elemento = (CDatoProvider) m_lDatosNuevos.damePrimerElemento ();
        elemento.ProcesaDato();
        m_lDatosProceso.eliminaPrimerElemento();
      }
      
      return false;   
    }
  
  
}
