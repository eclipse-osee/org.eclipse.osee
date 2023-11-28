/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.Quad;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.Validation;

/**
 * Enumeration of Word ML tag names with namespace.
 */

public enum WordXmlTag {
   //@formatter:off
      /**
       * XML tag for the Word document body element.
       */

      BODY("w:body"),

      /**
       * XML tag for bold
       */

      BOLD( "w:b" ),

      /**
       * XML tag for the field character element.
       */

      FIELD_CHARACTER
         (
            "w:fldChar",
            Set.of
               (
                  WordXmlAttribute.FIELD_CHARACTER_TYPE
               )
         ),

      /**
       * XML tag for a page footer
       */

      FOOTER( "w:ftr" ),

      /**
       * XML tag for a page header
       */

      HEADER( "w:hdr" ),

      /**
       * XML tag for the Word document hyper-link element.
       */

      HLINK
         (
            "w:hlink",
            Set.of
               (
                  WordXmlAttribute.DESTINATION
               )
         ),

      /**
       * XML tag for the instruction text element.
       */

      INSTRUCTION_TEXT( "w:instrText" ),

      /**
       * XML tag for a Word document paragraph element.
       */

      PARAGRAPH("w:p"),

      /**
       * XML tag for a Word document paragraph run element.
       */

      RUN("w:r"),

      /**
       * XML tag for a run presentation element.
       */

      RUN_PRESENTATION( "w:rPr" ),

      /**
       * XML tag for a Word run style element.
       */

      RUN_STYLE
         (
            "w:rStyle",
            Set.of
               (
                  WordXmlAttribute.VALUE
               )
         ),

      /**
       * XML tag for a Word document section element.
       */

      SECTION("wx:sect"),

      /**
       * XML tag for a Word document sub-section element.
       */

      SUBSECTION("wx:sub-section"),

      /**
       * XML tag for a Word document table element.
       */

      TABLE("w:tbl"),

      /**
       * XML tag for a Word document table column element.
       */

      TABLE_COLUMN("w:tc"),

      /**
       * XML tag for a Word document table row element.
       */

      TABLE_ROW("w:tr"),

      /**
       * XML tag for a Word document text element.
       */

      TEXT( "w:t" ),

      /**
       * XML tag ??
       */

      VANISH( "w:vanish" ),

      /**
       * XML tag for the "wordDocument" element.
       */

      WORD_DOCUMENT( "w:wordDocument" );
      //@formatter:on

   /**
    * Appends Word ML tags of the type <code>xmlTagType</code> for the tags specified by <code>wordXmlTags</code> to the
    * <code>appendable</code>.
    *
    * @param appendable an instance of an {@link Appendable} to append the closing tags to.
    * @param wordXmlTags the {@link WordXmlTag} enumeration members for the Word XML closing tags to be appended.
    * @return the {@link Appendable} <code>appendable</code>.
    * @throws NullPointerException when:
    * <ul>
    * <li><code>appendable</code> is <code>null</code>,</li>
    * <li><code>wordXmlTags</code> is <code>null</code>, or</li>
    * <li><code>wordXmlTags</code> contains a <code>null</code> element.</li>
    * </ul>
    * @throws OseeCoreExeption when appending to the <code>appendable</code> fails.
    */

   public static @NonNull Appendable appendTags(@NonNull Appendable appendable, @NonNull XmlTagType xmlTagType, @NonNull WordXmlTag... wordXmlTags) {

      Validation.requireNonNull(appendable, "WordXmlTag", "appendTags", "appendable");
      Validation.requireNonNull(xmlTagType, "XmlTagType", "appendTags", "xmlTagType");

      //@formatter:off
      Validation.require
         (
            wordXmlTags,
            Validation.ValueType.PARAMETER,
            "WordXmlTag",
            "appendTag",
            "wordXmlTags",
            "cannot be null or contain null elements",
            Validation::arrayContainsNull,
            NullPointerException::new
         );
      //@formatter:on

      try {

         for (var wordXmlTag : wordXmlTags) {
            appendable.append(wordXmlTag.getTag(xmlTagType));
         }

      } catch (Exception e) {
         //@formatter:off
            throw
               new OseeCoreException
                      (
                         new Message()
                                .title( "WordCoreUtil.WordXmlTag::appenCloseTags, failed to append to appendable." )
                                .indentInc()
                                .segmentIndexedArray( "Tags", wordXmlTags )
                                .reasonFollows( e )
                                .toString(),
                         e
                      );
            //@formatter:on
      }

      return appendable;
   }

   /**
    * Appends Word ML tags to the <code>appendable</code> according to the <code>wordXmlTagSpecifications</code>. The
    * specifications can be one of the following:
    * <dl>
    * <dt><code>Pair&lt;WordXmlTag,XmlTagType&gt;:</code></dt>
    * <dd>Appends an XML tag with no attributes.
    * <dl>
    * <dt>{@link WordXmlTag}:</dt>
    * <dd>The Word ML tag to append.</dd>
    * <dt>{@link XmlTagType}:</dt>
    * <dd>The type (open, close, self closing) of tag to append.</dd></dd>
    * <dt><code>Quad&lt;WordXmlTag,XmlTagType,WordXmlAttribute,CharSequence&gt;</code>:</dt> Appends an XML tag with a
    * single attribute.
    * <dd>
    * <dl>
    * <dt>{@link WordXmlTag}</dt>
    * <dd>The Word ML tag to append.</dd>
    * <dt>{@link XmlTagType}</dt>
    * <dd>The type (open, self closing) of tag to append.</dd>
    * <dt>{@link WordXmlAttribute}</dt>
    * <dd>The attribute to be included in the tag.</dd>
    * <dt>{@link CharSequence}</dt>
    * <dd>The value to be assigned to the attribute.</dd>
    * </dl>
    * </dd>
    * </dl>
    *
    * @param appendable an instance of an {@link Appendable} to append the closing tags to.
    * @param wordXmlTagSpecifications an array of <code>Pair&lt;WordXmlTag,XmlTagType&gt;</code> and or
    * <code>Quad&lt;WordXmlTag,XmlTagType,WordXmlAttribute,CharSequence&gt;</code> objects.
    * @return the {@link Appendable} <code>appendable</code>.
    * @throws NullPointerException when
    * <ul>
    * <li><code>appendable</code> is <code>null</code>,</li>
    * <li><code>wordXmlTagSpecifications</code> is <code>null</code>, or</li>
    * <li><code>wordXmlTagSpecifications</code> contains a <code>null</code> element.</li>
    * </ul>
    * @throws IllegalArgumentException when:
    * <ul>
    * <li><code>wordXmlTagSpecifications</code> contains an object other than
    * <code>Pair&lt;WordXmlTag,XmlTagType&gt;</code> or
    * <code>Quad&lt;WordXmlTag,XmlTagType,WordXmlAttribute,CharSequence&gt;</code>.</li>
    * <li>When the object is a {@link Pair} and the {@link XmlTagType} member is something other than
    * {@link XmlTagType#OPEN}, {@link XmlTagType#CLOSE}, or {@link XmlTagType#SELF_CLOSING}.</li>
    * <li>When the object is a {@Link Quad} and the {@link XmlTagType} member is something other than
    * {@link XmlTagType#OPEN}, or {@link XmlTagType#SELF_CLOSING}.</li>
    * </ul>
    */

   public static @NonNull Appendable appendTags(@NonNull Appendable appendable, @NonNull Object... wordXmlTagSpecifications) {

      Validation.requireNonNull(appendable, "WordXmlTag", "appendCloseTags", "appendable");

      //@formatter:off
      Validation.require
         (
            wordXmlTagSpecifications,
            Validation.ValueType.PARAMETER,
            "WordXmlTag",
            "appendTags",
            "wordXmlTagSpecifications",
            "cannot be null or contain null elements",
            Validation::arrayContainsNull,
            NullPointerException::new,
            "wordXmlTagSpecifications must be a Pair<WordXmlTag,XmlTagType> or Quad<WordXmlTag,XmlTagType,WordXmlAttribute,CharSequence>",
            Validation.arrayElementPredicate
               (
                  ( wordXmlTagSpecification ) ->

                        (
                             !( wordXmlTagSpecification instanceof Pair )
                          || !( ((Pair<?,?>) wordXmlTagSpecification).typesOk( WordXmlTag.class, XmlTagType.class ) )
                        )

                     &&

                        (
                              !( wordXmlTagSpecification instanceof Quad )
                           || !( ((Quad<?,?,?,?>) wordXmlTagSpecification).typesOk( WordXmlTag.class, XmlTagType.class, WordXmlAttribute.class, CharSequence.class ) )
                        )
               ),
            IllegalArgumentException::new
         );
      //@formatter:on

      try {

         for (var wordXmlTagSpecification : wordXmlTagSpecifications) {

            if (wordXmlTagSpecification instanceof Pair) {

               @SuppressWarnings("unchecked")
               var pair = (Pair<WordXmlTag, XmlTagType>) wordXmlTagSpecification;
               var wordXmlTag = pair.getFirst();
               var xmlTagType = pair.getSecond();
               switch (xmlTagType) {
                  case OPEN:
                     appendable.append(wordXmlTag.getOpenTag());
                     break;
                  case CLOSE:
                     appendable.append(wordXmlTag.getCloseTag());
                     break;
                  case SELF_CLOSING:
                     appendable.append(wordXmlTag.getSelfCloseTag());
                     break;
                  default:
                     //@formatter:off
                     throw
                        Validation.invalidCase
                           (
                              xmlTagType,
                              "WordXmlTag",
                              "appendTags",
                              "xmlTagType",
                              IllegalArgumentException::new
                           );
                     //@formatter:on
               }

            } else if (wordXmlTagSpecification instanceof Quad) {

               @SuppressWarnings("unchecked")
               var quad = (Quad<WordXmlTag, XmlTagType, WordXmlAttribute, CharSequence>) wordXmlTagSpecification;
               var wordXmlTag = quad.getFirst();
               var xmlTagType = quad.getSecond();
               var wordXmlAttribute = quad.getThird();
               var attributeValue = quad.getFourth();
               if (wordXmlTag.isValidAttribute(wordXmlAttribute)) {
                  switch (xmlTagType) {
                     case OPEN:
                        appendable.append(
                           wordXmlTag.getOpenTagWithAttributes(Pair.createNonNull(wordXmlAttribute, attributeValue)));
                     case SELF_CLOSING:
                        appendable.append(wordXmlTag.getSelfCloseTagWithAttributes(
                           Pair.createNonNull(wordXmlAttribute, attributeValue)));
                     default:
                        //@formatter:off
                        throw
                           Validation.invalidCase
                              (
                                 xmlTagType,
                                 "WordXmlTag",
                                 "appendTags",
                                 "xmlTagType",
                                 IllegalArgumentException::new
                              );
                        //@formatter:on
                  }
               }
            }
         }

      } catch (Exception e) {
         //@formatter:off
            throw
               new OseeCoreException
                      (
                         new Message()
                                .title( "WordCoreUtil.WordXmlTag::appendOpenTags, failed to append to appendable." )
                                .indentInc()
                                .segmentIndexedArray( "Word Ml Tag Specifications", wordXmlTagSpecifications )
                                .reasonFollows( e )
                                .toString(),
                         e
                      );
            //@formatter:on
      }

      return appendable;
   }

   /**
    * Returns the length of the opening without attributes and closing tags specified by <code>wordXmlTags</code>.
    *
    * @param wordXmlTags
    * @return the length of the specified opening without attributes and closing tags.
    */

   public static int sizeBothTags(WordXmlTag... wordXmlTags) {
      if (Objects.isNull(wordXmlTags) || (wordXmlTags.length == 0)) {
         return 0;
      }
      int sum = 0;
      for (var i = 0; i < wordXmlTags.length; i++) {
         sum += wordXmlTags[i].sizeBothTags();
      }
      return sum;
   }

   /**
    * Returns the length of the closing tags specified by <code>wordXmlTags</code>.
    *
    * @param wordXmlTags
    * @return the length of the specified closing tags.
    */

   public static int sizeCloseTags(WordXmlTag... wordXmlTags) {
      if (Objects.isNull(wordXmlTags) || (wordXmlTags.length == 0)) {
         return 0;
      }
      int sum = 0;
      for (var i = 0; i < wordXmlTags.length; i++) {
         sum += wordXmlTags[i].sizeCloseTag();
      }
      return sum;
   }

   /**
    * Returns the length of the opening without attributes tags specified by <code>wordXmlTags</code>.
    *
    * @param wordXmlTags
    * @return the length of the specified opening without attributes tags.
    */

   public static int sizeOpenTags(WordXmlTag... wordXmlTags) {
      if (Objects.isNull(wordXmlTags) || (wordXmlTags.length == 0)) {
         return 0;
      }
      int sum = 0;
      for (var i = 0; i < wordXmlTags.length; i++) {
         sum += wordXmlTags[i].sizeOpenTag();
      }
      return sum;
   }

   /**
    * Saves the {@link WordXmlAttribute}s associated with the Word ML tag.
    */

   private final @NonNull Set<@NonNull WordXmlAttribute> attributes;

   /**
    * Saves the Word ML tag formatted as a closing tag.
    */

   private final @NonNull String closeTag;

   /**
    * Saves the Word ML tag without attributes formatted as an open tag.
    */

   private final @NonNull String openTag;

   /**
    * Saves the regular expression as a {@link String} for the opening tag with any and all attributes. The regular
    * expression does not test for any specific attribute.
    */

   private final @NonNull String regexOpenTag;

   /**
    * Saves the Word ML tag formatted as a self closing tag.
    */

   private final @NonNull String selfCloseTag;

   /**
    * Saves the number of characters in both the opening and closing tags.
    */

   private final int sizeBothTags;

   /**
    * Saves the number of characters in the closing tag.
    */

   private final int sizeCloseTag;

   /**
    * Saves the number of characters in the opening tag.
    */

   private final int sizeOpenTag;

   /**
    * Saves the Word ML namespace and tag name.
    */

   private final @NonNull String tagName;

   /**
    * Creates a new {@link WordXmlTag} without attributes.
    *
    * @param tagName the namespace and name of the Word ML tag.
    * @throws IllegalArgumentException when <code>tagName</code> is <code>null</code> or blank.
    */

   private WordXmlTag(@NonNull String tagName) {
      //@formatter:off
      Validation.require
         (
            tagName,
            Validation.ValueType.PARAMETER,
            "WordXmlTag",
            "new",
            "tagName",
            "cannot be null or blank",
            Strings::isInvalidOrBlank,
            IllegalArgumentException::new
         );
      //@formatter:on

      this.tagName = tagName;
      this.openTag = "<" + this.tagName + ">";
      this.closeTag = "</" + this.tagName + ">";
      this.selfCloseTag = "<" + this.tagName + "/>";
      this.regexOpenTag = "<" + this.tagName + "[^>]*>";
      this.attributes = Set.of();
      this.sizeOpenTag = this.openTag.length();
      this.sizeCloseTag = this.closeTag.length();
      this.sizeBothTags = this.sizeOpenTag + this.sizeCloseTag;
   }

   /**
    * Creates a new {@link WordXmlTag} with attributes.
    *
    * @param tagName the namespace and name of the Word ML tag.
    * @param wordXmlAttributes a set of the {@link WordXmlAttribute}s for this Word XML tag.
    * @throws NullPointerException when:
    * <ul>
    * <li>the parameter <code>tagName</code> is <code>null</code>,</li>
    * <li>the parameter <code>wordXmlAttributes</code> is <code>null</code>, or</li>
    * <li>the parameter <code>wordXmlAttributes</code> contains a <code>null</code> entry.</li>
    * </ul>
    */

   private WordXmlTag(@NonNull String tagName, @NonNull Set<@NonNull WordXmlAttribute> wordXmlAttributes) {
      //@formatter:off
      Validation.require
         (
            tagName,
            Validation.ValueType.PARAMETER,
            "WordXmlTag",
            "new",
            "tagName",
            "cannot be null or blank",
            Strings::isInvalidOrBlank,
            IllegalArgumentException::new
         );

      Validation.require
         (
            wordXmlAttributes,
            Validation.ValueType.PARAMETER,
            "WordXmlTag",
            "new",
            "wordXmlAttributes",
            "cannot be null",
            Objects::isNull,
            "cannot contain null entries",
            Validation::collectionContainsNull,
            NullPointerException::new
         );
      //@formatter:on

      this.tagName = tagName;
      this.openTag = "<" + this.tagName + ">";
      this.closeTag = "</" + this.tagName + ">";
      this.selfCloseTag = "<" + this.tagName + "/>";
      this.regexOpenTag = "<" + this.tagName + "[^>]*(?<!/)>";
      this.attributes = wordXmlAttributes;
      this.sizeOpenTag = this.openTag.length();
      this.sizeCloseTag = this.closeTag.length();
      this.sizeBothTags = this.sizeOpenTag + this.sizeCloseTag;
   }

   /**
    * Gets the Word ML tag formatted as a closing tag.
    *
    * @return the tag formatted as a close tag.
    */

   public @NonNull String getCloseTag() {
      return this.closeTag;
   }

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

   public @NonNull String getTag(@NonNull XmlTagType xmlTagType) {

      Validation.requireNonNull(xmlTagType, "WordXmlTag", "getTag", "xmlTagType");

      switch (xmlTagType) {
         case OPEN:
            return this.openTag;
         case CLOSE:
            return this.closeTag;
         case SELF_CLOSING:
            return this.selfCloseTag;
         default:
            //@formatter:off
            throw
               Validation.invalidCase
                  (
                     xmlTagType,
                     "WordXmlTag",
                     "getTag",
                     "xmlTagType",
                     IllegalArgumentException::new
                  );
            //@formatter:on
      }

   }

   /**
    * Generates an XML tag with attributes.
    *
    * @param xmlTagType the XML tag type (open, self closing) to generate.
    * @param attributes an array of {@link Pair} objects containing the {@link WordXmlAttribute} and the attribute value
    * as a {@link CharSequence}.
    * @return the generated XML tag with attributes.
    */

   private StringBuilder getTagWithAttributes(XmlTagType xmlTagType, Pair<?, ?>... attributes) {

      //@formatter:off
         var stringBuilder =
            new StringBuilder( 1024 )
                   .append( "<" )
                   .append( this.tagName );
         //@formatter:on

      for (var attribute : attributes) {

         if (attribute.typesOk(WordXmlAttribute.class, CharSequence.class)) {
            var wordXmlAttribute = (WordXmlAttribute) attribute.getFirst();
            var value = (CharSequence) attribute.getSecond();
            stringBuilder.append(wordXmlAttribute.getNameWithValue(value));
         }

      }

      switch (xmlTagType) {
         case OPEN:
            stringBuilder.append(">");
            break;
         case SELF_CLOSING:
            stringBuilder.append("/>");
            break;
         default:
            //@formatter:off
            throw
               Validation.invalidCase
                  (
                     xmlTagType,
                     "WordXmlTag",
                     "getOpeningTagWithAttributes",
                     "xmlTagType",
                     IllegalArgumentException::new
                  );
            //@formatter:on
      }

      return stringBuilder;
   }

   /**
    * Gets the Word ML tag formatted as an open tag without attributes.
    *
    * @return the tag formatted as an open tag without attributes.
    */

   public @NonNull String getOpenTag() {
      return this.openTag;
   }

   /**
    * Generates an Word ML open tag with the specified <code>attributes</code>.
    *
    * @param attributes an array of <code>Pair&lt;WordXmlAttribute,CharSequence&gt;</code> attribute specifications.
    * @return a {@link CharSequence} with the generated Word ML open tag with attributes.
    * @throws NullPointerException when <code>attributes</code> is <code>null</code> or contains a <code>null</code>
    * element.
    * @throws IllegalArgumentException when an entry in <code>attributes</code> is not a
    * <code>Pair&lt;WordXmlAttribute,CharSequence&gt;</code> or either member of the {@link Pair} is <code>null</code>.
    */

   public @NonNull CharSequence getOpenTagWithAttributes(@NonNull Pair<?, ?>... attributes) {

      //@formatter:off
      Validation.require
         (
            attributes,
            Validation.ValueType.PARAMETER,
            "WordXmlTag",
            "getOpenTagWithAttributes",
            "attributes",
            "cannot be null or contain null elements",
            Validation::arrayContainsNull,
            NullPointerException::new,
            "attributes must be a Pair<WordXmlAttribute,CharSequence> with non-null entries",
            Validation.arrayElementPredicate
               (
                  ( attributeSpecification ) ->
                         !( ((Pair<?,?>) attributeSpecification).typesOk( WordXmlAttribute.class, CharSequence.class ) )
                      || Objects.isNull( ((Pair<?,?>) attributeSpecification).getFirst() )
                      || Objects.isNull( ((Pair<?,?>) attributeSpecification).getSecond() )
               ),
            IllegalArgumentException::new
         );
      //@formatter:on

      return this.getTagWithAttributes(XmlTagType.OPEN, attributes);
   }

   /**
    * Generates a regular expression string that finds everything between and including the open and closing tag for
    * this {@link WordXmlTag}.
    *
    * @return the regular expression string.
    */

   public @NonNull String getRegexEverythingBetweenTags() {
      //@formatter:off
         return
            new StringBuilder( 128 )
                   .append( "<" )
                   .append( this.tagName )
                   .append( "[\\s\\S]+?")
                   .append( this.closeTag )
                   .toString();
         //@formatter:on
   }

   /**
    * Gets a regular expression string that finds the open tag with any number of attributes. The regular expression
    * does not test for any specific attribute.
    *
    * @return the regular expression string.
    */

   public String getRegexOpenTag() {
      return this.regexOpenTag;
   }

   /**
    * Gets a regular expression string that finds the self closing tag with any number of attributes. The regular
    * expression does not test for any specific attribute.
    *
    * @return the regular expression string.
    */

   public String getSelfCloseTag() {
      return this.selfCloseTag;
   }

   /**
    * Generates a Word ML self closing tag with the specified <code>attributes</code>.
    *
    * @param attributes an array of <code>Pair&lt;WordXmlAttribute,CharSequence&gt;</code> attribute specifications.
    * @return a {@link CharSequence} with the generated Word ML self closing tag with attributes.
    * @throws NullPointerException when <code>attributes</code> is <code>null</code> or contains a <code>null</code>
    * element.
    * @throws IllegalArgumentException when an entry in <code>attributes</code> is not a
    * <code>Pair&lt;WordXmlAttribute,CharSequence&gt;</code> or either member of the {@link Pair} is <code>null</code>.
    */

   public CharSequence getSelfCloseTagWithAttributes(Pair<?, ?>... attributes) {

      //@formatter:off
      Validation.require
         (
            attributes,
            Validation.ValueType.PARAMETER,
            "WordXmlTag",
            "getOpenTagWithAttributes",
            "attributes",
            "cannot be null or contain null elements",
            Validation::arrayContainsNull,
            NullPointerException::new,
            "attributes must be a Pair<WordXmlAttribute,CharSequence> with non-null entries",
            Validation.arrayElementPredicate
               (
                  ( attributeSpecification ) ->
                         !( ((Pair<?,?>) attributeSpecification).typesOk( WordXmlAttribute.class, CharSequence.class ) )
                      || Objects.isNull( ((Pair<?,?>) attributeSpecification).getFirst() )
                      || Objects.isNull( ((Pair<?,?>) attributeSpecification).getSecond() )
               ),
            IllegalArgumentException::new
         );
      //@formatter:on

      return this.getTagWithAttributes(XmlTagType.SELF_CLOSING, attributes);
   }

   /**
    * Gets the Word ML tag name with name space and no other formatting.
    *
    * @return the Word ML tag name with name space.
    */

   public String getTagName() {
      return this.tagName;
   }

   /**
    * Predicate to determine if the provided tag name and name space match the Word ML tag the enumeration member
    * represents.
    *
    * @param tagName a Word ML tag name with name space and no other XML formatting such as '&lt;', '&gt;', and '/'
    * characters.
    * @return <code>true</code> when the <code>tagName</code> matches the XML tag represented by this enumeration
    * member; otherwise, <code>false</code>.
    */

   public boolean isTagName(CharSequence tagName) {
      return this.tagName.contentEquals(tagName);
   }

   /**
    * Predicate to determine if an attribute is valid for the Word ML tag represented by this enumeration member.
    *
    * @param wordXmlAttribute the {@link WordXmlAttribute} to test.
    * @return <code>true</code> when <code>wordXmlAttribute</code> is valid for the Word ML tag represented by the
    * enumeration member; otherwise, <code>false</code>.
    */

   public boolean isValidAttribute(WordXmlAttribute wordXmlAttribute) {
      return this.attributes.contains(wordXmlAttribute);
   }

   /**
    * Gets the number of characters in the {@link WordXmlTag} represented as an open without attributes and close tag.
    *
    * @return the number of characters in the open and close tags.
    */

   public int sizeBothTags() {
      return this.sizeBothTags;
   }

   /**
    * Gets the number of characters in the {@link WordXmlTag} represented as a close tag.
    *
    * @return the number of characters in the close tag.
    */

   public int sizeCloseTag() {
      return this.sizeCloseTag;
   }

   /**
    * Gets the number of characters in the {@link WordXmlTag} represented as an open tag without attributes.
    *
    * @return the number of characters in the open tag.
    */

   public int sizeOpenTag() {
      return this.sizeOpenTag;
   }

}

/* EOF */
