/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.exchange.transform;

import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.jdk.core.util.io.xml.SaxTransformer;
import org.xml.sax.Attributes;

/**
 * @author Roberto E. Escobar
 */
public class V0_9_2ArtifactDataTransformer extends SaxTransformer {
   private final Map<Integer, Long> artIdToNetGammaId;

   public V0_9_2ArtifactDataTransformer(Map<Integer, Long> artIdToNetGammaId) {
      this.artIdToNetGammaId = artIdToNetGammaId;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws XMLStreamException {
      super.startElementFound(uri, localName, qName, attributes);
      if (localName.equals("entry")) {
         int artifactId = Integer.parseInt(attributes.getValue("art_id"));
         GammaId gammaId = GammaId.valueOf(artIdToNetGammaId.get(artifactId));
         writer.writeAttribute("gamma_id", gammaId.getIdString());
      }
   }
}