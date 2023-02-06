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
package org.eclipse.osee.mim.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.CrossReferenceApi;
import org.eclipse.osee.mim.CrossReferenceEndpoint;
import org.eclipse.osee.mim.types.CrossReference;

public class CrossReferenceEndpointImpl implements CrossReferenceEndpoint {

   private final BranchId branch;
   private final CrossReferenceApi crossReferenceApi;

   public CrossReferenceEndpointImpl(BranchId branch, CrossReferenceApi crossReferenceApi) {
      this.branch = branch;
      this.crossReferenceApi = crossReferenceApi;
   }

   @Override
   public Collection<CrossReference> getAll(ArtifactId connectionId, long pageNum, long pageSize, AttributeTypeToken orderByAttributeTypeId) {
      return this.crossReferenceApi.getAll(branch, connectionId, "", pageNum, pageSize, orderByAttributeTypeId);
   }

   @Override
   public Collection<CrossReference> getAllAndFilter(ArtifactId connectionId, String filter, long pageNum, long pageSize, AttributeTypeToken orderByAttributeTypeId) {
      return this.crossReferenceApi.getAll(branch, connectionId, filter, pageNum, pageSize, orderByAttributeTypeId);
   }

   @Override
   public CrossReference get(ArtifactId crossReferenceId) {
      return this.crossReferenceApi.get(branch, crossReferenceId);
   }

}
