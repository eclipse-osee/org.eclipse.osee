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
package org.eclipse.osee.define.meta;

import java.io.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan D. Brooks
 */
public class ImportRelationPage extends WizardDataTransferPage {
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private Button btnSingleFile;
   private Text txtSingleFile;
   private String currentFileSelection;

   /**
    * @param name
    * @param selection
    */
   public ImportRelationPage(String name, IStructuredSelection selection) {
      super(name);
      setTitle("Import relations into Define");
      setDescription("Import relations into Define");

      if (selection != null && selection.size() == 1) {
         Object firstElement = selection.getFirstElement();
         if (firstElement instanceof IAdaptable) {
            Object resource = ((IAdaptable) firstElement).getAdapter(IResource.class);
            if (resource != null) {
               IResource currentResourceSelection = (IResource) resource;
               if (currentResourceSelection.getType() == IResource.FILE) {
                  currentFileSelection = currentResourceSelection.getLocation().toString();
               }
            }
         }
      }
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
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Import Source");
      GridLayout gd = new GridLayout();
      gd.numColumns = 3;
      composite.setLayout(gd);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      txtSingleFile = new Text(composite, SWT.SINGLE | SWT.BORDER);
      txtSingleFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      txtSingleFile.setText((currentFileSelection == null ? "" : currentFileSelection));
      txtSingleFile.addListener(SWT.Modify, this);
      btnSingleFile = new Button(composite, SWT.PUSH);
      btnSingleFile.setText("&Browse...");

      btnSingleFile.addSelectionListener(new SelectionAdapter() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            File file = selectFile();
            if (file != null && file.isFile()) txtSingleFile.setText(file.getPath());
         }
      });

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

   protected boolean validateSourceGroup() {
      if (!(new File(txtSingleFile.getText()).isFile())) {
         setErrorMessage(txtSingleFile.getText() + " is not a file");
         return false;
      }
      return true;
   }

   public boolean finish() {
      try {
         // getSpecifiedContainer().getProject();

         File file = new File(txtSingleFile.getText());
         // TODO this branch needs to be selected from the wizard not hard coded
         Job job = new ImportRelationJob(file, branchManager.getDefaultBranch());
         job.setUser(true);
         job.setPriority(Job.LONG);
         job.schedule();
      } catch (Exception ex) {
         ex.printStackTrace();
         ErrorDialog.openError(getShell(), "Define Import Error", "An error has occured while importing a document.",
               new Status(IStatus.ERROR, "org.eclipse.osee.framework.jdk.core", IStatus.ERROR,
                     "Unknown exception occurred in the import", ex));
      }
      return true;
   }

   private File selectFile() {
      FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SINGLE);
      dialog.setFilterExtensions(new String[] {"*.xml"});
      dialog.setFilterPath(AWorkspace.getWorkspacePath());

      String path = dialog.open();

      if (path != null) {
         return new File(path);
      } else {
         return null;
      }
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