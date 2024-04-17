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
import org.eclipse.osee.coverage.PartitionChartDataApi;
import org.eclipse.osee.coverage.PartitionChartDataEndpoint;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Stephen J. Molaro
 */
public class PartitionChartDataEndpointImpl implements PartitionChartDataEndpoint {

   private final PartitionChartDataApi partitionChartDataApi;
   private final BranchId branch;
   public PartitionChartDataEndpointImpl(BranchId branch, PartitionChartDataApi partitionChartDataApi) {
      this.partitionChartDataApi = partitionChartDataApi;
      this.branch = branch;
   }

   @Override
   public Collection<PartitionChartDataToken> getAllPartitionChartDatas(String filter, ArtifactId viewId, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType, boolean activeOnly) {
      viewId = viewId.isValid() ? viewId : ArtifactId.SENTINEL;
      if (Strings.isValid(filter)) {
         return partitionChartDataApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return partitionChartDataApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public PartitionChartDataToken getPartitionChartData(ArtifactId partitionChartDataId) {
      return partitionChartDataApi.get(branch, partitionChartDataId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return partitionChartDataApi.getCountWithFilter(branch, viewId, filter);
   }

}
