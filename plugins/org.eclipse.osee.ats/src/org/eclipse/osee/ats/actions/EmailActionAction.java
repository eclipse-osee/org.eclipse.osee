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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.notify.ArtifactEmailWizard;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EmailActionAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public EmailActionAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super();
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      try {
         updateName();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      setToolTipText(getText());
   }

   private void performEmail() {
      ArtifactEmailWizard wizard = new ArtifactEmailWizard(
         (AbstractWorkflowArtifact) selectedAtsArtifacts.getSelectedWorkflowArtifacts().iterator().next());
      WizardDialog dialog = new WizardDialog(Displays.getActiveShell(), wizard);
      dialog.create();
      dialog.open();
   }

   @Override
   public void runWithException() {
      performEmail();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EMAIL);
   }

   private void updateName() {
      setText(
         "Email " + (selectedAtsArtifacts.getSelectedWorkflowArtifacts().size() == 1 ? selectedAtsArtifacts.getSelectedWorkflowArtifacts().iterator().next().getArtifactTypeName() : ""));
   }

}
