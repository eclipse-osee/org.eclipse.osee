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
import org.eclipse.osee.mim.types.InterfaceStructureElementTokenWithPath;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. V
 */
public interface InterfaceElementApi extends QueryCapableMIMAPI<InterfaceStructureElementToken>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {

   List<InterfaceStructureElementToken> getAll(BranchId branch);

   List<InterfaceStructureElementToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   List<InterfaceStructureElementToken> getAllRelated(BranchId branch, ArtifactId structureId, ArtifactId viewId);

   List<InterfaceStructureElementToken> getAllRelated(BranchId branch, ArtifactId structureId,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureElementToken> getAll(BranchId branch, long pageNum, long pageSize);

   List<InterfaceStructureElementToken> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureElementToken> getAllRelated(BranchId branch, ArtifactId structureId, long pageNum,
      long pageSize);

   List<InterfaceStructureElementToken> getAllRelated(BranchId branch, ArtifactId structureId, ArtifactId viewId,
      long pageNum, long pageSize);

   List<InterfaceStructureElementToken> getAllRelated(BranchId branch, ArtifactId structureId, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   List<InterfaceStructureElementToken> getAllRelated(BranchId branch, ArtifactId structureId, ArtifactId viewId,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   InterfaceStructureElementToken getWithAllParentRelations(BranchId branch, ArtifactId elementId);

   List<InterfaceStructureElementToken> getAllRelatedAndFilter(BranchId branch, ArtifactId structureId, String filter);

   List<InterfaceStructureElementToken> getFiltered(BranchId branch, String filter);

   List<InterfaceStructureElementToken> getAllRelatedAndFilter(BranchId branch, ArtifactId structureId, String filter,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureElementToken> getAllRelatedAndFilter(BranchId branch, ArtifactId structureId, String filter,
      long pageNum, long pageSize);

   List<InterfaceStructureElementToken> getAllRelatedAndFilter(BranchId branch, ArtifactId structureId, String filter,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   List<InterfaceStructureElementToken> getFiltered(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   List<InterfaceStructureElementToken> getFiltered(BranchId branch, String filter, long pageNum, long pageSize);

   List<InterfaceStructureElementToken> getFiltered(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   List<InterfaceStructureElementToken> getElementsByType(BranchId branch, ArtifactId platformTypeId);

   List<InterfaceStructureElementToken> getElementsByName(BranchId branch, String name, long pageNum, long pageSize);

   int getElementsByNameCount(BranchId branch, String name);

   List<InterfaceStructureElementTokenWithPath> getElementsByType(BranchId branch);

   List<InterfaceStructureElementTokenWithPath> getElementsByTypeFilter(BranchId branch, String filter);

   InterfaceStructureElementToken get(BranchId branch, ArtifactId elementId);

   Collection<InterfaceStructureElementToken> get(BranchId branch, Collection<ArtifactId> elementIds,
      Collection<FollowRelation> followRelations);

   InterfaceStructureElementToken getRelated(BranchId branch, ArtifactId structureId, ArtifactId elementId);

   List<FollowRelation> getFollowRelationDetails();

   List<InterfaceStructureElementToken> getAllFromPlatformType(PlatformTypeToken pType);

   Collection<InterfaceStructureElementToken> getAllwithNoStructureRelations(BranchId branch, String filter,
      long pageNum, long pageSize);

   int getAllwithNoStructureRelationsCount(BranchId branch, String filter);
}
