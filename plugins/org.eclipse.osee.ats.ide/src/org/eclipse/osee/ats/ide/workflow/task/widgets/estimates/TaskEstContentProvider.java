/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.widgets.estimates;

import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class TaskEstContentProvider implements ITreeContentProvider {

   public TaskEstContentProvider() {
      super();
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      return Collections.EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
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
