/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxman.lib.util;

/**
 *
 * @author duxman
 */
import duxman.lib.log.CLog;

public class CBasicaDux extends Object
{
    protected CLog m_log;
    
    public CBasicaDux(CLog log)
    {
        m_log = log;        
    }
    public void Inicializalog(CLog log)
    {
        m_log = log;
    }
}
