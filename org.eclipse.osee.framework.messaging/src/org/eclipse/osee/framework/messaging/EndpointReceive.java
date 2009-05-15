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
package org.eclipse.osee.framework.messaging;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.internal.Activator;
import org.eclipse.osee.framework.messaging.internal.ApplicationDistributer;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class EndpointReceive {

   private ApplicationDistributer distributer;
   private ExecutorService executor;

   public EndpointReceive() {
      executor = Executors.newSingleThreadExecutor();
   }

   /**
    * The MessagingGateway implementation must call this method to set the ApplicationDistributer callback so that
    * received messages get propagated to the application.
    * 
    * @param distributer
    */
   public void onBind(ApplicationDistributer distributer) {
      this.distributer = distributer;
   }

   public void onUnbind(ApplicationDistributer distributer) {
      this.distributer = null;
   }

   /**
    * This method must be called by the implementing class when it receives a message so that it gets propagated to the
    * MessagingGateway
    */
   protected void onReceive(final Message message) {
      if (distributer == null) {
         String errorMsg =
               String.format(
                     "We have recieved message [%s] from [%s], but have no active ApplicationDistributer available.",
                     message.getId().toString(), message.getSource().toString());
         OseeLog.log(Activator.class, Level.WARNING, errorMsg);
      } else {
         executor.execute(new Runnable() {
            @Override
            public void run() {
               distributer.distribute(message);
            }
         });
      }
   }

   public abstract void start(Properties properties);

   public abstract void dispose();
}
