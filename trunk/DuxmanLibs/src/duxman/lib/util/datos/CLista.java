package duxman.lib.net.datos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import duxman.lib.log.CLog;

public class CLista 
{
	private List<CDato> m_ListaDatos;
	private String m_sNombreLista;
	
	
	public CLista(String sNombreLista)
	{
		m_ListaDatos =  Collections.synchronizedList(new ArrayList<CDato>());
		m_sNombreLista = sNombreLista;		
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
			CLog.write( e.getMessage() );
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
			CLog.write( e.getMessage() );
		}
		return Rtn;			
	}
	
}