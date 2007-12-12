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
package org.eclipse.osee.ats.util.Import;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.FileSelector;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.dialogs.WizardResourceImportPage;

/**
 * @author Donald G. Dunne
 */
public class TaskImportPage extends WizardDataTransferPage {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(WizardResourceImportPage.class);

   private IResource currentResourceSelection;
   private FileSelector fileSelector;
   private Text hridText;
   private Label actionLabel;
   private Button emailPocs;
   private String hrid;

   /**
    * @param pageName
    * @param selection
    */
   public TaskImportPage(String pageName, IStructuredSelection selection, String hrid) {
      super(pageName);
      this.hrid = hrid;
      setTitle("Import tasks into ATS");
      setDescription("Import tasks into ATS");

      if (selection != null && selection.size() == 1) {
         Object firstElement = selection.getFirstElement();
         if (firstElement instanceof IAdaptable) {
            currentResourceSelection = (IResource) ((IAdaptable) firstElement).getAdapter(IResource.class);
         }
      }
   }

   /**
    * (non-Javadoc) Method declared on IDialogPage.
    */
   public void createControl(Composite parent) {

      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
      composite.setFont(parent.getFont());

      createSourceGroup(composite);

      createOptionsGroup(composite);

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());

      setControl(composite);
   }

   protected void createOptionsGroup(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Options");
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      composite.setLayout(new GridLayout(2, false));

      Label label = new Label(composite, SWT.NONE);
      label.setText("HRID:");
      label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

      hridText = new Text(composite, SWT.SINGLE | SWT.BORDER);
      hridText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      hridText.addListener(SWT.Modify, this);
      if (hrid != null && !hrid.equals("")) hridText.setText(hrid);

      emailPocs = new Button(composite, SWT.CHECK);
      emailPocs.setText("Email POCs of Un-Completed Tasks?");
      emailPocs.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      emailPocs.addListener(SWT.Modify, this);

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      comp.setLayout(new GridLayout());
      actionLabel = new Label(comp, SWT.NONE);
      actionLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

   }

   /**
    * The <code>WizardResourceImportPage</code> implementation of this <code>Listener</code> method handles all
    * events and enablements for controls on this page. Subclasses may extend.
    * 
    * @param event Event
    */
   public void handleEvent(Event event) {
      setPageComplete(determinePageCompletion());
      updateWidgetEnablements();
   }

   private void createSourceGroup(Composite parent) {
      fileSelector = new FileSelector(parent, SWT.NONE, "Import Source (Excel saved as .xml", this);

      if (currentResourceSelection != null) fileSelector.setText(currentResourceSelection.getLocation().toString());
      setPageComplete(determinePageCompletion());
   } /*
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
      return fileSelector.validate(this);
   }

   @Override
   protected boolean validateOptionsGroup() {
      String hrid = hridText.getText();
      if (hrid.length() != 5) {
         setErrorMessage("Enter valid HRID");
         return false;
      }
      Collection<Artifact> arts;
      try {
         arts =
               ArtifactPersistenceManager.getInstance().getArtifactsFromHrid(hrid,
                     BranchPersistenceManager.getInstance().getAtsBranch());
         if (arts.size() != 1) {
            setErrorMessage("Can't retrieve artifact for entered HRID");
            actionLabel.setText("");
            return false;
         }
         Artifact art = arts.iterator().next();
         if (!(art instanceof StateMachineArtifact)) {
            setErrorMessage("Artifact retrieved is not a StateMachineArtifact");
            actionLabel.setText("");
            return false;
         }
         actionLabel.setText(String.format("Import to: \"%s\"\nCurrent state: %s",
               ((StateMachineArtifact) art).getDescriptiveName(), ((StateMachineArtifact) art).getCurrentStateName()));
         actionLabel.getParent().layout();
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return true;
   }

   public boolean finish() {
      File file = fileSelector.getFile();
      System.out.println("Not Implemented Yet");

      try {
         ExcelAtsTaskArtifactExtractor extractor =
               new ExcelAtsTaskArtifactExtractor(hridText.getText(),
                     BranchPersistenceManager.getInstance().getAtsBranch(), emailPocs.getSelection());
         Jobs.startJob(new TaskImportJob(file, hridText.getText(), extractor,
               BranchPersistenceManager.getInstance().getAtsBranch()));
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
         ErrorDialog.openError(getShell(), "ATS Import Error", "An error has occured while importing document.",
               new Status(IStatus.ERROR, "org.eclipse.osee.framework.jdk.core", IStatus.ERROR,
                     "Unknown exception occured in the import", ex));
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
      return true;
   }
}