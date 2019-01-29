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
package org.eclipse.osee.framework.jdk.core.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TreeNode<TreeType> {

   private TreeType myself;
   private TreeNode<TreeType> parent;
   private List<TreeNode<TreeType>> children;

   protected TreeNode(TreeNode<TreeType> parent, TreeType myself) {
      this.parent = parent;
      this.myself = myself;
      this.children = new ArrayList<>();
   }

   public TreeNode(TreeType myself) {
      this(null, myself);
   }

   @SuppressWarnings("null")
   public TreeNode() {
      this(null);
   }

   public TreeNode<TreeType> getParent() {
      return parent;
   }

   public TreeType getSelf() {
      return myself;
   }

   public List<TreeNode<TreeType>> getChildren() {
      return children;
   }

   public TreeNode<TreeType> addChild(TreeType child) {
      TreeNode<TreeType> newchild = new TreeNode<>(this, child);
      this.children.add(newchild);
      return newchild;
   }

   public void addChildren(Collection<TreeType> children) {
      for (TreeType child : children) {
         this.addChild(child);
      }
   }
}
