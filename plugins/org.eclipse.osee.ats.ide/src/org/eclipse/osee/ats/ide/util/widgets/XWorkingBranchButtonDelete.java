/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Shawn F. Cook
 */
public class XWorkingBranchButtonDelete extends XWorkingBranchButtonAbstract {

   public static String WIDGET_NAME = "XWorkingBranchButtonDelete";

   @Override
   protected void initButton(final Button button) {
      button.setToolTipText("Delete Working Branch");
      button.setImage(ImageManager.getImage(FrameworkImage.TRASH));
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            disableAll = true;
            refreshEnablement(button);
            button.setText("Deleting Branch...");
            button.redraw();
            button.getParent().layout();
            boolean deleted = AtsApiService.get().getBranchServiceIde().deleteWorkingBranch(getTeamArt(), true, false);
            if (!deleted) {
               button.setText("");
               button.getParent().layout();
               disableAll = false;
               refreshEnablement(button);
            }
         }
      });
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setText("");
      button.getParent().layout();
      button.setEnabled(!disableAll && isWorkingBranchInWork() && !isCommittedBranchExists());
   }

}
