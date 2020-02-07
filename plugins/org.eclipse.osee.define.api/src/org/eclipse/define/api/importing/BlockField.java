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
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author David W. Miller
 */
public class BlockField {
   protected String data;
   protected final BlockFieldToken bft;
   protected boolean match = false;
   protected boolean created = false;

   public BlockField(BlockFieldToken bft) {
      this.bft = bft;
      this.data = "";
   }

   public BlockField fillContent(String content) {
      Conditions.assertNotNullOrEmpty(content, "null content in add content to block");
      String strippedContent = content.replaceAll("<[^>]+>", "");
      Matcher contentMatcher = bft.contentRegex.matcher(strippedContent);
      if (contentMatcher.find()) {
         data = contentMatcher.group(1);
         this.match = true;
      } else {
         data = "";
      }
      return this;
   }

   public void appendContent(String content, boolean first) {
      String newline = "";
      if (!first && Strings.isValid(data)) {
         newline = "\n";
      }
      data = data + newline + content;
   }

   public String getImportTypeName() {
      return bft.getName();
   }

   public Long getId() {
      return bft.getId();
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

   public boolean isMatch() {
      return match;
   }

   public void setMatch(boolean value) {
      this.match = value;
   }

   public boolean isCreated() {
      return created;
   }

   public void setCreated(boolean value) {
      this.created = value;
   }
}
