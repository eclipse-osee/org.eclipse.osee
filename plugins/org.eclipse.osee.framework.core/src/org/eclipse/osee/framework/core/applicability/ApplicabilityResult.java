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
package org.eclipse.osee.framework.core.applicability;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApplicabilityResult {

   private String sanitizedContent = "";
   private String message = "";
   public ApplicabilityResult() {

   }

   public ApplicabilityResult(String result) {
      ObjectMapper objMapper = new ObjectMapper();
      JsonNode root;
      try {
         root = objMapper.readTree(result);
         this.sanitizedContent = root.get("sanitized_content").asText();
         this.message = root.get("message").asText();
      } catch (Exception ex) {
         this.sanitizedContent = "";
         this.message = "";
      }
   }

   public String getSanitizedContent() {
      return sanitizedContent;
   }

   public void setSanitizedContent(String content) {
      this.sanitizedContent = content;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

}
