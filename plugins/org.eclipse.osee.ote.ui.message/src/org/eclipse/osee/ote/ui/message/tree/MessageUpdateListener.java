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

import java.util.LinkedList;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.client.msg.core.AbstractMessageListener;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Ken J. Aguilar
 */
public class MessageUpdateListener extends AbstractMessageListener {

   private final WatchedMessageNode node;
   private final TreeViewer viewer;

   private final class NodeUpdate implements Runnable {

      private final AbstractTreeNode[] nodes;

      NodeUpdate(AbstractTreeNode[] nodes) {
         this.nodes = nodes;
      }

      @Override
      public void run() {
         for (AbstractTreeNode node : nodes) {
            viewer.refresh(node, true);
         }
      }

   };

   public MessageUpdateListener(TreeViewer viewer, WatchedMessageNode node) {
      super(node.getSubscription());
      this.viewer = viewer;
      this.node = node;
   }

   @Override
   public void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
      if (type == getSubscription().getMemType()) {
         node.incrementCounter();
      }
   }

   @Override
   public void subscriptionActivated(IMessageSubscription subscription) {
      update(node);
   }

   @Override
   public void subscriptionInvalidated(IMessageSubscription subscription) {
      String reason = subscription.getMessageClassName() + " does not exist";
      LinkedList<AbstractTreeNode> list = new LinkedList<AbstractTreeNode>();
      list.add(node);
      node.collectDescendants(list);

      for (AbstractTreeNode child : list) {
         child.setEnabled(false);
         child.setDisabledReason(reason);
      }
      update(list.toArray(new AbstractTreeNode[list.size()]));

   }

   @Override
   public void subscriptionNotSupported(IMessageSubscription subscription) {
      update(node);
   }

   @Override
   public void subscriptionResolved(IMessageSubscription subscription) {
      super.subscriptionResolved(subscription);
      node.setResolved(true);
      update(node);
   }

   @Override
   public void subscriptionUnresolved(IMessageSubscription subscription) {
      super.subscriptionUnresolved(subscription);
      node.setResolved(false);
      update(node);
   }

   private void update(AbstractTreeNode[] nodes) {
      Displays.ensureInDisplayThread(new NodeUpdate(nodes));
   }

   private void update(AbstractTreeNode node) {
      Displays.ensureInDisplayThread(new NodeUpdate(new AbstractTreeNode[] {node}));
   }

}
