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

import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class WordMlPublishingOutputFormatter extends AbstractPublishingOutputFormatter {

   public WordMlPublishingOutputFormatter() {
      super(PublishingOutputFormatMode.WORD_ML, CoreArtifactTokens.DataRightsFooters);
   }

   @Override
   public String formatDataRightsOpen(String classification, String content) {
      return content;
   }

   @Override
   public String formatDataRightsClose(String content) {
      return Strings.EMPTY_STRING;
   }
}
