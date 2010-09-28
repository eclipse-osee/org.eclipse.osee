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

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.ArtifactEmailWizard;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EmailActionAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public EmailActionAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super();
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      try {
         updateName();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      setToolTipText(getText());
   }

   private void performEmail() throws OseeCoreException {
      ArtifactEmailWizard wizard =
         new ArtifactEmailWizard(
            ((AbstractWorkflowArtifact) selectedAtsArtifacts.getSelectedSMAArtifacts().iterator().next()));
      WizardDialog dialog = new WizardDialog(Displays.getActiveShell(), wizard);
      dialog.create();
      dialog.open();
   }

   @Override
   public void run() {
      try {
         performEmail();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EMAIL);
   }

   private void updateName() throws OseeCoreException {
      setText("Email " + (selectedAtsArtifacts.getSelectedSMAArtifacts().size() == 1 ? selectedAtsArtifacts.getSelectedSMAArtifacts().iterator().next().getArtifactTypeName() : ""));
   }

   public void updateEnablement() {
      try {
         setEnabled(selectedAtsArtifacts.getSelectedSMAArtifacts().size() == 1);
         updateName();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         setEnabled(false);
      }
   }

}
