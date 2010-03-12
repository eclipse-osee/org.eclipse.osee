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
package org.eclipse.osee.framework.ui.plugin.util;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

/**
 * @author Robert A. Fisher
 */
public class ObjectList<A> extends List {
   private java.util.List<A> items;

   public ObjectList(Composite parent, int style) {
      super(parent, style);
      items = new ArrayList<A>();
   }

   @Override
   @Deprecated
   public void add(String string, int index) {
      throw new UnsupportedOperationException();
   }

   @Override
   @Deprecated
   public void add(String string) {
      throw new UnsupportedOperationException();
   }

   public void add(A item, String description) {
      items.add(item);
      super.add(description);
   }

   public void add(A item, String description, int index) {
      items.add(index, item);
      super.add(description, index);
   }

   public A getItemAt(int index) {
      return items.get(index);
   }

   public A getSelectedItem() {
      if (this.getSelectionCount() > 1) throw new IllegalStateException("There is more than one item selected");

      int index = this.getSelectionIndex();
      if (index < 0)
         return null;
      else
         return getItemAt(this.getSelectionIndex());
   }

   public Collection<A> getSelectedItems() {
      int[] selected = super.getSelectionIndices();
      ArrayList<A> selectedItems = new ArrayList<A>(selected.length);
      for (int i : selected)
         selectedItems.add(items.get(i));

      return selectedItems;
   }

   @Override
   public void remove(int start, int end) {
      for (int i = 0; i <= (end - start); i++)
         items.remove(start);
      super.remove(start, end);
   }

   @Override
   public void remove(int index) {
      items.remove(index);
      super.remove(index);
   }

   @Override
   @Deprecated
   public void remove(int[] indices) {
      throw new UnsupportedOperationException();
   }

   @Override
   @Deprecated
   public void remove(String string) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void removeAll() {
      items.clear();
      super.removeAll();
   }

   // Stop SWT from disallowing our simple class extension
   @Override
   protected void checkSubclass() {
   }
}
