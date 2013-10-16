/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.build.report.parser;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Megumi Telles
 */
public abstract class AtsAbstractSAXParser<T> extends DefaultHandler {

   private final File xmlFile;
   private static final SAXParserFactory factory = SAXParserFactory.newInstance();
   private final AtsDataHandler<T> dataHandler;

   public interface AtsDataHandler<T> {

      void handleData(T data);

   }

   public AtsAbstractSAXParser(File xmlFile, AtsDataHandler<T> dataHandler) {
      this.xmlFile = xmlFile;
      this.dataHandler = dataHandler;
   }

   public void parseDocument() throws OseeCoreException {
      SAXParser parser;
      try {
         parser = factory.newSAXParser();
         parser.parse(xmlFile, this);
      } catch (ParserConfigurationException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (SAXException ex) {
         OseeExceptions.wrapAndThrow(ex);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   protected void handleData(T data) {
      dataHandler.handleData(data);
   }

}