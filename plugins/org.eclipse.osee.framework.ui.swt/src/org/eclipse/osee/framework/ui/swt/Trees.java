/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.swt;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Convenience methods for working with SWT Trees
 * 
 * @see org.eclipse.swt.widgets.Tree
 * @author Robert A. Fisher
 */
public class Trees {

   /**
    * Pack all of the columns on a tree so that the columns fit the displayed data.
    */
   public static void packColumnData(Tree tree) {
      TreeColumn[] columns = tree.getColumns();
      for (TreeColumn column : columns) {
         column.pack();
      }
   }
}
