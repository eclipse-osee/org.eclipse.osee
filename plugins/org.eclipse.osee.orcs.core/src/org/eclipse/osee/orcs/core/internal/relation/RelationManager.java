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
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public interface RelationManager {

   int getMaximumRelationAllowed(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side);

   Collection<RelationTypeId> getValidRelationTypes(OrcsSession session, Artifact node);

   ///////////////////////////////////////

   void accept(OrcsSession session, GraphData graph, Artifact node, RelationVisitor visitor);

   ///////////////////////////////////////

   boolean hasDirtyRelations(OrcsSession session, Artifact node);

   Collection<RelationTypeId> getExistingRelationTypes(OrcsSession session, Artifact node);

   int getRelatedCount(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side);

   int getRelatedCount(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side, DeletionFlag includeDeleted);

   boolean areRelated(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode);

   String getRationale(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode);

   ///////////////////////////////////////

   <T extends Artifact> T getParent(OrcsSession session, Artifact child);

   <T extends Artifact> ResultSet<T> getChildren(OrcsSession session, Artifact parent);

   <T extends Artifact> ResultSet<T> getRelated(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side);

   <T extends Artifact> ResultSet<T> getRelated(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side, DeletionFlag flag);

   ///////////////////////////////////////

   void addChild(OrcsSession session, Artifact parent, Artifact child);

   void relate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode);

   void relate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode, String rationale);

   void relate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode, RelationSorter sortType);

   void relate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode, String rationale, RelationSorter sortType);

   void setRationale(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode, String rationale);

   void unrelate(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode);

   void unrelateFromAll(OrcsSession session, Artifact node);

   void unrelateFromAll(OrcsSession session, RelationTypeId type, Artifact node, RelationSide side);

   void cloneRelations(OrcsSession session, Artifact source, Artifact destination);

   void introduce(OrcsSession session, BranchId branch, Artifact source, Artifact destination);

   void setApplicabilityId(OrcsSession session, Artifact aNode, RelationTypeId type, Artifact bNode, ApplicabilityId applicId);

   List<Relation> getRelations(OrcsSession session, Artifact node, DeletionFlag includeDeleted);

   /**
    * Set USER_DEFINED order exactly as specified in bNodes List. Nodes not in bNodes will be removed.
    */
   void order(OrcsSession session, Artifact aNode, RelationTypeId type, RelationSide side, List<? extends Artifact> bNodes);

   ///////////////////////////////////////
}