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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class XChangeContentProvider implements ITreeContentProvider {

   private final ChangeXViewer changeXViewer;
   private static Object[] EMPTY_ARRAY = new Object[0];

   public XChangeContentProvider(ChangeXViewer commitXViewer) {
      super();
      this.changeXViewer = commitXViewer;
   }

   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection<?>) parentElement).toArray();
      }
      return EMPTY_ARRAY;
   }

   public Object getParent(Object element) {
      return null;
   }

   public boolean hasChildren(Object element) {
      if (element instanceof Collection) return true;
      return false;
   }

   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   /**
    * @return the changeXViewer
    */
   public ChangeXViewer getChangeXViewer() {
      return changeXViewer;
   }

}
