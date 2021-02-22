/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.framework.core.data;

import java.util.regex.Pattern;

/**
 * @author Branden W. Phillips
 */
public class FileTypeApplicabilityData {

   private final Pattern commentedTagPattern;
   private final String commentPrefix;
   private final String commentSuffix;

   public FileTypeApplicabilityData(Pattern commentedTagPattern, String commentPrefix, String commentSuffix) {
      this.commentedTagPattern = commentedTagPattern;
      this.commentPrefix = commentPrefix;
      this.commentSuffix = commentSuffix;
   }

   public FileTypeApplicabilityData() {
      this.commentedTagPattern = Pattern.compile("");
      this.commentPrefix = "";
      this.commentSuffix = "";
   }

   public Pattern getCommentedTagPattern() {
      return commentedTagPattern;
   }

   public String getCommentPrefix() {
      return commentPrefix;
   }

   public String getCommentSuffix() {
      return commentSuffix;
   }

}
