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
package org.eclipse.osee.framework.ui.skynet.panels;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractItemSelectPanel<T> {

   private T defaultItem;
   private T lastSelectedItem;
   private TableViewer currentItemWidget;
   private final Set<Listener> listeners;
   private final IBaseLabelProvider labelProvider;
   private final IContentProvider contentProvider;

   protected AbstractItemSelectPanel(IBaseLabelProvider labelProvider, IContentProvider contentProvider) {
      listeners = new HashSet<>();
      this.labelProvider = labelProvider;
      this.contentProvider = contentProvider;
   }

   public void addListener(Listener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void removeListener(Listener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }

   private void fireSelectionEvent(Event event) {
      for (Listener listener : listeners) {
         listener.handleEvent(event);
      }
   }

   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      currentItemWidget = new TableViewer(composite, SWT.BORDER | SWT.READ_ONLY);
      currentItemWidget.setLabelProvider(labelProvider);
      currentItemWidget.setContentProvider(contentProvider);
      currentItemWidget.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Button button = new Button(composite, SWT.PUSH);
      button.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
      button.setText("Select");
      button.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               createDialog();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      updateCurrentItemWidget();
   }

   protected void updateCurrentItemWidget() {
      if (currentItemWidget != null && Widgets.isAccessible(currentItemWidget.getControl())) {
         T object = getSelected();
         if (object == null) {
            object = getDefaultItem();
         }
         if (object != null) {
            Object input;
            if (object instanceof Collection<?>) {
               input = object;
            } else {
               input = new Object[] {object};
            }
            currentItemWidget.setInput(input);
            currentItemWidget.getTable().layout();
         } else {
            currentItemWidget.setInput(new Object[] {});
            currentItemWidget.getTable().layout();
         }
      }
   }

   public void setDefaultItem(T defaultItem) {
      this.defaultItem = defaultItem;
      setSelected(defaultItem);
   }

   public T getDefaultItem() {
      return defaultItem;
   }

   public void setSelected(T item) {
      this.lastSelectedItem = item;
   }

   public T getSelected() {
      return lastSelectedItem;
   }

   private final void createDialog() {

      T lastSelected = getSelected();
      if (lastSelected == null) {
         lastSelected = getDefaultItem();
      }
      Shell shell = AWorkbench.getActiveShell();
      Dialog dialog = createSelectDialog(shell, lastSelected);

      int result = dialog.open();
      if (result == Window.OK) {
         if (updateFromDialogResult(dialog)) {
            updateCurrentItemWidget();
            Event event = new Event();
            event.widget = currentItemWidget.getControl();
            fireSelectionEvent(event);
         }
      }
   }

   protected abstract boolean updateFromDialogResult(Dialog dialog);

   protected abstract Dialog createSelectDialog(Shell shell, T lastSelected);
}
