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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkWfdForConfiguration extends XHyperlinkWithFilteredDialog<ArtifactToken> {

   BranchId branch = BranchId.SENTINEL;

   public XHyperlinkWfdForConfiguration() {
      super("");
   }

   @Override
   protected boolean isSelectable() {
      if (branch.isInvalid()) {
         AWorkbench.popup("Build must be selected");
         return false;
      }
      if (ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch).getViews().isEmpty()) {
         Branch fullBranch = BranchManager.getBranch(branch);
         AWorkbench.popupf("No Valid Configurations found for build [%s]", fullBranch.getName());
         return false;
      }
      return true;
   }

   @Override
   public Collection<ArtifactToken> getSelectable() {
      if (branch.isValid()) {
         return ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch).getViews();
      } else {
         return Collections.emptyList();
      }
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

}
