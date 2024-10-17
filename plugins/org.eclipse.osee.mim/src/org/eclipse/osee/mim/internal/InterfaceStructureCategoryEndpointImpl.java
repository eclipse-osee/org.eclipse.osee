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

package org.eclipse.osee.mim.internal;

import java.util.Collection;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.InterfaceStructureCategoryApi;
import org.eclipse.osee.mim.InterfaceStructureCategoryEndpoint;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureCategoryEndpointImpl implements InterfaceStructureCategoryEndpoint {

   private final InterfaceStructureCategoryApi structureCategoryApi;
   private final BranchId branch;
   public InterfaceStructureCategoryEndpointImpl(BranchId branch, InterfaceStructureCategoryApi structureCategoryApi) {
      this.structureCategoryApi = structureCategoryApi;
      this.branch = branch;
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllStructureCategories(String filter, ArtifactId viewId,
      long pageNum, long pageSize, AttributeTypeToken orderByAttributeType) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return structureCategoryApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return structureCategoryApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public ArtifactAccessorResultWithoutGammas getStructureCategory(ArtifactId rateId) {
      return structureCategoryApi.get(branch, rateId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return structureCategoryApi.getCountWithFilter(branch, viewId, filter);
   }

}
