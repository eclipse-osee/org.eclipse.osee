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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public interface RelationManager {

   int getMaximumRelationAllowed(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side) throws OseeCoreException;

   Collection<RelationTypeId> getValidRelationTypes(OrcsSession session, RelationNode node) throws OseeCoreException;

   ///////////////////////////////////////

   void accept(OrcsSession session, GraphData graph, RelationNode node, RelationVisitor visitor) throws OseeCoreException;

   ///////////////////////////////////////

   boolean hasDirtyRelations(OrcsSession session, RelationNode node) throws OseeCoreException;

   Collection<RelationTypeId> getExistingRelationTypes(OrcsSession session, RelationNode node) throws OseeCoreException;

   int getRelatedCount(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side) throws OseeCoreException;

   int getRelatedCount(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side, DeletionFlag includeDeleted) throws OseeCoreException;

   boolean areRelated(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode) throws OseeCoreException;

   String getRationale(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode) throws OseeCoreException;

   ///////////////////////////////////////

   <T extends RelationNode> T getParent(OrcsSession session, RelationNode child) throws OseeCoreException;

   <T extends RelationNode> ResultSet<T> getChildren(OrcsSession session, RelationNode parent) throws OseeCoreException;

   <T extends RelationNode> ResultSet<T> getRelated(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side) throws OseeCoreException;

   <T extends RelationNode> ResultSet<T> getRelated(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side, DeletionFlag flag) throws OseeCoreException;

   ///////////////////////////////////////

   void addChild(OrcsSession session, RelationNode parent, RelationNode child) throws OseeCoreException;

   void addChildren(OrcsSession session, RelationNode parent, List<? extends RelationNode> children) throws OseeCoreException;

   void relate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode) throws OseeCoreException;

   void relate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, String rationale) throws OseeCoreException;

   void relate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, RelationSorter sortType) throws OseeCoreException;

   void relate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, String rationale, RelationSorter sortType) throws OseeCoreException;

   void setRationale(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, String rationale) throws OseeCoreException;

   void unrelate(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode) throws OseeCoreException;

   void unrelateFromAll(OrcsSession session, RelationNode node) throws OseeCoreException;

   void unrelateFromAll(OrcsSession session, RelationTypeId type, RelationNode node, RelationSide side) throws OseeCoreException;

   void cloneRelations(OrcsSession session, RelationNode source, RelationNode destination) throws OseeCoreException;

   void introduce(OrcsSession session, BranchId branch, RelationNode source, RelationNode destination) throws OseeCoreException;

   void setApplicabilityId(OrcsSession session, RelationNode aNode, RelationTypeId type, RelationNode bNode, ApplicabilityId applicId);

   List<Relation> getRelations(OrcsSession session, RelationNode node, DeletionFlag includeDeleted) throws OseeCoreException;

   ///////////////////////////////////////
}