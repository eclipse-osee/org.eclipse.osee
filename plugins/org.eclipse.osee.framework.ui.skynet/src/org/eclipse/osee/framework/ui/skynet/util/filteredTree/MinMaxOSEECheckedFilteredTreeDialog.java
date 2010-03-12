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
package org.eclipse.osee.framework.ui.skynet.util.filteredTree;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class MinMaxOSEECheckedFilteredTreeDialog extends OSEECheckedFilteredTreeDialog {

   private final int maxSelectionRequired;
   private final int minSelectionRequired;

   public MinMaxOSEECheckedFilteredTreeDialog(String dialogTitle, String dialogMessage, PatternFilter patternFilter, IContentProvider contentProvider, IBaseLabelProvider labelProvider, ViewerSorter viewerSorter, int minSelectionRequired, int maxSelectionRequired) {
      super(dialogTitle, dialogMessage, patternFilter, contentProvider, labelProvider, viewerSorter);
      this.minSelectionRequired = minSelectionRequired;
      this.maxSelectionRequired = maxSelectionRequired;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      if (this.minSelectionRequired == 0) {
         okButton.setEnabled(true);
      }
      return c;
   }

   @Override
   protected Result isComplete() {
      int numberSelected = getResult().length;
      if (minSelectionRequired <= numberSelected && maxSelectionRequired >= numberSelected) {
         return Result.TrueResult;
      } else {
         List<String> message = new ArrayList<String>();
         if (numberSelected < minSelectionRequired) {
            message.add(String.format("Must select at least [%s]", minSelectionRequired));
         }
         if (numberSelected > maxSelectionRequired) {
            message.add(String.format("Can't select more than [%s]", maxSelectionRequired));
         }
         return new Result(Collections.toString(" &&", message));
      }
   }
}
