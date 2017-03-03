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

import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChangeListener;

/**
 * @author Donald G. Dunne
 */
public class RdfContentProvider implements ITreeContentProvider, ArtifactChangeListener {
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
      if (parentElement instanceof RdfExplorerItem) {
         return ((RdfExplorerItem) parentElement).getChildrenItems().toArray();
      }
      if (parentElement instanceof Collection<?>) {
         return ((Collection<?>) parentElement).toArray();
      }
      return new Object[] {};
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof RdfExplorerItem) {
         return ((RdfExplorerItem) element).getParentItem();
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