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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.jdk.core.util.Conditions.checkNotNull;
import static org.eclipse.osee.framework.jdk.core.util.Strings.emptyString;
import static org.eclipse.osee.orcs.core.internal.relation.RelationUtil.DEFAULT_HIERARCHY;
import static org.eclipse.osee.orcs.core.internal.relation.RelationUtil.IS_CHILD;
import static org.eclipse.osee.orcs.core.internal.relation.RelationUtil.IS_PARENT;
import static org.eclipse.osee.orcs.core.internal.relation.RelationUtil.asTypeSide;
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
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.proxy.impl.ExternalArtifactManagerImpl.ProxyProvider;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.relation.RelationResolver;
import org.eclipse.osee.orcs.core.internal.relation.RelationTypeValidity;
import org.eclipse.osee.orcs.core.internal.relation.RelationVisitor;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderChange;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderManager;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderManagerFactory;
import org.eclipse.osee.orcs.core.internal.search.QueryModule.QueryModuleProvider;
import org.eclipse.osee.orcs.data.ArtifactReadable;

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
   private final ProxyProvider proxy;

   public RelationManagerImpl(Log logger, RelationTypeValidity validity, RelationResolver resolver, RelationFactory relationFactory, OrderManagerFactory orderFactory, QueryModuleProvider queryProvider, ProxyProvider proxyProvider) {
      super();
      this.logger = logger;
      this.validity = validity;
      this.resolver = resolver;
      this.relationFactory = relationFactory;
      this.orderFactory = orderFactory;
      this.provider = queryProvider;
      this.proxy = proxyProvider;
   }

   @Override
   public int getMaximumRelationAllowed(OrcsSession session, IRelationType type, RelationNode node, RelationSide side) throws OseeCoreException {
      Conditions.checkNotNull(node, "node");
      return validity.getMaximumRelationsAllowed(type, node.getArtifactType(), side);
   }

   @Override
   public Collection<? extends IRelationType> getValidRelationTypes(OrcsSession session, RelationNode node) throws OseeCoreException {
      Conditions.checkNotNull(node, "node");
      return validity.getValidRelationTypes(node.getArtifactType());
   }

   @Override
   public void accept(OrcsSession session, GraphData graph, RelationNode node, RelationVisitor visitor) throws OseeCoreException {
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
   public boolean hasDirtyRelations(OrcsSession session, RelationNode node) throws OseeCoreException {
      GraphData graph = node.getGraph();
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies container = graph.getAdjacencies(node);
      return container != null ? container.hasDirty() : false;
   }

   @Override
   public Collection<? extends IRelationType> getExistingRelationTypes(OrcsSession session, RelationNode node) throws OseeCoreException {
      checkNotNull(node, "node");
      GraphData graph = node.getGraph();
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies container = graph.getAdjacencies(node);
      Collection<? extends IRelationType> toReturn = null;
      if (container != null) {
         toReturn = container.getExistingTypes(DeletionFlag.EXCLUDE_DELETED);
      } else {
         logger.warn("Unable to find relation container for [%s]", node.getExceptionString());
         toReturn = Collections.<IRelationType> emptyList();
      }
      return toReturn;
   }

   @Override
   public int getRelatedCount(OrcsSession session, IRelationType type, RelationNode node, RelationSide side) throws OseeCoreException {
      return getRelatedCount(session, type, node, side, EXCLUDE_DELETED);
   }

   @Override
   public int getRelatedCount(OrcsSession session, IRelationType type, RelationNode node, RelationSide side, DeletionFlag includeDeleted) throws OseeCoreException {
      return getRelations(session, type, node, side, includeDeleted).size();
   }

   @Override
   public boolean areRelated(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode) throws OseeCoreException {
      return getRelation(session, aNode, type, bNode, EXCLUDE_DELETED).size() > 0;
   }

   @Override
   public <T extends RelationNode> T getParent(OrcsSession session, RelationNode child) throws OseeCoreException {
      ResultSet<T> toReturn = getRelated(session, DEFAULT_HIERARCHY, child, IS_CHILD);
      return toReturn.getOneOrNull();
   }

   @Override
   public <T extends RelationNode> ResultSet<T> getChildren(OrcsSession session, RelationNode parent) throws OseeCoreException {
      return getRelated(session, DEFAULT_HIERARCHY, parent, IS_PARENT);
   }

   @Override
   public <T extends RelationNode> ResultSet<T> getRelated(OrcsSession session, IRelationType type, RelationNode node, RelationSide side) throws OseeCoreException {
      List<Relation> links = getRelations(session, type, node, side, EXCLUDE_DELETED);
      List<T> result = null;
      if (links.isEmpty()) {
         result = Collections.emptyList();
      } else {
         RelationSide otherSide = side.oppositeSide();
         GraphData graph = node.getGraph();
         result = resolver.resolve(session, graph, links, otherSide);
         if (result.size() > 1) {
            OrderManager orderManager = orderFactory.createOrderManager(node);
            IRelationTypeSide key = asTypeSide(type, otherSide);
            orderManager.sort(key, result);
         }
      }
      return ResultSets.newResultSet(result);
   }

   @Override
   public String getRationale(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode) throws OseeCoreException {
      ResultSet<Relation> result = getRelation(session, aNode, type, bNode, EXCLUDE_DELETED);
      return result.getExactlyOne().getRationale();
   }

   @Override
   public void setRationale(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode, String rationale) throws OseeCoreException {
      ResultSet<Relation> result = getRelation(session, aNode, type, bNode, EXCLUDE_DELETED);
      Relation relation = result.getExactlyOne();
      relation.setRationale(rationale);
   }

   ///////////////////////// RELATE NODES ///////////////////
   @Override
   public void addChild(OrcsSession session, RelationNode parent, RelationNode child) throws OseeCoreException {
      unrelateFromAll(session, DEFAULT_HIERARCHY, child, IS_CHILD);
      relate(session, parent, DEFAULT_HIERARCHY, child);
   }

   @Override
   public void addChildren(OrcsSession session, RelationNode parent, List<? extends RelationNode> children) throws OseeCoreException {
      for (RelationNode child : children) {
         addChild(session, parent, child);
      }
   }

   @Override
   public void relate(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode) throws OseeCoreException {
      relate(session, aNode, type, bNode, emptyString(), RelationOrderBaseTypes.PREEXISTING);
   }

   @Override
   public void relate(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode, String rationale) throws OseeCoreException {
      relate(session, aNode, type, bNode, rationale, RelationOrderBaseTypes.PREEXISTING);
   }

   @Override
   public void relate(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode, IRelationSorterId sortType) throws OseeCoreException {
      relate(session, aNode, type, bNode, emptyString(), sortType);
   }

   @Override
   public void relate(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode, String rationale, IRelationSorterId sortType) throws OseeCoreException {
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
         graph.<RelationNodeAdjacencies> getAdjacencies(aNode).add(type.getGuid(), relation);
         graph.<RelationNodeAdjacencies> getAdjacencies(bNode).add(type.getGuid(), relation);
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

   private GraphData getGraph(RelationNode aNode, RelationNode bNode) {
      checkBranch(aNode, bNode);
      return aNode.getGraph().getTransaction().isOlderThan(
         bNode.getGraph().getTransaction()) ? bNode.getGraph() : aNode.getGraph();
   }

   private void checkMultiplicityCanAdd(OrcsSession session, IRelationType type, RelationNode aNode, RelationNode bNode) {
      int bSideCount = getRelations(session, type, aNode, SIDE_A, EXCLUDE_DELETED).size();
      int bSideMax = validity.getMaximumRelationsAllowed(type, bNode.getArtifactType(), SIDE_B);

      if (bSideCount >= bSideMax) {
         throw new OseeStateException("Relation type [%s] on [%s] exceeds max occurrence rule on [%s]", type.getName(),
            SIDE_B, aNode.getExceptionString());
      }

      int aSideCount = getRelations(session, type, bNode, SIDE_B, EXCLUDE_DELETED).size();
      int aSideMax = validity.getMaximumRelationsAllowed(type, aNode.getArtifactType(), SIDE_A);

      if (aSideCount >= aSideMax) {
         throw new OseeStateException("Relation type [%s] on [%s] exceeds max occurrence rule on [%s]", type.getName(),
            SIDE_A, bNode.getExceptionString());
      }
   }

   ///////////////////////// UNRELATE NODES ///////////////////
   @Override
   public void unrelate(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode) throws OseeCoreException {
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
   public void unrelateFromAll(OrcsSession session, IRelationType type, RelationNode node, RelationSide side) throws OseeCoreException {
      List<Relation> relations = getRelations(session, type, node, side, EXCLUDE_DELETED);

      RelationSide otherSide = side.oppositeSide();
      GraphData graph = node.getGraph();
      resolver.resolve(session, graph, relations, otherSide);

      boolean modified = false;
      Set<RelationNode> otherNodes = new LinkedHashSet<>();
      for (Relation relation : relations) {
         relation.delete();
         Integer artId = relation.getLocalIdForSide(otherSide);
         RelationNode otherNode = graph.getNode(artId);
         otherNodes.add(otherNode);
         modified = true;
      }
      if (modified) {
         order(session, type, node, side, OrderOp.REMOVE_FROM_ORDER, otherNodes);
      }
   }

   @Override
   public void unrelateFromAll(OrcsSession session, RelationNode node) throws OseeCoreException {
      unrelate(session, node, true);
   }

   private void unrelate(OrcsSession session, RelationNode node, boolean reorderRelations) throws OseeCoreException {
      checkNotNull(node, "node");
      if (node.isDeleteAllowed()) {

         GraphData graph = node.getGraph();
         List<Relation> relations = getRelations(session, node, EXCLUDE_DELETED);
         resolver.resolve(session, graph, relations, RelationSide.values());

         ResultSet<RelationNode> children = getChildren(session, node);
         for (RelationNode child : children) {
            unrelate(session, child, false);
         }

         try {
            node.delete();

            if (relations != null && !relations.isEmpty()) {
               Map<IRelationType, RelationSide> typesToRemove = new HashMap<>();
               for (Relation relation : relations) {
                  relation.delete();
                  IRelationType type = relation.getRelationType();
                  RelationSide otherSide = relation.getLocalIdForSide(SIDE_A) == node.getLocalId() ? SIDE_B : SIDE_A;
                  typesToRemove.put(type, otherSide);
               }

               if (!typesToRemove.isEmpty()) {
                  OrderManager orderManager = orderFactory.createOrderManager(node);

                  for (Entry<IRelationType, RelationSide> entry : typesToRemove.entrySet()) {
                     IRelationType type = entry.getKey();
                     RelationSide side = entry.getValue();

                     List<Relation> sideLinks = getRelations(session, type, node, side, EXCLUDE_DELETED);
                     List<RelationNode> nodes = resolver.resolve(session, graph, sideLinks, side);

                     IRelationTypeSide asTypeSide = asTypeSide(type, side);
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
   private void ensureRelationsInitialized(OrcsSession session, GraphData graph, RelationNode node) throws OseeCoreException {
      if (graph.getAdjacencies(node) == null) {
         RelationNodeAdjacencies container = relationFactory.createRelationContainer();
         graph.addAdjacencies(node, container);
      }
   }

   private ResultSet<Relation> getRelation(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode, DeletionFlag inludeDeleted) throws OseeCoreException {
      GraphData graph = getGraph(aNode, bNode);
      checkNotNull(session, "session");
      checkNotNull(type, "relationType");

      ensureRelationsInitialized(session, graph, aNode);
      ensureRelationsInitialized(session, graph, bNode);

      RelationNodeAdjacencies aAdjacencies = graph.getAdjacencies(aNode);
      RelationNodeAdjacencies bAdjacencies = graph.getAdjacencies(bNode);

      Relation relation = aAdjacencies.getRelation(aNode, type, bNode, inludeDeleted);
      if (relation != null) {
         bAdjacencies.add(type.getGuid(), relation);
      } else {
         relation = bAdjacencies.getRelation(aNode, type, bNode, inludeDeleted);
         if (relation != null) {
            aAdjacencies.add(type.getGuid(), relation);
         }
      }
      return ResultSets.singleton(relation);
   }

   private List<Relation> getRelations(OrcsSession session, IRelationType type, RelationNode node, RelationSide side, DeletionFlag includeDeleted) throws OseeCoreException {
      checkNotNull(session, "session");
      checkNotNull(type, "relationType");
      checkNotNull(side, "relationSide");
      checkNotNull(node, "node");

      GraphData graph = node.getGraph();
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies adjacencies = graph.getAdjacencies(node);
      return adjacencies.getList(type, includeDeleted, node, side);
   }

   private List<Relation> getRelations(OrcsSession session, RelationNode node, DeletionFlag includeDeleted) throws OseeCoreException {
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

   private void order(OrcsSession session, IRelationType type, RelationNode node1, RelationSide side, OrderOp op, Collection<? extends RelationNode> node2) throws OseeCoreException {
      order(session, type, node1, side, RelationOrderBaseTypes.PREEXISTING, op, node2);
   }

   private void order(OrcsSession session, IRelationType type, RelationNode node1, RelationSide side, IRelationSorterId sorterId, OrderOp op, Collection<? extends RelationNode> node2) throws OseeCoreException {
      OrderManager orderManager = orderFactory.createOrderManager(node1);

      RelationSide orderSide = side.oppositeSide();
      IRelationTypeSide key = asTypeSide(type, orderSide);
      IRelationSorterId sorterIdToUse = sorterId;
      if (sorterIdToUse == RelationOrderBaseTypes.PREEXISTING) {
         sorterIdToUse = orderManager.getSorterId(key);
      }
      List<Identifiable<String>> relatives = Collections.emptyList();
      if (RelationOrderBaseTypes.USER_DEFINED == sorterIdToUse) {
         ResultSet<RelationNode> arts = getRelated(session, type, node1, side);
         relatives = new LinkedList<>();
         for (RelationNode art : arts) {
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
   public void cloneRelations(OrcsSession session, RelationNode source, RelationNode destination) throws OseeCoreException {
      ensureRelationsInitialized(session, source.getGraph(), source);
      RelationNodeAdjacencies adjacencies1 = source.getGraph().getAdjacencies(source);
      if (adjacencies1 != null) {
         Collection<Relation> all = adjacencies1.getAll();
         if (!all.isEmpty()) {
            RelationNodeAdjacencies adjacencies2 = relationFactory.createRelationContainer();
            destination.getGraph().addAdjacencies(destination, adjacencies2);
            for (Relation relation : adjacencies1.getAll()) {
               Relation newRel = relationFactory.clone(relation);
               adjacencies2.add(newRel.getOrcsData().getTypeUuid(), newRel);
            }
         }
      }
   }

   @Override
   public void introduce(OrcsSession session, Long branch, RelationNode source, RelationNode destination) throws OseeCoreException {
      ensureRelationsInitialized(session, source.getGraph(), source);

      Collection<? extends IRelationType> validRelationTypes = getValidRelationTypes(session, destination);
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
   public void setApplicabilityId(OrcsSession session, RelationNode aNode, IRelationType type, RelationNode bNode, ApplicabilityId applicId) {
      ResultSet<Relation> result = getRelation(session, aNode, type, bNode, EXCLUDE_DELETED);
      Relation relation = result.getExactlyOne();
      relation.setApplicabilityId(applicId);
   }

   private ArtifactReadable doesRelatedArtifactExist(OrcsSession session, Relation rel, RelationNode destination, Long branch) {
      // need to check if the related artifact exists
      int artIdA = rel.getOrcsData().getArtIdA();
      int artIdB = rel.getOrcsData().getArtIdB();
      int checkArtId = destination.getLocalId() == artIdA ? artIdB : artIdA;
      // need to check if artifact to relate to exists
      ArtifactReadable readable =
         provider.getQueryFactory(session).fromBranch(branch).andUuid(checkArtId).getResults().getOneOrNull();
      return readable;
   }

   private Relation findRelationByLocalId(RelationNodeAdjacencies adjacencies, Integer localId) {
      for (Relation rel : adjacencies.getAll()) {
         if (localId.equals(rel.getOrcsData().getLocalId())) {
            return rel;
         }
      }
      return null;
   }
}
