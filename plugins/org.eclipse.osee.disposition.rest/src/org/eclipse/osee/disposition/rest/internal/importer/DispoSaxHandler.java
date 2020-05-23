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

package org.eclipse.osee.disposition.rest.internal.importer;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;

/**
 * XMLReader xmlReader = XMLReaderFactory.createXMLReader(); CollectionParser handler = new
 * CollectionParser(collectors); xmlReader.setContentHandler(handler); xmlReader.parse(new InputSource(inputStream));
 * 
 * @author Andrew M. Finkbeiner
 */
public class DispoSaxHandler extends AbstractSaxHandler {

   Map<String, ElementHandlers> handlers;

   public DispoSaxHandler() throws Exception {
      handlers = new HashMap<>();
      addHandlers(new TestPoint());
      addHandlers(new Number());
      addHandlers(new Result());
      addHandlers(new Stacktrace());
      addHandlers(new Actual());
      addHandlers(new Expected());
      addHandlers(new CheckGroup());
      addHandlers(new Config());
      addHandlers(new TestPointName());
      addHandlers(new ScriptVersion());
      addHandlers(new TestPointResults());
      addHandlers(new Time());
      addHandlers(new TimeSummary());
   }

   @Override
   public void endElementFound(String uri, String localName, String name) throws Exception {
      ElementHandlers handler;
      handler = handlers.get("*");
      if (handler != null) {
         handler.endElementFound(uri, localName, name, stripCData(getContents().trim()));
      }
      handler = handlers.get(name);
      if (handler != null) {
         handler.endElementFound(uri, localName, name, stripCData(getContents().trim()));
      }
   }

   private String stripCData(String content) {
      if (content.startsWith("<![CDATA[")) {
         return content.subSequence(9, content.length() - 3).toString();
      } else {
         return content;
      }

   }

   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws Exception {
      ElementHandlers handler;

      handler = handlers.get("*");
      if (handler != null) {
         handler.startElementFound(uri, localName, name, attributes);
      }

      handler = handlers.get(name);
      if (handler != null) {
         handler.startElementFound(uri, localName, name, attributes);
      }
   }

   public void addHandlers(ElementHandlers handler) throws Exception {
      Object obj = handlers.put(handler.getElementName(), handler);
      if (obj != null) {
         throw new Exception("Duplicate handler.");
      }
   }

   public ElementHandlers getHandler(String elementName) {
      return handlers.get(elementName);
   }
}