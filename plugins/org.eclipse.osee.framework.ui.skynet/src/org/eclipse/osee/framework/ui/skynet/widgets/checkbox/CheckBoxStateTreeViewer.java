/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.checkbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * Tree Viewer with ability to disable items from being checked. Override the Label Provider to provide own images. Run
 * CheckBoxStateTreeViewerExample to see example.
 *
 * @author Donald G. Dunne
 */
public class CheckBoxStateTreeViewer<T> extends TreeViewer implements ICheckBoxStateTreeViewer {

   private final List<ICheckBoxStateTreeListener> listeners = new ArrayList<>();
   private final Map<T, Boolean> checked = new HashMap<T, Boolean>();
   private final Map<T, Boolean> enabled = new HashMap<T, Boolean>();

   public CheckBoxStateTreeViewer(Composite parent, int style) {
      super(parent, style);
      setLabelProvider(new CheckBoxStateTreeLabelProvider(this));
      addSelectionChangedListener(new ISelectionChangedListener() {

         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Object selected = selection.getFirstElement();
            if (isEnabled(selected)) {
               setChecked((T) selected, !isChecked(selected));
               for (ICheckBoxStateTreeListener listener : listeners) {
                  listener.checkStateChanged(selected);
               }
               for (ICheckBoxStateTreeListener listener : listeners) {
                  listener.checkStateNodesChanged();
               }
               refresh(selected);
            }
         }
      });
   }

   @Override
   public boolean isEnabled(Object obj) {
      Boolean enabled = this.enabled.get(obj);
      if (enabled != null) {
         return enabled;
      }
      return true;
   }

   @Override
   public boolean isChecked(Object obj) {
      Boolean checked = this.checked.get(obj);
      if (checked != null) {
         return checked;
      }
      return false;
   }

   public void setChecked(T obj, boolean checked) {
      this.checked.put(obj, checked);
      refresh();
      for (ICheckBoxStateTreeListener listener : listeners) {
         listener.checkStateChanged(obj);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public void setEnabled(Object obj, boolean enabled) {
      this.enabled.put((T) obj, enabled);
   }

   public void addCheckListener(ICheckBoxStateTreeListener listener) {
      listeners.add(listener);
   }

   public void deSelectAll() {
      for (Entry<T, Boolean> entry : this.checked.entrySet()) {
         if (entry.getValue()) {
            setChecked(entry.getKey(), false);
         }
      }
      refresh();
      for (ICheckBoxStateTreeListener listener : listeners) {
         listener.checkStateNodesChanged();
      }
   }

   public void deSelectAll(Collection<T> selection) {
      for (Entry<T, Boolean> entry : this.checked.entrySet()) {
         if (selection.contains(entry.getKey())) {
            entry.setValue(false);
         }
      }
      refresh();
   }

   public List<T> getChecked() {
      List<T> results = new ArrayList<>();
      for (Entry<T, Boolean> entry : this.checked.entrySet()) {
         if (entry.getValue()) {
            results.add(entry.getKey());
         }
      }
      return results;
   }

   public void setChecked(Collection<T> checked) {
      for (Entry<T, Boolean> entry : this.checked.entrySet()) {
         entry.setValue(false);
         if (checked.contains(entry.getKey())) {
            entry.setValue(true);
         }
      }
      refresh();
   }

}
