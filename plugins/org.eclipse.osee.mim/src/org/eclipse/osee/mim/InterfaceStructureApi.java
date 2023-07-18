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
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti Api for accessing interface structures
 * @todo
 */
public interface InterfaceStructureApi extends QueryCapableMIMAPI<InterfaceStructureToken>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {

   InterfaceStructureToken get(BranchId branch, ArtifactId artId);

   List<InterfaceStructureToken> getAll(BranchId branch);

   List<InterfaceStructureToken> getAllWithRelations(BranchId branch, List<FollowRelation> followRelations,
      String filter, Collection<AttributeTypeId> attributes, AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAllWithoutRelations(BranchId branch);

   List<InterfaceStructureToken> getFiltered(BranchId branch, String filter);

   List<InterfaceStructureToken> getFilteredWithoutRelations(BranchId branch, String filter);

   List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId);

   List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId connectionId,
      ArtifactId subMessageId, String filter);

   List<InterfaceStructureToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAllWithoutRelations(BranchId branch, AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getFiltered(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getFilteredWithoutRelations(BranchId branch, String filter,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId subMessageId, String filter,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAll(BranchId branch, long pageNum, long pageSize);

   List<InterfaceStructureToken> getAllWithoutRelations(BranchId branch, long pageNum, long pageSize);

   List<InterfaceStructureToken> getFiltered(BranchId branch, String filter, long pageNum, long pageSize);

   List<InterfaceStructureToken> getFilteredWithoutRelations(BranchId branch, String filter, long pageNum,
      long pageSize);

   List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      long pageNum, long pageSize);

   List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId subMessageId, String filter,
      long pageNum, long pageSize);

   List<InterfaceStructureToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAllWithoutRelations(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getFiltered(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getFilteredWithoutRelations(BranchId branch, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAllRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId viewId, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   int getAllRelatedCount(BranchId branch, ArtifactId subMessageId);

   List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId connectionId,
      ArtifactId subMessageId, String filter, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAllRelatedAndFilter(BranchId branch, ArtifactId connectionId,
      ArtifactId subMessageId, ArtifactId viewId, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureToken> getAllByName(BranchId branch, String name, long pageNum, long pageSize);

   int getAllByNameCount(BranchId branch, String name);

   int getAllRelatedAndFilterCount(BranchId branch, ArtifactId subMessageId, String filter);

   InterfaceStructureToken getRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId structureId);

   InterfaceStructureToken getRelated(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId structureId, ArtifactId viewId);

   InterfaceStructureToken getRelatedAndFilter(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId structureId, String filter);

   InterfaceStructureToken getRelatedAndFilter(BranchId branch, ArtifactId connectionId, ArtifactId subMessageId,
      ArtifactId structureId, String filter, ArtifactId viewId);

   List<InterfaceStructureToken> getAllRelatedFromElement(InterfaceStructureElementToken element);

   InterfaceStructureToken getWithAllParentRelations(BranchId branch, ArtifactId structureId);

   InterfaceStructureToken getMessageHeaderStructure(BranchId branch, ArtifactId connectionId, ArtifactId messageId);

}
