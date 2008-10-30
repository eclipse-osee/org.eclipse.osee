/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.branch;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.swt.NonBlankValidator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ryan D. Brooks
 */
public class ParentBranchAction extends Action {
   private static IInputValidator inputValidator = new NonBlankValidator("The Branch name can not be blank.");

   /**
    * @param text
    * @param style
    */
   public ParentBranchAction(BranchView branchView) {
      super("Create New &Parent Branch", AS_PUSH_BUTTON);
      setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("branch.gif"));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.action.Action#run()
    */
   @Override
   public void run() {
      InputDialog dialog =
            new InputDialog(Display.getCurrent().getActiveShell(), "Name Branch", "Enter branch name", "parent branch",
                  inputValidator);

      if (dialog.open() == Window.CANCEL) {
         return;
      } else {
         try {
            createNewParentBranch(dialog.getValue(), dialog.getValue());
         } catch (Exception ex) {
            OSEELog.logException(getClass(), ex, true);
         }
      }
   }

   public static Branch createNewParentBranch(String branchShortName, String branchName) throws OseeCoreException {

      List<String> skynetTypeImport = new ArrayList<String>();
      skynetTypeImport.add("org.eclipse.osee.framework.skynet.core.ProgramAndCommon");

      // Create branch, import skynet types and initialize
      Branch branch =
            BranchManager.createRootBranch(branchShortName, branchName, null, skynetTypeImport, true);

      if (PlatformUI.isWorkbenchRunning() && BranchView.getBranchView() != null) {
         BranchView.getBranchView().forcePopulateView();
      }
      return branch;
   }
}