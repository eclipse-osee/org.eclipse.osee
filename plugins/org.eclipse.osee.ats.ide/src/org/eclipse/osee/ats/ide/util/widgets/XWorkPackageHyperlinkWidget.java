/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.column.WorkPackageFilterTreeDialog;
import org.eclipse.osee.ats.ide.ev.WorkPackageCollectionProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XWorkPackageHyperlinkWidget extends XHyperlinkLabelCmdValueSelection {

   IAtsWorkPackage workPackage = null;
   private final IAtsTeamDefinition teamDef;

   public XWorkPackageHyperlinkWidget(IAtsTeamDefinition teamDef) {
      super("Work Package", true, 50);
      this.teamDef = teamDef;
   }

   @Override
   public String getCurrentValue() {
      return workPackage == null ? "Not Selected" : workPackage.toString();
   }

   @Override
   public boolean handleSelection() {
      Collection<IAtsWorkPackage> options = AtsApiService.get().getEarnedValueService().getWorkPackageOptions(teamDef);
      if (options.isEmpty()) {
         AWorkbench.popup("No Work Packages configured for this Team");
         return false;
      }

      WorkPackageFilterTreeDialog dialog = new WorkPackageFilterTreeDialog("Select Work Package", "Select Work Package",
         new WorkPackageCollectionProvider(options));
      dialog.setInput();
      if (dialog.open() == Window.OK) {
         boolean removeFromWorkPackage = dialog.isRemoveFromWorkPackage();
         if (removeFromWorkPackage) {
            workPackage = null;
         } else {
            workPackage = dialog.getSelection();
         }
         return true;
      }
      return false;
   }

   @Override
   public boolean handleClear() {
      workPackage = null;
      return true;
   }

   public IAtsWorkPackage getSelected() {
      return workPackage;
   }

}
