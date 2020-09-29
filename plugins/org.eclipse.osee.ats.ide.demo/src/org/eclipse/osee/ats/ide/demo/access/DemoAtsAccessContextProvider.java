/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.demo.access;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.core.access.AbstractAtsAccessContextProvider;
import org.eclipse.osee.ats.core.access.demo.DemoAtsAccessContexts;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.DemoBranches;

/**
 * @author Donald G. Dunne
 */
public class DemoAtsAccessContextProvider extends AbstractAtsAccessContextProvider {

   public DemoAtsAccessContextProvider() {
      DemoAtsAccessContexts.register();
   }

   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public boolean isAtsApplicable(BranchId branch, ArtifactToken assocArt) {
      if (branch.isValid()) {
         if (DemoBranches.DEMO_BRANCHES.contains(branch)) {
            return true;
         }
         if (AtsApiService.get().getBranchService().getBranchType(branch) == BranchType.WORKING) {
            return isAtsApplicable(AtsApiService.get().getBranchService().getParentBranch(branch), assocArt);
         }
      }
      return false;
   }

}
