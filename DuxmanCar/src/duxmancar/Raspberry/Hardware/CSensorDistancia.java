/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Raspberry.Hardware;

/**
 *
 * @author duxman
 */
/**
 * Class to monitor distance measured by an HC-SR04 distance sensor on a
 * Raspberry Pi.
 * 
* The main method assumes the trig pin is connected to the pin # 7 and the echo
 * pin is connected to pin # 11. Output of the program are comma separated lines
 * where the first value is the number of milliseconds since unix epoch, and the
 * second value is the measured distance in centimeters.
 */
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.text.DecimalFormat;
import java.text.Format;

/**
 * DistanceMonitor class to monitor distance measured by sensor
 * 
* @author Rutger Claes <rutger.claes@cs.kuleuven.be>
 */
public class CSensorDistancia
{

    private final static Format DF22 = new DecimalFormat("#0.00");
    private final static double SOUND_SPEED = 34300; // in cm, 343 m/s
    private final static double DIST_FACT = SOUND_SPEED / 2; // round trip
    private final static int MIN_DIST = 5;

    
    public double CalcularDistancia()
    {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput trigPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Trig", PinState.LOW);
        final GpioPinDigitalInput echoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, "Echo");
        
        double tiempoInicio = 0d;
        double TiempoFin = 0d;
        double distanciaObstaculo = 0d;
        trigPin.high();
        try
        {
            Thread.sleep(0, 10000);
            System.out.println(".");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        trigPin.low();
        while (echoPin.isLow())
        {            
            tiempoInicio = System.nanoTime();
        }
        
        while (echoPin.isHigh())
        {         
            TiempoFin = System.nanoTime();
        }
        
        if (tiempoInicio > 0 && TiempoFin > 0)
        {
            double duracionPulso = (TiempoFin - tiempoInicio) / 1000d; // en MicroSegundos
            distanciaObstaculo = duracionPulso * 0.017;
        }
        return distanciaObstaculo;
    }
    
    public static void main(String[] args)
            throws InterruptedException
    {
        System.out.println("GPIO Control - Range Sensor HC-SR04.");
        System.out.println("Will stop is distance is smaller than " + MIN_DIST + " cm");
// create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput trigPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Trig", PinState.LOW);
        final GpioPinDigitalInput echoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, "Echo");
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                System.out.println("Oops!");
                gpio.shutdown();
                System.out.println("Exiting nicely.");
            }
        });
        System.out.println("Waiting for the sensor to be ready (2s)...");
        Thread.sleep(2000);
        boolean go = true;
        System.out.println("Looping until the distance is less than " + MIN_DIST + " cm");
        while (go)
        {
            double start = 0d, end = 0d;
            trigPin.high();
            try
            {
                Thread.sleep(0, 10000);
                System.out.println(".");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            trigPin.low();
            // Wait for the signal to return
            while (echoPin.isLow())
            {
                System.out.println("-");
                start = System.nanoTime();
            }
// There it is
            while (echoPin.isHigh())
            {
                System.out.println("+");
                end = System.nanoTime();
            }
            if (end > 0 && start > 0)
            {
                System.out.println("=");
                double pulseDuration = (end - start) / 1000000000d; // in seconds
                double distance = pulseDuration * DIST_FACT;
                if (distance < 1000) // Less than 10 meters
                {
                    System.out.println("Distance: " + DF22.format(distance) + " cm."); // + " (" + pulseDuration + " = " + end + " - " + start + ")");
                }
                if (distance > 0 && distance < MIN_DIST)
                {
                    go = false;
                }
                else
                {
                    if (distance < 0)
                    {
                        System.out.println("Dist:" + distance + ", start:" + start + ", end:" + end);
                    }
                    try
                    {
                        Thread.sleep(1000L);
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
            else
            {
                System.out.println("Hiccup!");
                try
                {
                    Thread.sleep(2000L);
                }
                catch (Exception ex)
                {
                }
            }
        }
        System.out.println("Done.");
        trigPin.low(); // Off
        gpio.shutdown();
    }
}
