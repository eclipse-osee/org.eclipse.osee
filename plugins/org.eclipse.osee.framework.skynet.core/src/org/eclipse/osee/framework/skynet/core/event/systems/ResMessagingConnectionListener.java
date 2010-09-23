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
package org.eclipse.osee.framework.skynet.core.event.systems;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.skynet.core.event.EventSystemPreferences;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.systems.InternalEventManager.ConnectionStatus;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public final class ResMessagingConnectionListener implements ConnectionListener, ConnectionStatus {

   private final EventSystemPreferences preferences;
   private boolean connectionStatus;

   public ResMessagingConnectionListener(EventSystemPreferences preferences) {
      this.preferences = preferences;
   }

   @Override
   public void connected(ConnectionNode node) {
      connectionStatus = preferences.isOseeEventBrokerValid();
      try {
         OseeEventManager.kickLocalRemEvent(this, RemoteEventServiceEventType.Rem2_Connected);
         OseeLog.log(Activator.class, Level.INFO, "RES2 Connected");
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("REM2: ResConnectionListener", ex);
      }
   }

   @Override
   public void notConnected(ConnectionNode node) {
      connectionStatus = false;
      try {
         OseeEventManager.kickLocalRemEvent(this, RemoteEventServiceEventType.Rem2_DisConnected);
         OseeLog.log(Activator.class, Level.INFO, "RES2 Dis-Connected");
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("REM2: ResConnectionListener", ex);
      }
   }

   @Override
   public boolean isConnected() {
      return connectionStatus;
   }
}