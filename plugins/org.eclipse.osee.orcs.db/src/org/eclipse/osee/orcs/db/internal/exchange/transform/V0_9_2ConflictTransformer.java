/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.orcs.db.internal.exchange.transform;

import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;
import org.xml.sax.Attributes;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class V0_9_2ConflictTransformer extends SaxTransformer {
   private final Map<Long, Long> artifactGammaToNetGammaId;

   public V0_9_2ConflictTransformer(Map<Long, Long> artifactGammaToNetGammaId) {
      this.artifactGammaToNetGammaId = artifactGammaToNetGammaId;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws XMLStreamException {
      writer.writeStartElement(localName);
      for (int i = 0; i < attributes.getLength(); i++) {
         String value = null;
         if (attributes.getLocalName(i).equals("source_gamma_id") || attributes.getLocalName(i).equals(
            "dest_gamma_id")) {
            Long netGammaId = artifactGammaToNetGammaId.get(Long.valueOf(attributes.getValue(i)));
            if (netGammaId != null) {
               value = String.valueOf(netGammaId);
            }
         }
         if (value == null) {
            value = attributes.getValue(i);
         }
         writer.writeAttribute(attributes.getLocalName(i), value);
      }
   }
}