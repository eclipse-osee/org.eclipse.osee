/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.relation.impl;

import static com.google.common.base.Predicates.and;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.deletionFlagEquals;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.nodeIdOnSideEquals;
import com.google.common.base.Predicate;
import java.util.List;
import org.eclipse.osee.framework.core.data.HasLocalId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.core.internal.graph.GraphAdjacencies;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.relation.RelationVisitor;
import org.eclipse.osee.orcs.core.internal.util.AbstractTypeCollection;
import org.eclipse.osee.orcs.core.internal.util.OrcsPredicates;

/**
 * @author Roberto E. Escobar
 */
public class RelationNodeAdjacencies extends AbstractTypeCollection<IRelationType, Relation, Long, Relation> implements GraphAdjacencies {

   @Override
   protected ResultSet<Relation> createResultSet(List<Relation> values) {
      return ResultSets.newResultSet(values);
   }

   @Override
   protected <T extends Relation> ResultSet<T> createResultSet(Long type, List<T> values) {
      return ResultSets.newResultSet(values);
   }

   @Override
   protected Relation asMatcherData(Relation data) {
      return data;
   }

   @Override
   protected IRelationType getType(Relation data) throws OseeCoreException {
      return data.getRelationType();
   }

   //////////////////////////////////////////////////////////////
   @SuppressWarnings({"unchecked", "rawtypes"})
   public List<Relation> getList(IRelationType type, DeletionFlag includeDeleted) throws OseeCoreException {
      Predicate deletionFlagEquals = deletionFlagEquals(includeDeleted);
      return getListByFilter(type.getId(), deletionFlagEquals);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public List<Relation> getList(IRelationType type, DeletionFlag includeDeleted, HasLocalId<Integer> localId, RelationSide side) throws OseeCoreException {
      Predicate deletionFlagEquals = deletionFlagEquals(includeDeleted);
      Predicate relIdOnSide = nodeIdOnSideEquals(localId, side);
      Predicate matcher = and(deletionFlagEquals, relIdOnSide);
      return getListByFilter(type.getId(), matcher);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public ResultSet<Relation> getResultSet(IRelationType type, DeletionFlag includeDeleted) throws OseeCoreException {
      Predicate deletionFlagEquals = deletionFlagEquals(includeDeleted);
      return getSetByFilter(type.getId(), deletionFlagEquals);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public ResultSet<Relation> getResultSet(IRelationType type, DeletionFlag includeDeleted, HasLocalId<Integer> localId, RelationSide side) throws OseeCoreException {
      Predicate deletionFlagEquals = deletionFlagEquals(includeDeleted);
      Predicate relIdOnSide = nodeIdOnSideEquals(localId, side);
      Predicate matcher = and(deletionFlagEquals, relIdOnSide);
      return getSetByFilter(type.getId(), matcher);
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public Relation getRelation(RelationNode aNode, IRelationType type, RelationNode bNode, DeletionFlag excludeDeleted) throws OseeCoreException {
      Predicate<Relation> nodeMatcher = OrcsPredicates.nodeIdsEquals(aNode, bNode);
      Predicate deletionFlagEquals = deletionFlagEquals(excludeDeleted);
      Predicate matcher = and(deletionFlagEquals, nodeMatcher);
      List<Relation> listByFilter = getListByFilter(type.getId(), matcher);
      return listByFilter.isEmpty() ? null : listByFilter.get(0);
   }

   public Relation getRelation(int artIdA, long typeUuid, int artIdB) throws OseeCoreException {
      Predicate<Relation> nodeMatcher = OrcsPredicates.nodeIdsEquals(artIdA, artIdB);
      List<Relation> listByFilter = getListByFilter(typeUuid, nodeMatcher);
      return listByFilter.isEmpty() ? null : listByFilter.get(0);
   }

   public void accept(RelationVisitor visitor) throws OseeCoreException {
      for (Relation relation : getAll()) {
         visitor.visit(relation);
      }
   }

}
