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

package org.eclipse.osee.coverage.internal;

import java.util.Collection;
import org.eclipse.osee.coverage.CoverageItemApi;
import org.eclipse.osee.coverage.CoverageItemEndpoint;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Stephen J. Molaro
 */
public class CoverageItemEndpointImpl implements CoverageItemEndpoint {

   private final CoverageItemApi coverageItemApi;
   private final BranchId branch;
   public CoverageItemEndpointImpl(BranchId branch, CoverageItemApi coverageItemApi) {
      this.coverageItemApi = coverageItemApi;
      this.branch = branch;
   }

   @Override
   public Collection<CoverageItemToken> getAllCoverageItems(String filter, ArtifactId viewId, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType, boolean activeOnly) {
      viewId = viewId.isValid() ? viewId : ArtifactId.SENTINEL;
      if (Strings.isValid(filter)) {
         return coverageItemApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return coverageItemApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public CoverageItemToken getCoverageItem(ArtifactId coverageItemId) {
      return coverageItemApi.get(branch, coverageItemId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return coverageItemApi.getCountWithFilter(branch, viewId, filter);
   }

}
