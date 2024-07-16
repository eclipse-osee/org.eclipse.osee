/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationFilterUtil.RelationMatcher;

/**
 * @author Roberto E. Escobar
 */
public class RelationCache {
   private final CompositeKeyHashMap<ArtifactToken, RelationTypeToken, List<RelationLink>> relationsByType =
      new CompositeKeyHashMap<>(1024, true);

   public void deCache(ArtifactToken artifact) {
      Collection<List<RelationLink>> removeValues = relationsByType.removeValues(artifact);

      if (removeValues != null) {
         for (List<RelationLink> relations : removeValues) {
            for (RelationLink relation : relations) {
               removeSingleRelation(artifact, relation);
            }
         }
      }
   }

   private void removeSingleRelation(ArtifactToken artifact, RelationLink relation) {
      ArtifactToken otherArtifact = relation.getOtherSideArtifact(artifact);
      List<RelationLink> relations = relationsByType.get(otherArtifact, relation.getRelationType());
      if (relations != null) {
         relations.remove(relation);
      }
   }

   public void cache(ArtifactToken artifact, RelationLink newRelation) {
      RelationTypeToken relationType = newRelation.getRelationType();
      List<RelationLink> selectedRelations = getAllByType(artifact, relationType);
      if (selectedRelations == null) {
         selectedRelations = new CopyOnWriteArrayList<>();
         relationsByType.put(artifact, relationType, selectedRelations);
      }
      if (selectedRelations.contains(newRelation)) {
         OseeLog.logf(Activator.class, Level.SEVERE,
            "Duplicate relationByType objects for same relation for Relation [%s] Artifact [%s]", newRelation,
            artifact);
      }
      selectedRelations.add(newRelation);
   }

   public List<RelationLink> getAll(ArtifactToken artifact) {
      return getRelations(artifact, DeletionFlag.INCLUDE_DELETED);
   }

   public List<RelationLink> getAllByType(ArtifactToken artifact, RelationTypeToken relationType) {
      return relationsByType.get(artifact, relationType);
   }

   public List<RelationLink> getRelations(ArtifactToken artifact, DeletionFlag deletionFlag) {
      List<RelationLink> linksFound = new ArrayList<>();
      RelationMatcher matcher = RelationFilterUtil.createMatcher(deletionFlag);
      findRelations(linksFound, artifact, matcher);
      return linksFound;
   }

   private void findRelations(Collection<RelationLink> linksFound, ArtifactToken artifact, RelationTypeToken relationType, RelationMatcher matcher) {
      List<RelationLink> sourceLink = relationsByType.get(artifact, relationType);
      RelationFilterUtil.filter(sourceLink, linksFound, matcher);
   }

   private void findRelations(Collection<RelationLink> linksFound, ArtifactToken artifact, RelationMatcher matcher) {
      List<List<RelationLink>> values = relationsByType.getValues(artifact);
      if (values != null) {
         for (List<RelationLink> linksSource : values) {
            RelationFilterUtil.filter(linksSource, linksFound, matcher);
         }
      }
   }

   /**
    * Find RelationById Related On ArtA or ArtB
    */
   public RelationLink getByRelIdOnArtifact(RelationId relLinkId, ArtifactId aArtifactId, ArtifactId bArtifactId, BranchToken branch) {
      RelationMatcher relIdMatcher = RelationFilterUtil.createFindFirstRelationLinkIdMatcher(relLinkId);
      List<RelationLink> links = new ArrayList<>();
      findRelations(links, ArtifactToken.valueOf(aArtifactId, branch), relIdMatcher);
      if (links.isEmpty()) {
         findRelations(links, ArtifactToken.valueOf(bArtifactId, branch), relIdMatcher);
      }
      return links.isEmpty() ? null : links.iterator().next();
   }

   public RelationLink getLoadedRelation(ArtifactToken artifact, ArtifactId aArtifactId, ArtifactId bArtifactId, RelationTypeToken relationType, DeletionFlag deletionFlag) {
      return getLoadedRelation(artifact, aArtifactId.getId(), bArtifactId.getId(), relationType, deletionFlag);
   }

   public RelationLink getLoadedRelation(ArtifactToken artifact, long aArtifactId, long bArtifactId, RelationTypeToken relationType, DeletionFlag deletionFlag) {
      Set<RelationLink> itemsFound = new HashSet<>();

      RelationMatcher artIdMatcher = new RelationMatcher() {

         @Override
         public boolean matches(RelationLink relation) {
            return relation.getArtifactIdA().equals(artifact) || relation.getArtifactIdB().equals(artifact);
         }

         @Override
         public boolean isFindNextAllowed() {
            return true;
         }
      };

      RelationMatcher matcher = RelationFilterUtil.createMatcher(deletionFlag, artIdMatcher);
      findRelations(itemsFound, artifact, relationType, matcher);

      List<RelationLink> relations = new ArrayList<>();
      for (RelationLink relation : itemsFound) {
         if (relation.getArtifactIdA().equals(aArtifactId) && relation.getArtifactIdB().equals(bArtifactId)) {
            relations.add(relation);
         }
      }
      int size = relations.size();
      if (size > 1) {
         OseeLog.logf(Activator.class, Level.SEVERE,
            "Artifact A [%s] has [%d] relations of same type [%s] to Artifact B [%s]", artifact, relations.size(),
            relationType, bArtifactId);
      }
      return size != 0 ? relations.iterator().next() : null;
   }

   public RelationLink getLoadedRelation(RelationTypeToken relationType, int aArtifactId, int bArtifactId, BranchId branch) {
      ArtifactId artifactA = ArtifactId.valueOf(aArtifactId);
      ArtifactId artifactB = ArtifactId.valueOf(bArtifactId);

      RelationMatcher bArtIdMatcher =
         RelationFilterUtil.createFindFirstRelatedArtIdMatcher(artifactB, RelationSide.SIDE_B);
      List<RelationLink> links = new ArrayList<>();
      findRelations(links, ArtifactToken.valueOf(aArtifactId, branch), relationType, bArtIdMatcher);
      if (links.isEmpty()) {
         RelationMatcher aArtIdMatcher =
            RelationFilterUtil.createFindFirstRelatedArtIdMatcher(artifactA, RelationSide.SIDE_A);
         findRelations(links, ArtifactToken.valueOf(bArtifactId, branch), relationType, aArtIdMatcher);
      }
      return links.isEmpty() ? null : links.iterator().next();
   }
}