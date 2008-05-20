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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;

/**
 * @author Ryan D. Brooks
 */
public class RelationManager {
   // the branch is accounted for because Artifact.equals 
   private static final CompositeKeyHashMap<Artifact, RelationType, List<RelationLink>> relations =
         new CompositeKeyHashMap<Artifact, RelationType, List<RelationLink>>(1024);

   private static final HashMap<Artifact, List<RelationLink>> artifactToRelations =
         new HashMap<Artifact, List<RelationLink>>(1024);

   private static RelationLink getLoadedRelation(Artifact artifact, int aArtifactId, int bArtifactId, RelationType relationType) {
      List<RelationLink> selectedRelations = relations.get(artifact, relationType);
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relation.getAArtifactId() == aArtifactId && relation.getBArtifactId() == bArtifactId) {
               return relation;
            }
         }
      }
      return null;
   }

   /**
    * This method should never be called by application code.
    * 
    * @param relationType
    * @param aArtifactId
    * @param bArtifactId
    * @param aBranch
    * @param bBranch
    * @return
    */
   public static RelationLink getLoadedRelation(RelationType relationType, int aArtifactId, int bArtifactId, Branch aBranch, Branch bBranch) {
      Artifact artifactA = ArtifactCache.get(aArtifactId, aBranch);
      Artifact artifactB = ArtifactCache.get(bArtifactId, bBranch);

      RelationLink relation = null;
      if (artifactA != null) {
         relation = getLoadedRelation(artifactA, aArtifactId, bArtifactId, relationType);
      }
      if (artifactB != null && relation == null) {
         relation = getLoadedRelation(artifactB, aArtifactId, bArtifactId, relationType);
      }
      return relation;
   }

   /**
    * Store the newly instantiated relation from the perspective of sideA in its appropriate order
    * 
    * @param relation
    * @param sideA
    */
   public static void manageRelation(RelationLink relation, boolean sideA) {
      Artifact artifact = ArtifactCache.get(relation.getArtifactId(sideA), relation.getBranch(sideA));

      if (artifact != null) {
         List<RelationLink> artifactsRelations = artifactToRelations.get(artifact);
         if (artifactsRelations == null) {
            artifactsRelations = Collections.synchronizedList(new ArrayList<RelationLink>(4));
            artifactToRelations.put(artifact, artifactsRelations);
         }
         artifactsRelations.add(relation);

         List<RelationLink> selectedRelations = relations.get(artifact, relation.getRelationType());
         if (selectedRelations == null) {
            selectedRelations = Collections.synchronizedList(new ArrayList<RelationLink>(4));
            relations.put(artifact, relation.getRelationType(), selectedRelations);
         }
         for (int i = 0; i < selectedRelations.size(); i++) {
            if (selectedRelations.get(i).getOrder(sideA) > relation.getOrder(sideA)) {
               selectedRelations.add(i, relation);
               return;
            }
         }
         selectedRelations.add(relation);
      }
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationType relationType) throws ArtifactDoesNotExist, SQLException {
      List<RelationLink> selectedRelations = relations.get(artifact, relationType);
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(selectedRelations.size());

      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (!relation.isDeleted()) {
               boolean otherSide = !relation.isOnSideA(artifact);
               artifacts.add(relation.getArtifact(otherSide));
            }
         }
      }
      return artifacts;
   }

   public static boolean hasDirtyLinks(Artifact artifact) throws SQLException {
      for (RelationLink relation : artifactToRelations.get(artifact)) {
         if (relation.isDirty()) {
            return true;
         }
      }
      return false;
   }

   public static Artifact getRelatedArtifact(Artifact artifact, RelationType relationType) throws ArtifactDoesNotExist, SQLException, MultipleArtifactsExist {
      List<RelationLink> selectedRelations = relations.get(artifact, relationType);

      if (selectedRelations == null) {
         throw new ArtifactDoesNotExist(
               "There is not an artifact related to " + artifact + " by a relation of type " + relationType);
      }

      List<RelationLink> trimmedRelations = new ArrayList<RelationLink>(selectedRelations.size());
      for (RelationLink relation : selectedRelations) {
         if (!relation.isDeleted()) {
            trimmedRelations.add(relation);
         }
      }

      if (trimmedRelations.size() > 1) {
         throw new MultipleArtifactsExist(
               "There are " + trimmedRelations.size() + " artifacts related to " + artifact + " by a relation of type " + relationType + " instead of the expected 1.");
      }
      RelationLink relation = trimmedRelations.get(0);
      boolean otherSide = !relation.isOnSideA(artifact);
      return relation.getArtifact(otherSide);
   }

   public static int getRelatedArtifactsCount(Artifact artifact, RelationType relationType) {
      List<RelationLink> selectedRelations = relations.get(artifact, relationType);

      int artifactCount = 0;
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (!relation.isDeleted()) {
               artifactCount++;
            }
         }
      }

      return artifactCount;
   }

   /**
    * @param artifact
    */
   public static void revertRelationsFor(Artifact artifact) {
      throw new UnsupportedOperationException();
   }

   public static void persistRelationsFor(Artifact artifact) throws SQLException {
      for (RelationLink relation : artifactToRelations.get(artifact)) {
         if (relation.isDirty()) {
            RelationPersistenceManager.makePersistent(relation);
         }
      }
   }

   public static List<ObjectPair<Artifact, String>> getRelations(Artifact artifact, RelationType relationType) throws ArtifactDoesNotExist, SQLException {
      List<RelationLink> selectedRelations = relations.get(artifact, relationType);
      List<ObjectPair<Artifact, String>> relationInfo =
            new ArrayList<ObjectPair<Artifact, String>>(selectedRelations.size());

      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (!relation.isDeleted()) {
               boolean otherSide = !relation.isOnSideA(artifact);
               relationInfo.add(new ObjectPair<Artifact, String>(relation.getArtifact(otherSide),
                     relation.getRationale()));
            }
         }
      }
      return relationInfo;
   }

   /**
    * @param relationType
    * @param artifactA
    * @param artifactB
    * @param rationale
    */
   public static void addRelation(RelationType relationType, Artifact artifactA, Artifact artifactB, String rationale) {
      RelationLink relation = getLoadedRelation(artifactA, artifactA.getArtId(), artifactB.getArtId(), relationType);

      if (relation == null) {
         relation = new RelationLink(artifactA, artifactB, relationType, rationale);

         relation.setDirty();
         RelationManager.manageRelation(relation, true);
         RelationManager.manageRelation(relation, false);
      }
   }

   public static void deleteRelation(RelationType relationType, Artifact artifactA, Artifact artifactB) {

   }
}