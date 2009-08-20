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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChangeListener;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

/**
 * The basis for the comments in this class can be found at
 * http://www.eclipse.org/articles/treeviewer-cg/TreeViewerArticle.htm
 * 
 * @author Ryan D. Brooks
 */
public class ArtifactContentProvider implements ITreeContentProvider, ArtifactChangeListener {
   private static Object[] EMPTY_ARRAY = new Object[0];

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
   }

   /**
    * The tree viewer calls its content provider&#8217;s getChildren method when it needs to create or display the child
    * elements of the domain object, <b>parent </b>. This method should answer an array of domain objects that represent
    * the unfiltered children of <b>parent </b>
    * 
    * @see ITreeContentProvider#getChildren(Object)
    */
   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Artifact) {
         Artifact parentItem = (Artifact) parentElement;
         try {
            if (AccessControlManager.hasPermission(parentItem, PermissionEnum.READ)) {
               Collection<Artifact> children = parentItem.getChildren();
               if (children != null) {
                  return children.toArray();
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      } else if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      return EMPTY_ARRAY;
   }

   /*
    * @see ITreeContentProvider#getParent(Object)
    */
   public Object getParent(Object element) {
      if (element instanceof Artifact) {
         try {
            return ((Artifact) element).getParent();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
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
   public boolean hasChildren(Object element) {
      /*
       * If the item is an artifact, then use it's optimized check. If it is not an artifact, then
       * resort to asking the general children
       */
      if (element instanceof Artifact) {
         Artifact artifact = (Artifact) element;
         try {
            if (AccessControlManager.hasPermission(artifact, PermissionEnum.READ)) {
               if (artifact.isDeleted()) {
                  return false;
               }
               return artifact.getRelatedArtifactsCount(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD) > 0;
            } else {
               return false;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            // Assume it has children if an error happens
            return true;
         }
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
   public Object[] getElements(Object element) {
      return getChildren(element);
   }
}