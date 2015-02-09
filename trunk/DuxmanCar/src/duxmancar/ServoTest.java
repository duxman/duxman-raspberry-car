/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar;

import com.pi4j.io.i2c.I2CBus;
import duxmancar.Raspberry.Hardware.CGestorI2CAdafruit;
import duxmancar.log.CLog;
import java.util.Scanner;
import org.apache.log4j.Logger;

/**
 *
 * @author duxman
 */
public class ServoTest
{

    static enum ESTADO_SERVICIO
    {

        START, STOP
    };

    static CLog claselog;
    static ESTADO_SERVICIO m_estado;
    static Logger m_log;

    public static I2CBus bus;
    public static CGestorI2CAdafruit oControladorServos;

    public static void main(String[] args)
    {
        try
        {

            claselog = new CLog(false, "/var/log/DuxmanCar.log", 10000, 3);
            m_log = Logger.getRootLogger();

            m_log.info("Inicializamos Controlador");
            oControladorServos = new CGestorI2CAdafruit(0x40, 60);
            servotest();
        }
        catch (Exception ex)
        {
            m_log.error(ex);
        }
    }

    public static int Pregunta(String cadena)
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.println(cadena);
        String srtn = keyboard.nextLine();
        return Integer.valueOf(srtn).intValue();
    }

    public static String sPregunta(String cadena)
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.println(cadena);
        String srtn = keyboard.nextLine();
        return srtn;
    }

    public static void servotest()
    {
        int iServos = Pregunta("Numero Servos");
        int iServoInicial = Pregunta("ID Primer Servo");
        int valor = 0;

        ponValorServos(iServoInicial, iServos, 0);

        String comando = "";

        while (!comando.equals("X"))
        {

            comando = sPregunta("Dame Comando \n"
                    + "\tQ(100)\tW(10)\tE(1)\n"
                    + "\tA(-100)\tS(-10)\tD(-1)\n"
                    + "\tX(Salir)\tZ(STOP)").toUpperCase();

            switch (comando)
            {
                case "Q":
                {
                    valor = valor + 100;
                    if (valor > 4096)
                    {
                        valor = 4096;
                    }
                    break;
                }
                case "W":
                {
                    valor = valor + 10;
                    if (valor > 4096)
                    {
                        valor = 4096;
                    }
                    break;
                }
                case "E":
                {
                    valor = valor + 1;
                    if (valor > 4096)
                    {
                        valor = 4096;
                    }
                    break;
                }
                case "A":
                {
                    valor = valor - 100;
                    if (valor < 1)
                    {
                        valor = 1;
                    }
                    break;
                }
                case "S":
                {
                    valor = valor - 10;
                    if (valor < 1)
                    {
                        valor = 1;
                    }
                    break;
                }
                case "D":
                {
                    valor = valor - 1;
                    if (valor < 1)
                    {
                        valor = 1;
                    }
                    break;
                }
                case "Z":
                {
                    valor = 0;
                    break;
                }

                default:
                {
                    valor = Integer.valueOf(comando).intValue();
                }
            }
            m_log.info("Valos pasado [" + valor + "]");
            ponValorServos(iServoInicial, iServos, valor);

        }
    }

    public static void ponValorServos(int ini, int num, int valor)
    {
        for (int i = ini; i < (ini + num); i++)
        {
            oControladorServos.setPWM(i, 0, valor);
        }
    }
}
