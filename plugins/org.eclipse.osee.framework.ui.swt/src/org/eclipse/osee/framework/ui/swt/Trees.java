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
    * 
    * @param tree
    */
   public static void packColumnData(Tree tree) {
      TreeColumn[] columns = tree.getColumns();
      for (TreeColumn column : columns) {
         column.pack();
      }
   }
}
