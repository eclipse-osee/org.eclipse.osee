/*******************************************************************************
 * Copyright (c) 2020 Boeing.
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
 * Only opens with internal editor
 *
 * @author Branden W. Phillips
 */
public class RelationOrderCompareHandler extends CompareHandler {

   public RelationOrderCompareHandler(String title, CompareItem leftCompareItem, CompareItem rightCompareItem, CompareItem parentCompareItem) {
      super(title, leftCompareItem, rightCompareItem, parentCompareItem);
   }

   @Override
   public void compare() {
      CompareConfiguration compareConfiguration = new CompareConfiguration();
      compareConfiguration.setLeftEditable(leftCompareItem.isEditable());
      compareConfiguration.setRightEditable(rightCompareItem.isEditable());

      CompareUI.openCompareEditorOnPage(
         new CompareInput(title, compareConfiguration, leftCompareItem, rightCompareItem, parentCompareItem),
         AWorkbench.getActivePage());
   }

}
