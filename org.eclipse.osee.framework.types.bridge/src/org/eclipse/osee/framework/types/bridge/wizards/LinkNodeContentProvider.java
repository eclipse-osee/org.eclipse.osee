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
package org.eclipse.osee.framework.types.bridge.wizards;

import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Roberto E. Escobar
 */
public class LinkNodeContentProvider implements ITreeContentProvider {

   @Override
   public Object[] getChildren(Object element) {
      if (element instanceof LinkNode) {
         return ((LinkNode) element).getChildren().toArray();
      } else if (element instanceof Collection<?>) {
         return ((Collection<?>) element).toArray();
      }
      return new Object[0];
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof LinkNode) {
         return ((LinkNode) element).getParent();
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof LinkNode) {
         return ((LinkNode) element).hasChildren();
      }
      return false;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }
}
