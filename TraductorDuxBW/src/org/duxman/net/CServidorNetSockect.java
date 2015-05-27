package org.duxman.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;
import org.duxman.Gui.CBLueClient;
import org.duxman.net.CBlueService.ConnectedThread;
import org.duxman.util.CConstantes;
import org.duxman.util.CTelegramasTraductror;

import android.content.Context;
import android.os.Handler;
import android.util.Log;


public class CServidorNetSockect implements CConstantes
{
	// Constants that indicate the current connection state
    
    
	public static final int SERVERPORT = 6000;
	private ServerSocket m_serverSocket;
	private Thread m_serverThread = null;
	private Handler m_handler;
	private  int 	m_State;
	
	private Socket m_socket = null;
	private CClientNetSocket m_commThread = null;
	private CTelegramasTraductror m_telegramas;
	public String addressremoto;
	
	public CServidorNetSockect(Handler handler)
	{
		m_serverThread = new Thread(new CServerThread());
		m_serverThread.start();
		m_handler = handler;
		m_telegramas = new CTelegramasTraductror();
		setState( STATE_NONE );
	}
		
	
	public void Stop()	
	{
		try 
		{
			m_serverSocket.close();
			if(m_commThread != null )
			{
				m_commThread.destroy();
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}
	


	private synchronized void setState(int state) 
	{
	    
	    m_State = state;
		    // Give the new state to the Handler so the UI Activity can update
	    m_handler.obtainMessage(MESSAGE_STATE_CHANGE_NET, state, -1).sendToTarget();
	}
	
	
	public synchronized int getState() 
	{
	    return m_State;
	}

	public void write(byte[] out) 
    {
        // Create temporary object
		CClientNetSocket r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) 
        {
            if (m_State != STATE_CONNECTED) 
            	return;
            r = m_commThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }
	
	public static String getIPAddress(boolean useIPv4) 
	{
		String rtn="";
        try 
        {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) 
            {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) 
                {
                    if (!addr.isLoopbackAddress()) 
                    {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) 
                        {
                            if (isIPv4) 
                            	rtn += sAddr + "\r\n";
                        } 
                        else 
                        {
                            if (!isIPv4) 
                            {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                rtn += delim<0 ? sAddr : sAddr.substring(0, delim) + "\r\n";                                
                            }
                        }
                    }
                }
            }
        } 
        catch (Exception ex) 
        {
        	
        } // for now eat exceptions
        return rtn;
    }
		
	//------------------------------------------------------
	//		CLASE SERVIDOR SOCKET
	//------------------------------------------------------
	class CServerThread implements Runnable 
	{

		public void run() 
		{			
			try 
			{
				m_serverSocket = new ServerSocket(SERVERPORT);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}			
			while (!Thread.currentThread().isInterrupted()) 
			{
		
				try 
				{															
					m_socket = m_serverSocket.accept();
					
					setState( STATE_CONNECTED );					
					
					addressremoto = m_socket.getInetAddress().toString();//.getLocalAddress().toString();
				
					m_commThread = new CClientNetSocket(m_socket,m_handler);
					
					CBLueClient.QUIENSOY = ORIGENES_DESTINOS[ID_TRADUCTOR];
					
					new Thread( m_commThread ).start();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	//------------------------------------------------------
	//		FIN CLASE SERVIDOR SOCKET
	//------------------------------------------------------
	//------------------------------------------------------
	//		CLASE COMUNICACION SOCKET
	//------------------------------------------------------
	///HEMOS EXTRAIDO ESTA CLASE PARA HACERLA VALIDA TANTO PARA CLIENTE COMO PARA SERVIDOR
	/*class CCommunicationThread implements Runnable 
	{

		private Socket clientSocket;

		private BufferedReader m_input;
		private DataOutputStream m_output = null;
						
		public CCommunicationThread(Socket clientSocket) 
		{

			this.clientSocket = clientSocket;
			try 
			{
				m_input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				m_output= new DataOutputStream(clientSocket.getOutputStream());				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		public void run() 
		{

			while (!Thread.currentThread().isInterrupted()) 
			{

				try 
				{

					String read = m_input.readLine();
					m_telegramas.ProcesaMensaje( read );
					read += "\r\n";
					m_handler.obtainMessage(CBLueClient.MESSAGE_READ_NET, read).sendToTarget();					
					//aqui remandar el telegrama por BlueTooth
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
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


	}*/

	//------------------------------------------------------
	//		FIN CLASE SERVIDOR SOCKET
	//------------------------------------------------------
	
}
