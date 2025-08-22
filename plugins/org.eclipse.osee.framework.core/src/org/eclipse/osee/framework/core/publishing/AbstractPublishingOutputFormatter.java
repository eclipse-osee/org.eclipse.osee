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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactToken;

public abstract class AbstractPublishingOutputFormatter implements PublishingOutputFormatter {

   private final PublishingOutputFormatMode pubOutputFormatMode;
   private final ArtifactToken dataRightsMappingArt;
   private final Map<String, String> cssCollector = new HashMap<>();

   public AbstractPublishingOutputFormatter(PublishingOutputFormatMode formatMode, ArtifactToken dataRightsMappingArt) {
      this.pubOutputFormatMode = formatMode;
      this.dataRightsMappingArt = dataRightsMappingArt;
   }

   @Override
   public String formatToc(String content) {
      return content;
   }

   @Override
   public String getDataRightsCss(String classification, String content) {
      return "";
   }

   @Override
   public PublishingOutputFormatMode getFormatMode() {
      return pubOutputFormatMode;
   }

   @Override
   public String getFormatModeAsString() {
      return pubOutputFormatMode.toString();
   }

   @Override
   public ArtifactToken getDataRightsMappingArtifact() {
      return dataRightsMappingArt;
   }

   @Override
   public String getCollectedCss() {
      return String.join("\n\n", cssCollector.values());
   }

   @Override
   public void addCss(String key, String value) {
      cssCollector.put(key, value);
   }

   @Override
   public boolean cssKeyExists(String key) {
      return cssCollector.containsKey(key);
   }
}
