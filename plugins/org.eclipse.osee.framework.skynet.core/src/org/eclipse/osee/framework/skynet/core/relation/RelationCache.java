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
package org.eclipse.osee.framework.skynet.core.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactKey;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationFilterUtil.RelationMatcher;

/**
 * @author Roberto E. Escobar
 */
public class RelationCache {
   private static final ThreadLocal<ArtifactKey> THREAD_SHARED_KEY = new ThreadLocal<ArtifactKey>() {

      @Override
      protected ArtifactKey initialValue() {
         return new ArtifactKey();
      }
   };

   // Indexed by ArtifactKey so that map does not hold strong reference to artifact which allows it to be garbage collected
   // the branch is accounted for because artifact key includes the branch uuid
   private final CompositeKeyHashMap<ArtifactKey, RelationTypeId, List<RelationLink>> relationsByType =
      new CompositeKeyHashMap<>(1024, true);

   private ArtifactKey getKey(ArtifactToken artifact) {
      ArtifactKey key = THREAD_SHARED_KEY.get();
      return key.setKey(artifact);
   }

   private ArtifactKey getKey(long artId, BranchId branchUuid) {
      ArtifactKey key = THREAD_SHARED_KEY.get();
      return key.setKey(artId, branchUuid);
   }

   public void deCache(ArtifactToken artifact) {
      ArtifactKey key = getKey(artifact);
      Collection<List<RelationLink>> removeValues = relationsByType.removeValues(key);

      if (removeValues != null) {
         for (List<RelationLink> relations : removeValues) {
            for (RelationLink relation : relations) {
               removeSingleRelation(artifact, relation);
            }
         }
      }
   }

   private void removeSingleRelation(ArtifactToken artifact, RelationLink relation) {
      ArtifactId otherArtifact = relation.getOtherSideArtifact(artifact);
      ArtifactKey key = getKey(otherArtifact.getId(), relation.getBranch());
      List<RelationLink> relations = relationsByType.get(key, relation.getRelationType());
      if (relations != null) {
         relations.remove(relation);
      }
   }

   public void cache(ArtifactToken artifact, RelationLink newRelation) {
      RelationTypeId relationType = newRelation.getRelationType();
      List<RelationLink> selectedRelations = getAllByType(artifact, relationType);
      if (selectedRelations == null) {
         selectedRelations = new CopyOnWriteArrayList<>();
         relationsByType.put(new ArtifactKey(artifact), relationType, selectedRelations);
      }
      if (selectedRelations.contains(newRelation)) {
         OseeLog.logf(Activator.class, Level.SEVERE,
            "Duplicate relationByType objects for same relation for Relation [%s] Artifact (%s)[%s]", newRelation,
            artifact.getId(), artifact.getName());
      }
      selectedRelations.add(newRelation);
   }

   public List<RelationLink> getAll(ArtifactToken artifact) {
      return getRelations(artifact, DeletionFlag.INCLUDE_DELETED);
   }

   public List<RelationLink> getAllByType(ArtifactToken artifact, RelationTypeId relationType) {
      ArtifactKey key = getKey(artifact);
      return relationsByType.get(key, relationType);
   }

   public List<RelationLink> getRelations(ArtifactToken artifact, DeletionFlag deletionFlag) {
      ArtifactKey key = getKey(artifact);
      List<RelationLink> linksFound = new ArrayList<>();
      RelationMatcher matcher = RelationFilterUtil.createMatcher(deletionFlag);
      findRelations(linksFound, key, matcher);
      return linksFound;
   }

   private void findRelations(Collection<RelationLink> linksFound, long artId, BranchId branchUuid, RelationTypeId relationType, RelationMatcher matcher) {
      List<RelationLink> sourceLink = relationsByType.get(getKey(artId, branchUuid), relationType);
      RelationFilterUtil.filter(sourceLink, linksFound, matcher);
   }

   private void findRelations(Collection<RelationLink> linksFound, long artId, BranchId branchUuid, RelationMatcher matcher) {
      ArtifactKey artifactKey = getKey(artId, branchUuid);
      findRelations(linksFound, artifactKey, matcher);
   }

   private void findRelations(Collection<RelationLink> linksFound, ArtifactKey artifactKey, RelationMatcher matcher) {
      List<List<RelationLink>> values = relationsByType.getValues(artifactKey);
      if (values != null) {
         for (List<RelationLink> linksSource : values) {
            RelationFilterUtil.filter(linksSource, linksFound, matcher);
         }
      }
   }

   /**
    * Find RelationById Related On ArtA or ArtB
    */
   public RelationLink getByRelIdOnArtifact(int relLinkId, int aArtifactId, int bArtifactId, BranchId branch) {
      RelationMatcher relIdMatcher = RelationFilterUtil.createFindFirstRelationLinkIdMatcher(relLinkId);
      List<RelationLink> links = new ArrayList<>();
      findRelations(links, aArtifactId, branch, relIdMatcher);
      if (links.isEmpty()) {
         findRelations(links, bArtifactId, branch, relIdMatcher);
      }
      return links.isEmpty() ? null : links.iterator().next();
   }

   public RelationLink getLoadedRelation(ArtifactToken artifact, ArtifactId aArtifactId, ArtifactId bArtifactId, RelationTypeId relationType, DeletionFlag deletionFlag) {
      return getLoadedRelation(artifact, aArtifactId.getId(), bArtifactId.getId(), relationType, deletionFlag);
   }

   public RelationLink getLoadedRelation(ArtifactToken artifact, long aArtifactId, long bArtifactId, RelationTypeId relationType, DeletionFlag deletionFlag) {
      Set<RelationLink> itemsFound = new HashSet<>();

      RelationMatcher artIdMatcher = new RelationMatcher() {

         @Override
         public boolean matches(RelationLink relationLink) {
            return relationLink.getArtifactIdA().equals(artifact) || relationLink.getArtifactIdB().equals(artifact);
         }

         @Override
         public boolean isFindNextAllowed() {
            return true;
         }
      };

      RelationMatcher matcher = RelationFilterUtil.createMatcher(deletionFlag, artIdMatcher);
      findRelations(itemsFound, artifact.getId(), artifact.getBranch(), relationType, matcher);

      List<RelationLink> relations = new ArrayList<>();
      for (RelationLink relation : itemsFound) {
         if (relation.getArtifactIdA().equals(aArtifactId) && relation.getArtifactIdB().equals(bArtifactId)) {
            relations.add(relation);
         }
      }
      int size = relations.size();
      if (size > 1) {
         OseeLog.logf(Activator.class, Level.SEVERE,
            "Artifact A [%s] has [%d] relations of same type [%s] to Artifact B [%s]", artifact.getId(),
            relations.size(), relationType, bArtifactId);
      }
      return size != 0 ? relations.iterator().next() : null;
   }

   public RelationLink getLoadedRelation(RelationTypeId relationType, int aArtifactId, int bArtifactId, BranchId branch) {
      ArtifactId artifactA = ArtifactId.valueOf(aArtifactId);
      ArtifactId artifactB = ArtifactId.valueOf(bArtifactId);

      RelationMatcher bArtIdMatcher =
         RelationFilterUtil.createFindFirstRelatedArtIdMatcher(artifactB, RelationSide.SIDE_B);
      List<RelationLink> links = new ArrayList<>();
      findRelations(links, aArtifactId, branch, relationType, bArtIdMatcher);
      if (links.isEmpty()) {
         RelationMatcher aArtIdMatcher =
            RelationFilterUtil.createFindFirstRelatedArtIdMatcher(artifactA, RelationSide.SIDE_A);
         findRelations(links, bArtifactId, branch, relationType, aArtIdMatcher);
      }
      return links.isEmpty() ? null : links.iterator().next();
   }

}
