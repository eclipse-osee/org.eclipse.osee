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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Roberto E. Escobar
 */
public class V0_9_2ArtifactVersionHandler extends AbstractSaxHandler {
   private final List<Long> obsoleteGammas = new ArrayList<Long>();

   private final Map<Long, Long> obsoleteGammaToNetGammaId;
   private final Map<Integer, Long> artIdToNetGammaId;

   private int previousArtifactId;
   private Long netGamma;

   public V0_9_2ArtifactVersionHandler(Map<Integer, Long> artIdToNetGammaId, Map<Long, Long> obsoleteGammaToNetGammaId) {
      this.artIdToNetGammaId = artIdToNetGammaId;
      this.obsoleteGammaToNetGammaId = obsoleteGammaToNetGammaId;
   }

   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (localName.equals("entry")) {
         int artifactId = Integer.parseInt(attributes.getValue("art_id"));
         Long gammaId = Long.parseLong(attributes.getValue("gamma_id"));

         if (isNextConceptualArtifact(artifactId)) {
            consolidate(previousArtifactId);
            initNextConceptualArtifact(artifactId, gammaId);
         } else {
            obsoleteGammas.add(gammaId);
         }

      }
   }

   private void initNextConceptualArtifact(int artifactId, Long gammaId) {
      obsoleteGammas.clear();
      previousArtifactId = artifactId;
      netGamma = gammaId;
   }

   private boolean isNextConceptualArtifact(int artifactId) {
      return previousArtifactId != artifactId;
   }

   private void consolidate(int artifactId) {
      if (!obsoleteGammas.isEmpty()) {
         artIdToNetGammaId.put(artifactId, netGamma);
         for (Long obsoleteGamma : obsoleteGammas) {
            obsoleteGammaToNetGammaId.put(obsoleteGamma, netGamma);
         }
      }
   }
}