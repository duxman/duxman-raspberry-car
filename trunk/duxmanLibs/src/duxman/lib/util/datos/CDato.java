package duxman.lib.util.datos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import duxman.lib.log.CLog;
import duxman.lib.util.CDatosComunes;

public class CDato extends CDatoProvider implements CDatosComunes
{	
	public List<String> Parametros;
	public eEstadoDato Estado;
	/***
	 * Constructor de la clase creamos la colección sincronizada
	 */
	public CDato(CLog log)
	{
            super(log);
            Parametros = Collections.synchronizedList( new ArrayList<String>(  ) );
	}	
	/***
	 * Codifica el mensaje desde los datos instroducidos
	 * @return
	 */
	public String CodificaMensaje()
	{
		StringBuffer sRtn = new StringBuffer();
		sRtn.append(String.format("%04d", getId()));
		sRtn.append(String.format("%04d", getOrigen()));
		sRtn.append(String.format("%04d", getDestino()));
		sRtn.append(String.format("%04d", getAccion()));
		sRtn.append(CodificarParametros());
		return sRtn.toString();
	}
	
	/***
	 * Descodifica el mesaje en los datos individuales
	 */
	
	public void DescodificaMensaje()
	{
		setId(      Integer.parseInt( getDato().substring(POS_ID * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * ( POS_ID + 1) ) ) );
		setOrigen(  Integer.parseInt( getDato().substring(POS_ORIGEN * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * ( POS_ORIGEN + 1) ) ) );
		setDestino( Integer.parseInt( getDato().substring(POS_DESTINO * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * ( POS_DESTINO + 1))  ) );
		setAccion(  Integer.parseInt( getDato().substring(POS_ACCION * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * ( POS_ACCION + 1) ) ));
		setParametrosUnidos (getDato().substring(12, getDato().length()) ); 
		DescodificaParametros();
	}
	/***
	 * Divide los parametro segun el divisor indicado
	 */
	private void DescodificaParametros()
	{
		synchronized (Parametros) 
		{
			String[] aParametros = getParametrosUnidos().split(DIVISOR_PARAMETROS);
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
			
		}
		finally
		{
			sRtn = sb.toString();
		}
		
		return sRtn;
	}

    @Override
    public void ProcesaDato()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
