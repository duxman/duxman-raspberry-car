package duxman.lib.net.datos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import duxman.lib.log.CLog;
import duxman.lib.util.CDatosComunes;

public class CDato implements CDatosComunes
{
	public String Dato;
	public int Origen;
	public int Destino;
	public int Id;
	public int Accion;
	private String ParametrosUnidos;
	public List<String> Parametros;
	public eEstadoDato Estado;
	/***
	 * Constructor de la clase creamos la colección sincronizada
	 */
	public CDato()
	{
		Parametros = Collections.synchronizedList( new ArrayList<String>(  ) );
	}	
	/***
	 * Codifica el mensaje desde los datos instroducidos
	 * @return
	 */
	public String CodificaMensaje()
	{
		StringBuffer sRtn = new StringBuffer();
		sRtn.append(String.format("%04d", Id));
		sRtn.append(String.format("%04d", Origen));
		sRtn.append(String.format("%04d", Destino));
		sRtn.append(String.format("%04d", Accion));
		sRtn.append(CodificarParametros());
		return sRtn.toString();
	}
	
	/***
	 * Descodifica el mesaje en los datos individuales
	 */
	
	public void DescodificaMensaje()
	{
		Id	= Integer.parseInt( Dato.substring(POS_ID * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * ( POS_ID + 1) ) );
		Origen = Integer.parseInt( Dato.substring(POS_ORIGEN * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * ( POS_ORIGEN + 1) ) );
		Destino = Integer.parseInt( Dato.substring(POS_DESTINO * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * ( POS_DESTINO + 1))  );
		Accion = Integer.parseInt( Dato.substring(POS_ACCION * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * ( POS_ACCION + 1) ) );
		ParametrosUnidos = Dato.substring(12, Dato.length()); 
		DescodificaParametros();
	}
	/***
	 * Divide los parametro segun el divisor indicado
	 */
	private void DescodificaParametros()
	{
		synchronized (Parametros) 
		{
			String[] aParametros = ParametrosUnidos.split(DIVISOR_PARAMETROS);
			for (String par : aParametros) 
			{
				Parametros.add( par );
			}
		}
	}
	/***
	 * Codifca los parametros los une en una unica cadena con un divisor definido
	 * @return retorna la cadena codificada
	 */
	private String 	CodificarParametros()
	{
		StringBuffer sb= new StringBuffer();
		String sRtn="";
		try
		{
			for (String par : Parametros) 
			{
				sb.append(par).append(DIVISOR_PARAMETROS);
			}
		}
		catch(Exception e)
		{
			CLog.write(e.getMessage());
		}
		finally
		{
			sRtn = sb.toString();
		}
		
		return sRtn;
	}
}
