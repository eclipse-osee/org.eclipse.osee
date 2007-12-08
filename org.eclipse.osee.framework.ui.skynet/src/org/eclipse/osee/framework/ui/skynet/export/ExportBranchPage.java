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
package org.eclipse.osee.framework.ui.skynet.export;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Robert A. Fisher
 */
public class ExportBranchPage extends WizardDataTransferPage {
   private DirectoryOrFileSelector directoryFileSelector;

   @SuppressWarnings("unused")
   private Branch branch;

   /**
    * @param name
    * @param selection
    */
   public ExportBranchPage(String name, Branch branch) {
      super(name);
      this.branch = branch;

      setTitle("Import Skynet types into Define");
      setDescription("Import Skynet types into Define");
   }

   /**
    * (non-Javadoc) Method declared on IDialogPage.
    */
   public void createControl(Composite parent) {

      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
      composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      composite.setFont(parent.getFont());

      createSourceGroup(composite);

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());

      setControl(composite);
   }

   /**
    * The <code>WizardResourceImportPage</code> implementation of this <code>Listener</code> method handles all
    * events and enablements for controls on this page. Subclasses may extend.
    * 
    * @param event Event
    */
   public void handleEvent(Event event) {
      setPageComplete(determinePageCompletion());
   }

   protected void createSourceGroup(Composite parent) {
      directoryFileSelector = new DirectoryOrFileSelector(parent, SWT.NONE, "Export Destination", this);

      //      if (currentResourceSelection == null) {
      // Select directory as the default
      directoryFileSelector.setDirectorySelected(true);
      //      }
      //      else {
      //         directoryFileSelector.setDirectorySelected(currentResourceSelection.getType() != IResource.FILE);
      //         directoryFileSelector.setText(currentResourceSelection.getLocation().toString());
      //      }

      setPageComplete(determinePageCompletion());
   }

   /*
    * @see WizardPage#becomesVisible
    */
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      // policy: wizards are not allowed to come up with an error message
      if (visible) {
         setErrorMessage(null);
      }
   }

   @Override
   protected boolean validateSourceGroup() {
      return directoryFileSelector.validate(this);
   }

   public boolean finish() {
      try {
         // getSpecifiedContainer().getProject();

         //         File file = directoryFileSelector.getFile();
         //         Job job = new ImportMetaJob(file);
         //         job.setUser(true);
         //         job.setPriority(Job.LONG);
         //         job.schedule();
      } catch (Exception ex) {
         ex.printStackTrace();
         ErrorDialog.openError(getShell(), "Define Import Error", "An error has occured while importing a document.",
               new Status(IStatus.ERROR, "org.eclipse.osee.framework.jdk.core", IStatus.ERROR,
                     "Unknown exception occurred in the import", ex));
      }
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.dialogs.WizardResourceImportPage#allowNewContainerName()
    */
   @Override
   protected boolean allowNewContainerName() {
      return false;
   }
}