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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
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
public class XHyperlinkBuildImpactWidget extends XHyperlinkLabelValueSelection {

   public static final String LABEL = "Build Impact";
   private String selected = null;
   private IAtsTeamDefinition teamDef;

   public XHyperlinkBuildImpactWidget() {
      this(LABEL);
   }

   public XHyperlinkBuildImpactWidget(String label) {
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

      Collection<IAtsVersion> versions =
         AtsApiService.get().getVersionService().getVersionsFromTeamDefHoldingVersions(teamDef);
      Set<String> verNames = versions.stream().map(IAtsVersion::getName).collect(Collectors.toSet());

      FilteredTreeDialog dialog = new FilteredTreeDialog("Select Build Impact", "Select Build Impact",
         new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
      dialog.setInput(verNames);

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
