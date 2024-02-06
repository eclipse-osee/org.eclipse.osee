/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.orcs.core.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * A class to specify the path of a publishing template content file and the format of that content.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateContentMapEntry {

   /**
    * The format of the contents of the publishing template file.
    */

   private @NonNull final FormatIndicator formatIndicator;

   /**
    * The {@link Path} of the file containing the contents for the publishing template.
    */

   private @NonNull final Path templateContentPath;

   /**
    * Creates a new publishing template content specification.
    *
    * @param formatIndicator the format of the content.
    * @param templateContentFilename the {@link Path} of the file containing the content.
    */

   public PublishingTemplateContentMapEntry(@NonNull FormatIndicator formatIndicator, @NonNull String templateContentFilename) {
      this.formatIndicator = Conditions.requireNonNull(formatIndicator);
      var templateContentPath = Paths.get(Conditions.requireNonNull(templateContentFilename));
      this.templateContentPath = Conditions.requireNonNull(templateContentPath);
   }

   /**
    * Gets format of the publishing template content.
    *
    * @return the content format as a {@link FormatIndicator}.
    */

   public @NonNull FormatIndicator getFormatIndicator() {
      return this.formatIndicator;
   }

   /**
    * Get the {@link Path} of the file containing the publishing template content.
    *
    * @return publishing template content file {@link Path}.
    */

   public @NonNull Path getTemplateContentPath() {
      return this.templateContentPath;
   }

}

/* EOF */
