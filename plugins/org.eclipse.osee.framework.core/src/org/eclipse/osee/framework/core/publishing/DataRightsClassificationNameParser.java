/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import java.util.Optional;

/**
 * Shared implementation of the data rights classification parsing rule applied to a
 * {@code DataRightsFooters} {@code GeneralStringData} attribute value. A single value encodes a
 * classification name on its first line followed by footer content on the remaining lines. This
 * rule is the single source of truth reused by the footers indicator loader and by
 * {@code DataRightClassificationMap} so the two cannot drift; both the classification name and the
 * footer content are derived from the same split here so a value and its footer stay consistent.
 *
 * @author David W. Miller
 */

public final class DataRightsClassificationNameParser {

   /**
    * The result of parsing a footer value: the trimmed classification name and the trimmed footer
    * content that follows it. Only produced when the value yields both parts under the shared rule.
    */

   public static final class DataRightsFooter {

      private final String classification;
      private final String content;

      private DataRightsFooter(String classification, String content) {
         this.classification = classification;
         this.content = content;
      }

      public String getClassification() {
         return this.classification;
      }

      public String getContent() {
         return this.content;
      }
   }

   private DataRightsClassificationNameParser() {
      // do nothing
   }

   /**
    * Derives the classification name from a raw {@code GeneralStringData} footer value. The value is
    * split with {@code value.split("\n", 2)}; a name is contributed only when the split yields two
    * parts (a first line followed by footer content). The classification name is the trimmed first
    * line. A {@code null} value, a value without footer content, or a value whose first line is
    * blank after trimming contributes no name.
    *
    * @param value the raw footer attribute value; may be {@code null}.
    * @return the trimmed first line as the classification name, or an empty {@link Optional} when the
    * value does not yield a name under this rule.
    */

   public static Optional<String> parseClassificationName(String value) {
      return parseFooter(value).map(DataRightsFooter::getClassification);
   }

   /**
    * Parses a raw {@code GeneralStringData} footer value into its classification name and footer
    * content using the single shared rule: split with {@code value.split("\n", 2)}, contribute a
    * result only when the split yields two parts, the classification is the trimmed first line, and
    * the content is the trimmed remainder. A {@code null} value, a value without footer content, or a
    * value whose first line is blank after trimming yields no result. Callers that need only the name
    * should use {@link #parseClassificationName(String)}, which is derived from this method so the
    * name and content cannot come from different rules.
    *
    * @param value the raw footer attribute value; may be {@code null}.
    * @return the parsed classification name and footer content, or an empty {@link Optional} when the
    * value does not yield a footer under this rule.
    */

   public static Optional<DataRightsFooter> parseFooter(String value) {

      if (value == null) {
         return Optional.empty();
      }

      String[] parts = value.split("\n", 2);

      if (parts.length != 2) {
         return Optional.empty();
      }

      String classification = parts[0].trim();

      if (classification.isEmpty()) {
         return Optional.empty();
      }

      return Optional.of(new DataRightsFooter(classification, parts[1].trim()));
   }

}
