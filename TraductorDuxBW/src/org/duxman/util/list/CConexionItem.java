package org.duxman.util.list;

import org.duxman.TraductorDuxBW.R;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

public class CConexionItem 
{	
    public TextView ConexionDir;    
    public ImageView ConexionTipo;
        
	public CConexionItem(  TextView Dir,  ImageView Tipo )
	{		
		ConexionDir		= Dir;		
		ConexionTipo	= Tipo;	
	}

	public void setEstado(Drawable drawable) 
	{
		ConexionTipo.setImageDrawable(drawable); 		
	}
	
	
	
}
