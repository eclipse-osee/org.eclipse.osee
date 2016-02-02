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
package org.eclipse.osee.framework.skynet.core.event.model;

/**
 * @author Donald G. Dunne
 */
public enum AccessControlEventType {
   ArtifactsLocked(EventType.LocalAndRemote, "AAn_QHnJpWky8xcyKEgA", 15),
   ArtifactsUnlocked(EventType.LocalAndRemote, "AFRkIPF_y3ExB4XCyPgA", 20),
   UserAuthenticated(EventType.LocalOnly, "AFRkIhbm0BbIGKALcKQA", 25);

   private final EventType eventType;
   private final String guid;
   private final int id;

   public boolean isRemoteEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.RemoteOnly;
   }

   public boolean isLocalEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.LocalOnly;
   }

   private AccessControlEventType(EventType eventType, String guid, int id) {
      this.eventType = eventType;
      this.guid = guid;
      this.id = id;
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

   public int getId() {
      return id;
   }

}
