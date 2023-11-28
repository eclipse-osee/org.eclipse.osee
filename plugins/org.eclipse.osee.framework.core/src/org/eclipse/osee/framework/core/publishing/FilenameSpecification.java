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

package org.eclipse.osee.framework.core.publishing;

import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;

/**
 * A class to specify the static parts of a publishing filename with extension.
 *
 * @author Loren K. Ashley
 */

public class FilenameSpecification {

   /**
    * Saves the filename extension excluding the extension separator.
    */

   private final @NonNull CharSequence extension;

   /**
    * Saves a key to be associated with the filename built from this specification.
    */

   private final @NonNull String key;

   /**
    * Save a possibly empty array of {@link CharSequence}s to be used as filename segments.
    */

   private final @NonNull CharSequence[] segments;

   /**
    * Saves the parameters for building a publishing filename.
    *
    * @param key a key to be associated with the filename build from this specification.
    * @param extension the extension for the filename excluding the extension separator.
    * @param segments a possibly empty array of {@link CharSequence}s to be used as filename segments.
    */

   public FilenameSpecification(@NonNull String key, @NonNull CharSequence extension, @NonNull CharSequence... segments) {
      this.key = Objects.requireNonNull(key);
      this.extension = Objects.requireNonNull(extension);
      this.segments = Objects.requireNonNull(segments);
   }

   /**
    * Builds the filename with extension for a publishing file.
    *
    * @param dateSegment the date segment for the filename.
    * @param randomSegment the random segment for the filename.
    * @return the build publishing filename.
    */

   @NonNull
   String build(@NonNull CharSequence dateSegment, @NonNull CharSequence randomSegment) {

      //@formatter:off
      var filename =
         FilenameFactory.create
            (
               Objects.requireNonNull(dateSegment),
               Objects.requireNonNull(randomSegment),
               this.extension,
               this.segments
            );
      //@formatter:on
      return filename;
   }

   /**
    * Gets the saved key to be associated with filenames generated from this specification.
    *
    * @return the filename's associated key.
    */

   public @NonNull String getKey() {
      return this.key;
   }

}

/* EOF */
