package org.eclipse.osee.framework.skynet.core.event2.artifact;


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
