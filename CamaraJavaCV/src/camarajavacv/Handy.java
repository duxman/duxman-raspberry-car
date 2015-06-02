package camarajavacv;


// Handy.java
// Andrew Davison, July 2013, ad@fivedots.psu.ac.th

/* Detect the user's gloved hand and fingers, drawing information
 on top of a webcam image.

 Usage:
 > run Handy
 */
import camarajavacv.HandPanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;

public class Handy extends JFrame
{

    // GUI components

    private HandPanel handPanel;

    public Handy()
    {
        super("Hand Detector");

        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        // Preload the opencv_objdetect module to work around a known bug.
        //Loader.load(opencv_objdetect.class);
        System.load("/home/duxman/git/duxman-raspberry-car/DetectarCirculos/lib/libopencv_java2410.so" );    
        
        handPanel = new HandPanel(); // the webcam pictures and drums appear here
        c.add(handPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                handPanel.closeDown();    // stop snapping pics, and any drum playing
                System.exit(0);
            }
        });

        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    } // end of Handy()

  // -------------------------------------------------------
    public static void main(String args[])
    {
        new Handy();
    }

} // end of Handy class
