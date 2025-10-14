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
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.skynet.core.utility.Branches;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
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
public class XHyperlinkApplicabilityWidget extends XHyperlinkLabelValueSelection {

   public static final String LABEL = "Applicability";
   private final AtsApiIde atsApi;
   private ApplicabilityToken selected = ApplicabilityToken.SENTINEL;
   private IAtsTeamDefinition teamDef;

   public XHyperlinkApplicabilityWidget() {
      this(LABEL);
   }

   public XHyperlinkApplicabilityWidget(String label) {
      super(label);
      atsApi = AtsApiService.get();
   }

   public ApplicabilityToken getToken() {
      return selected;
   }

   @Override
   public String getCurrentValue() {
      if (selected == null || selected.isInvalid()) {
         return Widgets.NOT_SET;
      }
      return selected.getName();
   }

   @Override
   public boolean handleSelection() {
      if (teamDef == null) {
         AWorkbench.popup("Team Definition must be selected");
         return false;
      }
      List<ApplicabilityToken> applicToks = getSelectableApplicabilityTokens(teamDef);
      FilteredTreeDialog dialog =
         new FilteredTreeDialog("Select Applicability Impacted", "Select Applicability Impacted",
            new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
      dialog.setInput(applicToks);

      boolean changed = false;
      if (dialog.open() == Window.OK) {
         selected = dialog.getSelectedFirst();
         return true;
      }
      if (changed) {
         refresh();
      }
      return changed;
   }

   public static List<ApplicabilityToken> getSelectableApplicabilityTokens(IAtsTeamDefinition teamDef) {
      List<ApplicabilityToken> tokens = new ArrayList<>();
      if (teamDef != null) {
         IAtsProgram program = AtsApiService.get().getProgramService().getProgram(teamDef);
         if (program != null) {
            BranchId branch = AtsApiService.get().getProgramService().getProductLineBranch(program);
            if (Branches.isValid(branch)) {
               ApplicabilityEndpoint applicEndpoint =
                  AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
               tokens.addAll(applicEndpoint.getApplicabilityTokens());
               return tokens;
            }
         }
      }
      return java.util.Collections.emptyList();
   }

   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

   public void set(Long storedApplicId) {
      ApplicabilityToken storedTok =
         atsApi.getStoreService().getApplicabilityToken(ApplicabilityId.valueOf(storedApplicId));
      if (storedTok.isValid()) {
         selected = storedTok;
      }
      refresh();
   }

}
