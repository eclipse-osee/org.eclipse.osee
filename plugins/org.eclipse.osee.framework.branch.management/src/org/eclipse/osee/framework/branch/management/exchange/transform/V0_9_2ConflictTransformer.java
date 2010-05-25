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

import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;
import org.xml.sax.Attributes;

/**
 * @author Roberto E. Escobar
 */
public class V0_9_2ConflictTransformer extends SaxTransformer {
   private final Map<Integer, Long> artIdToNetGammaId;
   private static final int ARTIFACT_CONFLICT = 3; // DO NOT CHANGE TO USE ENUM

   public V0_9_2ConflictTransformer(Map<Integer, Long> artIdToNetGammaId) {
      this.artIdToNetGammaId = artIdToNetGammaId;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws Exception {
      if (localName.equals("entry")) {
         int conflictType = Integer.parseInt(attributes.getValue("conflict_type"));
         if (ARTIFACT_CONFLICT == conflictType) {
            transformEntry(uri, localName, qName, attributes);
         } else {
            super.startElementFound(uri, localName, qName, attributes);
         }
      } else {
         super.startElementFound(uri, localName, qName, attributes);
      }

   }

   private boolean isGammaAttribute(String attributeName) {
      return attributeName.equalsIgnoreCase("source_gamma_id") || attributeName.equalsIgnoreCase("dest_gamma_id");
   }

   private void transformEntry(String uri, String localName, String qName, Attributes attributes) throws XMLStreamException {
      Integer artifactId = Integer.parseInt(attributes.getValue("conflict_id"));
      String netGamma = String.valueOf(artIdToNetGammaId.get(artifactId));

      writer.writeStartElement(localName);
      for (int i = 0; i < attributes.getLength(); i++) {
         String attributeName = attributes.getLocalName(i);
         if (isGammaAttribute(attributeName)) {
            writer.writeAttribute(attributeName, netGamma);
         }
      }
   }
}