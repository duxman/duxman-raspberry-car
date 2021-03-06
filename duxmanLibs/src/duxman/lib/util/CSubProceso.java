package duxman.lib.util;
import duxman.lib.log.*;
public abstract class CSubProceso extends Thread implements CDatosComunes
{
	
	private eEstados m_eEstados;
	private String 	 m_sName;
	protected CLog m_log;
        
	public CSubProceso(String sName, CLog log)
	{
		m_sName = sName;
                m_log   = log;
		setName( m_sName );
		
	}
	
	public void Run()
	{
		setEstado( eEstados.START );
		this.start();		
	}
	
	public void Stop()
	{
		setEstado( eEstados.STOP );		
	}
	
	public void Pause()
	{
		setEstado( eEstados.PAUSE );		
	}
	
	public String getNombre()
	{
		return m_sName;
	}
	
	public boolean estaVivo()
	{
		return isAlive();
		
	}
	
	@Override public void run() 
	{
		boolean brtn=true;
		try
		{
			if( getEstado() == eEstados.START )
			{
				setEstado( eEstados.STARTING );
				//
				//inicialización
				//
				setEstado( eEstados.RUN );
				
				while( getEstado().equals(eEstados.RUN) )
				{	
					try
					{
						if(getEstado() == eEstados.PAUSE )
						{
							getEstado().wait( TIEMPO_ESPERA_HILO );
						}
						brtn = ProcesoEjecucion();
					}
					catch(Exception e)
					{						
						if( !brtn )
						{
							setEstado( eEstados.STOP );
						}
                                                m_log.excepcion(e);
					}
					
				}
				if( getEstado() == eEstados.STOP )
				{
					setEstado( eEstados.STOPING );
					//
					//PARADA
					//
					setEstado(eEstados.OFF);
				}
			}
		}
		catch(Exception e)
		{	
                    m_log.excepcion(e);
		}				
	}
	
	/**
	 * Metodo abstracto que hay que implementar con el codigo a ejecutar en el thread.
	 * @return si la ejecución es correcta o no
	 * @throws Exception es caso de error pararemos el thread por medio de un trhow
	 */
	public abstract boolean ProcesoEjecucion() throws Exception;

	public eEstados getEstado() 
	{
		synchronized (m_eEstados) 
		{
			return m_eEstados;
		}
	}

	public void setEstado(eEstados eEstado) 
	{
		synchronized (m_eEstados) 
		{
			m_eEstados = eEstado;
		}
		
	}
	

}
