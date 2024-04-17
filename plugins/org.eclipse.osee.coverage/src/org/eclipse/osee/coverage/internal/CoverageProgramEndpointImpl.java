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
import org.eclipse.osee.coverage.CoverageProgramApi;
import org.eclipse.osee.coverage.CoverageProgramEndpoint;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Stephen J. Molaro
 */
public class CoverageProgramEndpointImpl implements CoverageProgramEndpoint {

   private final CoverageProgramApi coverageProgramApi;
   private final BranchId branch;
   public CoverageProgramEndpointImpl(BranchId branch, CoverageProgramApi coverageProgramApi) {
      this.coverageProgramApi = coverageProgramApi;
      this.branch = branch;
   }

   @Override
   public Collection<CoverageProgramToken> getAllScriptSets(String filter, ArtifactId viewId, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType, boolean activeOnly) {
      viewId = viewId.isValid() ? viewId : ArtifactId.SENTINEL;
      if (Strings.isValid(filter)) {
         return coverageProgramApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType,
            activeOnly);
      }
      return coverageProgramApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType, activeOnly);
   }

   @Override
   public CoverageProgramToken getCoverageProgram(ArtifactId coverageProgramId) {
      return coverageProgramApi.get(branch, coverageProgramId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return coverageProgramApi.getCountWithFilter(branch, viewId, filter);
   }

}
