/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.relation.impl;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DEFAULT_HIERARCHY;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.IS_CHILD;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.IS_PARENT;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.core.enums.RelationSorter.PREEXISTING;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import static org.eclipse.osee.framework.jdk.core.util.Conditions.checkNotNull;
import static org.eclipse.osee.framework.jdk.core.util.Strings.emptyString;
import static org.eclipse.osee.orcs.core.internal.util.OrcsConditions.checkBranch;
import static org.eclipse.osee.orcs.core.internal.util.OrcsConditions.checkOnGraph;
import static org.eclipse.osee.orcs.core.internal.util.OrcsConditions.checkRelateSelf;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationResolver;
import org.eclipse.osee.orcs.core.internal.relation.RelationTypeValidity;
import org.eclipse.osee.orcs.core.internal.relation.RelationVisitor;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderChange;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderManager;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderManagerFactory;
import org.eclipse.osee.orcs.core.internal.search.QueryModule.QueryModuleProvider;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public class RelationManagerImpl implements RelationManager {

   private final Log logger;
   private final RelationTypeValidity validity;
   private final RelationResolver resolver;
   private final RelationFactory relationFactory;
   private final OrderManagerFactory orderFactory;
   private final QueryModuleProvider provider;
   private final RelationTypes relationTypes;

   public RelationManagerImpl(Log logger, RelationTypeValidity validity, RelationResolver resolver, RelationFactory relationFactory, OrderManagerFactory orderFactory, QueryModuleProvider queryProvider, RelationTypes relationTypes) {
      this.logger = logger;
      this.validity = validity;
      this.resolver = resolver;
      this.relationFactory = relationFactory;
      this.orderFactory = orderFactory;
      this.provider = queryProvider;
      this.relationTypes = relationTypes;
   }

   @Override
   public int getMaximumRelationAllowed(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side) {
      Conditions.checkNotNull(node, "node");
      return validity.getMaximumRelationsAllowed(type, node.getArtifactTypeId(), side);
   }

   @Override
   public Collection<RelationTypeId> getValidRelationTypes(OrcsSession session, Artifact node) {
      Conditions.checkNotNull(node, "node");
      return validity.getValidRelationTypes(node.getArtifactTypeId());
   }

   @Override
   public void accept(OrcsSession session, GraphData graph, Artifact node, RelationVisitor visitor) {
      checkOnGraph(graph, node);
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies container = graph.getAdjacencies(node);
      if (container != null) {
         container.accept(visitor);
      } else {
         logger.warn("Unable to find relation container for [%s]", node.getExceptionString());
      }
   }

   @Override
   public boolean hasDirtyRelations(OrcsSession session, Artifact node) {
      GraphData graph = node.getGraph();
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies container = graph.getAdjacencies(node);
      return container != null ? container.hasDirty() : false;
   }

   @Override
   public Collection<RelationTypeId> getExistingRelationTypes(OrcsSession session, Artifact node) {
      checkNotNull(node, "node");
      GraphData graph = node.getGraph();
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies container = graph.getAdjacencies(node);
      Collection<RelationTypeId> toReturn = null;
      if (container != null) {
         toReturn = container.getExistingTypes(DeletionFlag.EXCLUDE_DELETED);
      } else {
         logger.warn("Unable to find relation container for [%s]", node.getExceptionString());
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }

   @Override
   public int getRelatedCount(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side) {
      return getRelatedCount(session, type, node, side, EXCLUDE_DELETED);
   }

   @Override
   public int getRelatedCount(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side, DeletionFlag includeDeleted) {
      return getRelations(session, type, node, side, includeDeleted).size();
   }

   @Override
   public boolean areRelated(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode) {
      return getRelation(session, aNode, type, bNode, EXCLUDE_DELETED).size() > 0;
   }

   @Override
   public <T extends Artifact> T getParent(OrcsSession session, Artifact child) {
      ResultSet<T> toReturn = getRelated(session, DEFAULT_HIERARCHY, child, IS_CHILD);
      return toReturn.getOneOrNull();
   }

   @Override
   public <T extends Artifact> ResultSet<T> getChildren(OrcsSession session, Artifact parent) {
      return getRelated(session, DEFAULT_HIERARCHY, parent, IS_PARENT);
   }

   @Override
   public <T extends Artifact> ResultSet<T> getRelated(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side) {
      return getRelated(session, type, node, side, EXCLUDE_DELETED);
   }

   @Override
   public <T extends Artifact> ResultSet<T> getRelated(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side, DeletionFlag flag) {
      List<Relation> links = getRelations(session, type, node, side, flag);
      List<T> result = null;
      if (links.isEmpty()) {
         result = Collections.emptyList();
      } else {
         RelationSide otherSide = side.oppositeSide();
         GraphData graph = node.getGraph();
         result = resolver.resolve(session, graph, links, otherSide);
         if (result.size() > 1) {
            OrderManager orderManager = orderFactory.createOrderManager(node);
            RelationTypeSide key = RelationTypeSide.create(relationTypes.get(type), otherSide);
            orderManager.sort(key, result);
         }
      }
      return ResultSets.newResultSet(result);
   }

   @Override
   public String getRationale(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode) {
      ResultSet<Relation> result = getRelation(session, aNode, type, bNode, EXCLUDE_DELETED);
      return result.getExactlyOne().getRationale();
   }

   @Override
   public void setRationale(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode, String rationale) {
      ResultSet<Relation> result = getRelation(session, aNode, type, bNode, EXCLUDE_DELETED);
      Relation relation = result.getExactlyOne();
      relation.setRationale(rationale);
   }

   ///////////////////////// RELATE NODES ///////////////////
   @Override
   public void addChild(OrcsSession session, Artifact parent, Artifact child) {
      unrelateFromAll(session, DEFAULT_HIERARCHY, child, IS_CHILD);
      relate(session, parent, DEFAULT_HIERARCHY, child);
   }

   @Override
   public void addChildren(OrcsSession session, Artifact parent, List<? extends Artifact> children) {
      for (Artifact child : children) {
         addChild(session, parent, child);
      }
   }

   @Override
   public void relate(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode) {
      relate(session, aNode, type, bNode, emptyString(), PREEXISTING);
   }

   @Override
   public void relate(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode, String rationale) {
      relate(session, aNode, type, bNode, rationale, PREEXISTING);
   }

   @Override
   public void relate(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode, RelationSorter sortType) {
      relate(session, aNode, type, bNode, emptyString(), sortType);
   }

   @Override
   public void relate(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode, String rationale, RelationSorter sortType) {
      checkBranch(aNode, bNode);
      checkRelateSelf(aNode, bNode);
      GraphData graph = getGraph(aNode, bNode);

      validity.checkRelationTypeValid(type, aNode, SIDE_A);
      validity.checkRelationTypeValid(type, bNode, SIDE_B);

      // Check we can create the type on other side of each node
      checkMultiplicityCanAdd(session, type, aNode, bNode);

      Relation relation = getRelation(session, aNode, type, bNode, INCLUDE_DELETED).getOneOrNull();
      boolean updated = false;
      if (relation == null) {
         relation = relationFactory.createRelation(aNode, type, bNode, rationale);
         graph.<RelationNodeAdjacencies> getAdjacencies(aNode).add(type, relation);
         graph.<RelationNodeAdjacencies> getAdjacencies(bNode).add(type, relation);
         updated = true;
      }
      if (relation.isDeleted()) {
         relation.unDelete();
         updated = true;
      }
      if (updated) {
         relation.setDirty();
         order(session, type, aNode, SIDE_A, sortType, OrderOp.ADD_TO_ORDER, Collections.singleton(bNode));
      }
   }

   private GraphData getGraph(Artifact aNode, Artifact bNode) {
      checkBranch(aNode, bNode);
      return aNode.getGraph().getTransaction().isOlderThan(
         bNode.getGraph().getTransaction()) ? bNode.getGraph() : aNode.getGraph();
   }

   private void checkMultiplicityCanAdd(OrcsSession session, RelationTypeId type, Artifact aNode, Artifact bNode) {
      int bSideCount = getRelations(session, type, aNode, SIDE_A, EXCLUDE_DELETED).size();
      int bSideMax = validity.getMaximumRelationsAllowed(type, bNode.getArtifactTypeId(), SIDE_B);

      if (bSideCount >= bSideMax) {
         throw new OseeStateException("Relation type [%s] on [%s] exceeds max occurrence rule on [%s]", type, SIDE_B,
            aNode.getExceptionString());
      }

      int aSideCount = getRelations(session, type, bNode, SIDE_B, EXCLUDE_DELETED).size();
      int aSideMax = validity.getMaximumRelationsAllowed(type, aNode.getArtifactTypeId(), SIDE_A);

      if (aSideCount >= aSideMax) {
         throw new OseeStateException("Relation type [%s] on [%s] exceeds max occurrence rule on [%s]", type, SIDE_A,
            bNode.getExceptionString());
      }
   }

   ///////////////////////// UNRELATE NODES ///////////////////
   @Override
   public void unrelate(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode) {
      Relation relation = getRelation(session, aNode, type, bNode, EXCLUDE_DELETED).getOneOrNull();
      boolean modified = false;
      if (relation != null) {
         relation.delete();
         modified = true;
      }
      if (modified) {
         order(session, type, aNode, SIDE_A, OrderOp.REMOVE_FROM_ORDER, Collections.singleton(bNode));
      }
   }

   @Override
   public void unrelateFromAll(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side) {
      List<Relation> relations = getRelations(session, type, node, side, EXCLUDE_DELETED);

      RelationSide otherSide = side.oppositeSide();
      GraphData graph = node.getGraph();
      resolver.resolve(session, graph, relations, otherSide);

      boolean modified = false;
      Set<Artifact> otherNodes = new LinkedHashSet<>();
      for (Relation relation : relations) {
         relation.delete();
         Integer artId = relation.getIdForSide(otherSide);
         Artifact otherNode = graph.getNode(artId);
         otherNodes.add(otherNode);
         modified = true;
      }
      if (modified) {
         order(session, type, node, side, OrderOp.REMOVE_FROM_ORDER, otherNodes);
      }
   }

   @Override
   public void unrelateFromAll(OrcsSession session, Artifact node) {
      unrelate(session, node, true);
   }

   private void unrelate(OrcsSession session, Artifact node, boolean reorderRelations) {
      checkNotNull(node, "node");
      if (node.isDeleteAllowed()) {

         GraphData graph = node.getGraph();
         List<Relation> relations = getRelations(session, node, EXCLUDE_DELETED);
         resolver.resolve(session, graph, relations, RelationSide.values());

         ResultSet<Artifact> children = getChildren(session, node);
         for (Artifact child : children) {
            unrelate(session, child, false);
         }

         try {
            node.delete();

            if (relations != null && !relations.isEmpty()) {
               Map<RelationTypeId, RelationSide> typesToRemove = new HashMap<>();
               for (Relation relation : relations) {
                  relation.delete();
                  RelationTypeId type = relation.getRelationType();
                  RelationSide otherSide = relation.getIdForSide(SIDE_A) == node.getLocalId() ? SIDE_B : SIDE_A;
                  typesToRemove.put(type, otherSide);
               }

               if (!typesToRemove.isEmpty()) {
                  OrderManager orderManager = orderFactory.createOrderManager(node);

                  for (Entry<RelationTypeId, RelationSide> entry : typesToRemove.entrySet()) {
                     RelationTypeId type = entry.getKey();
                     RelationSide side = entry.getValue();

                     List<Relation> sideLinks = getRelations(session, type, node, side, EXCLUDE_DELETED);
                     List<Artifact> nodes = resolver.resolve(session, graph, sideLinks, side);

                     RelationTypeSide asTypeSide = RelationTypeSide.create(relationTypes.get(type), side);
                     orderManager.setOrder(asTypeSide, nodes);
                  }
               }
            }
         } catch (OseeCoreException ex) {
            node.unDelete();
            throw ex;
         }
      }
   }

   ///////////////////////// READ HELPERS ///////////////////
   private void ensureRelationsInitialized(OrcsSession session, GraphData graph, Artifact node) {
      if (graph.getAdjacencies(node) == null) {
         RelationNodeAdjacencies container = relationFactory.createRelationContainer();
         graph.addAdjacencies(node, container);
      }
   }

   private ResultSet<Relation> getRelation(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode, DeletionFlag inludeDeleted) {
      GraphData graph = getGraph(aNode, bNode);
      checkNotNull(session, "session");
      checkNotNull(type, "relationType");

      ensureRelationsInitialized(session, graph, aNode);
      ensureRelationsInitialized(session, graph, bNode);

      RelationNodeAdjacencies aAdjacencies = graph.getAdjacencies(aNode);
      RelationNodeAdjacencies bAdjacencies = graph.getAdjacencies(bNode);

      Relation relation = aAdjacencies.getRelation(aNode, type, bNode, inludeDeleted);
      if (relation != null) {
         bAdjacencies.add(type, relation);
      } else {
         relation = bAdjacencies.getRelation(aNode, type, bNode, inludeDeleted);
         if (relation != null) {
            aAdjacencies.add(type, relation);
         }
      }
      return ResultSets.singleton(relation);
   }

   private List<Relation> getRelations(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side, DeletionFlag includeDeleted) {
      checkNotNull(session, "session");
      checkNotNull(type, "relationType");
      checkNotNull(side, "relationSide");
      checkNotNull(node, "node");

      GraphData graph = node.getGraph();
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies adjacencies = graph.getAdjacencies(node);
      return adjacencies.getList(type, includeDeleted, node, side);
   }

   @Override
   public List<Relation> getRelations(OrcsSession session, Artifact node, DeletionFlag includeDeleted) {
      checkNotNull(session, "session");
      GraphData graph = node.getGraph();
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies adjacencies = graph.getAdjacencies(node);
      return adjacencies.getList(includeDeleted);
   }

   private static enum OrderOp {
      ADD_TO_ORDER,
      REMOVE_FROM_ORDER;
   }

   private void order(OrcsSession session, RelationTypeId type, Artifact node1, RelationSide side, OrderOp op, Collection<? extends Artifact> node2) {
      order(session, type, node1, side, PREEXISTING, op, node2);
   }

   private void order(OrcsSession session, RelationTypeId type, Artifact node1, RelationSide side, RelationSorter sorterId, OrderOp op, Collection<? extends Artifact> node2) {
      OrderManager orderManager = orderFactory.createOrderManager(node1);

      RelationSide orderSide = side.oppositeSide();
      RelationTypeSide key = RelationTypeSide.create(relationTypes.get(type), orderSide);
      RelationSorter sorterIdToUse = sorterId;
      if (sorterIdToUse == PREEXISTING) {
         sorterIdToUse = orderManager.getSorterId(key);
      }
      List<ArtifactToken> relatives = Collections.emptyList();
      if (USER_DEFINED == sorterIdToUse) {
         ResultSet<Artifact> arts = getRelated(session, type, node1, side);
         relatives = new LinkedList<>();
         for (Artifact art : arts) {
            relatives.add(art);
         }
         relatives.removeAll(node2); // ensure no duplicates

         if (OrderOp.ADD_TO_ORDER == op) {
            relatives.addAll(node2); // always add to the end
         }
      }
      orderManager.setOrder(key, sorterIdToUse, relatives);
   }

   @Override
   public void order(OrcsSession session, Artifact node1, RelationTypeId type, RelationSide side, List<? extends Artifact> node2) {
      OrderManager orderManager = orderFactory.createOrderManager(node1);
      RelationTypeSide key = RelationTypeSide.create(relationTypes.get(type), side);
      orderManager.setOrder(key, RelationSorter.USER_DEFINED, node2);
   }

   @Override
   public void cloneRelations(OrcsSession session, Artifact source, Artifact destination) {
      ensureRelationsInitialized(session, source.getGraph(), source);
      RelationNodeAdjacencies adjacencies1 = source.getGraph().getAdjacencies(source);
      if (adjacencies1 != null) {
         Collection<Relation> all = adjacencies1.getAll();
         if (!all.isEmpty()) {
            RelationNodeAdjacencies adjacencies2 = relationFactory.createRelationContainer();
            destination.getGraph().addAdjacencies(destination, adjacencies2);
            for (Relation relation : adjacencies1.getAll()) {
               Relation newRel = relationFactory.clone(relation);
               RelationTypeId relationType = RelationTypeId.valueOf(newRel.getOrcsData().getTypeUuid());
               adjacencies2.add(relationType, newRel);
            }
         }
      }
   }

   @Override
   public void introduce(OrcsSession session, BranchId branch, Artifact source, Artifact destination) {
      ensureRelationsInitialized(session, source.getGraph(), source);

      Collection<RelationTypeId> validRelationTypes = getValidRelationTypes(session, destination);
      RelationNodeAdjacencies sourceAdjacencies = source.getGraph().getAdjacencies(source);
      RelationNodeAdjacencies destinationAdjacencies = destination.getGraph().getAdjacencies(destination);
      if (sourceAdjacencies != null) {
         for (Relation sourceRel : sourceAdjacencies.getAll()) {
            if (validRelationTypes.contains(sourceRel.getRelationType())) {
               Relation destinationRel =
                  findRelationByLocalId(destinationAdjacencies, sourceRel.getOrcsData().getLocalId());
               Relation introduceRelation = relationFactory.introduce(branch, sourceRel.getOrcsData());
               if (destinationRel != null) {
                  destinationRel.setOrcsData(introduceRelation.getOrcsData());
                  destinationRel.setDirty();
               }
            }
         }
      }
      // relation order
      String orderData = source.getOrderData();
      if (!orderData.isEmpty()) {
         destination.storeOrderData(OrderChange.Forced, source.getOrderData());
      }
   }

   @Override
   public void setApplicabilityId(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode, ApplicabilityId applicId) {
      ResultSet<Relation> result = getRelation(session, aNode, type, bNode, EXCLUDE_DELETED);
      Relation relation = result.getExactlyOne();
      relation.setApplicabilityId(applicId);
   }

   private Relation findRelationByLocalId(RelationNodeAdjacencies adjacencies, Integer id) {
      for (Relation rel : adjacencies.getAll()) {
         if (id.equals(rel.getOrcsData().getLocalId())) {
            return rel;
         }
      }
      return null;
   }

}
