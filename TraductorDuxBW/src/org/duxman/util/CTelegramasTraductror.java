package org.duxman.util;

import java.util.ArrayList;
import java.util.List;









import org.duxman.Gui.CBLueClient;
import org.duxman.net.CTelegrama;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
//import android.telephony.gsm.SmsManager;
import android.telephony.*;
import android.util.Log;


public class CTelegramasTraductror extends CTelegrama
{		
	
	public boolean procesaTelegrama(String Telegrama, List<String> lParametrosMensaje )
	{
						
		if( Telegrama.equals(TELEGRAMAS[ID_QUIT] ) )
		{
			//Procesar cerra todo y salir
		}
		else if( Telegrama.equals( TELEGRAMAS[ID_SMS] ) )
		{
			String telefono = lParametrosMensaje.get(0).toUpperCase();
			String mensaje = lParametrosMensaje.get(1).toUpperCase();
			mensaje =  mensaje.replace("_", " ");
			sendSMS(telefono, mensaje);			
		}
		return true;					
	}			
		
	
	private void sendSMS (String telefono, String mensaje)
	{		
		try
		{
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(telefono, null, mensaje, null, null);
			//openWhatsApp ("34"+telefono+"@s.whatsapp.net",mensaje);
		}
		catch(Exception e)
		{
			   Log.e("CTELEGRAMAS", "no se envio el sms", e);
		}
	}
	
	private void openWhatsApp(String id, String mensaje)
	{

		Cursor c = CBLueClient.appContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI,  new String[] { ContactsContract.Contacts.Data._ID }, ContactsContract.Data.DATA1 + "=?", new String[] { id }, null);
		c.moveToFirst();
		
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("content://com.android.contacts/data/" + c.getString(0)));
		i.putExtra(Intent.EXTRA_TEXT, mensaje);

		CBLueClient.appActivity.startActivity(i);
		
		c.close();
	}

}
