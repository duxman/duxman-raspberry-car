package duxmancar.Datos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import duxmancar.util.IDatosGenerales;

public class CDato extends CDatoProvider implements IDatosGenerales
{

    public List<String> Parametros;
    public eEstadoDato Estado;

    /**
     * *
     * Constructor de la clase creamos la colección sincronizada
     */
    public CDato(String sDato)
    {
        super(sDato);       
    }

    /**
     * *
     * Codifica el mensaje desde los datos instroducidos
     *
     * @return
     */
    public String CodificaMensaje()
    {
        Parametros = Collections.synchronizedList(new ArrayList<String>());
        StringBuffer sRtn = new StringBuffer();
        sRtn.append(String.format("%04d", getId()));
        sRtn.append(String.format("%04d", getOrigen()));
        sRtn.append(String.format("%04d", getDestino()));
        sRtn.append(String.format("%04d", getAccion()));
        sRtn.append(CodificarParametros());
        return sRtn.toString();
    }

    /**
     * *
     * Descodifica el mesaje en los datos individuales
     */
    @Override public void DescodificaMensaje()
    {
        String datostemp= getDato().substring(INI_DATOS.length(),getDato().length()-FIN_DATOS.length());
        Parametros = Collections.synchronizedList(new ArrayList<String>());
        
        m_log.info("Dato a procesar ["+ datostemp + "]");
        setId(Integer.parseInt(datostemp.substring(POS_ID * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ID + 1))));
        setOrigen(Integer.parseInt(datostemp.substring(POS_ORIGEN * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ORIGEN + 1))));
        setDestino(Integer.parseInt(datostemp.substring(POS_DESTINO * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_DESTINO + 1))));
        setAccion(Integer.parseInt(datostemp.substring(POS_ACCION * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ACCION + 1))));
        setParametrosUnidos(datostemp.substring(12, datostemp.length()));
        DescodificaParametros();
        m_log.info(toString());
        
    }

    @Override public void DescodificaMensaje(String sDato)
    {
        setDato(sDato);
        DescodificaMensaje();        
    }

    /**
     * *
     * Divide los parametro segun el divisor indicado
     */
    private void DescodificaParametros()
    {
        synchronized (Parametros)
        {
            if(getParametrosUnidos().contains(DIVISOR_PARAMETROS) )
            {
                String[] aParametros = getParametrosUnidos().split(DIVISOR_PARAMETROS);
                for (String par : aParametros)
                {
                    Parametros.add(par);
                }
            }
            else
            {
                Parametros.add(getParametrosUnidos());
            }
        }
    }

    /**
     * *
     * Codifca los parametros los une en una unica cadena con un divisor
     * definido
     *
     * @return retorna la cadena codificada
     */
    private String CodificarParametros()
    {
        StringBuffer sb = new StringBuffer();
        String sRtn = "";
        try
        {
            for (String par : Parametros)
            {
                sb.append(par).append(DIVISOR_PARAMETROS);
            }
        }
        catch (Exception e)
        {

        }
        finally
        {
            sRtn = sb.toString();
        }

        return sRtn;
    }

    @Override public String toString()
    {
        StringBuffer bf = new StringBuffer(getClass().getSimpleName()).append(" : ");
        bf.append("ID (").append(getId()).append(") ");
        bf.append("ORIGEN (").append(getOrigen()).append(") ");
        bf.append("DESTINO (").append(getDestino()).append(") ");
        bf.append("ACCION (").append(getAccion()).append(") ");
        return bf.toString();
    }

}
