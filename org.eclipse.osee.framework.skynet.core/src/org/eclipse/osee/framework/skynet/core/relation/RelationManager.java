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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactType;
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
   public static void manageRelation(RelationLink relation, RelationSide relationSide) {
      Artifact artifact = ArtifactCache.get(relation.getArtifactId(relationSide), relation.getBranch(relationSide));

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
            if (selectedRelations.get(i).getOrder(relationSide) > relation.getOrder(relationSide)) {
               selectedRelations.add(i, relation);
               return;
            }
         }
         selectedRelations.add(relation);
      }
   }

   private static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationType relationType, RelationSide relationSide) throws ArtifactDoesNotExist, SQLException {
      List<RelationLink> selectedRelations = relations.get(artifact, relationType);
      if (selectedRelations == null) {
         return Collections.emptyList();
      }
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(selectedRelations.size());

      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (!relation.isDeleted()) {
               if (relationSide == null) {
                  artifacts.add(relation.getArtifactOnOtherSide(artifact));
               } else {
                  // only select relations where the related artifact is on the side specified by relationEnum
                  // (and thus on the side opposite of "artifact")
                  if (relation.getSide(artifact) != relationSide) {
                     artifacts.add(relation.getArtifact(relationSide));
                  }
               }
            }
         }
      }
      return artifacts;
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationType relationType) throws ArtifactDoesNotExist, SQLException {
      return getRelatedArtifacts(artifact, relationType, null);
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationEnumeration relationEnum) throws ArtifactDoesNotExist, SQLException {
      return getRelatedArtifacts(artifact, relationEnum.getRelationType(), relationEnum.getSide());
   }

   private static Artifact getRelatedArtifact(Artifact artifact, RelationType relationType, RelationSide relationSide) throws ArtifactDoesNotExist, SQLException, MultipleArtifactsExist {
      List<Artifact> artifacts = getRelatedArtifacts(artifact, relationType, relationSide);

      if (artifacts.size() == 0) {
         throw new ArtifactDoesNotExist(String.format("There is no artifact related to %s by a relation of type %s",
               artifact, relationType));
      }

      if (artifacts.size() > 1) {
         throw new MultipleArtifactsExist(String.format(
               "There are %s artifacts related to %s by a relation of type %s instead of the expected 1.",
               artifacts.size(), artifact, relationType));
      }
      return artifacts.get(0);
   }

   public static Artifact getRelatedArtifact(Artifact artifact, IRelationEnumeration relationEnum) throws ArtifactDoesNotExist, SQLException, MultipleArtifactsExist {
      return getRelatedArtifact(artifact, relationEnum.getRelationType(), relationEnum.getSide());
   }

   public static Artifact getRelatedArtifact(Artifact artifact, RelationType relationType) throws ArtifactDoesNotExist, SQLException, MultipleArtifactsExist {
      return getRelatedArtifact(artifact, relationType, null);
   }

   public static int getRelatedArtifactsCount(Artifact artifact, RelationType relationType, RelationSide relationSide) {
      List<RelationLink> selectedRelations = relations.get(artifact, relationType);

      int artifactCount = 0;
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (!relation.isDeleted()) {
               if (relationSide == null) {
                  artifactCount++;
               } else {
                  // only select relations where the related artifact is on the side specified by relationEnum
                  // (and thus on the side opposite of "artifact")
                  if (relation.getSide(artifact) != relationSide) {
                     artifactCount++;
                  }
               }
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

   public static boolean hasDirtyLinks(Artifact artifact) throws SQLException {
      for (RelationLink relation : artifactToRelations.get(artifact)) {
         if (relation.isDirty()) {
            return true;
         }
      }
      return false;
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
               relationInfo.add(new ObjectPair<Artifact, String>(relation.getArtifactOnOtherSide(artifact),
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
    * @throws SQLException
    */
   public static void addRelation(RelationType relationType, Artifact artifactA, Artifact artifactB, String rationale) throws SQLException {
      ensureRelationCanBeAdded(relationType, artifactA, artifactB);

      RelationLink relation = getLoadedRelation(artifactA, artifactA.getArtId(), artifactB.getArtId(), relationType);

      if (relation == null) {
         relation = new RelationLink(artifactA, artifactB, relationType, rationale);

         relation.setDirty();
         RelationManager.manageRelation(relation, RelationSide.SIDE_A);
         RelationManager.manageRelation(relation, RelationSide.SIDE_B);
      }
   }

   public static void ensureRelationCanBeAdded(RelationType relationType, Artifact artifactA, Artifact artifactB) throws SQLException {
      ensureSideWillSupport(artifactA, relationType, RelationSide.SIDE_A, artifactA.getArtifactType(), 1);
      ensureSideWillSupport(artifactB, relationType, RelationSide.SIDE_B, artifactB.getArtifactType(), 1);
   }

   /**
    * Check whether artifactCount number of additional artifacts of type artifactType can be related to the artifact on
    * side relationSide for relations of type relationType
    * 
    * @param relationType
    * @param relationSide
    * @param artifact
    * @param artifactCount
    * @throws SQLException
    */
   public static void ensureSideWillSupport(Artifact artifact, RelationType relationType, RelationSide relationSide, ArtifactType artifactType, int artifactCount) throws SQLException {
      int maxCount = RelationTypeManager.getRelationSideMax(relationType, artifactType, relationSide);
      int usedCount = getRelatedArtifactsCount(artifact, relationType, relationSide.oppositeSide());

      if (maxCount == 0) {
         throw new IllegalArgumentException(String.format(
               "Artifact \"%s\" of type \"%s\" does not belong on side \"%s\" of relation \"%s\"",
               artifact.getDescriptiveName(), artifact.getArtifactTypeName(), relationType.getSideName(relationSide),
               relationType.getTypeName()));
      } else if (maxCount == 1 && usedCount + artifactCount > maxCount) {
         throw new IllegalArgumentException(
               String.format(
                     "Artifact \"%s\" of type \"%s\" can not be added to \"%s\" of relation \"%s\" because doing so would exceed the side maximum of %d for this artifact type",
                     artifact.getDescriptiveName(), artifact.getArtifactTypeName(), relationSide.toString(),
                     relationType.getTypeName(), maxCount));
      }
   }

   public static void deleteRelation(RelationType relationType, Artifact artifactA, Artifact artifactB) {
      getLoadedRelation(artifactA, artifactA.getArtId(), artifactB.getArtId(), relationType).delete();
   }

   public static void deleteRelations(RelationType relationType, Artifact artifact, RelationSide relationSide) {
      List<RelationLink> selectedRelations = relations.get(artifact, relationType);
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relationSide == null) {
               relation.delete();
            } else {
               if (relation.getSide(artifact) != relationSide) {
                  relation.delete();
               }
            }
         }
      }
   }

   /**
    * Remove all relations stored in the list awaiting to be deleted.
    * 
    * @throws SQLException
    */
   public static void purgeRelationsFor(Artifact artifact) throws SQLException {
      Collection<RelationLink> links = artifactToRelations.get(artifact);
      if (!links.isEmpty()) {
         List<Object[]> batchArgs = new ArrayList<Object[]>(links.size());
         String PURGE_RELATION = "Delete from osee_define_rel_link WHERE rel_link_id = ?";
         for (RelationLink link : links) {
            batchArgs.add(new Object[] {SQL3DataType.INTEGER, link.getRelationId()});
            link.markAsPurged();
         }
         ConnectionHandler.runPreparedUpdateBatch(PURGE_RELATION, batchArgs);
      }
   }
}