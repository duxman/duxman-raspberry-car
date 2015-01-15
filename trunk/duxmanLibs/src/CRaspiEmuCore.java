
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aduce
 */
public class CRaspiEmuCore
{
    List<String>   m_listaPuertosGPIO;
    Properties     m_properties;
    public CRaspiEmuCore()
    {
      m_properties = new Properties();
      InputStream input = null;
      m_listaPuertosGPIO = Collections.synchronizedList (new ArrayList<String>());
      try 
      {

        input = new FileInputStream("RaspiEmu.properties"); 
        // load a properties file
        m_properties.load(input);        
      } 
      catch (Exception ex) 
      {
        ex.printStackTrace();
      } 
      finally 
      {
        if (input != null) 
        {
          try 
          {
            input.close();
          } 
          catch (Exception e) 
          {
            e.printStackTrace();
          }
        }
      }
   }
   public List<String> obtenerListaGPIO()
   {
      Enumeration em = m_properties.keys();
      while(em.hasMoreElements())
      {
        String str = (String)em.nextElement();
        System.out.println(str + ": " + pro.get(str));
      }
      return      
   }
}
