import java.io.File;
import java.io.IOException;

import de.mz.jk.jsix.libs.XFiles;

/** Cacophony, , Mar 9, 2018*/
/**
 * <h3>{@link SymphonyClient}</h3>
 * @author jkuharev
 * @version Mar 9, 2018 1:24:32 PM
 */
public class SymphonyClient
{
	public static final String eol = System.getProperty("line.separator");
	
	public static File defaultSymphonySetupFolder = new File( "C:\\Program Files\\Waters\\Symphony" );
	private static String defaultMassLynxSymphonyClientExe = "MassLynxSymphonyClient.exe";

	private File symphonySetupFolder = defaultSymphonySetupFolder;
	private String massLynxSymphonyClientExe = "MassLynxSymphonyClient.exe";
	private String massLynxSymphonyClientCloneExe = "MassLynxSymphonyClient.external.exe";
	private String inputFilePath = "C:\\Users\\Administrator\\AppData\\Local\\Temp\\MLCurSmp.external.txt";

	public File getSymphonySetupFolder()
	{
		return symphonySetupFolder;
	}

	public void setSymphonySetupFolder(File symphonySetupFolder)
	{
		this.symphonySetupFolder = symphonySetupFolder;
	}

	public String getMassLynxSymphonyClientExe()
	{
		return massLynxSymphonyClientExe;
	}

	public void setMassLynxSymphonyClientExe(String massLynxSymphonyClientExe)
	{
		this.massLynxSymphonyClientExe = massLynxSymphonyClientExe;
	}

	public String getInputFilePath()
	{
		return inputFilePath;
	}

	public File absInputFile()
	{
		// etracting temporary path from the envornment did not properly work on the targen machine
		// String tempPath = XOS.getEnvironmentVariable( "temp", false );
		File file = new File( inputFilePath ).getAbsoluteFile();
		return file;
	}

	public void setInputFilePaath(String inputFileName)
	{
		this.inputFilePath = inputFileName;
	}

	public String getMassLynxSymphonyClientCloneExe()
	{
		return massLynxSymphonyClientCloneExe;
	}

	public void setMassLynxSymphonyClientCloneExe(String massLynxSymphonyClientCloneExe)
	{
		this.massLynxSymphonyClientCloneExe = massLynxSymphonyClientCloneExe;
	}

	public File absSymphonyFile(String relativeFilePath)
	{
		return new File( symphonySetupFolder, relativeFilePath );
	}

	public void cloneSymphonyClient()
	{
		File originalExe = absSymphonyFile( massLynxSymphonyClientExe );
		File cloneExe = absSymphonyFile( massLynxSymphonyClientCloneExe );
		if(!cloneExe.exists())
		{
			try
			{
				System.out.println( "cloning MassLynx Symphony Client ... " );
				System.out.println( "	source:	" + originalExe );
				System.out.println( "	target:	" + cloneExe );
				XFiles.copyFile( originalExe, cloneExe );
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		File cloneConfig = absSymphonyFile( massLynxSymphonyClientCloneExe + ".config" );
		if (!cloneConfig.exists())
		{

			try
			{
				System.out.println( "configuring clone client to use '" + inputFilePath + "' as inpput file ... " );
				String content = getConfigFileContent( inputFilePath );
				System.out.println( "wrinting config file ..." );
				System.out.println( "	" + cloneConfig );
				XFiles.writeFile( cloneConfig, content );
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private String getConfigFileContent(String inputFileName)
	{
		return 
			"<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + eol +
			"<configuration>" + eol +
			"  <configSections>" + eol +
			"    <section name=\"log4net\" type=\"log4net.Config.Log4NetConfigurationSectionHandler, log4net\" />" + eol +
			"  </configSections>" + eol +
			"  <log4net>" + eol +
			"    <appender name=\"RollingFile\" type=\"log4net.Appender.RollingFileAppender\">" + eol +
			"      <file type=\"log4net.Util.PatternString\">" + eol +
			"        <conversionPattern value=\"${ProgramData}/Waters/Symphony/Log/MassLynx %env{USERNAME}/MassLynxSymphonyClient.log\" />" + eol +
			"      </file>" + eol +
			"      <PreserveLogFileNameExtension value=\"true\" />" + eol +
			"      <appendToFile value=\"true\" />" + eol +
			"      <rollingStyle value=\"Size\" />" + eol +
			"      <maxSizeRollBackups value=\"10\" />" + eol +
			"      <maximumFileSize value=\"10MB\" />" + eol +
			"      <layout type=\"log4net.Layout.PatternLayout\">" + eol +
			"        <conversionPattern value=\"%date [%thread] %property{username} %-5level %logger - %message%newline\" />" + eol +
			"      </layout>" + eol +
			"    </appender>" + eol +
			"    <root>" + eol +
			"      <level value=\"DEBUG\" />" + eol +
			"      <appender-ref ref=\"RollingFile\" />" + eol +
			"    </root>" + eol +
			"  </log4net>" + eol +
			"  <appSettings>" + eol +
				"    <add key=\"InputFile\" value=\"" + inputFileName + "\"/>" + eol +
			"  </appSettings>" + eol +
			"</configuration>";
	}

	private String getInputFileContent(File rawFile, File xmlFile)
	{
		return "[Sample]" + eol +
				"Data File Name=" + rawFile.getAbsolutePath() + eol +
				"Process Parameters=" + xmlFile.getAbsolutePath() + eol +
				"Process Options=" + eol;
	}

	public void writeInputFile(File rawFile, File xmlFile) throws Exception
	{
		String content = getInputFileContent( rawFile, xmlFile );
		XFiles.writeFile( absInputFile(), content );
	}
}
