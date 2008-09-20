/*
 * Created on Sep 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

/**
 * @author Donald G. Dunne
 */
public enum AccessControlEventType {
   ArtifactsLocked(EventType.LocalAndRemote),
   ArtifactsUnlocked(EventType.LocalAndRemote),
   UserAuthenticated(EventType.LocalOnly);

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
   private AccessControlEventType(EventType eventType) {
      this.eventType = eventType;
   }
}
