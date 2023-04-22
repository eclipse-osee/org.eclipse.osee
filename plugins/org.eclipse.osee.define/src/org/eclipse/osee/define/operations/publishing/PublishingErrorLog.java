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

package org.eclipse.osee.define.operations.publishing;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.define.api.publishing.PublishingArtifactError;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.HasArtifactType;
import org.eclipse.osee.framework.core.publishing.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

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
    * Creates and appends to the log a new {@link PublishingArtifactError} with the specified {@link NamedId} and
    * {@link HasArtifactType} thing and message.
    *
    * @param thing object implementing the {@link NamedId} and {@link HasArtifactType} interfaces.
    * @param message a description of the error.
    */

   public <T extends NamedId & HasArtifactType> void error(T thing, String message) {
      this.publishingErrors.add(new PublishingArtifactError(thing, message));
   }

   /**
    * Creates and appends to the log a new {@link PublishingArtifactError} for the specified objects implementing the
    * {@link NamedId} and {@link HasArtifactType} interfaces.
    *
    * @param things a {@link List} of objects implementing the {@link NamedId} and {@link HasArtifactType} interfaces.
    * @param message a description of the error.
    */

   public <T extends NamedId & HasArtifactType> void error(List<T> things, String message) {
      this.publishingErrors.add(new PublishingArtifactError(things, message));
   }

   /**
    * Creates and appends to the log a new {@link PublishingArtifactError} with the <code>SENTINEL</code>
    * {@link ArtifactToken} and the specified message.
    *
    * @param message a description of the error.
    */

   public void error(String message) {
      this.publishingErrors.add(new PublishingArtifactError(ArtifactToken.SENTINEL, message));
   }

   /**
    * Publishes the error log to the provided {@link StringBuilder} in a text list format. The error log is not cleared.
    *
    * @param stringBuilder the {@link StringBuilder} to publish the error log to.
    */

   public void publishErrorLog(StringBuilder stringBuilder) {

      if (this.publishingErrors.isEmpty()) {
         return;
      }

      var count = 0;

      for (PublishingArtifactError error : this.publishingErrors) {
         stringBuilder.append(count++).append(":").append("\n");
         error.publish(stringBuilder);
         stringBuilder.append("\n");
      }

   }

   /**
    * Publishes the error log to the provided {@link WordMLProducer} in a table format. The error log is not cleared.
    *
    * @param wordMl the {@link WordMLProducer} to publish the error log to.
    */

   public void publishErrorLog(WordMLProducer wordMl) {

      if (this.publishingErrors.isEmpty()) {
         return;
      }

      wordMl.startErrorLog();

      for (PublishingArtifactError error : this.publishingErrors) {
         error.publish(wordMl);
      }

      wordMl.endErrorLog();
   }

   /**
    * Gets the number of errors on the publishing error log.
    *
    * @return the number of errors on the log.
    */

   public int size() {
      return this.publishingErrors.size();
   }

}

/* EOF */
