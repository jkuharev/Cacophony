/** Cacophony, de.mz.jk.cacophony.rmi, 27.08.2018*/
package de.mz.jk.cacophony.rmi;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.mz.jk.cacophony.SymphonyClient;
import de.mz.jk.cli.App;
import de.mz.jk.jsix.libs.XFiles;

/**
 * <h3>{@link SymphonyConnectorImplementation}</h3>
 * @author kuh1j
 * @version 27.08.2018 09:42:33
 */
public class SymphonyConnectorImplementation extends UnicastRemoteObject implements SymphonyConnector
{
	private SymphonyClient symphonyClient = null;

	protected SymphonyConnectorImplementation(SymphonyClient symphonyClient) throws RemoteException
	{
		super();
		this.symphonyClient = symphonyClient;
	}

	@Override public boolean fileExists(File file) throws Exception
	{
		return file != null && file.exists();
	}

	@Override public boolean isValidRawFile(File file) throws Exception
	{
		if (file.exists() && file.isDirectory())
		{
			File externInf = new File( file, "_extern.inf" );
			File headerTxt = new File( file, "_HEADER.txt" );
			return externInf.exists() && headerTxt.exists();
		}
		return false;
	}

	@Override public boolean isValidPipelineXMLFile(File file) throws Exception
	{
		try
		{
			return ( file.exists() &&
					file.isFile() &&
					file.canRead() &&
					file.getName().toLowerCase().endsWith( ".xml" ) &&
					XFiles.readLines( file, 3 ).contains( "<TasksPipeline" ) );
		}
		catch (Exception e)
		{}
		return false;
	}

	@Override public void executeSymphonyPipeline(File xml, File raw) throws Exception
	{
		App app = new App();
		app.setExe( symphonyClient.absSymphonyFile( symphonyClient.getMassLynxSymphonyClientCloneExe() ).getAbsolutePath() );
		app.addParam( xml.getAbsolutePath() );
		symphonyClient.writeInputFile( raw, xml );
		app.execute( true );
	}
}
