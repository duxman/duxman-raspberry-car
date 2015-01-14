package duxman.lib.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CGestorHilos
{
	public Map<String, CSubProceso> hHilosGestionados;
	
	
	public CGestorHilos()
	{
		hHilosGestionados =  Collections.synchronizedMap(new HashMap<String, CSubProceso>());
	}

}
