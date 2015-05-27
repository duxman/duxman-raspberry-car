package org.duxman.util.list;

import org.duxman.TraductorDuxBW.R;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CArrayAdapterBTDevices extends BaseAdapter 
{
	protected Activity activity;
	protected ArrayList<CBTDeviceItem> items;
	  
	public CArrayAdapterBTDevices(Activity activity, ArrayList<CBTDeviceItem> items) 
	{
	    this.activity = activity;
	    this.items = items;
	}
	
	public boolean add( CBTDeviceItem  object)
	{		
		return items.add(object);	
	}

    @Override   public boolean hasStableIds() 
    {
      return true;
    }
	
    @Override public int getCount() 
	{

		return items.size();
	}
    
    @Override public Object getItem(int position) 
    {
      return items.get(position);
    }
   
    @Override public long getItemId(int position) 
    {
      return items.indexOf( items.get(position) );
    }
    
	@Override
	public View getView(int position, View contentView, ViewGroup parent) 
	{
		View vi=contentView;
        
	    if(contentView == null) 
	    {
	      LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      vi = inflater.inflate(R.layout.bt_device_item, null);
	    }
	             
	    CBTDeviceItem item = items.get(position);
	    
	    TextView TextName = ( TextView ) vi.findViewById(R.id.TextBTDeviceName);
	    TextView TextDir = ( TextView ) vi.findViewById(R.id.TextBtDeviceDir);
	    
	    TextName.setText(item.BtDeviceName);
	    TextDir.setText(item.BtDeviceDir);
	    	    
	 
	    return vi;
	}
}