/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.mim.TransportTypeApi;
import org.eclipse.osee.mim.TransportTypeEndpoint;
import org.eclipse.osee.mim.types.TransportType;

public class TransportTypeEndpointImpl implements TransportTypeEndpoint {

   private final BranchId branch;
   private final TransportTypeApi transportTypeApi;

   public TransportTypeEndpointImpl(BranchId branch, TransportTypeApi transportTypeApi) {
      this.branch = branch;
      this.transportTypeApi = transportTypeApi;
   }

   @Override
   public Collection<TransportType> getAll(long pageNum, long pageSize, AttributeTypeToken orderByAttributeTypeId) {
      return this.transportTypeApi.getAll(branch, pageNum, pageSize, orderByAttributeTypeId);
   }

   @Override
   public TransportType get(ArtifactId transportTypeId) {
      return this.transportTypeApi.get(branch, transportTypeId);
   }

}
