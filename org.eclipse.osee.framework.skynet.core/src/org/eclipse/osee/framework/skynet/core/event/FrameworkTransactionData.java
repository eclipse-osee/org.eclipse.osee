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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * Collection of all the data that makes up a single SkynetTransaction and helper methods that allow the applications to
 * easily determine how the changes should be handled. Since events changing artifacts and relations can happen
 * internally in a single client or externally through a server or another client, this class separates the events into
 * cached and unloaded objects.<br>
 * <br>
 * In most cases, applications should only care about artifacts/relations that are loaded into their client's cache.
 * This class provides easy access to this information. In cases where more information is necessary, the Unloaded
 * objects are provided with the data needed to 1) determine if application cares about event prior to loading 2) data
 * needed to load the objects.<br>
 * <br>
 * Care needs to be taken by applications to no load every unloaded artifact and relation to determine if the event
 * needs to be handled. If unloaded objects need to be loaded, their artIds should be collected and bulk loaded through
 * the ArtifactQuery methods such as ArtifactQuery.getArtifactsByIds.
 * 
 * @author Donald G. Dunne
 */
public class FrameworkTransactionData {

   Collection<ArtifactTransactionModifiedEvent> xModifiedEvents;

   // artifact collections of artifacts based on artifactModType that are currently loaded in the client's artifact cache
   public Set<Artifact> cacheChangedArtifacts = new HashSet<Artifact>();
   public Set<Artifact> cacheDeletedArtifacts = new HashSet<Artifact>();
   public Set<Artifact> cacheAddedArtifacts = new HashSet<Artifact>();

   // collection of unloaded artifact changes that are NOT currently loaded in the client's artifact cache;  
   // where UnloadedArtifact contains artifact id, branch and artifact type id 
   public Set<UnloadedArtifact> unloadedChangedArtifacts = new HashSet<UnloadedArtifact>();
   public Set<UnloadedArtifact> unloadedDeletedArtifacts = new HashSet<UnloadedArtifact>();
   public Set<UnloadedArtifact> unloadedAddedArtifacts = new HashSet<UnloadedArtifact>();

   // cacheRelations are relations where one side artifact is already loaded in client's cache
   // NOTE: Change relations are Rationale or Order changes only
   public Set<LoadedRelation> cacheChangedRelations = new HashSet<LoadedRelation>();
   public Set<LoadedRelation> cacheAddedRelations = new HashSet<LoadedRelation>();
   public Set<LoadedRelation> cacheDeletedRelations = new HashSet<LoadedRelation>();

   // unloadedRelations are relations where neither side artifact is loaded in client's cache; normally don't care about these
   // NOTE: Change relations are Rationale or Order changes only
   public Set<UnloadedRelation> unloadedChangedRelations = new HashSet<UnloadedRelation>();
   public Set<UnloadedRelation> unloadedAddedRelations = new HashSet<UnloadedRelation>();
   public Set<UnloadedRelation> unloadedDeletedRelations = new HashSet<UnloadedRelation>();

   // artifact collection of artifacts on either side of a relation that are loaded in client's cache
   // NOTE: Change relations are Rationale or Order changes only
   public Set<Artifact> cacheRelationChangedArtifacts = new HashSet<Artifact>();
   public Set<Artifact> cacheRelationDeletedArtifacts = new HashSet<Artifact>();
   public Set<Artifact> cacheRelationAddedArtifacts = new HashSet<Artifact>();

   public int branchId = -1;

   public static enum ChangeType {
      Changed, Deleted, Added, All
   };

   /**
    * Return branchId of loaded artifacts or -1 if no loaded artifacts
    * 
    * @return branchId
    */
   public Integer getBranchId() {
      return branchId;
   }

   public Collection<Artifact> getArtifactsInRelations(ChangeType changeType, RelationType relationTypes) {
      return getArtifactsInRelations(changeType, Arrays.asList(relationTypes));
   }

   public Collection<Artifact> getArtifactsInRelations(ChangeType changeType, Collection<RelationType> relationTypes) {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      if (changeType == ChangeType.Added || changeType == ChangeType.All) {
         for (LoadedRelation loadedRelation : cacheAddedRelations) {
            if (relationTypes.contains(loadedRelation.getRelationType())) {
               if (loadedRelation.getArtifactA() != null) artifacts.add(loadedRelation.getArtifactA());
               if (loadedRelation.getArtifactB() != null) artifacts.add(loadedRelation.getArtifactB());
            }
         }
      }
      if (changeType == ChangeType.Deleted || changeType == ChangeType.All) {
         for (LoadedRelation loadedRelation : cacheDeletedRelations) {
            if (relationTypes.contains(loadedRelation.getRelationType())) {
               if (loadedRelation.getArtifactA() != null) artifacts.add(loadedRelation.getArtifactA());
               if (loadedRelation.getArtifactB() != null) artifacts.add(loadedRelation.getArtifactB());
            }
         }
      }
      if (changeType == ChangeType.Changed || changeType == ChangeType.All) {
         for (LoadedRelation loadedRelation : cacheChangedRelations) {
            if (relationTypes.contains(loadedRelation.getRelationType())) {
               if (loadedRelation.getArtifactA() != null) artifacts.add(loadedRelation.getArtifactA());
               if (loadedRelation.getArtifactB() != null) artifacts.add(loadedRelation.getArtifactB());
            }
         }
      }
      return artifacts;
   }

   public Collection<Integer> getArtifactIdsOfArtifactType(ArtifactType artifactType, ArtifactModType... artifactModType) throws OseeCoreException {
      Collection<ArtifactModType> artifactModTypes =
            org.eclipse.osee.framework.jdk.core.util.Collections.getAggregate(artifactModType);
      if (artifactType == null) return Collections.emptyList();
      Set<Integer> artIds = new HashSet<Integer>();
      for (ArtifactTransactionModifiedEvent modEvent : xModifiedEvents) {
         if (modEvent instanceof ArtifactModifiedEvent) {
            if (artifactModTypes.contains(artifactModType) && (((ArtifactModifiedEvent) modEvent).unloadedArtifact.getArtifactTypeId() == artifactType.getArtTypeId())) {
               artIds.add(((ArtifactModifiedEvent) modEvent).unloadedArtifact.getArtifactId());
            }
         }
      }
      return artIds;
   }

   /**
    * Return artifacts related to this artifact from event service loadedRelations collection. This will bulk load all
    * opposite-side artifacts if they are not already loaded.
    * 
    * @param cacheRelations
    * @return collection of related artifacts
    * @throws OseeCoreException
    */
   public Collection<Artifact> getRelatedArtifacts(int artId, int relationTypeId, int branchId, Collection<LoadedRelation> loadedRelations) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      try {
         Set<Integer> artifactIds = new HashSet<Integer>();
         for (LoadedRelation loadedRelation : loadedRelations) {
            // If given artId is artA
            if (loadedRelation.getArtifactA() != null && loadedRelation.getArtifactA().getArtId() == artId) {
               if (loadedRelation.getRelationType().getRelationTypeId() == relationTypeId) {
                  if (loadedRelation.getArtifactB() != null) {
                     artifacts.add(loadedRelation.getArtifactB());
                  } else {
                     artifactIds.add(loadedRelation.getUnloadedRelation().getArtifactBId());
                  }
               }
            }
            // If given artId is ArtB
            if (loadedRelation.getArtifactB() != null && loadedRelation.getArtifactB().getArtId() == artId) {
               if (loadedRelation.getRelationType().getRelationTypeId() == relationTypeId) {
                  if (loadedRelation.getArtifactA() != null) {
                     artifacts.add(loadedRelation.getArtifactA());
                  } else {
                     artifactIds.add(loadedRelation.getUnloadedRelation().getArtifactAId());
                  }
               }
            }
         }
         if (artifactIds.size() > 0) {
            artifacts.addAll(ArtifactQuery.getArtifactsFromIds(artifactIds, BranchManager.getBranch(branchId), false));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
      return artifacts;
   }

   public boolean isRelAddedChangedDeleted(Artifact artifact) {
      return isRelAddedChangedDeleted(artifact.getArtId());
   }

   public boolean isRelAddedChangedDeleted(int artId) {
      return isRelChange(artId) || isRelAdded(artId) || isRelDeleted(artId);

   }

   /**
    * @return true if any event was found
    */
   public boolean isHasEvent(Artifact artifact) {
      return isHasEvent(artifact.getArtId());
   }

   /**
    * @return true if any event was found
    */
   public boolean isHasEvent(int artId) {
      return isChanged(artId) || isDeleted(artId) || isRelChange(artId) || isRelDeleted(artId) || isRelAdded(artId);
   }

   public boolean isDeleted(Artifact artifact) {
      return isDeleted(artifact.getArtId());
   }

   public boolean isDeleted(int artId) {
      for (Artifact art : cacheDeletedArtifacts) {
         if (art.getArtId() == artId) return true;
      }
      return false;
   }

   public boolean isChanged(Artifact artifact) {
      return isChanged(artifact.getArtId());
   }

   public boolean isChanged(int artId) {
      for (Artifact art : cacheChangedArtifacts) {
         if (art.getArtId() == artId) return true;
      }
      return false;
   }

   /**
    * Relation rationale or order changed
    * 
    * @param artifact
    */
   public boolean isRelChange(Artifact artifact) {
      return isRelChange(artifact.getArtId());
   }

   /**
    * Relation rationale or order changed
    * 
    * @param artifact
    */
   public boolean isRelChange(int artId) {
      for (Artifact art : cacheRelationChangedArtifacts) {
         if (art.getArtId() == artId) return true;
      }
      return false;
   }

   public boolean isRelDeleted(Artifact artifact) {
      return isRelDeleted(artifact.getArtId());
   }

   public boolean isRelDeleted(int artId) {
      for (Artifact art : cacheRelationDeletedArtifacts) {
         if (art.getArtId() == artId) return true;
      }
      return false;
   }

   public boolean isRelAdded(Artifact artifact) {
      return isRelAdded(artifact.getArtId());
   }

   public boolean isRelAdded(int artId) {
      for (Artifact art : cacheRelationAddedArtifacts) {
         if (art.getArtId() == artId) return true;
      }
      return false;
   }

   /**
    * @return the xModifiedEvents
    */
   public Collection<ArtifactTransactionModifiedEvent> getXModifiedEvents() {
      return xModifiedEvents;
   }

   /**
    * @param modifiedEvents the xModifiedEvents to set
    */
   public void setXModifiedEvents(Collection<ArtifactTransactionModifiedEvent> modifiedEvents) {
      xModifiedEvents = modifiedEvents;
   }

}
