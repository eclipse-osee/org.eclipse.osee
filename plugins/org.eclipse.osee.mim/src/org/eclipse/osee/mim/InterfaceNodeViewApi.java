/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfaceNodeViewApi extends QueryCapableMIMAPI<InterfaceNode>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {
   ArtifactAccessor<InterfaceNode> getAccessor();

   InterfaceNode get(BranchId branch, ArtifactId nodeId);

   Collection<InterfaceNode> getAll(BranchId branch);

   Collection<InterfaceNode> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<InterfaceNode> getAll(BranchId branch, AttributeTypeToken orderByAttributeType);

   Collection<InterfaceNode> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType);

   Collection<InterfaceNode> getMessagePublisherNodes(BranchId branch, ArtifactId message);

   Collection<InterfaceNode> getMessageSubscriberNodes(BranchId branch, ArtifactId message);

   Collection<InterfaceNode> getNodesForConnection(BranchId branch, ArtifactId connectionId);

   Collection<InterfaceNode> getNodesByName(BranchId branch, String name, long pageNum, long pageSize);

   int getNodesByNameCount(BranchId branch, String name);
}
