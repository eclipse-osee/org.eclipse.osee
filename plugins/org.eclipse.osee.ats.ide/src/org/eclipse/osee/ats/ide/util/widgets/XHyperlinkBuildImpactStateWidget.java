/*******************************************************************************
 * Copyright (c) 2026 Boeing.
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

import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactState;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * Widget to allow a single configuration/view from those configured on the product line branch.
 *
 * @author Donald G. Dunne
 */
public class XHyperlinkBuildImpactStateWidget extends XHyperlinkLabelValueSelection {

   public static final String LABEL = "Build Impact State";
   private String selected = null;
   private IAtsTeamDefinition teamDef;

   public XHyperlinkBuildImpactStateWidget() {
      this(LABEL);
   }

   public XHyperlinkBuildImpactStateWidget(String label) {
      super(label);
   }

   public String getSelected() {
      return selected;
   }

   @Override
   public String getCurrentValue() {
      if (Strings.isInvalid(selected)) {
         return Widgets.NOT_SET;
      }
      return selected;
   }

   @Override
   public boolean handleSelection() {
      if (teamDef == null) {
         AWorkbench.popup("Team Definition must be selected");
         return false;
      }

      FilteredTreeDialog dialog = new FilteredTreeDialog("Select Build Impact State", "Select Build Impact State",
         new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
      dialog.setInput(BuildImpactState.getStateNames());

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

   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

   public void set(String verName) {
      if (Strings.isValid(verName)) {
         selected = verName;
      } else {
         selected = "";
      }
      refresh();
   }

   public void clear() {
      selected = "";
      refresh();
   }

}
