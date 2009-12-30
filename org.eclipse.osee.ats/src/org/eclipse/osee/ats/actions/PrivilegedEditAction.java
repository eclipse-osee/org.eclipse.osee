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

package org.eclipse.osee.ats.actions;

import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.ReadOnlyHyperlinkListener;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class PrivilegedEditAction extends Action {

   private final StateMachineArtifact sma;

   public PrivilegedEditAction(StateMachineArtifact sma) {
      this.sma = sma;
      setText("Privileged Edit");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      togglePriviledgedEdit();
   }

   private void togglePriviledgedEdit() {
      try {
         if (sma.isReadOnly()) {
            (new ReadOnlyHyperlinkListener(sma)).linkActivated(null);
         }
         if (sma.getEditor().isPriviledgedEditModeEnabled()) {
            if (MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Diable Privileged Edit",
                  "Privileged Edit Mode Enabled.\n\nDisable?\n\nNote: (changes will be saved)")) {
               sma.getEditor().setPriviledgedEditMode(false);
            }
         } else {
            Set<User> users = sma.getPrivilegedUsers();
            if (AtsUtil.isAtsAdmin()) users.add(UserManager.getUser());
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
                        "The following users have the ability to edit this " + sma.getArtifactTypeName() + " in case of emergency.\n\n" + sb.toString(),
                        MessageDialog.QUESTION, buttons, 0);
            int result = ed.open();
            if (iAmPrivileged && result == 0) sma.getEditor().setPriviledgedEditMode(true);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.PRIVILEDGED_EDIT);
   }
}
