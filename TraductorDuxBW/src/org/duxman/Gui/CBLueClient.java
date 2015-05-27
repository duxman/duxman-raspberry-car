/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.duxman.Gui;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.lang.Math;

import org.duxman.net.CBlueService;
import org.duxman.net.CClientNetSocket;
import org.duxman.net.CServidorNetSockect;
import org.duxman.util.CConstantes;
import org.duxman.util.CTelegramasTraductror;
import org.duxman.util.list.CConexionItem;
import org.duxman.TraductorDuxBW.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class CBLueClient extends Activity implements OnClickListener, CConstantes , OnTouchListener, SurfaceHolder.Callback
{
    // Debugging
    private static final String TAG = "CBLueClient";
    private static final boolean D = true;
    public static String QUIENSOY = "";
    public static String PHONE = "";
    

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    public static Context appContext;
    public static Context appActivity;

    // Layout Views
    private TextView mTitle;
    private ListView mConversationView;   
    private Button mSendButton;
    private RelativeLayout m_LayBT;
    private RelativeLayout m_LayNET;
    private RelativeLayout m_LaySERVER;
    
    private CBlueService m_BlueService = null;
    private CServidorNetSockect m_NetService = null;
    private CClientNetSocket m_NetClient =null;
    
    private String m_NombreDispositivoConectado = null; 
    private StringBuffer m_BufferSalida;
    private BluetoothAdapter m_BlueAdapter = null;    
    //private ArrayAdapter<String> m_ListaMsg;
    private boolean m_conectado;       
    ArrayList<CConexionItem> m_ConexionesList;
    

    CConexionItem itemBT;
    CConexionItem itemNet;
    CConexionItem itemNetServ;
    
    private Button mButtonAdelante;
    private Button mButtonAtras;
    private Button mButtonDerecha;
    private Button mButtonIzquierda;
    private Button mButtonStop;
    private int m_iContTelegrama;
    
	private SurfaceView svTouchArea;
	private SurfaceHolder svTouchAreaHolder;
	private Canvas canvas;
	private Bitmap joystick;
	private int touchX, touchY, wheelLeft, wheelRight;
	private TextView m_textLocation;
	private boolean m_bConduccionAutomatica;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        m_iContTelegrama = 0;
        m_bConduccionAutomatica = false;
        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
               
        // Set up the custom title        
        // Get local Bluetooth adapter
        m_BlueAdapter = BluetoothAdapter.getDefaultAdapter();
        
        appContext = getApplicationContext();
        appActivity = this;
        m_conectado = false;
    	PreparaComunicaciones();

        // If the adapter is null, then Bluetooth is not supported
        if (m_BlueAdapter == null) 
        {
            Toast.makeText(this, "Bluetooth no esta disponible", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onStart() 
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        
        if (!m_BlueAdapter.isEnabled()) 
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } 
        else 
        {
        	// Inicializamos el servicio blueTooth
        	if (m_BlueService == null)
        		m_BlueService = new CBlueService(this, m_HandlerBT);        
            //inicializamos la net
        	if (m_NetService == null)
        		m_NetService  = new CServidorNetSockect(m_HandlerServer);             
        }
    }
    @Override  public synchronized void onDestroy ()
    {
    	super.onDestroy();
    	if( m_NetClient != null )
    	{
    		m_NetClient.destroy();
    	}
    	if( m_NetService != null )
    	{
    		m_NetService.Stop();    	
    	}
    	if(m_BlueService != null)
    	{
    		m_BlueService.stop();
    	}
    	this.finish();    	
    }
    
    @Override
    public synchronized void onResume() 
    {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (m_BlueService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (m_BlueService.getState() == CBlueService.STATE_NONE) {
              // Start the Bluetooth chat services
              m_BlueService.start();
            }
        }
    }

    private void PreparaComunicaciones() 
    {
        Log.d(TAG, "PreparaComunicaciones()");

        // Inicializamos los mensajes
        //m_ListaMsg = new ArrayAdapter<String>(this, R.layout.message);
        
        //mConversationView = (ListView) findViewById(R.id.in);
        //mConversationView.setAdapter(m_ListaMsg);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(this );
        mSendButton.setBackgroundColor(Color.GRAY);
        
        m_LayBT		 = (RelativeLayout) findViewById(R.id.lay_conexion_item_bt);
        m_LayBT.setOnClickListener(this);
        
        m_LayNET 	 = (RelativeLayout) findViewById(R.id.lay_conexion_item_net);
        m_LayNET.setOnClickListener(this);
        
        m_LaySERVER  = (RelativeLayout) findViewById(R.id.lay_conexion_item_server);
        m_LaySERVER.setOnClickListener(this);
        
        
        mTitle = (TextView) findViewById(R.id.title_left_text);            
   
        mTitle.setText(R.string.app_name);
                       
        itemNet = new CConexionItem((TextView ) findViewById(R.id.ConexionDir_net),        							
        							(ImageView) findViewById(R.id.ConexionIcon_net));
        
        itemNet.ConexionDir.setText(CServidorNetSockect.getIPAddress(true));
        
        itemNetServ = new CConexionItem((TextView ) findViewById(R.id.ConexionDir_Server),        							
										(ImageView) findViewById(R.id.ConexionIcon_Server));
        
        itemNetServ.ConexionDir.setText(CServidorNetSockect.getIPAddress(true));
          
        itemBT = new CConexionItem(	(TextView ) findViewById(R.id.ConexionDir_bt),				
									(ImageView) findViewById(R.id.ConexionIcon_bt));                       
        itemBT.ConexionDir.setText(m_BlueAdapter.getAddress());
      /*
        //Inicializamos los botones de control
        mButtonAdelante = (Button) findViewById(R.id.buttonAdelante);
        mButtonAdelante .setOnClickListener(this );
      //Inicializamos los botones de control
        mButtonAtras = (Button) findViewById(R.id.buttonAtras);
        mButtonAtras .setOnClickListener(this );
      //Inicializamos los botones de control
        mButtonDerecha = (Button) findViewById(R.id.buttonDerecha);
        mButtonDerecha .setOnClickListener(this );
      //Inicializamos los botones de control
        mButtonIzquierda = (Button) findViewById(R.id.buttonIzquierda);
        mButtonIzquierda.setOnClickListener(this );
        //Inicializamos los botones de control
        mButtonStop = (Button) findViewById(R.id.ButtonStop);
        mButtonStop.setOnClickListener(this );
        */
        
        svTouchArea = (SurfaceView) findViewById(R.id.surfaceControl);
		// Needed to make the SurfaceView background transparent
		svTouchArea.setZOrderOnTop(true);
		svTouchAreaHolder = svTouchArea.getHolder();
		svTouchAreaHolder.addCallback(this);
		svTouchAreaHolder.setFormat(PixelFormat.TRANSPARENT);
		svTouchArea.setOnTouchListener(this);
	    m_textLocation = (TextView) findViewById(R.id.textlocation);
		joystick = BitmapFactory.decodeResource(getResources(), R.drawable.joystick);
		
        // Inicializamos el buffer de salida
        m_BufferSalida = new StringBuffer("");
    }

    private void PublicarDispositivo() 
    {
        if(D) Log.d(TAG, "ensure discoverable");
        if (m_BlueAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) 
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

 
    // The Handler that gets information back from the BluetoothChatService	
	private final Handler m_HandlerBT = new Handler() 
    {
        @Override public void handleMessage(Message msg) 
        {
            if(msg.what == MESSAGE_STATE_CHANGE_BLUE )
            {
            	switch (msg.arg1) 
    			{
    					
    				case STATE_CONNECTED:
    					mTitle.setText(R.string.title_connected_to);
    					mTitle.append(m_NombreDispositivoConectado);
    					itemBT.setEstado( getResources().getDrawable( R.drawable.btsi ) );        					
    				//	m_ListaMsg.clear();
    				break;
    				
    				case STATE_CONNECTING:
    					mTitle.setText(R.string.title_connecting);        					
    					itemBT.setEstado( getResources().getDrawable( R.drawable.btno) );
    				break;
    				
    				case STATE_LISTEN:
    				case STATE_NONE:
    					mTitle.setText(R.string.title_not_connected);
    					itemBT.setEstado( getResources().getDrawable( R.drawable.btno) );
    					QUIENSOY = "";
    					m_conectado = false;
    				break;
    			}
            }
            else if(msg.what == MESSAGE_WRITE_BLUE )
            {
            	byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
              //  m_ListaMsg.add("Me:  " + writeMessage);
            }
            else if(msg.what == MESSAGE_READ_BLUE )
            {
            	byte[] writeBuf = (byte[]) msg.obj;
            	String writeMessage = new String(writeBuf);
            	
            	MandarMsg( writeMessage  , msg.arg1 );            	            	          
                // construct a string from the buffer
            	
             //   m_ListaMsg.add("Me:  " + writeMessage);
                
            }
            else if(msg.what == MESSAGE_DEVICE_NAME )
            {
            	 // save the connected device's name
                m_NombreDispositivoConectado = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to " + m_NombreDispositivoConectado, Toast.LENGTH_LONG).show();
            }
            else if(msg.what == MESSAGE_TOAST )
            {
            	 Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_LONG).show();
            }
        }
    };
    
    private final Handler m_HandlerServer = new Handler() 
    {
        @Override public void handleMessage(Message msg) 
        {
            if(msg.what == MESSAGE_STATE_CHANGE_NET )
            {
            	switch (msg.arg1) 
    			{
    				case STATE_CONNECTED:        				        					        			               				
    					itemNetServ.setEstado( getResources().getDrawable( R.drawable.serversi) );    
    					m_conectado = true;
    				break;    				
    				case STATE_CONNECTING:    				    				    				
    				case STATE_LISTEN:
    				case STATE_NONE:
    					itemNetServ.setEstado( getResources().getDrawable( R.drawable.serverno) );    					
    					QUIENSOY = "";
    					m_conectado = false;
    				break;
    			}
            }
            else if(msg.what == MESSAGE_READ_NET )
            {
            	byte[] writeBuf = (byte[]) msg.obj;
            	String writeMessage = new String(writeBuf);
            	
            	MandarMsg( writeMessage  , msg.arg1 );
                // construct a string from the buffer
                
               //m_ListaMsg.add("Me:  " + writeMessage);
            }            
        }
    };
    private final Handler m_HandlerWifi = new Handler() 
    {
        @Override public void handleMessage(Message msg) 
        {
            if(msg.what == MESSAGE_STATE_CHANGE_NET )
            {
            	switch (msg.arg1) 
    			{
    				case STATE_CONNECTED:        				        					        			               				
    					itemNet.setEstado( getResources().getDrawable( R.drawable.wifisi) );    					        				
    				break;
    				
    				case STATE_CONNECTING:    					   			
    				case STATE_LISTEN:
    				case STATE_NONE:
    					itemNet.setEstado( getResources().getDrawable( R.drawable.wifino) );
    					QUIENSOY = "";
    					m_conectado = false;
    				break;
    			}
            }
            else if(msg.what == MESSAGE_READ_NET )
            {
            	byte[] writeBuf = (byte[]) msg.obj;
            	String writeMessage = new String(writeBuf);
            	
            	MandarMsg( writeMessage  , msg.arg1 );
                // construct a string from the buffer
                
             //  m_ListaMsg.add("Me:  " + writeMessage);
            }            
        }
    };                      

    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) 
        {
        	case REQUEST_CONNECT_DEVICE:
        	{
        		// When DeviceListActivity returns with a device to connect
        		if (resultCode == Activity.RESULT_OK) 
        		{
        			// Get the device MAC address
        			String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        			// Get the BLuetoothDevice object
        			BluetoothDevice device = m_BlueAdapter.getRemoteDevice(address);
        			//if( QUIENSOY.equals(ORIGENES_DESTINOS[ID_MOVIL]) || QUIENSOY.length() <= 0 )
        			{
//        			 	Attempt to connect to the device
            			m_BlueService.connect(device);            			
            			QUIENSOY = ORIGENES_DESTINOS[ID_TRADUCTOR];
            			m_conectado = true;
        			}
        			
        			
        		}
        		break;
        	}
        	case REQUEST_CONNECT_DEVICE_NET:
        	{
        		// When DeviceListActivity returns with a device to connect
        		if (resultCode == Activity.RESULT_OK) 
        		{
        			// Get the device MAC address
        			String address = data.getExtras().getString(CConectarNet.EXTRA_DEVICE_ADDRESS);
        			String addressPort = data.getExtras().getString(CConectarNet.EXTRA_DEVICE_ADDRESS_PORT).trim();
        			int iaddressPort = Integer.valueOf(addressPort).intValue();
        			try 
        			{
        				if( QUIENSOY.equals(ORIGENES_DESTINOS[ID_MOVIL]) || QUIENSOY.length() <= 0 )
            			{
//            			 	Attempt to connect to the device
        					m_NetClient = new CClientNetSocket(address, iaddressPort , m_HandlerWifi );
        					m_NetClient.start();
        					itemNet.setEstado( getResources().getDrawable( R.drawable.wifisi) );
        					if( !QUIENSOY.equals(ORIGENES_DESTINOS[ID_TRADUCTOR]) )
        						QUIENSOY = ORIGENES_DESTINOS[ID_MOVIL];        				
                			m_conectado = true;
            			}
        				else
        				{
        					Toast.makeText(getApplicationContext(), "Ya estas Funcionando como Traductor BT.\r\nSal y vuelve a entrar", Toast.LENGTH_LONG).show();
        				}
					} 
        			catch (UnknownHostException e) 
        			{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
        			catch (IOException e) 
        			{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			
        		}
        		break;
        	}
	        case REQUEST_ENABLE_BT:
	        {
	            // When the request to enable Bluetooth returns
	            if (resultCode == Activity.RESULT_OK) 
	            {
	                // Bluetooth is now enabled, so set up a chat session
	                PreparaComunicaciones();
	            } 
	            else 
	            {
	                // User did not enable Bluetooth or an error occured
	                Log.d(TAG, "BT not enabled");
	                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_LONG).show();
	                finish();
	            }
	        }
	        
	        case REQUEST_TELEGRAMA:
        	{        	
        		if (resultCode == Activity.RESULT_OK) 
        		{

        			String telegrama = data.getExtras().getString(CTecladoTelegramas.EXTRA_TELEGRAMA);
        			if( QUIENSOY.equals( ORIGENES_DESTINOS[ ID_MOVIL ] ) )
        			{
        					MandarMsg(telegrama, MESSAGE_READ_NET_TO_WIFI);	
        			}
        			else if( QUIENSOY.equals( ORIGENES_DESTINOS[ ID_TRADUCTOR ] ) )
        			{
        				MandarMsg(telegrama, MESSAGE_READ_NET_TO_BT);	
        			}        			        			        			        
        		}
        		break;
        	}
        }
    }
    
    
	public void MandarMsg(String msg , int destino)
    {
    	if( destino == MESSAGE_READ_NET_TO_BT)
    	{
    		MandaMensageBlue(msg);
    	}
    	if( destino == MESSAGE_READ_NET_TO_WIFI)
    	{
    		MandaMensageNet(msg);
    	}
    	if( destino == MESSAGE_READ_NET_TO_SERV)
    	{
    		MandaMensageNetServer(msg);
    	}            	
    }
    
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    public void MandaMensageBlue(String message) 
    {
        // Estamos conectados
        if (m_BlueService.getState() != STATE_CONNECTED) 
        {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show();
            return;
        }
        // Tenemos algo que mandar
        if (message.length() > 0) 
        {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            m_BlueService.write(send);                       
        }
    }
    
    public void MandaMensageNet(String message) 
    {
       try
       {
    	// Estamos conectados
        if (m_NetClient.getState1() != STATE_CONNECTED) 
        {
            Toast.makeText(this, R.string.not_connected + "WIFI", Toast.LENGTH_LONG).show();
            return;
        }

        // Tenemos algo que mandar
        if (message.length() > 0) 
        {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            m_NetClient.write(send);           
                       
        }
       }
       catch(Exception e)
       {
    	   Log.e(TAG, e.getMessage());
       }
    }
    
    public void MandaMensageNetServer(String message) 
    {
        // Estamos conectados
        if (m_NetService.getState() != STATE_CONNECTED) 
        {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show();
            return;
        }

        // Tenemos algo que mandar
        if (message.length() > 0) 
        {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            m_NetService.write(send);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {    	
    	switch (item.getItemId()) 
        {
	        case R.id.scanBT:
	        {
	            // Launch the DeviceListActivity to see devices and do scan
	        	Intent serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	            return true;
	        }
	        case R.id.scanNet:
	        {
	            // Launch the DeviceListActivity to see devices and do scan
	        	Intent serverIntent = new Intent(this, CConectarNet.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_NET);
	            return true;
	        }   
	        case R.id.discoverable:
	        {
	            // Ensure this device is discoverable by others
	            PublicarDispositivo();
	            return true;
	        }
        }
        return false;
    }

	@Override
	public void onClick(View v) 
	{								
		
		switch(v.getId())
		{
			case R.id.button_send:
			{
				/*if( m_conectado )
				{
				   // Launch the DeviceListActivity to see devices and do scan
					Intent serverIntent = new Intent(this, CTecladoTelegramas.class);
					startActivityForResult(serverIntent, REQUEST_TELEGRAMA);
					break;
				}
				else
				{
		            Toast.makeText(this,"No estas conectado", Toast.LENGTH_LONG).show();
				}*/
				String tele;
				if(m_bConduccionAutomatica == false )
				{
					tele = String.format("#INI#%04d%04d%04d%04d#FIN#\r\n", m_iContTelegrama++,1,6,0);
					mSendButton.setBackgroundColor(Color.BLUE);
					m_bConduccionAutomatica = true;
				}
				else
				{
					tele = String.format("#INI#%04d%04d%04d%04d#FIN#\r\n", m_iContTelegrama++,1,6,1);
					mSendButton.setBackgroundColor(Color.GRAY);
					m_bConduccionAutomatica = false;
				}
				MandaMensageNet(tele);
				break;	
				
			}
												
			case R.id.lay_conexion_item_bt:
			{
				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				break;
			}
        
			case R.id.lay_conexion_item_net:
			{
				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(this, CConectarNet.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_NET);
				break;
			}   
			case R.id.lay_conexion_item_server:
			{
				String sQuienEstaConectado = "No hay nadie conectado al servicio";
				if( m_NetService.getState() == STATE_CONNECTED )
				{
					sQuienEstaConectado = "Esta conectado : ["+  m_NetService.addressremoto+ "]";
				}
				 Toast.makeText(this,sQuienEstaConectado, Toast.LENGTH_LONG).show();
					
				break;
			} 
			case R.id.buttonAdelante:
			{
				String tele = String.format("#INI#%04d%04d%04d%04d%d#FIN#\r\n", m_iContTelegrama++,1,5,2,40);
				MandaMensageNet(tele);
				break;
			}
			case R.id.buttonAtras:
			{
				String tele = String.format("#INI#%04d%04d%04d%04d%d#FIN#\r\n", m_iContTelegrama++,1,5,3,40);
				MandaMensageNet(tele);
				break;
			}
			case R.id.buttonIzquierda:
			{
				String tele = String.format("#INI#%04d%04d%04d%04d%d#FIN#\r\n", m_iContTelegrama++,1,5,1,60);
				MandaMensageNet(tele);
				break;
			}
			case R.id.buttonDerecha:
			{
				String tele = String.format("#INI#%04d%04d%04d%04d%d#FIN#\r\n", m_iContTelegrama++,1,5,0,60);
				MandaMensageNet(tele);
				break;
			}
			case R.id.ButtonStop:
			{
				
				break;
			}
			
			
		}			
								
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		// Draw the joy stick on the surface as soon as ready
		drawJoystick(svTouchArea.getWidth() / 2, svTouchArea.getHeight() / 2);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// TODO Auto-generated method stub
		
	}


	public boolean onTouch(View v, MotionEvent event)
	{
		String tele;
		int wR,wL;
		int x,y;
		tele = String.format("#INI#%04d%04d%04d%04d#FIN#\r\n", m_iContTelegrama++,1,5,4);
		
		switch(event.getAction())
		{
		case MotionEvent.ACTION_UP:
			// Stop robot
			touchX = 0;
			touchY = 0;
			mTitle.setText("[X,Y] : [" + touchX + ","+ touchY + "]");
			//m_textLocation.setText
			
			drawJoystick(v.getWidth() / 2, v.getHeight() / 2);
			
			tele = String.format("#INI#%04d%04d%04d%04d#FIN#\r\n", m_iContTelegrama++,1,5,4);								
			break;
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			touchX = (int) (100 * (2 * (float) event.getX() / v.getWidth() - 1));
			touchY = (int) (100 * (1 - 2 * (float) event.getY() / v.getHeight()));			
			mTitle.setText("[X,Y] : [" + touchX + ","+ touchY + "]");											
			
			drawJoystick(event.getX(), event.getY());								

			//quiero girar
			if(Math.abs(touchY) < 30 )
			{
				//giramos a la derecha 
				if( touchX > 25 ) tele = String.format("#INI#%04d%04d%04d%04d%d#FIN#\r\n", m_iContTelegrama++,1,5,0,Math.abs(touchX));
				//giramos a la izquierda
				else if( touchX < 25 )  tele = String.format("#INI#%04d%04d%04d%04d%d#FIN#\r\n", m_iContTelegrama++,1,5,1,Math.abs(touchX));			
			}
			else
			{
				//solo adelante y atras
				if(Math.abs(touchX) < 50 )
				{
					//adelante
					if( touchY > 25 ) tele = String.format("#INI#%04d%04d%04d%04d%d#FIN#\r\n", m_iContTelegrama++,1,5,2,Math.abs(touchY));
					//atras
					else if( touchY < 25 ) tele = String.format("#INI#%04d%04d%04d%04d%d#FIN#\r\n", m_iContTelegrama++,1,5,3,Math.abs(touchY));					
				}
				else
				{
					//giramos ruedas derecha e izquierda en sentidos contrarios
					tele = String.format("#INI#%04d%04d%04d%04d%d|%d#FIN#\r\n", m_iContTelegrama++,1,5,5,( -1 * touchX ), touchX);					
				}
				
			}												
			break;							
		}		
		MandaMensageNet(tele);		
		return true;
	}
	
	private void drawJoystick(float x, float y)
	{
		canvas = svTouchAreaHolder.lockCanvas();
		canvas.drawColor(0, Mode.CLEAR);
		canvas.drawBitmap(joystick, x - joystick.getWidth() / 2, y - joystick.getHeight() / 2, null);
		svTouchAreaHolder.unlockCanvasAndPost(canvas);
	}


}
	
