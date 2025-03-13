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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * Provides a simple filtered list single select dialog that takes and returns given objects where name comes from
 * either toString or given label provider. setClearAllowed(true) to show a "Clear and Close" button.
 *
 * @author Donald G. Dunne
 */
public class FilteredListDialog<T> extends ElementListSelectionDialog {

   private Collection<T> input;
   private boolean clearAllowed = false;
   private String descUrl;

   public FilteredListDialog(String title, String message) {
      this(title, message, new LabelProvider());
   }

   public FilteredListDialog(String title, String message, ILabelProvider provider) {
      super(Displays.getActiveShell(), provider);
      setTitle(title);
      setMessage(message);
   }

   public void setInput(Collection<T> input) {
      this.input = input;
      if (input != null) {
         super.setElements(input.toArray(new Object[input.size()]));
      }
   }

   @SuppressWarnings("unchecked")
   public T getSelected() {
      Object[] elements = getResult();
      if (elements != null && elements.length > 0) {
         return (T) elements[0];
      }
      return null;
   }

   public boolean isClearSelected() {
      if (clearAllowed && getSelected() == null) {
         return true;
      }
      return false;
   }

   public boolean isClearAllowed() {
      return clearAllowed;
   }

   public void setClearAllowed(boolean clearAllowed) {
      this.clearAllowed = clearAllowed;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control control = super.createDialogArea(container);

      if (clearAllowed) {
         Composite composite = new Composite((Composite) control, SWT.None);
         composite.setLayout(new GridLayout());
         composite.setLayoutData(new GridData());

         final Button button = new Button(composite, SWT.PUSH);
         button.setText("Clear and Close");
         button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               setSelection(null);
               close();
            }
         });
      }

      if (Strings.isValid(descUrl)) {
         Composite composite = new Composite((Composite) control, SWT.None);
         composite.setLayout(new GridLayout());
         composite.setLayoutData(new GridData());

         final Button button = new Button(composite, SWT.PUSH);
         button.setText("Show Descriptions");
         button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               Program.launch(descUrl);
               cancelPressed();
            }
         });
      }

      return control;
   }

   public Collection<T> getInput() {
      return input;
   }

   public void setDescUrl(String descUrl) {
      this.descUrl = descUrl;
   }

}
