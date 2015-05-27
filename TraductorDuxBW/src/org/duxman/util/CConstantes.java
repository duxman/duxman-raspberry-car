package org.duxman.util;

public interface CConstantes 
{
	public final static String[] ORIGENES_DESTINOS = {"MOVIL","TRADUCTOR","RPI"};
	public final static int ID_MOVIL 		= 0;
	public final static int ID_TRADUCTOR 	= 1;
	public final static int ID_RPI			= 2;
	
	public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE_BLUE = 1;
    public static final int MESSAGE_READ_BLUE = 2;
    public static final int MESSAGE_WRITE_BLUE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    
    public static final int MESSAGE_TOAST = 5;
        
    public static final int MESSAGE_READ_NET = 6; 
    public static final int MESSAGE_WRITE_NET = 7;
    public static final int MESSAGE_CONNECTED_NET = 8;    
    public static final int MESSAGE_STATE_CHANGE_NET = 9;
    public static final int MESSAGE_DISCONNECTED_NET = 10;
    
    public static final int MESSAGE_READ_NET_TO_BT = 1;    
    public static final int MESSAGE_READ_NET_TO_WIFI = 2;
    public static final int MESSAGE_READ_NET_TO_SERV = 3;
    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int REQUEST_CONNECT_DEVICE_NET = 3;
    public static final int REQUEST_TELEGRAMA = 4;
}
