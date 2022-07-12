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
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * Provides a simple filtered list single select dialog that takes and returns given objects where name comes from
 * either toString or given label provider
 *
 * @author Donald G. Dunne
 */
public class FilteredListDialog<T> extends ElementListSelectionDialog {

   private Collection<T> input;

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
      super.setElements(input.toArray(new Object[input.size()]));
   }

   @SuppressWarnings("unchecked")
   public T getSelected() {
      Object[] elements = getResult();
      if (elements != null && elements.length > 0) {
         return (T) elements[0];
      }
      return null;
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      return super.createDialogArea(parent);
   }

   public Collection<T> getInput() {
      return input;
   }

}
