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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class V0_9_2ArtifactVersionHandler extends AbstractSaxHandler {
   private final Map<Integer, Long> artIdToNetGammaId = new HashMap<>(14000);
   private final Map<Long, Long> artifactGammaToNetGammaId = new HashMap<>(14000);

   @Override
   public void endElementFound(String uri, String localName, String qName) {
      //
   }

   public Map<Long, Long> getArtifactGammaToNetGammaId() {
      return artifactGammaToNetGammaId;
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
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