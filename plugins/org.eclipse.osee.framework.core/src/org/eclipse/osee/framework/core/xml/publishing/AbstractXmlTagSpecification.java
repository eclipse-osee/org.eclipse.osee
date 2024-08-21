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
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

class AbstractXmlTagSpecification extends AbstractXmlNameSpecification {

   /**
    * Saves the {@link WordMlAttribute}s associated with the Word ML tag.
    */

   final @NonNull Set<@NonNull XmlAttributeSpecification> attributes;

   /**
    * Saves the Word ML tag formatted as a closing tag.
    */

   final @NonNull String closeTag;

   /**
    * Saves the Word ML tag without attributes formatted as an open tag.
    */

   final @NonNull String openTag;

   /**
    * Saves the regular expression as a {@link String} for the opening tag with any and all attributes. The regular
    * expression does not test for any specific attribute.
    */

   final @NonNull String regexOpenTag;

   /**
    * Saves the Word ML tag formatted as a self closing tag.
    */

   final @NonNull String selfCloseTag;

   /**
    * Saves the number of characters in both the opening and closing tags.
    */

   final int sizeBothTags;

   /**
    * Saves the number of characters in the closing tag.
    */

   final int sizeCloseTag;

   /**
    * Saves the number of characters in the self close tag without attributes.
    */

   final int sizeSelfCloseTag;

   /**
    * Saves the number of characters in the open tag without attributes.
    */

   final int sizeOpenTag;

   /**
    * Creates a new {@link WordMlTag} without attributes.
    *
    * @param tagName the namespace and name of the Word ML tag.
    * @throws IllegalArgumentException when <code>tagName</code> is <code>null</code> or blank.
    */

   AbstractXmlTagSpecification(@NonNull AbstractXmlNamespaceSpecification abstractXmlNamespaceSpecification, @NonNull String tagName) {
      this(abstractXmlNamespaceSpecification, tagName, Set.of());
   }

   /**
    * Creates a new {@link WordMlTag} with attributes.
    *
    * @param tagName the namespace and name of the Word ML tag.
    * @param wordXmlAttributes a set of the {@link WordMlAttribute}s for this Word XML tag.
    * @throws NullPointerException when:
    * <ul>
    * <li>the parameter <code>tagName</code> is <code>null</code>,</li>
    * <li>the parameter <code>wordXmlAttributes</code> is <code>null</code>, or</li>
    * <li>the parameter <code>wordXmlAttributes</code> contains a <code>null</code> entry.</li>
    * </ul>
    */

   AbstractXmlTagSpecification(@NonNull AbstractXmlNamespaceSpecification abstractXmlNamespaceSpecification, @NonNull String tagName, @NonNull Set<@NonNull XmlAttributeSpecification> wordXmlAttributes) {

      super(abstractXmlNamespaceSpecification, tagName);

      //@formatter:off
      Conditions.require
         (
            tagName,
            Conditions.ValueType.PARAMETER,
            "tagName",
            "cannot be null or blank",
            Strings::isInvalidOrBlank,
            IllegalArgumentException::new
         );

      var safeWordXmlAttributes =
         Conditions.require
            (
               wordXmlAttributes,
               Conditions.ValueType.PARAMETER,
               "wordXmlAttributes",
               "cannot be null or cannot contain null entries",
               Conditions.or
                  (
                     Objects::isNull,
                     Conditions::collectionContainsNull
                  ),
               NullPointerException::new
            );

      this.attributes   = safeWordXmlAttributes;

      this.openTag      = "<"  + this.fullName + ">";
      this.closeTag     = "</" + this.fullName + ">";
      this.selfCloseTag = "<"  + this.fullName + "/>";
      this.regexOpenTag = "<"  + this.fullName + "[^>]*>";
      this.sizeOpenTag  = this.openTag.length();
      this.sizeCloseTag = this.closeTag.length();
      this.sizeBothTags = this.sizeOpenTag + this.sizeCloseTag;
      this.sizeSelfCloseTag = this.selfCloseTag.length();
      //@formatter:on
   }

}
