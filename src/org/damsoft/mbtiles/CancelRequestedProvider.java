/**
 * 
 */
package org.damsoft.mbtiles;

/**
 * @author Hans van Dam
 *
 */
public interface CancelRequestedProvider {

	public abstract boolean isCancelrequested();

	public abstract void setCancelrequested(boolean cancelrequested);

}