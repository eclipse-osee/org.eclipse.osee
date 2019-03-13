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
package org.eclipse.define.api.importing;

import java.util.regex.Matcher;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author David W. Miller
 */
public class BlockField {
   protected String data;
   protected final BlockFieldToken bft;

   public BlockField(BlockFieldToken bft) {
      this.bft = bft;
      this.data = "";
   }

   public Boolean fillContent(String content) {
      Boolean match = false;
      Conditions.assertNotNullOrEmpty(content, "null content in add content to block");
      String strippedContent = content.replaceAll("<[^>]+>", "");
      Matcher contentMatcher = bft.contentRegex.matcher(strippedContent);
      if (contentMatcher.find()) {
         data = contentMatcher.group(1);
         match = true;
      } else {
         data = "Failed to set content";
      }
      return match;
   }

   public void appendContent(String content, boolean first) {
      String newline = "";
      if (!first) {
         newline = "\n";
      }
      data = data + newline + content;
   }

   public String getImportTypeName() {
      return bft.getName();
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   public AttributeTypeToken getOseeType() {
      return bft.getOseeType();
   }

   public Boolean getMarksComplete() {
      return bft.getMarksComplete();
   }
}
