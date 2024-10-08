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
import org.eclipse.osee.mim.InterfaceMessagePeriodicityApi;
import org.eclipse.osee.mim.InterfaceMessagePeriodicityEndpoint;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessagePeriodicityEndpointImpl implements InterfaceMessagePeriodicityEndpoint {

   private final InterfaceMessagePeriodicityApi MessagePeriodicityApi;
   private final BranchId branch;
   public InterfaceMessagePeriodicityEndpointImpl(BranchId branch, InterfaceMessagePeriodicityApi MessagePeriodicityApi) {
      this.MessagePeriodicityApi = MessagePeriodicityApi;
      this.branch = branch;
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllMessagePeriodicities(String filter, ArtifactId viewId,
      long pageNum, long pageSize, AttributeTypeToken orderByAttributeType) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      if (Strings.isValid(filter)) {
         return MessagePeriodicityApi.getAllByFilter(branch, viewId, filter, pageNum, pageSize, orderByAttributeType);
      }
      return MessagePeriodicityApi.getAll(branch, viewId, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public ArtifactAccessorResultWithoutGammas getMessagePeriodicity(ArtifactId rateId) {
      return MessagePeriodicityApi.get(branch, rateId);
   }

   @Override
   public int getCount(String filter, ArtifactId viewId) {
      return MessagePeriodicityApi.getCountWithFilter(branch, viewId, filter);
   }

}
