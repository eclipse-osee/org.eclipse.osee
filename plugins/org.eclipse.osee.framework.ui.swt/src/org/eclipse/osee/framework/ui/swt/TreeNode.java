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

package org.eclipse.osee.framework.ui.swt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IAdaptable;

/**
 * @author Robert A. Fisher
 */
public class TreeNode implements ITreeNode, Serializable {
   private static final long serialVersionUID = -4932401485883022954L;
   private ITreeNode parent;
   protected Object[] children;
   private Object backingData;

   /**
    * Constructor for serialization.
    */
   protected TreeNode() {

   }

   /**
    * Create a stand alone node backed by some data.
    */
   public TreeNode(Object backingData) {
      this(null, null, backingData);
   }

   public TreeNode(ITreeNode parent, Object[] children, Object backingData) {
      this.parent = parent;
      this.children = children;
      this.backingData = backingData;
   }

   @Override
   public Object[] getChildren() {
      return children;
   }

   @Override
   public void setChildren(Object[] objChildren) {
      if (objChildren == null) {
         this.children = new Object[0];
      } else {
         Collection<Object> newChildren = new ArrayList<>(objChildren.length);

         for (Object obj : objChildren) {
            newChildren.add(obj instanceof ITreeNode ? obj : getChild(obj));
         }

         this.children = newChildren.toArray();
      }
   }

   @Override
   public Object getBackingData() {
      return backingData;
   }

   @Override
   public ITreeNode getParent() {
      return parent;
   }

   /**
    * Subclasses should override this method so that calls to setChildren(objChildren) will result in a set of children
    * that matches the subclass. The default implementation will setup the children as TreeNode's.
    * 
    * @param backingData The data that should be placed on the child.
    * @return A new node for the backing data.
    */
   protected ITreeNode getChild(Object backingData) {
      return new TreeNode(this, null, backingData);
   }

   /**
    * Recursively fill a node from a content provider runnable. If an exception is thrown from the provider then it will
    * be set as the child of the node.
    */
   public static void fillNode(ITreeNode node, IContentProviderRunnable provider) {
      try {
         node.setChildren(provider.run(node.getBackingData()));

         for (Object child : node.getChildren()) {
            fillNode((ITreeNode) child, provider);
         }

      } catch (Exception e) {
         node.setChildren(new Object[] {e});
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getAdapter(Class<T> type) {
      if (type == null) {
         throw new IllegalArgumentException("adapter can not be null");
      } else if (backingData instanceof IAdaptable) {
         return ((IAdaptable) backingData).getAdapter(type);
      }

      else if (type.isInstance(backingData)) {
         return (T) backingData;
      }

      else if (type.isInstance(this)) {
         return (T) this;
      }
      return null;
   }
}
