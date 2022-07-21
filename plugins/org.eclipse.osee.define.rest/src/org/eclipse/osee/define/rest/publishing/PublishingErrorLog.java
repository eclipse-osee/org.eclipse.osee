/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.rest.publishing;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.define.api.PublishingArtifactError;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.util.WordMLWriter;

/**
 * @author Loren K. Ashley
 */

/**
 * Encapsulates a log of {@link PublishingArtifactError} objects that can be inserted in to the document being
 * published.
 *
 * @author Loren K. Ashley
 */

public class PublishingErrorLog {

   /**
    * Accumulator for {@link PublishingArtifactError} objects.
    */

   private final List<PublishingArtifactError> publishingErrors;

   /**
    * Creates a new empty {@link PublishingErrorLog}.
    */

   public PublishingErrorLog() {
      this.publishingErrors = new LinkedList<PublishingArtifactError>();
   }

   /**
    * Removes all entries from the log.
    */

   public void clear() {
      this.publishingErrors.clear();
   }

   /**
    * Creates and appends to the log a new {@link PublishingArtifactError} with the specified artifact and message.
    *
    * @param artifactToken the artifact the error is pertaining to.
    * @param message a description of the error.
    */

   public void error(ArtifactToken artifactToken, String message) {
      this.publishingErrors.add(new PublishingArtifactError(artifactToken.getId(), artifactToken.getName(),
         artifactToken.getArtifactType(), message));
   }

   /**
    * Creates and appends to the log a new {@link PublishingArtifactError} with the specified team workflow and message.
    *
    * @param iAtsTeamWorkflow the team workflow the error is pertaining to.
    * @param message a description of the error.
    */

   public void error(IAtsTeamWorkflow iAtsTeamWorkflow, String message) {
      this.publishingErrors.add(new PublishingArtifactError(iAtsTeamWorkflow.getId(), iAtsTeamWorkflow.getName(),
         iAtsTeamWorkflow.getArtifactType(), message));
   }

   /**
    * Creates and appends to the log a new {@link PublishingArtifactError} with the <code>SENTINEL</code>
    * {@link ArtifactToken} and the specified message.
    *
    * @param message a description of the error.
    */

   public void error(String message) {
      this.publishingErrors.add(
         new PublishingArtifactError(ArtifactToken.SENTINEL.getId(), "N/A", ArtifactTypeToken.SENTINEL, message));
   }

   /**
    * Publishes the error log to the provided {@link WordMLWriter} in a table format. The error log is not cleared.
    *
    * @param wordMl the {@link WordMLWriter} to publish the error log to.
    */

   public void publishErrorLog(WordMLWriter wordMl) {

      if (this.publishingErrors.isEmpty()) {
         return;
      }

      wordMl.startErrorLog();

      for (PublishingArtifactError error : this.publishingErrors) {
         wordMl.addErrorRow(error.getArtId().toString(), error.getArtName(), error.getArtType().getName(),
            error.getErrorDescription());
      }

      wordMl.endErrorLog();
   }

}

/* EOF */
