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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChangeListener;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerLinkNode;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * The basis for the comments in this class can be found at
 * http://www.eclipse.org/articles/treeviewer-cg/TreeViewerArticle.htm
 *
 * @author Ryan D. Brooks
 */
public class ArtifactContentProvider implements ITreeContentProvider, ArtifactChangeListener {
   private static Object[] EMPTY_ARRAY = new Object[0];
   private final ArtifactDecorator artifactDecorator;

   public ArtifactContentProvider(ArtifactDecorator artifactDecorator) {
      this.artifactDecorator = artifactDecorator;
   }

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
      // do nothing
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
      if (parentElement instanceof Artifact) {
         Artifact parentItem = (Artifact) parentElement;
         try {
            if (AccessControlManager.hasPermission(parentItem, PermissionEnum.READ)) {
               Collection<Artifact> children = parentItem.getChildren();
               List<RelationLink> relationsAll = parentItem.getRelationsAll(DeletionFlag.EXCLUDE_DELETED);
               List<Object> allChildren = new ArrayList<>();
               allChildren.addAll(children);

               if (isShowRelations()) {
                  addRelations(parentItem, relationsAll, allChildren);
               }
               return allChildren.toArray();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else if (parentElement instanceof ArtifactExplorerLinkNode) {
         List<Artifact> children = new ArrayList<>();
         ArtifactExplorerLinkNode artifactExplorerLinkNode = (ArtifactExplorerLinkNode) parentElement;
         children.addAll(artifactExplorerLinkNode.getOppositeArtifacts());

         return children.toArray();
      } else if (parentElement instanceof Collection) {
         return ((Collection<?>) parentElement).toArray();
      }
      return EMPTY_ARRAY;
   }

   private boolean isShowRelations() {
      return artifactDecorator != null && artifactDecorator.showRelations();
   }

   private void addRelations(Artifact parentItem, List<RelationLink> relationsAll, List<Object> allChildren) {
      Set<ArtifactExplorerLinkNode> relationTypes = new HashSet<>();
      for (RelationLink link : relationsAll) {
         if (!link.isOfType(CoreRelationTypes.DefaultHierarchical_Child)) {
            RelationType relType = RelationTypeManager.getType(link.getRelationType());
            if (link.getArtifactIdA().equals(parentItem)) {
               relationTypes.add(new ArtifactExplorerLinkNode(parentItem, relType, true));
            } else {
               relationTypes.add(new ArtifactExplorerLinkNode(parentItem, relType, false));
            }
         }
      }
      List<ArtifactExplorerLinkNode> sortedRelationTypes = new ArrayList<>(relationTypes);
      java.util.Collections.sort(sortedRelationTypes, new Comparator<ArtifactExplorerLinkNode>() {

         @Override
         public int compare(ArtifactExplorerLinkNode n1, ArtifactExplorerLinkNode n2) {
            if (n1.getArtifact().notEqual(n2.getArtifact())) {
               return n1.getArtifact().compareTo(n2.getArtifact());
            } else if (n1.getRelationType().notEqual(n2.getRelationType())) {
               return n1.getRelationType().compareTo(n2.getRelationType());
            } else {
               int n1Side = n1.isParentIsOnSideA() ? 0 : 1;
               int n2Side = n2.isParentIsOnSideA() ? 0 : 1;

               return n1Side - n2Side;
            }
         }
      });
      allChildren.addAll(sortedRelationTypes);
   }

   /*
    * @see ITreeContentProvider#getParent(Object)
    */
   @Override
   public Object getParent(Object element) {
      if (element instanceof Artifact) {
         try {
            return ((Artifact) element).getParent();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else if (element instanceof ArtifactExplorerLinkNode) {
         return ((ArtifactExplorerLinkNode) element).getArtifact();
      }
      return null;
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
      /*
       * If the item is an artifact, then use it's optimized check. If it is not an artifact, then resort to asking the
       * general children
       */
      if (element instanceof Artifact) {
         Artifact artifact = (Artifact) element;
         try {
            if (AccessControlManager.hasPermission(artifact, PermissionEnum.READ)) {
               if (artifact.isDeleted()) {
                  return false;
               }
               if (artifact.getRelatedArtifactsCount(CoreRelationTypes.DefaultHierarchical_Child) > 0) {
                  return true;
               } else if (isShowRelations()) {
                  List<RelationLink> relationsAll = artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED);
                  for (RelationLink link : relationsAll) {
                     if (!link.getRelationType().getId().equals(
                        CoreRelationTypes.DefaultHierarchical_Child.getGuid())) {
                        return true;
                     }
                  }
               }
            }
            return false;
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            // Assume it has children if an error happens
            return true;
         }
      } else if (element instanceof ArtifactExplorerLinkNode) {
         return true;
      } else {
         return getChildren(element).length > 0;
      }
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
   public Object[] getElements(Object element) {
      return getChildren(element);
   }
}