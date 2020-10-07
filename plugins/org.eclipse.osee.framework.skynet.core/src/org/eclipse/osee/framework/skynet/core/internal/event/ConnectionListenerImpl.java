/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.internal.event;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.event.EventUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.skynet.core.event.EventSystemPreferences;
import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public final class ConnectionListenerImpl implements ConnectionListener {

   private final EventSystemPreferences preferences;
   private final Transport transport;

   public ConnectionListenerImpl(EventSystemPreferences preferences, Transport transport) {
      this.preferences = preferences;
      this.transport = transport;
   }

   @Override
   public void connected(ConnectionNode node) {
      transport.setConnected(preferences.isOseeEventBrokerValid());
      try {
         transport.send(this, RemoteEventServiceEventType.Rem_Connected);
         OseeLog.log(Activator.class, Level.INFO, "Remote Event Service - Connected");
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("REM: ResConnectionListener", ex);
      }
   }

   @Override
   public void notConnected(ConnectionNode node) {
      transport.setConnected(false);
      try {
         transport.send(this, RemoteEventServiceEventType.Rem_DisConnected);
         OseeLog.log(Activator.class, Level.INFO, "Remote Event Service - Dis-Connected");
      } catch (OseeCoreException ex) {
         EventUtil.eventLog("REM: ResConnectionListener", ex);
      }
   }

}