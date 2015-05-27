package org.duxman.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.duxman.Gui.CBLueClient;
import org.duxman.util.CConstantes;
import org.duxman.util.CTelegramasTraductror;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;


public class CClientNetSocket  extends Thread  implements CConstantes
{
	  
	  private String m_dstAddress;	  
	  private int 	 m_dstPort;
	  private CTelegramasTraductror m_telegramas;
	  
	  public Socket clientSocket;
	  
	  private BufferedReader m_input;
	  private DataOutputStream m_output = null;
	  private Handler m_handler; 
	  private int m_state;
	  private boolean escliente=false;
	  
	  
	  public CClientNetSocket(String addr, int port, Handler h ) throws UnknownHostException, IOException
	  {				 
		  m_dstAddress = addr.trim();
		  m_dstPort = port;
		  m_handler = h;
		  m_telegramas = new CTelegramasTraductror();
		  escliente = true;
	  }		  
	  
	 public CClientNetSocket(Socket clientSocket, Handler h) 
	 {

		this.clientSocket = clientSocket;
		m_handler = h;
		m_telegramas = new CTelegramasTraductror();
		escliente = false;		
	}

	private synchronized void setState(int state) 
	{
		    
		    m_state = state;
		    //Give the new state to the Handler so the UI Activity can update
		    m_handler.obtainMessage(CBLueClient.MESSAGE_STATE_CHANGE_NET, state, -1).sendToTarget();
	}
	
	public synchronized int getState1() 
	{
	    return m_state;
	}
	
	@Override	public void run() 
	{
		try
		{
		  try 
		  {
			  if( escliente )
			  {
				  clientSocket = new Socket( m_dstAddress, m_dstPort );
			  }
			  
			  m_input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			  m_output= new DataOutputStream(clientSocket.getOutputStream());
			  setState( STATE_CONNECTED);
		  } 
		  catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
			  
			while (!Thread.currentThread().isInterrupted() && clientSocket.isConnected()) 
			{
	
				try
				{
					String read = m_input.readLine();							
	                if( read.endsWith("FINTELE") )
	                {                    	
	                	int destino = m_telegramas.ProcesaMensaje( read );				
	    				read += "\r\n";
	    				m_handler.obtainMessage(MESSAGE_READ_NET, destino,-1, read.getBytes() ).sendToTarget();
	    				
	                }   
	                else
	                {
	                	read ="";
	                }
					
					if( !clientSocket.isConnected() )
					{
						setState(STATE_NONE);
						Thread.currentThread().interrupt();
					}
					//aqui remandar el telegrama por BlueTooth
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		finally
		{
			m_handler.obtainMessage(CBLueClient.MESSAGE_DISCONNECTED_NET).sendToTarget();
			setState( STATE_NONE );			
		}		
	}

	public void write(byte[] buffer) 
    {
        try 
        {
        	m_output.write(buffer);

            // Share the sent message back to the UI Activity
            m_handler.obtainMessage(CBLueClient.MESSAGE_WRITE_NET, -1, -1, buffer).sendToTarget();
        } 
        catch (IOException e) 
        {
            Log.e("", "Exception during write", e);
        }
    }

	  
}
