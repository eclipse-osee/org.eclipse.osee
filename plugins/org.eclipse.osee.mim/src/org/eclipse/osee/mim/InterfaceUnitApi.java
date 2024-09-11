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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.InterfaceUnitToken;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfaceUnitApi {

   InterfaceUnitToken get(BranchId branch, ArtifactId unitId);

   Collection<InterfaceUnitToken> getAll(BranchId branch);

   Collection<InterfaceUnitToken> getAll(BranchId branch, ArtifactId viewId);

   Collection<InterfaceUnitToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<InterfaceUnitToken> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute);

   Collection<InterfaceUnitToken> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<InterfaceUnitToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize);

   Collection<InterfaceUnitToken> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<InterfaceUnitToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<InterfaceUnitToken> getAllByFilter(BranchId branch, String filter);

   Collection<InterfaceUnitToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter);

   Collection<InterfaceUnitToken> getAllByFilter(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   Collection<InterfaceUnitToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute);

   Collection<InterfaceUnitToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   Collection<InterfaceUnitToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize);

   Collection<InterfaceUnitToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<InterfaceUnitToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter);
}
