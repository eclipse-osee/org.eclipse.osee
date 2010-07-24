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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class MessageViewLabelProvider extends LabelProvider {

   private final INodeVisitor<String> nodeVisitor = new INodeVisitor<String>() {

      @Override
      public String elementNode(ElementNode node) {
         return node.getElementName();
      }

      @Override
      public String messageNode(MessageNode node) {
         String type = node.getPackageName();
         type = type.substring(type.lastIndexOf('.') + 1);
         return String.format("%s [%s]", node.getName(), type);
      }

      @Override
      public String rootNode(RootNode node) {
         return node.getName();
      }

   };

   @Override
   public Image getImage(Object element) {
      assert element instanceof AbstractTreeNode;
      final AbstractTreeNode node = (AbstractTreeNode) element;
      return node.getImage();
   }

   @Override
   public String getText(Object element) {
      assert element instanceof AbstractTreeNode;
      return ((AbstractTreeNode) element).visit(nodeVisitor);
   }
}
