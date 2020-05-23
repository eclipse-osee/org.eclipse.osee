/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class XFilteredTree extends GenericXWidget {

   private FilteredTree filteredTree;
   private Composite parent;
   private Composite composite;
   private int requiredMinSelected = 0;
   private int requiredMaxSelected = 0;
   private PatternFilter patternFilter = null;

   public XFilteredTree(String displayLabel) {
      super(displayLabel);
      patternFilter = new PatternFilter();
   }

   @Override
   public Control getControl() {
      return filteredTree;
   }

   /**
    * Create List Widgets. Widgets Created: List: horizonatalSpan takes up 2 columns; horizontalSpan must be >=2
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      this.parent = parent;
      composite = null;

      if (!verticalLabel && horizontalSpan < 2) {
         horizontalSpan = 2;
      } else if (verticalLabel) {
         horizontalSpan = 1;
      }

      if (isDisplayLabel() && verticalLabel) {
         composite = new Composite(parent, SWT.NONE);
         int numColumns = 1;
         GridLayout gridLayout = new GridLayout();
         gridLayout.numColumns = numColumns;
         composite.setLayout(gridLayout);
         GridData gd = new GridData(
            GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
         gd.horizontalSpan = horizontalSpan;
         composite.setLayoutData(gd);
      } else {
         composite = parent;
      }

      // Create List Widgets
      if (isDisplayLabel()) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      filteredTree = new FilteredTree(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL, patternFilter, true);
      filteredTree.getViewer().setContentProvider(new ArrayTreeContentProvider());
      GridData gridData5 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
      gridData5.verticalSpan = 10;
      if (isFillHorizontally()) {
         gridData5.grabExcessHorizontalSpace = true;
      }
      gridData5.heightHint = 500;
      gridData5.grabExcessVerticalSpace = true;
      filteredTree.setLayoutData(gridData5);
      filteredTree.setEnabled(isEditable());

   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      filteredTree.dispose();
      if (composite != parent) {
         composite.dispose();
      }
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   @Override
   public IStatus isValid() {
      IStatus valid = Status.OK_STATUS;
      if (isRequiredEntry()) {
         int numSelected = ((StructuredSelection) filteredTree.getViewer().getSelection()).size();
         if (requiredMaxSelected != 0) {
            if (numSelected < requiredMinSelected) {
               valid = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                  getLabel() + " must have at least " + requiredMinSelected + " selected.");
            } else if (numSelected > requiredMaxSelected) {
               valid = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                  getLabel() + " should only have " + requiredMaxSelected + " selected.");
            } else {
               valid = new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel());
            }
         }
         if (numSelected == 0) {
            valid = new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
         }
      }
      return valid;
   }

   @Override
   public boolean isEmpty() {
      return filteredTree.getViewer().getSelection().isEmpty();
   }

   /**
    * Minimum number of selected items that makes this widget valid
    */
   public void setRequiredSelected(int minSelected, int maxSelected) {
      requiredMinSelected = minSelected;
      requiredMaxSelected = maxSelected;
      setRequiredEntry(true);
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      if (!requiredEntry) {
         requiredMinSelected = 1;
         requiredMaxSelected = 1;
      }
   }

   public PatternFilter getPatternFilter() {
      return patternFilter;
   }

   public void setPatternFilter(PatternFilter patternFilter) {
      this.patternFilter = patternFilter;
   }

   public void setInput(Object input) {
      filteredTree.getViewer().setInput(input);
   }

   public Collection<Object> getSelected() {
      List<Object> results = new ArrayList<>();
      Iterator<?> i = ((IStructuredSelection) filteredTree.getViewer().getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         results.add(obj);
      }
      return results;
   }

   public FilteredTree getFilteredTree() {
      return filteredTree;
   }
}