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

package org.eclipse.osee.ats.editor.service;

import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class PrivilegedEditService extends WorkPageService {

   private Action action;

   public PrivilegedEditService(SMAManager smaMgr) {
      super(smaMgr);
   }

   @Override
   public Action createToolbarService() {
      action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            togglePriviledgedEdit();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.PRIVILEDGED_EDIT));
      return action;
   }

   private void togglePriviledgedEdit() {
      try {
         if (smaMgr.getSma().isReadOnly()) {
            (new ReadOnlyHyperlinkListener(smaMgr)).linkActivated(null);
         }
         if (smaMgr.getEditor().isPriviledgedEditModeEnabled()) {
            if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Diable Privileged Edit",
                  "Privileged Edit Mode Enabled.\n\nDisable?\n\nNote: (changes will be saved)")) {
               smaMgr.getEditor().setPriviledgedEditMode(false);
            }
         } else {
            Set<User> users = smaMgr.getPrivilegedUsers();
            if (AtsPlugin.isAtsAdmin()) users.add(UserManager.getUser());
            StringBuffer sb = new StringBuffer();
            for (User user : users)
               sb.append(user.getName() + "\n");
            String buttons[];
            boolean iAmPrivileged = users.contains(UserManager.getUser());
            if (iAmPrivileged)
               buttons = new String[] {"Override and Edit", "Cancel"};
            else
               buttons = new String[] {"Cancel"};
            MessageDialog ed =
                  new MessageDialog(
                        Display.getCurrent().getActiveShell(),
                        "Privileged Edit",
                        null,
                        "The following users have the ability to edit this " + smaMgr.getSma().getArtifactTypeName() + " in case of emergency.\n\n" + sb.toString(),
                        MessageDialog.QUESTION, buttons, 0);
            int result = ed.open();
            if (iAmPrivileged && result == 0) smaMgr.getEditor().setPriviledgedEditMode(true);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#isShowSidebarService(org.eclipse.osee.ats.workflow.AtsWorkPage)
    */
   @Override
   public boolean isShowSidebarService(AtsWorkPage page) throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Privileged Edit";
   }
}
