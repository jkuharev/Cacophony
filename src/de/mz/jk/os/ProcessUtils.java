/** PLGSAutoProcessor, de.mz.jk.os, Feb 16, 2016*/
package de.mz.jk.os;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.mz.jk.jsix.libs.XJava;

/**
 * <h3>{@link ProcessUtils}</h3>
 * @author jkuharev
 * @version Feb 16, 2016 1:02:52 PM
 */
public class ProcessUtils
{
	public static boolean DEBUG = false;
	private static String OperatingSystem = System.getProperty( "os.name" ).toLowerCase();

	/**
	 * list running processes on a Windows or Unix computer
	 * @return
	 * @throws Exception
	 */
	public static List<ProcessInfo> listProcesses() throws Exception
	{
			return OperatingSystem.toLowerCase().contains( "windows" ) ? listWindowsProcesses() : listUnixProcesses();
	}

	/**
	 * list running processes on a Windows computer
	 * @return
	 * @throws Exception
	 */
	public static List<ProcessInfo> listWindowsProcesses() throws Exception
	{
		List<ProcessInfo> listOfProcesses = new ArrayList<>();
		Process taskManProc = Runtime.getRuntime().exec( "tasklist.exe /fo csv" );
		BufferedReader input = new BufferedReader( new InputStreamReader( taskManProc.getInputStream() ) );
		for ( String line = null; ( line = input.readLine() ) != null; )
		{
			if( line.contains( "\"PID\"" ) )
			{ // skip title line
				// "Abbildname","PID","Sitzungsname","Sitz.-Nr.","Speichernutzung"
				// "Image Name","PID","Session Name","Session#","Mem Usage"
				// if (DEBUG) System.out.println( line );
			}
			else
			{ // parse individual processes
				// "csrss.exe","616","Services","0","5,500 K"
				String[] parts = line.replaceAll( "^\"|\"$", "" ).split( "\",\"", 5 );
				if(parts.length<5) throw new Exception("bad output format");
				ProcessInfo p = new ProcessInfo();
				p.cmd = XJava.stripQuotation(parts[0]);
				p.pid = Integer.parseInt( parts[1] );
				// p.cpu =
				p.mem = parseMemUsage( XJava.stripQuotation(parts[4]) );
				listOfProcesses.add( p );
			}
		}
		input.close();
		return listOfProcesses;
	}

	// parse a memory usage string to bytes
	private static float parseMemUsage(String mem) 
	{
		String[] parts = mem.split("\\s+", 2);
		float value = Float.parseFloat( parts[0].trim().replace(",", "") ); // stupid tousands separator
		char units = parts[1].trim().toLowerCase().charAt(0);
		switch(units) // it looks like it is always K
		{
			case 't': value *= 1024;
			case 'g': value *= 1024;
			case 'm': value *= 1024;
			case 'k': value *= 1024;
			default:
		}
		return value;
	}

	/**
	 * list running processes on a Unix/Linux/Mac OS X computer
	 * @return
	 * @throws Exception
	 */
	public static List<ProcessInfo> listUnixProcesses() throws Exception
	{
		List<ProcessInfo> listOfProcesses = new ArrayList<>();
		// ps -eo pid,%cpu,%mem,comm
		// pid: process id
		// %cpu: percentage of the cpu usage
		// %mem: percentage of the memory usage
		// comm: the command name without command line arguments
		// args: the command name including command line arguments
		Process taskManProc = Runtime.getRuntime().exec( "ps -eo pid,%cpu,%mem,comm" );
		BufferedReader input = new BufferedReader( new InputStreamReader( taskManProc.getInputStream() ) );
		for ( String line; ( line = input.readLine() ) != null; )
		{
			line = line.trim();
			if (line.startsWith( "PID" ))
			{ // skip title line
				/*  "PID %CPU %MEM COMM" */
				// if (DEBUG) System.out.println( line );
			}
			else
			{ // parse individual processes
				/*  48   0.0  0.1 /usr/libexec/kextd */
				String[] parts = line.trim().split( "\\s+", 4 );
				if(parts.length<4) break;
				ProcessInfo p = new ProcessInfo();
				p.pid = Integer.parseInt( parts[0].trim() );
				p.cpu = Float.parseFloat( parts[1].trim() );
				p.mem = Float.parseFloat( parts[2].trim() );
				p.cmd = parts[3].trim();
				listOfProcesses.add( p );
			}
		}
		input.close();
		return listOfProcesses;
	}

	/**
	 * find system processes with matching command names
	 * @param cmdPattern
	 * @param matchByRegExp true to treat cmdPattern as a regular expression or false as text
	 * @return
	 */
	public static List<ProcessInfo> findProcesses(String cmdPattern, boolean matchByRegExp)
	{
		List<ProcessInfo> res = new ArrayList<>();
		try
		{
			List<ProcessInfo> procs = listProcesses();
			for ( ProcessInfo pi : procs )
			{
				if( 
						(  matchByRegExp && pi.cmd.matches( cmdPattern ) ) || 
						( !matchByRegExp && pi.cmd.toLowerCase().contains( cmdPattern.toLowerCase() ) )
					)
					res.add( pi );
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
	}

	public static int getProcessId(Process process)
	{
		try
		{
			Class<?> cProcessImpl = process.getClass();
			Field fPid = cProcessImpl.getDeclaredField( "pid" );
			if (!fPid.isAccessible())
			{
				fPid.setAccessible( true );
			}
			return fPid.getInt( process );
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	public static void main(String[] args) throws Exception
	{
//		System.out.println( parseMemUsage("23,333 M") );

		
		
		DEBUG = true;
		List<ProcessInfo> procs = findProcesses("PING", false);
		for ( ProcessInfo pi : procs )
			System.out.println( pi );
	}
}
