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
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfacePlatformTypeApi extends QueryCapableMIMAPI<PlatformTypeToken>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {

   ArtifactAccessor<PlatformTypeToken> getAccessor();

   PlatformTypeToken get(BranchId branch, ArtifactId platformTypeId);

   List<PlatformTypeToken> get(BranchId branch, List<ArtifactId> platformTypeIds, List<FollowRelation> followRelations);

   List<PlatformTypeToken> getAll(BranchId branch);

   List<PlatformTypeToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAll(BranchId branch, long pageNum, long pageSize);

   List<PlatformTypeToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   PlatformTypeToken getWithRelations(BranchId branch, ArtifactId platformTypeId, List<FollowRelation> relationTypes);

   List<PlatformTypeToken> getAllWithRelations(BranchId branch, List<FollowRelation> relationTypes);

   List<PlatformTypeToken> getAllWithEnumSet(BranchId branch);

   List<PlatformTypeToken> getFilteredWithRelations(BranchId branch, String filter, List<FollowRelation> relationTypes);

   List<PlatformTypeToken> getAllWithRelations(BranchId branch, List<FollowRelation> relationTypes,
      AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllWithRelations(BranchId branch, List<FollowRelation> relationTypes, long pageNum,
      long pageSize);

   List<PlatformTypeToken> getAllWithRelations(BranchId branch, List<FollowRelation> relationTypes, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllWithEnumSet(BranchId branch, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllWithEnumSet(BranchId branch, long pageNum, long pageSize);

   List<PlatformTypeToken> getAllWithEnumSet(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getFilteredWithRelations(BranchId branch, String filter, List<FollowRelation> relationTypes,
      AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getFilteredWithRelations(BranchId branch, String filter, List<FollowRelation> relationTypes,
      long pageNum, long pageSize);

   List<PlatformTypeToken> getFilteredWithRelations(BranchId branch, String filter, List<FollowRelation> relationTypes,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   PlatformTypeToken getWithAllParentRelations(BranchId branch, ArtifactId platformTypeId);

   PlatformTypeToken getWithElementRelations(BranchId branch, ArtifactId platformTypeId);

   List<PlatformTypeToken> getAllWithElementRelations(BranchId branch);

   List<PlatformTypeToken> getFilteredWithElementRelations(BranchId branch, String filter);

   List<PlatformTypeToken> getAllWithElementRelations(BranchId branch, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getFilteredWithElementRelations(BranchId branch, String filter,
      AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllWithElementRelations(BranchId branch, long pageNum, long pageSize);

   List<PlatformTypeToken> getFilteredWithElementRelations(BranchId branch, String filter, long pageNum, long pageSize);

   List<PlatformTypeToken> getAllWithElementRelations(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getFilteredWithElementRelations(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllFromEnumerationSet(InterfaceEnumerationSet enumSet);

   String getUniqueIdentifier(String logicalType, String min, String max, String validRange, String units,
      String defaultValue, int bytes);

   Collection<PlatformTypeToken> getAllwithNoElementRelations(BranchId branch, String filter, long pageNum,
      long pageSize);

   int getAllwithNoElementRelationsCount(BranchId branch, String filter);
}
