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

/**
 * Maintains counts of {@link org.eclipse.osee.ote.messaging.dds.entity.DataWriter}'s actively or inactively write the same Topic as the {@link org.eclipse.osee.ote.messaging.dds.entity.DataReader}this is attached to.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class LivelinessChangedStatus extends Status {
   private long activeCount;
   private long activeCountChange;
   private long inactiveCount;
   private long inactiveCountChange;

   /**
    * @param activeCount The current number of active <code>DataWriter</code>'s with the same Topic. 
    * @param inactiveCount The current number of inactive <code>DataWriter</code>'s with the same Topic.
    * @param activeCountChange The change in activeCount since the last time the listener was called or the status was read.
    * @param inactiveCountChange The change in inactiveCount since the last time the listener was called or the status was read.
    */
   public LivelinessChangedStatus(long activeCount, long inactiveCount, long activeCountChange, long inactiveCountChange) {
      super();
      this.activeCount = activeCount;
      this.inactiveCount = inactiveCount;
      this.activeCountChange = activeCountChange;
      this.inactiveCountChange = inactiveCountChange;
   }

   /**
    * Gets the current number of active <code>DataWriter</code>'s with the same Topic. 
    * 
    * @return Returns the activeCount.
    */
   public long getActiveCount() {
      return activeCount;
   }

   /**
    * Gets the change in activeCount since the last time the listener was called or the status was read.
    * 
    * @return Returns the activeCountChange.
    */
   public long getActiveCountChange() {
      return activeCountChange;
   }

   /**
    * Gets the current number of inactive <code>DataWriter</code>'s with the same Topic.
    * 
    * @return Returns the inactiveCount.
    */
   public long getInactiveCount() {
      return inactiveCount;
   }

   /**
    * Gets the change in inactiveCount since the last time the listener was called or the status was read.
    * 
    * @return Returns the inactiveCountChange.
    */
   public long getInactiveCountChange() {
      return inactiveCountChange;
   }
}
