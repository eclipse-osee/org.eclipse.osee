/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Branden W. Phillips
 */
public class EditBlockedStatusAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public EditBlockedStatusAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Block/Unblock Workflow", IAction.AS_PUSH_BUTTON);
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.X_RED));
   }

   @Override
   public void runWithException() {
      for (Artifact workItem : selectedAtsArtifacts.getSelectedWorkflowArtifacts()) {
         String blockedReason = workItem.getSoleAttributeValue(AtsAttributeTypes.BlockedReason, "");
         IAtsChangeSet changes = AtsClientService.get().createChangeSet("Set blocked status");
         if (!Strings.isValid(blockedReason)) {
            EntryDialog ed =
               new EntryDialog("Setting Workflow to Blocked", "Enter the reason for this workflow being blocked");
            if (ed.open() == 0) {
               blockedReason = ed.getEntry();
               if (!Strings.isValid(blockedReason)) {
                  blockedReason = "No reason given, please enter one";
               }
               changes.setSoleAttributeValue(workItem, AtsAttributeTypes.BlockedReason, "Blocked - " + blockedReason);
            }
         } else {
            boolean unblock =
               MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Unblock Workflow", "Are you sure you wish to set this workflow to unblocked?");
            if (unblock) {
               changes.deleteAttributes(workItem, AtsAttributeTypes.BlockedReason);
            }
         }
         changes.executeIfNeeded();
      }
   }

}
