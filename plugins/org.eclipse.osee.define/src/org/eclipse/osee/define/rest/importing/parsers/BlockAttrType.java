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

package org.eclipse.osee.define.rest.importing.parsers;

import java.util.regex.Matcher;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author David W. Miller
 */
public class BlockAttrType extends BlockAttr {

   public BlockAttrType(String typeRegex, String contentRegex, String attrTypeName, AttributeTypeToken type) {
      super(typeRegex, contentRegex, attrTypeName, type);
   }

   @Override
   public Boolean fillContent(String content) {
      Boolean match = false;
      Conditions.assertNotNullOrEmpty(content, "null content in add content to block");
      String strippedContent = content.replaceAll("<[^>]+>", "");
      Matcher matcher = typeRegex.matcher(strippedContent);
      if (matcher.find()) {
         Matcher contentMatcher = contentRegex.matcher(strippedContent);
         if (contentMatcher.find()) {
            data = matcher.group(1);
            if (data.equals("Requirement")) {
               data = CoreArtifactTypes.CustomerRequirementMsWord.getName();
            } else if (data.equals("Heading")) {
               data = CoreArtifactTypes.HeadingMsWord.getName();
            } else if (data.equals("Design Description")) {
               data = CoreArtifactTypes.DesignDescriptionMsWord.getName();
            } else {
               data = CoreArtifactTypes.SystemRequirementMsWord.getName(); // default
            }
         } else {
            data = CoreArtifactTypes.SystemRequirementMsWord.getName();
         }
         match = true;
      }
      return match;
   }
}
