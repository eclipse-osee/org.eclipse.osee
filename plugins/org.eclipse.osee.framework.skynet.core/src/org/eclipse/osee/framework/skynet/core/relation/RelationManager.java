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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.RelationSorter.PREEXISTING;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderFactory;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan D. Brooks
 */
public class RelationManager {

   private static final String GET_DELETED_ARTIFACT =
      "SELECT DISTINCT %s_art_id, txs.branch_id FROM osee_txs txs, osee_relation_link rel WHERE txs.branch_id = ? AND txs.gamma_id = rel.gamma_id AND rel.rel_link_type_id = ? AND %s_art_id = ? AND txs.tx_current in (2,3)";

   private static final RelationSorterProvider relationSorterProvider = new RelationSorterProvider();
   private static final RelationOrderFactory relationOrderFactory = new RelationOrderFactory();

   private static final RelationCache relationCache = new RelationCache();

   /**
    * Store the newly instantiated relation from the perspective of relationSide in its appropriate order
    */
   public static void manageRelation(RelationLink newRelation, RelationSide relationSide) {
      Artifact artifact = ArtifactCache.getActive(newRelation.getArtifactId(relationSide), newRelation.getBranch());
      if (artifact != null) {
         List<RelationLink> artifactsRelations = relationCache.getAll(artifact);
         if (artifactsRelations == null) {
            artifactsRelations = new CopyOnWriteArrayList<>();
         }

         // Verify that relation is unique by aArtId, bArtId and relTypeId; Needs to be cleaned up in DB, Only log problem.
         // Need to do this check before to catch the relations that are .equal but not ==
         for (RelationLink relation : artifactsRelations) {
            if (relation.getArtifactIdA().equals(newRelation.getArtifactIdA()) && relation.getArtifactIdB().equals(
               newRelation.getArtifactIdB()) && relation.getRelationType().equals(
                  newRelation.getRelationType()) && relation != newRelation) {
               OseeLog.logf(Activator.class, Level.WARNING,
                  "Duplicate relation objects for same relation for RELATION 1 [%s] RELATION 2 [%s]", relation,
                  newRelation);
            }
         }

         if (artifactsRelations.contains(newRelation)) {
            // Always want to return if relation link is already managed
            return;
         }

         artifactsRelations.add(newRelation);
         relationCache.cache(artifact, newRelation);
      }
   }

   private static List<Artifact> getRelatedArtifactsUnSorted(Artifact artifact, IRelationType relationType, RelationSide relationSide) {
      return getRelatedArtifacts(artifact, relationType, relationSide, false);
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationType relationType, RelationSide relationSide) {
      return getRelatedArtifacts(artifact, relationType, relationSide, true);
   }

   private static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationType relationType, RelationSide relationSide, boolean sort) {
      if (artifact.isHistorical()) {
         throw new OseeCoreException("Artifact [%s] is historical.  Historical relations are only supported on server",
            artifact);
      }
      if (relationSide == null) {
         throw new OseeArgumentException("RelationSide cannot be null");
      }

      List<RelationLink> selectedRelations = null;
      if (relationType == null) {
         selectedRelations = relationCache.getAll(artifact);
      } else {
         selectedRelations = relationCache.getAllByType(artifact, relationType);
      }

      List<Artifact> relatedArtifacts;

      if (selectedRelations == null) {
         relatedArtifacts = new ArrayList<>();
      } else {
         relatedArtifacts = new ArrayList<>(selectedRelations.size());

         ArtifactQuery.getArtifactListFrom(
            getRelatedArtifactIds(selectedRelations, relationSide, DeletionFlag.EXCLUDE_DELETED), artifact.getBranch());

         for (RelationLink relation : selectedRelations) {
            if (!relation.isDeleted()) {
               try {
                  if (relation.getSide(artifact).isOppositeSide(relationSide)) {
                     relationSide = relation.getOppositeSide(artifact);
                     relatedArtifacts.add(relation.getArtifactOnOtherSide(artifact));
                  }
               } catch (ArtifactDoesNotExist ex) {
                  OseeLog.log(Activator.class, Level.WARNING, ex);
               }
            }
         }
         if (sort) {
            sort(artifact, relationType, relationSide, relatedArtifacts);
         }
      }
      return relatedArtifacts;
   }

   private static Collection<ArtifactId> getRelatedArtifactIds(List<RelationLink> relations, RelationSide side, DeletionFlag allowDeleted) {
      Collection<ArtifactId> ret = new HashSet<>();
      if (relations != null) {
         for (RelationLink rel : relations) {
            if (allowDeleted == INCLUDE_DELETED || allowDeleted == EXCLUDE_DELETED && !rel.isDeleted()) {
               ret.add(rel.getArtifactId(side));
            }
         }
      }
      return ret;
   }

   public static Set<Artifact> getRelatedArtifacts(Collection<? extends Artifact> artifacts, int depth, RelationTypeSide... relationEnums) {
      return getRelatedArtifacts(artifacts, depth, EXCLUDE_DELETED, relationEnums);
   }

   public static Set<Artifact> getRelatedArtifacts(Collection<? extends Artifact> artifacts, int depth, DeletionFlag allowDeleted, RelationTypeSide... relationEnums) {
      findHistoricalArtifacts(artifacts);

      Set<Artifact> relatedArtifacts = new HashSet<>(artifacts.size() * 8);
      Collection<Artifact> newArtifactsToSearch = new ArrayList<>(artifacts);
      Collection<Artifact> newArtifacts = new ArrayList<>();
      Set<ArtifactId> relatedArtIds = new HashSet<>();
      if (artifacts.isEmpty()) {
         return relatedArtifacts;
      }

      // loop through till either depth is reached or there are no more artifacts to search at this level
      for (int i = 0; i < depth && !newArtifactsToSearch.isEmpty(); i++) {
         relatedArtIds.clear();
         for (Artifact artifact : newArtifactsToSearch) {
            List<RelationLink> selectedRelations = new ArrayList<>();
            if (relationEnums.length == 0) {
               /**
                * since getting relations by type will return the link between this artifact and it's parent, make sure
                * not to put it in the list of selected relations
                */
               List<RelationLink> relations = relationCache.getAll(artifact);
               for (RelationLink rel : relations) {
                  if (rel.getArtifactIdA().notEqual(artifact)) {
                     selectedRelations.add(rel);
                  }
               }
               relatedArtIds.addAll(getRelatedArtifactIds(selectedRelations, RelationSide.SIDE_B, allowDeleted));
            } else {
               for (RelationTypeSide relationEnum : relationEnums) {
                  Collection<RelationLink> links = relationCache.getAllByType(artifact, relationEnum);
                  if (links != null) {
                     for (RelationLink rel : links) {
                        /**
                         * since getting relations by type will return the link between this artifact and it's parent,
                         * make sure not to put it in the list of selected relations
                         */
                        if (rel.getArtifactId(relationEnum.getSide()).notEqual(artifact)) {
                           selectedRelations.add(rel);
                        }
                     }
                  }
                  relatedArtIds.addAll(getRelatedArtifactIds(selectedRelations, relationEnum.getSide(), allowDeleted));
               }
            }
         }

         if (relatedArtIds.size() > 0) {
            BranchId branch = artifacts.iterator().next().getBranch();
            newArtifacts = ArtifactQuery.getArtifactListFrom(relatedArtIds, branch, allowDeleted);
         }
         newArtifactsToSearch.clear();
         newArtifactsToSearch.addAll(newArtifacts);
         relatedArtifacts.addAll(newArtifacts);
      }
      return relatedArtifacts;
   }

   private static void findHistoricalArtifacts(Collection<? extends Artifact> artifacts) {
      for (Artifact artifact : artifacts) {
         if (artifact.isHistorical()) {
            throw new OseeCoreException(
               "Artifact [%s] is historical. Historical relations are only supported on the server.", artifact);
         }
      }
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationTypeSide relationType, DeletionFlag deletionFlag) {
      List<Artifact> artifacts = getRelatedArtifacts(artifact, relationType, relationType.getSide());
      Collection<ArtifactId> artIds = new ArrayList<>();

      if (deletionFlag.areDeletedAllowed()) {
         Object[] formatArgs = relationType.getSide().isSideA() ? new Object[] {"a", "b"} : new Object[] {"b", "a"};
         try (JdbcStatement chStmt = ConnectionHandler.getStatement()) {
            String sql = String.format(GET_DELETED_ARTIFACT, formatArgs);
            chStmt.runPreparedQuery(sql, artifact.getBranch(), relationType.getGuid(), artifact);
            while (chStmt.next()) {
               artIds.add(ArtifactId.valueOf(chStmt.getLong(formatArgs[0] + "_art_id")));
            }
         }

         List<Artifact> deletedArtifacts = ArtifactQuery.getArtifactListFrom(artIds, artifact.getBranch());

         for (Artifact art : deletedArtifacts) {
            if (art.isDeleted()) {
               artifacts.add(art);
            }
         }
      }

      return artifacts;
   }

   public static List<Artifact> getRelatedArtifactsUnSorted(Artifact artifact, RelationTypeSide relationEnum) {
      return getRelatedArtifactsUnSorted(artifact, relationEnum, relationEnum.getSide());
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationTypeSide relationEnum) {
      return getRelatedArtifacts(artifact, relationEnum, relationEnum.getSide());
   }

   private static Artifact getRelatedArtifact(Artifact artifact, IRelationType relationType, RelationSide relationSide) {
      List<Artifact> artifacts = getRelatedArtifactsUnSorted(artifact, relationType, relationSide);

      if (artifacts.isEmpty()) {
         throw new ArtifactDoesNotExist("There is no artifact related to [%s] by a relation of type [%s]",
            artifact.toStringWithId(), relationType);
      }

      if (artifacts.size() > 1) {
         throw new MultipleArtifactsExist(
            "There are %s artifacts related to \"%s\" by a relation of type \"%s\" on side %s instead of the expected 1.",
            artifacts.size(), artifact.toStringWithId(), relationType, relationSide);
      }
      return artifacts.get(0);
   }

   public static Artifact getRelatedArtifact(Artifact artifact, RelationTypeSide relationEnum) {
      return getRelatedArtifact(artifact, relationEnum, relationEnum.getSide());
   }

   public static int getRelatedArtifactsCount(Artifact artifact, RelationTypeSide relationTypeEnum) {
      return getRelatedArtifactsCount(artifact, relationTypeEnum, relationTypeEnum.getSide());
   }

   public static int getRelatedArtifactsCount(Artifact artifact, IRelationType relationType, RelationSide relationSide) {
      if (artifact.isHistorical()) {
         throw new OseeCoreException("Artifact [%s] is historical.  Historical relations are only supported on server",
            artifact);
      }
      List<RelationLink> selectedRelations = relationCache.getAllByType(artifact, relationType);

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

   public static void prepareRelationsForReload(Artifact artifact) {
      // weakness:  references held to links by other applications will continue to exist.
      //We do not want to drop relation links for historical artifacts because the relation manager will clobber the current artifacts relations.
      if (!artifact.isHistorical()) {
         relationCache.deCache(artifact);
      }
   }

   public static boolean hasDirtyLinks(Artifact artifact) {
      List<RelationLink> selectedRelations = relationCache.getAll(artifact);
      for (RelationLink relation : selectedRelations) {
         if (relation.isDirty()) {
            return true;
         }
      }
      return false;
   }

   /*
    * Return what relation is dirty otherwise null
    */
   public static String reportHasDirtyLinks(Artifact artifact) {
      List<RelationLink> selectedRelations = relationCache.getAll(artifact);
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relation.isDirty()) {
               try {
                  return String.format("Relation\n\n[%s]\n\naSide [%s]\n\nbSide [%s]", relation,
                     relation.getArtifactIdA(), relation.getArtifactIdB());
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      }
      return null;
   }

   public static List<RelationLink> getRelationsUnordered(Artifact artifact, IRelationType relationType, RelationSide relationSide) {
      return getRelations(artifact, relationType, relationSide, false);
   }

   public static List<RelationLink> getRelations(Artifact artifact, IRelationType relationType, RelationSide relationSide) {
      return getRelations(artifact, relationType, relationSide, true);
   }

   public static List<RelationLink> getRelations(Artifact artifact, IRelationType relationType, RelationSide relationSide, boolean sort) {
      if (artifact.isHistorical()) {
         throw new OseeCoreException("Artifact [%s] is historical.  Historical relations are only supported on server",
            artifact);
      }

      List<RelationLink> selectedRelations =
         relationCache.getAllByType(artifact, RelationTypeManager.getType(relationType));
      if (selectedRelations == null) {
         return Collections.emptyList();
      }

      List<RelationLink> relations = new ArrayList<>(selectedRelations.size());

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
      if (sort) {
         sortRelations(artifact, relationType, relationSide, relations);
      }
      return relations;
   }

   public static void ensureRelationCanBeAdded(IRelationType relationType, Artifact artifactA, Artifact artifactB) {
      // For now, relations can not be cross branch.  Ensure that both artifacts are on same branch
      ensureSameBranch(artifactA, artifactB);
      RelationType relType = RelationTypeManager.getType(relationType);
      ensureSideWillSupport(artifactA, relType, RelationSide.SIDE_A, 1);
      ensureSideWillSupport(artifactB, relType, RelationSide.SIDE_B, 1);
   }

   private static void ensureSameBranch(Artifact a, Artifact b) throws OseeArgumentException {
      if (!a.isOnSameBranch(b)) {
         throw new OseeArgumentException("Cross branch linking is not yet supported.");
      }
   }

   /**
    * Check whether artifactCount number of additional artifacts of type artifactType can be related to the artifact on
    * side relationSide for relations of type relationType
    */
   private static void ensureSideWillSupport(Artifact artifact, RelationType relationType, RelationSide relationSide, int artifactCount) {
      if (!relationType.isArtifactTypeAllowed(relationSide, artifact.getArtifactType())) {
         throw new OseeArgumentException(String.format(
            "Artifact [%s] of type [%s] does not belong on side [%s] of relation [%s] - only artifacts of type [%s] are allowed",
            artifact.getName(), artifact.getArtifactTypeName(), relationType.getSideName(relationSide),
            relationType.getName(), relationType.getArtifactType(relationSide)));
      }

      // ensure that we can add artifactCount number or artifacts to the side opposite this artifact
      int nextCount = getRelatedArtifactsCount(artifact, relationType, relationSide.oppositeSide());
      nextCount += artifactCount;
      RelationTypeMultiplicity multiplicity = relationType.getMultiplicity();
      if (!multiplicity.isWithinLimit(relationSide.oppositeSide(), nextCount)) {
         throw new OseeArgumentException(String.format(
            "Artifact [%s] of type [%s] cannot be added to [%s] of relation [%s] because doing so would exceed the side maximum of [%s] for this artifact type",
            artifact.getName(), artifact.getArtifactTypeName(), relationSide.toString(), relationType.getName(),
            multiplicity.asLimitLabel(relationSide.oppositeSide())));
      }
   }

   public static void deleteRelation(IRelationType relationType, Artifact artifactA, Artifact artifactB) {
      RelationLink relation =
         relationCache.getLoadedRelation(artifactA, artifactA, artifactB, relationType, DeletionFlag.EXCLUDE_DELETED);
      Conditions.checkNotNull(relation, "relationLink",
         "A relation link of type [%s] does exist in the cache between a artifact %d and b artifact %d", relationType,
         artifactA.getArtId(), artifactB.getArtId());
      ArtifactPersistenceManager.performDeleteRelationChecks(artifactA, relationType);
      relation.delete(true);

      updateOrderListOnDelete(artifactA, relationType, RelationSide.SIDE_B,
         getRelatedArtifacts(artifactA, relationType, RelationSide.SIDE_B));
      updateOrderListOnDelete(artifactB, relationType, RelationSide.SIDE_A,
         getRelatedArtifacts(artifactB, relationType, RelationSide.SIDE_A));
   }

   public static void deleteRelationsAll(Artifact artifact, boolean reorderRelations, SkynetTransaction transaction) {
      if (artifact.isHistorical()) {
         throw new OseeCoreException(
            "Artifact [%s] is historical. Historical relations are only supported on the server.", artifact);
      }
      List<RelationLink> selectedRelations = relationCache.getAll(artifact);
      Set<Pair<IRelationType, RelationSide>> typesToUpdate = new HashSet<>();
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            typesToUpdate.add(
               new Pair<IRelationType, RelationSide>(relation.getRelationType(), relation.getOppositeSide(artifact)));
            relation.delete(reorderRelations, transaction);
         }
      }

      for (Pair<IRelationType, RelationSide> type : typesToUpdate) {
         updateOrderListOnDelete(artifact, type.getFirst(), type.getSecond(),
            getRelatedArtifacts(artifact, type.getFirst(), type.getSecond()));
      }
   }

   public static void deleteRelations(Artifact artifact, IRelationType relationType, RelationSide relationSide) {
      if (artifact.isHistorical()) {
         throw new OseeCoreException(
            "Artifact [%s] is historical. Historical relations are only supported on the server.", artifact);
      }
      List<RelationLink> selectedRelations = relationCache.getAllByType(artifact, relationType);
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relation.getSide(artifact) != relationSide) {
               relation.delete(true);
            }
         }
      }

      updateOrderListOnDelete(artifact, relationType, relationSide,
         getRelatedArtifacts(artifact, relationType, relationSide));
   }

   /**
    * Remove all relations stored in the list awaiting to be deleted.
    */
   public static void purgeRelationsFor(Artifact artifact) {
      if (artifact.isHistorical()) {
         throw new OseeCoreException(
            "Artifact [%s] is historical. Historical relations are only supported on the server.", artifact);
      }
      Collection<RelationLink> links = relationCache.getAll(artifact);
      if (!links.isEmpty()) {
         List<Object[]> batchArgs = new ArrayList<>(links.size());
         String PURGE_RELATION = "delete from osee_relation_link WHERE rel_link_id = ?";
         for (RelationLink link : links) {
            batchArgs.add(new Object[] {link.getId()});
            link.markAsPurged();
         }
         ConnectionHandler.runBatchUpdate(PURGE_RELATION, batchArgs);
      }
   }

   public static void addRelation(IRelationType relationType, Artifact artifactA, Artifact artifactB, String rationale) {
      addRelation(PREEXISTING, relationType, artifactA, artifactB, rationale);
   }

   public static void addRelation(RelationSorter sorterId, IRelationType relationType, Artifact artifactA, Artifact artifactB, String rationale) {
      Conditions.checkExpressionFailOnTrue(artifactA.equals(artifactB), "Not valid to relate artifact [%s] to itself",
         artifactA);
      RelationLink relation =
         relationCache.getLoadedRelation(artifactA, artifactA, artifactB, relationType, INCLUDE_DELETED);

      if (relation == null) {
         ensureRelationCanBeAdded(relationType, artifactA, artifactB);

         relation = getOrCreate(artifactA, artifactB, RelationTypeManager.getType(relationType), 0, 0, rationale,
            ModificationType.NEW, ApplicabilityId.BASE);
         relation.setDirty();
         if (relation.isDeleted()) {
            relation.undelete();
         }

         RelationTypeSideSorter sorter = createTypeSideSorter(artifactA, relationType, RelationSide.SIDE_B);
         sorter.addItem(sorterId, artifactB);

      } else if (relation.isDeleted()) {
         relation.undelete();
         RelationTypeSideSorter sorter = createTypeSideSorter(artifactA, relationType, RelationSide.SIDE_B);
         sorter.addItem(sorterId, artifactB);
      }
   }

   public static RelationLink getRelationLink(Artifact artifactA, Artifact artifactB, IRelationType relationType) {
      List<RelationLink> relationLinks = relationCache.getAllByType(artifactA, relationType);
      for (RelationLink relation : relationLinks) {
         if (relation.getArtifactB().equals(artifactB)) {
            return relation;
         }
      }
      throw new OseeCoreException("Unable to find a relation link for type[%s] artA[%s] artB[%s]", relationType,
         artifactA.getName(), artifactB.getName());
   }

   public static List<RelationSorter> getRelationOrderTypes() {
      return relationSorterProvider.getAllRelationOrderIds();
   }

   public static RelationTypeSideSorter createTypeSideSorter(Artifact artifact, IRelationType relationType, RelationSide side) {
      RelationOrderData data = createRelationOrderData(artifact);
      return new RelationTypeSideSorter(RelationTypeManager.getType(relationType), side, relationSorterProvider, data);
   }

   public static RelationOrderData createRelationOrderData(Artifact artifact) {
      return relationOrderFactory.createRelationOrderData(artifact);
   }

   public static void setRelationOrder(Artifact artifact, IRelationType relationType, RelationSide side, RelationSorter orderId, List<Artifact> relatives) {
      RelationTypeSideSorter sorter = createTypeSideSorter(artifact, relationType, side);
      sorter.setOrder(relatives, orderId);
   }

   private static void sort(Artifact artifact, IRelationType type, RelationSide side, List<Artifact> listToOrder) {
      if (type == null || side == null || listToOrder.size() <= 1) {
         return;
      }
      RelationTypeSideSorter sorter = createTypeSideSorter(artifact, type, side);
      sorter.sort(listToOrder);
   }

   private static void sortRelations(Artifact artifact, IRelationType type, RelationSide side, List<RelationLink> listToOrder) {
      if (type == null || side == null || listToOrder.size() <= 1) {
         return;
      }
      RelationTypeSideSorter sorter = createTypeSideSorter(artifact, type, side);
      sorter.sortRelations(listToOrder);
   }

   private static void updateOrderListOnDelete(Artifact artifact, IRelationType relationType, RelationSide relationSide, List<Artifact> relatives) {
      RelationTypeSideSorter sorter = createTypeSideSorter(artifact, relationType, relationSide);
      sorter.setOrder(relatives, sorter.getSorterId());
   }

   public static void deCache(Artifact artifact) {
      relationCache.deCache(artifact);
   }

   /**
    * Return existing RelationLink or create new one. This needs to be synchronized so two threads don't create the same
    * link object twice.
    *
    * @param relationId 0 or relationId if already created
    */
   public static synchronized RelationLink getOrCreate(ArtifactToken aArtifactId, ArtifactToken bArtifactId, RelationTypeToken relationType, int relationId, int gammaId, String rationale, ModificationType modificationType, ApplicabilityId applicabilityId) {
      BranchId branch = aArtifactId.getBranch();
      RelationLink relation = null;
      if (relationId != 0) {
         relation = getLoadedRelationById(relationId, aArtifactId, bArtifactId, branch);
      } else {
         relation = getLoadedRelation(relationType, aArtifactId, bArtifactId, branch);
      }
      if (relation == null) {
         relation = new RelationLink(aArtifactId, bArtifactId, branch, relationType, relationId, gammaId, rationale,
            modificationType, applicabilityId);
      }
      manageRelation(relation, RelationSide.SIDE_A);
      manageRelation(relation, RelationSide.SIDE_B);

      return relation;
   }

   public static RelationLink getLoadedRelation(IRelationType relationType, ArtifactToken aArtifactId, ArtifactToken bArtifactId, BranchId branch) {
      return relationCache.getLoadedRelation(relationType, aArtifactId.getId().intValue(),
         bArtifactId.getId().intValue(), branch);
   }

   public static RelationLink getLoadedRelationById(int relLinkId, ArtifactId aArtifactId, ArtifactId bArtifactId, BranchId branch) {
      return relationCache.getByRelIdOnArtifact(relLinkId, aArtifactId.getId().intValue(),
         bArtifactId.getId().intValue(), branch);
   }

   public static List<RelationLink> getRelationsAll(Artifact artifact, DeletionFlag deletionFlag) {
      if (artifact.isHistorical()) {
         throw new OseeCoreException("Artifact [%s] is historical.  Historical relations are only supported on server",
            artifact);
      }
      return relationCache.getRelations(artifact, deletionFlag);
   }
}