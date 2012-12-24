/**
 * 
 */
package org.damsoft.mbtiles;

/**
 * @author Hans van Dam
 *
 */
public interface ICancelRequestedProvider {

	public abstract boolean isCancelrequested();

	public abstract void setCancelrequested(boolean cancelrequested);

	public abstract void cancelRequestExecuted();

}