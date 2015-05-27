/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Datos.Procesadores;

import com.pi4j.io.gpio.GpioController;
import duxmancar.CProperties;
import duxmancar.Datos.CDato;
import duxmancar.Raspberry.Hardware.ControlMotores.CMotorControlPuenteH;
import static duxmancar.Raspberry.Hardware.ControlMotores.CMotorControlPuenteH.VELOCIDAD_GIRO_3;
import duxmancar.util.IDatosGenerales;

/**
 *
 * @author duxman
 */
public class CPuenteHControl  extends CProcesadorDatosProvider implements IDatosGenerales
{
    private final CMotorControlPuenteH m_gestorMotoresDc;
    private GpioController m_gpio;
    
    public CPuenteHControl(  GpioController gpio)
    {
        super();
        m_gestorMotoresDc = new CMotorControlPuenteH();
        m_gestorMotoresDc.inicializa(gpio, true);
        m_gpio = gpio;
        
    }
    
    @Override public void run()
    {
        while (true)
        {
            try
            {
                CDato dato = getDato();
                ProcesaDato(dato);
            }
            catch (Exception ex)
            {
                m_log.error(ex);
            }
        }
    }
    
    public void ProcesaDato(CDato dato)
    {
        int Accion = dato.getAccion();

        if (Accion == eAccionesServo.STOP.ordinal())
        {
            m_log.info("paramos totalmente marcha");
            m_gestorMotoresDc.paroMotor();
        }
        else
        {
            String sVelocidad = dato.m_lParametros.get(0);
            int parametroVelocidad = Integer.valueOf(sVelocidad).intValue();

            if (Accion == eAccionesServo.ARRIBA.ordinal())
            {
                m_log.info("marcha adelante " + sVelocidad);
                m_gestorMotoresDc.marchaMotor(true, parametroVelocidad );
            }

            if (Accion == eAccionesServo.ABAJO.ordinal())
            {
                m_log.info("marcha atras " + sVelocidad);
                m_gestorMotoresDc.marchaMotor(false, parametroVelocidad );
            }

            if (Accion == eAccionesServo.DERECHA.ordinal())
            {
                m_log.info("giramos derecha " + sVelocidad);
                m_gestorMotoresDc.girarDerecha(VELOCIDAD_GIRO_3);
            }
            if (Accion == eAccionesServo.IZQUIERDA.ordinal())
            {
                m_log.info("giramos izquierda " + sVelocidad);
                m_gestorMotoresDc.girarIzquierda(VELOCIDAD_GIRO_3);
            }
            if (Accion == eAccionesServo.RUEDAS.ordinal())
            {
                String sVelocidadIzq = dato.m_lParametros.get(1);
                int parametroVelocidadIzq = Integer.valueOf(sVelocidadIzq).intValue();
                m_log.info("giramos Derecha " + sVelocidad);
                m_log.info("giramos Izquierda " + sVelocidadIzq);                                
                m_gestorMotoresDc.girarRuedas(parametroVelocidad,parametroVelocidadIzq);
            }
        }
    }

}
