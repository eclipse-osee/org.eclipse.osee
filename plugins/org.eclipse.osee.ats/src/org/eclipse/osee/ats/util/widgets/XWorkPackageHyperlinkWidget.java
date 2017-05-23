/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.column.WorkPackageFilterTreeDialog;
import org.eclipse.osee.ats.ev.WorkPackageCollectionProvider;
import org.eclipse.osee.ats.internal.AtsClientService;
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
      Collection<IAtsWorkPackage> options =
         AtsClientService.get().getEarnedValueService().getWorkPackageOptions(teamDef);

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
