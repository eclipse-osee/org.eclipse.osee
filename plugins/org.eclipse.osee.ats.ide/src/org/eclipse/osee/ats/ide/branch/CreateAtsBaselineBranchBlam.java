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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.branch.BranchData;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CreateAtsBaselineBranchBlam extends AbstractAtsBlam {
   private static final String PARENT_BRANCH = "Parent Branch";
   private static final String BRANCH_NAME = "Branch Name";
   private static final String APPLY_ACCESS_CONTROL = "Apply Access Control";

   @Override
   public String getName() {
      return "Create ATS Baseline Branch";
   }

   @Override
   public String getDescriptionUsage() {
      return "Create an ATS Basline Branch including access control and setting associated artifact.";
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andBranchSelWidget();
      wb.andWidget(BRANCH_NAME, WidgetId.XTextWidget);
      wb.andWidget(APPLY_ACCESS_CONTROL, WidgetId.XCheckBoxWidget).andLabelAfter().andHorizLabel().andDefault("true");
      return wb.getXWidgetDatas();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      BranchToken parentBranch = variableMap.getBranch(PARENT_BRANCH);
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
      bd.setAuthor(ArtifactToken.valueOf(OseeApiService.user().getId(), OseeApiService.user().getName(),
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
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

}
