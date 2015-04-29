/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jduxmancarvision.OpenCV;

import boofcv.abst.feature.disparity.StereoDisparity;
import boofcv.abst.feature.disparity.StereoDisparitySparse;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.disparity.DisparityAlgorithms;
import boofcv.factory.feature.disparity.FactoryStereoDisparity;
import boofcv.gui.image.ShowImages;
import boofcv.gui.image.VisualizeImageData;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import java.awt.image.BufferedImage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 *
 * @author aduce
 */
public class CDisparidad
{

  public static int DISPARIDAD_F32 = 0;
  public static int DISPARIDAD_UI8 = 1;
  public static int DISPARIDAD_BUF_UI8 = 2;
  public static int DISPARIDAD_BUF_F32 = 3;
  private static ImageFloat32 m_disparityMapF32;
  private static ImageUInt8 m_disparityMapUI8;

  /**
   * Computes the dense disparity between between two stereo images. The input
   * images must be rectified with lens distortion removed to work! Floating
   * point images are also supported.
   *
   * @param rectLeft Rectified left camera image
   * @param rectRight Rectified right camera image
   * @param regionSize Radius of region being matched
   * @param minDisparity Minimum disparity that is considered
   * @param maxDisparity Maximum disparity that is considered
   * @return Disparity image
   */
  public ImageUInt8 denseDisparity (ImageUInt8 rectLeft,
                                    ImageUInt8 rectRight,
                                    int regionSize,
                                    int minDisparity,
                                    int maxDisparity,
                                    int maxperpixel,
                                    int validate, 
                                    double texture)
  {
    // A slower but more accuracy algorithm is selected
    // All of these parameters should be turned
    StereoDisparity<ImageUInt8, ImageUInt8> disparityAlg;
    disparityAlg = FactoryStereoDisparity.regionWta (DisparityAlgorithms.RECT_FIVE,
            minDisparity,
            maxDisparity,
            regionSize,
            regionSize,
            maxperpixel,
            validate,
            texture,
            ImageUInt8.class);

    // process and return the results
    disparityAlg.process (rectLeft, rectRight);

    m_disparityMapUI8 = disparityAlg.getDisparity ();
    return m_disparityMapUI8;
  }

  /**
   * Same as above, but compute disparity to within sub-pixel accuracy. The
   * difference between the two is more apparent when a 3D point cloud is
   * computed.
   */
  public ImageFloat32 denseDisparitySubpixel (ImageUInt8 rectLeft,
                                              ImageUInt8 rectRight,
                                              int regionSize,
                                              int minDisparity,
                                              int maxDisparity)
  {
    // A slower but more accuracy algorithm is selected
    // All of these parameters should be turned
    StereoDisparity<ImageUInt8, ImageFloat32> disparityAlg;
    disparityAlg = FactoryStereoDisparity.regionSubpixelWta (DisparityAlgorithms.RECT_FIVE,
            minDisparity,
            maxDisparity,
            regionSize,
            regionSize,
            25,
            1,
            0.2,
            ImageUInt8.class);

    // process and return the results
    disparityAlg.process (rectLeft, rectRight);
    m_disparityMapF32 = disparityAlg.getDisparity ();
    return m_disparityMapF32;
  }

  public double spareDisparity (ImageUInt8 rectLeft,
                                ImageUInt8 rectRight,
                                int regionSize,
                                int X,
                                int Y,
                                int minDisparity,
                                int maxDisparity)
  {
    // A slower but more accuracy algorithm is selected
    // All of these parameters should be turned
    StereoDisparitySparse<ImageUInt8> disparityAlg;
    disparityAlg = FactoryStereoDisparity.regionSparseWta (minDisparity,
            maxDisparity,
            regionSize,
            regionSize,
            25,
            0.2,
            true,
            ImageUInt8.class);

    // process and return the results
    disparityAlg.setImages (rectLeft, rectRight);
    disparityAlg.process (X, Y);
    return disparityAlg.getDisparity ();
  }

  public ImageUInt8 denseDisparity (BufferedImage rectLeft,
                                    BufferedImage rectRight,
                                    int regionSize,
                                    int minDisparity,
                                    int maxDisparity, 
                                    int maxperpixel,
                                    int validate, 
                                    double texture)
  {

    ImageUInt8 distLeft = ConvertBufferedImage.convertFrom (rectLeft, (ImageUInt8) null);
    ImageUInt8 distRight = ConvertBufferedImage.convertFrom (rectRight, (ImageUInt8) null);

    return denseDisparity (distLeft, distRight, regionSize, minDisparity, maxDisparity, maxperpixel,validate,texture);
  }

  public ImageFloat32 denseDisparitySubpixel (BufferedImage rectLeft,
                                              BufferedImage rectRight,
                                              int regionSize,
                                              int minDisparity,
                                              int maxDisparity)
  {

    ImageUInt8 distLeft = ConvertBufferedImage.convertFrom (rectLeft, (ImageUInt8) null);
    ImageUInt8 distRight = ConvertBufferedImage.convertFrom (rectRight, (ImageUInt8) null);

    return denseDisparitySubpixel (distLeft, distRight, regionSize, minDisparity, maxDisparity);
  }

  public double spareDisparity (BufferedImage rectLeft,
                                BufferedImage rectRight,
                                int regionSize,
                                int X,
                                int Y,
                                int minDisparity,
                                int maxDisparity)
  {

    ImageUInt8 distLeft = ConvertBufferedImage.convertFrom (rectLeft, (ImageUInt8) null);
    ImageUInt8 distRight = ConvertBufferedImage.convertFrom (rectRight, (ImageUInt8) null);

    return spareDisparity (distLeft, distRight, regionSize, X, Y, minDisparity, maxDisparity);
  }

  public BufferedImage getBufferedImageDisparity (int iTypeDisparity, int iMin, int iMax)
  {
    BufferedImage visualized = null;
    if (iTypeDisparity == DISPARIDAD_UI8)
    {
      visualized = VisualizeImageData.disparity (m_disparityMapUI8, null, iMin, iMax, 0);
    }
    else if (iTypeDisparity == DISPARIDAD_F32)
    {
      visualized = VisualizeImageData.disparity (m_disparityMapF32, null, iMin, iMax, 0);
    }
    return visualized;
  }
  
  public Mat getMatDisparity (int iTypeDisparity, int iMin, int iMax)
  {
    Mat visualized = null;
    if (iTypeDisparity == DISPARIDAD_UI8)
    {
      visualized = img2Mat( VisualizeImageData.disparity (m_disparityMapUI8, null, iMin, iMax, 0) );
    }
    else if (iTypeDisparity == DISPARIDAD_F32)
    {
      visualized = img2Mat( VisualizeImageData.disparity (m_disparityMapF32, null, iMin, iMax, 0) );
    }
    return visualized;
  }

  public void showDisparity (int iTypeDisparity, int iMin, int iMax)
  {
    if ((iTypeDisparity == DISPARIDAD_BUF_F32) || (iTypeDisparity == DISPARIDAD_BUF_UI8))
    {
      ShowImages.showWindow (getBufferedImageDisparity (iTypeDisparity, iMin, iMax), "Disparidad Buffered");
    }
    else if (iTypeDisparity == DISPARIDAD_F32)
    {
      ShowImages.showWindow (m_disparityMapF32, "Disparidad F32", true);
    }
    else if (iTypeDisparity == DISPARIDAD_UI8)
    {
      ShowImages.showWindow (m_disparityMapUI8, "Disparidad UI8");
    }
  }
  
  public void showMat(String sTitle, Mat img )
  {
     ShowImages.showWindow (mat2Img(img), sTitle);
  }
  
  public void showBuffered(String sTitle, BufferedImage img )
  {
     ShowImages.showWindow (img, sTitle);
  }

  public BufferedImage mat2Img (Mat in)
  {
    BufferedImage out;
    byte[] data = new byte[in.width() * in.height() * (int) in.elemSize ()];
    int type;
    in.get (0, 0, data);

    if (in.channels () == 1)
    {
      type = BufferedImage.TYPE_BYTE_GRAY;
    }
    else
    {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }

    out = new BufferedImage (in.width(), in.height(), type);

    out.getRaster ().setDataElements (0, 0, in.width(), in.height(), data);
    return out;
  }

  public Mat img2Mat (BufferedImage in)
  {
    Mat out;
    byte[] data;
    int r, g, b;

    if (in.getType () == BufferedImage.TYPE_INT_RGB)
    {
      out = new Mat (in.getHeight (), in.getWidth (), CvType.CV_8UC3);
      data = new byte[in.getWidth () * in.getHeight () * (int) out.elemSize ()];
      int[] dataBuff = in.getRGB (0, 0, in.getWidth (), in.getHeight (), null, 0, in.getWidth ());
      for (int i = 0; i < dataBuff.length; i++)
      {
        data[i * 3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
        data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
        data[i * 3 + 2] = (byte) ((dataBuff[i]) & 0xFF);
      }
    }
    else
    {
      out = new Mat (in.getHeight (), in.getWidth (), CvType.CV_8UC1);
      data = new byte[in.getWidth () * in.getHeight () * (int) out.elemSize ()];
      int[] dataBuff = in.getRGB (0, 0, in.getWidth (), in.getHeight (), null, 0, in.getWidth ());
      for (int i = 0; i < dataBuff.length; i++)
      {
        r = (byte) ((dataBuff[i] >> 16) & 0xFF);
        g = (byte) ((dataBuff[i] >> 8) & 0xFF);
        b = (byte) ((dataBuff[i]) & 0xFF);
        data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
      }
    }
    out.put (0, 0, data);
    return out;
  }
}
