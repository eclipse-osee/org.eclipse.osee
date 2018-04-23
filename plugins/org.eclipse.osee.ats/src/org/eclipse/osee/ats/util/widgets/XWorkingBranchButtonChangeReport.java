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

import org.eclipse.osee.ats.branch.AtsBranchManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Shawn F. Cook
 */
public class XWorkingBranchButtonChangeReport extends XWorkingBranchButtonAbstract {

   public static String WIDGET_NAME = "XWorkingBranchButtonChangeReport";

   @Override
   protected void initButton(Button button) {
      button.setToolTipText("Show Change Report");
      button.setImage(ImageManager.getImage(FrameworkImage.BRANCH_CHANGE));
      button.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            AtsBranchManager.showChangeReport(getTeamArt());
         }
      });
   }

   @Override
   protected void refreshEnablement(Button button) {
      button.setEnabled(!disableAll && (isWorkingBranchInWork() || isCommittedBranchExists()));
   }
}
