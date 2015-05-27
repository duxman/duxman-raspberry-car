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

import org.duxman.TraductorDuxBW.R;
import org.duxman.net.CTelegrama;
import org.duxman.util.CConstantes;
import org.duxman.util.CTelegramasTraductror;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class CTecladoTelegramas extends Activity implements OnClickListener , CConstantes
{
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_TELEGRAMA = "TELEGRAMA";    
    private Handler m_handler;
    private CTelegramasTraductror m_telegrama;
    
     
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        m_telegrama = new CTelegramasTraductror();

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.teclado_telegrama);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);               

        // Initialize the button to perform device discovery
        Button btnOn = (Button) findViewById(R.id.btnON);
        Button btnOff= (Button) findViewById(R.id.btnOff);
        Button btnSaludo = (Button) findViewById(R.id.btnSaludo);
        Button btnSet= (Button) findViewById(R.id.btnSet);
        Button btnAllOn = (Button) findViewById(R.id.btnAllOn);
        Button btnAllOff= (Button) findViewById(R.id.btnAllOff);
        Button btnSms = (Button) findViewById(R.id.btnSms);
        Button btnSound= (Button) findViewById(R.id.btnSound);
        Button btnQuit= (Button) findViewById(R.id.btnQuit);
        Button btnSend= (Button) findViewById(R.id.btnSend);
        
        btnOn .setOnClickListener(this );
        btnOff.setOnClickListener(this );
        btnSaludo.setOnClickListener(this );
        btnSet.setOnClickListener(this );
        btnAllOn.setOnClickListener(this );
        btnAllOff.setOnClickListener(this );
        btnSms.setOnClickListener(this );
        btnSound.setOnClickListener(this );
        btnQuit.setOnClickListener(this );
        btnSend.setOnClickListener(this );
        
                       
        
    }

    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
    }
   
	@Override public void onClick(View v) 
	{
		TextView t = (TextView) findViewById(R.id.textLabel);
		if( v.getId() == R.id.btnSend)
		{		    
		    String Telegrama = t.getText().toString()+"\r\n";
		    
			// Create the result Intent and include the MAC address
	        Intent intent = new Intent();
	        intent.putExtra(EXTRA_TELEGRAMA, Telegrama);	       
	        //Set result and finish this Activity
	        setResult(Activity.RESULT_OK, intent);
	        finish();
			
		}
		else 			
		{
			
			EditText e = (EditText)findViewById(R.id.editTexto);
			String id = e.getText().toString();
			String telegrama = "";
			t.setText(telegrama);						
			if( v.getId() == R.id.btnON)
			{				
				String[] Params = {id,"ON"};
				telegrama= m_telegrama.dameTelegrama(	CTelegrama.TELEGRAMAS[ CTelegrama.ID_ENCENDER ], 
														CBLueClient.QUIENSOY, 
														ORIGENES_DESTINOS[ID_RPI], 
														Params);
			}
			else if( v.getId() == R.id.btnOff)
			{
				String[] Params = {id,"OFF"};
				telegrama= m_telegrama.dameTelegrama(	CTelegrama.TELEGRAMAS[ CTelegrama.ID_ENCENDER ], 
														CBLueClient.QUIENSOY, 
														ORIGENES_DESTINOS[ID_RPI], 
														Params);
				
			}
			else 
			if( v.getId() == R.id.btnSaludo)
			{
				telegrama= m_telegrama.dameTelegrama(	CTelegrama.TELEGRAMAS[ CTelegrama.ID_SALUDAR ], 
						CBLueClient.QUIENSOY, 
						ORIGENES_DESTINOS[ID_RPI]);
			}
			else
			if( v.getId() == R.id.btnSet)
			{
				String[] Params = {id};
				CBLueClient.PHONE = id;
				telegrama= m_telegrama.dameTelegrama(	CTelegrama.TELEGRAMAS[ CTelegrama.ID_SET ], 
														CBLueClient.QUIENSOY, 
														ORIGENES_DESTINOS[ID_RPI], 
														Params);
			}
			else
			if( v.getId() == R.id.btnAllOn)
			{
				telegrama= m_telegrama.dameTelegrama(	CTelegrama.TELEGRAMAS[ CTelegrama.ID_ENCENDERALL ], 
						CBLueClient.QUIENSOY, 
						ORIGENES_DESTINOS[ID_RPI]);	
			}
			else
			if( v.getId() == R.id.btnAllOff)
			{
				telegrama= m_telegrama.dameTelegrama(	CTelegrama.TELEGRAMAS[ CTelegrama.ID_APAGARALL ], 
						CBLueClient.QUIENSOY, 
						ORIGENES_DESTINOS[ID_RPI]);	
				
			}
			else
			if( v.getId() == R.id.btnSms)
			{
				id = id.replace(" ", "_");
				
				String[] Params = {CBLueClient.PHONE,id};
				telegrama= m_telegrama.dameTelegrama(	CTelegrama.TELEGRAMAS[ CTelegrama.ID_SMS ], 
						CBLueClient.QUIENSOY, 
						ORIGENES_DESTINOS[ID_RPI],
						Params);	
			}
			else
			if( v.getId() == R.id.btnSound)
			{
				
			}
			else
			if( v.getId() == R.id.btnQuit)
			{
				
			}							
			t.setText(telegrama);
		}
		
        		
	}

}

