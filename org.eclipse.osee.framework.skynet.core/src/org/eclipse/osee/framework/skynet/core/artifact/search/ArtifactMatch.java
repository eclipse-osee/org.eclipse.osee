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

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactXmlQueryResultParser.MatchLocation;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatch {
   private final Artifact artifact;
   private HashCollection<Long, MatchLocation> attributeMatches;

   protected ArtifactMatch(Artifact artifact) {
      this.artifact = artifact;
      this.attributeMatches = null;
   }

   public boolean hasMatchData() {
      return attributeMatches != null;
   }

   protected void addMatches(HashCollection<Long, MatchLocation> attributeMatches) {
      this.attributeMatches = attributeMatches;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public HashCollection<Attribute<?>, MatchLocation> getMatchData() throws OseeCoreException {
      HashCollection<Attribute<?>, MatchLocation> matchData = new HashCollection<Attribute<?>, MatchLocation>();

      for (Attribute<?> attribute : artifact.getAttributes()) {
         Collection<MatchLocation> locations = attributeMatches.getValues((long) attribute.getGammaId());
         if (locations != null) {
            matchData.put(attribute, locations);
         }
      }
      return matchData;
   }
}
