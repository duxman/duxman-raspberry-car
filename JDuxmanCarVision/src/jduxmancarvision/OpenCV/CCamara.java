/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.OpenCV;

import java.util.ArrayList;
import java.util.List;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_WIDTH;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author duxman
 */
public class CCamara implements IBasicoVideo
{
  protected VideoCapture m_camara; 
  protected Mat m_ultimaImagen;
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
  private Size m_partnerSize;
  
  public CCamara( int iIdCamara, int iWidth, int iHeight )
  {       
    m_ultimaImagen= new Mat();
    m_matrizX = new Mat();
    m_matrizY = new Mat();
    m_distorsion = new Mat();    
    m_esquinas =  new MatOfPoint2f();   
    m_matrizCamara = new Mat();
    m_puntosImagen = new ArrayList<>();
    m_puntosObjeto = new ArrayList<>();
    m_R = new ArrayList<>();
    m_T = new ArrayList<>();
  }
  
  public int iniciaCamara() 
  { 
    int iRtn =  eValoresRetorno.OK.ordinal();
    try
    {
      m_camara = new VideoCapture(m_iIdCamara);
      m_camara.set(CV_CAP_PROP_FRAME_WIDTH,m_iWidth);
      m_camara.set(CV_CAP_PROP_FRAME_HEIGHT,m_iHeight);
      
      if( m_camara.isOpened() == false )
      {
         iRtn =  eValoresRetorno.ERROR.ordinal();
      }
    }
    catch(Exception e)
    {
      iRtn =  eValoresRetorno.ERROR.ordinal();
    }
    finally
    {
      return iRtn;
    }
  }
  
  public Mat capturarImagen()
  {
    Mat Rtn = new Mat();
    try
    {
      m_camara.read(Rtn);
      if( Rtn.empty() == false )
      {
          Rtn.copyTo( m_ultimaImagen );
      }
    }
    catch(Exception e)
    {
      
    }
    finally
    {
      return Rtn;
    }
  }
          
  public Mat dameImagen()
  {    
    return m_ultimaImagen;
  }
  
  public Mat dameImagenGris()
  {
    Mat Rtn = new Mat();
    try
    {
       Imgproc.cvtColor(m_ultimaImagen, Rtn, Imgproc.COLOR_RGB2GRAY);
    }
    catch(Exception e)
    {
      
    }
    finally
    {
      return Rtn;
    }
  }
  
  private boolean BuscarAjedrez()
  {
      boolean bRtn= true;
      
      try
      {
        TermCriteria m_criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 40, 0.001);      
        Size ventana = new Size(5, 5);
        Size zona = new Size(-1, -1);  
        
        if( Calib3d.findChessboardCorners(dameImagenGris(), m_partnerSize, m_esquinas, FLAGS_CORNER ) == false )      
        {
              return false;
        }
        
        Imgproc.cornerSubPix(dameImagenGris(), m_esquinas, ventana, zona, m_criteria);
      }
      catch(Exception e)
      {
          bRtn= false;
      }
      finally
      {
          return bRtn;
      }
  }
  
  public void calibrar( Size partnerSize )
  {    
    try
    {
       m_partnerSize = partnerSize;
       if( BuscarAjedrez() == true )
       {
            double errReproj = Calib3d.calibrateCamera(m_puntosObjeto,m_puntosImagen, dameImagen().size(),m_matrizCamara, m_distorsion,m_R,m_T,FLAGS_CALIB);
            System.out.println("done, \nerrReproj = " + errReproj);
            System.out.println("cameraMatrix = \n" + m_matrizCamara.dump());
            System.out.println("distCoeffs = \n" + m_distorsion.dump());
       }
    }
    catch(Exception e)
    {
      
    }    
  }
  

  public Mat get_matrizX ()
  {
    return m_matrizX;
  }
  
  public MatOfPoint2f get_esquinas ()
  {
    return m_esquinas;
  }
  

  public Mat get_matrizY ()
  {
    return m_matrizY;
  }
  
  public Mat get_matrizCamara ()
  {
    return m_matrizCamara;
  }
  

  public Mat get_distorsion ()
  {
    return m_distorsion;
  }

  public List<Mat> get_puntosImagen ()
  {
    return m_puntosImagen;
  }
  
  public List<Mat> get_puntosObjeto()
  {
    return m_puntosObjeto;
  }          
  
  public void set_matrizX (Mat matrizX)
  {
    this.m_matrizX = matrizX;
  }

  public void set_matrizY (Mat matrizY)
  {
    this.m_matrizY = matrizY;
  }
  

  public void set_distorsion (Mat distorsion)
  {
    this.m_distorsion = distorsion;
  }
  
  public void set_esquinas ( MatOfPoint2f esquinas)
  {
    m_esquinas = esquinas;
  }
  
}