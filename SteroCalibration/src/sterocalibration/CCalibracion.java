/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sterocalibration;

import boofcv.abst.calib.CalibrateStereoPlanar;
import boofcv.abst.calib.ConfigChessboard;
import boofcv.abst.calib.ConfigSquareGrid;
import boofcv.abst.calib.PlanarCalibrationDetector;
import boofcv.alg.geo.calibration.PlanarCalibrationTarget;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.calib.FactoryPlanarCalibrationTarget;
import boofcv.io.UtilIO;
import boofcv.io.image.UtilImageIO;
import boofcv.misc.BoofMiscOps;
import boofcv.struct.calib.StereoParameters;
import boofcv.struct.image.ImageFloat32;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author aduce
 */
public class CCalibracion
{

  // Detects the target and calibration point inside the target
  PlanarCalibrationDetector detector;
  // Description of the target's physical dimension
  PlanarCalibrationTarget target;
  // List of calibration images
  List<String> left;
  List<String> right;
  // Many 3D operations assumed a right handed coordinate system with +Z pointing out of the image.
  // If the image coordinate system is left handed then the y-axis needs to be flipped to meet
  // that requirement.  Most of the time this is false.
  boolean flipY;

  /**
   * Square grid target taken by a PtGrey Bumblebee camera.
   */
  String m_Dir, m_preDer, m_preIzq ;
  
  public CCalibracion(String dir, String preDer, String preIzq)
  {
      m_Dir = dir;
      m_preDer = preDer;
      m_preIzq = preIzq;
  }
  
  public void setupBumblebeeSquare ( int iCW, int iCH, int iSize )
  {
    // Use the wrapper below for square grid targets.
    detector = FactoryPlanarCalibrationTarget.detectorSquareGrid (new ConfigSquareGrid (iCW, iCH));
    // Target physical description
    target = FactoryPlanarCalibrationTarget.gridSquare (iCW, iCH, iSize, iSize);
    

    left = BoofMiscOps.directoryList (m_Dir, m_preIzq);
    right = BoofMiscOps.directoryList (m_Dir, m_preDer);

    flipY = false;
  }

  /**
   * Chessboard target taken by a PtGrey Bumblebee camera.
   */
  public void setupBumblebeeChess ( int iCW, int iCH, int iSize )
  {
    // Use the wrapper below for chessboard targets.
    detector = FactoryPlanarCalibrationTarget.detectorChessboard (new ConfigChessboard (iCW, iCH));
    // Target physical description
    target = FactoryPlanarCalibrationTarget.gridChess (iCW, iCH, iSize);   

    left = BoofMiscOps.directoryList (m_Dir, m_preIzq);
    right = BoofMiscOps.directoryList (m_Dir, m_preDer);

    flipY = false;
  }

  /**
   * Process calibration images, compute intrinsic parameters, save to a file
   */
  public void process ( String sFicheroSalida)
  {
    // Declare and setup the calibration algorithm
    CalibrateStereoPlanar calibratorAlg = new CalibrateStereoPlanar (detector, flipY);
    calibratorAlg.configure (target, true, 2);

    // ensure the lists are in the same order
    Collections.sort (left);
    Collections.sort (right);

    for (int i = 0; i < left.size (); i++)
    {
      BufferedImage l = UtilImageIO.loadImage (left.get (i));
      BufferedImage r = UtilImageIO.loadImage (right.get (i));

      ImageFloat32 imageLeft = ConvertBufferedImage.convertFrom (l, (ImageFloat32) null);
      ImageFloat32 imageRight = ConvertBufferedImage.convertFrom (r, (ImageFloat32) null);

      if (!calibratorAlg.addPair (imageLeft, imageRight))
      {
        System.out.println ("Failed to detect target in " + left.get (i) + " and/or " + right.get (i));
      }
    }

    // Process and compute calibration parameters
    StereoParameters stereoCalib = calibratorAlg.process ();

    // print out information on its accuracy and errors
    calibratorAlg.printStatistics ();

    // save results to a file and print out
    UtilIO.saveXML (stereoCalib, sFicheroSalida);
    stereoCalib.print ();

    // Note that the stereo baseline translation will be specified in the same units as the calibration grid.
    // Which is in millimeters (mm) in this example.
  }
}
