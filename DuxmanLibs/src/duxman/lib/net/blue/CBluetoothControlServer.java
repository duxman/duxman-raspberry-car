/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxman.lib.net.blue;

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
public abstract class CBluetoothControlServer extends CSubProceso
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
        super( "Hilo_" + sServerName);
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
    
    public abstract boolean procesarDato(String sDato);
    public abstract boolean comprobarFinDato(String sDato);
    
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
    
    @Override public  boolean ProcesoEjecucion() throws Exception
    {
         String cadenatmp = m_bfReader.readLine();
         m_sComandoRecivido = m_sComandoRecivido.concat(cadenatmp);
         if( comprobarFinDato(m_sComandoRecivido) )
         {                        
            System.out.println("Received " + m_sComandoRecivido);
            procesarDato(m_sComandoRecivido);
            m_sComandoRecivido="";
         }                 
         return true;        
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
            while(true)
            {
                m_conexionCliente = m_localServer.acceptAndOpen();
                System.out.println("Cliente conectado");

                //DataInputStream din   = new DataInputStream(conn.openInputStream());
                m_bfReader    = new BufferedReader(new InputStreamReader( m_conexionCliente.openInputStream() ) );                
            }
        }
        catch(Exception e)
        {
          e.printStackTrace();                                
        }        
    }     
}
