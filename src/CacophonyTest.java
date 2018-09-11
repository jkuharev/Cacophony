import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import de.mz.jk.cacophony.rmi.SymphonyConnector;

/** Cacophony, , Sep 10, 2018*/
/**
 * <h3>{@link CacophonyTest}</h3>
 * @author jkuharev
 * @version Sep 10, 2018 2:30:59 PM
 */
public class CacophonyTest
{
	public static void main(String[] args) throws Exception
	{
		Registry reg = LocateRegistry.getRegistry( "192.168.1.156", 1099 );
		SymphonyConnector con = (SymphonyConnector)reg.lookup( "cacophony" );
		System.out.println( "successfully connected." );
		boolean there = con.fileExists( new File(
				"C:\\Symphony\\Pipelines\\IDEFIX-TESLA-T-ECOLI.xml" ) );
		// boolean there = con.fileExists( new File(
		// "/Volumes/DAT/Users/jkuharev/Desktop/WAIT.xml" ) );
		System.out.println( "file there: " + there );
	}
}
