/** PLGSAutoProcessor, de.mz.jk.os, Feb 16, 2016*/
package de.mz.jk.os;

/**
 * <h3>{@link ProcessInfo}</h3>
 * @author jkuharev
 * @version Feb 16, 2016 1:06:52 PM
 */
public class ProcessInfo
{
	public String cmd = "";
	public int pid = 0;
	public float cpu = 0;
	public float mem = 0;

	@Override public String toString()
	{
		return "PID:" + pid + "\t%CPU:" + cpu + "\t%MEM:" + mem + "\tCMD:" + cmd;
	}
}
