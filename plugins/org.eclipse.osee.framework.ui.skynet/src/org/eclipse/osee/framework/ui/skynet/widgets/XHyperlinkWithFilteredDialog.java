/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Hyperlink label with value. On selection of hyperlink, a filtered list dialog opens to select one. Does not support
 * multi-select.<br/>
 * <br/>
 * This is the preferred widget for non-text usage because it does not load values until user selects to change. This
 * makes initial display of widget perform better. Use generic OSEE Types extensions where types are involved. Extend
 * for related types (eg: Targeted Version) or other mechanisms for loading values (eg: reading from another location
 * like filesystem> <br/>
 * <br/>
 * Implementing classes use Wfd for With Filtered Dialog
 *
 * @author Donald G. Dunne
 */
public abstract class XHyperlinkWithFilteredDialog<T> extends XHyperlinkLabelValueSelection {

   public final String NOT_SET = Widgets.NOT_SET;
   protected T selected = null;
   private ILabelProvider labelProvider;
   private Collection<T> selectedItems = new ArrayList<>();

   public XHyperlinkWithFilteredDialog(String label) {
      this(label, new StringLabelProvider());
   }

   public XHyperlinkWithFilteredDialog(String label, ILabelProvider labelProvider) {
      super(label);
      this.labelProvider = labelProvider;
      setUseLabelFont(false);
   }

   @Override
   public String getCurrentValue() {
      if (isMultiSelect()) {
         return selectedItems.isEmpty() ? NOT_SET : Collections.toString("; ", selectedItems);
      }
      return selected == null ? NOT_SET : selected.toString();
   }

   public abstract Collection<T> getSelectable();

   // Override to provide default
   public T getDefaultSelected() {
      return null;
   }

   public T getSelected() {
      return selected;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      GridLayout layout = new GridLayout(2, false);
      layout.marginWidth = 5;
      layout.marginHeight = 2;
      comp.setLayout(layout);
   }

   protected boolean isSelectable() {
      return true;
   }

   /**
    * called if single-select
    */
   protected void handleSelectionPersist(T selected) {
      // for subclass implementation
   }

   /**
    * called if multi-select
    */
   protected void handleSelectedItemsPersist(Collection<T> selectedItems) {
      // for subclass implementation
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean handleSelection() {
      try {
         if (!isSelectable()) {
            return false;
         }

         if (isMultiSelect()) {
            FilteredCheckboxTreeDialog<T> dialog = new FilteredCheckboxTreeDialog<T>("Select " + label,
               "Select " + label, new ArrayTreeContentProvider(), labelProvider, new StringNameComparator());
            dialog.setInput(getSelectable());
            T defaultSelected = getDefaultSelected();
            if (defaultSelected != null) {
               dialog.setInitialSelections(Arrays.asList(defaultSelected));
            }
            if (dialog.open() == Window.OK) {
               selectedItems = dialog.getChecked();
               handleSelectedItemsPersist(selectedItems);
               return true;
            }
         } else {
            FilteredTreeDialog dialog = new FilteredTreeDialog("Select " + label, "Select " + label,
               new ArrayTreeContentProvider(), labelProvider, new StringNameComparator());
            dialog.setInput(getSelectable());
            T defaultSelected = getDefaultSelected();
            if (defaultSelected != null) {
               dialog.setInitialSelections(Arrays.asList(defaultSelected));
            }
            if (dialog.open() == Window.OK) {
               selected = (T) dialog.getSelectedFirst();
               handleSelectionPersist(selected);
               return true;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void setSelected(T selected) {
      this.selected = selected;
      refresh();
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && isEmpty() && getCurrentValue().isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
      }
      if (isRequiredEntry() && getCurrentValue().equals(Widgets.NOT_SET)) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " is Not Set.");
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      if (isMultiSelect()) {
         return selectedItems.isEmpty();
      }
      return selected == null;
   }

   @Override
   public Object getData() {
      if (isMultiSelect()) {
         return selectedItems;
      }
      return selected;
   }

   public Collection<T> getSelectedItems() {
      return selectedItems;
   }

}
