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

package org.eclipse.osee.ats.ide.actions;

import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.PrivilegedUserManager;
import org.eclipse.osee.ats.ide.util.ReadOnlyHyperlinkListener;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class PrivilegedEditAction extends AbstractAtsAction {

   private final AbstractWorkflowArtifact sma;
   private final WorkflowEditor editor;

   public PrivilegedEditAction(AbstractWorkflowArtifact sma, WorkflowEditor editor) {
      super();
      this.sma = sma;
      this.editor = editor;
      setText("Privileged Edit");
      setToolTipText(getText());
   }

   @Override
   public void runWithException() {
      if (sma.isReadOnly()) {
         new ReadOnlyHyperlinkListener(sma).linkActivated(null);
      }
      if (editor.isPrivilegedEditModeEnabled()) {
         if (MessageDialog.openQuestion(Displays.getActiveShell(), "Diable Privileged Edit",
            "Privileged Edit Mode Enabled.\n\nDisable?\n\nNote: (changes will be saved)")) {
            editor.setPrivilegedEditMode(false);
         }
      } else {
         Set<IAtsUser> users = PrivilegedUserManager.getPrivilegedUsers(sma);
         if (AtsClientService.get().getUserService().isAtsAdmin()) {
            users.add(AtsClientService.get().getUserService().getCurrentUser());
         }
         StringBuffer stringBuffer = new StringBuffer();
         for (IAtsUser user : users) {
            stringBuffer.append(user.getName());
            stringBuffer.append("\n");
         }
         String buttons[];
         boolean iAmPrivileged = users.contains(AtsClientService.get().getUserService().getCurrentUser());
         if (iAmPrivileged) {
            buttons = new String[] {"Override and Edit", "Cancel"};
         } else {
            buttons = new String[] {"Cancel"};
         }
         MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Privileged Edit", null,
            "The following users have the ability to edit this " + sma.getArtifactTypeName() + " in case of emergency.\n\n" + stringBuffer.toString(),
            MessageDialog.QUESTION, buttons, 0);
         int result = dialog.open();
         if (iAmPrivileged && result == 0) {
            editor.setPrivilegedEditMode(true);
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.PRIVILEGED_EDIT);
   }
}
