/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AccessContextResults {

   ArtifactToken artifact = ArtifactToken.SENTINEL;
   Collection<AccessContextResult> contextResults = new ArrayList<>();
   XResultData results;
   AccessTypeMatch finalMatch = AccessTypeMatch.NotComputed;
   String reason = "";

   public AccessContextResults() {
   }

   public Collection<AccessContextResult> getContextResults() {
      return contextResults;
   }

   public void setContextResults(Collection<AccessContextResult> contextResults) {
      this.contextResults = contextResults;
   }

   public ArtifactToken getArtifact() {
      return artifact;
   }

   public void setArtifact(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   public XResultData getResults() {
      if (results == null) {
         results = new XResultData();
         results.logf("Context Results:\nArtifact: %s\n", artifact.toStringWithId());
         for (AccessContextResult result : contextResults) {
            result.logToResults(results);
         }
         results.logf("Final Match: %s\n", finalMatch.name());
         results.logf("Reason: %s\n", reason);
      }
      return results;
   }

   public AccessTypeMatch getFinalMatch() {
      return finalMatch;
   }

   public void setFinalMatch(AccessTypeMatch finalMatch) {
      this.finalMatch = finalMatch;
   }

   public String getReason() {
      return reason;
   }

   public void setReason(String reason) {
      this.reason = reason;
   }

}
