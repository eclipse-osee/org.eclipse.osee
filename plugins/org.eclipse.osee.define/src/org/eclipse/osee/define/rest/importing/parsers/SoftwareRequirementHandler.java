/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import java.util.regex.Pattern;
import org.eclipse.osee.define.api.importing.RoughArtifact;
import org.eclipse.osee.define.api.importing.RoughArtifactKind;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Robert A. Fisher
 */
public abstract class SoftwareRequirementHandler extends WordOutlineExtractorDelegate {
   private static final Pattern partitionPattern = Pattern.compile("\\[([\\w\\(|\\)]+)\\]");

   private final AttributeTypeToken attributeType;

   protected SoftwareRequirementHandler(AttributeTypeToken attributeType) {
      super();
      this.attributeType = attributeType;
   }

   @Override
   public void processHeadingText(RoughArtifact roughArtifact, String headingText) {
      StringBuilder text = new StringBuilder(headingText);
      Matcher matcher = partitionPattern.matcher(text);
      boolean isRequirement = false;

      while (matcher.find()) {
         isRequirement = true;

         roughArtifact.addAttribute(attributeType, matcher.group(1).trim());
         text.delete(matcher.start(), matcher.end());
         matcher.reset(text);
      }

      if (!isRequirement) {
         roughArtifact.setRoughArtifactKind(RoughArtifactKind.SECONDARY);
      }

      roughArtifact.setName(text.toString().trim());
   }
}
