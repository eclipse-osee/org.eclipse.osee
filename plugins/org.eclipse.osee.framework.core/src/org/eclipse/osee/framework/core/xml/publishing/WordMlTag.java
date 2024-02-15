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

import java.util.Optional;
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Enumeration of Word ML tag names with namespace.
 */

public enum WordMlTag implements XmlTagSpecification {
   //@formatter:off

      /**
       * XML tag for the Word document body element.
       */

      BODY
         (
            WordXmlNamespace.WORDML,
            "body"
         ),

      /**
       * XML tag for bold
       */

      BOLD
         (
            WordXmlNamespace.WORDML,
            "b"
         ),

      /**
       * XML tag for a color specification.
       */

      COLOR
         (
            WordXmlNamespace.WORDML,
            "color",
            Set.of
               (
                  WordMlAttribute.VALUE
               )
         ),

      /**
       * XML tag for the field character element.
       */

      FIELD_CHARACTER
         (
            WordXmlNamespace.WORDML,
            "fldChar",
            Set.of
               (
                  WordMlAttribute.FIELD_CHARACTER_TYPE
               )
         ),

      /**
       * XML tag for the simple field element.
       */

      FIELD_SIMPLE
         (
            WordXmlNamespace.WORDML,
            "fldSimple",
            Set.of
               (
                  WordMlAttribute.INSTRUCTION
               )
         ),

      /**
       * XML tag for a page footer
       */

      FOOTER
         (
            WordXmlNamespace.WORDML,
            "ftr"
         ),

      /**
       * XML tag for a hard break.
       */

      HARD_BREAK
         (
            WordXmlNamespace.WORDML,
            "br",
            Set.of
               (
                  WordMlAttribute.TYPE
               )
         ),

      /**
       * XML tag for a page header
       */

      HEADER
         (
            WordXmlNamespace.WORDML,
            "hdr"
         ),

      /**
       * XML tag for the Word document hyper-link element.
       */

      HLINK
         (
            WordXmlNamespace.WORDML,
            "hlink",
            Set.of
               (
                  WordMlAttribute.DESTINATION
               )
         ),

      /**
       * XML tag for the instruction text element.
       */

      INSTRUCTION_TEXT
         (
            WordXmlNamespace.WORDML,
            "instrText"
         ),

      /**
       * XML tag for a list presentation element.
       */

      LIST_PRESENTATION
         (
            WordXmlNamespace.WORDML,
            "listPr"
         ),

      /**
       * XML tag of a nfc element.
       */

      NFC
         (
            WordXmlNamespace.WORDML,
            "nfc",
            Set.of
               (
                  WordMlAttribute.VALUE
               )
         ),

      /**
       * XML tag for a non-breaking hyphen.
       */

      NO_BREAK_HYPHEN
         (
            WordXmlNamespace.WORDML,
            "noBreakHyphen"
         ),

      /**
       * XML tag for no proof reading
       */

      NO_PROOF
         (
            WordXmlNamespace.WORDML,
            "noProof"
         ),

      /**
       * XML tag for OLE data
       */

      OLE_DATA
         (
            WordXmlNamespace.WORDML,
            "docOleData"
         ),

      /**
       * XML tag for page margins.
       */

      PAGE_MARGINS
         (
            WordXmlNamespace.WORDML,
            "pgMar",
            Set.of
               (
                  WordMlAttribute.BOTTOM,
                  WordMlAttribute.FOOTER,
                  WordMlAttribute.GUTTER,
                  WordMlAttribute.HEADER,
                  WordMlAttribute.LEFT,
                  WordMlAttribute.RIGHT,
                  WordMlAttribute.TOP
               )
         ),

      /**
       * XML tag for a page size.
       */

      PAGE_SIZE
         (
            WordXmlNamespace.WORDML,
            "pgSz",
            Set.of
               (
                  WordMlAttribute.CODE,
                  WordMlAttribute.HEIGHT,
                  WordMlAttribute.ORIENTATION,
                  WordMlAttribute.WIDTH
               )
         ),

      /**
       * XML tag for a Word document paragraph element.
       */

      PARAGRAPH
         (
            WordXmlNamespace.WORDML,
            "p"
         ),

      /**
       * XML tag for a paragraph presentation element.
       */

      PARAGRAPH_PRESENTATION
         (
            WordXmlNamespace.WORDML,
            "pPr"
         ),

      /**
       * XML tag for a paragraph style.
       */

      PARAGRAPH_STYLE
         (
            WordXmlNamespace.WORDML,
            "pStyle",
            Set.of
               (
                  WordMlAttribute.VALUE
               )
         ),

      /**
       * XML tag for a Word document paragraph run element.
       */

      RUN
         (
            WordXmlNamespace.WORDML,
            "r"
         ),

      /**
       * XML tag for a run presentation element.
       */

      RUN_PRESENTATION
         (
            WordXmlNamespace.WORDML,
            "rPr"
         ),

      /**
       * XML tag for a Word run style element.
       */

      RUN_STYLE
         (
            WordXmlNamespace.WORDML,
            "rStyle",
            Set.of
               (
                  WordMlAttribute.VALUE
               )
         ),

      /**
       * XML tag for a Word document section element.
       */

      SECTION
         (
            WordXmlNamespace.WORDML,
            "sect"
         ),

      /**
       * XML tag for a section presentation element.
       */

      SECTION_PRESENTATION
         (
            WordXmlNamespace.WORDML,
            "sectPr"
         ),

      /**
       * XML tag for a start element.
       */

      START
         (
            WordXmlNamespace.WORDML,
            "start",
            Set.of
               (
                  WordMlAttribute.VALUE
               )
         ),

      /**
       * XML tag for a styles element.
       */

      STYLES
         (
            WordXmlNamespace.WORDML,
            "styles"
         ),

      /**
       * XML tag for a Word document table element.
       */

      TABLE
         (
            WordXmlNamespace.WORDML,
            "tbl"
         ),

      /**
       * XML tag for a Word document table column element.
       */

      TABLE_COLUMN
         (
            WordXmlNamespace.WORDML,
            "tc"
         ),

      /**
       * XML tag for a Word document table row element.
       */

      TABLE_ROW
         (
            WordXmlNamespace.WORDML,
            "tr"
         ),

      /**
       * XML tag for a Word document text element.
       */

      TEXT
         (
            WordXmlNamespace.WORDML,
            "t",
            Set.of
               (
                  XmlAttribute.SPACE
               )
         ),

      /**
       * XML tag for a page type.
       */

      TYPE
         (
            WordXmlNamespace.WORDML,
            "type",
            Set.of
               (
                  WordMlAttribute.VALUE
               )
         ),

      /**
       * XML tag ??
       */

      VANISH
         (
            WordXmlNamespace.WORDML,
            "vanish"
         ),

      /**
       * XML tag for the "wordDocument" element.
       */

      WORD_DOCUMENT
         (
            WordXmlNamespace.WORDML,
            "wordDocument"
         );
      //@formatter:on

   private final AbstractXmlTagSpecification abstractTagSpecification;

   /**
    * Creates a new {@link WordMlTag} without attributes.
    *
    * @param tagName the namespace and name of the Word ML tag.
    * @throws IllegalArgumentException when <code>tagName</code> is <code>null</code> or blank.
    */

   private WordMlTag(@NonNull XmlNamespaceSpecification xmlNamespaceSpecification, @NonNull String tagName) {
      this(xmlNamespaceSpecification, tagName, Set.of());
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

   private WordMlTag(@NonNull XmlNamespaceSpecification xmlNamespaceSpecification, @NonNull String tagName,
      @NonNull Set<@NonNull XmlAttributeSpecification> xmlAttributeSpecifications) {
      //@formatter:off
      this.abstractTagSpecification =
         new AbstractXmlTagSpecification
                (
                   new AbstractXmlNamespaceSpecification
                          (
                             xmlNamespaceSpecification.getPrefix(),
                             xmlNamespaceSpecification.getUri().orElse( null )
                          ),
                   tagName,
                   xmlAttributeSpecifications
                );
      //@formatter:on
   }

   @Override
   public String getName() {
      return this.abstractTagSpecification.name;
   }

   @Override
   public String getFullname() {
      return this.abstractTagSpecification.fullName;
   }

   @Override
   public String getPrefix() {
      return this.abstractTagSpecification.prefix;
   }

   @Override
   public Optional<String> getUri() {
      return Optional.ofNullable(this.abstractTagSpecification.uri);
   }

   @Override
   public Set<XmlAttributeSpecification> getAllowedAttributes() {
      return this.abstractTagSpecification.attributes;
   }

   @Override
   public @NonNull String getCloseTag() {
      return this.abstractTagSpecification.closeTag;
   }

   @Override
   public @NonNull String getOpenTag() {
      return this.abstractTagSpecification.openTag;
   }

   @Override
   public @NonNull String getSelfCloseTag() {
      return this.abstractTagSpecification.selfCloseTag;
   }

   @Override
   public String getRegexOpenTag() {
      return this.abstractTagSpecification.regexOpenTag;
   }

   @Override
   public int sizeCloseTag() {
      return this.abstractTagSpecification.sizeCloseTag;
   }

   @Override
   public int sizeOpenTag() {
      return this.abstractTagSpecification.sizeOpenTag;
   }

   @Override
   public int sizeSelfCloseTag() {
      return this.abstractTagSpecification.sizeSelfCloseTag;
   }

   //-----------------------------------------------------------

   /**
    * Generates a regular expression string that finds everything between and including the open and closing tag for
    * this {@link WordMlTag}.
    *
    * @return the regular expression string.
    */

   @Override
   public @NonNull String getRegexEverythingBetweenTags() {
      //@formatter:off
         return
            new StringBuilder( 128 )
                   .append( "<" )
                   .append( this.abstractTagSpecification.fullName )
                   .append( "[\\s\\S]+?")
                   .append( this.getCloseTag() )
                   .toString();
         //@formatter:on
   }

}

/* EOF */
