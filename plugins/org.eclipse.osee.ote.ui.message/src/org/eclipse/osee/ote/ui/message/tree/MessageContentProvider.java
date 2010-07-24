/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MessageContentProvider implements ITreeContentProvider {
   private Viewer viewer;
   private RootNode rootInput = null;

   @Override
   public Object[] getChildren(Object parentElement) {
      assert parentElement instanceof AbstractTreeNode;
      return ((AbstractTreeNode) parentElement).getChildren().toArray();
   }

   @Override
   public Object getParent(Object element) {
      assert element instanceof AbstractTreeNode;
      return ((AbstractTreeNode) element).getParent();
   }

   @Override
   public boolean hasChildren(Object element) {
      assert element instanceof AbstractTreeNode;
      return ((AbstractTreeNode) element).hasChildren();
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement == null) {
         return new Object[0];
      }
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // required to implement by IContentProvider
   }

   public void clear() {
      if (rootInput != null) {
         rootInput.removeAll();
         if (viewer != null) {
            viewer.refresh();
         }
      }
   }

   public void refresh() {
      viewer.refresh();
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.viewer = viewer;
      if (oldInput != null) {
         ((RootNode) oldInput).removeAll();
      }
      rootInput = (RootNode) newInput;
   }
}