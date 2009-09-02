package org.eclipse.osee.framework.types.bridge.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.types.bridge.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.panels.AbstractItemSelectPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class SelectOseeTypesPanel extends AbstractItemSelectPanel<List<IFile>> {

   protected SelectOseeTypesPanel() {
      super(new WorkbenchLabelProvider(), new ArrayContentProvider());
   }

   @Override
   protected Dialog createSelectDialog(Shell shell, List<IFile> lastSelected) throws OseeCoreException {
      ResourceSelectionDialog dialog =
            new ResourceSelectionDialog(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
      dialog.addFilter(new OseeTypesFilter());
      dialog.setTitle("Select OseeTypes to import");
      dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
      if (lastSelected != null) {
         dialog.setInitialElementSelections(lastSelected);
      }
      return dialog;
   }

   @Override
   protected boolean updateFromDialogResult(Dialog dialog) {
      boolean updateRequired = false;
      if (dialog instanceof CheckedTreeSelectionDialog) {
         Object[] results = ((CheckedTreeSelectionDialog) dialog).getResult();
         if (results != null && results.length > 0) {
            List<IFile> selected = new ArrayList<IFile>();
            for (Object object : results) {
               if (object instanceof IFile) {
                  selected.add((IFile) object);
               }
            }
            if (!selected.isEmpty()) {
               setSelected(selected);
               updateRequired = true;
            }
         }
      }
      return updateRequired;
   }

   private final class ResourceSelectionDialog extends CheckedTreeSelectionDialog {

      public ResourceSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
         super(parent, labelProvider, contentProvider);
      }

      @Override
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
               CheckboxTreeViewer viewer = getTreeViewer();
               TreeItem[] items = viewer.getTree().getItems();
               for (int i = 0; i < items.length; i++) {
                  viewer.setSubtreeChecked(items[i], false);
               }
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

   private final class OseeTypesFilter extends ViewerFilter {

      private boolean processIFile(Object resource) {
         boolean toReturn = false;
         if (resource instanceof IFile) {
            IFile aFile = (IFile) resource;
            String currentExtension = aFile.getFileExtension();
            if (currentExtension.equalsIgnoreCase("osee")) {
               toReturn = true;
            }
         }
         return toReturn;
      }

      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
         if (element instanceof IProject) {
            if (((IProject) element).isOpen()) {
               return true;
            }
         } else if (element instanceof IContainer) {
            IContainer container = (IContainer) element;
            String name = container.getName();
            if (!name.startsWith(".") && !name.equals("osee")) {
               final MutableBoolean mutable = new MutableBoolean(false);
               try {
                  container.accept(new IResourceVisitor() {

                     @Override
                     public boolean visit(IResource resource) throws CoreException {
                        mutable.setValue(processIFile(resource));
                        return mutable.getValue();
                     }
                  }, IResource.DEPTH_INFINITE, true);
               } catch (CoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
               return mutable.getValue();
            }
         } else {
            return processIFile(element);
         }
         return false;
      }
   }
}