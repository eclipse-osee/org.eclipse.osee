/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.internal.relation;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.transaction.TxData;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 * @author Megumi Telles
 */
public interface RelationManager {

   int getMaximumRelationAllowed(RelationTypeToken type, Artifact node, RelationSide side);

   Collection<RelationTypeToken> getValidRelationTypes(Artifact node);

   ///////////////////////////////////////

   void accept(GraphData graph, Artifact node, RelationVisitor visitor);

   ///////////////////////////////////////

   boolean hasDirtyRelations(Artifact node);

   Collection<RelationTypeToken> getExistingRelationTypes(Artifact node);

   int getRelatedCount(RelationTypeToken type, Artifact node, RelationSide side);

   int getRelatedCount(RelationTypeToken type, Artifact node, RelationSide side, DeletionFlag includeDeleted);

   boolean areRelated(Artifact aNode, RelationTypeToken type, Artifact bNode);

   String getRationale(Artifact aNode, RelationTypeToken type, Artifact bNode);

   ///////////////////////////////////////

   <T extends Artifact> T getParent(OrcsSession session, Artifact child);

   <T extends Artifact> ResultSet<T> getChildren(OrcsSession session, Artifact parent);

   <T extends Artifact> ResultSet<T> getRelated(OrcsSession session, RelationTypeToken type, Artifact node, RelationSide side);

   <T extends Artifact> ResultSet<T> getRelated(OrcsSession session, RelationTypeToken type, Artifact node, RelationSide side, DeletionFlag flag);

   ///////////////////////////////////////

   void addChild(OrcsSession session, Artifact parent, Artifact child);

   void relate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode);

   void relate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode, String rationale);

   void relate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode, RelationSorter sortType);

   void relate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode, String rationale, RelationSorter sortType);

   void setRationale(Artifact aNode, RelationTypeToken type, Artifact bNode, String rationale);

   Relation unrelate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode);

   void unrelateFromAll(OrcsSession session, Artifact node);

   void unrelateFromAll(OrcsSession session, RelationTypeToken type, Artifact node, RelationSide side);

   void cloneRelations(Artifact source, Artifact destination);

   void introduce(BranchId branch, Artifact source, Artifact destination);

   void setApplicabilityId(Artifact aNode, RelationTypeToken type, Artifact bNode, ApplicabilityId applicId);

   List<Relation> getRelations(Artifact node, DeletionFlag includeDeleted);

   /**
    * Set USER_DEFINED order exactly as specified in bNodes List. Nodes not in bNodes will be removed.
    */
   void order(Artifact aNode, RelationTypeToken type, RelationSide side, List<? extends Artifact> bNodes);

   void relate(OrcsSession session, Artifact asArtifactA, RelationTypeToken type, Artifact asArtifactB, TxData txData);

   void relate(OrcsSession session, Artifact aNode, RelationTypeToken type, Artifact bNode, String rationale, RelationSorter sortType, TxData txData);

   ///////////////////////////////////////
}