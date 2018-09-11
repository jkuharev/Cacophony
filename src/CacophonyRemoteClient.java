import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import de.mz.jk.cacophony.rmi.CacophonyMainApplication;
import de.mz.jk.cacophony.rmi.SymphonyConnector;
import de.mz.jk.jsix.libs.XJava;
import de.mz.jk.jsix.ui.TextWindowDragAndDropUI;
import de.mz.jk.jsix.ui.TextWindowDragAndDropUI.FileActionListener;
import de.mz.jk.jsix.utilities.Settings;

/**
 * <h3>{@link CacophonyRemoteClient}</h3>
 * @author Dr. Joerg Kuharev
 * @version 07.09.2018
 */
public class CacophonyRemoteClient implements WindowListener, ActionListener, FileActionListener
{
	// -----------------------------------------------------------------------------
	public static void main(String[] args) throws Exception
	{
		CacophonyRemoteClient gui = new CacophonyRemoteClient();
	}
	// -----------------------------------------------------------------------------

	private TextWindowDragAndDropUI ui = null;
	private Settings cfg = new Settings( "cacophony.ini", XJava.dateStamp() + " - Settings for Cacophony by J.K." );
	private SymphonyConnector symphonyConnector = null;

// private JTextArea textArea = new JTextArea();
	private JToolBar toolBar = new JToolBar();
	private JButton btnGo = new JButton( "[ > ]" );
	private JButton btnStop = new JButton( "[ x ]" );
	private JButton btnInfo = new JButton( "[ i ]" );
	private JButton btnClear = new JButton( "[ - ]" );
	private JLabel statusBar = new JLabel( "" );
	// -----------------------------------------------------------------------------
	public static final int defaultServiceTcpPort = 1099;
	private int serviceTcpPort = defaultServiceTcpPort;
	public static final String defaultServiceName = "cacophony";
	private String serviceName = defaultServiceName;
	// -----------------------------------------------------------------------------

	// -----------------------------------------------------------------------------
	public static final String defaultServiceIpAdress = "127.0.0.1";
	private String serviceIpAddress = defaultServiceName;
	private Registry reg = null;
	private boolean rmiStarted;
	// -----------------------------------------------------------------------------
	private CacophonyMainApplication theApp = null;

	// -----------------------------------------------------------------------------
	public CacophonyRemoteClient() throws Exception
	{
		initApp();
	}

	// -----------------------------------------------------------------------------
	public void initApp()
	{
		ui = new TextWindowDragAndDropUI( "Cacophony-RMI-Client", 600, 400, "" );
		ui.addFileActionListener( this );

		JFrame win = ui.getWin();
		win.addWindowListener( this );

		initConfig();
		theApp = new CacophonyMainApplication( cfg );

		win.add( getToolBar(), BorderLayout.NORTH );
		win.add( theApp.getUI(), BorderLayout.SOUTH );

		win.setVisible( true );
	}

	private void initConfig()
	{
		serviceTcpPort = cfg.getIntValue( "service.tcp.port", defaultServiceTcpPort, false );
		serviceName = cfg.getStringValue( "service.name", defaultServiceName, false );
		serviceIpAddress = cfg.getStringValue( "service.host.ip.address", "localhost", false );
	}
	// -----------------------------------------------------------------------------

	// -----------------------------------------------------------------------------
	private JToolBar getToolBar()
	{
		btnGo.setToolTipText( "connect to server" );
		btnStop.setToolTipText( "disconnect from server" );
		btnInfo.setToolTipText( "show connection info" );
		toolBar.add( btnGo );
		toolBar.add( btnStop );
		toolBar.add( btnInfo );
		toolBar.addSeparator();
		toolBar.add( btnClear );
		btnGo.setEnabled( true );
		btnStop.setEnabled( true );
		btnInfo.setEnabled( true );
		btnGo.addActionListener( this );
		btnStop.addActionListener( this );
		btnInfo.addActionListener( this );
		btnClear.addActionListener( this );
		return toolBar;
	}
	
	private void setButtonStates(boolean run, boolean stop)
	{
		btnGo.setEnabled( run );
		btnStop.setEnabled( stop );
	}

	// -----------------------------------------------------------------------------
	@Override public void windowActivated(WindowEvent e)	{}
	@Override public void windowClosed(WindowEvent e)	{}
	@Override public void windowDeactivated(WindowEvent e)	{}
	@Override public void windowDeiconified(WindowEvent e)	{}
	@Override public void windowIconified(WindowEvent e)	{}
	@Override public void windowOpened(WindowEvent e)	{}
	
	@Override public void windowClosing(WindowEvent e)
	{
		try
		{
			java.rmi.Naming.unbind( serviceName );
		}
		catch (Exception ex)
		{}
		System.exit( 0 );
	}

	// -----------------------------------------------------------------------------
	@Override public void actionPerformed(ActionEvent evt)
	{
		Object src = evt.getSource();

		if (src.equals( btnGo ))
		{
			runService();
		}
		else if (src.equals( btnStop ))
		{
			stopService();
		}
		else if (src.equals( btnInfo ))
		{
			showInfo();
		}
		else if (src.equals( btnClear ))
		{
			clearScreen();
		}
	}

	/**
	 * 
	 */
	private void clearScreen()
	{
		ui.getOutputTextArea().setText( "" );
	}

	/**
	 * 
	 */
	private void showInfo()
	{
		System.out.println( "--------------------------------------------------------------------------------" );
		System.out.println( "RMI service information:" );
		System.out.println( "\thost:\t" + serviceIpAddress );
		System.out.println( "\tport:\t" + serviceTcpPort );
		System.out.println( "\tname:\t" + serviceName );
		System.out.println( "\tstate:\t" + ( ( rmiStarted ) ? "online" : "offline" ) );
		System.out.println( "--------------------------------------------------------------------------------" );
	}

	/**
	 * 
	 */
	private void stopService()
	{
		try
		{
			java.rmi.Naming.unbind( serviceName );
			symphonyConnector = null;
			theApp.setSymphonyConnector( symphonyConnector );
		}
		catch (Exception e)
		{}
		setButtonStates( true, true );
		rmiStarted = false;
	}

	/**
	 * 
	 */
	private void runService()
	{
		rmiStarted = false;
		try
		{
			System.out.println( "trying to connect to remote service '" + getServiceUrl() + "' ... " );
			reg = LocateRegistry.getRegistry( serviceIpAddress, serviceTcpPort );
			symphonyConnector = (SymphonyConnector)reg.lookup( serviceName );
			System.out.println( "successfully connected." );
			setButtonStates( false, true );
			rmiStarted = true;
			theApp.setSymphonyConnector( symphonyConnector );
		}
		catch (Exception e)
		{
			System.out.println( "failed to connect to the service '" + getServiceUrl() + "'!" );
			e.printStackTrace();
		}
	}

	public String getServiceUrl()
	{
		return "rmi://" + serviceIpAddress + ":" + serviceTcpPort + "/" + serviceName;
	}

	@Override public List<File> filterTargetFiles(List<File> files)
	{
		if (rmiStarted && theApp != null)
		{
			return files;
		}
		// 
		System.out.println( "can not process dropped files... ");
		System.out.println( "please establish a connection to the server and try again." );
		return null;
	}

	@Override public void doMultiFileAction(List<File> files)
	{}

	@Override public void doSingleFileAction(File file)
	{
		theApp.addFile( file );
	}
}
