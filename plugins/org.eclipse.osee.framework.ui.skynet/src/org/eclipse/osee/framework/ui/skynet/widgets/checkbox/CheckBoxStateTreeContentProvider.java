/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.checkbox;

import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Donald G. Dunne
 */
public class CheckBoxStateTreeContentProvider implements ITreeContentProvider {

   @Override
   public void dispose() {
      // TODO Auto-generated method stub

   }

   @Override
   public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
      // TODO Auto-generated method stub

   }

   @SuppressWarnings("unchecked")
   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof List<?>) {
         return ((List<CheckBoxStateTreeNode>) inputElement).toArray(
            new CheckBoxStateTreeNode[((List<?>) inputElement).size()]);
      }
      if (inputElement instanceof CheckBoxStateTreeNode) {
         return ((CheckBoxStateTreeNode) inputElement).getChildren().toArray();
      }
      return null;
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      return ((CheckBoxStateTreeNode) parentElement).getChildren().toArray();
   }

   @Override
   public Object getParent(Object element) {
      return ((CheckBoxStateTreeNode) element).getParent();
   }

   @Override
   public boolean hasChildren(Object element) {
      return ((CheckBoxStateTreeNode) element).getChildren().size() > 0;
   }

}
