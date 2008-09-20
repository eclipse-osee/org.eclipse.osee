/*
 * Created on Sep 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;


/**
 * @author Donald G. Dunne
 */
public enum BranchEventType {

   // Local and Remote events
   Deleted(EventType.LocalAndRemote),
   Added(EventType.LocalAndRemote),
   Renamed(EventType.LocalAndRemote),
   Committed(EventType.LocalAndRemote),

   // Local event only; Does not get sent Remote
   DefaultBranchChanged(EventType.LocalOnly);

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
   private BranchEventType(EventType eventType) {
      this.eventType = eventType;
   }
}
