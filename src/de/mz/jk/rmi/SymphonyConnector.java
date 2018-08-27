/** Cacophony, de.mz.jk.cacophony.rmi, 27.08.2018*/
package de.mz.jk.cacophony.rmi;

import java.io.File;
import java.io.Serializable;
import java.rmi.Remote;

/**
 * <h3>{@link SymphonyConnector}</h3>
 * @author kuh1j
 * @version 27.08.2018 09:31:20
 */
public interface SymphonyConnector extends Remote, Serializable
{
	/** check if given file path is also valid for the remote partner */
	public boolean fileExists(File file) throws Exception;

	/** test raw file **/
	public boolean isValidRawFile(File file) throws Exception;

	/** test pipeline xml file */
	public boolean isValidPipelineXMLFile(File file) throws Exception;

	/** execute a single pipeline */
	public void executeSymphonyPipeline(File xml, File raw) throws Exception;
}
