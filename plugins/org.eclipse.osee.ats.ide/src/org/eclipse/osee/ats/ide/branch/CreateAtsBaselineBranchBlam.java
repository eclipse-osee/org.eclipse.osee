/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.branch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class CreateAtsBaselineBranchBlam extends AbstractBlam {
   private static final String PARENT_BRANCH = "Parent Branch";
   private static final String BRANCH_NAME = "Branch Name";
   private static final String APPLY_ACCESS_CONTROL = "Apply Access Control";

   @Override
   public String getName() {
      return "Create ATS Baseline Branch";
   }

   @Override
   public Collection<String> getCategoriesStr() {
      return Arrays.asList("ATS.Admin");
   }

   @Override
   public String getDescriptionUsage() {
      return "Create an ATS Basline Branch including access control and setting associated artifact.";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"" + PARENT_BRANCH + "\" />");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"" + BRANCH_NAME + "\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + APPLY_ACCESS_CONTROL + "\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      BranchToken parentBranch = (BranchToken) variableMap.getBranch(PARENT_BRANCH);
      if (parentBranch == null) {
         log("Must select Parent Branch");
         return;
      }

      BranchType branchType = AtsApiService.get().getBranchService().getBranchType(parentBranch);
      if (!branchType.equals(BranchType.BASELINE)) {
         logf("ERROR: Parent Branch %s is [%s] and should be Baseline", parentBranch.toStringWithId(), branchType);
         return;
      }

      String branchName = variableMap.getString(BRANCH_NAME);
      if (Strings.isInValid(branchName)) {
         log("Must select Branch Name");
         return;
      }
      boolean applyAccess = variableMap.getBoolean(APPLY_ACCESS_CONTROL);

      BranchData bd = new BranchData();
      bd.setParent(parentBranch);
      bd.setAssociatedArt(AtsArtifactToken.AtsCmBranch);
      bd.setAuthor(ArtifactToken.valueOf(UserManager.getUser().getId(), UserManager.getUser().getName(),
         AtsApiService.get().getAtsBranch()));
      bd.setCreationComment(String.format("New Baseline Branch from %s", parentBranch.toStringWithId()));
      bd.setBranchType(BranchType.BASELINE);
      bd.setBranchName(branchName);
      bd.setApplyAccess(applyAccess);

      BranchData createBranch = AtsApiService.get().getBranchService().createBranch(bd);
      if (createBranch.getResults().isErrors()) {
         XResultDataUI.report(createBranch.getResults(), getName());
      } else {
         if (applyAccess) {
            log("NOTE: Apply Access not implemented yet");
         }
         log("Branch Created");
      }
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.OseeAdmin);
   }

}
