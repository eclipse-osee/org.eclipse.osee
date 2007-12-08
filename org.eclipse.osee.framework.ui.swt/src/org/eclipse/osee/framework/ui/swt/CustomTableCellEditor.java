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
package org.eclipse.osee.framework.ui.swt;

import java.util.BitSet;
import org.eclipse.swt.widgets.TableItem;

public abstract class CustomTableCellEditor<T> {

   protected final BitSet doNotEditList = new BitSet();

   /**
    * Called when the user has typed a value and then presses <I><B>Enter</B></I>.
    * 
    * @param itemIndex the zero relative index of the {@link TableItem} of the cell to be edited
    * @param value the current selection in the cell before it lost focus
    * @param previousValue the value that was in the cell before the edit took place
    * @return the value that will be actually written into the table's cell
    */
   abstract protected T applyValue(final int itemIndex, final T value, final T previousValue);

   /**
    * Called when the cell editor has lost focus.
    * 
    * @param itemIndex the row index of the TableItem's cell that has lost focus
    * @param value the current value of the cell just before it lost focus
    * @param previousValue the value of the cell before editing began
    * @return the value that cekk will be set to when focus is lost
    */
   abstract protected T focusLost(final int itemIndex, final T value, final T previousValue);

   /**
    * Sets the rows of the table that will not be editted by this cell editor
    * 
    * @param list a list of table row indices
    */
   public void setNotEditableList(final int... list) {
      doNotEditList.clear();
      for (int item : list) {
         doNotEditList.set(item);
      }
   }
}
