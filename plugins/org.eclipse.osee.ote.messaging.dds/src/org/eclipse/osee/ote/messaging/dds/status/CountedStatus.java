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
 * A <code>Status</code> that also maintains a counts for the total and incremental number of samples associated with the status.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public abstract class CountedStatus extends Status {
   private long totalCount;
   private long totalCountChange;

   /**
    * @param totalCount The cumulative count of samples or topics associated with this status.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    */
   public CountedStatus(long totalCount, long totalCountChange) {
      super();
      this.totalCount = totalCount;
      this.totalCountChange = totalCountChange;
   }

   /**
    * Gets the Total Count of all samples or topics associated with this status.
    * 
    * @return Returns the totalCount.
    */
   public long getTotalCount() {
      return totalCount;
   }

   /**
    * Gets the number of samples or topics associated with this status since the last time the listener was called or the status was read.
    * 
    * @return Returns the totalCountChange.
    */
   public long getTotalCountChange() {
      return totalCountChange;
   }

}
