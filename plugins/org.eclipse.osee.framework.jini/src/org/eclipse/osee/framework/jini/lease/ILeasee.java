/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jini.lease;

/**
 * This interface should be implemented by classes that use the OseeLeaseGrantor class in order to provide information
 * about lease events.
 * 
 * @author David Diepenbrock
 */
public interface ILeasee {

   /**
    * Called when a lease expires or is canceled.
    * 
    * @param consumer The consumer of the lease
    */
   void onLeaseCompleted(Object consumer);
}
