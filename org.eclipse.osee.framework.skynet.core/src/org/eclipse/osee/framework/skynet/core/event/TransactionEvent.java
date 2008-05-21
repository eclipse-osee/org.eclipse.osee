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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.ArtifactVersionIncrementedEvent;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
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
   private Set<Integer> modified = new HashSet<Integer>();
   private Set<Integer> deleted = new HashSet<Integer>();
   private Set<Integer> purged = new HashSet<Integer>();
   private Set<Integer> relChanged = new HashSet<Integer>();
   private Set<Integer> artifactVersionIncremented = new HashSet<Integer>();
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
    * 
    * @throws SQLException
    * @throws ArtifactDoesNotExist
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
            try {
               Artifact artifact = ((ArtifactModifiedEvent) event).getArtifact();
               modified.add(artifact.getArtId());
               if (((ArtifactModifiedEvent) event).getType() == ArtifactModifiedEvent.ModType.Deleted) deleted.add(artifact.getArtId());
               if (((ArtifactModifiedEvent) event).getType() == ArtifactModifiedEvent.ModType.Purged) purged.add(artifact.getArtId());
               debug.report("   MATCH FOUND ");
            } catch (ArtifactDoesNotExist ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            } catch (MultipleArtifactsExist ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            } catch (SQLException ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }

         } else if (event instanceof RelationModifiedEvent) {
            debug.report("   MATCH FOUND ");
            relChanged.add(((RelationModifiedEvent) event).getLink().getAArtifactId());
            relChanged.add(((RelationModifiedEvent) event).getLink().getBArtifactId());
         } else if (event instanceof ArtifactVersionIncrementedEvent) {
            debug.report("   MATCH FOUND ");
            artifactVersionIncrementedEvent.put(((ArtifactVersionIncrementedEvent) event).getOldVersion(),
                  ((ArtifactVersionIncrementedEvent) event));
            artifactVersionIncremented.add(((ArtifactVersionIncrementedEvent) event).getOldVersion().getArtId());
            artifactVersionIncremented.add(((ArtifactVersionIncrementedEvent) event).getNewVersion().getArtId());
         }
      }
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
      ed.setDeleted(deleted.contains(artifact.getArtId()));
      ed.setModified(modified.contains(artifact.getArtId()));
      ed.setRelChange(relChanged.contains(artifact.getArtId()));
      ed.setPurged(purged.contains(artifact.getArtId()));
      ed.setArtifactVersionIncremented(artifactVersionIncremented.contains(artifact.getArtId()));
      ed.setAvie(artifactVersionIncrementedEvent.get(artifact.getArtId()));
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
