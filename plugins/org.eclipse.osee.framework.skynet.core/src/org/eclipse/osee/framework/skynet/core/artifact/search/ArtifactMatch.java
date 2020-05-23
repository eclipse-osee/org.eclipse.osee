/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatch {
   private final Artifact artifact;
   private final HashCollection<AttributeId, MatchLocation> matchData = new HashCollection<>();

   public ArtifactMatch(Artifact artifact) {
      this.artifact = artifact;
   }

   public boolean hasMatchData() {
      return !matchData.isEmpty();
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public HashCollection<AttributeId, MatchLocation> getMatchData() {
      return matchData;
   }

   public void addMatchData(AttributeId attr, List<MatchLocation> locations) {
      matchData.put(attr, locations);
   }
}
