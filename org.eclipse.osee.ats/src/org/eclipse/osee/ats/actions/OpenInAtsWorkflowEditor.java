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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;

/**
 * @author Donald G. Dunne
 */
public class OpenInAtsWorkflowEditor extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public OpenInAtsWorkflowEditor(String name, ISelectedAtsArtifacts selectedAtsArtifacts) {
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setText(name);
   }

   public OpenInAtsWorkflowEditor(ISelectedAtsArtifacts selectedAtsArtifacts) {
      this("Open in ATS Workflow Editor", selectedAtsArtifacts);
   }

   public void performOpen() {
      try {
         for (Artifact art : selectedAtsArtifacts.getSelectedSMAArtifacts()) {
            AtsUtil.openAtsAction((StateMachineArtifact) art, AtsOpenOption.OpenAll);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void updateEnablement() {
      try {
         setEnabled(selectedAtsArtifacts.getSelectedSMAArtifacts().size() > 0);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         setEnabled(false);
      }
   }

   @Override
   public void run() {
      performOpen();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.WORKFLOW);
   }

}
