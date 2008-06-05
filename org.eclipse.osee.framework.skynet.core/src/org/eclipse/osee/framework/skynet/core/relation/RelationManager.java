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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.skynet.core.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;

/**
 * @author Ryan D. Brooks
 */
public class RelationManager {
   // the branch is accounted for because Artifact.equals includes the branch in the comparison
   private static final CompositeKeyHashMap<Artifact, RelationType, List<RelationLink>> relationsByType =
         new CompositeKeyHashMap<Artifact, RelationType, List<RelationLink>>(1024);

   private static final HashMap<Artifact, List<RelationLink>> artifactToRelations =
         new HashMap<Artifact, List<RelationLink>>(1024);

   private static final int LINKED_LIST_KEY = -1;

   private static RelationLink getLoadedRelation(Artifact artifact, int aArtifactId, int bArtifactId, RelationType relationType) {
      List<RelationLink> selectedRelations = relationsByType.get(artifact, relationType);
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (!relation.isDeleted() && relation.getAArtifactId() == aArtifactId && relation.getBArtifactId() == bArtifactId) {
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
      Artifact artifactA = ArtifactCache.getActive(aArtifactId, aBranch);
      Artifact artifactB = ArtifactCache.getActive(bArtifactId, bBranch);

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
    * Store the newly instantiated relation from the perspective of relationSide in its appropriate order
    * 
    * @param relation
    * @param relationSide
    */
   public static void manageRelation(RelationLink relation, RelationSide relationSide) {
      Artifact artifact =
            ArtifactCache.getActive(relation.getArtifactId(relationSide), relation.getBranch(relationSide));

      if (artifact != null && (!artifact.isLinksLoaded() || !relation.isInDb())) {
         List<RelationLink> artifactsRelations = artifactToRelations.get(artifact);
         if (artifactsRelations == null) {
            artifactsRelations = Collections.synchronizedList(new ArrayList<RelationLink>(4));
            artifactToRelations.put(artifact, artifactsRelations);
         }
         if (artifactsRelations.contains(relation)) {
            System.out.printf("%s  Rel: %d, artA: %d, artB: %d, \n", relation.getRelationType().getTypeName(),
                  relation.getRelationId(), relation.getAArtifactId(), relation.getBArtifactId());
            return;
         }

         artifactsRelations.add(relation);

         List<RelationLink> selectedRelations = relationsByType.get(artifact, relation.getRelationType());
         if (selectedRelations == null) {
            selectedRelations = Collections.synchronizedList(new ArrayList<RelationLink>(4));
            relationsByType.put(artifact, relation.getRelationType(), selectedRelations);
         }
         selectedRelations.add(relation);
      }
   }

   private static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationType relationType, RelationSide relationSide) throws ArtifactDoesNotExist, SQLException {
      List<RelationLink> selectedRelations = null;
      if (relationType == null) {
         selectedRelations = artifactToRelations.get(artifact);
      } else {
         selectedRelations = relationsByType.get(artifact, relationType);
      }
      if (selectedRelations == null) {
         return Collections.emptyList();
      }
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(selectedRelations.size());

      if (needsBulkLoad(selectedRelations, artifact, relationSide)) {
         if (relationSide == null) {
            ArtifactQuery.getRelatedArtifacts(artifact, relationType, RelationSide.SIDE_A);
            ArtifactQuery.getRelatedArtifacts(artifact, relationType, RelationSide.SIDE_B);
         } else {
            ArtifactQuery.getRelatedArtifacts(artifact, relationType, relationSide);
         }
      }

      for (RelationLink relation : selectedRelations) {
         if (!relation.isDeleted()) {
            if (relationSide == null) {
               artifacts.add(relation.getArtifactOnOtherSide(artifact));
            } else {
               // only select relations where the related artifact is on relationSide
               // (and thus on the side opposite of "artifact")
               if (relation.getSide(artifact) != relationSide) {
                  artifacts.add(relation.getArtifact(relationSide));
               }
            }
         }
      }
      return artifacts;
   }

   private static void addRelatedArtifactIds(int queryId, Artifact artifact, Collection<Artifact> relatedArtifacts, CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters, List<RelationLink> relations, RelationSide side) {
      if (relations == null) {
         return;
      }
      for (RelationLink relation : relations) {
         if (!relation.isDeleted()) {
            RelationSide resolvedSide = null;
            if (side == RelationSide.OPPOSITE) {
               resolvedSide = relation.getSide(artifact).oppositeSide();
            } else {
               if (relation.getSide(artifact) != side) {
                  resolvedSide = side;
               }
            }
            if (resolvedSide != null) {
               int artId = relation.getArtifactId(resolvedSide);
               int branchId = relation.getBranch(resolvedSide).getBranchId();
               Artifact relatedArtifact = ArtifactCache.getActive(artId, branchId);
               if (relatedArtifact == null) {
                  insertParameters.put(artId, branchId, new Object[] {SQL3DataType.INTEGER, queryId,
                        SQL3DataType.INTEGER, artId, SQL3DataType.INTEGER, branchId});
               } else {
                  relatedArtifacts.add(relatedArtifact);
               }
            }
         }
      }
   }

   public static Set<Artifact> getRelatedArtifacts(Collection<Artifact> artifacts, int depth, IRelationEnumeration... relationTypes) throws SQLException {
      int queryId = ArtifactLoader.getNewQueryId();
      CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters =
            new CompositeKeyHashMap<Integer, Integer, Object[]>(artifacts.size() * 8);
      Set<Artifact> relatedArtifacts = new HashSet<Artifact>(insertParameters.size());
      Collection<Artifact> newArtifacts = artifacts;
      for (int i = 0; i < depth && newArtifacts.size() > 0; i++) {

         insertParameters.clear();
         for (Artifact artifact : newArtifacts) {
            List<RelationLink> selectedRelations = null;
            if (relationTypes.length == 0) {
               selectedRelations = artifactToRelations.get(artifact);
               addRelatedArtifactIds(queryId, artifact, relatedArtifacts, insertParameters, selectedRelations,
                     RelationSide.OPPOSITE);
            } else {
               for (IRelationEnumeration relationEnum : relationTypes) {
                  selectedRelations = relationsByType.get(artifact, relationEnum.getRelationType());
                  addRelatedArtifactIds(queryId, artifact, relatedArtifacts, insertParameters, selectedRelations,
                        relationEnum.getSide());
               }
            }
         }

         if (insertParameters.size() > 0) {
            ArtifactLoader.selectArtifacts(new ArrayList<Object[]>(insertParameters.values()));
            newArtifacts =
                  ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters.size(), false);
            ArtifactLoader.clearQuery(queryId);
            relatedArtifacts.addAll(newArtifacts);
         }
      }
      return relatedArtifacts;
   }

   private static boolean needsBulkLoad(List<RelationLink> selectedRelations, Artifact artifact, RelationSide relationSide) throws ArtifactDoesNotExist, SQLException {
      for (RelationLink relation : selectedRelations) {
         if (!relation.isDeleted()) {
            if (relationSide == null) {
               Artifact temp = relation.getArtifactOnOtherSideIfLoaded(artifact);
               if (temp == null) {
                  return true;
               }
            } else {
               if (relation.getSide(artifact) != relationSide) {
                  Artifact temp = relation.getArtifactIfLoaded(relationSide);
                  if (temp == null) {
                     return true;
                  }
               }
            }
         }
      }
      return false;
   }

   public static List<Artifact> getRelatedArtifactsAll(Artifact artifact) throws ArtifactDoesNotExist, SQLException {
      return getRelatedArtifacts(artifact, null, null);
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
         throw new MultipleArtifactsExist(
               String.format(
                     "There are %s artifacts related to \"%s\" by a relation of type \"%s\" on side %s instead of the expected 1.",
                     artifacts.size(), artifact, relationType, relationSide.toString()));
      }
      return artifacts.get(0);
   }

   public static Artifact getRelatedArtifact(Artifact artifact, IRelationEnumeration relationEnum) throws ArtifactDoesNotExist, SQLException, MultipleArtifactsExist {
      return getRelatedArtifact(artifact, relationEnum.getRelationType(), relationEnum.getSide());
   }

   public static int getRelatedArtifactsCount(Artifact artifact, RelationType relationType, RelationSide relationSide) {
      List<RelationLink> selectedRelations = relationsByType.get(artifact, relationType);

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
    * @throws SQLException
    * @deprecated
    */
   public static void revertRelationsFor(Artifact artifact) throws SQLException {
      //This is inappropriate to use as references held to links by other applications will continue to exist.
      artifactToRelations.remove(artifact);
      for (RelationType type : RelationTypeManager.getValidTypes(artifact.getArtifactType(), artifact.getBranch())) {
         relationsByType.remove(artifact, type);
      }
   }

   public static boolean hasDirtyLinks(Artifact artifact) {
      List<RelationLink> selectedRelations = artifactToRelations.get(artifact);
      if (selectedRelations == null) {
         return false;
      }
      for (RelationLink relation : selectedRelations) {
         if (relation.isDirty() && !relation.isDeleted()) {
            return true;
         }
      }
      return false;
   }

   /**
    * @param artifact
    * @param relationType if not null persists the relations of this type, otherwise persists relations of all types
    * @throws SQLException
    */
   public static void persistRelationsFor(Artifact artifact, RelationType relationType) throws SQLException {
      List<RelationLink> selectedRelations;
      if (relationType == null) {
         selectedRelations = artifactToRelations.get(artifact);
      } else {
         selectedRelations = relationsByType.get(artifact, relationType);
      }

      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relation.isDirty()) {
               RelationPersistenceManager.makePersistent(relation);
            }
         }
      }
   }

   public static List<RelationLink> getRelationsAll(Artifact artifact) {
      List<RelationLink> selectedRelations = artifactToRelations.get(artifact);

      if (selectedRelations == null) {
         return Collections.emptyList();
      }

      List<RelationLink> relations = new ArrayList<RelationLink>(selectedRelations.size());
      for (RelationLink relation : selectedRelations) {
         if (!relation.isDeleted()) {
            relations.add(relation);
         }
      }
      return relations;
   }

   public static List<RelationLink> getRelations(Artifact artifact, RelationType relationType, RelationSide relationSide) {
      List<RelationLink> selectedRelations = relationsByType.get(artifact, relationType);
      if (selectedRelations == null) {
         return Collections.emptyList();
      }

      List<RelationLink> relations = new ArrayList<RelationLink>(selectedRelations.size());

      for (RelationLink relation : selectedRelations) {
         if (!relation.isDeleted()) {
            if (relationSide == null) {
               relations.add(relation);
            } else {
               // only select relations where the related artifact is on the side specified by relationEnum
               // (and thus on the side opposite of "artifact")
               if (relation.getSide(artifact) != relationSide) {
                  relations.add(relation);
               }
            }
         }
      }
      return relations;
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

         setDefaultRelationOrder(relation, artifactA, artifactB);

         RelationManager.manageRelation(relation, RelationSide.SIDE_A);
         RelationManager.manageRelation(relation, RelationSide.SIDE_B);
      }
      SkynetEventManager.getInstance().kick(
            new CacheRelationModifiedEvent(relation, relation.getABranch(), relation.getRelationType().getTypeName(),
                  relation.getASideName(), ModType.Added, RelationManager.class));
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

   public static void deleteRelationsAll(Artifact artifact) {
      List<RelationLink> selectedRelations = artifactToRelations.get(artifact);
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            relation.delete();
         }
      }
   }

   public static void deleteRelations(Artifact artifact, RelationType relationType, RelationSide relationSide) {
      List<RelationLink> selectedRelations = relationsByType.get(artifact, relationType);
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

   /**
    * @param targetLink
    * @param dropLink
    * @throws SQLException
    */
   private static void addRelationAndModifyOrder(Artifact sourceArtifact, Artifact movedArtifact, RelationLink targetLink, boolean infront) throws SQLException {

      RelationSide side = targetLink.getSide(sourceArtifact);
      Artifact artA = null;
      Artifact artB = null;
      if (RelationSide.SIDE_A == side) {
         artA = sourceArtifact;
         artB = movedArtifact;
      } else {
         artA = movedArtifact;
         artB = sourceArtifact;
      }

      RelationLink relationToModify =
            getLoadedRelation(targetLink.getRelationType(), artA.getArtId(), artB.getArtId(), artA.getBranch(),
                  artB.getBranch());
      if (relationToModify == null) {
         RelationManager.addRelation(targetLink.getRelationType(), artA, artB, "");
         relationToModify =
               getLoadedRelation(targetLink.getRelationType(), artA.getArtId(), artB.getArtId(), artA.getBranch(),
                     artB.getBranch());
      }
      if (relationToModify == targetLink) {
         return;
      }
      List<RelationLink> selectedRelations = relationsByType.get(sourceArtifact, targetLink.getRelationType());
      selectedRelations.remove(relationToModify);
      selectedRelations.add(
            infront ? selectedRelations.indexOf(targetLink) : selectedRelations.indexOf(targetLink) + 1,
            relationToModify);

      int lastArtId = LINKED_LIST_KEY;
      for (RelationLink link : selectedRelations) {
         if (!link.isDeleted() && link.getSide(sourceArtifact) == side) {
            if (link.getOrder(side.oppositeSide()) != lastArtId) {
               link.setOrder(side.oppositeSide(), lastArtId);
            }
            lastArtId = link.getArtifactId(side.oppositeSide());
         }
      }
      SkynetEventManager.getInstance().kick(
            new CacheArtifactModifiedEvent(sourceArtifact,
                  org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType.Changed, null));
   }

   /**
    * @param targetLink
    * @param dropLink
    * @throws SQLException
    */
   public static void addRelationAndModifyOrder(Artifact parentArtifact, Artifact targetArtifact, Artifact[] movedArts, RelationType type, boolean infront) throws SQLException {
      RelationLink targetRelation =
            getLoadedRelation(parentArtifact, parentArtifact.getArtId(), targetArtifact.getArtId(), type);
      if (targetRelation == null) {
         targetRelation = getLoadedRelation(parentArtifact, targetArtifact.getArtId(), parentArtifact.getArtId(), type);
         if (targetRelation == null) {
            throw new IllegalArgumentException(
                  String.format(
                        "Unable to locate a valid relation using that has [%s] on one side and [%s] on the other of type [%s]",
                        parentArtifact.toString(), targetArtifact.toString(), type.toString()));
         }
      }

      for (int i = movedArts.length - 1; i >= 0; i--) {
         addRelationAndModifyOrder(parentArtifact, movedArts[i], targetRelation, infront);
      }
   }

   /**
    * @param sideA
    * @param targetArtifact
    * @param insertAfterTarget
    * @param relationType
    * @param artifactA
    * @param artifactB
    * @param rationale
    * @throws SQLException
    * @throws OseeCoreException
    */
   public static void addRelation(Artifact artifactATarget, boolean insertAfterATarget, Artifact artifactBTarget, boolean insertAfterBTarget, RelationType relationType, Artifact artifactA, Artifact artifactB, String rationale) throws SQLException, OseeCoreException {

      ensureRelationCanBeAdded(relationType, artifactA, artifactB);

      RelationLink relation = getLoadedRelation(artifactA, artifactA.getArtId(), artifactB.getArtId(), relationType);

      if (relation == null) {
         relation = new RelationLink(artifactA, artifactB, relationType, rationale);
         relation.setDirty();

         setDefaultRelationOrder(relation, artifactA, artifactB);

         RelationManager.manageRelation(relation, RelationSide.SIDE_A);
         RelationManager.manageRelation(relation, RelationSide.SIDE_B);

         setRelationOrdering(RelationSide.SIDE_B, relation, artifactBTarget, insertAfterBTarget, artifactA, artifactA,
               artifactBTarget);
         setRelationOrdering(RelationSide.SIDE_A, relation, artifactATarget, insertAfterATarget, artifactB,
               artifactATarget, artifactB);
      }
      SkynetEventManager.getInstance().kick(
            new CacheRelationModifiedEvent(relation, relation.getABranch(), relation.getRelationType().getTypeName(),
                  relation.getASideName(), ModType.Added, RelationManager.class));

   }

   private static void setDefaultRelationOrder(RelationLink relation, Artifact artifactA, Artifact artifactB) {
      List<RelationLink> selectedRelations = getRelations(artifactA, relation.getRelationType(), RelationSide.SIDE_B);
      if (selectedRelations != null && selectedRelations.size() > 0) {
         relation.setOrder(RelationSide.SIDE_B, selectedRelations.get(selectedRelations.size() - 1).getArtifactId(
               RelationSide.SIDE_B));
      } else {
         relation.setOrder(RelationSide.SIDE_B, -1);
      }
      selectedRelations = getRelations(artifactB, relation.getRelationType(), RelationSide.SIDE_A);
      if (selectedRelations != null && selectedRelations.size() > 0) {
         relation.setOrder(RelationSide.SIDE_A, selectedRelations.get(selectedRelations.size() - 1).getArtifactId(
               RelationSide.SIDE_A));
      } else {
         relation.setOrder(RelationSide.SIDE_A, -1);
      }
   }

   private static void setRelationOrdering(RelationSide side, RelationLink relation, Artifact targetArtifact, boolean insertAfterTarget, Artifact sourceArtifact, Artifact artA, Artifact artB) throws OseeCoreException {
      if (targetArtifact != null) {

         RelationLink targetRelation =
               getLoadedRelation(sourceArtifact, artA.getArtId(), artB.getArtId(), relation.getRelationType());
         if (targetRelation == null) {
            throw new OseeCoreException(String.format(
                  "No Relation exists on [%s] of type [%s] between aArtId[%d] and bArtId[%d].", artA.toString(),
                  relation.getRelationType().toString(), artA.getArtId(), artB.getArtId()));
         }
         List<RelationLink> selectedRelations = relationsByType.get(sourceArtifact, relation.getRelationType());
         if (selectedRelations.remove(relation)) {
            int targetIndex = selectedRelations.indexOf(targetRelation);
            int index = insertAfterTarget ? targetIndex + 1 : targetIndex;
            selectedRelations.add(index, relation);
            int lastArtId = LINKED_LIST_KEY;
            for (RelationLink link : selectedRelations) {
               if (!link.isDeleted() && link.getSide(sourceArtifact) == side.oppositeSide()) {
                  if (link.getOrder(side) != lastArtId) {
                     link.setOrder(side, lastArtId);
                  }
                  lastArtId = link.getArtifactId(side);
               }
            }
         }
      }
   }

   /**
    * @param targetArtifact
    * @param insertAfterTarget
    * @param relationType
    * @param artifactA
    * @param artifactB
    * @throws SQLException
    * @throws OseeCoreException
    */
   public static void setRelationOrder(Artifact artifactATarget, boolean insertAfterATarget, Artifact artifactBTarget, boolean insertAfterBTarget, RelationType relationType, Artifact artifactA, Artifact artifactB) throws OseeCoreException {

      RelationLink relation = getLoadedRelation(artifactA, artifactA.getArtId(), artifactB.getArtId(), relationType);

      setRelationOrdering(RelationSide.SIDE_B, relation, artifactBTarget, insertAfterBTarget, artifactA, artifactA,
            artifactBTarget);
      setRelationOrdering(RelationSide.SIDE_A, relation, artifactATarget, insertAfterATarget, artifactB,
            artifactATarget, artifactB);
      SkynetEventManager.getInstance().kick(
            new CacheRelationModifiedEvent(relation, relation.getABranch(), relation.getRelationType().getTypeName(),
                  relation.getASideName(), ModType.Added, RelationManager.class));
   }

   /**
    * @param artifact
    * @throws SQLException
    */
   public static void sortRelations(Artifact artifact, Map<Integer, RelationLink> sideA, Map<Integer, RelationLink> sideB) throws SQLException {
      List<RelationType> types = RelationTypeManager.getValidTypes(artifact.getArtifactType(), artifact.getBranch());
      for (RelationType type : types) {
         List<RelationLink> relations = relationsByType.get(artifact, type);
         if (relations != null) {
            sideA.clear();
            sideB.clear();
            boolean badValues = false;
            for (RelationLink relation : relations) {
               if (!relation.isDeleted()) {
                  if (relation.getSide(artifact) == RelationSide.SIDE_A) {
                     if (sideB.put(relation.getOrder(RelationSide.SIDE_B), relation) != null) {
                        badValues = true;
                     }
                  } else {
                     if (sideA.put(relation.getOrder(RelationSide.SIDE_A), relation) != null) {
                        badValues = true;
                     }
                  }
               }
            }
            if (!badValues) {
               relations.clear();

               //do side b first
               RelationLink relation = sideB.remove(LINKED_LIST_KEY);
               while (relation != null) {
                  relations.add(relation);
                  RelationLink newRelation = sideB.get(relation.getArtifactId(RelationSide.SIDE_B));
                  sideB.remove(relation.getArtifactId(RelationSide.SIDE_B));
                  relation = newRelation;
               }
               relations.addAll(sideB.values());
               //now side a
               relation = sideA.remove(LINKED_LIST_KEY);
               while (relation != null) {
                  relations.add(relation);
                  relation = sideA.remove(relation.getArtifactId(RelationSide.SIDE_A));
               }
               relations.addAll(sideA.values());
            }
         }
      }
   }
}