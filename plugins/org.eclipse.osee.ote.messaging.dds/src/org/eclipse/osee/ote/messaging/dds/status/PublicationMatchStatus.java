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
 * Maintains counts of the number of {@link org.eclipse.osee.ote.messaging.dds.entity.DataReader}'s that the {@link org.eclipse.osee.ote.messaging.dds.entity.DataWriter} matched based upon <code>Topic</code> & Qos Policies.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class PublicationMatchStatus extends CountedStatus {
   private InstanceHandle lastSubscriptionHandle;

   /**
    * @param totalCount The cumulative count of <code>DataReader</code>'s whose Qos Policies match.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    * @param lastSubscriptionHandle The last <code>DataReader</code> matching the <code>DataWriter</code> causing the status to change.
    */
   public PublicationMatchStatus(long totalCount, long totalCountChange, InstanceHandle lastSubscriptionHandle) {
      super(totalCount, totalCountChange);
      this.lastSubscriptionHandle = lastSubscriptionHandle;
   }

   /**
    * Gets a handle to the last <code>DataReader</code> matching the DataWriter causing the status to change.
    * 
    * @return Returns the lastInstanceHandle.
    */
   public InstanceHandle getLastSubscriptionHandle() {
      return lastSubscriptionHandle;
   }
}
