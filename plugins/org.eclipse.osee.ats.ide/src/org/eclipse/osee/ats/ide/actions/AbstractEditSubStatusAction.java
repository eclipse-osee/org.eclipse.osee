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
package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Branden Philips
 * @author Bhawana Mishra
 */
public abstract class AbstractEditSubStatusAction extends AbstractAtsAction {
   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private final AttributeTypeString attrType;
   private final String positive;
   private final String negative;

   public AbstractEditSubStatusAction(ISelectedAtsArtifacts selectedAtsArtifacts, String positive, String negative, FrameworkImage image, AttributeTypeString attrType) {
      super(positive + "/" + negative + " Workflow", selectedAtsArtifacts);
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      this.attrType = attrType;
      this.positive = positive;
      this.negative = negative;
      setImageDescriptor(ImageManager.getImageDescriptor(image));
   }

   @Override
   public void runWithException() {
      boolean setWorkflow = false;
      boolean prompt = false;
      boolean fetchedStatus = false;
      String reason = "";
      for (Artifact workItemArt : selectedAtsArtifacts.getSelectedWorkflowArtifacts()) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + positive + " status");
         if (!fetchedStatus) {
            reason = workItemArt.getSoleAttributeValue(attrType, "");
            IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(workItemArt);
            if (reason.equals("")) {
               setWorkflow = true;
               EntryDialog ed = new EntryDialog("Setting Workflow to " + positive,
                  "Enter the reason to set this workflow to " + positive);
               if (ed.open() == 0) {
                  reason = ed.getEntry();
                  if (!Strings.isValid(reason)) {
                     reason = getSubStatusReason(workItem);
                  }
               }
            }
            /*
             * return or do nothing if the Workflow isn't already in block and user pressed either Cancel or OK without
             * entering reason
             */
            if (reason.isEmpty() && getSubStatusReason(workItem).isEmpty()) {
               return;
            }
            fetchedStatus = true;
         }
         if (setWorkflow) {
            changes.setSoleAttributeValue(workItemArt, attrType, positive + " - " + reason);
         } else {
            if (!prompt) {
               if (!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  negative + " Workflow", "Are you sure you wish to set this workflow to " + negative + "?")) {
                  return;
               }
               prompt = true;
            }
            changes.deleteAttributes(workItemArt, attrType);
         }
         changes.executeIfNeeded();
      }
   }

   private String getSubStatusReason(IAtsWorkItem workItem) {
      return AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem, attrType, "");
   }
}
