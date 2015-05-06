/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import jduxmancarvision.OpenCV.*;
import org.opencv.core.Core;
import static org.opencv.imgcodecs.Imgcodecs.imencode;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
/**
 *
 * @author duxman
 */
public class JVision extends Thread
{
    Logger m_log;
    CCamaraStereo camaras;
    Size m_partner;
    JVentana ventana;
    boolean bCalibrar;
    boolean b3d;
    boolean bobjetos;
    static int inum;
    PrintWriter listaXML;
    PrintWriter lista;
    public JVision()
    {       
      m_log = Logger.getRootLogger();      
      bobjetos= false;
    }
    
    public void setVentana( JVentana v )
    {
       ventana = v;
    }
    
    public void init(  )
    {
        try
        {   
            inum = 0;
            m_log.info("Inicializamos opencv");
            // Load the native library.
            System.load("/home/duxman/git/duxman-raspberry-car/JDuxmanCarVision/dist/lib/libopencv_java300.so" );                        
            inicializaVideo();            
        }
        catch (InterruptedException ex)
        {
            
        }
    }
    
    public void inicializaVideo() throws InterruptedException
    {               
        camaras = new CCamaraStereo( m_partner );                                
        
        if(camaras.iniciaCamaras() == 1)
        {
           m_log.info("Camera OK?");
        }
        else
        {                                   
            m_log.info("Camera Error");
        }        
    }
    
    public void CargarCalibracion()
    {
        camaras.cargarCalibracion();        
    }
       
    
    public void procesar3d()
    {
      int imindisp =( ( Integer) ventana.mindisp.getValue()).intValue()*16;
      int imaxdisp =( ( Integer) ventana.spindisp.getValue()).intValue()*16;
      int preFilterSize =( ( Integer) ventana.preFilterSize.getValue()).intValue();
      int preFilterCap =( ( Integer) ventana.preFilterCap.getValue()).intValue();
      int SADWindowSize =( ( Integer) ventana.SADWindowSize.getValue()).intValue();
      int textureThreshold =( ( Integer) ventana.textureThreshold.getValue()).intValue();
      int uniquenessRatio =( ( Integer) ventana.uniquenessRatio.getValue()).intValue();
                  
              
      
      /*int isad =( ( Integer) ventana.spinsombras.getValue()).intValue();
      int maxperpixel =( ( Integer) ventana.maxperpixel.getValue()).intValue();
      int validate =( ( Integer) ventana.validate.getValue()).intValue();
      double texture =( ( Integer) ventana.texture.getValue()).doubleValue();
      texture  = (texture<0)?(-texture/10):(texture);
      int iPrefilterCap =( ( Integer) ventana.iprefiltersize.getValue()).intValue();
      int iuniqueratio =( ( Integer) ventana.iuniqueratio.getValue()).intValue();
      int ispecksize =( ( Integer) ventana.iwinsize.getValue()).intValue();
      int ispeckrange =( ( Integer) ventana.iwinrange.getValue()).intValue();
      int imaxdiff =( ( Integer) ventana.imaxdiff.getValue()).intValue();
      */
      Mat m = camaras.procesarImagen3d(imindisp ,imaxdisp ,preFilterSize ,preFilterCap ,SADWindowSize,textureThreshold,uniquenessRatio               );
      
      if(m.empty() == false )
      {          
          
          ventana.setImage( convertir(m) , ventana.lblImgFinal);
          ventana.setImage( convertir(camaras.m_DisparidadProcesada) , ventana.lblImgFinal2);
          distancia();
          ventana.setImage( convertir(camaras.m_imagenRectificadaDerecha) , ventana.lblImgDerPro);
          ventana.setImage( convertir(camaras.m_imagenRectificadaIzquierda) , ventana.lblImgIzqPro);
      } 
      
      
    /*  Mat m = camaras.procesarImagen3d(imindisp,imaxdisp, maxperpixel,validate, texture );
      
      if(m.empty() == false )
      {          
          ventana.setImage( convertir(camaras.m_imagenRectificadaIzquierda) , ventana.lblImgIzqPro);
          ventana.setImage( convertir(m) , ventana.lblImgFinal);          
          distancia();
          ventana.setImage( convertir(camaras.m_imagenRectificadaDerecha) , ventana.lblImgDerPro);
      }  
      */
    }
    public void tomarfoto3()
    {
        imwrite("imgrgb.ppm", camaras.camaraIzquierda().dameImagen());
        imwrite("disparidad.pgm", camaras.m_DisparidadProcesada);
    }
    
   
    
    public void procesarObjetos()
    {
       int iTresMin =  ( (Integer) ventana.ithresholdMin.getValue()).intValue();
       int iTresMax =  ( (Integer) ventana.ithresholdMax.getValue()).intValue();
       int iThresType =  ventana.thresholdType.getSelectedIndex();
       int iPunto =  ( (Integer) ventana.iPunto.getValue()).intValue();
       int iarea =   ( (Integer) ventana.iarea.getValue()).intValue();
       
      camaras.detectarObjetos(iTresMin, iTresMax, iThresType, iPunto, iarea);      
      
      ventana.setImage( convertir(camaras.camaraIzquierda().dameImagen()) , ventana.lblImgIzqPro);
      ventana.setImage( convertir(camaras.camaraDerecha().dameImagen()) , ventana.lblImgDerPro);            
      
      ventana.setImage(convertir(camaras.camaraIzquierda().dameUltimaImagenProcesada()),ventana.lblImgFinal);
      ventana.setImage(convertir(camaras.camaraDerecha().dameUltimaImagenProcesada()),ventana.lblImgFinal2);
    }
    public void fichero() throws FileNotFoundException
    {
        if( listaXML == null )
        {
          listaXML = new PrintWriter("imagenes/list.xml") ;
          lista = new PrintWriter("imagenes/list.txt") ;
          listaXML.write("<?xml version=\"1.0\"?>\n<opencv_storage>\n<imagelist>\n");
        }
        else
        {
            listaXML.write("</imagelist>\n</opencv_storage>\n");
            listaXML.flush();
            listaXML.close();
            lista.flush();
            lista.close();
        }
    }
    public void  CalibrarIndividual() throws IOException
    {
      if( lista == null )
      {
          lista = new PrintWriter("imagenes/list.txt") ;
      }
      if( listaXML == null )
      {
          listaXML = new PrintWriter("imagenes/list.xml") ;
      }
      int esqX=((Integer)ventana.esquinasX.getValue()).intValue();
      int esqY=((Integer)ventana.esquinasY.getValue()).intValue();
      Size partnerSize = new Size(esqX,esqY );
      boolean ret = camaras.calibrarIndividual( partnerSize );
      if(ret == true )
      {
        String izq,der;
        izq = "imagenes/izq"+inum+".jpg";
        der = "imagenes/der"+inum+".jpg";
        inum++;                
                        
        listaXML.write("\""+izq+"\"\n");
        listaXML.write("\""+der+"\"\n");       
        lista.write(izq);
        lista.write(der);
        
        tomarfotos(izq,der);  
        
        ventana.setImage(convertir(camaras.camaraIzquierda().dameUltimaImagenProcesada()),ventana.lblImgDerPro);
        ventana.setImage(convertir(camaras.camaraDerecha().dameUltimaImagenProcesada()),ventana.lblImgIzqPro);                        
      }
    }
    
    public void tomarfotos(String izq, String der)
    {                
        imwrite(izq, camaras.camaraIzquierda().dameImagen());
        imwrite(der, camaras.camaraDerecha().dameImagen());    
    }
    public void tomarfotos2(String derecha, String Izquierda)
    {                
        camaras.capturarCamaraEstereo();
        imwrite(derecha, camaras.camaraIzquierda().dameImagen());
        imwrite(Izquierda, camaras.camaraDerecha().dameImagen());    
    }
    public void distancia()
    {
        double d = camaras.dameDistanciaAZona();
        if ( d > 10)
        ventana.setTitle(" OpenCV 3D DISTANCIA CENTRO :" + String.valueOf(d));
    }
    public void  CalibrarEstero()
    {
        camaras.calibrar();
    }
       
   
    
    public void run()
    {
        while(true)
        {            
            Mat[] imgs = camaras.capturarCamaraEstereoVideo();                          
                        
            if(!imgs[0].empty() )
                ventana.setImage(convertir(imgs[0]),ventana.lblImgDer);
            
            if(!imgs[1].empty())
                ventana.setImage(convertir(imgs[1]),ventana.lblImgIzq);
            
            if( b3d )
            {
                try
                {
                    procesar3d();
                }
                catch(Exception e)
                {
                    m_log.error("erro procesando");
                }
            }
            
            if( bobjetos )
            {
                try
                {
                    procesarObjetos();
                }
                catch(Exception e)
                {
                    m_log.error("error procesando procesarObjetos");
                }
            }
        }        
    }
    
   /*public void detectarCosasV1()
    {
       try
       { 
           if( m_bDetectar )  
           {  
              
              //Imgproc.adaptiveThreshold(imagenTMP,imagenTMP,250, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 35, 4);                            
                             
              Imgproc.threshold(imagenTMP, imagenTMP, m_ithresholValue, 255, Imgproc.THRESH_BINARY );              
              
              Imgproc.dilate(imagenTMP, imagenTMP, new Mat() ,new Point(-1, -1), m_isizeValue);
              for(int i = 0 ;i  < m_isizeValue; i++)
                    Imgproc.GaussianBlur(imagenTMP, imagenTMP, new  Size(m_isizeValue, m_isizeValue),m_isizeValue,m_isizeValue);               
              
              Imgproc.threshold(imagenTMP, imagenTMP, m_ithresholValue, 255, Imgproc.THRESH_BINARY );                                         

              
           }
       }
       catch(Exception e)
       {
           m_log.error("Error detectar " +e.getMessage());
       }
    }
    
     public void detectarCosas()
    {
       try
       { 
           if( m_bDetectar )  
           {  
             
            Imgproc.threshold(imagenTMP, imagenTMP, m_ithresholValue, 255, Imgproc.THRESH_BINARY ); 
            Imgproc.erode(imagenTMP, imagenTMP, new Mat() );
            Imgproc.dilate(imagenTMP, imagenTMP, new Mat() ,new Point(-1, -1), 1);  

            //Core.merge(hsv_planes, imagenTMP);           
            //Imgproc.threshold(imagenTMP, imagenTMP, m_ithresholValue, 255, Imgproc.THRESH_BINARY_INV );     
                                                             
           }
       }
       catch(Exception e)
       {
           m_log.error("Error detectar " +e.getMessage());
       }
    }
    
     public void detectarCosas2()
    {
       try
       { 
           if( m_bDetectar2 )  
           {
             
               
               
                Blobs Regions = new Blobs();
                 Regions.BlobAnalysis(
                    imagenTMP,               // image
                    -1, -1,                     // ROI start col, row
                    -1, -1,                     // ROI cols, rows
                    1,                          // border (0 = black; 1 = white)
                    1000);     
                m_log.info( Regions.PrintRegionData() );
                
                for(int i = 1; i <= Blobs.MaxLabel; i++)
                {
                    double [] Region = Blobs.RegionData[i];
                    int Parent = (int) Region[Blobs.BLOBPARENT];
                    int Color = (int) Region[Blobs.BLOBCOLOR];
                    int MinX = (int) Region[Blobs.BLOBMINX];
                    int MaxX = (int) Region[Blobs.BLOBMAXX];
                    int MinY = (int) Region[Blobs.BLOBMINY];
                    int MaxY = (int) Region[Blobs.BLOBMAXY]; 
                    int area = (int) Region[Blobs.BLOBAREA];
                    if( ( MinX == 0 && MinY == 0 ) && ( MaxX >= ( imagenTMP.cols() * 0.75 ) && MaxY >= ( imagenTMP.rows() * 0.75 ) ) )
                    {
                        m_log.info("Borde de la imagen ignorado");
                    }
                    else
                    {
                        Core.rectangle(imagen0, new Point(MinX, MinY), new Point(MaxX , MaxY ), new Scalar(0, 255, 0),3);
                    }
                    
                }                                                                
           }
       }
       catch(Exception e)
       {
           m_log.error("Error detectar " +e.getMessage());
       }
    }
    
               
    public void runFace(  )
    {
        try
        {
            if( m_bCaras )
            {                                                         
                faceDetector1.detectMultiScale(imagen0, faceDetections);
                for (Rect rect : faceDetections.toArray()) 
                {
                    Core.rectangle(imagen0, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    m_log.info("Cara detectada");                    
                }                                

                faceDetector2.detectMultiScale(imagen0, faceDetections);
                for (Rect rect : faceDetections.toArray()) 
                {
                    Core.rectangle(imagen0, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    m_log.info("Cara detectada");
                }                                    

                faceDetector3.detectMultiScale(imagen0, faceDetections);
                for (Rect rect : faceDetections.toArray()) 
                {
                    Core.rectangle(imagen0, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    m_log.info("Cara detectada");
                }                                                    
            }
        }
        catch(Exception e)
        {
            m_log.error("Error runface" + e.getMessage());
        }
        
    }
    */
    private Image convertir(Mat imagen) 
    {
        MatOfByte matOfByte = new MatOfByte();
        imencode(".jpg", imagen, matOfByte); 

        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;

        try 
        {

            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } 
        catch (Exception e) 
        {
         m_log.error(e.getMessage());
        }
        return (Image)bufImage;
    }
    
}
