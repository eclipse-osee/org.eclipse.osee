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
package org.eclipse.osee.framework.messaging.event.res;

import java.rmi.RemoteException;

/**
 * Client call back
 * 
 * @author Donald G. Dunne
 */
public abstract class FrameworkEventListener implements IFrameworkEventListener {

   private static final long serialVersionUID = -3051021974908944273L;
   private final Long uid;

   public FrameworkEventListener() {
      this.uid = (long) (Math.random() * Long.MAX_VALUE);
   }

   /**
    * Since the listeners are hashed on the service side, it is necessary to force the equals and hashcode operators to
    * work in a particular manner.
    */
   @Override
   public final boolean equals(Object obj) {
      if (obj instanceof FrameworkEventListener) {
         return ((FrameworkEventListener) obj).uid.equals(this.uid);
      }
      return false;
   }

   @Override
   public final int hashCode() {
      return uid.hashCode();
   }

   /**
    * Callback for the client
    */
   public abstract void onEvent(RemoteEvent[] events) throws RemoteException;

}
