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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class CConectarNet extends Activity implements OnClickListener 
{
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_DEVICE_ADDRESS_PORT = "device_address_Por";
    private Handler m_handler;
    
     
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_net_conexion);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);               

        // Initialize the button to perform device discovery
        Button btnConectar = (Button) findViewById(R.id.btnConectar);
        Button btnCancel= (Button) findViewById(R.id.btnCancel);
        btnConectar.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        
    }

    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
    }
   
	@Override public void onClick(View v) 
	{
		if( v.getId() == R.id.btnConectar)
		{
		 
			EditText textoDirIp = (EditText) findViewById( R.id.textoIp );				
			String address = textoDirIp.getText().toString();                    
			
			EditText textoIpPort = (EditText) findViewById( R.id.textoport );				
			String addressPort = textoIpPort.getText().toString();

	        // Create the result Intent and include the MAC address
	        Intent intent = new Intent();
	        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
	        intent.putExtra(EXTRA_DEVICE_ADDRESS_PORT, addressPort);

	        // Set result and finish this Activity
	        setResult(Activity.RESULT_OK, intent);
	        finish();
			
		}
		else if( v.getId() == R.id.btnCancel)
		{
			setResult(Activity.RESULT_CANCELED);
	        finish();
		}
		
        		
	}

}

