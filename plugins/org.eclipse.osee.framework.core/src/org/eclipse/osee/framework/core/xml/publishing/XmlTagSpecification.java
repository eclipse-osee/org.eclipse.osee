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

public interface XmlTagSpecification extends XmlNameSpecification {

   Set<XmlAttributeSpecification> getAllowedAttributes();

   /**
    * Gets the Word ML tag formatted as a closing tag.
    *
    * @return the tag formatted as a close tag.
    */

   public @NonNull String getCloseTag();

   /**
    * Gets the Word ML tag formatted as an open tag without attributes.
    *
    * @return the tag formatted as an open tag without attributes.
    */

   public @NonNull String getOpenTag();

   /**
    * Gets a regular expression string that finds the self closing tag with any number of attributes. The regular
    * expression does not test for any specific attribute.
    *
    * @return the regular expression string.
    */

   public @NonNull String getSelfCloseTag();

   /**
    * Gets the number of characters in the {@link WordMlTag} represented as a close tag.
    *
    * @return the number of characters in the close tag.
    */

   public int sizeCloseTag();

   /**
    * Gets the number of characters in the {@link WordMlTag} represented as an open tag without attributes.
    *
    * @return the number of characters in the open tag.
    */

   public int sizeOpenTag();

   public int sizeSelfCloseTag();

   /**
    * Gets the {@link String} representation of the Word ML tag as the {@link XmlTagType} (open, close, self closing)
    * specified by <code>xmlTagType</code>.
    *
    * @param xmlTagType the {@link XmlTagType} to get.
    * @return the {@link String} representation of the Word ML tag as the {@link XmlTagType} (open, close, self closing)
    * specified by <code>xmlTagType</code>.
    * @throws NullPointerException when <code>xmlTagType</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>xmlTagType</code> is not one of {@link XmlTagType#OPEN},
    * {@link XmlTagType#CLOSE}, or {@link XmlTagType#SELF_CLOSING}.
    */

   public default @NonNull String getTag(@NonNull XmlTagType xmlTagType) {

      final var safeXmlTagType = Conditions.requireNonNull(xmlTagType, "xmlTagType");

      switch (safeXmlTagType) {
         case OPEN:
            return this.getOpenTag();
         case CLOSE:
            return this.getCloseTag();
         case SELF_CLOSING:
            return this.getSelfCloseTag();
         default:
            throw Conditions.invalidCase(xmlTagType, "xmlTagType", IllegalArgumentException::new);
      }

   }

   /**
    * Gets a regular expression string that finds the open tag with any number of attributes. The regular expression
    * does not test for any specific attribute.
    *
    * @return the regular expression string.
    */

   public String getRegexOpenTag();

   /**
    * Predicate to determine if the provided tag name and name space match the Word ML tag the enumeration member
    * represents.
    *
    * @param tagName a Word ML tag name with name space and no other XML formatting such as '&lt;', '&gt;', and '/'
    * characters.
    * @return <code>true</code> when the <code>tagName</code> matches the XML tag represented by this enumeration
    * member; otherwise, <code>false</code>.
    */

   public default boolean isTagName(CharSequence tagName) {
      return this.getFullname().contentEquals(tagName);
   }

   /**
    * Predicate to determine if an attribute is valid for the Word ML tag represented by this enumeration member.
    *
    * @param wordXmlAttribute the {@link WordMlAttribute} to test.
    * @return <code>true</code> when <code>wordXmlAttribute</code> is valid for the Word ML tag represented by the
    * enumeration member; otherwise, <code>false</code>.
    */

   public default boolean isValidAttribute(XmlAttributeSpecification xmlAttribute) {
      return this.getAllowedAttributes().contains(xmlAttribute);
   }

   /**
    * Gets the size of the {@link String} representation of the Word ML tag as the {@link XmlTagType} (open, close, self
    * closing) specified by <code>xmlTagType</code>.
    *
    * @param xmlTagType the {@link XmlTagType} to get.
    * @return the size of the {@link String} representation of the Word ML tag as the {@link XmlTagType} (open, close,
    * self closing) specified by <code>xmlTagType</code>.
    * @throws NullPointerException when <code>xmlTagType</code> is <code>null</code>.
    * @throws IllegalArgumentException when <code>xmlTagType</code> is not one of {@link XmlTagType#OPEN},
    * {@link XmlTagType#CLOSE}, or {@link XmlTagType#SELF_CLOSING}.
    */

   public default int size(@NonNull XmlTagType xmlTagType) {

      final var safeXmlTagType = Conditions.requireNonNull(xmlTagType, "xmlTagType");

      switch (safeXmlTagType) {
         case OPEN:
            return this.sizeOpenTag();
         case CLOSE:
            return this.sizeCloseTag();
         case SELF_CLOSING:
            return this.sizeSelfCloseTag();
         default:
            throw Conditions.invalidCase(xmlTagType, "xmlTagType", IllegalArgumentException::new);
      }

   }

   /**
    * Generates a regular expression string that finds everything between and including the open and closing tag for
    * this {@link XmlMlTagSpecification} implementation.
    *
    * @return the regular expression string.
    */

   public default @NonNull String getRegexEverythingBetweenTags() {
      //@formatter:off
         return
            new StringBuilder( 128 )
                   .append( "<" )
                   .append( this.getFullname() )
                   .append( "[\\s\\S]+?")
                   .append( this.getCloseTag() )
                   .toString();
         //@formatter:on
   }

   /**
    * Returns the length of the opening without attributes and closing tags specified by <code>wordXmlTags</code>.
    *
    * @param wordXmlTags
    * @return the length of the specified opening without attributes and closing tags.
    */

   public static int sizeBothTags(XmlTagSpecification... xmlTagSpecifications) {
      if (Objects.isNull(xmlTagSpecifications) || (xmlTagSpecifications.length == 0)) {
         return 0;
      }
      var sum = 0;
      for (var xmlTagSpecification : xmlTagSpecifications) {
         sum += xmlTagSpecification.sizeOpenTag() + xmlTagSpecification.sizeCloseTag();
      }
      return sum;
   }

   /**
    * Returns the length of the closing tags specified by <code>wordXmlTags</code>.
    *
    * @param wordXmlTags
    * @return the length of the specified closing tags.
    */

   public static int sizeCloseTags(XmlTagSpecification... xmlTags) {
      if (Objects.isNull(xmlTags) || (xmlTags.length == 0)) {
         return 0;
      }
      int sum = 0;
      for (var i = 0; i < xmlTags.length; i++) {
         sum += xmlTags[i].sizeCloseTag();
      }
      return sum;
   }

   /**
    * Returns the length of the opening without attributes tags specified by <code>wordXmlTags</code>.
    *
    * @param wordXmlTags
    * @return the length of the specified opening without attributes tags.
    */

   public static int sizeOpenTags(XmlTagSpecification... xmlTags) {
      if (Objects.isNull(xmlTags) || (xmlTags.length == 0)) {
         return 0;
      }
      int sum = 0;
      for (var i = 0; i < xmlTags.length; i++) {
         sum += xmlTags[i].sizeOpenTag();
      }
      return sum;
   }

}
