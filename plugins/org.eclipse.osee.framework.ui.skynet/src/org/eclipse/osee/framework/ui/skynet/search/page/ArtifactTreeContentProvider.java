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
package org.eclipse.osee.framework.ui.skynet.search.page;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTreeContentProvider implements ITreeContentProvider, IArtifactSearchContentProvider {

   private final Object[] EMPTY_ARR = new Object[0];

   private AbstractArtifactSearchResult searchResult;
   private final ArtifactSearchPage searchPage;
   private final AbstractTreeViewer treeViewer;
   @SuppressWarnings("rawtypes")
   private Map<Object, Set> childrenMap;

   private final FakeArtifactParent orphanParent = new FakeArtifactParent("Artifacts with no parent");
   private final FakeArtifactParent multiParent = new FakeArtifactParent("Artifacts with multiple parents");

   ArtifactTreeContentProvider(ArtifactSearchPage page, AbstractTreeViewer viewer) {
      searchPage = page;
      treeViewer = viewer;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      Object[] children = getChildren(inputElement);
      int elementLimit = getElementLimit();
      if (elementLimit != -1 && elementLimit < children.length) {
         Object[] limitedChildren = new Object[elementLimit];
         System.arraycopy(children, 0, limitedChildren, 0, elementLimit);
         return limitedChildren;
      }
      return children;
   }

   private int getElementLimit() {
      return searchPage.getElementLimit().intValue();
   }

   @Override
   public void dispose() {
      // nothing to do
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      if (newInput instanceof AbstractArtifactSearchResult) {
         initialize((AbstractArtifactSearchResult) newInput);
      }
   }

   private synchronized void initialize(AbstractArtifactSearchResult result) {
      searchResult = result;
      childrenMap = new HashMap<>();
      boolean showLineMatches = searchResult.hasAttributeMatches();

      if (result != null) {
         Object[] elements = result.getElements();
         for (int i = 0; i < elements.length; i++) {
            if (showLineMatches) {
               Match[] matches = result.getMatches(elements[i]);
               for (int j = 0; j < matches.length; j++) {
                  Match match = matches[j];
                  if (match instanceof AttributeMatch) {
                     insert(((AttributeMatch) match).getLineElement(), false);
                  } else {
                     insert(match.getElement(), false);
                  }
               }
            } else {
               insert(elements[i], false);
            }
         }
      }
   }

   private void insert(Object child, boolean refreshViewer) {
      Object parent = getParent(child);
      while (parent != null) {
         if (insertChild(parent, child)) {
            if (refreshViewer) {
               treeViewer.add(parent, child);
            }
         } else {
            if (refreshViewer) {
               treeViewer.refresh(parent);
            }
            return;
         }
         child = parent;
         parent = getParent(child);
      }
      if (insertChild(searchResult, child)) {
         if (refreshViewer) {
            treeViewer.add(searchResult, child);
         }
      }
   }

   /**
    * returns true if the child already was a child of parent.
    *
    * @return Returns <code>true</code> if the child was added
    */
   @SuppressWarnings("unchecked")
   private boolean insertChild(Object parent, Object child) {
      Set<Object> children = childrenMap.get(parent);
      if (children == null) {
         children = new HashSet<>();
         childrenMap.put(parent, children);
      }
      return children.add(child);
   }

   private boolean hasChild(Object parent, Object child) {
      Set<?> children = childrenMap.get(parent);
      return children != null && children.contains(child);
   }

   private void remove(Object element, boolean refreshViewer) {
      if (hasChildren(element)) {
         if (refreshViewer) {
            treeViewer.refresh(element);
         }
      } else {
         if (!hasMatches(element)) {
            childrenMap.remove(element);
            Object parent = getParent(element);
            if (parent != null) {
               removeFromSiblings(element, parent);
               remove(parent, refreshViewer);
            } else {
               removeFromSiblings(element, searchResult);
               if (refreshViewer) {
                  treeViewer.refresh();
               }
            }
         } else {
            if (refreshViewer) {
               treeViewer.refresh(element);
            }
         }
      }
   }

   private boolean hasMatches(Object element) {
      if (element instanceof AttributeLineElement) {
         AttributeLineElement lineElement = (AttributeLineElement) element;
         return lineElement.getNumberOfMatches(searchResult) > 0;
      }
      return searchResult.getMatchCount(element) > 0;
   }

   private void removeFromSiblings(Object element, Object parent) {
      Set<?> siblings = childrenMap.get(parent);
      if (siblings != null) {
         siblings.remove(element);
      }
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      Set<?> children = childrenMap.get(parentElement);
      if (children == null) {
         return EMPTY_ARR;
      }
      return children.toArray();
   }

   @Override
   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   @Override
   public synchronized void elementsChanged(Object[] updatedElements) {
      for (int i = 0; i < updatedElements.length; i++) {
         if (!(updatedElements[i] instanceof AttributeLineElement)) {
            if (searchResult.getMatchCount(updatedElements[i]) > 0) {
               insert(updatedElements[i], true);
            } else {
               remove(updatedElements[i], true);
            }
         } else {
            AttributeLineElement lineElement = (AttributeLineElement) updatedElements[i];
            int nMatches = lineElement.getNumberOfMatches(searchResult);
            if (nMatches > 0) {
               if (hasChild(lineElement.getParent(), lineElement)) {
                  treeViewer.update(new Object[] {lineElement, lineElement.getParent()}, null);
               } else {
                  insert(lineElement, true);
               }
            } else {
               remove(lineElement, true);
            }
         }
      }
   }

   @Override
   public void clear() {
      initialize(searchResult);
      treeViewer.refresh();
   }

   @Override
   public Object getParent(Object element) {
      Object toReturn = null;
      if (element instanceof AttributeLineElement) {
         toReturn = ((AttributeLineElement) element).getParent();
      } else if (element instanceof AttributeMatch) {
         AttributeMatch match = (AttributeMatch) element;
         toReturn = match.getArtifact();
      } else if (element instanceof Artifact) {
         Artifact resource = (Artifact) element;
         try {
            toReturn = resource.getParent();
            if (toReturn == null && resource.isNotRootedInDefaultRoot()) {
               toReturn = orphanParent;
            }
         } catch (MultipleArtifactsExist ex) {
            toReturn = multiParent;
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return toReturn;
   }
}