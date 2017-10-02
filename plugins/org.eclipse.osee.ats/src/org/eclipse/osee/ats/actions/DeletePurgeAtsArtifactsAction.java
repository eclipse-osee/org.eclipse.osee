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

import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.util.AtsDeleteManager;
import org.eclipse.osee.ats.util.AtsDeleteManager.DeleteOption;
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
