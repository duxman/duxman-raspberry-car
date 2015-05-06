/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision;

import duxmancar.log.CLog;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import jduxmancarvision.OpenCV.IBasicoVideo;
import static jduxmancarvision.OpenCV.IBasicoVideo.COMANDO_VIDEO_DEV_DER;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class JDuxmanCarVision
{

    /**
     * @param args the command line arguments
     */
    static CLog claselog;
    static Logger m_log;
    static JVision vision;
    static JVentana ventana;
    public static int CAMARA_DERECHA = 0;
    public static int CAMARA_IZQUIERDA = 1;
    
    public static void main(String[] args)
    {
        claselog = new CLog(false, "DuxmanCarVision.log", 10000, 5);
        m_log = Logger.getRootLogger();
        
        m_log.info("Ejecutamos comando para detectar video devices");        
        
        String VideoIzq =  cmdExec(IBasicoVideo.COMANDO_VIDEO_DEV_IZQ);
        String VideoDer =  cmdExec(IBasicoVideo.COMANDO_VIDEO_DEV_DER);
        
        int index = VideoIzq.indexOf(IBasicoVideo.CADENA_BUSCAR)+ IBasicoVideo.CADENA_BUSCAR.length();        
        String videoDerId =  VideoIzq.substring(index ,index + 1 );
        CAMARA_IZQUIERDA = Integer.parseInt(videoDerId);
        
        index = VideoDer.indexOf(IBasicoVideo.CADENA_BUSCAR)+ IBasicoVideo.CADENA_BUSCAR.length();        
        String videoIzqId =  VideoDer.substring(index ,index + 1 );
        CAMARA_DERECHA = Integer.parseInt(videoIzqId);
        
        
        vision = new JVision();
        ventana = new JVentana(vision);
        vision.setVentana(ventana);

    }

    public static String cmdExec(String cmdLine)
    {
        String line;
        String output = "";
        try
        {
            Process p = Runtime.getRuntime().exec(cmdLine);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null)
            {
                output += (line + '\n');
            }
            input.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return output;
    }

}
