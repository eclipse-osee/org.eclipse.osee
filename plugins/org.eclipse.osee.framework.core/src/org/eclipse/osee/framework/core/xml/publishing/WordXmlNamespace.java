/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.core.xml.publishing;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public enum WordXmlNamespace implements XmlNamespaceSpecification {

   //@formatter:off
   ANNOTATION_MARKUP_LANGUAGE
      (
         "aml",
         "http://schemas.microsoft.com/aml/2001/core"
      ),

   AUX_HINT
      (
         "wx",
         "http://schemas.microsoft.com/office/word/2003/auxHint"
      ),

   WORDML
      (
         "w",
         "http://schemas.microsoft.com/office/word/2003/wordml"
      ),

   XML
      (
         "xml"
      );
   //@formatter:on

   private final @NonNull String prefix;
   private final @NonNull Optional<@Nullable String> uri;

   private WordXmlNamespace(@NonNull String prefix, @NonNull String uri) {
      this.prefix = Objects.requireNonNull(prefix);
      this.uri = Optional.of(uri);
   }

   private WordXmlNamespace(@NonNull String prefix) {
      this.prefix = Objects.requireNonNull(prefix);
      this.uri = Optional.empty();
   }

   @Override
   public @NonNull String getPrefix() {
      return this.prefix;
   }

   @Override
   public @NonNull Optional<String> getUri() {
      return this.uri;
   }

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}
