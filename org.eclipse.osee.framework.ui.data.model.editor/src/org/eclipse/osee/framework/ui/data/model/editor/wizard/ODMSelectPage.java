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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ArtifactTypeContentProvider;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ArtifactTypeLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Roberto E. Escobar
 */
public class ODMSelectPage extends WizardPage {

   private CheckboxTreeViewer fViewer;
   private ILabelProvider fLabelProvider;
   private ITreeContentProvider fContentProvider;
   private ISelectionStatusValidator fValidator = null;
   private ViewerComparator fComparator;
   private String fEmptyListMessage = "Nothing Available";
   private IStatus fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
   private List<ViewerFilter> fFilters;
   private Object fInput;
   private boolean fIsEmpty;
   private int fWidth = 60;
   private int fHeight = 18;
   private boolean fContainerMode;
   private Object[] fExpandedElements;
   private List<Object> fInitialSelections;

   public ODMSelectPage(String pageName, String description) {
      super(pageName, pageName, null);
      setDescription(description);
      fContainerMode = false;
      fExpandedElements = null;
      fInitialSelections = new ArrayList<Object>();
      fLabelProvider = new ArtifactTypeLabelProvider();
      fContentProvider = new ArtifactTypeContentProvider();
      fContainerMode = false;
      setPageComplete(false);
   }

   public void setInitialSelections(Object[] items) {
      this.fInitialSelections.clear();
      if (items != null && items.length > 0) {
         this.fInitialSelections.addAll(Arrays.asList(items));
      }
   }

   public void setComparator(ViewerComparator comparator) {
      fComparator = comparator;
   }

   public void addFilter(ViewerFilter filter) {
      if (fFilters == null) {
         fFilters = new ArrayList<ViewerFilter>(4);
      }
      fFilters.add(filter);
   }

   public void setValidator(ISelectionStatusValidator validator) {
      fValidator = validator;
   }

   public void setInput(Object input) {
      fInput = input;
   }

   public void setExpandedElements(Object[] elements) {
      fExpandedElements = elements;
   }

   public void setSize(int width, int height) {
      fWidth = width;
      fHeight = height;
   }

   protected void updateOKStatus() {
      if (!fIsEmpty) {
         if (fValidator != null) {
            fCurrStatus = fValidator.validate(fViewer.getCheckedElements());
            updateStatus(fCurrStatus);
         } else if (!fCurrStatus.isOK()) {
            fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", //$NON-NLS-1$
                  null);
         }
      } else {
         fCurrStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, fEmptyListMessage, null);
      }
      updateStatus(fCurrStatus);
   }

   private void updateStatus(IStatus status) {

   }

   public void create() {
      BusyIndicator.showWhile(null, new Runnable() {
         public void run() {
            fViewer.setCheckedElements(getInitialElementSelections().toArray());
            if (fExpandedElements != null) {
               fViewer.setExpandedElements(fExpandedElements);
            }
            updateOKStatus();
         }
      });
   }

   private List<Object> getInitialElementSelections() {
      return fInitialSelections;
   }

   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Label messageLabel = new Label(composite, SWT.NONE);
      if (messageLabel != null) {
         messageLabel.setText("Hello");
      }
      messageLabel.setFont(composite.getFont());

      CheckboxTreeViewer treeViewer = createTreeViewer(composite);
      Control buttonComposite = createSelectionButtons(composite);
      GridData data = new GridData(GridData.FILL_BOTH);
      data.widthHint = convertWidthInCharsToPixels(fWidth);
      data.heightHint = convertHeightInCharsToPixels(fHeight);
      Tree treeWidget = treeViewer.getTree();
      treeWidget.setLayoutData(data);
      treeWidget.setFont(parent.getFont());
      if (fIsEmpty) {
         messageLabel.setEnabled(false);
         treeWidget.setEnabled(false);
         buttonComposite.setEnabled(false);
      }
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            setPageComplete(getSelected().length > 0);
         }
      });

      setControl(composite);
   }

   protected CheckboxTreeViewer createTreeViewer(Composite parent) {
      if (fContainerMode) {
         fViewer = new ContainerCheckedTreeViewer(parent, SWT.BORDER);
      } else {
         fViewer = new CheckboxTreeViewer(parent, SWT.BORDER);
      }

      fViewer.setContentProvider(fContentProvider);
      fViewer.setLabelProvider(fLabelProvider);
      fViewer.addCheckStateListener(new ICheckStateListener() {
         public void checkStateChanged(CheckStateChangedEvent event) {
            updateOKStatus();
         }
      });
      fViewer.setComparator(fComparator);
      if (fFilters != null) {
         for (int i = 0; i != fFilters.size(); i++) {
            fViewer.addFilter((ViewerFilter) fFilters.get(i));
         }
      }
      fViewer.setInput(fInput);
      return fViewer;
   }

   protected CheckboxTreeViewer getTreeViewer() {
      return fViewer;
   }

   protected Composite createSelectionButtons(Composite composite) {
      Composite buttonComposite = new Composite(composite, SWT.RIGHT);
      GridLayout layout = new GridLayout();
      layout.numColumns = 2;
      buttonComposite.setLayout(layout);
      buttonComposite.setFont(composite.getFont());
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
      data.grabExcessHorizontalSpace = true;
      composite.setData(data);
      Button selectButton = createButton(buttonComposite, IDialogConstants.SELECT_ALL_ID, "Select All", false);
      SelectionListener listener = new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            Object[] viewerElements = fContentProvider.getElements(fInput);
            if (fContainerMode) {
               fViewer.setCheckedElements(viewerElements);
            } else {
               for (int i = 0; i < viewerElements.length; i++) {
                  fViewer.setSubtreeChecked(viewerElements[i], true);
               }
            }
            updateOKStatus();
         }
      };
      selectButton.addSelectionListener(listener);
      Button deselectButton = createButton(buttonComposite, IDialogConstants.DESELECT_ALL_ID, "De-select All", false);
      listener = new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            fViewer.setCheckedElements(new Object[0]);
            updateOKStatus();
         }
      };
      deselectButton.addSelectionListener(listener);
      return buttonComposite;
   }

   protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
      // increment the number of columns in the button bar
      ((GridLayout) parent.getLayout()).numColumns++;
      Button button = new Button(parent, SWT.PUSH);
      button.setText(label);
      button.setFont(JFaceResources.getDialogFont());
      button.setData(new Integer(id));
      button.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            //            buttonPressed(((Integer) event.widget.getData()).intValue());
         }
      });
      if (defaultButton) {
         Shell shell = parent.getShell();
         if (shell != null) {
            shell.setDefaultButton(button);
         }
      }
      //      buttons.put(new Integer(id), button);
      setButtonLayoutData(button);
      return button;
   }

   private boolean evaluateIfTreeEmpty(Object input) {
      Object[] elements = fContentProvider.getElements(input);
      if (elements.length > 0) {
         if (fFilters != null) {
            for (int i = 0; i < fFilters.size(); i++) {
               ViewerFilter curr = (ViewerFilter) fFilters.get(i);
               elements = curr.filter(fViewer, input, elements);
            }
         }
      }
      return elements.length == 0;
   }

   public ArtifactDataType[] getSelected() {
      Object[] checked = getTreeViewer().getCheckedElements();
      List<ArtifactDataType> selected = new ArrayList<ArtifactDataType>();
      if (checked != null && checked.length > 0) {
         for (Object object : checked) {
            if (object instanceof ArtifactDataType) {
               selected.add((ArtifactDataType) object);
            }
         }
      }
      return selected.toArray(new ArtifactDataType[selected.size()]);
   }

}
