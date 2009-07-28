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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTreeContentProvider implements ITreeContentProvider, IArtifactSearchContentProvider {

   private final Object[] EMPTY_ARR = new Object[0];

   private AbstractArtifactSearchResult fResult;
   private ArtifactSearchPage fPage;
   private AbstractTreeViewer fTreeViewer;
   @SuppressWarnings("unchecked")
   private Map fChildrenMap;

   ArtifactTreeContentProvider(ArtifactSearchPage page, AbstractTreeViewer viewer) {
      fPage = page;
      fTreeViewer = viewer;
   }

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
      return fPage.getElementLimit().intValue();
   }

   public void dispose() {
      // nothing to do
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      if (newInput instanceof AbstractArtifactSearchResult) {
         initialize((AbstractArtifactSearchResult) newInput);
      }
   }

   @SuppressWarnings("unchecked")
   private synchronized void initialize(AbstractArtifactSearchResult result) {
      fResult = result;
      fChildrenMap = new HashMap();
      boolean showLineMatches = fResult.hasAttributeMatches();

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
               fTreeViewer.add(parent, child);
            }
         } else {
            if (refreshViewer) {
               fTreeViewer.refresh(parent);
            }
            return;
         }
         child = parent;
         parent = getParent(child);
      }
      if (insertChild(fResult, child)) {
         if (refreshViewer) {
            fTreeViewer.add(fResult, child);
         }
      }
   }

   /**
    * returns true if the child already was a child of parent.
    * 
    * @param parent
    * @param child
    * @return Returns <code>true</code> if the child was added
    */
   @SuppressWarnings("unchecked")
   private boolean insertChild(Object parent, Object child) {
      Set children = (Set) fChildrenMap.get(parent);
      if (children == null) {
         children = new HashSet();
         fChildrenMap.put(parent, children);
      }
      return children.add(child);
   }

   @SuppressWarnings("unchecked")
   private boolean hasChild(Object parent, Object child) {
      Set children = (Set) fChildrenMap.get(parent);
      return children != null && children.contains(child);
   }

   private void remove(Object element, boolean refreshViewer) {
      if (hasChildren(element)) {
         if (refreshViewer) fTreeViewer.refresh(element);
      } else {
         if (!hasMatches(element)) {
            fChildrenMap.remove(element);
            Object parent = getParent(element);
            if (parent != null) {
               removeFromSiblings(element, parent);
               remove(parent, refreshViewer);
            } else {
               removeFromSiblings(element, fResult);
               if (refreshViewer) fTreeViewer.refresh();
            }
         } else {
            if (refreshViewer) {
               fTreeViewer.refresh(element);
            }
         }
      }
   }

   private boolean hasMatches(Object element) {
      if (element instanceof AttributeLineElement) {
         AttributeLineElement lineElement = (AttributeLineElement) element;
         return lineElement.getNumberOfMatches(fResult) > 0;
      }
      return fResult.getMatchCount(element) > 0;
   }

   @SuppressWarnings("unchecked")
   private void removeFromSiblings(Object element, Object parent) {
      Set siblings = (Set) fChildrenMap.get(parent);
      if (siblings != null) {
         siblings.remove(element);
      }
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      Set children = (Set) fChildrenMap.get(parentElement);
      if (children == null) return EMPTY_ARR;
      return children.toArray();
   }

   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   public synchronized void elementsChanged(Object[] updatedElements) {
      for (int i = 0; i < updatedElements.length; i++) {
         if (!(updatedElements[i] instanceof AttributeLineElement)) {
            if (fResult.getMatchCount(updatedElements[i]) > 0) {
               insert(updatedElements[i], true);
            } else {
               remove(updatedElements[i], true);
            }
         } else {
            AttributeLineElement lineElement = (AttributeLineElement) updatedElements[i];
            int nMatches = lineElement.getNumberOfMatches(fResult);
            if (nMatches > 0) {
               if (hasChild(lineElement.getParent(), lineElement)) {
                  fTreeViewer.update(new Object[] {lineElement, lineElement.getParent()}, null);
               } else {
                  insert(lineElement, true);
               }
            } else {
               remove(lineElement, true);
            }
         }
      }
   }

   public void clear() {
      initialize(fResult);
      fTreeViewer.refresh();
   }

   public Object getParent(Object element) {
      if (element instanceof AttributeLineElement) {
         return ((AttributeLineElement) element).getParent();
      }
      if (element instanceof AttributeMatch) {
         AttributeMatch match = (AttributeMatch) element;
         return match.getArtifact();
      }
      if (element instanceof Artifact) {
         Artifact resource = (Artifact) element;
         try {
            return resource.getParent();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return null;
   }
}