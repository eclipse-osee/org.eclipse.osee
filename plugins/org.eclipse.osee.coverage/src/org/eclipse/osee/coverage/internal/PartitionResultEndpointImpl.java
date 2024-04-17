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
import org.eclipse.osee.coverage.PartitionResultApi;
import org.eclipse.osee.coverage.PartitionResultEndpoint;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Stephen J. Molaro
 */
public class PartitionResultEndpointImpl implements PartitionResultEndpoint {

   private final PartitionResultApi partitionResultApi;
   private final BranchId branch;
   public PartitionResultEndpointImpl(BranchId branch, PartitionResultApi partitionResultApi) {
      this.partitionResultApi = partitionResultApi;
      this.branch = branch;
   }

   @Override
   public Collection<PartitionResultToken> getAllPartitionResults(String filter, ArtifactId viewId, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType, boolean activeOnly) {
      viewId = viewId.isValid() ? viewId : ArtifactId.SENTINEL;
      if (Strings.isValid(filter)) {
         return partitionResultApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return partitionResultApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public PartitionResultToken getPartitionResult(ArtifactId partitionResultId) {
      return partitionResultApi.get(branch, partitionResultId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return partitionResultApi.getCountWithFilter(branch, viewId, filter);
   }

}
