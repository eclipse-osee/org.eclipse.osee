/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
            boolean deleted = AtsBranchManager.deleteWorkingBranch(getTeamArt(), true, false);
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
      if (Strings.isValid(button.getText())) {
         button.setText("");
         button.getParent().layout();
      }
      button.setEnabled(!disableAll && isWorkingBranchInWork() && !isCommittedBranchExists());
   }

}
