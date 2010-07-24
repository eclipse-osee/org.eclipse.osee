/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
