/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Branden W. Phillips
 */
public class XWorkingBranchButtonWordChangeReport extends XWorkingBranchButtonAbstract {

   public static String WIDGET_NAME = XWorkingBranchButtonWordChangeReport.class.getSimpleName();

   @Override
   protected void initButton(Button button) {
      button.setToolTipText("Generate Full Word Change Report");
      button.setImage(ImageManager.getProgramImage("docx"));
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            AtsApiService.get().getBranchServiceIde().generateWordChangeReport(getTeamArt());
         }
      });
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setEnabled(!disableAll && (isWorkingBranchInWork() || isCommittedBranchExists()));
   }
}
