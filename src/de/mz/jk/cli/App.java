/** PLGSAutoProcessorApplication, waters.plgs.bin, Jan 27, 2016*/
package de.mz.jk.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.mz.jk.jsix.libs.XJava;

/**
 * <h3>{@link App}</h3>
 * @author jkuharev
 * @version Jan 27, 2016 10:19:53 AM
 */
public class App
{
	private String executable = "";
	private List<AppParam> args = new ArrayList<AppParam>();

	private ProcessBuilder procBuilder = null;
	private Process proc = null;
	private int procExitCode = 1;
	private AppState procState = AppState.building;
	private String cmd = null;

	private Collection<StdOutErrListener> procOutputListeners = new HashSet<StdOutErrListener>();
	
	/** define path to the executable file */
	public App setExe(String executableFilePath)
	{
		this.executable = executableFilePath;
		return this;
	}

	/** add a command line parameter */
	public App addParam(AppParam p) { args.add( p ); return this;  }

	/** add a simple command line parameter */
	public App addParam(String name){	return addParam( new AppParam( name ) ); }

	/** add a key-value command line parameter */
	public App addParam(String name, Object value) { return addParam( new AppParam( name ).addValue( value ) ); }

	/** get all parameters */
	public List<AppParam> getParams(){ return args; }

	/** get the executed command as string **/
	public String getCommandLineString(){ return cmd; }
	
	/** get the process instance */
	public Process getProcess(){ return proc; }
	
	/** add a standard output listener */
	public App addStdOutErrListener(StdOutErrListener listener)
	{
		procOutputListeners.add( listener );
		return this;
	}

	/** prepare process builder */
	public void prepare()
	{
		// create command line parameter list
		List<String> params = new ArrayList<String>();
		params.add( executable );

		for ( AppParam a : args )
		{
			params.addAll( a.getValues() );
		}
		cmd = XJava.joinList( params, " " );

		// prepare process
		procBuilder = new ProcessBuilder( params );
		procState = AppState.initialized;
	}
	
	/**
	 * execute the command line process
	 */
	public void execute(boolean waitForEnd) throws Exception
	{
		try
		{
			if (procBuilder == null) prepare();
			XJava.joinList(procBuilder.command(), " ");
			proc = procBuilder.start();
			redirectStream( proc.getInputStream(), false );
			redirectStream( proc.getErrorStream(), true );
			procState = AppState.running;
		}
		catch (Exception e)
		{
			procState = AppState.failed;
			throw ( e );
		}
		if (waitForEnd) waitFor();
	}

	/**
	 * synchronously wait for process end
	 * @return exit value
	 */
	public int waitFor()
	{
		try
		{
			procExitCode = proc.waitFor();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		procState = ( procExitCode == 0 ) ? AppState.finished : AppState.failed;
		return procExitCode;
	}

	/** get the process exit code */
	public int getExitCode()
	{
		return procExitCode;
	}

	private void redirectStream(InputStream in, final boolean stdErr)
	{
		final BufferedReader stdInput = new BufferedReader( new InputStreamReader( in ) );
		new Thread()
		{
			@Override public void run()
			{
				String s = null;
				try { 
					while (( s = stdInput.readLine() ) != null)
					{
						for(StdOutErrListener listener : procOutputListeners)
							if(stdErr) 
								listener.processStdErrTextLine( s );
							else
								listener.processStdOutTextLine( s );
					}
				}
				catch (IOException e) { e.printStackTrace(); }
			}
		}.start();
	}
}

