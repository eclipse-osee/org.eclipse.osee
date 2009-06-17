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
package org.eclipse.osee.ote.messaging.dds.status;

import org.eclipse.osee.ote.messaging.dds.InstanceHandle;

/**
 * Maintains counts of the number of times the {@link org.eclipse.osee.ote.messaging.dds.entity.DataReader} detected a missed deadline.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class RequestedDeadlineMissedStatus extends CountedStatus {
   private InstanceHandle lastInstanceHandle;

   /**
    * @param totalCount The cumulative count of deadlines missed.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    * @param lastInstanceHandle The last instance in the <code>DataReader</code> for which the deadline was missed.
    */
   public RequestedDeadlineMissedStatus(long totalCount, long totalCountChange, InstanceHandle lastInstanceHandle) {
      super(totalCount, totalCountChange);
      this.lastInstanceHandle = lastInstanceHandle;
   }

   /**
    * Gets a handle to the instance in the <code>DataReader</code> for which the last deadline that was missed.
    * 
    * @return Returns the lastInstanceHandle.
    */
   public InstanceHandle getLastInstanceHandle() {
      return lastInstanceHandle;
   }
}
