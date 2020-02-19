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
package org.eclipse.osee.framework.messaging.event.res.internal;

import java.rmi.RemoteException;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.result.XConsoleLogger;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;

/**
 * @author Roberto E. Escobar
 */
public class FrameworkRelayMessagingListener<T extends RemoteEvent> extends OseeMessagingListener {

   private final IFrameworkEventListener frameworkEventListener;
   private final boolean isVerbose;

   protected FrameworkRelayMessagingListener(Class<?> clazz, IFrameworkEventListener frameworkEventListener, boolean isVerbose) {
      super(clazz);
      this.frameworkEventListener = frameworkEventListener;
      this.isVerbose = isVerbose;
   }

   @Override
   public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
      if (isVerbose) {
         XConsoleLogger.err(
            String.format(getClass().getSimpleName() + " - received [%s]", message.getClass().getSimpleName()));
      }
      try {
         T remoteEvent = asCastedMessage(message);
         frameworkEventListener.onEvent(remoteEvent);
      } catch (RemoteException ex) {
         XConsoleLogger.err(getClass().getSimpleName() + " - process: " + ex.getLocalizedMessage());
      }
   }

   @SuppressWarnings("unchecked")
   private T asCastedMessage(Object message) {
      return (T) message;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (frameworkEventListener == null ? 0 : frameworkEventListener.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      FrameworkRelayMessagingListener<?> other = (FrameworkRelayMessagingListener<?>) obj;
      if (frameworkEventListener == null) {
         if (other.frameworkEventListener != null) {
            return false;
         }
      } else if (!frameworkEventListener.equals(other.frameworkEventListener)) {
         return false;
      }
      return true;
   }
}
