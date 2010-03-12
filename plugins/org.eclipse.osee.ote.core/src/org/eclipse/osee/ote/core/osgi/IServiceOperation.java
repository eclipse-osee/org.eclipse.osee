/*
 * Created on Mar 9, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.core.osgi;

/**
 * An operation that interacts with an OSGI service in an atomic manner
 * @author Ken J. Aguilar
 *
 */
public interface IServiceOperation {

	/**
	 * the operation to perform against the service. The service instance will be
	 * valid during the execution of the operation. Subclasses should handle the case
	 * when the operation is asynchronously interrupted. 
	 * @param service
	 * @throws InterruptedException
	 */
	void doOperation(Object service) throws InterruptedException;
	
	/**
	 * called when the service is about to be removed. 
	 */
	void interrupt();
}
