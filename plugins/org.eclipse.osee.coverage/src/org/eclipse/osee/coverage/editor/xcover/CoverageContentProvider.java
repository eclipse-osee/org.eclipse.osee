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
package org.eclipse.osee.coverage.editor.xcover;

import java.util.Collection;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MergeItemGroup;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.MessageCoverageItem;

public class CoverageContentProvider implements ITreeContentProvider {

   public CoverageContentProvider(CoverageXViewer coverageXViewer) {
      super();
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof CoveragePackageBase) {
         Collection<?> children = ((CoveragePackageBase) parentElement).getChildren();
         return children.toArray(new Object[children.size()]);
      }
      if (parentElement instanceof CoverageUnit) {
         Collection<?> children = ((CoverageUnit) parentElement).getChildren();
         return children.toArray(new Object[children.size()]);
      }
      if (parentElement instanceof MergeItem) {
         Collection<?> children = ((MergeItem) parentElement).getChildren();
         return children.toArray(new Object[children.size()]);
      }
      if (parentElement instanceof MergeItemGroup) {
         Collection<?> children = ((MergeItemGroup) parentElement).getChildren();
         return children.toArray(new Object[children.size()]);
      }
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      return ArrayUtils.EMPTY_OBJECT_ARRAY;
   }

   public Object getParent(Object element) {
      if (element instanceof CoverageUnit) {
         return ((CoverageUnit) element).getParent();
      }
      if (element instanceof CoverageItem) {
         return ((CoverageItem) element).getParent();
      }
      return null;
   }

   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof MessageMergeItem) return new Object[] {inputElement};
      if (inputElement instanceof MessageCoverageItem) return new Object[] {inputElement};
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}
