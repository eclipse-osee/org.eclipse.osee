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
package org.eclipse.osee.framework.messaging.event.skynet;

import java.rmi.RemoteException;

/**
 * Client callback base class.
 * 
 * @author Robert A. Fisher
 */
public abstract class ASkynetEventListener implements ISkynetEventListener {

   private final Long uid;

   public ASkynetEventListener() {
      this.uid = (long) (Math.random() * Long.MAX_VALUE);
   }

   /**
    * Since the listeners are hashed on the service side, it is necessary to force the equals and hashcode operators to
    * work in a particular manner.
    */
   @Override
   public final boolean equals(Object obj) {
      if (obj instanceof ASkynetEventListener) {
         return ((ASkynetEventListener) obj).uid.equals(this.uid);
      }
      return false;
   }

   @Override
   public final int hashCode() {
      return uid.hashCode();
   }

   /**
    * Callback for the client to process remote skynet events. The array will contain a list of events to which this
    * listener is subscribed.
    * 
    * @throws RemoteException
    */
   public abstract void onEvent(ISkynetEvent[] events) throws RemoteException;

}
