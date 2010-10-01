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
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink.ArtifactLinker;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderFactory;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

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
            artifactsRelations = new CopyOnWriteArrayList<RelationLink>();
         }

         // Verify that relation is unique by aArtId, bArtId and relTypeId; Needs to be cleaned up in DB, Only log problem.
         // Need to do this check before to catch the relations that are .equal but not ==
         for (RelationLink relation : artifactsRelations) {
            if (relation.getAArtifactId() == newRelation.getAArtifactId() && //
            relation.getBArtifactId() == newRelation.getBArtifactId() && //
            relation.getRelationType() == newRelation.getRelationType() && //
            relation != newRelation) {
               OseeLog.log(Activator.class, Level.WARNING, String.format(
                  "Duplicate relation objects for same relation for RELATION 1 [%s] RELATION 2 [%s]", relation,
                  newRelation));
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

   private static List<Artifact> getRelatedArtifactsUnSorted(Artifact artifact, IRelationType relationType, RelationSide relationSide) throws OseeCoreException {
      return getRelatedArtifacts(artifact, relationType, relationSide, false);
   }

   private static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationType relationType, RelationSide relationSide) throws OseeCoreException {
      return getRelatedArtifacts(artifact, relationType, relationSide, true);
   }

   @SuppressWarnings("unused")
   private static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationType relationTypeToken, RelationSide relationSide, boolean sort) throws OseeCoreException {
      if (relationSide == null) {
         throw new OseeArgumentException("RelationSide cannot be null");
      }
      List<RelationLink> selectedRelations = null;
      if (relationTypeToken == null) {
         selectedRelations = relationCache.getAll(artifact);
      } else {
         selectedRelations = relationCache.getAllByType(artifact, relationTypeToken);
      }

      if (selectedRelations == null) {
         return Collections.emptyList();
      }

      Collection<Artifact> bulkLoadedArtifacts =
         ArtifactQuery.getArtifactListFromIds(
            getRelatedArtifactIds(selectedRelations, relationSide, DeletionFlag.EXCLUDE_DELETED), artifact.getBranch());

      List<Artifact> relatedArtifacts = new ArrayList<Artifact>(selectedRelations.size());
      relatedArtifacts.clear();
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
         RelationType relationType = RelationTypeManager.getType(relationTypeToken);
         sort(artifact, relationType, relationSide, relatedArtifacts);
      }
      return relatedArtifacts;
   }

   private static Collection<Integer> getRelatedArtifactIds(List<RelationLink> relations, RelationSide side, DeletionFlag allowDeleted) {
      Collection<Integer> ret = new HashSet<Integer>();
      if (relations != null) {
         for (RelationLink rel : relations) {
            if (allowDeleted == INCLUDE_DELETED || allowDeleted == EXCLUDE_DELETED && !rel.isDeleted()) {
               ret.add(rel.getArtifactId(side));
            }
         }
      }
      return ret;
   }

   public static Set<Artifact> getRelatedArtifacts(Collection<? extends Artifact> artifacts, int depth, IRelationEnumeration... relationEnums) throws OseeCoreException {
      return getRelatedArtifacts(artifacts, depth, EXCLUDE_DELETED, relationEnums);
   }

   public static Set<Artifact> getRelatedArtifacts(Collection<? extends Artifact> artifacts, int depth, DeletionFlag allowDeleted, IRelationEnumeration... relationEnums) throws OseeCoreException {
      Set<Artifact> relatedArtifacts = new HashSet<Artifact>(artifacts.size() * 8);
      Collection<Artifact> newArtifactsToSearch = new ArrayList<Artifact>(artifacts);
      Collection<Artifact> newArtifacts = new ArrayList<Artifact>();
      Set<Integer> relatedArtIds = new HashSet<Integer>();
      if (artifacts.isEmpty()) {
         return relatedArtifacts;
      }

      // loop through till either depth is reached or there re no more artifacts to search at this level
      for (int i = 0; i < depth && !newArtifactsToSearch.isEmpty(); i++) {
         relatedArtIds.clear();
         for (Artifact artifact : newArtifactsToSearch) {
            List<RelationLink> selectedRelations = new ArrayList<RelationLink>();
            if (relationEnums.length == 0) {
               /**
                * since getting relations by type will return the link between this artifact and it's parent, make sure
                * not to put it in the list of selected relations
                */
               List<RelationLink> relations = relationCache.getAll(artifact);
               for (RelationLink rel : relations) {
                  if (!rel.getArtifactA().equals(artifact)) {
                     selectedRelations.add(rel);
                  }
               }
               relatedArtIds.addAll(getRelatedArtifactIds(selectedRelations, RelationSide.SIDE_B, allowDeleted));
            } else {
               for (IRelationEnumeration relationEnum : relationEnums) {
                  Collection<RelationLink> links =
                     relationCache.getAllByType(artifact, RelationTypeManager.getType(relationEnum));
                  if (links != null) {
                     for (RelationLink rel : links) {
                        /**
                         * since getting relations by type will return the link between this artifact and it's parent,
                         * make sure not to put it in the list of selected relations
                         */
                        if (rel.getArtifactId(relationEnum.getSide()) != artifact.getArtId()) {
                           selectedRelations.add(rel);
                        }
                     }
                  }
                  relatedArtIds.addAll(getRelatedArtifactIds(selectedRelations, relationEnum.getSide(), allowDeleted));
               }
            }
         }

         if (relatedArtIds.size() > 0) {
            Branch branch = artifacts.toArray(new Artifact[0])[0].getBranch();
            newArtifacts = ArtifactQuery.getArtifactListFromIds(relatedArtIds, branch, allowDeleted);
         }
         newArtifactsToSearch.clear();
         newArtifactsToSearch.addAll(newArtifacts);
         relatedArtifacts.addAll(newArtifacts);
      }
      return relatedArtifacts;
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationEnumeration relationEnum, DeletionFlag deletionFlag) throws OseeCoreException {
      RelationType relationType = RelationTypeManager.getType(relationEnum);
      List<Artifact> artifacts = getRelatedArtifacts(artifact, relationType, relationEnum.getSide());
      Collection<Integer> artIds = new ArrayList<Integer>();

      if (deletionFlag.areDeletedAllowed()) {
         Object[] formatArgs = relationEnum.getSide().isSideA() ? new Object[] {"a", "b"} : new Object[] {"b", "a"};
         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            String sql = String.format(GET_DELETED_ARTIFACT, formatArgs);
            chStmt.runPreparedQuery(sql, artifact.getBranch().getId(), relationType.getId(), artifact.getArtId());
            while (chStmt.next()) {
               int artId = chStmt.getInt(formatArgs[0] + "_art_id");
               artIds.add(artId);
            }
         } finally {
            chStmt.close();
         }

         List<Artifact> deletedArtifacts =
            ArtifactQuery.getArtifactListFromIds(artIds, artifact.getBranch(), INCLUDE_DELETED);

         for (Artifact art : deletedArtifacts) {
            if (art.isDeleted()) {
               artifacts.add(art);
            }
         }
      }

      return artifacts;
   }

   public static List<Artifact> getRelatedArtifactsUnSorted(Artifact artifact, IRelationEnumeration relationEnum) throws OseeCoreException {
      RelationType relationType = RelationTypeManager.getType(relationEnum);
      return getRelatedArtifactsUnSorted(artifact, relationType, relationEnum.getSide());
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationEnumeration relationEnum) throws OseeCoreException {
      return getRelatedArtifacts(artifact, RelationTypeManager.getType(relationEnum), relationEnum.getSide());
   }

   private static Artifact getRelatedArtifact(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
      List<Artifact> artifacts = getRelatedArtifactsUnSorted(artifact, relationType, relationSide);

      if (artifacts.isEmpty()) {
         throw new ArtifactDoesNotExist("There is no artifact related to [%s] by a relation of type [%s]", artifact,
            relationType);
      }

      if (artifacts.size() > 1) {
         throw new MultipleArtifactsExist(
            "There are %s artifacts related to \"%s\" by a relation of type \"%s\" on side %s instead of the expected 1.",
            artifacts.size(), artifact, relationType, relationSide);
      }
      return artifacts.get(0);
   }

   public static Artifact getRelatedArtifact(Artifact artifact, IRelationEnumeration relationEnum) throws OseeCoreException {
      return getRelatedArtifact(artifact, RelationTypeManager.getType(relationEnum), relationEnum.getSide());
   }

   public static int getRelatedArtifactsCount(Artifact artifact, IRelationEnumeration relationTypeEnum) throws OseeCoreException {
      return getRelatedArtifactsCount(artifact, RelationTypeManager.getType(relationTypeEnum),
         relationTypeEnum.getSide());
   }

   public static int getRelatedArtifactsCount(Artifact artifact, IRelationType relationType, RelationSide relationSide) {
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
                     relation.getArtifactA(), relation.getArtifactB());
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      }
      return null;
   }

   /**
    * @param relationType if not null persists the relations of this type, otherwise persists relations of all types
    */
   public static void persistRelationsFor(SkynetTransaction transaction, Artifact artifact, RelationType relationType) throws OseeCoreException {
      List<RelationLink> selectedRelations;
      if (relationType == null) {
         selectedRelations = relationCache.getAll(artifact);
      } else {
         selectedRelations = relationCache.getAllByType(artifact, relationType);
      }

      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relation.isDirty()) {
               transaction.addRelation(relation);

               try {
                  Artifact artifactOnOtherSide = relation.getArtifactOnOtherSide(artifact);
                  List<RelationLink> otherSideRelations =
                     relationCache.getAllByType(artifactOnOtherSide, relation.getRelationType());
                  for (int i = 0; i < otherSideRelations.size(); i++) {
                     if (relation.equals(otherSideRelations.get(i))) {
                        if (i + 1 < otherSideRelations.size()) {
                           RelationLink nextRelation = otherSideRelations.get(i + 1);
                           if (nextRelation.isDirty()) {
                              transaction.addRelation(nextRelation);
                           }
                        }
                     }
                  }
               } catch (ArtifactDoesNotExist ex) {
                  OseeLog.log(
                     RelationManager.class,
                     Level.SEVERE,
                     String.format(
                        "Unable to to persist other side relation order because the artifact on the other side of [%s, %s] doesn't exist. ",
                        artifact.toString(), relation.toString()), ex);
               }
            }
         }
      }
   }

   public static List<RelationLink> getRelations(Artifact artifact, RelationType relationType, RelationSide relationSide) {
      List<RelationLink> selectedRelations = relationCache.getAllByType(artifact, relationType);
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

   public static void ensureRelationCanBeAdded(RelationType relationType, Artifact artifactA, Artifact artifactB) throws OseeCoreException {
      // For now, relations can not be cross branch.  Ensure that both artifacts are on same branch
      ensureSameBranch(artifactA, artifactB);
      ensureSideWillSupport(artifactA, relationType, RelationSide.SIDE_A, 1);
      ensureSideWillSupport(artifactB, relationType, RelationSide.SIDE_B, 1);
   }

   private static void ensureSameBranch(Artifact a, Artifact b) throws OseeArgumentException {
      if (!a.getBranch().equals(b.getBranch())) {
         throw new OseeArgumentException("Cross branch linking is not yet supported.");
      }
   }

   /**
    * Check whether artifactCount number of additional artifacts of type artifactType can be related to the artifact on
    * side relationSide for relations of type relationType
    */
   private static void ensureSideWillSupport(Artifact artifact, RelationType relationType, RelationSide relationSide, int artifactCount) throws OseeCoreException {
      if (!relationType.isArtifactTypeAllowed(relationSide, artifact.getArtifactType())) {
         throw new OseeArgumentException(
            String.format(
               "Artifact [%s] of type [%s] does not belong on side [%s] of relation [%s] - only artifacts of type [%s] are allowed",
               artifact.getName(), artifact.getArtifactTypeName(), relationType.getSideName(relationSide),
               relationType.getName(), relationType.getArtifactType(relationSide)));
      }

      // ensure that we can add artifactCount number or artifacts to the side opposite this artifact
      int nextCount = getRelatedArtifactsCount(artifact, relationType, relationSide.oppositeSide());
      nextCount += artifactCount;
      RelationTypeMultiplicity multiplicity = relationType.getMultiplicity();
      if (!multiplicity.isWithinLimit(relationSide.oppositeSide(), nextCount)) {
         throw new OseeArgumentException(
            String.format(
               "Artifact [%s] of type [%s] cannot be added to [%s] of relation [%s] because doing so would exceed the side maximum of [%s] for this artifact type",
               artifact.getName(), artifact.getArtifactTypeName(), relationSide.toString(), relationType.getName(),
               multiplicity.asLimitLabel(relationSide.oppositeSide())));
      }
   }

   public static void deleteRelation(RelationType relationType, Artifact artifactA, Artifact artifactB) throws OseeCoreException {
      RelationLink relation =
         relationCache.getLoadedRelation(artifactA, artifactA.getArtId(), artifactB.getArtId(), relationType,
            DeletionFlag.EXCLUDE_DELETED);
      Conditions.checkNotNull(relation, "relationLink",
         "A relation link of type [%s] does exist in the cache between a artifact %d and b artifact %d", relationType,
         artifactA.getArtId(), artifactB.getArtId());
      relation.delete(true);

      updateOrderListOnDelete(artifactA, relationType, RelationSide.SIDE_B,
         getRelatedArtifacts(artifactA, relationType, RelationSide.SIDE_B));
      updateOrderListOnDelete(artifactB, relationType, RelationSide.SIDE_A,
         getRelatedArtifacts(artifactB, relationType, RelationSide.SIDE_A));
   }

   public static void deleteRelationsAll(Artifact artifact, boolean reorderRelations) throws OseeCoreException {
      List<RelationLink> selectedRelations = relationCache.getAll(artifact);
      Set<Pair<RelationType, RelationSide>> typesToUpdate = new HashSet<Pair<RelationType, RelationSide>>();
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            typesToUpdate.add(new Pair<RelationType, RelationSide>(relation.getRelationType(),
               relation.getOppositeSide(artifact)));
            relation.delete(reorderRelations);
         }
      }

      for (Pair<RelationType, RelationSide> type : typesToUpdate) {
         updateOrderListOnDelete(artifact, type.getFirst(), type.getSecond(),
            getRelatedArtifacts(artifact, type.getFirst(), type.getSecond()));
      }
   }

   public static void deleteRelations(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
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
    * This method should only be called for unordered Relation Types. It does not handle reordering relation types that
    * maintain order.
    */
   public static void revertRelations(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
      List<RelationLink> selectedRelations = relationCache.getAllByType(artifact, relationType);
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relationSide == null) {
               ArtifactPersistenceManager.revertRelationLink(null, relation);
            } else {
               if (relation.getSide(artifact) != relationSide) {
                  ArtifactPersistenceManager.revertRelationLink(null, relation);
               }
            }
         }
      }
   }

   /**
    * Remove all relations stored in the list awaiting to be deleted.
    */
   public static void purgeRelationsFor(Artifact artifact) throws OseeCoreException {
      Collection<RelationLink> links = relationCache.getAll(artifact);
      if (!links.isEmpty()) {
         List<Object[]> batchArgs = new ArrayList<Object[]>(links.size());
         String PURGE_RELATION = "delete from osee_relation_link WHERE rel_link_id = ?";
         for (RelationLink link : links) {
            batchArgs.add(new Object[] {link.getId()});
            link.markAsPurged();
         }
         ConnectionHandler.runBatchUpdate(PURGE_RELATION, batchArgs);
      }
   }

   public static void addRelation(IRelationType relationType, Artifact artifactA, Artifact artifactB, String rationale) throws OseeCoreException {
      addRelation(null, relationType, artifactA, artifactB, rationale);
   }

   public static void addRelation(IRelationSorterId sorterId, IRelationType relationTypeToken, Artifact artifactA, Artifact artifactB, String rationale) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(artifactA.equals(artifactB), "Not valid to relate artifact [%s] to itself",
         artifactA);
      RelationLink relation =
         relationCache.getLoadedRelation(artifactA, artifactA.getArtId(), artifactB.getArtId(), relationTypeToken,
            INCLUDE_DELETED);

      RelationType relationType = RelationTypeManager.getType(relationTypeToken);
      if (relation == null) {
         ensureRelationCanBeAdded(relationType, artifactA, artifactB);
         relation =
            getOrCreate(artifactA.getArtId(), artifactB.getArtId(), artifactA.getBranch(), relationType, 0, 0,
               rationale, ModificationType.NEW);
         relation.setDirty();

         RelationTypeSideSorter sorter = createTypeSideSorter(artifactA, relationType, RelationSide.SIDE_B);
         sorter.addItem(sorterId, artifactB);

      } else if (relation.isDeleted()) {
         relation.undelete();
         RelationTypeSideSorter sorter = createTypeSideSorter(artifactA, relationType, RelationSide.SIDE_B);
         sorter.addItem(sorterId, artifactB);
      }
   }

   public static RelationLink getRelationLink(Artifact artifactA, Artifact artifactB, IRelationType relationType) throws OseeCoreException {
      List<RelationLink> relationLinks = relationCache.getAllByType(artifactA, relationType);
      for (RelationLink relation : relationLinks) {
         if (relation.getArtifactB().equals(artifactB)) {
            return relation;
         }
      }
      throw new OseeCoreException("Unable to find a relation link for type[%s] artA[%s] artB[%s]",
         relationType.getName(), artifactA.getName(), artifactB.getName());
   }

   public static RelationSorterProvider getSorterProvider() {
      return relationSorterProvider;
   }

   public static List<IRelationSorterId> getRelationOrderTypes() {
      return relationSorterProvider.getAllRelationOrderIds();
   }

   public static RelationTypeSideSorter createTypeSideSorter(IArtifact artifact, RelationType relationType, RelationSide side) throws OseeCoreException {
      return relationOrderFactory.createTypeSideSorter(relationSorterProvider, artifact, relationType, side);
   }

   public static RelationOrderData createRelationOrderData(IArtifact artifact) throws OseeCoreException {
      return relationOrderFactory.createRelationOrderData(artifact);
   }

   public static void setRelationOrder(IArtifact artifact, RelationType relationType, RelationSide side, IRelationSorterId orderId, List<Artifact> relatives) throws OseeCoreException {
      RelationTypeSideSorter sorter = createTypeSideSorter(artifact, relationType, side);
      sorter.setOrder(relatives, orderId);
   }

   private static void sort(IArtifact artifact, RelationType type, RelationSide side, List<Artifact> listToOrder) throws OseeCoreException {
      if (type == null || side == null || listToOrder.size() <= 1) {
         return;
      }
      RelationTypeSideSorter sorter = createTypeSideSorter(artifact, type, side);
      sorter.sort(listToOrder);
   }

   private static void updateOrderListOnDelete(Artifact artifact, RelationType relationType, RelationSide relationSide, List<Artifact> relatives) throws OseeCoreException {
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
   public static synchronized RelationLink getOrCreate(int aArtifactId, int bArtifactId, Branch branch, RelationType relationType, int relationId, int gammaId, String rationale, ModificationType modificationType) {
      RelationLink relation = null;
      if (relationId != 0) {
         relation = getLoadedRelationById(relationId, aArtifactId, bArtifactId, branch);
      } else {
         relation = getLoadedRelation(relationType, aArtifactId, bArtifactId, branch);
      }
      if (relation == null) {
         relation =
            new RelationLink(new RelationArtifactLinker(), aArtifactId, bArtifactId, branch, relationType, relationId,
               gammaId, rationale, modificationType);
      }
      manageRelation(relation, RelationSide.SIDE_A);
      manageRelation(relation, RelationSide.SIDE_B);

      return relation;
   }

   public static RelationLink getLoadedRelation(IRelationType relationType, int aArtifactId, int bArtifactId, Branch branch) {
      return relationCache.getLoadedRelation(relationType, aArtifactId, bArtifactId, branch);
   }

   public static RelationLink getLoadedRelationById(int relLinkId, int aArtifactId, int bArtifactId, Branch branch) {
      return relationCache.getByRelIdOnArtifact(relLinkId, aArtifactId, bArtifactId, branch);
   }

   public static List<RelationLink> getRelationsAll(Artifact artifact, DeletionFlag deletionFlag) {
      return relationCache.getRelations(artifact, deletionFlag);
   }

   private static final class RelationArtifactLinker implements ArtifactLinker {

      @Override
      public Artifact getArtifact(int artifactId, Branch branch) throws OseeCoreException {
         Artifact relatedArtifact = ArtifactCache.getActive(artifactId, branch);
         if (relatedArtifact == null) {
            return ArtifactQuery.getArtifactFromId(artifactId, branch);
         }
         return relatedArtifact;
      }

      @Override
      public String getLazyArtifactName(int artifactId, Branch branch) {
         Artifact artifact = ArtifactCache.getActive(artifactId, branch);
         return artifact != null ? artifact.getName() : "Unloaded";
      }

      @Override
      public void deleteFromRelationOrder(Artifact aArtifact, Artifact bArtifact, RelationType relationType) throws OseeCoreException {
         RelationTypeSideSorter aSorter = RelationManager.createTypeSideSorter(aArtifact, relationType, SIDE_B);
         aSorter.removeItem(null, bArtifact);

         RelationTypeSideSorter bSorter = RelationManager.createTypeSideSorter(bArtifact, relationType, SIDE_A);
         bSorter.removeItem(null, aArtifact);
      }

      @Override
      public void updateCachedArtifact(int artId, Branch branch) {
         ArtifactCache.updateCachedArtifact(artId, branch.getId());
      }
   }
}
