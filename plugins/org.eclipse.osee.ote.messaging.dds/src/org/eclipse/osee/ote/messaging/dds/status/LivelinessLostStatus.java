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
 * Maintains counts of the number of times the {@link org.eclipse.osee.ote.messaging.dds.entity.DataWriter} failed to signal its liveliness within the liveliness period.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class LivelinessLostStatus extends CountedStatus {

   /**
    * @param totalCount The cumulative count of liveliness lost.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    */
   public LivelinessLostStatus(long totalCount, long totalCountChange) {
      super(totalCount, totalCountChange);
   }

}
