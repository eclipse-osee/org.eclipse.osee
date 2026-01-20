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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
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
 * Widget to allow a single configuration/view from those configured on the product line branch.
 *
 * @author Donald G. Dunne
 */
public class XHyperlinkConfigurationWidget extends XHyperlinkLabelValueSelection {

   public static final String LABEL = "Configuration";
   private BranchViewToken selected = BranchViewToken.SENTINEL;
   private IAtsTeamDefinition teamDef;

   public XHyperlinkConfigurationWidget() {
      this(LABEL);
   }

   public XHyperlinkConfigurationWidget(String label) {
      super(label);
   }

   public BranchViewToken getToken() {
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
      List<BranchViewToken> applicToks = getSelectableBranchViewTokens(teamDef);
      FilteredTreeDialog dialog = new FilteredTreeDialog("Select Configuration", "Select Configuration",
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

   public static List<BranchViewToken> getSelectableBranchViewTokens(IAtsTeamDefinition teamDef) {
      List<BranchViewToken> tokens = new ArrayList<>();
      if (teamDef != null) {
         IAtsProgram program = AtsApiService.get().getProgramService().getProgram(teamDef);
         if (program != null) {
            BranchId branch = AtsApiService.get().getProgramService().getProductLineBranch(program);
            if (Branches.isValid(branch)) {
               ApplicabilityEndpoint applicEndpoint =
                  AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
               tokens.addAll(applicEndpoint.getBranchViewTokens());
               return tokens;
            }
         }
      }
      return tokens;
   }

   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

   public void set(BranchViewToken branchViewToken) {
      if (branchViewToken.isValid()) {
         selected = branchViewToken;
      }
      refresh();
   }

}
