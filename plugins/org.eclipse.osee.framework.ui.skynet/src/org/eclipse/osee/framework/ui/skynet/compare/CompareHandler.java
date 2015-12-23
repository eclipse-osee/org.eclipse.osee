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
package org.eclipse.osee.framework.ui.skynet.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Jeff C. Phillips
 */
public class CompareHandler {
   private final CompareItem leftCompareItem;
   private final CompareItem rightCompareItem;
   private final CompareItem parentCompareItem;
   private final String title;

   /**
    * The left string is the 'Was' content and the right is the 'Is" content
    */
   public CompareHandler(String left, String right) {
      this(null, new CompareItem("Was", left, System.currentTimeMillis()),
         new CompareItem("Is", right, System.currentTimeMillis()), null);
   }

   public CompareHandler(String title, CompareItem leftCompareItem, CompareItem rightCompareItem, CompareItem parentCompareItem) {
      this.title = title;
      this.leftCompareItem = leftCompareItem;
      this.rightCompareItem = rightCompareItem;
      this.parentCompareItem = parentCompareItem;
   }

   public void compare() {
      CompareConfiguration compareConfiguration = new CompareConfiguration();
      compareConfiguration.setLeftEditable(leftCompareItem.isEditable());
      compareConfiguration.setRightEditable(rightCompareItem.isEditable());

      CompareUI.openCompareEditorOnPage(
         new CompareInput(title, compareConfiguration, leftCompareItem, rightCompareItem, parentCompareItem),
         AWorkbench.getActivePage());
   }
}
