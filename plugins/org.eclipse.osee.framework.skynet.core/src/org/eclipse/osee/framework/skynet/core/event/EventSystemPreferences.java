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

package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class EventSystemPreferences {
   // This will disable all Local TransactionEvents and enable loop-back routing of Remote TransactionEvents back
   // through the RemoteEventService as if they came from another client.  This is for testing purposes only and
   // should be reset to false before release.
   private boolean enableRemoteEventLoopback = false;
   private boolean disableEvents = false;
   private boolean pendRunning = false;

   public boolean isDisableEvents() {
      return disableEvents;
   }

   public void setDisableEvents(boolean disableEvents) {
      this.disableEvents = disableEvents;
   }

   public boolean isEnableRemoteEventLoopback() {
      return enableRemoteEventLoopback;
   }

   public void setEnableRemoteEventLoopback(boolean enableRemoteEventLoopback) {
      this.enableRemoteEventLoopback = enableRemoteEventLoopback;
   }

   /**
    * If true, all listeners will be called back in main thread. For testing purposes only.
    */
   public void setPendRunning(boolean pendRunning) {
      this.pendRunning = pendRunning;
   }

   public boolean isPendRunning() {
      return pendRunning;
   }

   public String getEventDebug() {
      return System.getProperty("eventDebug");
   }

   public String getOseeEventBrokerUri() {
      return OseeProperties.getOseeDefaultBrokerUri();
   }

   public boolean isOseeEventBrokerValid() {
      return Strings.isValid(getOseeEventBrokerUri());
   }
}