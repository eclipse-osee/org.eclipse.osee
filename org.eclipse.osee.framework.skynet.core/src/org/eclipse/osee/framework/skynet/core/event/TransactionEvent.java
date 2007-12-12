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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.ArtifactVersionIncrementedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.CoreDebug;

/**
 * @author Jeff C. Phillips
 */
public abstract class TransactionEvent extends Event {

   private Collection<Event> localEvents;
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private CoreDebug debug = new CoreDebug(false, "TE");
   private boolean processedLookups = false;
   private Set<Artifact> modified = new HashSet<Artifact>();
   private Set<Artifact> deleted = new HashSet<Artifact>();
   private Set<Artifact> purged = new HashSet<Artifact>();
   private Set<Artifact> relChanged = new HashSet<Artifact>();
   private Set<Artifact> artifactVersionIncremented = new HashSet<Artifact>();
   private Map<Artifact, ArtifactVersionIncrementedEvent> artifactVersionIncrementedEvent =
         new HashMap<Artifact, ArtifactVersionIncrementedEvent>();

   /**
    * @param events
    * @param sender
    */
   public TransactionEvent(Collection<Event> events, Object sender) {
      super(sender);
      this.localEvents = new ArrayList<Event>();
      this.localEvents.addAll(events);
      processLookups();
   }

   /**
    * This method rolls up the transaction event data into pieces most widely used by applications. Calling getEventData
    * with an artifact will retrieve cached data instead of requiring each UI to loop through all localEvents looking
    * for events tied to that artifact.
    */
   private void processLookups() {
      // Use modified
      if (processedLookups) return;
      processedLookups = true;
      for (Event event : localEvents) {
         debug.report("   event " + event);
         // Use guid check cause a deleted artifactModEvent doesn't currently return the deleted
         // artifact
         if (event instanceof ArtifactModifiedEvent) {
            Artifact artifact = ((ArtifactModifiedEvent) event).getArtifact();
            modified.add(artifact);
            if (((ArtifactModifiedEvent) event).getType() == ArtifactModifiedEvent.ModType.Deleted) deleted.add(artifact);
            if (((ArtifactModifiedEvent) event).getType() == ArtifactModifiedEvent.ModType.Purged) purged.add(artifact);
            debug.report("   MATCH FOUND ");
         } else if (event instanceof RelationModifiedEvent) {
            debug.report("   MATCH FOUND ");
            relChanged.add(((RelationModifiedEvent) event).getLink().getArtifactA());
            relChanged.add(((RelationModifiedEvent) event).getLink().getArtifactB());
         } else if (event instanceof ArtifactVersionIncrementedEvent) {
            debug.report("   MATCH FOUND ");
            artifactVersionIncrementedEvent.put(((ArtifactVersionIncrementedEvent) event).getOldVersion(),
                  ((ArtifactVersionIncrementedEvent) event));
            artifactVersionIncremented.add(((ArtifactVersionIncrementedEvent) event).getOldVersion());
            artifactVersionIncremented.add(((ArtifactVersionIncrementedEvent) event).getNewVersion());
         }
      }
   }

   public boolean hasLocalEvent(Artifact artifact) {
      return getLocalEvents(artifact).size() > 0;
   }

   public Collection<Event> getLocalEvents(Artifact artifact) {
      ArrayList<Event> events = new ArrayList<Event>();
      for (Event event : localEvents) {
         if ((event instanceof ArtifactModifiedEvent) && ((ArtifactModifiedEvent) event).getArtifact().equals(artifact))
            events.add(event);
         else if (event instanceof RelationModifiedEvent) {
            if (((RelationModifiedEvent) event).effectsArtifact(artifact)) {
               events.add(event);
            }
         } else if ((event instanceof ArtifactVersionIncrementedEvent) && ((ArtifactVersionIncrementedEvent) event).getOldVersion().equals(
               artifact)) {
            events.add(event);
         }
      }
      return events;
   }

   /**
    * @return Returns the localEvents.
    */
   public Collection<Event> getLocalEvents() {
      return localEvents;
   }

   /**
    * Fires off events for this listener only.
    * 
    * @param listener - IEventReceiver
    */
   public void fireSingleEvent(IEventReceiver listener) {
      HashSet<IEventReceiver> validReceivers;

      for (Event event : localEvents) {
         validReceivers = new HashSet<IEventReceiver>(eventManager.getReceivers(event));

         if (validReceivers.contains(listener)) listener.onEvent(event);
      }
   }

   /**
    * Avie will be set if version inc event exists for the given artifact AND the artifact matches the old version in
    * the event
    */
   public EventData getEventData(Artifact artifact) {
      EventData ed = new EventData();
      ed.setDeleted(deleted.contains(artifact));
      ed.setModified(modified.contains(artifact));
      ed.setRelChange(relChanged.contains(artifact));
      ed.setPurged(purged.contains(artifact));
      ed.setArtifactVersionIncremented(artifactVersionIncremented.contains(artifact));
      ed.setAvie(artifactVersionIncrementedEvent.get(artifact));
      ed.setHasEvent(ed.isRelChange() || ed.isDeleted() || ed.isModified() || ed.isPurged());
      return ed;
   }
   public static class EventData {
      private boolean deleted;
      private boolean purged;
      private boolean hasEvent;
      private boolean modified;
      private boolean relChange;
      private boolean artifactVersionIncremented;
      private ArtifactVersionIncrementedEvent avie = null;

      public EventData() {
      }

      /**
       * Avie will be set if version inc event exists for the given artifact AND the artifact matches the old version in
       * the event
       * 
       * @return ArtifactVersionIncrementedEvent or null if none available
       */
      public ArtifactVersionIncrementedEvent getAvie() {
         return avie;
      }

      public void setAvie(ArtifactVersionIncrementedEvent avie) {
         this.avie = avie;
      }

      /**
       * @return true if any event was found
       */
      public boolean isHasEvent() {
         return hasEvent;
      }

      /**
       * @param hasEvent
       */
      public void setHasEvent(boolean hasEvent) {
         this.hasEvent = hasEvent;
      }

      /**
       * Artifact was either deleted or purged
       * 
       * @return true if deleted or purged
       */
      public boolean isRemoved() {
         return deleted || purged;
      }

      public boolean isModified() {
         return modified;
      }

      public void setModified(boolean modified) {
         this.modified = modified;
      }

      public boolean isRelChange() {
         return relChange;
      }

      public void setRelChange(boolean relChange) {
         this.relChange = relChange;
      }

      public boolean isPurged() {
         return purged;
      }

      public void setPurged(boolean purged) {
         this.purged = purged;
      }

      /**
       * @return the deleted
       */
      public boolean isDeleted() {
         return deleted;
      }

      /**
       * @param deleted the deleted to set
       */
      public void setDeleted(boolean deleted) {
         this.deleted = deleted;
      }

      /**
       * @return the artifactVersionIncremented
       */
      public boolean isArtifactVersionIncremented() {
         return artifactVersionIncremented;
      }

      /**
       * @param artifactVersionIncremented the artifactVersionIncremented to set
       */
      public void setArtifactVersionIncremented(boolean artifactVersionIncremented) {
         this.artifactVersionIncremented = artifactVersionIncremented;
      }

   }

}
