/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package duxmancar.Datos;

import duxmancar.util.IDatosGenerales;
import duxmancar.util.IDatosGenerales.eAccionesServo;
import duxmancar.util.IDatosGenerales.eDestinos;

/**
 *
 * @author duxman
 */
public class CTelegramasAutomatico implements IDatosGenerales
{
    public static int iId = 0;
    
    public static String dameDato( eMovimientosAutonomos movi, int iVelocidad )
    {
        if( movi == eMovimientosAutonomos.ADELANTE )
        {
            return dameDatoMarchaAdelante(iVelocidad);
        }
        else if( movi == eMovimientosAutonomos.ATRAS )
        {
            return dameDatoMarchaAtras(iVelocidad);
        }
        else if( movi == eMovimientosAutonomos.DERECHA )
        {
            return dameDatoMarchaDerecha(iVelocidad);
        }
        else if( movi == eMovimientosAutonomos.IZQUIERDA )
        {
            return dameDatoMarchaIzquierda(iVelocidad);
        }
        else if( movi == eMovimientosAutonomos.STOP )
        {
            return dameDatoMarchaStop();
        }
        else if( movi == eMovimientosAutonomos.GIRO )
        {
            return dameDatoMarchaDerecha( (int) (iVelocidad * 1.25) );
        }       
        
        return dameDatoMarchaStop();
    }
    
    public static String dameDatoMarchaAdelante(int iVelocidad)
    {
        return CDato.CodificaMensaje(   iId++,
                                        eDestinos.RASPBERRY.ordinal(),
                                        eDestinos.DCCONTROL.ordinal(),
                                        eAccionesServo.ARRIBA.ordinal(),
                                        iVelocidad );
        
    }
    
    public static String dameDatoMarchaAtras(int iVelocidad)
    {
        return CDato.CodificaMensaje(   iId++,
                                        eDestinos.RASPBERRY.ordinal(),
                                        eDestinos.DCCONTROL.ordinal(),
                                        eAccionesServo.ABAJO.ordinal(),
                                        iVelocidad );
        
    }
    
    public static String dameDatoMarchaDerecha(int iVelocidad)
    {
        return CDato.CodificaMensaje(   iId++,
                                        eDestinos.RASPBERRY.ordinal(),
                                        eDestinos.DCCONTROL.ordinal(),
                                        eAccionesServo.RUEDAS.ordinal(),
                                        -1 * iVelocidad,iVelocidad );
        
    }
    
    public static String dameDatoMarchaIzquierda(int iVelocidad)
    {
        return CDato.CodificaMensaje(   iId++,
                                        eDestinos.RASPBERRY.ordinal(),
                                        eDestinos.DCCONTROL.ordinal(),
                                        eAccionesServo.RUEDAS.ordinal(),
                                        iVelocidad,-1 * iVelocidad );
        
    }
    
    public static String dameDatoMarchaStop()
    {
        return CDato.CodificaMensaje(   iId++,
                                        eDestinos.RASPBERRY.ordinal(),
                                        eDestinos.DCCONTROL.ordinal(),
                                        eAccionesServo.STOP.ordinal() );
        
    }
    
}
