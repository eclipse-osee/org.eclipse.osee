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

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.LinkManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationLinkGroup;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent;

/**
 * The basis for the comments in this class can be found at
 * http://www.eclipse.org/articles/treeviewer-cg/TreeViewerArticle.htm
 * 
 * @author Ryan D. Brooks
 */
public class RelationContentProvider implements ITreeContentProvider {
   private static Object[] EMPTY_ARRAY = new Object[0];
   protected TreeViewer viewer;
   private SkynetEventManager eventManager;
   private final RelationsComposite relComp;

   //   private static final int EXPAND_CHILDREN_LEVEL = 7;

   public RelationContentProvider(RelationsComposite relComp) {
      this.relComp = relComp;
      this.eventManager = SkynetEventManager.getInstance();
   }

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
      this.viewer = (TreeViewer) viewer;
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
         if (parentElement instanceof IRelationLinkDescriptor) {
            IRelationLinkDescriptor descriptor = (IRelationLinkDescriptor) parentElement;
            Artifact parent = (Artifact) viewer.getInput();
            LinkManager linkManager = parent.getLinkManager();

            Collection<RelationLinkGroup> groups = new LinkedList<RelationLinkGroup>();
            RelationLinkGroup group;

            group = linkManager.getSideAGroup(descriptor);
            if (group != null) {
               groups.add(group);
               for (Artifact art : group.getArtifacts())
                  eventManager.register(RelationModifiedEvent.class, art, relComp);
            }
            group = linkManager.getSideBGroup(descriptor);
            if (group != null) {
               groups.add(group);
               for (Artifact art : group.getArtifacts())
                  eventManager.register(RelationModifiedEvent.class, art, relComp);
            }
            return groups.toArray();
         } else if (parentElement instanceof RelationLinkGroup) {
            return ((RelationLinkGroup) parentElement).getGroupSide().toArray();
         }
      } catch (SQLException ex) {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return EMPTY_ARRAY;
   }

   //   /**
   //    * Expands the parents children if the number of children are less than or equal the maxium
   //    * number allowed.
   //    */
   //   private void expandTreeViewer(TreeViewer treeViewer, Object parent, int numberOfChildren) {
   //      if (numberOfChildren <= EXPAND_CHILDREN_LEVEL) {
   //         treeViewer.expandToLevel(parent, 1);
   //      }
   //   }

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
      // might be inefficient if getChildren is not a lightweight operation
      int numberOfChildren = getChildren(element).length;
      // expandTreeViewer(viewer, element, numberOfChildren);

      return numberOfChildren > 0;
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
      try {
         if (inputElement instanceof Artifact) {
            return ((Artifact) inputElement).getLinkManager().getLinkDescriptors().toArray();
         }
         throw new IllegalArgumentException("Unsupported input type:" + inputElement.getClass().getCanonicalName());
      } catch (SQLException ex) {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return new Object[] {};
   }

}