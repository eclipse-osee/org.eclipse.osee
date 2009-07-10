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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class SimpleCheckFilteredTreeDialog extends MinMaxOSEECheckedFilteredTreeDialog {

   private static PatternFilter patternFilter = new PatternFilter();

   public SimpleCheckFilteredTreeDialog(String title, String message, ITreeContentProvider contentProvider, LabelProvider labelProvider, ViewerSorter viewerSorter, int minSelectionRequired, int maxSelectionRequired) {
      super(title, message, patternFilter, contentProvider, labelProvider, viewerSorter, minSelectionRequired,
            maxSelectionRequired);
   }

}
