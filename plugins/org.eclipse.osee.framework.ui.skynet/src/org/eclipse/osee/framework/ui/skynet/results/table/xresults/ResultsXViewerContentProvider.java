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

package org.eclipse.osee.framework.ui.skynet.results.table.xresults;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;

public class ResultsXViewerContentProvider implements ITreeContentProvider {

   protected Collection<IResultsXViewerRow> rootSet = new HashSet<>();
   private static Object[] EMPTY_ARRAY = new Object[0];

   public ResultsXViewerContentProvider() {
      super();
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      if (parentElement instanceof IResultsXViewerRow) {
         Collection<IResultsXViewerRow> children = ((IResultsXViewerRow) parentElement).getChildren();
         return children.toArray(new IResultsXViewerRow[children.size()]);
      }
      return EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof IResultsXViewerRow) {
         return ((IResultsXViewerRow) element).getParent();
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof IResultsXViewerRow) {
         return ((IResultsXViewerRow) element).hasChildren();
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

   /**
    * @return the rootSet
    */
   public Collection<IResultsXViewerRow> getRootSet() {
      return rootSet;
   }

}
