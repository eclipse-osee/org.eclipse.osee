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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * A cell editor that presents a list of items in a combo box. The cell editor's value is the zero-based index of the
 * selected item.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class Search {

   /**
    * The list of items to present.
    */
   private String[] items;

   /**
    * The multi character search string
    */
   private String searchString = "";

   private int firstIndex, lastIndex;
   private boolean dirty;

   /**
    * Creates a new Search containing the given list of choices.
    * 
    * @param items the list of strings
    */
   public Search(String[] items) {
      this.items = items;
      firstIndex = -1;
      lastIndex = -1;
      dirty = true;
   }

   /**
    * Returns the list of choices for list
    * 
    * @return the list of choices for the list
    */
   public String[] getItems() {
      int range;

      if (lastIndex == -1 || firstIndex == -1)
         return items;
      else
         range = lastIndex - firstIndex;
      String[] temp = new String[range];
      System.arraycopy(items, firstIndex, temp, 0, range);
      return temp;
   }

   /**
    * @return the first index in the subarray
    */
   public int getFirstIndex() {
      return this.firstIndex;
   }

   /**
    * @return the last index in the subarray
    */
   public int getLastIndex() {
      return this.lastIndex;
   }

   /**
    * Performs the ProgressiveSearch on the list and populates the first and last index of the modified array
    * 
    * @param e the key event
    */
   public void progressiveSearch(KeyEvent e) {
      int first = -1;
      int last = -1;

      if (!Character.isISOControl(e.character)) // Looks for valid NON control character 0x20 - 0x7E
         searchString = new String(searchString + e.character);
      else if (e.character == SWT.BS) // Backspace Character
      {
         if (searchString.length() > 1)
            searchString = new String(searchString.substring(0, searchString.length() - 1));
         else if (searchString.length() == 1) // No more characters in search string... reset string;
            this.reset();
         else
            return;
      } else
         // If invalid character.. do nothing
         return;

      // Look for first instance of substring
      for (int i = 0; i < items.length; i++) {
         if (items[i].length() >= searchString.length()) {
            if (searchString.equalsIgnoreCase(items[i].substring(0, searchString.length()))) {
               first = i;
               break;
            }
         }
      }
      // Look for last instance of substring
      if (first != -1) {
         for (int i = first; i < items.length; i++) {
            if (items[i].length() >= searchString.length()) {
               if (!searchString.equalsIgnoreCase(items[i].substring(0, searchString.length()))) {
                  last = i;
                  break;
               }
            }
         }
         if (last == -1) lastIndex = items.length;
      } else
         // If substring is not found.. remove last character
         searchString = new String(searchString.substring(0, searchString.length() - 1));

      if (first != -1 && last != -1) {
         firstIndex = first;
         lastIndex = last;
         dirty = true;
      }
      return;
   }

   /**
    * Resets the search string as well as the first and last indexes
    */
   public void reset() {
      firstIndex = 0;
      lastIndex = items.length;
      searchString = new String("");
      dirty = true;
   }

   public int getLength() {
      return searchString.length();
   }

   public boolean getDirty() {
      return this.dirty;
   }

   public void setDirty(boolean value) {
      this.dirty = value;
   }
}