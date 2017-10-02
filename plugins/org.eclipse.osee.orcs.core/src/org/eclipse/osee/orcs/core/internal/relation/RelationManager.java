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
package org.eclipse.osee.orcs.core.internal.relation;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public interface RelationManager {

   int getMaximumRelationAllowed(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side) ;

   Collection<RelationTypeId> getValidRelationTypes(OrcsSession session, RelationNode node) ;

   ///////////////////////////////////////

   void accept(OrcsSession session, GraphData graph, RelationNode node, RelationVisitor visitor) ;

   ///////////////////////////////////////

   boolean hasDirtyRelations(OrcsSession session, RelationNode node) ;

   Collection<RelationTypeId> getExistingRelationTypes(OrcsSession session, RelationNode node) ;

   int getRelatedCount(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side) ;

   int getRelatedCount(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side, DeletionFlag includeDeleted) ;

   boolean areRelated(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode) ;

   String getRationale(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode) ;

   ///////////////////////////////////////

   <T extends RelationNode> T getParent(OrcsSession session, RelationNode child) ;

   <T extends RelationNode> ResultSet<T> getChildren(OrcsSession session, RelationNode parent) ;

   <T extends RelationNode> ResultSet<T> getRelated(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side) ;

   <T extends RelationNode> ResultSet<T> getRelated(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side, DeletionFlag flag) ;

   ///////////////////////////////////////

   void addChild(OrcsSession session, RelationNode parent, RelationNode child) ;

   void addChildren(OrcsSession session, RelationNode parent, List<? extends RelationNode> children) ;

   void relate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode) ;

   void relate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, String rationale) ;

   void relate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, RelationSorter sortType) ;

   void relate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, String rationale, RelationSorter sortType) ;

   void setRationale(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, String rationale) ;

   void unrelate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode) ;

   void unrelateFromAll(OrcsSession session, RelationNode node) ;

   void unrelateFromAll(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side) ;

   void cloneRelations(OrcsSession session, RelationNode source, RelationNode destination) ;

   void introduce(OrcsSession session, BranchId branch, RelationNode source, RelationNode destination) ;

   void setApplicabilityId(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, ApplicabilityId applicId);

   List<Relation> getRelations(OrcsSession session, RelationNode node, DeletionFlag includeDeleted) ;

   ///////////////////////////////////////
}