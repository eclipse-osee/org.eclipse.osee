/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config.operation;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.branch.BranchData;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigOperations {

   private final AtsApi atsApi;

   public AtsConfigOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public BranchData createBranch(BranchData bd) {
      atsApi.getBranchService().validate(bd, atsApi);
      if (bd.isValidate() || bd.getResults().isErrors()) {
         return bd;
      }

      atsApi.getBranchService().createBranch(bd);
      return bd;
   }
}
