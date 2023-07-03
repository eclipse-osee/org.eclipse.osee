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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.InterfaceRateApi;
import org.eclipse.osee.mim.InterfaceRateEndpoint;
import org.eclipse.osee.mim.types.PLGenericDBObject;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceRateEndpointImpl implements InterfaceRateEndpoint {

   private final InterfaceRateApi rateApi;
   private final BranchId branch;
   public InterfaceRateEndpointImpl(BranchId branch, InterfaceRateApi rateApi) {
      this.rateApi = rateApi;
      this.branch = branch;
   }

   @Override
   public Collection<PLGenericDBObject> getAllRates(String filter, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return rateApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return rateApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public PLGenericDBObject getRate(ArtifactId rateId) {
      return rateApi.get(branch, rateId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return rateApi.getCountWithFilter(branch, viewId, filter);
   }

}
