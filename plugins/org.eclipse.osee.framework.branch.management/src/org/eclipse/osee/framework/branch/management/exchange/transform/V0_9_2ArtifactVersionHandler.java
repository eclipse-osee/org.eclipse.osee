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
package org.eclipse.osee.framework.branch.management.exchange.transform;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class V0_9_2ArtifactVersionHandler extends AbstractSaxHandler {
   private final Map<Integer, Long> artIdToNetGammaId = new HashMap<Integer, Long>(14000);
   private final Map<Long, Long> artifactGammaToNetGammaId = new HashMap<Long, Long>(14000);

   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
   }

   public Map<Long, Long> getArtifactGammaToNetGammaId() {
      return artifactGammaToNetGammaId;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (localName.equals("entry")) {
         Integer artifactId = Integer.valueOf(attributes.getValue("art_id"));
         Long gammaId = Long.valueOf(attributes.getValue("gamma_id"));

         Long netGammaId = artIdToNetGammaId.get(artifactId);
         if (netGammaId == null) {
            netGammaId = gammaId;
            artIdToNetGammaId.put(artifactId, netGammaId);
         }
         artifactGammaToNetGammaId.put(gammaId, netGammaId);
      }
   }

   public Map<Integer, Long> getArtIdToNetGammaId() {
      return artIdToNetGammaId;
   }
}