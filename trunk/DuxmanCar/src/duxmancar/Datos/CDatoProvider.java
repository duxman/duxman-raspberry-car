/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Datos;

import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public abstract class CDatoProvider
{

    private String Dato;
    private int Origen;
    private int Destino;
    private int Id;
    private int Accion;
    private String ParametrosUnidos;
    protected Logger m_log;

    public CDatoProvider(String sDato)
    {
        m_log = Logger.getRootLogger();
        Dato = sDato;
        DescodificaMensaje();
    }

    public abstract void DescodificaMensaje();

    public abstract void DescodificaMensaje(String sDato);

    /**
     * @return the Dato
     */
    public String getDato()
    {
        return Dato;
    }

    /**
     * @param Dato the Dato to set
     */
    public void setDato(String Dato)
    {
        this.Dato = Dato;
    }

    /**
     * @return the Origen
     */
    public int getOrigen()
    {
        return Origen;
    }

    /**
     * @param Origen the Origen to set
     */
    public void setOrigen(int Origen)
    {
        this.Origen = Origen;
    }

    /**
     * @return the Destino
     */
    public int getDestino()
    {
        return Destino;
    }

    /**
     * @param Destino the Destino to set
     */
    public void setDestino(int Destino)
    {
        this.Destino = Destino;
    }

    /**
     * @return the Id
     */
    public int getId()
    {
        return Id;
    }

    /**
     * @param Id the Id to set
     */
    public void setId(int Id)
    {
        this.Id = Id;
    }

    /**
     * @return the Accion
     */
    public int getAccion()
    {
        return Accion;
    }

    /**
     * @param Accion the Accion to set
     */
    public void setAccion(int Accion)
    {
        this.Accion = Accion;
    }

    /**
     * @return the ParametrosUnidos
     */
    public String getParametrosUnidos()
    {
        return ParametrosUnidos;
    }

    /**
     * @param ParametrosUnidos the ParametrosUnidos to set
     */
    public void setParametrosUnidos(String ParametrosUnidos)
    {
        this.ParametrosUnidos = ParametrosUnidos;
    }
}
