package duxman.lib.util.datos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import duxman.lib.log.CLog;

public class CLista<T> 
{
	private List<T> m_ListaDatos;
	private String m_sNombreLista;
	
	
	public CLista(String sNombreLista)
	{
		m_ListaDatos =  Collections.synchronizedList(new ArrayList<T>());
		m_sNombreLista = sNombreLista;		
	}
	
  public T damePrimerElemento()
  {
      T rtn=null;
      synchronized(m_ListaDatos)
      {
        if(m_ListaDatos.size () > 0)
        {
          rtn = m_ListaDatos.get(0);          
        }
      }
      return rtn;
  }
  public void eliminaPrimerElemento()
  {      
      synchronized(m_ListaDatos)
      {
        if(m_ListaDatos.size () > 0)
        {
          m_ListaDatos.remove (0);
        }
      }      
  }
  public int addElemento(T elemento)
  {
      int rtn =0;
      synchronized(m_ListaDatos)
      {
        if(m_ListaDatos != null )
        {
          m_ListaDatos.add (elemento);
          rtn = m_ListaDatos.size ();
        }
      }      
      return rtn;
  }
	public int dameNumeroElementos()
	{
		int iRtn=0;
		try
		{
			synchronized (m_ListaDatos)
			{
				iRtn = m_ListaDatos.size();				
			}
		}
		catch(Exception e)
		{
			
		}
		return iRtn;			
	}
	
	public boolean hayElementos()
	{
		boolean Rtn=false;
		try
		{
			synchronized (m_ListaDatos)
			{
				Rtn = (m_ListaDatos.size()>0)?(true):(false);				
			}
		}
		catch(Exception e)
		{
			
		}
		return Rtn;			
	}
	
}