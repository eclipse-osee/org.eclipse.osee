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
package org.eclipse.osee.framework.jdk.core.util.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterPrettyPrint implements XMLStreamWriter {

   private static final String[] spaces;

   static {
      int count = 200;
      spaces = new String[count];
      for (int i = 0; i < count; i++) {
         spaces[i] = new String("\n");
         for (int j = 0; j < i; j++) {
            spaces[i] += " ";
         }
      }
   }

   private final XMLStreamWriter writer;
   private int indent = 0;

   public XMLStreamWriterPrettyPrint(XMLStreamWriter writer) {
      this.writer = writer;
   }

   @Override
   public void writeStartElement(String localName) throws XMLStreamException {
      writeNewLineAndIndent();
      indent++;
      writer.writeStartElement(localName);
   }

   @Override
   public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
      writeNewLineAndIndent();
      indent++;
      writer.writeStartElement(namespaceURI, localName);
   }

   @Override
   public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
      writeNewLineAndIndent();
      indent++;
      writer.writeStartElement(prefix, localName, namespaceURI);
   }

   @Override
   public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
      writeNewLineAndIndent();
      writer.writeEmptyElement(namespaceURI, localName);
   }

   @Override
   public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
      writeNewLineAndIndent();
      writer.writeEmptyElement(prefix, localName, namespaceURI);
   }

   @Override
   public void writeEmptyElement(String localName) throws XMLStreamException {
      writeNewLineAndIndent();
      writer.writeEmptyElement(localName);
   }

   @Override
   public void writeEndElement() throws XMLStreamException {
      indent--;
      writeNewLineAndIndent();
      writer.writeEndElement();
   }

   @Override
   public void writeEndDocument() throws XMLStreamException {
      indent--;
      writer.writeEndDocument();
   }

   @Override
   public void close() throws XMLStreamException {
      writer.close();
   }

   @Override
   public void flush() throws XMLStreamException {
      writer.flush();
   }

   @Override
   public void writeAttribute(String localName, String value) throws XMLStreamException {
      writer.writeAttribute(localName, value);
   }

   @Override
   public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
      writer.writeAttribute(prefix, namespaceURI, localName, value);
   }

   @Override
   public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
      writer.writeAttribute(namespaceURI, localName, value);
   }

   @Override
   public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
      writer.writeNamespace(prefix, namespaceURI);
   }

   @Override
   public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
      writer.writeDefaultNamespace(namespaceURI);
   }

   @Override
   public void writeComment(String data) throws XMLStreamException {
      writeNewLine();
      writer.writeComment(data);
   }

   @Override
   public void writeProcessingInstruction(String target) throws XMLStreamException {
      writer.writeProcessingInstruction(target);
   }

   @Override
   public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
      writer.writeProcessingInstruction(target, data);
   }

   @Override
   public void writeCData(String data) throws XMLStreamException {
      writer.writeCData(data);
   }

   @Override
   public void writeDTD(String dtd) throws XMLStreamException {
      writer.writeDTD(dtd);
   }

   @Override
   public void writeEntityRef(String name) throws XMLStreamException {
      writer.writeEntityRef(name);
   }

   @Override
   public void writeStartDocument() throws XMLStreamException {
      writer.writeStartDocument();
   }

   @Override
   public void writeStartDocument(String version) throws XMLStreamException {
      writer.writeStartDocument();
   }

   @Override
   public void writeStartDocument(String encoding, String version) throws XMLStreamException {
      writer.writeStartDocument(encoding, version);
   }

   @Override
   public void writeCharacters(String text) throws XMLStreamException {
      writer.writeCharacters(text);
   }

   @Override
   public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
      writer.writeCharacters(text, start, len);
   }

   @Override
   public String getPrefix(String uri) throws XMLStreamException {
      return writer.getPrefix(uri);
   }

   @Override
   public void setPrefix(String prefix, String uri) throws XMLStreamException {
      writer.setPrefix(prefix, uri);
   }

   @Override
   public void setDefaultNamespace(String uri) throws XMLStreamException {
      writer.setDefaultNamespace(uri);
   }

   @Override
   public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
      writer.setNamespaceContext(context);
   }

   @Override
   public NamespaceContext getNamespaceContext() {
      return writer.getNamespaceContext();
   }

   @Override
   public Object getProperty(String name) throws IllegalArgumentException {
      return writer.getProperty(name);
   }

   private void writeNewLineAndIndent() throws XMLStreamException {
      if (indent < spaces.length) {
         writer.writeCharacters(spaces[indent]);
      } else {
         for (int i = 0; i < indent; i++) {
            writer.writeCharacters(" ");
         }
      }
   }

   private void writeNewLine() throws XMLStreamException {
      writer.writeCharacters(spaces[0]);
   }

}
