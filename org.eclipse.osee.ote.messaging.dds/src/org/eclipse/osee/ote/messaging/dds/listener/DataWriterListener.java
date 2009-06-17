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

import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.PublicationMatchStatus;

/**
 * The interface for receiving events from <code>DataWriter</code>'s.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface DataWriterListener extends Listener{

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onLivelinessLost(DataWriter theWriter, LivelinessLostStatus status);

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onOfferedDeadlineMissed(DataWriter theWriter, OfferedDeadlineMissedStatus status);

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onOfferedIncompatibleQos(DataWriter theWriter, OfferedIncompatibleQosStatus status);

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void onPublicationMatch(DataWriter theWriter, PublicationMatchStatus status);

   /**
    * This method is called when information for a <code>DataWriter</code> has been sent to the
    * middleware.
    * 
    * @param theWriter - The <code>DataWriter</code> who's information was sent.
    */
   public void onDataSentToMiddleware(DataWriter theWriter);
}
