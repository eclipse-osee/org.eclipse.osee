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
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;

/**
 * @author Luciano T. Vaglienti Api for accessing interface sub messages
 */
public interface InterfaceSubMessageApi extends QueryCapableMIMAPI<InterfaceSubMessageToken>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {
   ArtifactAccessor<InterfaceSubMessageToken> getAccessor();

   InterfaceSubMessageToken get(BranchId branch, ArtifactId subMessageId);

   Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId);

   Collection<InterfaceSubMessageToken> getAllByRelationAndFilter(BranchId branch, ArtifactId messageId, String filter);

   Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter);

   Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId, long pageNum, long pageSize);

   Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   List<InterfaceSubMessageToken> getAllRelatedFromStructure(InterfaceStructureToken structure);

   InterfaceSubMessageToken getWithAllParentRelations(BranchId branch, ArtifactId subMessageId);
}
