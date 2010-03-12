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
package org.eclipse.osee.framework.ui.skynet;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.database.init.SkynetTypesEnumGenerator;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class GenerateTypeEnumAction implements IActionDelegate {

   public GenerateTypeEnumAction() {
      super();
   }

   @SuppressWarnings("unchecked")
   public void run(IAction action) {
      try {
         StructuredSelection sel = AWorkspace.getSelection();
         Iterator i = sel.iterator();
         File selection = null;
         while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof IResource) {
               IResource resource = (IResource) obj;
               if (resource != null) {
                  selection = resource.getLocation().toFile();
                  if (selection != null) {

                     Object destinationObj = getFolderToStoreAutoGenFilesIn(resource);
                     if (destinationObj instanceof IContainer) {
                        IContainer destinationFolder = (IContainer) destinationObj;
                        File storeAt = destinationFolder.getLocation().toFile();
                        SkynetTypesEnumGenerator gen = new SkynetTypesEnumGenerator();
                        gen.extractTypesFromSheet(selection, storeAt);
                        gen.finish();

                        destinationFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
                     }
                  }
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private Object getFolderToStoreAutoGenFilesIn(IResource resource) {
      CheckedTreeSelectionDialog resourceDialog =
            new ResourceSelectionTree(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                  new WorkbenchLabelProvider(), new WorkbenchContentProvider());

      resourceDialog.setInput(resource.getWorkspace().getRoot());
      resourceDialog.addFilter(new ViewerFilter() {

         @Override
         public boolean select(Viewer viewer, Object parentElement, Object element) {
            IResource resource = null;
            if (element instanceof IContainer) {
               IContainer container = (IContainer) element;
               String name = container.getName();
               if (name.startsWith(".") == false && name.equals("bin") == false) {
                  resource = container;
               }
            }
            if (resource != null) {
               return true;
            }
            return false;
         }
      });
      resourceDialog.setMessage("Select where Auto-Generated classes should be stored.");
      resourceDialog.setTitle("Select Destination");
      resourceDialog.setEmptyListMessage("No Projects Available");
      IContainer container = resource.getParent();
      resourceDialog.setInitialSelection(container);
      List<Object> expand = new ArrayList<Object>();
      expand.add(container);
      if (container.getParent() != null) {
         expand.add(container.getParent());
      }
      resourceDialog.setExpandedElements(expand.toArray(new Object[expand.size()]));
      int result = resourceDialog.open();
      return result != Window.CANCEL ? resourceDialog.getFirstResult() : null;
   }

   public void selectionChanged(IAction action, ISelection selection) {
   }

   private final class ResourceSelectionTree extends CheckedTreeSelectionDialog {

      public ResourceSelectionTree(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
         super(parent, labelProvider, contentProvider);
      }

      protected Control createDialogArea(Composite parent) {
         Composite composite = new Composite(parent, SWT.NONE);
         GridLayout layout = new GridLayout();
         layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
         layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
         layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
         layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
         composite.setLayout(layout);
         composite.setLayoutData(new GridData(GridData.FILL_BOTH));
         applyDialogFont(composite);

         Label messageLabel = createMessageArea(composite);
         CheckboxTreeViewer treeViewer = createTreeViewer(composite);
         GridData data = new GridData(GridData.FILL_BOTH);
         data.widthHint = convertWidthInCharsToPixels(80);
         data.heightHint = convertHeightInCharsToPixels(16);
         Tree treeWidget = treeViewer.getTree();
         treeViewer.addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
               boolean wasChecked = event.getChecked();
               //               getTreeViewer().setAllChecked(false);
               getTreeViewer().setSubtreeChecked(getTreeViewer().getTree().getItems(), false);
               event.getCheckable().setChecked(event.getElement(), wasChecked);
            }

         });
         treeWidget.setLayoutData(data);
         treeWidget.setFont(parent.getFont());
         messageLabel.setEnabled(true);
         treeWidget.setEnabled(true);
         return composite;
      }
   };
}
