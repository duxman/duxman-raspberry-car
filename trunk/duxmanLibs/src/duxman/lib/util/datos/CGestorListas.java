package duxman.lib.util.datos;

import duxman.lib.util.CSubProceso;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aduce
 */
public abstract class CGestorListas extends CSubProceso
{  

    public CGestorListas(String sName)
    {
        super(sName);
    }
    
    public boolean ProcesoEjecucion() throws Exception
    {
     return false;   
    }
  
  
}
