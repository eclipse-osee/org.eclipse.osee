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

package org.eclipse.osee.define.api.importing;

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
