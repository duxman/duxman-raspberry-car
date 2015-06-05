package duxmancar.Datos;

import duxmancar.Raspberry.Hardware.Sensores.Vision.CObstaculo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import duxmancar.util.IDatosGenerales;

public class CDato extends CDatoProvider implements IDatosGenerales
{

    public List<String> m_lParametros;
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
  
    public static String CodificaMensaje(int iId, int iOrigen, int iDestino, int iAccion, int... iParametros)
    {        
        
        StringBuffer sRtn = new StringBuffer();
        sRtn.append(INI_DATOS);
        sRtn.append(String.format("%04d", iId));
        sRtn.append(String.format("%04d", iOrigen));
        sRtn.append(String.format("%04d", iDestino));
        sRtn.append(String.format("%04d", iAccion));
        for(int par : iParametros)
        {
             sRtn.append(par).append(DIVISOR_PARAMETROS);
        }       
        sRtn.append(FIN_DATOS);
        return sRtn.toString();
    }
    
    public static String CodificaMensajeInfo( CObstaculo.eSimbolo simbolo, double distancia )
    {        
        
        StringBuffer sRtn = new StringBuffer();
        sRtn.append(INI_DATOS);
        sRtn.append(simbolo.toString()).append(" a ");
        sRtn.append(distancia).append(" Cm");        
        sRtn.append(FIN_DATOS);
        return sRtn.toString();
    }
    /**
     * *
     * Descodifica el mesaje en los datos individuales
     */
    @Override public void DescodificaMensaje()
    {
        String datostemp= getDato().substring(INI_DATOS.length(),getDato().length()-FIN_DATOS.length());
        m_lParametros = Collections.synchronizedList(new ArrayList<String>());
        
        m_log.info("Dato a procesar ["+ datostemp + "]");
        setId(Integer.parseInt(datostemp.substring(POS_ID * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ID + 1))));
        setOrigen(Integer.parseInt(datostemp.substring(POS_ORIGEN * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ORIGEN + 1))));
        setDestino(Integer.parseInt(datostemp.substring(POS_DESTINO * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_DESTINO + 1))));
        setAccion(Integer.parseInt(datostemp.substring(POS_ACCION * LEN_PARAMETROS_FIJOS, LEN_PARAMETROS_FIJOS * (POS_ACCION + 1))));
        setParametrosUnidos(datostemp.substring(POS_PARAMETROS * LEN_PARAMETROS_FIJOS  , datostemp.length()));
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
        synchronized (m_lParametros)
        {
            if(getParametrosUnidos().contains("|") )
            {
                int iInicio = 0;
                String parametros = getParametrosUnidos();
                m_log.info( "Parametros unidos : " + parametros );
                while(iInicio < parametros.length()  )
                {
                    int iFin =  parametros.indexOf("|",iInicio);           
                    if(iFin == -1 )
                    {
                        iFin =  parametros.length();
                    }
                                        
                    m_log.info("Separador encontrado en " + parametros + " posicion  desde "+ iInicio + " hasta " + iFin );                    
                    
                    String par = parametros.substring(iInicio,iFin);                    
                    
                    m_lParametros.add(par);
                                        
                    m_log.info("Parametro " + m_lParametros.size() + " : " + par );                    
                    
                    iInicio=iFin+1;
                                                                                                  
                }                
            }
            else
            {
                m_lParametros.add(getParametrosUnidos());
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
            for (String par : m_lParametros)
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
