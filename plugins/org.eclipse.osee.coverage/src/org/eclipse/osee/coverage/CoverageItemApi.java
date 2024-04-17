/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.coverage;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.coverage.internal.CoverageItemToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Stephen J. Molaro
 */
public interface CoverageItemApi {

   CoverageItemToken get(BranchId branch, ArtifactId scriptDefTypeId);

   Collection<CoverageItemToken> getAll(BranchId branch);

   Collection<CoverageItemToken> getAll(BranchId branch, ArtifactId viewId);

   Collection<CoverageItemToken> getAll(BranchId branch, ArtifactId viewId, List<FollowRelation> followRelations);

   Collection<CoverageItemToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAll(BranchId branch, ArtifactId viewId, List<FollowRelation> followRelations,
      AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<CoverageItemToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize);

   Collection<CoverageItemToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAll(BranchId branch, ArtifactId viewId, List<FollowRelation> followRelations,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAll(BranchId branch, Collection<FollowRelation> followRelations, String filter,
      Collection<AttributeTypeId> attributes, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, String filter);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, String filter,
      Collection<AttributeTypeId> searchAttributes);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      Collection<AttributeTypeId> searchAttributes, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<CoverageItemToken> getAllByFilter(BranchId branch, String filter,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      Collection<AttributeTypeId> followAttributes);

   int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter);
}
