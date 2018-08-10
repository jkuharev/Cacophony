/** PLGSAutoProcessorApplication, waters.plgs.bin, Jan 15, 2016*/
package de.mz.jk.cli;

/**
 * <h3>{@link ProcessStatus}</h3>
 * @author jkuharev
 * @version Jan 15, 2016 1:51:06 PM
 */
public enum AppState
{
	building,
	initialized,
	running,
	failed,
	finished,
	unknown;
}
