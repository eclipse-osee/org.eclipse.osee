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
package org.eclipse.osee.ote.messaging.dds.listener;

import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessChangedStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleRejectedStatus;
import org.eclipse.osee.ote.messaging.dds.status.SubscriptionMatchStatus;

/**
 * The interface for receiving events from a particular <code>DataReader</code>.
 * 
 * @see org.eclipse.osee.ote.messaging.dds.entity.DataReader
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface DataReaderListener extends Listener{
   
   /**
    * The method called when data is available for a <code>DataReader</code>. This method
    * is only called if there is no <code>SubscriberListener</code> assigned to the
    * <code>Subscriber</code> that created the <code>DataReader</code>.
    * 
    * @param theReader - The reader for which data is available.
    */
   public void onDataAvailable(DataReader theReader);

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onSampleRejected(DataReader theReader, SampleRejectedStatus status);

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onLivelinessChanged(DataReader theReader, LivelinessChangedStatus status);

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onRequestedDeadlineMissed(DataReader theReader, RequestedDeadlineMissedStatus status);

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onRequestedIncompatibleQos(DataReader theReader, RequestedIncompatibleQosStatus status);

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onSubscriptionMatch(DataReader theReader, SubscriptionMatchStatus status);

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onSampleLost(DataReader theReader, SampleLostStatus status);
}
