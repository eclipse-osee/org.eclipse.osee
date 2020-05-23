/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.relation.impl;

import static com.google.common.base.Predicates.and;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.deletionFlagEquals;
import static org.eclipse.osee.orcs.core.internal.util.OrcsPredicates.nodeIdOnSideEquals;
import com.google.common.base.Predicate;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphAdjacencies;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationVisitor;
import org.eclipse.osee.orcs.core.internal.util.AbstractTypeCollection;
import org.eclipse.osee.orcs.core.internal.util.OrcsPredicates;

/**
 * @author Roberto E. Escobar
 */
public class RelationNodeAdjacencies extends AbstractTypeCollection<RelationTypeToken, Relation, RelationTypeToken, Relation> implements GraphAdjacencies {

   @Override
   protected ResultSet<Relation> createResultSet(List<Relation> values) {
      return ResultSets.newResultSet(values);
   }

   @Override
   protected <T extends Relation> ResultSet<T> createResultSet(RelationTypeToken type, List<T> values) {
      return ResultSets.newResultSet(values);
   }

   @Override
   protected Relation asMatcherData(Relation data) {
      return data;
   }

   @Override
   protected RelationTypeToken getType(Relation data) {
      return data.getRelationType();
   }

   //////////////////////////////////////////////////////////////
   @SuppressWarnings({"unchecked", "rawtypes"})
   public List<Relation> getList(RelationTypeToken type, DeletionFlag includeDeleted) {
      Predicate deletionFlagEquals = deletionFlagEquals(includeDeleted);
      return getListByFilter(type, deletionFlagEquals);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public List<Relation> getList(RelationTypeToken relationType, DeletionFlag includeDeleted, ArtifactId id, RelationSide side) {
      Predicate deletionFlagEquals = deletionFlagEquals(includeDeleted);
      Predicate relIdOnSide = nodeIdOnSideEquals(id, side);
      Predicate matcher = and(deletionFlagEquals, relIdOnSide);
      return getListByFilter(relationType, matcher);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public ResultSet<Relation> getResultSet(RelationTypeToken type, DeletionFlag includeDeleted) {
      Predicate deletionFlagEquals = deletionFlagEquals(includeDeleted);
      return getSetByFilter(type, deletionFlagEquals);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public ResultSet<Relation> getResultSet(RelationTypeToken type, DeletionFlag includeDeleted, ArtifactId id, RelationSide side) {
      Predicate deletionFlagEquals = deletionFlagEquals(includeDeleted);
      Predicate relIdOnSide = nodeIdOnSideEquals(id, side);
      Predicate matcher = and(deletionFlagEquals, relIdOnSide);
      return getSetByFilter(type, matcher);
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public Relation getRelation(Artifact aNode, RelationTypeToken type, Artifact bNode, DeletionFlag excludeDeleted) {
      Predicate<Relation> nodeMatcher = OrcsPredicates.nodeIdsEquals(aNode, bNode);
      Predicate deletionFlagEquals = deletionFlagEquals(excludeDeleted);
      Predicate matcher = and(deletionFlagEquals, nodeMatcher);
      List<Relation> listByFilter = getListByFilter(type, matcher);
      return listByFilter.isEmpty() ? null : listByFilter.get(0);
   }

   public Relation getRelation(ArtifactId artIdA, RelationTypeToken relationType, ArtifactId artIdB) {
      Predicate<Relation> nodeMatcher = OrcsPredicates.nodeIdsEquals(artIdA, artIdB);
      List<Relation> listByFilter = getListByFilter(relationType, nodeMatcher);
      return listByFilter.isEmpty() ? null : listByFilter.get(0);
   }

   public void accept(RelationVisitor visitor) {
      for (Relation relation : getAll()) {
         visitor.visit(relation);
      }
   }

}
