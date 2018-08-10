/** PLGSAutoProcessorApplication, de.mz.jk.cli, Jan 27, 2016*/
package de.mz.jk.cli;

/**
 * <h3>{@link StdOutErrListener}</h3>
 * @author jkuharev
 * @version Jan 27, 2016 3:17:37 PM
 */
public interface StdOutErrListener
{
	/** redirect to System.out and System.err */
	public static StdOutErrListener redirectToTerminal = new StdOutErrListener()
	{
		@Override public void processStdOutTextLine(String line){ System.out.println( line ); }
		@Override public void processStdErrTextLine(String line){ System.err.println( line ); }
	};  
	
	public void processStdErrTextLine(String line);
	public void processStdOutTextLine(String line);
}
