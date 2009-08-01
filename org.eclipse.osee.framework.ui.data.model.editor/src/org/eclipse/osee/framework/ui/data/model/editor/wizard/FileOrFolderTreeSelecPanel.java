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
package org.eclipse.osee.framework.ui.data.model.editor.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.osee.framework.ui.data.model.editor.wizard.FileOrFolderSelectPanel.ButtonType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class FileOrFolderTreeSelecPanel extends Composite {

   private FileOrFolderSelectPanel fileSelect;
   private FileOrFolderSelectPanel folderSelect;
   private CheckboxTreeViewer treeViewer;
   private final List<IFileStore> selectedFiles;
   private final Set<String> addedItems;
   private Button addButton;
   private Button removeButton;

   public FileOrFolderTreeSelecPanel(Composite parent, int style) {
      super(parent, style);
      GridLayout layout = new GridLayout();
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      selectedFiles = new ArrayList<IFileStore>();
      addedItems = new HashSet<String>();

      createControl(this);
   }

   private void createSourceSelect(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      fileSelect =
            FileOrFolderSelectPanel.createFileSelectPanel(composite, SWT.NONE, "Select File:  ", SWT.OPEN | SWT.SINGLE,
                  ButtonType.RADIO, new String[] {"*.xml"});

      folderSelect =
            FileOrFolderSelectPanel.createFolderSelectPanel(composite, SWT.NONE, "Select Folder:",
                  SWT.OPEN | SWT.SINGLE, ButtonType.RADIO);
   }

   private void createControl(Composite parent) {
      SashForm sash = new SashForm(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      sash.setLayout(layout);
      sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sash.setOrientation(SWT.VERTICAL);

      createSourceSelect(sash);

      Composite composite = new Composite(sash, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createCheckBoxTree(composite);
      createButtons(composite);

      attachListeners();
      sash.setWeights(new int[] {4, 6});
   }

   private void attachListeners() {
      Listener listener = new Listener() {

         @Override
         public void handleEvent(Event event) {
            if (event.widget instanceof Button) {
               Button button = (Button) event.widget;
               if (button.getText().equals("Select File:  ") && fileSelect.isSelected()) {
                  folderSelect.setSelected(false);
               } else if (button.getText().equals("Select Folder:") && folderSelect.isSelected()) {
                  fileSelect.setSelected(false);
               }

               String resource =
                     fileSelect.isSelected() ? fileSelect.getSelectedResource() : folderSelect.getSelectedResource();
               addButton.setEnabled(Strings.isValid(resource) && !addedItems.contains(resource));
               removeButton.setEnabled(!selectedFiles.isEmpty());
            }
         }
      };
      addButton.setEnabled(false);
      removeButton.setEnabled(false);

      fileSelect.addListener(listener);
      folderSelect.addListener(listener);
   }

   private void createCheckBoxTree(Composite parent) {
      treeViewer = new CheckboxTreeViewer(parent);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.setContentProvider(new FileContentProvider());
      treeViewer.setLabelProvider(new FileLabelProvider());
      treeViewer.setInput(selectedFiles);
   }

   private void createButtons(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      addButton = new Button(composite, SWT.PUSH);
      addButton.setText("Add");
      addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      addButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            String resource = null;
            if (fileSelect.isSelected()) {
               resource = fileSelect.getSelectedResource();
            } else if (folderSelect.isSelected()) {
               resource = folderSelect.getSelectedResource();
            }

            if (Strings.isValid(resource) && !addedItems.contains(resource)) {
               try {
                  IFileStore fileStore = EFS.getStore(new File(resource).toURI());
                  if (fileStore != null) {
                     boolean wasAdded = selectedFiles.add(fileStore);
                     if (wasAdded) {
                        addedItems.add(resource);
                        //                        treeViewer.refresh(fileStore);
                     }
                     //                  if (file.isDirectory()) {
                     //                     System.out.println("Scan for *.xml - Osee Types");
                     //                  } else {
                     //                     System.out.println("Check File");
                     //                  }
                  }
               } catch (Exception ex) {
                  OseeLog.log(ODMEditorActivator.class, Level.SEVERE, ex);
               }
            }
            treeViewer.refresh();
         }
      });

      removeButton = new Button(composite, SWT.PUSH);
      removeButton.setText("Remove");
      removeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      removeButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            for (Object object : selection.toList()) {
               if (object != null) {
                  // Remove Here
               }
            }
         }
      });

      Button selectAllButton = new Button(composite, SWT.PUSH);
      selectAllButton.setText("Select All");
      selectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      selectAllButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            for (TreeItem item : treeViewer.getTree().getItems()) {
               item.setChecked(true);
            }
         }
      });

      Button deselectAllButton = new Button(composite, SWT.PUSH);
      deselectAllButton.setText("Deselect All");
      deselectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      deselectAllButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            for (TreeItem item : treeViewer.getTree().getItems()) {
               item.setChecked(false);
            }
         }
      });
   }

   public List<String> getCheckedItems() {
      List<String> checkedItems = new ArrayList<String>();
      for (Object object : treeViewer.getCheckedElements()) {
         if (object instanceof IFileStore) {
            //               ((IFileStore) object)
         }
      }
      return checkedItems;
   }
   private final static class FileContentProvider implements ITreeContentProvider {
      private static final Object[] EMPTY = new Object[0];

      @Override
      public Object[] getChildren(Object parentElement) {
         if (parentElement instanceof Collection) {
            return ((Collection) parentElement).toArray();
         }
         if (parentElement instanceof IFileStore) {
            IFileStore[] children = null;
            if (children != null) {
               return children;
            }
         }
         return EMPTY;
      }

      public Object getParent(Object element) {
         if (element instanceof IFileStore) {
            return ((IFileStore) element).getParent();
         }
         return null;
      }

      public boolean hasChildren(Object element) {
         return getChildren(element).length > 0;
      }

      public Object[] getElements(Object element) {
         return getChildren(element);
      }

      public void dispose() {
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }

   }

   private static class FileLabelProvider extends LabelProvider {
      private static final Image IMG_FOLDER =
            PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

      private static final Image IMG_FILE =
            PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

      public Image getImage(Object element) {
         if (element instanceof IFileStore) {
            IFileStore curr = (IFileStore) element;
            if (curr.fetchInfo().isDirectory()) {
               return IMG_FOLDER;
            }
            return IMG_FILE;
         }
         return null;
      }

      public String getText(Object element) {
         if (element instanceof IFileStore) {
            return ((IFileStore) element).getName();
         }
         return super.getText(element);
      }
   }
}
