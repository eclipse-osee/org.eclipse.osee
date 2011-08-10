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
package org.eclipse.osee.mail.admin.internal;

import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import org.eclipse.osee.mail.MailEventUtil;
import org.eclipse.osee.mail.SendMailStatus;

/**
 * @author Roberto E. Escobar
 */
public class StatusTransportListener implements TransportListener {

   private final SendMailStatus status;
   private volatile boolean wasUpdateReceived;

   public StatusTransportListener(SendMailStatus status) {
      this.status = status;
      this.wasUpdateReceived = false;
   }

   @Override
   public void messageDelivered(TransportEvent event) {
      handleEvent(event);
   }

   @Override
   public void messageNotDelivered(TransportEvent event) {
      handleEvent(event);
   }

   @Override
   public void messagePartiallyDelivered(TransportEvent event) {
      handleEvent(event);
   }

   private void handleEvent(TransportEvent event) {
      synchronized (this) {
         try {
            MailEventUtil.loadStatus(status, event);
         } catch (Exception ex) {
            ex.printStackTrace();
         }
         wasUpdateReceived = true;
         notify();
      }
   }

   public synchronized boolean wasUpdateReceived() {
      return wasUpdateReceived;
   }

}