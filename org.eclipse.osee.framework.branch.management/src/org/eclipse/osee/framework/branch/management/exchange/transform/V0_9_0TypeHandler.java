/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.HashMap;
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class V0_9_0TypeHandler extends AbstractSaxHandler {
   private final HashMap<Integer, String> typeIdMap = new HashMap<Integer, String>(100);
   private final String typeIdColumn;
   private final String typeNameColumn;
   private final AbstractOseeCache<?> cache;

   public V0_9_0TypeHandler(AbstractOseeCache<?> cache, String typeIdColumn, String typeNameColumn) {
      this.typeIdColumn = typeIdColumn;
      this.typeNameColumn = typeNameColumn;
      this.cache = cache;
   }

   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException, OseeCoreException {
      if (localName.equals("entry")) {
         String typeName = attributes.getValue(typeNameColumn);
         if (typeName.equals("ats.Parent Branch Id")) {
            typeName = "ats.Baseline Branch Guid";
         }
         String guid = cache.getBySoleName(typeName).getGuid();
         typeIdMap.put(Integer.parseInt(attributes.getValue(typeIdColumn)), guid);
      }
   }

   public HashMap<Integer, String> getTypeIdMap() {
      return typeIdMap;
   }
}