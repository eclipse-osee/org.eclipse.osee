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
               data = CoreArtifactTypes.CustomerRequirementMSWord.getName();
            } else if (data.equals("Heading")) {
               data = CoreArtifactTypes.HeadingMSWord.getName();
            } else if (data.equals("Design Description")) {
               data = CoreArtifactTypes.DesignDescriptionMSWord.getName();
            } else {
               data = CoreArtifactTypes.SystemRequirementMSWord.getName(); // default
            }
         } else {
            data = CoreArtifactTypes.SystemRequirementMSWord.getName();
         }
         match = true;
      }
      return match;
   }
}
