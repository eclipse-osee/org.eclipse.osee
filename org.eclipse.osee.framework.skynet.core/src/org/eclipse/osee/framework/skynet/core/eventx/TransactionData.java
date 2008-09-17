/*
 * Created on Sep 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.eventx;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * @author Donald G. Dunne
 */
public class TransactionData {

   // artifact cache of artifacts based on artifactModType
   public Set<Artifact> cacheChangedArtifacts = new HashSet<Artifact>();
   public Set<Artifact> cacheDeletedArtifacts = new HashSet<Artifact>();
   public Set<Artifact> cacheAddedArtifacts = new HashSet<Artifact>();

   // collection of unloaded artifact changes where UnloadedArtifact contains artifact id, branch and artifact type id 
   public Set<UnloadedArtifact> unloadedChangedArtifacts = new HashSet<UnloadedArtifact>();
   public Set<UnloadedArtifact> unloadedDeletedArtifacts = new HashSet<UnloadedArtifact>();
   public Set<UnloadedArtifact> unloadedAddedArtifacts = new HashSet<UnloadedArtifact>();

   // cacheRelations are relations where one side artifact is already in cache
   public Set<LoadedRelation> cacheChangedRelations = new HashSet<LoadedRelation>();
   public Set<LoadedRelation> cacheAddedRelations = new HashSet<LoadedRelation>();
   public Set<LoadedRelation> cacheDeletedRelations = new HashSet<LoadedRelation>();

   // unloadedRelations are relations where neither side artifact is loaded; normally don't care about these
   public Set<UnloadedRelation> unloadedChangedRelations = new HashSet<UnloadedRelation>();
   public Set<UnloadedRelation> unloadedAddedRelations = new HashSet<UnloadedRelation>();
   public Set<UnloadedRelation> unloadedDeletedRelations = new HashSet<UnloadedRelation>();

   // artifact cache of all loaded artifacts on either side of a relation mod
   public Set<Artifact> cacheRelationChangedArtifacts = new HashSet<Artifact>();
   public Set<Artifact> cacheRelationDeletedArtifacts = new HashSet<Artifact>();
   public Set<Artifact> cacheRelationAddedArtifacts = new HashSet<Artifact>();

   /**
    * Return artifacts related to this artifact from event service loadedRelations collection. This will bulk load all
    * opposite-side artifacts if they are not already loaded.
    * 
    * @param cacheRelations
    * @return
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
            artifacts.addAll(ArtifactQuery.getArtifactsFromIds(artifactIds,
                  BranchPersistenceManager.getBranch(branchId), false));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
      return artifacts;
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
      return isChanged(artId) || isRelChange(artId) || isDeleted(artId);
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

   public boolean isRelChange(Artifact artifact) {
      return isRelChange(artifact.getArtId());
   }

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

}
