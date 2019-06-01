/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author David W. Miller
 */
public class BlockAttrText extends BlockAttr {

   private static final String BLOCK_ATTR_REGEX = "^[^:]+:\\s*(.*)";

   public BlockAttrText(String typeRegex, String contentRegex, String attrTypeName, AttributeTypeToken type) {
      super(typeRegex, contentRegex, attrTypeName, type);
   }

   @Override
   public Boolean fillContent(String content) {
      Boolean match = false;
      Conditions.assertNotNullOrEmpty(content, "null content in add content to block");
      String strippedContent = content.replaceAll("<[^>]+>", "");
      Matcher matcher = typeRegex.matcher(strippedContent);
      if (matcher.find()) {
         // text attr uses full content to get all of the word content into the attribute
         Matcher valueMatcher = Pattern.compile(BLOCK_ATTR_REGEX).matcher(content.replaceAll("<[^>]+>", ""));

         if (valueMatcher.find()) {
            String objectText = valueMatcher.group(1);
            if (Strings.isValid(objectText)) {
               Matcher contentMatcher = contentRegex.matcher(content);
               if (contentMatcher.find()) {
                  String paragraph = contentMatcher.group(1);
                  String text = contentMatcher.group(3);
                  // string will have an extra :, replace it
                  data = paragraph + text.replaceFirst("<w:r><w:t> :  ", "<w:r><w:t>");
               }
            } else {
               data = "";
            }
         }

         match = true;
      }
      return match;
   }
}
