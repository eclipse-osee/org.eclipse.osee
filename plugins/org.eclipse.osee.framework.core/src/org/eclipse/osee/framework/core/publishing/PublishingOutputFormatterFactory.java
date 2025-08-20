/*********************************************************************
 * Copyright (c) 2025 Boeing
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

public class PublishingOutputFormatterFactory {

   public static PublishingOutputFormatter getFormatter(PublishingOutputFormatMode mode) {
      switch (mode) {
         case WORD_ML:
            return new WordMlPublishingOutputFormatter();
         case HTML:
            return new HtmlPublishingOutputFormatter();
         case MARKDOWN:
            return new MarkdownPublishingOutputFormatter();
         case PAGED:
            return new PdfPublishingOutputFormatter();
         case NO_OP:
            return new NoOpPublishingOutputFormatter();
         default:
            throw new IllegalArgumentException("Unsupported PublishingOutputFormatMode: " + mode);
      }
   }
}
