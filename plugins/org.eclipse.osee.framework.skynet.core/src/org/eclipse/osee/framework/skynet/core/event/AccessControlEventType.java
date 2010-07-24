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
package org.eclipse.osee.framework.skynet.core.event;

/**
 * @author Donald G. Dunne
 */
public enum AccessControlEventType {
   ArtifactsLocked(EventType.LocalAndRemote, "AAn_QHnJpWky8xcyKEgA"),
   ArtifactsUnlocked(EventType.LocalAndRemote, "AFRkIPF_y3ExB4XCyPgA"),
   UserAuthenticated(EventType.LocalOnly, "AFRkIhbm0BbIGKALcKQA");

   private final EventType eventType;
   private final String guid;

   public boolean isRemoteEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.RemoteOnly;
   }

   public boolean isLocalEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.LocalOnly;
   }

   private AccessControlEventType(EventType eventType, String guid) {
      this.eventType = eventType;
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public static AccessControlEventType getByGuid(String guid) {
      for (AccessControlEventType type : values()) {
         if (type.guid.equals(guid)) {
            return type;
         }
      }
      return null;
   }

}
