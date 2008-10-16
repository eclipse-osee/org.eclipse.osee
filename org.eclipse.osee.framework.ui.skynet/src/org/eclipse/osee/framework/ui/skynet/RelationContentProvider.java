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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;

/**
 * The basis for the comments in this class can be found at
 * http://www.eclipse.org/articles/treeviewer-cg/TreeViewerArticle.htm
 * 
 * @author Ryan D. Brooks
 */
public class RelationContentProvider implements ITreeContentProvider {
   private static Object[] EMPTY_ARRAY = new Object[0];
   private Artifact artifact;

   /*
    * @see IContentProvider#dispose()
    */
   public void dispose() {
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
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.artifact = (Artifact) newInput;
   }

   /**
    * The tree viewer calls its content provider&#8217;s getChildren method when it needs to create or display the child
    * elements of the domain object, <b>parent </b>. This method should answer an array of domain objects that represent
    * the unfiltered children of <b>parent </b>
    * 
    * @see ITreeContentProvider#getChildren(Object)
    */
   public Object[] getChildren(Object parentElement) {
      try {
         if (parentElement instanceof Artifact) {
            Artifact artifact = (Artifact) parentElement;
            List<RelationType> relationTypes =
                  RelationTypeManager.getValidTypes(artifact.getArtifactType(), artifact.getBranch());
            return relationTypes.toArray();
         } else if (parentElement instanceof RelationType) {
            RelationType relationType = (RelationType) parentElement;
            int sideAMax =
                  RelationTypeManager.getRelationSideMax(relationType, artifact.getArtifactType(), RelationSide.SIDE_A);
            int sideBMax =
                  RelationTypeManager.getRelationSideMax(relationType, artifact.getArtifactType(), RelationSide.SIDE_B);
            RelationTypeSide sideA = new RelationTypeSide(relationType, RelationSide.SIDE_A, artifact);
            RelationTypeSide sideB = new RelationTypeSide(relationType, RelationSide.SIDE_B, artifact);
            boolean onSideA = sideBMax > 0;
            boolean onSideB = sideAMax > 0;
            if (onSideA && onSideB) {
               return new Object[] {sideA, sideB};
            } else if (onSideA) {
               return new Object[] {sideA};
            } else if (onSideB) {
               return new Object[] {sideB};
            }
         } else if (parentElement instanceof RelationTypeSide) {
            RelationTypeSide relationTypeSide = (RelationTypeSide) parentElement;
            artifact.getRelatedArtifacts(relationTypeSide); // ensure the artifacts are bulk loaded; otherwise the sort will be slower
            List<RelationLink> relations = artifact.getRelations(relationTypeSide);
            if (!relationTypeSide.getRelationType().isOrdered()) {
               Collections.sort(relations, new AlphabeticalRelationComparator(relationTypeSide.getSide()));
            }
            return relations.toArray();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return EMPTY_ARRAY;
   }

   /*
    * @see ITreeContentProvider#getParent(Object)
    */
   public Object getParent(Object element) {
      return null;
   }

   /**
    * The tree viewer asks its content provider if the domain object represented by <b>element </b> has any children.
    * This method is used by the tree viewer to determine whether or not a plus or minus should appear on the tree
    * widget.
    * 
    * @see ITreeContentProvider#hasChildren(Object)
    */
   public boolean hasChildren(Object element) {
      if (element instanceof RelationTypeSide) {
         try {
            return artifact.getRelatedArtifactsCount((RelationTypeSide) element) > 0;
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            return false;
         }
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
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }
}