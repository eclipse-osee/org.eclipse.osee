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
package org.eclipse.osee.ote.ui.define.importer;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TestRunStorageKey;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.ote.ui.define.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Roberto E. Escobar
 */
public class OutfileImportPage extends WizardDataTransferPage {
   private static final String PAGE_NAME = "Outfile Import";

   private CheckboxTreeViewer treeViewer;
   private final IStructuredSelection startingResourceSelection;
   private final boolean treeSelectionMode;
   private final ITreeContentProvider contentProvider;
   private final ILabelProvider labelProvider;
   private final List<ViewerFilter> filters;
   private final Object treeInput;
   private XBranchSelectWidget branchSelect;

   protected OutfileImportPage(IStructuredSelection selection) {
      super(PAGE_NAME);
      this.treeInput = ResourcesPlugin.getWorkspace().getRoot();
      this.treeSelectionMode = true;
      this.contentProvider = new WorkbenchContentProvider();
      this.labelProvider = new WorkbenchLabelProvider();
      this.filters = new ArrayList<>();
      //      this.filters.add(new FileFilter());
      this.startingResourceSelection = selection;
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   @Override
   public void handleEvent(Event event) {
      updateWidgetEnablements();
   }

   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
      composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      composite.setFont(parent.getFont());

      Group group = new Group(composite, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Select a Branch to import files into");
      group.setToolTipText("Opens the branch selection dialog");
      branchSelect = new XBranchSelectWidget("branch");
      branchSelect.createWidgets(group, SWT.BORDER | SWT.READ_ONLY);

      createFileSelectArea(composite);

      restoreWidgetValues();
      updateWidgetEnablements();
      setErrorMessage(null); // should not initially have error message
      setControl(composite);
   }

   private void createFileSelectArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
      layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
      layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
      layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
      group.setLayout(layout);
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setText("Select Files");
      group.setToolTipText("Select files to import");

      Composite composite = new Composite(group, SWT.NULL);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      composite.setFont(parent.getFont());

      if (treeSelectionMode) {
         treeViewer = new ContainerCheckedTreeViewer(composite, SWT.BORDER);
      } else {
         treeViewer = new CheckboxTreeViewer(composite, SWT.BORDER);
      }

      treeViewer.setContentProvider(contentProvider);
      treeViewer.setLabelProvider(labelProvider);
      treeViewer.addCheckStateListener(new ICheckStateListener() {
         @Override
         public void checkStateChanged(CheckStateChangedEvent event) {
            setPageComplete(determinePageCompletion());
         }
      });
      if (filters != null) {
         for (int i = 0; i != filters.size(); i++) {
            treeViewer.addFilter(filters.get(i));
         }
      }
      treeViewer.setInput(treeInput);

      Tree treeWidget = treeViewer.getTree();
      GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
      data.widthHint = convertWidthInCharsToPixels(80);
      data.heightHint = convertHeightInCharsToPixels(16);
      treeWidget.setLayout(new GridLayout());
      treeWidget.setLayoutData(data);
      treeWidget.setFont(composite.getFont());
      treeWidget.setEnabled(true);

      createSelectionButtons(composite);
   }

   private Button createButton(Composite parent, int id, String label, boolean defaultButton) {
      // increment the number of columns in the button bar
      ((GridLayout) parent.getLayout()).numColumns++;
      Button button = new Button(parent, SWT.PUSH);
      button.setText(label);
      button.setFont(JFaceResources.getDialogFont());
      button.setData(new Integer(id));
      setButtonLayoutData(button);
      return button;
   }

   private Composite createSelectionButtons(Composite composite) {
      Composite buttonComposite = new Composite(composite, SWT.RIGHT);
      GridLayout layout = new GridLayout();
      layout.numColumns = 2;
      buttonComposite.setLayout(layout);
      buttonComposite.setFont(composite.getFont());
      GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
      data.grabExcessHorizontalSpace = true;
      composite.setData(data);
      Button selectButton = createButton(buttonComposite, IDialogConstants.SELECT_ALL_ID, "Select All", false);
      SelectionListener listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Object[] viewerElements = contentProvider.getElements(treeInput);
            if (treeSelectionMode) {
               treeViewer.setCheckedElements(viewerElements);
            } else {
               for (int i = 0; i < viewerElements.length; i++) {
                  treeViewer.setSubtreeChecked(viewerElements[i], true);
               }
            }
            updateWidgetEnablements();
         }
      };
      selectButton.addSelectionListener(listener);
      Button deselectButton = createButton(buttonComposite, IDialogConstants.DESELECT_ALL_ID, "Deselect All", false);
      listener = new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            treeViewer.setCheckedElements(new Object[0]);
            updateWidgetEnablements();
         }
      };
      deselectButton.addSelectionListener(listener);
      return buttonComposite;
   }

   @Override
   protected void restoreWidgetValues() {
      IDialogSettings settings = getDialogSettings();
      if (settings != null) {
         String lastSelected = settings.get(TestRunStorageKey.SELECTED_BRANCH_ID);
         try {
            branchSelect.setSelection(BranchManager.getBranchToken(Long.parseLong(lastSelected)));
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      onStartSelectedResource();
   }

   private void onStartSelectedResource() {
      if (startingResourceSelection != null) {
         if (startingResourceSelection.size() > 0) {
            for (Object firstElement : startingResourceSelection.toArray()) {
               if (firstElement instanceof IAdaptable) {
                  Object resource = ((IAdaptable) firstElement).getAdapter(IResource.class);
                  if (resource != null) {
                     IResource currentResource = (IResource) resource;
                     if (currentResource.getType() == IResource.FILE) {
                        IResource parentResource = currentResource.getParent();
                        if (parentResource != null && parentResource.isAccessible() != false) {
                           treeViewer.expandToLevel(parentResource, IResource.DEPTH_ONE);
                           treeViewer.setChecked(currentResource, true);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   protected boolean validateSourceGroup() {
      boolean toReturn = super.validateSourceGroup();
      URI[] selectedResources = getSelectedResources();
      if (selectedResources.length == 0) {
         setMessage(null);
         setErrorMessage("There are no resources currently selected for import.");
         toReturn &= false;
      }
      return toReturn;
   }

   @Override
   protected boolean validateDestinationGroup() {
      boolean toReturn = super.validateDestinationGroup();
      BranchId branch = branchSelect.getData();
      if (branch == null) {
         setMessage(null);
         setErrorMessage("Please select a working branch. Cannot import into a null branch.");
         toReturn &= false;
      } else {
         try {
            if (BranchManager.isParentSystemRoot(branch)) {
               setMessage(null);
               setErrorMessage("Please select a working branch. Cannot import into a top-level branch.");
               toReturn &= false;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return toReturn;
   }

   @Override
   protected void updateWidgetEnablements() {
      boolean pageComplete = determinePageCompletion();
      setPageComplete(pageComplete);
      if (pageComplete) {
         setMessage(null);
      }
      super.updateWidgetEnablements();
   }

   @Override
   protected void saveWidgetValues() {
      IDialogSettings settings = getDialogSettings();
      if (settings != null) {
         // update source names history
         String[] branchUuids = settings.getArray(TestRunStorageKey.BRANCH_IDS);
         if (branchUuids == null) {
            branchUuids = new String[0];
         }

         BranchId branch = branchSelect.getData();
         try {
            if (branch != null) {
               String lastBranchSelected = branch.getIdString();
               branchUuids = addToHistory(branchUuids, lastBranchSelected);

               settings.put(TestRunStorageKey.BRANCH_IDS, branchUuids);
               settings.put(TestRunStorageKey.SELECTED_BRANCH_ID, lastBranchSelected);
               try {
                  settings.save(this.getClass().getName());
               } catch (IOException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   private URI[] getSelectedResources() {
      Object[] selected = treeViewer.getCheckedElements();
      List<URI> selectedResources = new ArrayList<>();
      for (Object object : selected) {
         if (object instanceof IFile) {
            IFile iFile = (IFile) object;
            selectedResources.add(iFile.getLocationURI());
         }
      }
      return selectedResources.toArray(new URI[selectedResources.size()]);
   }

   public boolean finish() {
      saveWidgetValues();
      return new ImportOutfileUIOperation(branchSelect.getData(), getSelectedResources()).execute();
   }
}
