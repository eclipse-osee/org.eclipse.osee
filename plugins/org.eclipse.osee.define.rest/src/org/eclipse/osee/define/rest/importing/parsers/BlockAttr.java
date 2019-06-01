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

/**
 * @author David W. Miller
 */
public class BlockAttr {
   protected final Pattern typeRegex;
   protected final Pattern contentRegex;
   private Boolean marksComplete;
   protected String data;
   protected final String attrTypeName;
   private final AttributeTypeToken oseeType;

   public BlockAttr(String typeRegex, String contentRegex, String attrTypeName, AttributeTypeToken oseeType) {
      this.typeRegex = Pattern.compile(typeRegex);
      this.contentRegex = Pattern.compile(contentRegex, Pattern.DOTALL); // DOTALL is important for block attr text subclass
      this.attrTypeName = attrTypeName;
      this.oseeType = oseeType;
      this.marksComplete = false;
      this.data = "";
   }

   public Boolean fillContent(String content) {
      Boolean match = false;
      Conditions.assertNotNullOrEmpty(content, "null content in add content to block");
      String strippedContent = content.replaceAll("<[^>]+>", "");
      Matcher matcher = typeRegex.matcher(strippedContent);
      if (matcher.find()) {
         Matcher contentMatcher = contentRegex.matcher(strippedContent);
         if (contentMatcher.find()) {
            data = contentMatcher.group(1);
         } else {
            data = "";
         }
         match = true;
      }
      return match;
   }

   public String getImportTypeName() {
      return attrTypeName;
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   public AttributeTypeToken getOseeType() {
      return oseeType;
   }

   public Boolean getMarksComplete() {
      return marksComplete;
   }

   public void setMarksComplete(Boolean marksComplete) {
      this.marksComplete = marksComplete;
   }

   @Override
   public String toString() {
      return attrTypeName;
   }
}
