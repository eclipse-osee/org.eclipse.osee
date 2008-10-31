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
package org.eclipse.osee.define.traceability;

import java.io.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan D. Brooks
 */
public class ImportTraceabilityPage extends WizardDataTransferPage {
   public static final String PAGE_NAME = "org.eclipse.osee.define.wizardPage.importTraceabilityPage";

   private DirectoryOrFileSelector directoryFileSelector;
   private BranchSelectComposite branchSelectComposite;

   private IResource currentResourceSelection;

   /**
    * @param name
    * @param selection
    */
   public ImportTraceabilityPage(IStructuredSelection selection) {
      super(PAGE_NAME);

      setTitle("Import traceability into OSEE Define");
      setDescription("Import relations between artifacts");

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
    * The <code>WizardResourceImportPage</code> implementation of this <code>Listener</code> method handles all events
    * and enablements for controls on this page. Subclasses may extend.
    * 
    * @param event Event
    */
   public void handleEvent(Event event) {
      setPageComplete(determinePageCompletion());
   }

   protected void createSourceGroup(Composite parent) {
      directoryFileSelector = new DirectoryOrFileSelector(parent, SWT.NONE, "Import Source", this);

      if (currentResourceSelection == null) {
         // Select directory as the default
         directoryFileSelector.setDirectorySelected(true);
      } else {
         directoryFileSelector.setDirectorySelected(currentResourceSelection.getType() != IResource.FILE);
         directoryFileSelector.setText(currentResourceSelection.getLocation().toString());
      }

      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Destination Branch");
      GridLayout gd = new GridLayout();
      composite.setLayout(gd);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      branchSelectComposite = new BranchSelectComposite(composite, SWT.BORDER, false);

      setPageComplete(determinePageCompletion());
   }

   /*
    * @see WizardPage#becomesVisible
    */
   @Override
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

   public File getImportFile() {
      return directoryFileSelector.getFile();
   }

   public Branch getSelectedBranch() {
      return branchSelectComposite.getSelectedBranch();
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