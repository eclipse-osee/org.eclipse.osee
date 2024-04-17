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
import org.eclipse.osee.coverage.PartitionDefApi;
import org.eclipse.osee.coverage.PartitionDefEndpoint;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Stephen J. Molaro
 */
public class PartitionDefEndpointImpl implements PartitionDefEndpoint {

   private final PartitionDefApi partitionDefApi;
   private final BranchId branch;
   public PartitionDefEndpointImpl(BranchId branch, PartitionDefApi partitionDefApi) {
      this.partitionDefApi = partitionDefApi;
      this.branch = branch;
   }

   @Override
   public Collection<PartitionDefToken> getAllPartitionDefs(String filter, ArtifactId viewId, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType, boolean activeOnly) {
      viewId = viewId.isValid() ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return partitionDefApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return partitionDefApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public PartitionDefToken getPartitionDef(ArtifactId partitionDefId) {
      return partitionDefApi.get(branch, partitionDefId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return partitionDefApi.getCountWithFilter(branch, viewId, filter);
   }

}
