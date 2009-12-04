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
package org.eclipse.osee.framework.manager.servlet;

import java.io.StringReader;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
class ClientInstallInfo {
   private final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
   private final static String EMPTY_STRING = "";

   private final String name;
   private String os;
   private boolean isActive;
   private String execPath;
   private String comment;

   private ClientInstallInfo(String name) {
      this.name = name;
      this.os = null;
      this.execPath = EMPTY_STRING;
      this.isActive = false;
      this.comment = EMPTY_STRING;
   }

   public String getName() {
      return name;
   }

   public String getOs() {
      return os;
   }

   public String getExecPath() {
      return execPath;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public String getComment() {
      return comment;
   }

   @Override
   public String toString() {
      return String.format("name:[%s] os:[%s] isActive:[%s] comment:[%s] location:[%s]", name, os, isActive, comment,
            execPath);
   }

   public static ClientInstallInfo createFromXml(String name, String data) throws OseeCoreException {
      ClientInstallInfo info = new ClientInstallInfo(name);
      try {
         if (!data.startsWith(XML_HEADER)) {
            data = XML_HEADER + data;
         }
         XMLReader reader = XMLReaderFactory.createXMLReader();
         reader.setContentHandler(new Parser(info));
         reader.parse(new InputSource(new StringReader(data)));
      } catch (Exception ex) {
         throw new OseeCoreException(String.format("Error parsing data for client install: [%s]", name), ex);
      }
      return info;
   }

   private final static class Parser extends AbstractSaxHandler {
      private final ClientInstallInfo info;

      private Parser(ClientInstallInfo info) {
         this.info = info;
      }

      @Override
      public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
         if (localName.equalsIgnoreCase("install")) {
            info.os = attributes.getValue("os");
            if (info.os != null) {
               info.os = info.os.toLowerCase();
            } else {
               info.os = EMPTY_STRING;
            }
            info.isActive = Boolean.valueOf(attributes.getValue("isActive"));
         }
      }

      @Override
      public void endElementFound(String uri, String localName, String name) throws SAXException {
         try {
            if (localName.equalsIgnoreCase("location")) {
               info.execPath = getContents();
               if (info.execPath == null) {
                  info.execPath = EMPTY_STRING;
               }
            } else if (localName.equalsIgnoreCase("comment")) {
               info.comment = getContents();
               if (info.comment == null) {
                  info.comment = EMPTY_STRING;
               }
            }
         } catch (Exception ex) {
            throw new IllegalStateException(ex);
         }
      }
   }
}
