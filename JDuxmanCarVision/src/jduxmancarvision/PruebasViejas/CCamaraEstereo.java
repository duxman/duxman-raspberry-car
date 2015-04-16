/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.PruebasViejas;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_WIDTH;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author duxman
 */
public class CCamaraEstereo implements IInterfazBasicaVisionEstero
{
    private Logger m_log;
   
    private VideoCapture[] m_Camaras;
    private Mat[] m_imagenes;
    private Mat[] m_imagenesGris;
    private Size m_tamImagenes;
    
    public CCamaraEstereo()
    {
        m_log = Logger.getRootLogger();       
        initOpenCV( "/home/duxman/git/duxman-raspberry-car/JDuxmanCarVision/dist/lib/libopencv_java2410.so" );
    }
    
    public void initOpenCV( String RutaLib  )
    {
        try
        {
            m_log.info("InicializamosOpenCV");
            // Load the native library.
            System.load( RutaLib );                                    
        }
        catch ( Exception ex )
        {
            m_log.error( "Error inicializando  OpenCV " + ex.getMessage() );
        }
    }
    
    public int initCamaras( int iWidth , int iHeigth )
    {
        m_tamImagenes =  new Size( iWidth, iHeigth );
        for( int i=0 ; i<2 ; i++ )
        {
            m_log.info("inicializamos camara " + i );
        
            m_Camaras[i]  =  new VideoCapture( i );
            m_Camaras[i].set(CV_CAP_PROP_FRAME_WIDTH,iWidth);
            m_Camaras[i].set(CV_CAP_PROP_FRAME_HEIGHT,iHeigth);
            
            m_imagenes[i] = new Mat();
            m_imagenesGris[i] = new Mat();
        }
    
        if( m_Camaras[CAMARA1].isOpened() && m_Camaras[CAMARA2].isOpened() )
        {
           return OK; 
        }
        else
        {
            return ERROR;
        }                
    }
    
    public int CapturarImagenes()
    {
        m_Camaras[CAMARA1].read( m_imagenes[CAMARA1] );
        m_Camaras[CAMARA2].read( m_imagenes[CAMARA2] );
        
        if( m_imagenes[CAMARA1].empty() == false && m_imagenes[CAMARA2].empty() == false )
        {
           Imgproc.cvtColor(m_imagenes[CAMARA1], m_imagenesGris[CAMARA1], Imgproc.COLOR_RGB2GRAY);               
           Imgproc.cvtColor(m_imagenes[CAMARA2], m_imagenesGris[CAMARA2], Imgproc.COLOR_RGB2GRAY);               
           
           return OK; 
        }
        else
        {
            return ERROR;
        }            
    }
    
    public Mat dameImagen( int idx )
    {
        return m_imagenes[idx];
    }
    
    public Mat dameImagenGris( int idx )
    {
        return m_imagenesGris[idx];
    }
          
}
