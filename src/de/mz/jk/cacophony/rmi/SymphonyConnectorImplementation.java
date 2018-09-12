/** Cacophony, de.mz.jk.cacophony.rmi, 27.08.2018*/
package de.mz.jk.cacophony.rmi;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.mz.jk.cacophony.SymphonyClient;
import de.mz.jk.jsix.libs.XFiles;
import de.mz.jk.jsix.os.app.App;

/**
 * <h3>{@link SymphonyConnectorImplementation}</h3>
 * @author Dr. Joerg Kuharev
 * @version 27.08.2018 09:42:33
 */
public class SymphonyConnectorImplementation extends UnicastRemoteObject implements SymphonyConnector, Serializable
{
	private SymphonyClient symphonyClient = null;

	public SymphonyConnectorImplementation(SymphonyClient symphonyClient) throws RemoteException
	{
		super();
		this.symphonyClient = symphonyClient;
	}

	@Override public boolean fileExists(File file) throws Exception
	{
		boolean res = file.exists();
		System.out.println( "Testing file ... " + file.getAbsolutePath() + "..." + ( res ? "[found]" : "[not found]" ) );
		return res;
	}

	@Override public boolean isValidRawFile(File file) throws Exception
	{
		boolean res = false;
		if (file.exists() && file.isDirectory())
		{
			File externInf = new File( file, "_extern.inf" );
			File headerTxt = new File( file, "_HEADER.txt" );
			res = externInf.exists() && headerTxt.exists();
		}
		System.out.println( "Testing raw file ... " + file.getAbsolutePath() + "..." + ( res ? "[valid]" : "[invalid]" ) );
		return res;
	}

	@Override public boolean isValidPipelineXMLFile(File file) throws Exception
	{
		boolean res = false;
		try
		{
			res = ( file.exists() &&
					file.isFile() &&
					file.canRead() &&
					file.getName().toLowerCase().endsWith( ".xml" ) &&
					XFiles.readLines( file, 3 ).contains( "<TasksPipeline" ) );
		}
		catch (Exception e)
		{}
		System.out.println( "Testing XML file ... " + file.getAbsolutePath() + "..." + ( res ? "[valid]" : "[invalid]" ) );
		return res;
	}

	@Override public void executeSymphonyPipeline(File xml, File raw) throws Exception
	{
		System.out.println( "Executing pipeline ... " );
		System.out.println( "XML:	" + xml.getAbsolutePath() );
		System.out.println( "RAW:	" + raw.getAbsolutePath() );
		App app = new App();
		app.setExe( symphonyClient.absSymphonyFile( symphonyClient.getMassLynxSymphonyClientCloneExe() ).getAbsolutePath() );
		app.addParam( xml.getAbsolutePath() );
		symphonyClient.writeInputFile( raw, xml );
		app.execute( true );
		System.out.println( "done!" );
		System.out.println( "************************************************************" );
	}
}
