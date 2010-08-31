/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.tree;

import java.util.Collection;
import java.util.HashMap;

public final class RootNode extends AbstractTreeNode {
   private final HashMap<String, MessageNode> childMessages = new HashMap<String, MessageNode>();

   public RootNode(final String name) {
      this(name, 3000);
   }

   public RootNode(final String name, final int defaultChildrenNum) {
      super(name, null);
      setLevel(0);
   }

   @Override
   public boolean canSetValue() {
      return false;
   }

   @Override
   String getLabel() {
      return "root";
   }

   @Override
   public <T> T visit(INodeVisitor<T> visitor) {
      return visitor.rootNode(this);
   }

   public MessageNode getMessageByName(String message) {
      return childMessages.get(message);
   }

   public void addChild(MessageNode node) {
      childMessages.put(node.getMessageClassName(), node);
      node.setParent(this);
   }

   @Override
   public Collection<MessageNode> getChildren() {
      return childMessages.values();
   }

   @Override
   public boolean hasChildren() {
      return !childMessages.isEmpty();
   }

   @Override
   public void removeAll() {
      for (AbstractTreeNode child : childMessages.values()) {
         child.dispose();
      }
      childMessages.clear();

   }

   @Override
   public void deleteChildren(Collection<AbstractTreeNode> children) {
      for (AbstractTreeNode child : children) {
         childMessages.remove(((MessageNode) child).getMessageClassName());
         child.dispose();
      }
   }

   public static void main(String[] args) {
      RootNode root = new RootNode("test root");
      String msgName1 = "osee.test.a.msg1";
      String msgName2 = "osee.test.a.msg2";
      String msgName3 = "osee.test.b.msg3";
      MessageNode msg1 = new MessageNode(msgName1);
      MessageNode msg2 = new MessageNode(msgName2);
      MessageNode msg3 = new MessageNode(msgName3);
      root.addChild(msg1);
      root.addChild(msg2);
      root.addChild(msg3);
      assert root.getMessageByName(msgName1) != null;
      assert root.getMessageByName(msgName2) != null;
      assert root.getMessageByName(msgName3) != null;
   }
}
