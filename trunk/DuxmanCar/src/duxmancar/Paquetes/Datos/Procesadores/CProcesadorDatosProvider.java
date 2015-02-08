/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Paquetes.Datos.Procesadores;

import duxmancar.Paquetes.Datos.CDato;
import duxmancar.Paquetes.Datos.CProperties;
import static duxmancar.lib.util.IDatosGenerales.MAX_MENSAJES;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public abstract class CProcesadorDatosProvider extends Thread
{
    
    protected Logger m_log;
    protected List<CDato> m_listaDatos;
    public CProcesadorDatosProvider()
    {
        m_log = Logger.getRootLogger();                     
        m_listaDatos = Collections.synchronizedList( new ArrayList<CDato>());
    }
    
    public void addDato(CDato dato) throws Exception
    {
        putDato(dato);
    }
    
    private synchronized void putDato(CDato dato) throws Exception
    {        
        
        while (m_listaDatos.size() == MAX_MENSAJES )
        {
            wait();
        }
        m_log.info("Añadido Dato :"  +dato );
        m_listaDatos.add(dato);
        notify();        
    }
    
    public synchronized CDato getDato() throws Exception
    {  
        notify();
        while(m_listaDatos.size() == 0)
        {
            wait();
        }
        CDato rtn = m_listaDatos.get(0);
        m_listaDatos.remove(0);
        return rtn;                                       
    }
    
}
