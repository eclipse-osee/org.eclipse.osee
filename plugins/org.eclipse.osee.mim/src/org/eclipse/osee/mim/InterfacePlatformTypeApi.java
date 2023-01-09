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

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfacePlatformTypeApi extends QueryCapableMIMAPI<PlatformTypeToken>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {

   ArtifactAccessor<PlatformTypeToken> getAccessor();

   PlatformTypeToken get(BranchId branch, ArtifactId platformTypeId);

   List<PlatformTypeToken> getAll(BranchId branch);

   List<PlatformTypeToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAll(BranchId branch, long pageNum, long pageSize);

   List<PlatformTypeToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   PlatformTypeToken getWithRelations(BranchId branch, ArtifactId platformTypeId, List<RelationTypeSide> relationTypes);

   List<PlatformTypeToken> getAllWithRelations(BranchId branch, List<RelationTypeSide> relationTypes);

   List<PlatformTypeToken> getAllWithEnumSet(BranchId branch);

   List<PlatformTypeToken> getFilteredWithRelations(BranchId branch, String filter, List<RelationTypeSide> relationTypes);

   List<PlatformTypeToken> getAllWithRelations(BranchId branch, List<RelationTypeSide> relationTypes, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllWithRelations(BranchId branch, List<RelationTypeSide> relationTypes, long pageNum, long pageSize);

   List<PlatformTypeToken> getAllWithRelations(BranchId branch, List<RelationTypeSide> relationTypes, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllWithEnumSet(BranchId branch, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllWithEnumSet(BranchId branch, long pageNum, long pageSize);

   List<PlatformTypeToken> getAllWithEnumSet(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getFilteredWithRelations(BranchId branch, String filter, List<RelationTypeSide> relationTypes, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getFilteredWithRelations(BranchId branch, String filter, List<RelationTypeSide> relationTypes, long pageNum, long pageSize);

   List<PlatformTypeToken> getFilteredWithRelations(BranchId branch, String filter, List<RelationTypeSide> relationTypes, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   PlatformTypeToken getWithAllParentRelations(BranchId branch, ArtifactId platformTypeId);

   PlatformTypeToken getWithElementRelations(BranchId branch, ArtifactId platformTypeId);

   List<PlatformTypeToken> getAllWithElementRelations(BranchId branch);

   List<PlatformTypeToken> getFilteredWithElementRelations(BranchId branch, String filter);

   List<PlatformTypeToken> getAllWithElementRelations(BranchId branch, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getFilteredWithElementRelations(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllWithElementRelations(BranchId branch, long pageNum, long pageSize);

   List<PlatformTypeToken> getFilteredWithElementRelations(BranchId branch, String filter, long pageNum, long pageSize);

   List<PlatformTypeToken> getAllWithElementRelations(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getFilteredWithElementRelations(BranchId branch, String filter, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   List<PlatformTypeToken> getAllFromEnumerationSet(InterfaceEnumerationSet enumSet);

   String getUniqueIdentifier(String logicalType, String min, String max, String validRange, String units, String defaultValue, int bytes);
}
