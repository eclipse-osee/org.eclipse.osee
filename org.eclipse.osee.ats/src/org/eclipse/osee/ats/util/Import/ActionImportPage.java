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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.FileSelector;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Donald G. Dunne
 */
public class ActionImportPage extends WizardDataTransferPage {

   private IResource currentResourceSelection;
   private FileSelector fileSelector;
   private Label actionLabel;
   private Button emailPocs;

   /**
    * @param pageName
    * @param selection
    */
   public ActionImportPage(String pageName, IStructuredSelection selection) {
      super(pageName);
      setTitle("Import Actions into ATS");
      setDescription("Import Actions into ATS");

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

      emailPocs = new Button(composite, SWT.CHECK);
      emailPocs.setText("Email POCs?");
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

   public boolean finish() {
      final File file = fileSelector.getFile();
      try {
         AbstractSkynetTxTemplate txWrapper = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {

            @Override
            protected void handleTxWork()throws OseeCoreException, SQLException{
               ExcelAtsActionArtifactExtractor extractor =
                     new ExcelAtsActionArtifactExtractor(BranchPersistenceManager.getAtsBranch(),
                           emailPocs.getSelection());
               extractor.discoverArtifactAndRelationData(file);
               if (extractor.dataIsValid()) extractor.createArtifactsAndNotify();
               WorldView.loadIt("Imported Action Artifacts", extractor.getActionArts());
               AWorkbench.popup("Complete", "Action Import Complete");
            }
         };
         txWrapper.execute();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
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