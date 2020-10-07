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

package org.eclipse.osee.framework.skynet.core.event.model;

import org.eclipse.osee.framework.core.event.EventType;
import org.eclipse.osee.framework.core.event.FrameworkEvent;

/**
 * @author Donald G. Dunne
 */
public enum RemoteEventServiceEventType implements FrameworkEvent {
   Rem_Connected(EventType.LocalOnly),
   Rem_DisConnected(EventType.LocalOnly);

   private final EventType eventType;

   public boolean isRemoteEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.RemoteOnly;
   }

   public boolean isLocalEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.LocalOnly;
   }

   /**
    * @param localOnly true if this event type is to be thrown only locally and not to other clients
    */
   private RemoteEventServiceEventType(EventType eventType) {
      this.eventType = eventType;
   }

}
