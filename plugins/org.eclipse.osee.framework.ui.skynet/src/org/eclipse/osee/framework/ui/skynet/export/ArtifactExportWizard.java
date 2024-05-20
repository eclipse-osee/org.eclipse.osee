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

package org.eclipse.osee.framework.ui.skynet.export;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for exporting OSEE Artifacts from the workspace to the local file system.
 * <p>
 * This class may be instantiated and used without further configuration; this class is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * var wizard = new ArtifactExportWizard();
 * wizard.init(workbench, selection);
 * var wizardDialog = new WizardDialog(shell, wizard);
 * wizardDialog.open();
 * </pre>
 * <p>
 * During the call to <code>open</code>, the wizard dialog is presented to the user. When the user hits Finish, the
 * user-selected artifacts are exported to the user-specified location in the local file system, the dialog closes, and
 * the call to <code>open</code> returns.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public class ArtifactExportWizard extends Wizard implements IExportWizard {

   private ArtifactExportPage artifactExportPage;
   private IStructuredSelection selection;

   public ArtifactExportWizard() {

      final var workbenchSettings = ArtifactExportConstants.DEFAULT_DIALOG_SETTINGS;

      var sectionSettings = workbenchSettings.getSection(ArtifactExportConstants.WIZARD_NAME);

      if (sectionSettings == null) {
         sectionSettings = workbenchSettings.addNewSection(ArtifactExportConstants.WIZARD_NAME);
      }

      this.setDialogSettings(sectionSettings);
   }

   @Override
   public void addPages() {

      this.artifactExportPage = new ArtifactExportPage(ArtifactExportConstants.WIZARD_PAGE_1_NAME, this.selection);
      this.addPage(this.artifactExportPage);
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {

      this.selection = selection;
      this.setWindowTitle(ArtifactExportConstants.WINDOW_TITLE);
      this.setDefaultPageImageDescriptor(ArtifactExportConstants.WIZARD_PAGE_1_ICON);
      this.setNeedsProgressMonitor(true);
   }

   @Override
   public boolean performFinish() {

      return this.artifactExportPage.finish();
   }

   @Override
   public boolean performCancel() {

      return this.artifactExportPage.cancel();
   }
}