/*********************************************************************
 * Copyright (c) 2010 Boeing
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

/**
 * @author Donald G. Dunne
 */
public enum EventModType {
   Modified("AISIbRtFzxuuH0pissgA"),
   Deleted("AISIbRvYChyA1qUTqXAA"),
   Purged("AISIbRxrXDC4PjDm6JwA"),
   Reloaded("AISIbSCAzETv5pfJhNAA"),
   Added("AISIbSEWTQUeu120fkwA"),
   ChangeType("AISIbSGpn08WdLdsA6AA");

   private final String guid;

   private EventModType(String guid) {
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public static EventModType getType(String guid) {
      for (EventModType type : values()) {
         if (type.guid.equals(guid)) {
            return type;
         }
      }
      return null;
   }

};
