/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.OpenCV;

/**
 *
 * @author duxman
 */
public class CDistanciasPuntos
{
   private int m_iDistanciaNE;
   private int m_iDistanciaNO;
   private int m_iDistanciaSE;
   private int m_iDistanciaSO;   
   private int m_iDistanciaCentro;
   
   public CDistanciasPuntos(int iDistanciaNE, int iDistanciaNO, int iDistanciaSE, int iDistanciaSO, int iDistanciaCentro)
   {
       m_iDistanciaCentro = iDistanciaCentro;
       m_iDistanciaNE = m_iDistanciaNE;
       m_iDistanciaNO = m_iDistanciaNO;
       m_iDistanciaSE = iDistanciaSE;
       m_iDistanciaSO = m_iDistanciaSO;
   }

    /**
     * @return the m_iDistanciaNE
     */
    public int getDistanciaNE()
    {
        return m_iDistanciaNE;
    }

    /**
     * @return the m_iDistanciaNO
     */
    public int getDistanciaNO()
    {
        return m_iDistanciaNO;
    }

    /**
     * @return the m_iDistanciaSE
     */
    public int getDistanciaSE()
    {
        return m_iDistanciaSE;
    }

    /**
     * @return the m_iDistanciaSO
     */
    public int getDistanciaSO()
    {
        return m_iDistanciaSO;
    }

    /**
     * @return the m_iDistanciaCentro
     */
    public int getDistanciaCentro()
    {
        return m_iDistanciaCentro;
    }
   
}
