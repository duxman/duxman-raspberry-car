package duxman.lib.log;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aduce
 */
import org.apache.log4j.Logger;
import de.mindpipe.android.logging.log4j.LogConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.RollingFileAppender;
public class CLog
{
    private Logger m_logger=null;
    
    public CLog(boolean esAndroid,String sLocation, int iMaxFile, int iMaxBackup)
    {          
      if(esAndroid)
      {
        final LogConfigurator logConfigurator = new LogConfigurator();
        try
        {
          logConfigurator.setFileName(sLocation);    
          logConfigurator.setRootLevel(Level.ALL);
          logConfigurator.setUseFileAppender ( true );
          logConfigurator.setMaxBackupSize (iMaxFile);
          logConfigurator.setMaxFileSize (iMaxBackup);
          logConfigurator.configure();
        }
        catch (Exception e)
        {
            System.out.println("Failed to add appender !!");
        }  
      }
      else
      {
        //This is the root logger provided by log4j
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.ALL);
        
        //Define log pattern layout
        PatternLayout layout = new PatternLayout("[%d{yyyy-MM-dd HH:mm:ss,SSS}][%-5p][%C.%M]: [ %m%n ]");
 
        //Add console appender to root logger
        rootLogger.addAppender(new ConsoleAppender(layout));
        try
        {          
          RollingFileAppender fileAppender = new RollingFileAppender(layout, sLocation);
          fileAppender.setMaxFileSize (String.valueOf(iMaxFile) );
          fileAppender.setMaxFileSize (String.valueOf(iMaxBackup));         
          rootLogger.addAppender(fileAppender);
        }
        catch (Exception e)
        {
            System.out.println("Failed to add appender !!");
        }        
      }
      m_logger = Logger.getLogger( this.getClass () );
      m_logger.info("--- Inicio log ---");      
      
    }
    
    public Logger setLogger(Class<?> clase,Level level) throws Exception     
    {
      try
      {
        m_logger = Logger.getLogger( clase );
        m_logger.setLevel (level);
        m_logger.info("añadida clase " + clase.getSimpleName () + " al log");
      }
      catch(Exception e)
      {
        throw( new Exception("No se pudo añadir la clase " + clase.getSimpleName () + " al sistema de logs " ) );
      }
      return m_logger;
    }
    
    public Logger setLogger(Class<?> clase) throws Exception
    {      
      return setLogger (clase, Level.ALL);
    }
    
    public void setLevel( Level level)
    {
      m_logger.setLevel (level);
    }
    
    
    public void write ( Level iLevel, String sCadena )
    {            
      switch( iLevel.toInt ())
      {
        case Level.DEBUG_INT:
        {
          m_logger.debug (sCadena);
          break;
        }
        case Level.ERROR_INT:
        {
          m_logger.error (sCadena);
          break;
        }         
        case Level.INFO_INT:
        {
          m_logger.info (sCadena);
          break;
        }
        case Level.WARN_INT:
        {
          m_logger.warn (sCadena);
          break;
        }
        case Level.TRACE_INT:  
        {
          m_logger.trace (sCadena);
          break;
        }
      }
    }
    public void write(String sCadena)
    {
        write (Level.INFO,sCadena);
    }
    
    public void error(String sCadena)
    {
        write (Level.ERROR,sCadena);
    }
    
    public void trace(String sCadena)
    {
        write(Level.TRACE, sCadena);
    }
    
    public void excepcion( Exception e)
    {
      error ( e.getMessage () );
      StackTraceElement[] aStack = e.getStackTrace();
      for( int i = 0 ; i< aStack.length; i++ )
      {
        write( Level.FATAL,aStack[i].toString () );  
      }            
    }        
  
}
