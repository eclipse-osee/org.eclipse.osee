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
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;
import org.xml.sax.Attributes;

/**
 * @author Roberto E. Escobar
 */
public class V0_9_2TxsNetGammaTransformer extends SaxTransformer {
   private final Map<Long, Long> obsoleteGammaToNet;

   public V0_9_2TxsNetGammaTransformer(Map<Long, Long> obsoleteGammaToNet) {
      this.obsoleteGammaToNet = obsoleteGammaToNet;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws Exception {
      if (localName.equals("entry")) {
         writer.writeStartElement(localName);
         for (int i = 0; i < attributes.getLength(); i++) {
            String attributeName = attributes.getLocalName(i);
            String value = attributes.getValue(i);

            if ("gamma_id".equals(attributeName)) {
               Long gammaId = Long.parseLong(value);
               Long netGammaId = obsoleteGammaToNet.get(gammaId);
               if (netGammaId != null) {
                  value = String.valueOf(netGammaId);
               }
            }
            writer.writeAttribute(attributeName, value);
         }
      } else {
         super.startElementFound(uri, localName, qName, attributes);
      }
   }
}