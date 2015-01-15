/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxman.lib.net.implementado;
import duxman.lib.net.blue.CBluetoothControlServer;
import duxman.lib.util.datos.CDato;
import duxman.lib.util.datos.CGestorListas;
import duxman.lib.util.datos.CLista;
/**
 *
 * @author duxman
 */
public class CBtServer extends CBluetoothControlServer
{
    private CGestorListas m_gestorBtServer;
    public CBtServer(String sServerName)
    {
        super(sServerName);
        m_gestorBtServer = new CGestorListas (sServerName + "_Gestor");
    }

    @Override  public boolean compruebaFinal(String sDato)
    {      
      boolean bTieneInicio=false;
      boolean bTieneFin=false;      
      bTieneInicio = buscaIniDato (sDato);
      bTieneFin    = (buscaFinDato (sDato) >= 0)?true:false;
      
      return (bTieneInicio && bTieneFin);      
    }

    @Override  public boolean ProcesaDatos(String sDato)
    {
      boolean rtn=true;
      
      return rtn;
      
    }
 
    
}
