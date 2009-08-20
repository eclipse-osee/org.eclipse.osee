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
package org.eclipse.osee.framework.ui.skynet.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.ArtifactContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

/**
 * @author Roberto E. Escobar
 * @author Ryan C. Schmitt
 */
public class ArtifactSelectionDialog extends SelectionStatusDialog {

   private final XBranchSelectWidget branchSelect;
   private TreeViewer treeViewer;
   private Object input;
   private int fWidth = 60;
   private int fHeight = 18;
   private boolean fIsEmpty;
   private IStatus currentStatus = new Status(IStatus.OK, SkynetGuiPlugin.PLUGIN_ID, 0, "", null);
   private ISelectionStatusValidator validator;
   private final ITreeContentProvider contentProvider;
   private final ILabelProvider labelProvider;

   public ArtifactSelectionDialog(Shell parent) {
      super(parent);
      branchSelect = new XBranchSelectWidget("");
      contentProvider = new ArtifactContentProvider();
      labelProvider = new ArtifactLabelProvider();
   }

   public void setValidator(ISelectionStatusValidator validator) {
      this.validator = validator;
   }

   public void setBranch(Branch branch) {
      branchSelect.setBranch(branch);
   }

   public Branch getBranch() {
      return branchSelect.getData();
   }

   @Override
   public int open() {
      fIsEmpty = evaluateIfTreeEmpty(input);
      super.open();
      return getReturnCode();
   }

   @Override
   protected void computeResult() {
      setResult(Arrays.asList(getTreeSelectedItems()));
   }

   @Override
   public Artifact getFirstResult() {
      return (Artifact) super.getFirstResult();
   }

   private void updateOKStatus() {
      if (!fIsEmpty) {
         if (validator != null) {
            currentStatus = validator.validate(getTreeSelectedItems());
            updateStatus(currentStatus);
         } else if (!currentStatus.isOK()) {
            currentStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", null);
         }
      } else {
         currentStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, "No entries available", null);
      }
      updateStatus(currentStatus);
   }

   public void setSize(int width, int height) {
      fWidth = width;
      fHeight = height;
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite composite = (Composite) super.createDialogArea(parent);

      Label messageLabel = createMessageArea(composite);
      createBranchSelectArea(composite);
      treeViewer = createTreeArea(composite);

      GridData data = new GridData(GridData.FILL_BOTH);
      data.widthHint = convertWidthInCharsToPixels(fWidth);
      data.heightHint = convertHeightInCharsToPixels(fHeight);
      Tree treeWidget = treeViewer.getTree();
      treeWidget.setLayoutData(data);
      treeWidget.setFont(parent.getFont());
      if (fIsEmpty) {
         messageLabel.setEnabled(false);
         treeWidget.setEnabled(false);
         //         buttonComposite.setEnabled(false);
      }

      return composite;
   }

   private void access$superCreate() {
      super.create();
   }

   @Override
   public void create() {
      BusyIndicator.showWhile(null, new Runnable() {
         public void run() {
            access$superCreate();
            treeViewer.setSelection(new IStructuredSelection() {
               @Override
               public boolean isEmpty() {
                  return getInitialElementSelections().isEmpty();
               }

               @Override
               public Object getFirstElement() {
                  return getInitialElementSelections().get(0);
               }

               @Override
               public Iterator<?> iterator() {
                  return getInitialElementSelections().iterator();
               }

               @Override
               public int size() {
                  return getInitialElementSelections().size();
               }

               @Override
               public Object[] toArray() {
                  return getInitialElementSelections().toArray();
               }

               @Override
               public List<?> toList() {
                  return getInitialElementSelections();
               }

            }, true);

            /*
             * treeViewer.setCheckedElements(getInitialElementSelections().toArray());
             * if (fExpandedElements != null) {
             * fViewer.setExpandedElements(fExpandedElements);
             * }
             */
            updateOKStatus();
         }
      });
   }

   private void createBranchSelectArea(Composite parent) {
      branchSelect.setDisplayLabel(false);
      branchSelect.createWidgets(parent, 1);
      branchSelect.addListener(new Listener() {
         @Override
         public void handleEvent(Event event) {
            refreshItems();
         }

      });
   }

   private TreeViewer createTreeArea(Composite parent) {
      TreeViewer treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.setContentProvider(contentProvider);
      treeViewer.setLabelProvider(labelProvider);
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {

            updateOKStatus();
         }
      });
      List<Object> data = new ArrayList<Object>(1);
      data.add(input);
      treeViewer.setInput(data);
      return treeViewer;
   }

   private Object[] getTreeSelectedItems() {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      Object[] selected;
      if (selection != null) {
         selected = selection.toArray();
      } else {
         selected = new Object[0];
      }
      return selected;
   }

   private boolean evaluateIfTreeEmpty(Object input) {
      boolean results = true;
      if (contentProvider != null) {
         Object[] elements = contentProvider.getElements(input);
         results = elements.length == 0;
      }
      return results;
   }

   @SuppressWarnings("unchecked")
   private void refreshItems() {
      Branch branch = getBranch();
      if (branch != null) {
         List<Object> data = (List<Object>) treeViewer.getInput();
         data.clear();
         data.add(getBaseArtifact(branch));
         treeViewer.refresh();
      }
   }

   private Artifact getBaseArtifact(Branch branch) {
      Artifact toReturn = null;
      try {
         toReturn = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
      return toReturn;
   }

   public void setInput(Object input) {
      Branch branch = (Branch) input;
      setBranch(branch);
      this.input = getBaseArtifact(branch);
   }
}
