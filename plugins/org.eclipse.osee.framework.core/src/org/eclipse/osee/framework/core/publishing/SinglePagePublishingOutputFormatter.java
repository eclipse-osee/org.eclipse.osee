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

import org.eclipse.osee.framework.core.data.ArtifactToken;

public abstract class SinglePagePublishingOutputFormatter extends AbstractPublishingOutputFormatter {

   public SinglePagePublishingOutputFormatter(PublishingOutputFormatMode formatMode, ArtifactToken dataRightsMappingArt) {
      super(formatMode, dataRightsMappingArt);
   }

   @Override
   public String formatDataRightsOpen(String classification, String content) {
      //@formatter:off
      return String.format("<hr style=\"border: 5px double #000;\" />" +
                           "<p style=\"text-align: center; font-size: 8pt; white-space: pre-wrap; max-width: 90%%; margin-left: auto; margin-right: auto;\">%s</p>", content);
      //@formatter:on
   }

   @Override
   public String formatDataRightsClose(String content) {
      //@formatter:off
      return String.format("<p style=\"text-align: center; font-size: 8pt; white-space: pre-wrap; max-width: 90%%; margin-left: auto; margin-right: auto;\">%s</p>" +
                           "<hr style=\"border: 5px double #000;\" />", content);
      //@formatter:on
   }
}
