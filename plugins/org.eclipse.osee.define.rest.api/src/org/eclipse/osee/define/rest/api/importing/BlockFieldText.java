/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest.api.importing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author David W. Miller
 */
public class BlockFieldText extends BlockField {

   private static final String BLOCK_ATTR_REGEX = "^[^:]+:\\s*(.*)";

   public BlockFieldText(BlockFieldToken bft) {
      super(bft);
   }

   @Override
   public BlockField fillContent(String content) {
      Conditions.assertNotNullOrEmpty(content, "null content in add content to block");

      // text field uses full content to get all of the word content into the attribute
      Matcher valueMatcher = Pattern.compile(BLOCK_ATTR_REGEX).matcher(content.replaceAll("<[^>]+>", ""));

      if (valueMatcher.find()) {
         String objectText = valueMatcher.group(1);
         if (Strings.isValid(objectText)) {
            Matcher contentMatcher = bft.contentRegex.matcher(content);
            if (contentMatcher.find()) {
               String paragraph = contentMatcher.group(1);
               String text = contentMatcher.group(3);
               // string will have an extra :, replace it
               data = paragraph + text.replaceFirst("<w:r><w:t> :  ", "<w:r><w:t>");
               this.setMatch(true);
            }
         } else {
            data = "";
         }
      }
      return this;
   }
}
