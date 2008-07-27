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
package org.eclipse.osee.framework.search.engine.internal;

import java.sql.Connection;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TagQueueJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Roberto E. Escobar
 */
final class AttributeXmlParser extends AbstractSaxHandler {
   private static int CACHE_LIMIT = 1000;
   private TagQueueJoinQuery joinQuery;
   private Connection connection;
   private int cacheCount;

   AttributeXmlParser(Connection connection, TagQueueJoinQuery joinQuery) {
      this.joinQuery = joinQuery;
      this.cacheCount = 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#endElementFound(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void endElementFound(String uri, String localName, String name) throws SAXException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      if (name.equalsIgnoreCase("entry")) {
         String gammaId = attributes.getValue("gammaId");
         if (Strings.isValid(gammaId)) {
            joinQuery.add(Long.parseLong(gammaId));
            cacheCount++;
            if (cacheCount >= CACHE_LIMIT) {
               try {
                  joinQuery.store(connection);
                  cacheCount = 0;
               } catch (Exception ex) {
                  OseeLog.log(AttributeXmlParser.class, Level.WARNING, ex);
               }
            }
         }
      }
   }
}