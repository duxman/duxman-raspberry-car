/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxman.lib.net.blue;

import duxman.lib.log.CLog;
import duxman.lib.net.CSockectControlServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
/**
 *
 * @author duxman
 */
public abstract class CBluetoothControlServer extends CSockectControlServer
{
    
    
    //public UUID uuid = null;
            
    private UUID                     m_ServerUUID       = null;  
    private String                   m_sServerURL       = "";    
    private StreamConnectionNotifier m_localServer      = null;
    private StreamConnection         m_conexionCliente  = null;
    private LocalDevice              m_DispositivoLocal = null;   
    private Boolean                  m_conectado        = false;
    
    public CBluetoothControlServer(String sServerName, CLog log)
    {
        super( sServerName, log);
        try
        {
            
            Random rand = new Random();
            int randomNum = rand.nextInt((9999 -1) + 1) + 1;
            
            m_sServerName = sServerName;
            m_log.write("inicializamos datos BlueTooth");         
            
            m_ServerUUID = new UUID( String.valueOf(randomNum) ,true);
            m_sServerURL  =  "btspp://localhost:" + m_ServerUUID+ ";name=" + m_sServerName ;                        
                 
        }
        catch(Exception e)
        {
          m_log.excepcion(e);                                
        }
    }
       
    public void activaVisivilidad()
    {
        try
        {
            m_DispositivoLocal.setDiscoverable(DiscoveryAgent.GIAC);
        }
        catch (BluetoothStateException ex)
        { 
            m_log.excepcion(ex);                                
        }
    }
               
    protected void creaConexion()
    {
        try
        {                       
            m_log.write("Nos ponemos en modo activo");
            m_DispositivoLocal = LocalDevice.getLocalDevice();
                                    
            m_log.write("Iniciamos server");
            ///TODO Moverlo a otro lado un hilo aparte
            
            CBtListener btListener = new CBtListener(m_sServerName);
            btListener.start();
                    
        }
        catch(Exception e)
        {
          m_log.excepcion(e);                                                               
        }        
    } 
    
    
    class CBtListener extends Thread
    {
        public CBtListener(String sServerName)
        {
            super(sServerName + "_Listener");
        }
        
        @Override  public void run()
        {
            try
            {
                
                    while(m_conexionCliente == null)
                    {
                        m_localServer = (StreamConnectionNotifier)Connector.open(m_sServerURL);
                        m_conexionCliente = m_localServer.acceptAndOpen();
                        activaVisivilidad();
                    }    
                    
                    m_log.write("Cliente conectado");                                        
                    creaBufferStream( m_conexionCliente.openInputStream(), m_conexionCliente.openOutputStream() );                       
                    Run();                                          
            }
            catch (Exception ex)
            {
               m_log.excepcion(ex);
            }
        }
        
    }
}
