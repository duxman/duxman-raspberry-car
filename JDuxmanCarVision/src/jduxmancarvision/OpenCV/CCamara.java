/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.OpenCV;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;

/**
 *
 * @author duxman
 */
public class CCamara extends CFuncionesBasicasCamara implements IBasicoVideo
{

    protected VideoCapture m_camara;
    protected Mat m_ultimaImagen;
    protected Mat m_ultimaImagenGris;
    protected Mat m_ultimaImagenProcesada;
    protected Mat m_matrizX;
    protected Mat m_matrizY;
    protected Mat m_matrizCamara;
    protected Mat m_distorsion;
    protected List<Mat> m_puntosImagen;
    protected List<Mat> m_puntosObjeto;
    protected List<Mat> m_R;
    protected List<Mat> m_T;

    protected MatOfPoint2f m_esquinas;

    private int m_iWidth;
    private int m_iHeight;
    private int m_iIdCamara;
    protected Blobs m_Regions;
    
    

    public CCamara(int iIdCamara, int iWidth, int iHeight)
    {
        m_log = Logger.getRootLogger();
        m_iIdCamara = iIdCamara;
        m_ultimaImagen = new Mat();
        m_ultimaImagenGris = new Mat();
        m_ultimaImagenProcesada = new Mat();
        m_matrizX = new Mat();
        m_matrizY = new Mat();
        
        m_esquinas = new MatOfPoint2f();
        
        m_matrizCamara = new Mat();//Mat.eye(3,3, CvType.CV_64F);
        m_distorsion = new Mat();//Mat.eye(5,1, CvType.CV_64F);
        m_puntosImagen = new ArrayList<>();
        m_puntosObjeto = new ArrayList<>();
        m_R = new ArrayList<>();
        m_T = new ArrayList<>();
    }

    public int iniciaCamara(int iId)
    {
        int iRtn = eValoresRetorno.OK.ordinal();
        try
        {
            m_camara = new VideoCapture(iId);

            if (m_camara.isOpened() == true)
            {
                m_iIdCamara = iId;                            
                m_log.info("Encontrada Camara " + iId);                
            }                
            else            
            {
                iRtn = eValoresRetorno.ERROR.ordinal();
            }
        }
        catch (Exception e)
        {
            iRtn = eValoresRetorno.ERROR.ordinal();
        }
        finally
        {
            return iRtn;
        }
    }
    
    public Mat capturarImagenVideo()
    {
        Mat Rtn = new Mat();
        m_camara.read(Rtn);
        return Rtn;
    }
    
    public Mat capturarImagen()
    {        
        try
        {
            m_camara.read(m_ultimaImagen);
            if (m_ultimaImagen.empty() == false)
            {
                Imgproc.cvtColor(m_ultimaImagen, m_ultimaImagenGris, Imgproc.COLOR_RGB2GRAY);
            }
        }
        catch (Exception e)
        {
            m_log.error(e.getMessage());
        }
        finally
        {
            return m_ultimaImagen;
        }
    }

    public Mat dameImagen()
    {
        return m_ultimaImagen;
    }
    
    public Mat dameImagen(boolean vertical)
    {
        Mat m = new Mat();
        Core.transpose(m_ultimaImagen, m);
        Core.flip(m, m, 1);
        return m;
    }

    public Mat dameImagenGris()
    {

        return m_ultimaImagenGris;
    }
    
    public Mat dameUltimaImagenProcesada()
    {
      return  m_ultimaImagenProcesada;
    }

    public boolean BuscarAjedrez(Size partnerSize)
    {
        boolean bRtn = false;
        m_partnerSize =  partnerSize;
        try
        {
            TermCriteria m_criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 30, 0.01);
            Size ventana = new Size(15, 15);
            Size zona = new Size(-1, -1);
            
            boolean retorno = Calib3d.findChessboardCorners(m_ultimaImagenGris, 
                                                            m_partnerSize, 
                                                            m_esquinas, 
                                                            Calib3d.CALIB_CB_ADAPTIVE_THRESH |
                                                            Calib3d.CALIB_CB_NORMALIZE_IMAGE);
            
            
            m_ultimaImagen.copyTo(m_ultimaImagenProcesada);
            Calib3d.drawChessboardCorners(m_ultimaImagenProcesada, m_partnerSize, m_esquinas, retorno);

            if (retorno == false)
            {
                bRtn = false;
            }
            else
            {                
                Imgproc.cornerSubPix(m_ultimaImagenGris, m_esquinas, ventana, zona, m_criteria);
                bRtn = retorno;
            }
        }
        catch (Exception e)
        {
            m_log.error(e.getMessage());
            bRtn = false;
        }
        finally
        {
            return bRtn;
        }
    }
   
    public boolean calibrar(Size partnerSize)
    {        
        return calibrar(partnerSize,false, m_ultimaImagen );
    }
    
    public boolean calibrar(Size partnerSize,boolean  calibrar)
    {        
        return calibrar(partnerSize,calibrar, m_ultimaImagen );
    }
    
    public boolean calibrar(Size partnerSize,boolean  calibrar, Mat imagen)
    {
        boolean rtn = false;
        try
        {
            m_ultimaImagen = imagen;
            Imgproc.cvtColor(m_ultimaImagen, m_ultimaImagenGris, Imgproc.COLOR_RGB2GRAY);            
            if(m_ultimaImagen.empty() == false )
            {        
                m_partnerSize = partnerSize;
                
                m_puntosImagen.add(m_esquinas);
                
                m_puntosObjeto.add( dameListaPuntos3D() );
                
                if( calibrar )    
                {
                    double errReproj = Calib3d.calibrateCamera(m_puntosObjeto, m_puntosImagen, m_ultimaImagen.size(), m_matrizCamara, m_distorsion, m_R, m_T, FLAGS_CALIB);
                    m_log.info("Calibrada");
                }
                rtn = true;            
            }
            else
            {
                m_log.warn("Imagen no encontrada ");
            }
        }
        catch (Exception e)
        {
            m_log.error(e.getMessage());
        }
        return rtn;
    }

    public Mat get_matrizX()
    {
        return m_matrizX;
    }

    public MatOfPoint2f get_esquinas()
    {
        return m_esquinas;
    }

    public Mat get_matrizY()
    {
        return m_matrizY;
    }

    public Mat get_matrizCamara()
    {
        return m_matrizCamara;
    }

    public Mat get_distorsion()
    {
        return m_distorsion;
    }

    public List<Mat> get_puntosImagen()
    {
        return m_puntosImagen;
    }

    public List<Mat> get_puntosObjeto()
    {
        return m_puntosObjeto;
    }

    public void set_matrizX(Mat matrizX)
    {
        this.m_matrizX = matrizX;
    }

    public void set_matrizY(Mat matrizY)
    {
        this.m_matrizY = matrizY;
    }

    public void set_distorsion(Mat distorsion)
    {
        this.m_distorsion = distorsion;
    }

    public void set_esquinas(MatOfPoint2f esquinas)
    {
        m_esquinas = esquinas;
    }

    /**
     * @return the m_iIdCamara
     */
    public int dameIdCamara()
    {
        return m_iIdCamara;
    }
    
  
    public void  detectarObjetos(int iThresholdmin, int ithresholdMax, int iThresholdType, int iPunto, int iAreaMin)
    {
       
        Imgproc.adaptiveThreshold(m_ultimaImagenGris, m_ultimaImagenProcesada, ithresholdMax,ADAPTIVE_THRESH_MEAN_C,iThresholdType,5,iPunto ); 
        Imgproc.erode(m_ultimaImagenProcesada, m_ultimaImagenProcesada, new Mat() );
        //Imgproc.dilate(m_ultimaImagenProcesada, m_ultimaImagenProcesada, new Mat() ,new Point(-1, -1), iPunto);  
        Imgproc.blur(m_ultimaImagenProcesada, m_ultimaImagenProcesada, new Size(iPunto, iPunto) );
        
       
        
        /*List<MatOfPoint> contorno = new ArrayList<>();
        Imgproc.erode(m_ultimaImagenGris, m_ultimaImagenProcesada, new Mat() );
        Imgproc.dilate(m_ultimaImagenProcesada, m_ultimaImagenProcesada, new Mat() ,new Point(-1, -1), 1);  
        Imgproc.findContours(m_ultimaImagenProcesada,contorno,new Mat(),Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        for(int i=0; i< contorno.size();i++)
        {
            Imgproc.drawContours(m_ultimaImagen, contorno,i,new Scalar(0, 255, 0),1,8,new Mat(),1,new Point(iPunto, iPunto) );
        }*/
        
        m_Regions = new Blobs();
        m_Regions.BlobAnalysis( m_ultimaImagenProcesada,               // image
                              -1, -1,                     // ROI start col, row
                              -1, -1,                     // ROI cols, rows
                               0,                         // border (0 = black; 1 = white)
                               1000);     
        
        m_log.info( m_Regions.PrintRegionData() );
        
        for(int i = 1; i <= m_Regions.MaxLabel; i++)
        {
            double [] Region = m_Regions.RegionData[i];
            int Parent = (int) Region[Blobs.BLOBPARENT];
            int Color = (int) Region[Blobs.BLOBCOLOR];
            int MinX = (int) Region[Blobs.BLOBMINX];
            int MaxX = (int) Region[Blobs.BLOBMAXX];
            int MinY = (int) Region[Blobs.BLOBMINY];
            int MaxY = (int) Region[Blobs.BLOBMAXY]; 
            int area = (int) Region[Blobs.BLOBAREA];            
            if( ( MinX == 0 && MinY == 0 ) && ( MaxX >= ( m_ultimaImagenProcesada.cols() * 0.75 ) && MaxY >= ( m_ultimaImagenProcesada.rows() * 0.75 ) ) )
            {
                m_log.info("Borde de la imagen ignorado");
                m_Regions.ResetRegion(i);
            }
            else
            {
             
                
            }
                    
        }    
    }

}
