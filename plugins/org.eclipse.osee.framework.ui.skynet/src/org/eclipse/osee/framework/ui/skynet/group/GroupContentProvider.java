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

package org.eclipse.osee.framework.ui.skynet.group;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChangeListener;

/**
 * The basis for the comments in this class can be found at
 * http://www.eclipse.org/articles/treeviewer-cg/TreeViewerArticle.htm
 * 
 * @author Donald G. Dunne
 */
public class GroupContentProvider implements ITreeContentProvider, ArtifactChangeListener {
   protected TreeViewer viewer;

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.viewer = (TreeViewer) viewer;
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof GroupExplorerItem) {
         return ((GroupExplorerItem) parentElement).getGroupItems().toArray();
      }
      return new Object[] {};
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof GroupExplorerItem) {
         return ((GroupExplorerItem) element).getParentItem();
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }
}