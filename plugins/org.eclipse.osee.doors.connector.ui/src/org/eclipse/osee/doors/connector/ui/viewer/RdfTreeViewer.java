/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.doors.connector.ui.viewer;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 * @author David W. Miller
 */
public class RdfTreeViewer extends TreeViewer {

   public RdfTreeViewer(RdfExplorer rdfExplorer, Composite parent) {
      super(parent);
      addSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = getSelection();
            if (selection instanceof IStructuredSelection) {
               RdfExplorerItem item = (RdfExplorerItem) ((IStructuredSelection) selection).getFirstElement();
               if (item != null && item.getParentItem() != null) {
                  rdfExplorer.expandItem((IStructuredSelection) selection);
               }
            }
         }
      });
   }

   @Override
   public boolean isExpandable(Object elementOrTreePath) {
      Object element;
      TreePath path = null;
      if (elementOrTreePath instanceof TreePath) {
         path = (TreePath) elementOrTreePath;
         element = path.getLastSegment();
      } else {
         element = elementOrTreePath;
      }
      IContentProvider cp = getContentProvider();
      if (cp instanceof ITreeContentProvider) {
         ITreeContentProvider tcp = (ITreeContentProvider) cp;
         return tcp.hasChildren(element);
      }
      return false;
   }

}
