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
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfaceUnitApi {

   ArtifactAccessorResultWithoutGammas get(BranchId branch, ArtifactId unitId);

   Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch);

   Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, ArtifactId viewId);

   Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize);

   Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, String filter);

   Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, ArtifactId viewId, String filter);

   Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize);

   Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter);
}
