package de.mz.jk.cacophony;
import java.io.File;
import java.io.IOException;

import de.mz.jk.jsix.libs.XFiles;
import de.mz.jk.jsix.utilities.Settings;

/** Cacophony, , Mar 9, 2018*/
/**
 * <h3>{@link SymphonyClient}</h3>
 * @author jkuharev
 * @version Mar 9, 2018 1:24:32 PM
 */
public class SymphonyClient
{
	public static File defaultSymphonySetupFolder = new File( "C:\\Program Files\\Waters\\Symphony" );
	private static String defaultMassLynxSymphonyClientExe = "MassLynxSymphonyClient.exe";

	private File symphonySetupFolder = defaultSymphonySetupFolder;
	private String massLynxSymphonyClientExe = "MassLynxSymphonyClient.exe";
	private String massLynxSymphonyClientCloneExe = "MassLynxSymphonyClient.external.exe";
	private String inputFilePath = "C:\\Users\\Administrator\\AppData\\Local\\Temp\\MLCurSmp.external.txt";

	/**
	 * create a symphony client by using default settings
	 */
	public SymphonyClient()
	{}

	/**
	 * create a symphony client and load settings from the given user configuration
	 * and  ensure a clone of MassLynxSymphonyClient.exe exists
	 */
	public SymphonyClient(Settings cfg, boolean initClone)
	{
		initConfig( cfg );
		if (initClone) cloneSymphonyClient();
	}

	/**
	 * load settings from the given user configuration
	 * @param cfg
	 */
	public void initConfig(Settings cfg)
	{
		String symphonyDir = cfg.getStringValue( "symphonySetupFolder", defaultSymphonySetupFolder.getAbsolutePath(), false );
		String inputFileName = cfg.getStringValue( "symphonyInputFile", "C:\\Users\\Administrator\\AppData\\Local\\Temp\\MLCurSmp.Cacophony.txt", false );
		String cloneExe = cfg.getStringValue( "symphonyClientCloneExe", "CacophonyClient.exe", false );

		setInputFilePath( inputFileName );
		setMassLynxSymphonyClientCloneExe( cloneExe );
	}

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
		File file = new File( inputFilePath ).getAbsoluteFile();
		return file;
	}

	public void setInputFilePath(String inputFileName)
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

	/**
	 * ensure a clone of MassLynxSymphonyClient.exe exists
	 */
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
			"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
			"<configuration>\n" +
			"  <configSections>\n" +
			"    <section name=\"log4net\" type=\"log4net.Config.Log4NetConfigurationSectionHandler, log4net\" />\n" +
			"  </configSections>\n" +
			"  <log4net>\n" +
			"    <appender name=\"RollingFile\" type=\"log4net.Appender.RollingFileAppender\">\n" +
			"      <file type=\"log4net.Util.PatternString\">\n" +
			"        <conversionPattern value=\"${ProgramData}/Waters/Symphony/Log/MassLynx %env{USERNAME}/MassLynxSymphonyClient.log\" />\n" +
			"      </file>\n" +
			"      <PreserveLogFileNameExtension value=\"true\" />\n" +
			"      <appendToFile value=\"true\" />\n" +
			"      <rollingStyle value=\"Size\" />\n" +
			"      <maxSizeRollBackups value=\"10\" />\n" +
			"      <maximumFileSize value=\"10MB\" />\n" +
			"      <layout type=\"log4net.Layout.PatternLayout\">\n" +
			"        <conversionPattern value=\"%date [%thread] %property{username} %-5level %logger - %message%newline\" />\n" +
			"      </layout>\n" +
			"    </appender>\n" +
			"    <root>\n" +
			"      <level value=\"DEBUG\" />\n" +
			"      <appender-ref ref=\"RollingFile\" />\n" +
			"    </root>\n" +
			"  </log4net>\n" +
			"  <appSettings>\n" +
				"    <add key=\"InputFile\" value=\"" + inputFileName + "\"/>\n" +
			"  </appSettings>\n" +
			"</configuration>";
	}

	private String getInputFileContent(File rawFile, File xmlFile)
	{
		return "[Sample]\n" +
				"Data File Name=" + rawFile.getAbsolutePath() + "\n" +
				"Process Parameters=" + xmlFile.getAbsolutePath() + "\n" +
				"Process Options=\n";
	}

	public void writeInputFile(File rawFile, File xmlFile) throws Exception
	{
		String content = getInputFileContent( rawFile, xmlFile );
		XFiles.writeFile( absInputFile(), content );
	}
}
