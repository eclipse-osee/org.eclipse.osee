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

package org.eclipse.osee.ote.ui.define.viewers.data;

import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Roberto E. Escobar
 */
public class DataItemContentProvider implements ITreeContentProvider {

   private static final Object[] EMPTY_ARRAY = new Object[0];

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection<?>) parentElement).toArray();
      }
      if (parentElement instanceof DataItem) {
         return ((DataItem) parentElement).getChildren();
      }
      return EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof DataItem) {
         return ((DataItem) element).getParent();
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof Collection<?>) {
         return true;
      }
      if (element instanceof DataItem) {
         return ((DataItem) element).hasChildren();
      }
      return false;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) {
         return new Object[] {inputElement};
      }
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

}
