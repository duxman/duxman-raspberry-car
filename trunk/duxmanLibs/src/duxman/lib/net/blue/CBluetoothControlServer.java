/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxman.lib.net.blue;

import duxman.lib.net.CSockectControlServer;
import java.io.*;
import static java.lang.Math.random;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.UUID;
import javax.bluetooth.*;
import javax.microedition.io.*;
import duxman.lib.util.*;
/**
 *
 * @author duxman
 */
public abstract class CBluetoothControlServer extends CSockectControlServer
{
    LocalDevice m_DispositivoLocal = null;
    
    //public UUID uuid = null;
     
    
    private String                   m_sServerName      = ""; 
    private UUID                     m_ServerUUID       = null;  
    private String                   m_sServerURL       = "";
    private LocalDevice              m_localDevice      = null;
    private StreamConnectionNotifier m_localServer      = null;
    private StreamConnection         m_conexionCliente  = null;
    private String                   m_sComandoRecivido = "";
    private BufferedReader           m_bfReader         = null;
    
    public CBluetoothControlServer(String sServerName)
    {
        super( sServerName);
        try
        {
            
            Random rand = new Random();
            int randomNum = rand.nextInt((9999 -1) + 1) + 1;
            
            m_sServerName = sServerName;
            System.out.println("inicializamos datos");         
            
            m_ServerUUID = new UUID( String.valueOf(randomNum) ,true);
            m_sServerURL  =  "btspp://localhost:" + m_ServerUUID+ ";name=" + m_sServerName ;                        
        }
        catch(Exception e)
        {
          e.printStackTrace();                                
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
            Logger.getLogger(CBluetoothControlServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
               
    private void creaConexion()
    {
        try
        {                       
            System.out.println("Nos ponemos en modo activo");
            m_DispositivoLocal = LocalDevice.getLocalDevice();
            activaVisivilidad();
                        
            System.out.println("Iniciamos server");
            ///TODO Moverlo a otro lado un hilo aparte
            m_localServer = (StreamConnectionNotifier)Connector.open(m_sServerURL);
            m_conexionCliente = m_localServer.acceptAndOpen();
            
            System.out.println("Cliente conectado");                            
            
            creaBufferStream( m_conexionCliente.openInputStream(), m_conexionCliente.openOutputStream() );            
            
            Run();                                   
        }
        catch(Exception e)
        {
          e.printStackTrace();                                
        }        
    }     
}
