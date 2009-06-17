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
 * Maintains counts of Topics whose name match the topic this <code>Status</code> is attached to, but type differs.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class InconsistentTopicStatus extends CountedStatus {

   /**
    * @param totalCount The cumulative countt of inconsistent statuses.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    */
   public InconsistentTopicStatus(long totalCount, long totalCountChange) {
      super(totalCount, totalCountChange);
   }
   
}
