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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti Api for accessing interface sub messages
 */
public interface InterfaceSubMessageApi extends QueryCapableMIMAPI<InterfaceSubMessageToken>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {
   ArtifactAccessor<InterfaceSubMessageToken> getAccessor();

   InterfaceSubMessageToken get(BranchId branch, ArtifactId subMessageId);

   Collection<InterfaceSubMessageToken> get(BranchId branch, Collection<ArtifactId> subMessageIds,
      Collection<FollowRelation> followRelations);

   Collection<InterfaceSubMessageToken> getAll(BranchId branch);

   Collection<InterfaceSubMessageToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<InterfaceSubMessageToken> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<InterfaceSubMessageToken> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId);

   Collection<InterfaceSubMessageToken> getAllByRelationAndFilter(BranchId branch, ArtifactId messageId, String filter);

   Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter);

   Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId,
      AttributeTypeId orderByAttribute);

   Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter,
      AttributeTypeId orderByAttribute);

   Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId, long pageNum,
      long pageSize);

   Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   Collection<InterfaceSubMessageToken> getAllByRelation(BranchId branch, ArtifactId messageId, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   Collection<InterfaceSubMessageToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   List<InterfaceSubMessageToken> getAllByName(BranchId branch, String name, long pageNum, long pageSize);

   int getAllByNameCount(BranchId branch, String name);

}
