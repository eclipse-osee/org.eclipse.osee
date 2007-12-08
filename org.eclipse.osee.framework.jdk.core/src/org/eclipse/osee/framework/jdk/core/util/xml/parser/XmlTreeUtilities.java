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
package org.eclipse.osee.framework.jdk.core.util.xml.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.eclipse.osee.framework.jdk.core.collection.tree.TreeNode;

/**
 * @author Roberto E. Escobar
 */
public class XmlTreeUtilities {

   private static XmlTreeUtilities instance = null;

   private XmlTreeUtilities() {
   }

   public static XmlTreeUtilities getInstance() {
      if (instance == null) {
         instance = new XmlTreeUtilities();
      }
      return instance;
   }

   private Stack<String> processPath(String pattern) {
      Stack<String> elementPath = new Stack<String>();
      String[] path = pattern.split("/");
      for (int index = path.length - 1; index >= 0; index--) {
         String entry = path[index];
         entry = entry.trim();
         if (entry.length() > 0) {
            elementPath.push(entry);
         }
      }
      return elementPath;
   }

   public List<TreeNode<XmlNode>> getXmlNodesAt(String path, TreeNode<XmlNode> root) {
      Stack<String> elementPath = processPath(path);
      List<TreeNode<XmlNode>> toReturn = new ArrayList<TreeNode<XmlNode>>();
      List<TreeNode<XmlNode>> nodes = root.getChildren();
      while (!elementPath.empty()) {
         String token = elementPath.pop();
         nodes = collectNodesMatchingToken(token, nodes);
      }
      if (nodes != null && nodes.size() > 0) {
         toReturn.addAll(nodes);
      }

      return toReturn;
   }

   private List<TreeNode<XmlNode>> collectNodesMatchingToken(String token, List<TreeNode<XmlNode>> nodes) {
      List<TreeNode<XmlNode>> toReturn = new ArrayList<TreeNode<XmlNode>>();

      for (TreeNode<XmlNode> node : nodes) {
         if (token.equalsIgnoreCase("*")) {
            if (node.getChildren().size() > 0) {
               toReturn.addAll(node.getChildren());
            }
         }
         if (node.getSelf().getName().equalsIgnoreCase(token)) {
            toReturn.add(node);
            if (node.getChildren().size() > 0) {
               toReturn.addAll(node.getChildren());
            }
         }
      }
      return toReturn;
   }

   public int countNodesMatchingText(List<TreeNode<XmlNode>> nodes, String toMatch) {
      int result = 0;
      for (TreeNode<XmlNode> node : nodes) {
         if (node != null) {
            String content = node.getSelf().getTextContent();
            if (content.equalsIgnoreCase(toMatch)) {
               result++;
            }
         }
      }
      return result;
   }

   public XmlNode getFirstXmlNode(List<TreeNode<XmlNode>> nodes) {
      XmlNode toReturn = null;
      if (nodes.size() > 0) {
         toReturn = nodes.get(0).getSelf();
      }
      return toReturn;
   }

   public String getFirstXmlNodeText(List<TreeNode<XmlNode>> nodes) {
      XmlNode node = getFirstXmlNode(nodes);
      return node != null ? node.getTextContent() : "";
   }

   public boolean containsNode(String path, TreeNode<XmlNode> nodes) {
      List<TreeNode<XmlNode>> searchResults = getXmlNodesAt("Location", nodes);
      return searchResults.size() > 0;
   }

   public String getTextAtPath(String path, TreeNode<XmlNode> nodes) {
      return getFirstXmlNodeText(getXmlNodesAt(path, nodes));
   }

   public XmlNode getXmlNodeAtPath(String path, TreeNode<XmlNode> nodes) {
      return getFirstXmlNode(getXmlNodesAt(path, nodes));
   }
}
