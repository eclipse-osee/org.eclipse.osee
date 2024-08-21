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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * A class to specify the static parts of a publishing filename with extension.
 *
 * @author Loren K. Ashley
 */

public class FilenameSpecification {

   /**
    * Saves the desired format for the generated filename.
    */

   private final FilenameFormat filenameFormat;

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
    * @param filenameFormat the {@link FilenameFormat} for the generated filename.
    * @param extension the extension for the filename excluding the extension separator.
    * @param segments a possibly empty array of {@link CharSequence}s to be used as filename segments.
    */

   public FilenameSpecification(@NonNull String key, @NonNull FilenameFormat filenameFormat, @NonNull CharSequence extension, @NonNull CharSequence... segments) {
      this.key = Conditions.requireNonNull(key, "key");
      this.filenameFormat = Conditions.requireNonNull(filenameFormat, "filenameFormat");
      this.extension = Conditions.requireNonNull(extension, "extension");
      this.segments = Conditions.requireNonNull(segments, "segments");
   }

   /**
    * Builds the filename with extension for a publishing file.
    *
    * @return the generated filename.
    * @throws IllegalStateException for an unexpected {@link FilenameFormat}.
    */

   @NonNull
   String build() {

      switch (this.filenameFormat) {

         case PREVIEW: {
            //@formatter:off
            final var filename =
               FilenameFactory.create
                  (
                     FilenameFactory.getDateSegment(),
                     FilenameFactory.getRandomSegment(),
                     this.extension,
                     this.segments
                  );
            //@formatter:on
            return filename;
         }

         case EXPORT: {
            //@formatter:off
            final var filename =
               FilenameFactory.create
                  (
                     this.extension,
                     this.segments[0]
                  );
            //@formatter:on
            return filename;
         }

         default:
            throw Conditions.invalidCase(this.filenameFormat, "filenameFormat", IllegalStateException::new);
      }
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
