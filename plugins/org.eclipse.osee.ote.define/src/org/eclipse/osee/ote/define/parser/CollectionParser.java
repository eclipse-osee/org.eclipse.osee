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

public class CollectionParser extends AbstractSaxHandler implements ICollectionSource {

   private final List<SaxChunkCollector> collectors;

   public CollectionParser(ArrayList<SaxChunkCollector> collectors) {
      this.collectors = collectors;
   }

   public CollectionParser() {
      collectors = new ArrayList<>();
   }

   @Override
   public void endElementFound(String uri, String localName, String name) {
      for (SaxChunkCollector collector : collectors) {
         collector.endElementFound(localName, this);
      }
   }

   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) {
      for (SaxChunkCollector collector : collectors) {
         collector.startElementFound(localName, attributes, this);
      }
   }

   public List<SaxChunkCollector> getCollectors() {
      return collectors;
   }
}
