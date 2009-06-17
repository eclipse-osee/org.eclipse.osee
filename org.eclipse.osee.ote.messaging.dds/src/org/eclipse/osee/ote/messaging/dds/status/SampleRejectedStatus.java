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
 * Maintains counts of the number of samples rejected by a {@link org.eclipse.osee.ote.messaging.dds.entity.DataReader}.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class SampleRejectedStatus extends CountedStatus {
   private SampleRejectedStatusKind lastReason;
   private InstanceHandle lastInstanceHandle;

   /** 
    * @param totalCount The cumulative count of all samples rejected by the <code>DataReader</code>.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    * @param lastReason The reason the last sample was rejected.
    * @param lastInstanceHandle The instance of the last rejected sample.
    */
   public SampleRejectedStatus(long totalCount, long totalCountChange, SampleRejectedStatusKind lastReason, InstanceHandle lastInstanceHandle) {
      super(totalCount, totalCountChange);
      this.lastReason = lastReason;
      this.lastInstanceHandle = lastInstanceHandle;
   }

   /**
    * Gets the instance of the last sample that was rejected.
    * 
    * @return Returns the lastInstanceHandle.
    */
   public InstanceHandle getLastInstanceHandle() {
      return lastInstanceHandle;
   }

   /**
    * Gets the reason the last sample was rejected.
    * 
    * @return Returns the lastReason.
    */
   public SampleRejectedStatusKind getLastReason() {
      return lastReason;
   }
}
