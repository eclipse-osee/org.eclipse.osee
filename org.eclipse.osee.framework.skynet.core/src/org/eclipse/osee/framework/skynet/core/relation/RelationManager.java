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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactKey;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderFactory;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Ryan D. Brooks
 */
public class RelationManager {
   // Indexed by ArtifactKey so that map does not hold strong reference to artifact which allows it to be garbage collected
   // the branch is accounted for because artifactkey includes the branch id
   private static final CompositeKeyHashMap<ArtifactKey, RelationType, List<RelationLink>> relationsByType =
         new CompositeKeyHashMap<ArtifactKey, RelationType, List<RelationLink>>(1024, true);

   private static final String GET_DELETED_ARTIFACT =
         "INSERT INTO osee_join_artifact (query_id, insert_time, art_id, branch_id, transaction_id) (SELECT DISTINCT ?, sysdate, %s_art_id, det.branch_id, ? FROM osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.branch_id = ? AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id AND rel.rel_link_type_id = ? AND %s_art_id = ? AND tx_current in (2, 3))";

   private static RelationSorterProvider relationSorterProvider = new RelationSorterProvider();
   private static RelationOrderFactory relationOrderFactory = new RelationOrderFactory();

   private static final ThreadLocal<ArtifactKey> threadLocalKey = new ThreadLocal<ArtifactKey>() {

      @Override
      protected ArtifactKey initialValue() {
         return new ArtifactKey(0, 0);
      }
   };

   private static RelationLink getLoadedRelation(Artifact artifact, int aArtifactId, int bArtifactId, RelationType relationType, boolean includeDeleted) {
      List<RelationLink> selectedRelations =
            getLoadedRelations(artifact.getArtId(), artifact.getBranch().getId(), relationType, includeDeleted);
      Set<RelationLink> relations = new HashSet<RelationLink>();
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if ((includeDeleted || !relation.isDeleted()) && relation.getAArtifactId() == aArtifactId && relation.getBArtifactId() == bArtifactId) {
               relations.add(relation);
            }
         }
      }
      if (relations.size() == 0) {
         return null;
      }
      if (relations.size() > 1) {
         OseeLog.log(Activator.class, Level.SEVERE, String.format(
               "Artifact A [%s] has [%d] relations of same type [%s] to Artifact B [%s]", artifact.getArtId(),
               relations.size(), relationType, bArtifactId));
      }
      return relations.iterator().next();
   }

   private static List<RelationLink> getLoadedRelations(int artifactId, int branchId, RelationType relationType, boolean includeDeleted) {
      List<RelationLink> selectedRelations =
            relationsByType.get(threadLocalKey.get().getKey(artifactId, branchId), relationType);
      List<RelationLink> relations = new ArrayList<RelationLink>();
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if ((includeDeleted || !relation.isDeleted()) && (relation.getAArtifactId() == artifactId || relation.getBArtifactId() == artifactId)) {
               relations.add(relation);
            }
         }
      }
      return relations;
   }

   /**
    * @return Returns all cached relations including deleted relations
    * @throws OseeArgumentException
    */
   private static RelationLink getLoadedRelation(Artifact artifact, int aArtifactId, int bArtifactId, RelationType relationType) throws OseeArgumentException {
      RelationLink relationLink = getLoadedRelation(artifact, aArtifactId, bArtifactId, relationType, false);

      if (relationLink == null) {
         throw new OseeArgumentException(
               "A relation link of type: " + relationType.getName() + "does exist in the cache between a artifact: " + aArtifactId + " and b artifact:" + bArtifactId);
      }

      return relationLink;
   }

   public static RelationLink getLoadedRelationById(int relLinkId, int aArtifactId, int bArtifactId, Branch aBranch, Branch bBranch) {
      RelationLink relation = null;
      for (RelationLink link : getRelationsAll(aArtifactId, aBranch.getId(), true)) {
         if (link.getRelationId() == relLinkId) {
            relation = link;
            break;
         }
      }
      for (RelationLink link : getRelationsAll(bArtifactId, bBranch.getId(), true)) {
         if (link.getRelationId() == relLinkId) {
            relation = link;
            break;
         }
      }
      return relation;
   }

   public static RelationLink getLoadedRelation(RelationType relationType, int aArtifactId, int bArtifactId, Branch aBranch, Branch bBranch) {
      RelationLink relation = null;
      List<RelationLink> relations = getLoadedRelations(aArtifactId, aBranch.getId(), relationType, true);
      for (RelationLink rel : relations) {
         if (rel.getBArtifactId() == bArtifactId) {
            relation = rel;
            break;
         }
      }

      if (relation == null) {
         relations = getLoadedRelations(bArtifactId, bBranch.getId(), relationType, true);
         for (RelationLink rel : relations) {
            if (rel.getAArtifactId() == aArtifactId) {
               relation = rel;
               break;
            }
         }
      }
      return relation;
   }

   /**
    * Store the newly instantiated relation from the perspective of relationSide in its appropriate order
    */
   public static void manageRelation(RelationLink newRelation, RelationSide relationSide) {
      if (RelationLink.isRelationUnderTest() && newRelation.getRelationId() == RelationLink.RELATION_ID_UNDER_TEST) {
         System.out.println("RelationManager.manageRelation relationId == " + RelationLink.RELATION_ID_UNDER_TEST + " for side " + relationSide);
      }
      Artifact artifact =
            ArtifactCache.getActive(newRelation.getArtifactId(relationSide), newRelation.getBranch(relationSide));
      if (artifact != null) {
         List<RelationLink> artifactsRelations =
               getFlattenedList(relationsByType.getValues(threadLocalKey.get().getKey(artifact)));
         if (artifactsRelations == null) {
            artifactsRelations = new CopyOnWriteArrayList<RelationLink>();
         }

         // Verify that relation is unique by aArtId, bArtId and relTypeId; Needs to be cleaned up in DB, Only log problem.
         // Need to do this check before to catch the relations that are .equal but not ==
         for (RelationLink relation : artifactsRelations) {
            if (relation.getAArtifactId() == newRelation.getAArtifactId() && //
            relation.getBArtifactId() == newRelation.getBArtifactId() && //
            relation.getRelationType() == newRelation.getRelationType() && //
            relation != newRelation && relation.isDeleted() == newRelation.isDeleted()) {
               OseeLog.log(Activator.class, Level.SEVERE, String.format(
                     "Duplicate relation objects for same relation for RELATION 1 [%s] RELATION 2 [%s]", relation,
                     newRelation));
            }
         }

         if (artifactsRelations.contains(newRelation)) {
            // Always want to return if relation link is already managed
            return;
         }

         artifactsRelations.add(newRelation);

         List<RelationLink> selectedRelations =
               relationsByType.get(threadLocalKey.get().getKey(artifact), newRelation.getRelationType());
         if (selectedRelations == null) {
            selectedRelations = new CopyOnWriteArrayList<RelationLink>();
            relationsByType.put(new ArtifactKey(artifact), newRelation.getRelationType(), selectedRelations);
         }
         if (selectedRelations.contains(newRelation)) {
            OseeLog.log(Activator.class, Level.SEVERE, String.format(
                  "Duplicate relationByType objects for same relation for Relation [%s] Artifact (%s)[%s]",
                  newRelation, artifact.getArtId(), artifact.getName()));
         }
         selectedRelations.add(newRelation);
      }
   }

   private static List<RelationLink> getFlattenedList(List<List<RelationLink>> values) {
      List<RelationLink> links = new ArrayList<RelationLink>();
      for (List<RelationLink> valueLinks : values) {
         for (RelationLink link : valueLinks) {
            links.add(link);
         }
      }
      return links;
   }

   private static List<Artifact> getRelatedArtifactsUnSorted(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
      return getRelatedArtifacts(artifact, relationType, relationSide, false);
   }

   private static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
      return getRelatedArtifacts(artifact, relationType, relationSide, true);
   }

   private static List<Artifact> getRelatedArtifacts(Artifact artifact, RelationType relationType, RelationSide relationSide, boolean sort) throws OseeCoreException {
      if (relationSide == null) {
         throw new OseeArgumentException("RelationSide cannot be null");
      }
      List<RelationLink> selectedRelations = null;
      if (relationType == null) {
         selectedRelations = getFlattenedList(relationsByType.getValues(threadLocalKey.get().getKey(artifact)));
      } else {
         selectedRelations = relationsByType.get(threadLocalKey.get().getKey(artifact), relationType);
      }

      if (selectedRelations == null) {
         return Collections.emptyList();
      }

      int queryId = ArtifactLoader.getNewQueryId();
      int mapCapacity = (int) (selectedRelations.size() * 1.25) + 1;
      List<Object[]> insertParameters = new ArrayList<Object[]>(mapCapacity);
      HashMap<Integer, Branch> insertMap = new HashMap<Integer, Branch>(mapCapacity);
      List<Artifact> relatedArtifacts = new ArrayList<Artifact>(selectedRelations.size());

      addRelatedArtifactIds(queryId, artifact, relatedArtifacts, insertParameters, insertMap, selectedRelations,
            relationSide);

      // This is for bulk loading so we do not lose are references
      @SuppressWarnings("unused")
      Collection<Artifact> bulkLoadedArtifacts;
      if (insertParameters.size() > 0) {
         bulkLoadedArtifacts =
               ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, false, false, false);
      }

      //now that bulk loading is done, put the artifacts in the right order and return them
      relatedArtifacts.clear();
      for (RelationLink relation : selectedRelations) {
         if (!relation.isDeleted()) {
            try {
               if (relation.getSide(artifact).isOppositeSide(relationSide)) {
                  relationSide = relation.getSide(artifact).oppositeSide();
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
      return relatedArtifacts;
   }

   private static void addRelatedArtifactIds(int queryId, Artifact artifact, Collection<Artifact> relatedArtifacts, List<Object[]> insertParameters, HashMap<Integer, Branch> insertMap, List<RelationLink> relations, RelationSide side) {
      if (relations == null) {
         return;
      }
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      for (RelationLink relation : relations) {
         if (!relation.isDeleted()) {
            RelationSide resolvedSide = null;
            if (relation.getSide(artifact) != side) {
               resolvedSide = side;
            }
            if (resolvedSide != null) {
               int artId = relation.getArtifactId(resolvedSide);
               Branch branch = relation.getBranch(resolvedSide);
               Artifact relatedArtifact = ArtifactCache.getActive(artId, branch.getId());
               if (relatedArtifact == null) {
                  if (!branch.equals(insertMap.get(artId))) {
                     insertMap.put(artId, branch);
                     insertParameters.add(new Object[] {queryId, insertTime, artId, branch.getId(),
                           SQL3DataType.INTEGER});
                  }
               } else {
                  relatedArtifacts.add(relatedArtifact);
               }
            }
         }
      }
   }

   public static Set<Artifact> getRelatedArtifacts(Collection<? extends Artifact> artifacts, int depth, IRelationEnumeration... relationEnums) throws OseeCoreException {
      return getRelatedArtifacts(artifacts, depth, false, relationEnums);
   }

   public static Set<Artifact> getRelatedArtifacts(Collection<? extends Artifact> artifacts, int depth, boolean allowDeleted, IRelationEnumeration... relationEnums) throws OseeCoreException {
      int queryId = ArtifactLoader.getNewQueryId();
      List<Object[]> insertParameters = new ArrayList<Object[]>(artifacts.size() * 8);
      HashMap<Integer, Branch> insertMap = new HashMap<Integer, Branch>(artifacts.size() * 8);
      Set<Artifact> relatedArtifacts = new HashSet<Artifact>(artifacts.size() * 8);
      Collection<Artifact> newArtifactsToSearch = new ArrayList<Artifact>(artifacts);
      Collection<Artifact> newArtifacts = new ArrayList<Artifact>();
      int oldArtifactCount = -1;
      for (int i = 0; i < depth && oldArtifactCount != relatedArtifacts.size(); i++) {
         oldArtifactCount = relatedArtifacts.size();
         insertParameters.clear();
         newArtifacts.clear();
         for (Artifact artifact : newArtifactsToSearch) {
            List<RelationLink> selectedRelations = null;

            if (relationEnums.length == 0) {
               selectedRelations = getFlattenedList(relationsByType.getValues(threadLocalKey.get().getKey(artifact)));
               addRelatedArtifactIds(queryId, artifact, newArtifacts, insertParameters, insertMap, selectedRelations,
                     RelationSide.SIDE_B);
            } else {
               for (IRelationEnumeration relationEnum : relationEnums) {
                  selectedRelations =
                        relationsByType.get(threadLocalKey.get().getKey(artifact),
                              RelationTypeManager.getType(relationEnum));
                  addRelatedArtifactIds(queryId, artifact, newArtifacts, insertParameters, insertMap,
                        selectedRelations, relationEnum.getSide());
               }
            }
         }

         if (insertParameters.size() > 0) {
            newArtifacts.addAll(ArtifactLoader.loadArtifacts(queryId, ArtifactLoad.FULL, null, insertParameters, false,
                  false, allowDeleted));
         }
         newArtifactsToSearch.clear();
         newArtifactsToSearch.addAll(newArtifacts);
         relatedArtifacts.addAll(newArtifacts);
      }

      return relatedArtifacts;
   }

   @SuppressWarnings("unchecked")
   public static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationEnumeration relationEnum, boolean includeDeleted) throws OseeCoreException {
      RelationType relationType = RelationTypeManager.getType(relationEnum);
      if (includeDeleted) {
         List<Artifact> artifacts = getRelatedArtifacts(artifact, relationType, relationEnum.getSide());
         int queryId = ArtifactLoader.getNewQueryId();

         Object[] formatArgs = relationEnum.getSide().isSideA() ? new Object[] {"a", "b"} : new Object[] {"b", "a"};
         String sql = String.format(GET_DELETED_ARTIFACT, formatArgs);

         ConnectionHandler.runPreparedUpdate(sql, queryId, SQL3DataType.INTEGER, artifact.getBranch().getId(),
               relationType.getId(), artifact.getArtId());

         List<Artifact> deletedArtifacts =
               ArtifactLoader.loadArtifactsFromQueryId(queryId, ArtifactLoad.FULL, null, 4, false, false, true);

         if (artifacts.isEmpty()) {
            artifacts = new LinkedList<Artifact>();
         }
         for (Artifact artifactLoop : deletedArtifacts) {
            if (artifactLoop.isDeleted()) {
               artifacts.add(artifactLoop);
            }
         }

         return artifacts;
      } else {
         return getRelatedArtifacts(artifact, relationType, relationEnum.getSide());
      }

   }

   public static List<Artifact> getRelatedArtifactsUnSorted(Artifact artifact, IRelationEnumeration relationEnum) throws OseeCoreException {
      RelationType relationType = RelationTypeManager.getType(relationEnum);
      return getRelatedArtifactsUnSorted(artifact, relationType, relationEnum.getSide());
   }

   public static List<Artifact> getRelatedArtifacts(Artifact artifact, IRelationEnumeration relationEnum) throws OseeCoreException {
      return getRelatedArtifacts(artifact, RelationTypeManager.getType(relationEnum), relationEnum.getSide());
   }

   private static Artifact getRelatedArtifact(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
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

   public static Artifact getRelatedArtifact(Artifact artifact, IRelationEnumeration relationEnum) throws OseeCoreException {
      return getRelatedArtifact(artifact, RelationTypeManager.getType(relationEnum), relationEnum.getSide());
   }

   public static int getRelatedArtifactsCount(Artifact artifact, IRelationEnumeration relationTypeEnum) throws OseeCoreException {
      return getRelatedArtifactsCount(artifact, RelationTypeManager.getType(relationTypeEnum),
            relationTypeEnum.getSide());
   }

   public static int getRelatedArtifactsCount(Artifact artifact, RelationType relationType, RelationSide relationSide) {
      List<RelationLink> selectedRelations = relationsByType.get(threadLocalKey.get().getKey(artifact), relationType);

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
         relationsByType.removeValues(threadLocalKey.get().getKey(artifact));
      }
   }

   public static boolean hasDirtyLinks(Artifact artifact) {
      List<RelationLink> selectedRelations =
            getFlattenedList(relationsByType.getValues(threadLocalKey.get().getKey(artifact)));
      if (selectedRelations == null) {
         return false;
      }
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
      List<RelationLink> selectedRelations =
            getFlattenedList(relationsByType.getValues(threadLocalKey.get().getKey(artifact)));
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relation.isDirty()) {
               try {
                  return String.format("Relation\n\n[%s]\n\naSide [%s]\n\nbSide [%s]", relation,
                        relation.getArtifactA(), relation.getArtifactB());
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
               }
            }
         }
      }
      return null;
   }

   /**
    * @param transaction
    * @param artifact
    * @param relationType if not null persists the relations of this type, otherwise persists relations of all types
    */
   public static void persistRelationsFor(SkynetTransaction transaction, Artifact artifact, RelationType relationType) throws OseeCoreException {
      List<RelationLink> selectedRelations;
      if (relationType == null) {
         selectedRelations = getFlattenedList(relationsByType.getValues(threadLocalKey.get().getKey(artifact)));
      } else {
         selectedRelations = relationsByType.get(threadLocalKey.get().getKey(artifact), relationType);
      }

      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relation.isDirty()) {
               transaction.addRelation(relation);

               try {
                  Artifact artifactOnOtherSide = relation.getArtifactOnOtherSide(artifact);
                  List<RelationLink> otherSideRelations =
                        relationsByType.get(threadLocalKey.get().getKey(artifactOnOtherSide),
                              relation.getRelationType());
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

   public static List<RelationLink> getRelationsAll(int artId, int branchId, boolean includeDeleted) {
      List<RelationLink> selectedRelations =
            getFlattenedList(relationsByType.getValues(threadLocalKey.get().getKey(artId, branchId)));

      if (selectedRelations == null) {
         return Collections.emptyList();
      }

      List<RelationLink> relations = new ArrayList<RelationLink>(selectedRelations.size());
      for (RelationLink relation : selectedRelations) {
         if (!relation.isDeleted() || includeDeleted) {
            relations.add(relation);
         }
      }
      return relations;
   }

   public static List<RelationLink> getRelations(Artifact artifact, RelationType relationType, RelationSide relationSide) {
      List<RelationLink> selectedRelations = relationsByType.get(threadLocalKey.get().getKey(artifact), relationType);
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
      // TODO Fix this when fix cross branching (not writing or reading from db correctly)
      //      if (!artifactA.getBranch().equals(artifactB.getBranch())) {
      //         throw new OseeArgumentException("Cross branch linking is not yet supported.");
      //      }
      ensureSideWillSupport(artifactA, relationType, RelationSide.SIDE_A, 1);
      ensureSideWillSupport(artifactB, relationType, RelationSide.SIDE_B, 1);
   }

   /**
    * Check whether artifactCount number of additional artifacts of type artifactType can be related to the artifact on
    * side relationSide for relations of type relationType
    * 
    * @param relationType
    * @param relationSide
    * @param artifact
    * @param artifactCount
    * @throws OseeArgumentException
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
                     artifact.getName(), artifact.getArtifactTypeName(), relationSide.toString(),
                     relationType.getName(), multiplicity.asLimitLabel(relationSide.oppositeSide())));
      }
   }

   public static void deleteRelation(RelationType relationType, Artifact artifactA, Artifact artifactB) throws OseeCoreException {
      RelationLink relation = getLoadedRelation(artifactA, artifactA.getArtId(), artifactB.getArtId(), relationType);
      relation.delete(true);

      updateOrderListOnDelete(artifactA, relationType, RelationSide.SIDE_B, getRelatedArtifacts(artifactA,
            relationType, RelationSide.SIDE_B));
      updateOrderListOnDelete(artifactB, relationType, RelationSide.SIDE_A, getRelatedArtifacts(artifactB,
            relationType, RelationSide.SIDE_A));
   }

   public static void deleteRelationsAll(Artifact artifact, boolean reorderRelations) throws OseeCoreException {
      List<RelationLink> selectedRelations =
            getFlattenedList(relationsByType.getValues(threadLocalKey.get().getKey(artifact)));
      Set<Pair<RelationType, RelationSide>> typesToUpdate = new HashSet<Pair<RelationType, RelationSide>>();
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            typesToUpdate.add(new Pair<RelationType, RelationSide>(relation.getRelationType(), relation.getSide(
                  artifact).oppositeSide()));
            relation.delete(reorderRelations);
         }
      }

      for (Pair<RelationType, RelationSide> type : typesToUpdate) {
         updateOrderListOnDelete(artifact, type.getFirst(), type.getSecond(), getRelatedArtifacts(artifact,
               type.getFirst(), type.getSecond()));
      }
   }

   public static void deleteRelations(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
      List<RelationLink> selectedRelations = relationsByType.get(threadLocalKey.get().getKey(artifact), relationType);
      if (selectedRelations != null) {
         for (RelationLink relation : selectedRelations) {
            if (relation.getSide(artifact) != relationSide) {
               relation.delete(true);
            }
         }
      }

      updateOrderListOnDelete(artifact, relationType, relationSide, getRelatedArtifacts(artifact, relationType,
            relationSide));
   }

   /**
    * This method should only be called for unordered Relation Types. It does not handle reordering relation types that
    * maintain order.
    * 
    * @param artifact
    * @param relationType
    * @param relationSide
    * @throws OseeCoreException
    */
   public static void revertRelations(Artifact artifact, RelationType relationType, RelationSide relationSide) throws OseeCoreException {
      List<RelationLink> selectedRelations = relationsByType.get(threadLocalKey.get().getKey(artifact), relationType);
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
    * 
    * @throws OseeDataStoreException
    */
   public static void purgeRelationsFor(Artifact artifact) throws OseeDataStoreException {
      Collection<RelationLink> links =
            getFlattenedList(relationsByType.getValues(threadLocalKey.get().getKey(artifact)));
      if (!links.isEmpty()) {
         List<Object[]> batchArgs = new ArrayList<Object[]>(links.size());
         String PURGE_RELATION = "delete from osee_relation_link WHERE rel_link_id = ?";
         for (RelationLink link : links) {
            batchArgs.add(new Object[] {link.getRelationId()});
            link.markAsPurged();
         }
         ConnectionHandler.runBatchUpdate(PURGE_RELATION, batchArgs);
      }
   }

   public static void addRelation(RelationType relationType, Artifact artifactA, Artifact artifactB, String rationale) throws OseeCoreException {
      addRelation(null, relationType, artifactA, artifactB, rationale);
   }

   public static void addRelation(IRelationSorterId sorterId, RelationType relationType, Artifact artifactA, Artifact artifactB, String rationale) throws OseeCoreException {
      if (artifactA.equals(artifactB)) {
         throw new OseeArgumentException(String.format("Not valid to relate artifact [%s] to itself", artifactA));
      }
      RelationLink relation =
            getLoadedRelation(artifactA, artifactA.getArtId(), artifactB.getArtId(), relationType, true);

      if (relation == null) {
         ensureRelationCanBeAdded(relationType, artifactA, artifactB);

         relation = RelationLink.getOrCreate(artifactA, artifactB, relationType, rationale, ModificationType.NEW);
         relation.setDirty();

         RelationTypeSideSorter sorter = createTypeSideSorter(artifactA, relationType, RelationSide.SIDE_B);
         sorter.addItem(sorterId, artifactB);

         try {
            OseeEventManager.kickRelationModifiedEvent(RelationManager.class, RelationEventType.Added, relation,
                  relation.getABranch(), relationType.getName());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else if (relation.isDeleted()) {
         relation.undelete();
         RelationTypeSideSorter sorter = createTypeSideSorter(artifactA, relationType, RelationSide.SIDE_B);
         sorter.addItem(sorterId, artifactB);
      }
   }

   public static void setRelationRationale(Artifact artifactA, Artifact artifactB, RelationType relationType, String rationale) throws OseeCoreException {
      RelationLink relation = getRelationLink(artifactA, artifactB, relationType);
      relation.setRationale(rationale, true);
   }

   public static String getRelationRationale(Artifact artifactA, Artifact artifactB, RelationType relationType) throws OseeCoreException {
      RelationLink relation = getRelationLink(artifactA, artifactB, relationType);
      return relation.getRationale();
   }

   private static RelationLink getRelationLink(Artifact artifactA, Artifact artifactB, RelationType relationType) throws OseeCoreException {
      List<RelationLink> relationLinks = relationsByType.get(threadLocalKey.get().getKey(artifactA), relationType);
      for (RelationLink relation : relationLinks) {
         if (relation.getArtifactB().equals(artifactB)) {
            return relation;
         }
      }
      throw new OseeCoreException(String.format("Unable to find a relation link for type[%s] artA[%s] artB[%s]",
            relationType.getName(), artifactA.getName(), artifactB.getName()));
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
}