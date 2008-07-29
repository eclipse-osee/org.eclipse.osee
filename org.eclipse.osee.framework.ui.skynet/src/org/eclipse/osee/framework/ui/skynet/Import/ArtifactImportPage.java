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
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactImportPage extends WizardDataTransferPage {
   public enum ImportMethod {
      EXCEL, GENERAL, WHOLE_WORD, WORD_OUTLINE
   };

   public static final String PAGE_NAME = "osee.define.wizardPage.artifactImportPage";
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactImportPage.class);
   private final Artifact destinationArtifact;
   private List typeList;
   private List branchList;

   private Button chkReuseArtifacts;
   private Button radImportUnderDhRoot;
   private Button radImportUnderNamedRootFolder;
   private Button radImportUnderSelection;
   private Text txtImportUnderFolderName;

   private DirectoryOrFileSelector directoryFileSelector;

   private Button radWordOutlineExtractor;
   private Button radExcelExtractor;
   private Button radWholeWordExtractor;
   private Button radGeneralExtractor;

   private File importResource;

   private boolean built;

   /**
    * Constructor used by the Skynet drag-n-drop
    */
   public ArtifactImportPage(File importResource, Artifact reuseRootArtifact) {
      super(PAGE_NAME);
      Assert.isNotNull(importResource, "importResource can not be null");
      Assert.isNotNull(reuseRootArtifact, "reuseRootArtifact can not be null");

      this.importResource = importResource;
      this.destinationArtifact = reuseRootArtifact;
      this.built = false;
   }

   /**
    * Constructor that is used by the Eclipse framework
    * 
    * @param pageName
    * @param selection
    */
   public ArtifactImportPage(IStructuredSelection selection) {
      super(PAGE_NAME);

      this.importResource = null;
      Artifact selectedArtifact = null;
      if (selection != null && selection.size() == 1) {
         Object firstElement = selection.getFirstElement();
         if (firstElement instanceof IAdaptable) {
            Object resource = ((IAdaptable) firstElement).getAdapter(IResource.class);
            if (resource instanceof IResource) {
               importResource = ((IResource) resource).getLocation().toFile();
            }
         } else if (firstElement instanceof Artifact) {
            selectedArtifact = (Artifact) firstElement;
         }
      }

      this.destinationArtifact = selectedArtifact;
      this.built = false;
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

      built = true;

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
      createImportMethodSelection(parent);
      setPageComplete(determinePageCompletion());
   }

   private void createImportSource(Composite parent) {

      directoryFileSelector = new DirectoryOrFileSelector(parent, SWT.NONE, "Import Source", this);

      if (importResource == null) {
         // Select directory as the default
         directoryFileSelector.setDirectorySelected(true);
      } else {
         directoryFileSelector.setDirectorySelected(!importResource.isFile());
         directoryFileSelector.setText(importResource.getAbsolutePath());
      }

      Group composite = new Group(parent, SWT.NONE);
      GridLayout layout = new GridLayout(2, false);
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      radImportUnderDhRoot = new Button(composite, SWT.RADIO);
      radImportUnderDhRoot.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
      radImportUnderDhRoot.setText("Add to Default Hierarchy Root");
      radImportUnderDhRoot.setToolTipText("All the top level artifacts that are imported " + "will become root level children.");
      radImportUnderDhRoot.addListener(SWT.Selection, this);

      radImportUnderNamedRootFolder = new Button(composite, SWT.RADIO);
      radImportUnderNamedRootFolder.setText("Add to Root Level Folder:");
      radImportUnderNamedRootFolder.setToolTipText("All the top level artifacts that are imported " + "will become children of the named folder at the root level.");
      radImportUnderNamedRootFolder.addListener(SWT.Selection, this);
      txtImportUnderFolderName = new Text(composite, SWT.BORDER);
      txtImportUnderFolderName.setText("Recent Imports");
      txtImportUnderFolderName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      radImportUnderSelection = new Button(composite, SWT.RADIO);
      radImportUnderSelection.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
      radImportUnderSelection.setText("Add to Selected Artifact:" + destinationArtifact);
      radImportUnderSelection.setToolTipText("All the top level artifacts that are imported " + "will become children of the selected artifact.");
      radImportUnderSelection.addListener(SWT.Selection, this);
      radImportUnderSelection.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            if (radImportUnderSelection.getSelection()) {
               Branch branch = destinationArtifact.getBranch();
               branchList.setSelection(new String[] {branch.getBranchName()});
               branchList.showSelection();
               populateTypeList(branch);
            }
         }
      });

      chkReuseArtifacts = new Button(composite, SWT.CHECK);
      chkReuseArtifacts.addListener(SWT.Selection, this);
      chkReuseArtifacts.setText("Re-use Artifacts");
      chkReuseArtifacts.setEnabled(true);
      chkReuseArtifacts.setToolTipText("All imported artifacts will be checked against the root\n" + "import artifact and the content will be placed on the artifact\n" + "that has the same identifying attributes and level from the root");

      chkReuseArtifacts.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));

      // Setup initial selections
      chkReuseArtifacts.setSelection(false);
      if (destinationArtifact == null) {
         radImportUnderDhRoot.setSelection(true);
      } else {
         radImportUnderSelection.setSelection(true);
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
      return directoryFileSelector.validate(this);
   }

   protected void createOptionsGroup(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Options");
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setLayout(new GridLayout(2, false));

      Label label = new Label(composite, SWT.NONE);
      label.setText("Branch:");
      label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

      label = new Label(composite, SWT.NONE);
      label.setText("Artifact Type:");
      label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

      branchList = new List(composite, SWT.BORDER | SWT.V_SCROLL);
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 300;
      branchList.setLayoutData(gridData);

      typeList = new List(composite, SWT.BORDER | SWT.V_SCROLL);
      gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 300;
      typeList.setLayoutData(gridData);

      Branch defaultBranch;
      if (destinationArtifact == null) {
         defaultBranch = BranchPersistenceManager.getDefaultBranch();
      } else {
         defaultBranch = destinationArtifact.getBranch();
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

      populateTypeList(defaultBranch);
      // Start out with an item selected
      typeList.setSelection(0);

      branchList.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            String itemName = branchList.getItem(branchList.getSelectionIndex());
            populateTypeList((Branch) branchList.getData(itemName));
         }
      });
   }

   private void populateTypeList(Branch branch) {
      try {
         String[] selection = typeList.getSelection();
         typeList.removeAll();
         for (ArtifactType descriptor : ConfigurationPersistenceManager.getValidArtifactTypes(branch)) {
            typeList.add(descriptor.getName());
            typeList.setData(descriptor.getName(), descriptor);
         }
         typeList.setSelection(selection);
         // If the item wasn't available in the new list
         if (typeList.getSelectionCount() == 0) {
            typeList.select(0);
         }
         typeList.showSelection();
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
         typeList.add("error loading artifact types");
      }
   }

   private void createImportMethodSelection(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Import Method");
      composite.setToolTipText("Select the method to be used for importing the selected file or directory");
      GridLayout layout = new GridLayout(2, true);
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      radWordOutlineExtractor = new Button(composite, SWT.RADIO);
      radWordOutlineExtractor.setText("Word Outline");
      radWordOutlineExtractor.setToolTipText(WordOutlineExtractor.getDescription());
      radWordOutlineExtractor.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
      radWordOutlineExtractor.addListener(SWT.Selection, this);

      radWholeWordExtractor = new Button(composite, SWT.RADIO);
      radWholeWordExtractor.setText("Whole Word Document");
      radWholeWordExtractor.setToolTipText(WholeWordDocumentExtractor.getDescription());
      radWholeWordExtractor.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
      radWholeWordExtractor.addListener(SWT.Selection, this);

      radExcelExtractor = new Button(composite, SWT.RADIO);
      radExcelExtractor.setText("Excel XML Artifacts");
      radExcelExtractor.setToolTipText(ExcelArtifactExtractor.getDescription());
      radExcelExtractor.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
      radExcelExtractor.addListener(SWT.Selection, this);

      radGeneralExtractor = new Button(composite, SWT.RADIO);
      radGeneralExtractor.setText("General Documents (All Formats)");
      radGeneralExtractor.setToolTipText(NativeDocumentExtractor.getDescription());
      radGeneralExtractor.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
      radGeneralExtractor.addListener(SWT.Selection, this);

      radWordOutlineExtractor.setSelection(true);
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
      return directoryFileSelector.getFile();
   }

   public boolean isExcelExtractor() {
      return radExcelExtractor.getSelection();
   }

   public boolean isWholeWordExtractor() {
      return radWholeWordExtractor.getSelection();
   }

   public boolean isGeneralDocumentExtractor() {
      return radGeneralExtractor.getSelection();
   }

   public boolean isWordOutlineExtractor() {
      return radWordOutlineExtractor.getSelection();
   }

   public ArtifactType getSelectedType() {
      String itemName = typeList.getItem(typeList.getSelectionIndex());
      return (ArtifactType) typeList.getData(itemName);
   }

   public Branch getSelectedBranch() {
      if (chkReuseArtifacts.getSelection()) {
         return destinationArtifact.getBranch();
      } else {
         String itemName = branchList.getItem(branchList.getSelectionIndex());
         return (Branch) branchList.getData(itemName);
      }
   }

   public Artifact getImportRoot() throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist {
      Artifact importRoot;

      if (radImportUnderDhRoot.getSelection()) {
         importRoot = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(getSelectedBranch());
      } else if (radImportUnderNamedRootFolder.getSelection()) {
         importRoot =
               ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(getSelectedBranch()).getChild(
                     txtImportUnderFolderName.getText());
      } else if (radImportUnderSelection.getSelection()) {
         importRoot = destinationArtifact;
      } else {
         throw new IllegalStateException("One of the 3 radio buttons should be selected.");
      }

      return importRoot;
   }

   public Artifact getReuseArtifactRoot() {
      if (chkReuseArtifacts.getSelection()) {
         return destinationArtifact;
      }
      return null;
   }

   /*
    * The page may not be init'd here
    */
   @Override
   protected void updateWidgetEnablements() {
      super.updateWidgetEnablements();

      if (built) {
         branchList.setEnabled(!radImportUnderSelection.getSelection());

         txtImportUnderFolderName.setEnabled(false);// TODO future development
         radImportUnderSelection.setEnabled(destinationArtifact != null);

         typeList.setEnabled(radGeneralExtractor.getSelection() || radWordOutlineExtractor.getSelection() || radWholeWordExtractor.getSelection());
      }

   }
}