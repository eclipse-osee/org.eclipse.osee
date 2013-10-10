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
import static org.eclipse.osee.framework.core.util.Conditions.checkNotNull;
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
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.Identifiable;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.relation.RelationResolver;
import org.eclipse.osee.orcs.core.internal.relation.RelationTypeValidity;
import org.eclipse.osee.orcs.core.internal.relation.RelationVisitor;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderManager;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderManagerFactory;
import org.eclipse.osee.orcs.core.internal.util.ResultSets;

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

   public RelationManagerImpl(Log logger, RelationTypeValidity validity, RelationResolver resolver, RelationFactory relationFactory, OrderManagerFactory orderFactory) {
      super();
      this.logger = logger;
      this.validity = validity;
      this.resolver = resolver;
      this.relationFactory = relationFactory;
      this.orderFactory = orderFactory;
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
   public boolean hasDirtyRelations(OrcsSession session, GraphData graph, RelationNode node) throws OseeCoreException {
      checkOnGraph(graph, node);
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies container = graph.getAdjacencies(node);
      return container != null ? container.hasDirty() : false;
   }

   @Override
   public Collection<? extends IRelationType> getExistingRelationTypes(OrcsSession session, GraphData graph, RelationNode node) throws OseeCoreException {
      checkOnGraph(graph, node);
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
   public int getRelatedCount(OrcsSession session, GraphData graph, IRelationType type, RelationNode node, RelationSide side) throws OseeCoreException {
      return getRelatedCount(session, graph, type, node, side, EXCLUDE_DELETED);
   }

   @Override
   public int getRelatedCount(OrcsSession session, GraphData graph, IRelationType type, RelationNode node, RelationSide side, DeletionFlag includeDeleted) throws OseeCoreException {
      return getRelations(session, graph, type, node, side, includeDeleted).size();
   }

   @Override
   public boolean areRelated(OrcsSession session, GraphData graph, RelationNode aNode, IRelationType type, RelationNode bNode) throws OseeCoreException {
      return getRelation(session, graph, aNode, type, bNode, EXCLUDE_DELETED).size() > 0;
   }

   @Override
   public <T extends RelationNode> T getParent(OrcsSession session, GraphData graph, RelationNode child) throws OseeCoreException {
      ResultSet<T> toReturn = getRelated(session, graph, DEFAULT_HIERARCHY, child, IS_CHILD);
      return toReturn.getOneOrNull();
   }

   @Override
   public <T extends RelationNode> ResultSet<T> getChildren(OrcsSession session, GraphData graph, RelationNode parent) throws OseeCoreException {
      return getRelated(session, graph, DEFAULT_HIERARCHY, parent, IS_PARENT);
   }

   @Override
   public <T extends RelationNode> ResultSet<T> getRelated(OrcsSession session, GraphData graph, IRelationType type, RelationNode node, RelationSide side) throws OseeCoreException {
      List<Relation> links = getRelations(session, graph, type, node, side, EXCLUDE_DELETED);
      List<T> result = null;
      if (links.isEmpty()) {
         result = Collections.emptyList();
      } else {
         RelationSide otherSide = side.oppositeSide();
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
   public String getRationale(OrcsSession session, GraphData graph, RelationNode aNode, IRelationType type, RelationNode bNode) throws OseeCoreException {
      ResultSet<Relation> result = getRelation(session, graph, aNode, type, bNode, EXCLUDE_DELETED);
      return result.getExactlyOne().getRationale();
   }

   @Override
   public void setRationale(OrcsSession session, GraphData graph, RelationNode aNode, IRelationType type, RelationNode bNode, String rationale) throws OseeCoreException {
      ResultSet<Relation> result = getRelation(session, graph, aNode, type, bNode, EXCLUDE_DELETED);
      Relation relation = result.getExactlyOne();
      relation.setRationale(rationale);
   }

   ///////////////////////// RELATE NODES ///////////////////
   @Override
   public void addChild(OrcsSession session, GraphData graph, RelationNode parent, RelationNode child) throws OseeCoreException {
      unrelateFromAll(session, graph, DEFAULT_HIERARCHY, child, IS_CHILD);
      relate(session, graph, parent, DEFAULT_HIERARCHY, child);
   }

   @Override
   public void addChildren(OrcsSession session, GraphData graph, RelationNode parent, List<? extends RelationNode> children) throws OseeCoreException {
      for (RelationNode child : children) {
         addChild(session, graph, parent, child);
      }
   }

   @Override
   public void relate(OrcsSession session, GraphData graph, RelationNode aNode, IRelationType type, RelationNode bNode) throws OseeCoreException {
      relate(session, graph, aNode, type, bNode, emptyString(), RelationOrderBaseTypes.PREEXISTING);
   }

   @Override
   public void relate(OrcsSession session, GraphData graph, RelationNode aNode, IRelationType type, RelationNode bNode, String rationale) throws OseeCoreException {
      relate(session, graph, aNode, type, bNode, rationale, RelationOrderBaseTypes.PREEXISTING);
   }

   @Override
   public void relate(OrcsSession session, GraphData graph, RelationNode aNode, IRelationType type, RelationNode bNode, IRelationSorterId sortType) throws OseeCoreException {
      relate(session, graph, aNode, type, bNode, emptyString(), sortType);
   }

   @Override
   public void relate(OrcsSession session, GraphData graph, RelationNode aNode, IRelationType type, RelationNode bNode, String rationale, IRelationSorterId sortType) throws OseeCoreException {
      checkOnGraph(graph, aNode, bNode);
      checkBranch(aNode, bNode);
      checkRelateSelf(aNode, bNode);

      // Check we can create the type on other side of each node
      checkTypeAndCanAdd(session, graph, type, aNode, SIDE_B);
      checkTypeAndCanAdd(session, graph, type, bNode, SIDE_A);

      Relation relation = getRelation(session, graph, aNode, type, bNode, INCLUDE_DELETED).getOneOrNull();
      boolean updated = false;
      if (relation == null) {
         relation = relationFactory.createRelation(aNode, type, bNode);
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
         order(session, graph, type, aNode, SIDE_A, sortType, OrderOp.ADD_TO_ORDER, Collections.singleton(bNode));
      }
   }

   private void checkTypeAndCanAdd(OrcsSession session, GraphData graph, IRelationType type, RelationNode node, RelationSide side) throws OseeCoreException {
      validity.checkRelationTypeValid(type, node, side);
      checkMultiplicityCanAdd(session, graph, type, node, side);
   }

   private void checkMultiplicityCanAdd(OrcsSession session, GraphData graph, IRelationType type, RelationNode node, RelationSide side) throws OseeCoreException {
      int currentCount = getRelatedCount(session, graph, type, node, side);
      validity.checkRelationTypeMultiplicity(type, node, side, currentCount + 1);
   }

   ///////////////////////// UNRELATE NODES ///////////////////
   @Override
   public void unrelate(OrcsSession session, GraphData graph, RelationNode aNode, IRelationType type, RelationNode bNode) throws OseeCoreException {
      Relation relation = getRelation(session, graph, aNode, type, bNode, EXCLUDE_DELETED).getOneOrNull();
      boolean modified = false;
      if (relation != null) {
         relation.delete();
         modified = true;
      }
      if (modified) {
         order(session, graph, type, aNode, SIDE_A, OrderOp.REMOVE_FROM_ORDER, Collections.singleton(bNode));
      }
   }

   @Override
   public void unrelateFromAll(OrcsSession session, GraphData graph, IRelationType type, RelationNode node, RelationSide side) throws OseeCoreException {
      List<Relation> relations = getRelations(session, graph, type, node, side, EXCLUDE_DELETED);

      RelationSide otherSide = side.oppositeSide();
      resolver.resolve(session, graph, relations, otherSide);

      boolean modified = false;
      Set<RelationNode> otherNodes = new LinkedHashSet<RelationNode>();
      for (Relation relation : relations) {
         relation.delete();
         Integer artId = relation.getLocalIdForSide(otherSide);
         RelationNode otherNode = graph.getNode(artId);
         otherNodes.add(otherNode);
         modified = true;
      }
      if (modified) {
         order(session, graph, type, node, side, OrderOp.REMOVE_FROM_ORDER, otherNodes);
      }
   }

   @Override
   public void unrelateFromAll(OrcsSession session, GraphData graph, RelationNode node) throws OseeCoreException {
      unrelate(session, graph, node, true);
   }

   private void unrelate(OrcsSession session, GraphData graph, RelationNode node, boolean reorderRelations) throws OseeCoreException {
      checkNotNull(node, "node");
      if (node.isDeleteAllowed()) {

         List<Relation> relations = getRelations(session, graph, node, EXCLUDE_DELETED);
         resolver.resolve(session, graph, relations, RelationSide.values());

         ResultSet<RelationNode> children = getChildren(session, graph, node);
         for (RelationNode child : children) {
            unrelate(session, graph, child, false);
         }

         try {
            node.delete();

            if (relations != null && !relations.isEmpty()) {
               Map<IRelationType, RelationSide> typesToRemove = new HashMap<IRelationType, RelationSide>();
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

                     List<Relation> sideLinks = getRelations(session, graph, type, node, side, EXCLUDE_DELETED);
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
   @SuppressWarnings("unused")
   private void ensureRelationsInitialized(OrcsSession session, GraphData graph, RelationNode node) throws OseeCoreException {
      if (graph.getAdjacencies(node) == null) {
         RelationNodeAdjacencies container = relationFactory.createRelationContainer();
         graph.addAdjacencies(node, container);
      }
   }

   private ResultSet<Relation> getRelation(OrcsSession session, GraphData graph, RelationNode aNode, IRelationType type, RelationNode bNode, DeletionFlag inludeDeleted) throws OseeCoreException {
      checkNotNull(session, "session");
      checkOnGraph(graph, aNode, bNode);
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

   private List<Relation> getRelations(OrcsSession session, GraphData graph, IRelationType type, RelationNode node, RelationSide side, DeletionFlag includeDeleted) throws OseeCoreException {
      checkNotNull(session, "session");
      checkOnGraph(graph, node);
      checkNotNull(type, "relationType");
      checkNotNull(side, "relationSide");

      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies adjacencies = graph.getAdjacencies(node);
      return adjacencies.getList(type, includeDeleted, node, side);
   }

   private List<Relation> getRelations(OrcsSession session, GraphData graph, RelationNode node, DeletionFlag includeDeleted) throws OseeCoreException {
      checkNotNull(session, "session");
      checkOnGraph(graph, node);
      ensureRelationsInitialized(session, graph, node);
      RelationNodeAdjacencies adjacencies = graph.getAdjacencies(node);
      return adjacencies.getList(includeDeleted);
   }

   private static enum OrderOp {
      ADD_TO_ORDER,
      REMOVE_FROM_ORDER;
   }

   private void order(OrcsSession session, GraphData graph, IRelationType type, RelationNode node1, RelationSide side, OrderOp op, Collection<? extends RelationNode> node2) throws OseeCoreException {
      order(session, graph, type, node1, side, RelationOrderBaseTypes.PREEXISTING, op, node2);
   }

   private void order(OrcsSession session, GraphData graph, IRelationType type, RelationNode node1, RelationSide side, IRelationSorterId sorterId, OrderOp op, Collection<? extends RelationNode> node2) throws OseeCoreException {
      OrderManager orderManager = orderFactory.createOrderManager(node1);

      RelationSide orderSide = side.oppositeSide();
      IRelationTypeSide key = asTypeSide(type, orderSide);
      IRelationSorterId sorterIdToUse = sorterId;
      if (sorterIdToUse == RelationOrderBaseTypes.PREEXISTING) {
         sorterIdToUse = orderManager.getSorterId(key);
      }
      List<Identifiable<String>> relatives = Collections.emptyList();
      if (RelationOrderBaseTypes.USER_DEFINED == sorterIdToUse) {
         ResultSet<RelationNode> arts = getRelated(session, graph, type, node1, side);
         relatives = new LinkedList<Identifiable<String>>();
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
}
