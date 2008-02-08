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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.osee.framework.ui.skynet.search.ArtifactSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * @author Michael S. Rodgers
 */
public class ArtifactListContentProvider implements IStructuredContentProvider {
   private final Object[] EMPTY_ARR = new Object[0];

   private ArtifactSearchViewPage aPage;
   private AbstractArtifactSearchResult aResult;

   public ArtifactListContentProvider(ArtifactSearchViewPage page) {
      aPage = page;
   }

   public void dispose() {
      // nothing to do
   }

   public Object[] getElements(Object inputElement) {
      SkynetEventManager.getInstance().unRegisterAll(aPage);

      if (inputElement instanceof ArtifactSearchResult) {
         Object[] objs = ((ArtifactSearchResult) inputElement).getElements();

         for (Object obj : objs) {

            if (obj instanceof Match) {
               if (((Match) objs[0]).getElement() instanceof Artifact) {

                  Artifact artifact = (Artifact) ((Match) objs[0]).getElement();
                  SkynetEventManager.getInstance().register(ArtifactModifiedEvent.class, artifact, aPage);
               }
            }
         }
         return objs;
      } else
         return EMPTY_ARR;
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      if (newInput instanceof ArtifactSearchResult) {
         aResult = (ArtifactSearchResult) newInput;
      }
   }

   public void elementsChanged(Object[] updatedElements) {
      TableViewer viewer = getViewer();

      if (aResult.getMatchCount() > 0) {
         for (int i = 0; i < updatedElements.length; i++) {
            if (viewer.testFindItem(updatedElements[i]) != null)
               viewer.update(updatedElements[i], null);
            else
               viewer.add(updatedElements[i]);
         }
      } else
         viewer.remove(updatedElements);
   }

   public TableViewer getViewer() {
      return (TableViewer) aPage.getViewer();
   }

   public void clear() {
      getViewer().refresh();
   }
}
