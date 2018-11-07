/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
