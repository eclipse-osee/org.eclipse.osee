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
package org.eclipse.osee.ote.define.parser;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CollectionParser extends AbstractSaxHandler implements ICollectionSource {

   private List<SaxChunkCollector> collectors;

   public CollectionParser(ArrayList<SaxChunkCollector> collectors) {
      this.collectors = collectors;
   }

   public CollectionParser() {
      collectors = new ArrayList<SaxChunkCollector>();
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.jdk.core.util.io.xml.AbstractSaxHandler#endElementFound(java.lang.String,
    *      java.lang.String, java.lang.String)
    */
   @Override
   public void endElementFound(String uri, String localName, String name) throws SAXException {
      for (SaxChunkCollector collector : collectors) {
         collector.endElementFound(localName, this);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String,
    *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      for (SaxChunkCollector collector : collectors) {
         collector.startElementFound(localName, attributes, this);
      }
   }

   public List<SaxChunkCollector> getCollectors() {
      return collectors;
   }
}
