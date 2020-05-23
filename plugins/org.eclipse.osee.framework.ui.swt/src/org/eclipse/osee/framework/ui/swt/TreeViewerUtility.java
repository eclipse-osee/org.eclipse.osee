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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Provides utility methods for the TreeViewer
 * 
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 */
public class TreeViewerUtility {
   private static final Object dummy = new Object();

   /**
    * Retrieve a listing of all the selected items in preorder sequence.
    * 
    * @param treeViewer The tree to be traversed
    */
   public static <A extends Object> void getPreorderSelection(TreeViewer treeViewer, List<A> selectedTreeItems) {
      Map<A, Object> expansionHash = new HashMap<>();
      Map<A, Object> selectionHash = new HashMap<>();
      populateSelectionHash(treeViewer, selectionHash);
      populateExpansionHash(treeViewer, expansionHash);
      traverseTree(((ITreeContentProvider) treeViewer.getContentProvider()).getChildren(treeViewer.getInput()),
         (ITreeContentProvider) treeViewer.getContentProvider(), selectedTreeItems, selectionHash, expansionHash);
   }

   @SuppressWarnings("unchecked")
   private static <A extends Object> void populateExpansionHash(TreeViewer tree, Map<A, Object> selectionHash) {

      for (Object obj : tree.getExpandedElements()) {
         selectionHash.put((A) obj, dummy);
      }
   }

   @SuppressWarnings("unchecked")
   private static <A extends Object> void populateSelectionHash(TreeViewer tree, Map<A, Object> selectionHash) {
      Iterator<?> iterator = ((IStructuredSelection) tree.getSelection()).iterator();

      while (iterator.hasNext()) {
         selectionHash.put((A) iterator.next(), dummy);
      }
   }

   @SuppressWarnings("unchecked")
   private static <A extends Object> void traverseTree(Object[] items, ITreeContentProvider contentProvider, List<A> selectedTreeItems, Map<A, Object> selectionHash, Map<A, Object> expandedHash) {
      for (Object item : items) {
         if (selectionHash.containsKey(item)) {
            selectedTreeItems.add((A) item);
         }
         if (expandedHash.containsKey(item)) {
            traverseTree(contentProvider.getChildren(item), contentProvider, selectedTreeItems, selectionHash,
               expandedHash);
         }
      }
   }
}
