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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * The basis for the comments in this class can be found at
 * http://www.eclipse.org/articles/treeviewer-cg/TreeViewerArticle.htm
 *
 * @author Ryan D. Brooks
 */
public class RelationContentProvider implements ITreeContentProvider {
   private static Object[] EMPTY_ARRAY = new Object[0];
   private Artifact artifactRoot;
   private final Map<Object, Object> childToParentMap = new HashMap<>();

   @Override
   public void dispose() {
      // do nothing
   }

   /**
    * Notifies this content provider that the given viewer's input has been switched to a different element.
    * <p>
    * A typical use for this method is registering the content provider as a listener to changes on the new input (using
    * model-specific means), and deregistering the viewer from the old input. In response to these change notifications,
    * the content provider propagates the changes to the viewer.
    * </p>
    *
    * @param viewer the viewer
    * @param oldInput the old input element, or <code>null</code> if the viewer did not previously have an input
    * @param newInput the new input element, or <code>null</code> if the viewer does not have an input
    * @see IContentProvider#inputChanged(Viewer, Object, Object)
    */
   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.artifactRoot = (Artifact) newInput;
   }

   /**
    * The tree viewer calls its content provider&#8217;s getChildren method when it needs to create or display the child
    * elements of the domain object, <b>parent </b>. This method should answer an array of domain objects that represent
    * the unfiltered children of <b>parent </b>
    *
    * @see ITreeContentProvider#getChildren(Object)
    */
   @Override
   public Object[] getChildren(Object parentElement) {
      try {
         if (parentElement instanceof Artifact) {
            Collection<RelationType> relationTypes = artifactRoot.getValidRelationTypes();
            for (RelationType type : relationTypes) {
               childToParentMap.put(type, parentElement);
            }
            Object[] ret = relationTypes.toArray();
            Arrays.sort(ret);
            return ret;
         } else if (parentElement instanceof RelationType) {
            RelationType relationType = (RelationType) parentElement;
            int sideAMax = RelationTypeManager.getRelationSideMax(relationType, artifactRoot.getArtifactType(),
               RelationSide.SIDE_A);
            int sideBMax = RelationTypeManager.getRelationSideMax(relationType, artifactRoot.getArtifactType(),
               RelationSide.SIDE_B);

            RelationTypeSideSorter sideA =
               RelationManager.createTypeSideSorter(artifactRoot, relationType, RelationSide.SIDE_A);
            RelationTypeSideSorter sideB =
               RelationManager.createTypeSideSorter(artifactRoot, relationType, RelationSide.SIDE_B);
            boolean onSideA = sideBMax > 0;
            boolean onSideB = sideAMax > 0;

            childToParentMap.put(sideA, parentElement);
            childToParentMap.put(sideB, parentElement);

            if (onSideA && onSideB) {
               return new Object[] {sideA, sideB};
            } else if (onSideA) {
               return new Object[] {sideA};
            } else if (onSideB) {
               return new Object[] {sideB};
            }
         } else if (parentElement instanceof RelationTypeSideSorter) {
            RelationTypeSideSorter relationSorter = (RelationTypeSideSorter) parentElement;
            List<RelationLink> relations = artifactRoot.getRelations(relationSorter);
            WrapperForRelationLink[] wrapper = new WrapperForRelationLink[relations.size()];
            for (int i = 0; i < relations.size(); i++) {
               RelationLink relation = relations.get(i);
               if (relationSorter.getSide().isSideA()) {
                  wrapper[i] = new WrapperForRelationLink(relation, relationSorter.getRelationType(),
                     relation.getArtifactA(), relation.getArtifactA(), relationSorter.getArtifact());
               } else {
                  wrapper[i] = new WrapperForRelationLink(relation, relationSorter.getRelationType(),
                     relation.getArtifactB(), relationSorter.getArtifact(), relation.getArtifactB());
               }
               childToParentMap.put(wrapper[i], parentElement);
            }
            return wrapper;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      return EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      return childToParentMap.get(element);
   }

   /**
    * The tree viewer asks its content provider if the domain object represented by <b>element </b> has any children.
    * This method is used by the tree viewer to determine whether or not a plus or minus should appear on the tree
    * widget.
    *
    * @see ITreeContentProvider#hasChildren(Object)
    */
   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof RelationTypeSideSorter) {
         try {
            RelationTypeSideSorter sorter = (RelationTypeSideSorter) element;
            return RelationManager.getRelatedArtifactsCount(sorter.getArtifact(), sorter.getRelationType(),
               sorter.getSide()) > 0;
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return false;
         }
      } else if (element instanceof RelationType) {
         return true;
      }

      return getChildren(element).length > 0;
   }

   /**
    * This is the method invoked by calling the <b>setInput </b> method on the tree viewer. In fact, the <b>getElements
    * </b> method is called only in response to the tree viewer's <b>setInput </b> method and should answer with the
    * appropriate domain objects of the inputElement. The <b>getElements </b> and <b>getChildren </b> methods operate in
    * a similar way. Depending on your domain objects, you may have the <b>getElements </b> simply return the result of
    * calling <b>getChildren </b>. The two methods are kept distinct because it provides a clean way to differentiate
    * between the root domain object and all other domain objects.
    *
    * @see IStructuredContentProvider#getElements(Object)
    */
   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }
}