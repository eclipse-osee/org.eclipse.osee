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
package org.eclipse.osee.framework.jdk.core.collection.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TreeNode<treeType> {

   private treeType myself;
   private TreeNode<treeType> parent;
   private List<TreeNode<treeType>> children;

   protected TreeNode(TreeNode<treeType> parent, treeType myself) {
      this.parent = parent;
      this.myself = myself;
      this.children = new ArrayList<TreeNode<treeType>>();
   }

   public TreeNode(treeType myself) {
      this(null, myself);
   }

   public TreeNode() {
      this(null);
   }

   public TreeNode<treeType> getParent() {
      return parent;
   }

   public treeType getSelf() {
      return myself;
   }

   public List<TreeNode<treeType>> getChildren() {
      return children;
   }

   public TreeNode<treeType> addChild(treeType child) {
      TreeNode<treeType> newchild = new TreeNode<treeType>(this, child);
      this.children.add(newchild);
      return newchild;
   }

   public void addChildren(Collection<treeType> children) {
      for (treeType child : children) {
         this.addChild(child);
      }
   }
}
