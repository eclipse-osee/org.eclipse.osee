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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.FileSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan D. Brooks
 */
public class BranchImportPage extends WizardDataTransferPage {
   public enum ImportMethod {
      EXCEL, GENERAL, POWER_POINT, WHOLE_WORD, WORD_OUTLINE, VISIO
   };

   public static final String PAGE_NAME = "osee.define.wizardPage.artifactImportPage";
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchImportPage.class);

   private Button chkIncludeMainLevelBranch;
   private Button chkIncludeDescendantBranches;
   private FileSelector fileSelector;
   private List branchList;

   private File importResource;
   private final Branch destinationBranch;

   /**
    * Constructor used by the Skynet drag-n-drop
    */
   public BranchImportPage(File importResource, Branch destinationBranch) {
      super(PAGE_NAME);
      if (importResource == null) throw new IllegalArgumentException("importResource can not be null");
      if (destinationBranch == null) throw new IllegalArgumentException("destinationBranch can not be null");

      //      Assert.isNotNull(importResource, "importResource");
      //      Assert.isNotNull(reuseRootArtifact, "reuseRootArtifact");

      this.importResource = importResource;
      this.destinationBranch = destinationBranch;
   }

   /**
    * Constructor that is used by the Eclipse framework
    * 
    * @param pageName
    * @param selection
    */
   public BranchImportPage(IStructuredSelection selection) {
      super(PAGE_NAME);

      this.importResource = null;
      Branch selectedBranch = null;
      if (selection != null && selection.size() == 1) {
         Object firstElement = selection.getFirstElement();
         if (firstElement instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) firstElement;
            Object adapter = adaptable.getAdapter(IResource.class);
            if (adapter instanceof IResource) {
               importResource = ((IResource) adapter).getLocation().toFile();
            }
            adapter = adaptable.getAdapter(Branch.class);
            if (adapter instanceof Branch) {
               selectedBranch = (Branch) adapter;
            }
         }
      }

      this.destinationBranch = selectedBranch;
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
      createImportSource(parent);
      setPageComplete(determinePageCompletion());
   }

   private void createImportSource(Composite parent) {

      fileSelector = new FileSelector(parent, SWT.NONE, "Import Source", this);

      Group composite = new Group(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      chkIncludeMainLevelBranch = new Button(composite, SWT.CHECK);
      chkIncludeMainLevelBranch.addListener(SWT.Selection, this);
      chkIncludeMainLevelBranch.setText("Main Level Branch");
      chkIncludeMainLevelBranch.setToolTipText("Import the main level branch from the import file");
      chkIncludeMainLevelBranch.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

      chkIncludeDescendantBranches = new Button(composite, SWT.CHECK);
      chkIncludeDescendantBranches.addListener(SWT.Selection, this);
      chkIncludeDescendantBranches.setText("Descendant Branches");
      chkIncludeDescendantBranches.setToolTipText("Import the desendant branches from the import file");
      chkIncludeDescendantBranches.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false));

      // Setup initial selections
      chkIncludeMainLevelBranch.setSelection(true);
      chkIncludeDescendantBranches.setSelection(true);

      if (importResource != null) {
         fileSelector.setText(importResource.getAbsolutePath());
      }
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
      return fileSelector.validate(this) && (chkIncludeDescendantBranches.getSelection() || chkIncludeMainLevelBranch.getSelection());
   }

   protected void createOptionsGroup(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Destination");
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setLayout(new GridLayout(1, true));

      Label label = new Label(composite, SWT.NONE);
      label.setText("Branch:");
      label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

      branchList = new List(composite, SWT.BORDER | SWT.V_SCROLL);
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 300;
      branchList.setLayoutData(gridData);

      Branch defaultBranch;
      if (destinationBranch == null) {
         defaultBranch = BranchPersistenceManager.getInstance().getDefaultBranch();
      } else {
         defaultBranch = destinationBranch;
      }

      try {
         int defaultBranchIndex = 0;
         for (Branch branch : BranchPersistenceManager.getBranches()) {
            branchList.add(branch.getBranchName());
            branchList.setData(branch.getBranchName(), branch);
            if (branch.equals(defaultBranch)) {
               branchList.select(defaultBranchIndex);
            } else {
               defaultBranchIndex++;
            }
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
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

   public File getImportFile() {
      return fileSelector.getFile();
   }

   /**
    * @return returns the selectedBranch
    */
   public Branch getSelectedBranch() {
      String itemName = branchList.getItem(branchList.getSelectionIndex());
      return (Branch) branchList.getData(itemName);
   }

   /**
    * @return returns the includeMainLevelBranch
    */
   public boolean isIncludeMainLevelBranch() {
      return chkIncludeMainLevelBranch.getSelection();
   }

   /**
    * @return returns the includeDescendantBranches
    */
   public boolean isIncludeDescendantBranches() {
      return chkIncludeDescendantBranches.getSelection();
   }
}