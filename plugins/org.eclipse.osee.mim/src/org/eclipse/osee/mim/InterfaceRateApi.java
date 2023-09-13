/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfaceRateApi {

   ArtifactAccessorResult get(BranchId branch, ArtifactId rateId);

   Collection<ArtifactAccessorResult> getAll(BranchId branch);

   Collection<ArtifactAccessorResult> getAll(BranchId branch, ArtifactId viewId);

   Collection<ArtifactAccessorResult> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResult> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResult> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<ArtifactAccessorResult> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize);

   Collection<ArtifactAccessorResult> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResult> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResult> getAllByFilter(BranchId branch, String filter);

   Collection<ArtifactAccessorResult> getAllByFilter(BranchId branch, ArtifactId viewId, String filter);

   Collection<ArtifactAccessorResult> getAllByFilter(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResult> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResult> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   Collection<ArtifactAccessorResult> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize);

   Collection<ArtifactAccessorResult> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResult> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter);
}
