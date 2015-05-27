package org.duxman.net;

import java.util.ArrayList;
import java.util.List;

import org.duxman.Gui.CBLueClient;
import org.duxman.util.CConstantes;

public abstract class CTelegrama implements CConstantes
{
	public final static String FINTELEGRAMA =  "FINTELE\r\n";
	public final static String SEPARADOR =  " ";
	public final static String[] TELEGRAMAS = {"ENCENDER","SALUDAR","ENCENDERALL","APAGARALL","COUNTDOWN","COUNTUP","QUIT","SMS","SET","SOUND","DERECHA","IZQUIERDA","DELANTE","ATRAS"};		
	public final static int ID_ENCENDER 	= 0;
	public final static int ID_SALUDAR 		= 1;
	public final static int ID_ENCENDERALL	= 2;
	public final static int ID_APAGARALL	= 3;
	public final static int ID_COUNTDOWN	= 4;
	public final static int ID_COUNTUP		= 5;
	public final static int ID_QUIT			= 6;
	public final static int ID_SMS			= 7;
	public final static int ID_SET			= 8;
	public final static int ID_SOUND		= 9;
		
	protected List<String> m_lParametrosMensaje  =new ArrayList<String>();
	
	protected String m_Telegrama;
	protected String m_Origen;
	protected String m_Destino;
	
	public CTelegrama()
	{	
	}
	
	protected void finalize()
	{		
	}
	
	public abstract boolean procesaTelegrama(String Tele , List<String> lParametrosMensaje ) throws InterruptedException;
	
	
	private String eliminarCaracteres(String msg)
	{
		String rtn= msg ;
		
		rtn = rtn.replace("  "," ");
		rtn = rtn.replace(".","");
		rtn = rtn.replace("\r","");
		rtn = rtn.replace("\n","");
		rtn = rtn.replace("\n","");
		rtn = rtn.replace("#","");
		rtn = rtn.replace("&","");	
		return rtn;	
		
	}
	


	public int ProcesaMensaje( String msg ) throws InterruptedException
	{
		
		dameParametrosMensaje( msg );	
		
		m_Telegrama =  m_lParametrosMensaje.get(0);
		m_lParametrosMensaje.remove(0);
		
		m_Origen = m_lParametrosMensaje.get(0);
		m_lParametrosMensaje.remove(0);
		
		m_Destino = m_lParametrosMensaje.get(0);
		m_lParametrosMensaje.remove(0);
		
		if( m_Destino.equals( CBLueClient.QUIENSOY ))
		{
			procesaTelegrama(m_Telegrama, m_lParametrosMensaje );
			return 0;
		}
		else
		{
			if( m_Destino.equals( ORIGENES_DESTINOS[ID_RPI]) )
				return MESSAGE_READ_NET_TO_BT;
			if( m_Destino.equals( ORIGENES_DESTINOS[ ID_TRADUCTOR ]) )
				return MESSAGE_READ_NET_TO_SERV;
			if( m_Destino.equals( ORIGENES_DESTINOS[ ID_MOVIL ]) )
				return MESSAGE_READ_NET_TO_WIFI; 
			
		}
		return 0;
				
		
	}
	
	
	public void dameParametrosMensaje( String msg )
	{
		m_lParametrosMensaje.clear();
		String msgTemp = eliminarCaracteres(msg);				
		
		String[] arrayparam = msgTemp.split(" ") ;
		 		 		 
		for(String  s : arrayparam)
		{			   
			 m_lParametrosMensaje.add(s);
		}			 		
	}
	
	
	public String dameTelegrama(String telegrama, String origen,String destino, String[] parametros)
	{
		StringBuffer rtn=new StringBuffer();
		rtn.append(telegrama).append(SEPARADOR);
		rtn.append(origen).append(SEPARADOR);
		rtn.append(destino).append(SEPARADOR);
		
		for(String  s : parametros)
		{	
			rtn.append(s).append(SEPARADOR);
		}
		rtn.append(FINTELEGRAMA);
		
		return rtn.toString();			
	}
	
	public String dameTelegrama(String telegrama, String origen,String destino)
	{
		StringBuffer rtn=new StringBuffer();
		rtn.append(telegrama).append(SEPARADOR);
		rtn.append(origen).append(SEPARADOR);
		rtn.append(destino).append(SEPARADOR);			
		rtn.append(FINTELEGRAMA);
		
		return rtn.toString();			
	}
	

}

