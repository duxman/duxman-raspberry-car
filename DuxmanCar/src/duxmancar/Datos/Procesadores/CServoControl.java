/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Datos.Procesadores;

import duxmancar.Datos.CDato;
import duxmancar.CProperties;
import duxmancar.Raspberry.Hardware.CGestorI2CAdafruit;
import duxmancar.util.IDatosGenerales;

/**
 *
 * @author duxman
 */
public class CServoControl extends CProcesadorDatosProvider implements IDatosGenerales
{

    private final CGestorI2CAdafruit m_oControladorServos;

    public CServoControl(CGestorI2CAdafruit oControladorServos)
    {
        super();
        m_oControladorServos = oControladorServos;
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
            m_oControladorServos.setPWM(CProperties.POSICION_SERVO_DER, CProperties.POS_OFF_SERVO, CProperties.POS_OFF_SERVO);
            m_oControladorServos.setPWM(CProperties.POSICION_SERVO_IZQ, CProperties.POS_OFF_SERVO, CProperties.POS_OFF_SERVO);
        }
        else
        {
            String sVelocidad = dato.Parametros.get(0);
            int parametroVelocidad = Integer.valueOf(sVelocidad).intValue();

            if (Accion == eAccionesServo.ARRIBA.ordinal())
            {
                m_log.info("marcha adelante " + sVelocidad);
                m_oControladorServos.setPWM(CProperties.POSICION_SERVO_DER, CProperties.POS_OFF_SERVO, parametroVelocidad);
                m_oControladorServos.setPWM(CProperties.POSICION_SERVO_IZQ, CProperties.POS_OFF_SERVO, parametroVelocidad);
            }

            if (Accion == eAccionesServo.ABAJO.ordinal())
            {
                m_log.info("marcha atras " + sVelocidad);
                m_oControladorServos.setPWM(CProperties.POSICION_SERVO_DER, CProperties.POS_OFF_SERVO, parametroVelocidad);
                m_oControladorServos.setPWM(CProperties.POSICION_SERVO_IZQ, CProperties.POS_OFF_SERVO, parametroVelocidad);
            }

            if (Accion == eAccionesServo.DERECHA.ordinal())
            {
                m_log.info("giramos derecha " + sVelocidad);
                m_oControladorServos.setPWM(CProperties.POSICION_SERVO_DER, CProperties.POS_OFF_SERVO, CProperties.POS_OFF_SERVO);
                m_oControladorServos.setPWM(CProperties.POSICION_SERVO_IZQ, CProperties.POS_OFF_SERVO, parametroVelocidad);
            }
            if (Accion == eAccionesServo.IZQUIERDA.ordinal())
            {
                m_log.info("giramos izquierda " + sVelocidad);
                m_oControladorServos.setPWM(CProperties.POSICION_SERVO_DER, CProperties.POS_OFF_SERVO, parametroVelocidad);
                m_oControladorServos.setPWM(CProperties.POSICION_SERVO_IZQ, CProperties.POS_OFF_SERVO, CProperties.POS_OFF_SERVO);
            }
        }
    }

}
