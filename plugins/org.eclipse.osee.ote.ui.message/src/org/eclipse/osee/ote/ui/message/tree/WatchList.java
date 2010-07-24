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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.ui.message.watch.ElementPath;
import org.eclipse.osee.ote.ui.message.watch.ViewRefresher;
import org.eclipse.osee.ote.ui.message.watch.WatchView;

public class WatchList implements ITreeContentProvider {

   private TreeViewer viewer;
   private RootNode rootNode = null;
   private IOteMessageService service = null;
   private final ViewRefresher viewRefresher;

   public WatchList(WatchView view) {
      viewRefresher = new ViewRefresher(this, view, 200);
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof IOteMessageService) {
         return getChildren(rootNode);
      }
      return ((AbstractTreeNode) parentElement).getChildren().toArray();
   }

   @Override
   public Object getParent(Object element) {
      assert element instanceof AbstractTreeNode;
      return ((AbstractTreeNode) element).getParent();
   }

   @Override
   public boolean hasChildren(Object element) {
      assert element instanceof AbstractTreeNode;
      return ((AbstractTreeNode) element).hasChildren();
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement == null) {
         return new Object[0];
      }
      return getChildren(rootNode);
   }

   @Override
   public void dispose() {
      if (rootNode != null) {
         deleteAll();
      }
      rootNode = null;
      viewRefresher.dispose();
   }

   public void clear() {
      if (rootNode != null) {
         rootNode.removeAll();
         if (viewer != null) {
            viewer.refresh();
         }
      }
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.viewer = (TreeViewer) viewer;

      if (newInput != null) {
         rootNode = new RootNode("root");
         service = (IOteMessageService) newInput;
         viewRefresher.start();
      } else {
         deleteAll();
         rootNode = null;
         service = null;
         viewRefresher.stop();
      }
   }

   public MessageNode createElements(String message, Collection<ElementPath> elements) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
      boolean needToRegisterListener = false;
      WatchedMessageNode messageNode = (WatchedMessageNode) rootNode.getMessageByName(message);
      if (messageNode == null) {
         messageNode = createMessageNode(message);
         needToRegisterListener = true;
      }
      for (ElementPath elementPath : elements) {
         if (elementPath.isValidElement() && messageNode.findChildElement(elementPath) == null) {
            addChildElementPath(message, elementPath, messageNode);
         }
      }
      if (needToRegisterListener) {
         // need to register listener only after children are created.
         messageNode.getSubscription().addSubscriptionListener(new MessageUpdateListener(viewer, messageNode));

      }
      return messageNode;
   }

   private MessageNode addChildElementPath(String message, ElementPath elementPath, WatchedMessageNode messageNode) {

      // messageNode.addUpdateListener(nodeUpdateHandler);
      if (messageNode.findChildElement(elementPath) == null) {
         if (elementPath.size() > 2) {// then it's some sort of nested item
            // who's parent is not the MessageNode
            ElementNode parentNode = findParent(messageNode, elementPath);
            if (parentNode == null) {
               parentNode = createDescendants(messageNode, elementPath);
            }
            createElement(parentNode, elementPath);
         } else {
            createElement(messageNode, elementPath);
         }
      } else {
         System.out.println("tried to add twice");
      }
      return messageNode;
   }

   public void deleteAll() {
      if (rootNode != null) {
         viewer.getTree().setRedraw(false);
         rootNode.removeAll();
         viewer.getTree().setRedraw(true);
         viewer.refresh();
      }
   }

   public void clearUpdateCounters(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         Object item = iter.next();
         if (item instanceof WatchedMessageNode) {
            WatchedMessageNode node = (WatchedMessageNode) item;
            node.clearUpdateCounter();
            viewer.update(node, null);
         }
      }
   }

   public void clearAllUpdateCounters() {
      if (rootNode != null && !rootNode.isDisposed()) {
         for (MessageNode node : rootNode.getChildren()) {
            if (node instanceof WatchedMessageNode) {
               WatchedMessageNode msgNode = (WatchedMessageNode) node;
               msgNode.clearUpdateCounter();
               viewer.update(msgNode, null);
            }
         }
      }
   }

   public void deleteSelection(IStructuredSelection selection) {
      final HashSet<MessageNode> msgNodesToDelete = new HashSet<MessageNode>(64);
      final HashSet<ElementNode> elemNodesToDelete = new HashSet<ElementNode>(64);
      final INodeVisitor<Boolean> visitor = new INodeVisitor<Boolean>() {

         @Override
         public Boolean elementNode(ElementNode node) {
            if (!msgNodesToDelete.contains(node.getParent())) {
               return elemNodesToDelete.add(node);
            }
            return null;
         }

         @Override
         public Boolean messageNode(MessageNode node) {
            return msgNodesToDelete.add(node);
         }

         @Override
         public Boolean rootNode(RootNode node) {
            return null;
         }

      };
      for (Object node : selection.toArray()) {
         ((AbstractTreeNode) node).visit(visitor);
      }
      viewer.getTree().setRedraw(false);
      for (AbstractTreeNode node : msgNodesToDelete) {
         node.delete();
      }
      for (AbstractTreeNode node : elemNodesToDelete) {
         node.delete();
      }
      viewer.getTree().setRedraw(true);
      viewer.refresh();
   }

   private WatchedMessageNode createMessageNode(String message) throws Exception {
      IMessageSubscription subscription = service.subscribe(message);
      WatchedMessageNode node = new WatchedMessageNode(subscription);
      rootNode.addChild(node);
      return node;
   }

   private ElementNode createElement(MessageNode messageNode, ElementPath elementPath) {
      return createElementCommon(messageNode, elementPath);
   }

   private ElementNode createElementCommon(MessageNode messageNode, ElementPath elementPath) {
      WatchedElementNode child = new WatchedElementNode(elementPath);
      messageNode.addChild(child);
      return child;
   }

   private ElementNode createElementCommon(ElementNode parentNode, ElementPath elementPath) {
      WatchedElementNode child = new WatchedElementNode(elementPath);
      parentNode.addChild(child);
      return child;
   }

   private ElementNode findParent(MessageNode messageNode, ElementPath elementPath) {
      return messageNode.findDescendant(elementPath.subElementPath(elementPath.size() - 2));
   }

   private ElementNode createDescendants(MessageNode messageNode, ElementPath elementPath) {
      ElementPath subElementPath = elementPath.subElementPath(1);
      ElementNode elementNode = messageNode.findChildElement(subElementPath);
      if (elementNode == null) {
         elementNode = createElementCommon(messageNode, subElementPath);
      }
      ElementNode parentNode = elementNode;
      for (int i = 2; i < elementPath.size() - 1; i++) {
         subElementPath = elementPath.subElementPath(i);
         elementNode = messageNode.findChildElement(subElementPath);
         if (elementNode == null) {
            elementNode = createElementCommon(parentNode, subElementPath);
         }
         parentNode = elementNode;
      }
      return elementNode;
   }

   private ElementNode createElement(ElementNode parentNode, ElementPath elementPath) {
      return createElementCommon(parentNode, elementPath);
   }

   public WatchedMessageNode getMessageNode(String msgClassName) {
      return (WatchedMessageNode) rootNode.getMessageByName(msgClassName);
   }

   public Collection<MessageNode> getMessages() {
      if (rootNode == null || rootNode.isDisposed()) {
         return Collections.emptyList();
      }
      return rootNode.getChildren();
   }
}