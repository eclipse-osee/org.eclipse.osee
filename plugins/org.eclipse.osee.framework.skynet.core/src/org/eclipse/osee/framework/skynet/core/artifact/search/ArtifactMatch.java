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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.SearchResponse.ArtifactMatchMetaData;
import org.eclipse.osee.framework.core.message.SearchResponse.AttributeMatchMetaData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatch {
   private final Artifact artifact;
   private final ArtifactMatchMetaData matchMetaData;

   public ArtifactMatch(Artifact artifact, ArtifactMatchMetaData matchMetaData) {
      this.artifact = artifact;
      this.matchMetaData = matchMetaData;
   }

   public boolean hasMatchData() {
      return matchMetaData != null;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public HashCollection<Attribute<?>, MatchLocation> getMatchData() throws OseeCoreException {
      HashCollection<Attribute<?>, MatchLocation> matchData = new HashCollection<Attribute<?>, MatchLocation>();
      for (Attribute<?> attribute : artifact.getAttributes()) {
         AttributeMatchMetaData match = matchMetaData.getAttributeMatch((long) attribute.getGammaId());
         if (match != null) {
            matchData.put(attribute, match.getLocations());
         }
      }
      return matchData;
   }
}
