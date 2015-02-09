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

        Parametros = Collections.synchronizedList(new ArrayList<String>());
    }

    /**
     * *
     * Codifica el mensaje desde los datos instroducidos
     *
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

    /**
     * *
     * Descodifica el mesaje en los datos individuales
     */
    @Override public void DescodificaMensaje()
    {
        setId(Integer.parseInt(getDato().substring(POS_ID * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ID + 1))));
        setOrigen(Integer.parseInt(getDato().substring(POS_ORIGEN * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ORIGEN + 1))));
        setDestino(Integer.parseInt(getDato().substring(POS_DESTINO * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_DESTINO + 1))));
        setAccion(Integer.parseInt(getDato().substring(POS_ACCION * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ACCION + 1))));
        setParametrosUnidos(getDato().substring(12, getDato().length()));
        DescodificaParametros();
    }

    @Override public void DescodificaMensaje(String sDato)
    {
        setDato(sDato);
        setId(Integer.parseInt(sDato.substring(POS_ID * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ID + 1))));
        setOrigen(Integer.parseInt(sDato.substring(POS_ORIGEN * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ORIGEN + 1))));
        setDestino(Integer.parseInt(sDato.substring(POS_DESTINO * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_DESTINO + 1))));
        setAccion(Integer.parseInt(sDato.substring(POS_ACCION * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ACCION + 1))));
        setParametrosUnidos(sDato.substring(12, sDato.length()));
        DescodificaParametros();
    }

    /**
     * *
     * Divide los parametro segun el divisor indicado
     */
    private void DescodificaParametros()
    {
        synchronized (Parametros)
        {
            String[] aParametros = getParametrosUnidos().split(DIVISOR_PARAMETROS);
            for (String par : aParametros)
            {
                Parametros.add(par);
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
