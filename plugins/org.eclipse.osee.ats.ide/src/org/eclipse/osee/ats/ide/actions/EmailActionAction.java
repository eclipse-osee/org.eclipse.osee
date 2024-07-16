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

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.notify.WorkflowEmailWizard;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
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
      WorkflowEmailWizard wizard = new WorkflowEmailWizard(
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
