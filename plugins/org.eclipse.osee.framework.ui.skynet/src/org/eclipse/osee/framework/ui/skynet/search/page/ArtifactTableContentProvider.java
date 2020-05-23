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

package org.eclipse.osee.framework.ui.skynet.search.page;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTableContentProvider implements IStructuredContentProvider, IArtifactSearchContentProvider {

   private final Object[] EMPTY_ARR = new Object[0];

   private final ArtifactSearchPage fPage;
   private AbstractArtifactSearchResult fResult;

   public ArtifactTableContentProvider(ArtifactSearchPage page) {
      fPage = page;
   }

   @Override
   public void dispose() {
      // nothing to do
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof AbstractArtifactSearchResult) {
         int elementLimit = getElementLimit();
         Object[] elements = ((AbstractArtifactSearchResult) inputElement).getElements();
         if (elementLimit != -1 && elements.length > elementLimit) {
            Object[] shownElements = new Object[elementLimit];
            System.arraycopy(elements, 0, shownElements, 0, elementLimit);
            return shownElements;
         }
         return elements;
      }
      return EMPTY_ARR;
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      if (newInput instanceof AbstractArtifactSearchResult) {
         fResult = (AbstractArtifactSearchResult) newInput;
      }
   }

   @Override
   public void elementsChanged(Object[] updatedElements) {
      TableViewer viewer = getViewer();
      int elementLimit = getElementLimit();
      boolean tableLimited = elementLimit != -1;
      for (int i = 0; i < updatedElements.length; i++) {
         if (fResult.getMatchCount(updatedElements[i]) > 0) {
            if (viewer.testFindItem(updatedElements[i]) != null) {
               viewer.update(updatedElements[i], null);
            } else {
               if (!tableLimited || viewer.getTable().getItemCount() < elementLimit) {
                  viewer.add(updatedElements[i]);
               }
            }
         } else {
            viewer.remove(updatedElements[i]);
         }
      }
   }

   private int getElementLimit() {
      return fPage.getElementLimit().intValue();
   }

   private TableViewer getViewer() {
      return (TableViewer) fPage.getViewer();
   }

   @Override
   public void clear() {
      getViewer().refresh();
   }
}
