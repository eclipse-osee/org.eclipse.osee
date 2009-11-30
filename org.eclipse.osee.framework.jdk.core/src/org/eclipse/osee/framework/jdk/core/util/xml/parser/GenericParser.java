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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.collection.tree.Tree;
import org.eclipse.osee.framework.jdk.core.collection.tree.TreeNode;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
public class GenericParser extends AbstractSaxHandler {

   private String toFind;
   private boolean startCollecting;
   private Tree<XmlNode> collectedTree;
   private TreeNode<XmlNode> currentNode;
   private Set<IXmlNodeListener> listeners;
   private Set<IProcessingInstructionsListener> instructionListeners;
   private static XMLReader xmlReader;

   public GenericParser(String startTag) {
      this.toFind = startTag;
      this.listeners = new HashSet<IXmlNodeListener>();
      this.instructionListeners = new HashSet<IProcessingInstructionsListener>();
   }

   public void reset() {
      this.startCollecting = false;
      this.currentNode = null;
      this.collectedTree = null;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (localName.equalsIgnoreCase(toFind)) {
         startCollecting = true;
         collectedTree = new Tree<XmlNode>(new XmlNode(localName, attributes));
         currentNode = null;
      }

      if (startCollecting) {
         XmlNode node = new XmlNode(localName, attributes);
         if (currentNode == null) {
            currentNode = collectedTree.getRoot();
         } else {
            currentNode = currentNode.addChild(node);
         }
      }
   }

   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
      if (localName.equalsIgnoreCase(toFind)) {
         startCollecting = false;
         notifyTreeCollected(collectedTree);
      }

      if (startCollecting) {
         if (currentNode != null) {
            currentNode.getSelf().setTextContent(getContents());
            if (currentNode.getSelf().getName().equalsIgnoreCase(localName)) {
               // Found End of current Node Stop appending Children
               TreeNode<XmlNode> parent = currentNode.getParent();
               currentNode = parent;
            }
         }
      }
   }

   public void processingInstruction(String target, String data) throws SAXException {
      super.processingInstruction(target, data);
      notifyProcessingInstructionChanged(target, data);
   }

   synchronized public void registerInstructionListener(IProcessingInstructionsListener listener) {
      if (!instructionListeners.contains(listener)) {
         instructionListeners.add(listener);
      }
   }

   synchronized public void deregisterInstructionListener(IProcessingInstructionsListener listener) {
      if (instructionListeners.contains(listener)) {
         instructionListeners.remove(listener);
      }
   }

   synchronized public void registerXmlNodeListener(IXmlNodeListener listener) {
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   synchronized public void deregisterXmlNodeListener(IXmlNodeListener listener) {
      if (listeners.contains(listener)) {
         listeners.remove(listener);
      }
   }

   synchronized public void notifyTreeCollected(final Tree<XmlNode> tree) {
      TreeNode<XmlNode> root = tree.getRoot();
      for (IXmlNodeListener listener : listeners) {
         listener.collectionChanged(root);
      }
   }

   synchronized public void notifyProcessingInstructionChanged(String target, String data) {
      for (IProcessingInstructionsListener listener : instructionListeners) {
         listener.processingInstructionChanged(target, data);
      }
   }

   public void process(File xmlSource) throws SAXException, IOException {
      this.reset();
      process(new FileInputStream(xmlSource));
   }

   public void process(InputStream xmlSource) throws SAXException, IOException {
      this.reset();
      if (xmlReader == null) {
         xmlReader = XMLReaderFactory.createXMLReader();
      }
      xmlReader.setContentHandler(this);
      xmlReader.setErrorHandler(new ErrorHandler() {

         public void warning(SAXParseException exception) throws SAXException {
            exception.printStackTrace();
         }

         public void fatalError(SAXParseException exception) throws SAXException {
            exception.printStackTrace();
         }

         public void error(SAXParseException exception) throws SAXException {
            exception.printStackTrace();
         }

      });
      xmlReader.parse(new InputSource(xmlSource));
      System.out.println();
   }
}
