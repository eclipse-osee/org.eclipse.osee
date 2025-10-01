/*******************************************************************************
 * Copyright (c) 2025 Boeing.
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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Branches;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;

/**
 * Widget to allow a single applicability token to be selected from the applicabilities configured on the product line
 * branch for this team and stored as the workflow's single applicability token. This is different than other PLE where
 * applicability tokens come from the same branch the artifact is on.
 *
 * @author Donald G. Dunne
 */
public class XHyperlinkApplicabilityDam extends XHyperlinkLabelValueSelection implements ArtifactWidget {

   private IAtsTeamWorkflow teamWf;
   private final AtsApi atsApi;
   private String value = Widgets.NOT_SET;
   private ApplicabilityToken selected;

   public XHyperlinkApplicabilityDam() {
      this("Applicability");
   }

   public XHyperlinkApplicabilityDam(String label) {
      super(label);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      return value;
   }

   @Override
   public Artifact getArtifact() {
      return (Artifact) teamWf.getStoreObject();
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public boolean handleSelection() {
      try {
         // We get applicabilities from related configured branch, eg: PLE branch config
         List<ApplicabilityToken> applicabilityTokens = getSelectableApplicabilityTokens();

         FilteredTreeDialog dialog =
            new FilteredTreeDialog("Select Applicability Impacted", "Select Applicability Impacted",
               new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
         dialog.setInput(applicabilityTokens);

         if (dialog.open() == Window.OK) {
            ApplicabilityToken selected = dialog.getSelectedFirst();
            BranchToken branch = CoreBranches.COMMON;
            // Set applicability value on this workflow on Common
            ApplicabilityEndpoint applicEp = AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
            applicEp.setApplicability(selected, Arrays.asList(teamWf.getStoreObject()));
            refresh();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public void refresh() {
      ApplicabilityEndpoint applicEp =
         AtsApiService.get().getOseeClient().getApplicabilityEndpoint(CoreBranches.COMMON);
      selected = applicEp.getApplicabilityToken(teamWf.getArtifactId());
      if (selected == null) {
         value = Widgets.NOT_SET;
      } else {
         value = selected.toString();
      }
      super.refresh();
   }

   public void refreshSuper() {
      super.refresh();
   }

   private List<ApplicabilityToken> getSelectableApplicabilityTokens() {
      List<ApplicabilityToken> tokens = new ArrayList<>();
      BranchToken branch = getBranch();
      if (Branches.isValid(branch)) {
         ApplicabilityEndpoint applicEndpoint = AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
         tokens.addAll(applicEndpoint.getApplicabilityTokens());
         return tokens;
      }
      return Collections.emptyList();
   }

   private BranchToken getBranch() {
      if (teamWf != null) {
         BranchToken branch = atsApi.getBranchService().getWorkingBranch(teamWf);
         if (Branches.isValid(branch)) {
            return branch;
         }
         Collection<BranchToken> branches = atsApi.getBranchService().getBranchesCommittedTo(teamWf);
         if (Branches.isNotEmpty(branches)) {
            return branches.iterator().next();
         }
         IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
         BranchId branch2 = atsApi.getBranchService().getBranch(teamDef);
         if (Branches.isValid(branch2)) {
            return atsApi.getBranchService().getBranch(branch2);
         }
         IAtsTeamDefinition mainTeamDef = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
         if (mainTeamDef != null) {
            BranchId branch3 = atsApi.getBranchService().getBranch(mainTeamDef);
            if (Branches.isValid(branch3)) {
               return atsApi.getBranchService().getBranch(branch3);
            }
            for (IAtsVersion ver : atsApi.getVersionService().getVersions(mainTeamDef)) {
               BranchId branch4 = atsApi.getBranchService().getBranch(ver);
               if (Branches.isValid(branch4)) {
                  return atsApi.getBranchService().getBranch(branch4);
               }
            }
         }
      }
      return BranchToken.SENTINEL;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof IAtsTeamWorkflow) {
         teamWf = (IAtsTeamWorkflow) artifact;
      }
   }

}
