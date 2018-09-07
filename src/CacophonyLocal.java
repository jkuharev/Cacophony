import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.mz.jk.cacophony.SymphonyClient;
import de.mz.jk.jsix.libs.XFiles;
import de.mz.jk.jsix.libs.XJava;
import de.mz.jk.jsix.os.app.App;
import de.mz.jk.jsix.ui.TextWindowDragAndDropUI;
import de.mz.jk.jsix.ui.TextWindowDragAndDropUI.FileActionListener;
import de.mz.jk.jsix.utilities.Settings;

/** Cacophony, , Mar 8, 2018*/
/**
 * <h3>{@link Cacophony}</h3>
 * @author jkuharev
 * @version Mar 8, 2018 12:57:57 PM
 */
public class CacophonyLocal implements FileActionListener, ActionListener
{
	private SymphonyClient symphonyClient = new SymphonyClient();

	public static void main(String[] args)
	{
		CacophonyLocal c = new CacophonyLocal();
	}

	static String welcomeMessage = 
			"This app allows to externally run Symphony pipelines.\n" +
			"Drag and drop RAW folders and a pipeline xml file over here,\n" +
			"then press 'RUN' to trigger Symphony pipelines";

	private Settings cfg = new Settings( "Cacophony.ini", XJava.dateStamp() + " - Settings for Cacophony by J.K." );
	private TextWindowDragAndDropUI ui = new TextWindowDragAndDropUI( "Cacophony.", 640, 480, welcomeMessage );

	public Set<File> rawFiles = new TreeSet<>();
	public File xmlFile = null;

	private JButton btnReset = new JButton( "CLEAR QUEUE" );
	private JButton btnRun = new JButton( "RUN" );
	private JButton btnList = new JButton( "SHOW QUEUE" );
	private JTextField fieldXmlFileView = new JTextField( "" );


	public CacophonyLocal()
	{
		initConfig();
		symphonyClient.cloneSymphonyClient();
		initGUI();
		resetFiles();
	}

	/**
	 * 
	 */
	private void initGUI()
	{
		ui.addFileActionListener( this );
		
		// buttons
		JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		btnPanel.add( btnReset );
		btnPanel.add( btnList );
		btnPanel.add( btnRun );
		btnReset.setActionCommand( "reset" );
		btnRun.setActionCommand( "run" );
		btnList.setActionCommand( "list" );
		btnReset.addActionListener( this );
		btnList.addActionListener( this );
		btnRun.addActionListener( this );

		// text field for displaying xml file path
		fieldXmlFileView.setBackground( Color.DARK_GRAY );
		fieldXmlFileView.setForeground( Color.GREEN );
		fieldXmlFileView.setFont( new Font( "monospaced", Font.PLAIN, 11 ) );
		fieldXmlFileView.setEditable( false );
		fieldXmlFileView.setEnabled( true );
		
		// tool bar
		JPanel toolBarPanel = new JPanel( new BorderLayout() );
		toolBarPanel.add( fieldXmlFileView, BorderLayout.NORTH );
		toolBarPanel.add( btnPanel, BorderLayout.SOUTH);
		
		// add to window
		ui.getWin().add( toolBarPanel, BorderLayout.SOUTH );
		ui.getWin().setVisible( true ); // update GUI
	}

	private void initConfig()
	{
		String recentXML = cfg.getStringValue( "recentSymphonyPipelineXmlFile", "", false );
		String symphonyDir = cfg.getStringValue( "symphonySetupFolder", symphonyClient.defaultSymphonySetupFolder.getAbsolutePath(), false );
		String inputFileName = cfg.getStringValue( "symphonyInputFile", "C:\\Users\\Administrator\\AppData\\Local\\Temp\\MLCurSmp.Cacophony.txt", false );
		String cloneExe = cfg.getStringValue( "symphonyClientCloneExe", "CacophonyClient.exe", false );
		
		symphonyClient.setInputFilePath( inputFileName );
		symphonyClient.setMassLynxSymphonyClientCloneExe( cloneExe );
		
		new File( symphonyDir );

		File recentXmlFile = new File( recentXML );
		if (isValidPipelineXMLFile( recentXmlFile ))
		{
			setPielineXMLFile( recentXmlFile );
		}
	}

	@Override public List<File> filterTargetFiles(List<File> files)
	{
		for ( File f : files )
		{
			if (isValidRawFile( f ))
			{
				System.out.println( "adding raw file: " + f.getName() );
				rawFiles.add( f );
			}
			else if (isValidPipelineXMLFile( f ))
			{
				setPielineXMLFile( f );
			}
		}
		return files;
	}

	private void setPielineXMLFile(File file)
	{
		System.out.println( "setting pipeline xml file: " + file.getAbsolutePath() );
		xmlFile = file;
		fieldXmlFileView.setText( xmlFile.getAbsolutePath() );
		cfg.setValue( "recentSymphonyPipelineXmlFile", xmlFile.getAbsolutePath() );
	}

	private boolean isValidRawFile(File file)
	{
		if (file.exists() && file.isDirectory())
		{
			File externInf = new File( file, "_extern.inf" );
			File headerTxt = new File( file, "_HEADER.txt" );
			return externInf.exists() && headerTxt.exists();
		}
		return false;
	}

	private boolean isValidPipelineXMLFile(File file)
	{
		try{
			return ( 
					file.exists() && 
					file.isFile() && 
					file.canRead() && 
					file.getName().toLowerCase().endsWith( ".xml" ) &&
					XFiles.readLines( file, 3 ).contains( "<TasksPipeline" ) 
			);
		}
		catch (Exception e)
		{}
		return false;
	}

	@Override public void doMultiFileAction(List<File> files)	{}
	@Override public void doSingleFileAction(File file){}

	@Override public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		switch (cmd)
		{
			case "list":
				displayQueue();
				break;
			case "run":
				System.out.println( "running pipelines ... " );
				runPipelines();
				break;
			case "reset":
				System.out.println( "clearing files ..." );
				resetFiles();
				break;
			default:
				System.out.println( "unknown command " + cmd );
		}
	}

	/**
	 * 
	 */
	private void displayQueue()
	{
		System.out.println( "queued raw files:" );
		int i = 1;
		for ( File f : rawFiles )
		{
			System.out.println( "\t" + i + ": " + f.getName() );
			i++;
		}
		System.out.println( "pipeline xml file: " );
		System.out.println( "	" + xmlFile.getName() );
	}

	/**
	 * 
	 */
	private void runPipelines()
	{
		if (xmlFile == null)
		{
			System.out.println( "please drag and drop a valid pipeline xml file and try again." );
			return;
		}
		
		if (rawFiles.size() < 1)
		{
			System.out.println( "please drag and drop valid raw files and try again." );
			return;
		}

		Thread task = new Thread() {
			public void run()
			{
				runQueue(rawFiles, xmlFile);
			}
		};
		task.start();
		System.out.println("finished queue execution");
	}

	private void runQueue(final Set<File> raws, final File xml) 
	{
		System.out.println( "running pipeline '" + xml.getName() + "' for files:" );
		int i = 1;
		for ( File f : raws )
		{
			System.out.print( "\t" + i + ": " + f.getName() + " ... " );
			try
			{
				executeSymphonyPipeline( xml, f );
				System.out.println( "[ok]" );
			}
			catch (Exception e)
			{
				System.out.println( "[error]" );
			}
		}
		System.out.println( "Do not forget to clear the raw file queue!" );	
	}

	/**
	 * 
	 * @param xml
	 * @param raw
	 */
	private synchronized void executeSymphonyPipeline(File xml, File raw) throws Exception
	{
		App app = new App();
		app.setExe( symphonyClient.absSymphonyFile( symphonyClient.getMassLynxSymphonyClientCloneExe() ).getAbsolutePath() );
		app.addParam( xmlFile.getAbsolutePath() );
		symphonyClient.writeInputFile( raw, xml );
		app.execute( true );
	}

	public void resetFiles()
	{
		rawFiles = new TreeSet<>();
	}
}
