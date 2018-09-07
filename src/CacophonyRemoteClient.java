import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.*;

import de.mz.jk.cacophony.rmi.CacophonyMainApplication;
import de.mz.jk.cacophony.rmi.SymphonyConnector;
import de.mz.jk.jsix.libs.XJava;
import de.mz.jk.jsix.ui.JTextAreaOutputStream;
import de.mz.jk.jsix.utilities.Settings;

/**
 * <h3>{@link CacophonyRemoteClient}</h3>
 * @author Dr. Joerg Kuharev
 * @version 07.09.2018
 */
public class CacophonyRemoteClient extends JFrame implements WindowListener, ActionListener
{
	// -----------------------------------------------------------------------------
	public static void main(String[] args) throws Exception
	{
		CacophonyRemoteClient gui = new CacophonyRemoteClient();
	}
	// -----------------------------------------------------------------------------

	private Settings cfg = new Settings( "cacophony.ini", XJava.dateStamp() + " - Settings for Cacophony by J.K." );
	private SymphonyConnector symphonyConnector = null;

	private JTextArea textArea = new JTextArea();
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
		setLayout(new BorderLayout());
		setTitle( "Cacophony-RMI-Client" );
		
		initTextArea();
		initToolBar();
		
		setSize(600, 400);
		setVisible(true);
		addWindowListener( this );
		initConfig();

		theApp = new CacophonyMainApplication( cfg );
		theApp.addToWindow( this );
	}

	private void initConfig()
	{
		serviceTcpPort = cfg.getIntValue( "service.tcp.port", defaultServiceTcpPort, false );
		serviceName = cfg.getStringValue( "service.name", defaultServiceName, false );
		serviceIpAddress = cfg.getStringValue( "service.host.ip.address", "localhost", false );
	}

	// -----------------------------------------------------------------------------
	private void initTextArea()
	{
		textArea.setEditable( false );
		textArea.setBackground( Color.DARK_GRAY );
		textArea.setForeground( Color.GREEN );
		new JTextAreaOutputStream( textArea, true, true );
		add( new JScrollPane( textArea ), BorderLayout.CENTER );
	}

	// -----------------------------------------------------------------------------
	private void initToolBar()
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
		add( toolBar, BorderLayout.NORTH );
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
		textArea.setText( "" );
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
}
