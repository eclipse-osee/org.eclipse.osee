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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.InterfaceValidationApi;
import org.eclipse.osee.mim.InterfaceValidationEndpoint;
import org.eclipse.osee.mim.types.ConnectionValidationResult;

/**
 * @author Ryan T. Baldwin
 */
public class InterfaceValidationEndpointImpl implements InterfaceValidationEndpoint {

   private final BranchId branch;
   private final InterfaceValidationApi validationApi;

   public InterfaceValidationEndpointImpl(BranchId branch, InterfaceValidationApi validationApi) {
      this.branch = branch;
      this.validationApi = validationApi;
   }

   @Override
   public ConnectionValidationResult validateConnection(ArtifactId connectionId, ArtifactId viewId) {
      return validationApi.validateConnection(branch, viewId, connectionId);
   }

}
