/** Cacophony, de.mz.jk.cacophony.rmi, Sep 7, 2018*/
package de.mz.jk.cacophony.rmi;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.mz.jk.jsix.utilities.Settings;

/**
 * <h3>{@link CacophonyMainApplication}</h3>
 * @author jkuharev
 * @version Sep 7, 2018 2:01:23 PM
 */
public class CacophonyMainApplication implements ActionListener
{
	private SymphonyConnector symphonyConnector = null;
	private Set<File> rawFiles = new TreeSet<>();
	private File xmlFile = null;

	private JButton btnReset = new JButton( "RESET" );
	private JButton btnPreview = new JButton( "PREVIEW" );
	private JButton btnRun = new JButton( "RUN" );
	private JTextField fieldXmlFileView = new JTextField( "" );
	private Settings appSettings = null;

	public CacophonyMainApplication(Settings config)
	{
		this.appSettings = config;
		initConfig();
	}

	private void initConfig()
	{
		String recentXML = appSettings.getStringValue( "recentSymphonyPipelineXmlFile", "", false );
		File recentXMLFile = new File(recentXML);
		if(recentXMLFile.exists()) 
		{
			System.out.println( "Setting XML pipeline file from configuration file ..." );
			System.out.println( "Note: execution of pipelines will fail, if this file is not available on the server!" );
			xmlFile = recentXMLFile;
		}
	}

	public void setSymphonyConnector(SymphonyConnector symphonyConnector)
	{
		this.symphonyConnector = symphonyConnector;
	}

	public SymphonyConnector getSymphonyConnector()
	{
		return symphonyConnector;
	}

	public void addFile(File file)
	{
		System.out.println( file.getAbsolutePath() );
		try
		{
			if (!symphonyConnector.fileExists( file ))
			{
				System.out.println( "bad for you, file is not accessible on the remote machine!" );
			}
			else
			if (symphonyConnector.isValidPipelineXMLFile( file ))
			{
				System.out.println( "good news, valid XML pipeline." );
				xmlFile = file;
				appSettings.setValue( "recentSymphonyPipelineXmlFile", file.getAbsolutePath() );

			}
			else if (symphonyConnector.isValidRawFile( file ))
			{
				System.out.println( "good news, valid RAW file." );
				rawFiles.add( file );
			}
			else
			{
				System.out.println( "something is wrong with this file? ... ignoring" );
			}
		}
		catch (Exception e)
		{
			System.out.println( "failed to test file type and validity ..." );
			e.printStackTrace();
		}
	}

	public void addToWindow(Window win)
	{
		win.add( getToolBar( new JButton[] { btnReset, btnPreview, btnRun } ), BorderLayout.SOUTH );
		win.setVisible( true );
	}

	public Component getUI()
	{
		JPanel toolBar = getToolBar( new JButton[] { btnReset, btnPreview, btnRun } );
		return toolBar;
	}

	private JPanel getToolBar(JButton[] btns)
	{
		// text field for displaying xml file path
		fieldXmlFileView.setBackground( Color.DARK_GRAY );
		fieldXmlFileView.setForeground( Color.GREEN );
		fieldXmlFileView.setFont( new Font( "monospaced", Font.PLAIN, 11 ) );
		fieldXmlFileView.setEditable( false );
		fieldXmlFileView.setEnabled( true );
		JPanel toolBar = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		for ( JButton btn : btns )
		{
			btn.setActionCommand( btn.getText() );
			btn.addActionListener( this );
			toolBar.add( btn );
		}
		// tool bar
		JPanel toolBarPanel = new JPanel( new BorderLayout() );
		toolBarPanel.add( fieldXmlFileView, BorderLayout.NORTH );
		toolBarPanel.add( toolBar, BorderLayout.SOUTH );
		return toolBarPanel;
	}

	@Override public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		switch (cmd)
		{
			case "RESET":
				resetQueue();
				break;
			case "PREVIEW":
				displayQueue();
				break;
			case "RUN":
				runPipelines();
				break;
			default:
				System.out.println( "unknown command: " + cmd );
				break;
		}
	}

	private void resetQueue()
	{
		System.out.println( "... resetting the queue of raw files ..." );
		rawFiles = new TreeSet<>();
		displayQueue();
	}

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
			System.out.println( "please drag and drop any valid raw files and try again." );
			return;
		}
		Thread task = new Thread()
		{
			public void run()
			{
				runQueue( rawFiles, xmlFile );
			}
		};
		task.start();
		System.out.println( "finished queue execution" );
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
				symphonyConnector.executeSymphonyPipeline( xml, f );
				System.out.println( "[ok]" );
			}
			catch (Exception e)
			{
				System.out.println( "[error]" );
			}
		}
		System.out.println( "Do not forget to clear the raw file queue!" );
	}
}
