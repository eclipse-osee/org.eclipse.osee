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
package org.eclipse.osee.ote.messaging.dds.entity;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides threading capability for the publication of data. The DDS system
 * makes use of this class internally and controls it as needed to control
 * the publication of data.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
class PublisherThread extends Thread {
   private DomainParticipant domainParticipant;
   
   /**
    * Get a <code>PublisherThread</code> for a particular <code>DomainParticipant</code>
    * 
    * @param domainParticipant - The participant for the thread to call upon
    */
   public PublisherThread(DomainParticipant domainParticipant) {
      super("Publisher Thread");
      this.domainParticipant = domainParticipant;
      setDaemon(true);
   }
   
   public synchronized void run() {
      
      try {
         while (true) {
            wait(); // Wait for a notify
            
            // Once notified, call all the queue publishing
            Collection<Publisher> publishers = domainParticipant.getPublishers();
            synchronized (publishers) {
               Iterator<Publisher> iter = publishers.iterator();
               while (iter.hasNext()) {
                  iter.next().publishQueuedData();
               }
            }
         }
      }
      catch (InterruptedException ex) {
         // We do not expect to be interrupted, so print the error
         ex.printStackTrace();
      }
   }
   
   /**
    * Method to cause the thread to publish the queue'd information in each <code>Subscriber</code>
    * then go back to sleep.
    */
   public synchronized void wakeUp() {
      notifyAll();
   }
}
