/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import org.eclipse.osee.ats.ide.util.AtsDeleteManager;
import org.eclipse.osee.ats.ide.util.AtsDeleteManager.DeleteOption;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DeletePurgeAtsArtifactsAction extends AbstractAtsAction {

   private static final String MSG = "Delete/Purge Ats Artifact(s)";
   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private boolean prompt = true;
   private final boolean executeInCurrentThread;

   public DeletePurgeAtsArtifactsAction(ISelectedAtsArtifacts selectedAtsArtifacts, boolean executeInCurrentThread) {
      super(MSG, ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_EDITOR));
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText(getText());
      this.executeInCurrentThread = executeInCurrentThread;
   }

   @Override
   public void runWithException() {
      AtsDeleteManager.handleDeletePurgeAtsObject(selectedAtsArtifacts.getSelectedWorkflowArtifacts(),
         executeInCurrentThread, prompt ? DeleteOption.Prompt : null);
   }

   public void setPrompt(boolean prompt) {
      this.prompt = prompt;
   }
}
