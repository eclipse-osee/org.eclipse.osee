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

import java.util.Optional;
import java.util.Set;
import org.eclipse.jdt.annotation.NonNull;

public enum AnnotationMlTag implements XmlTagSpecification {

   //@formatter:off
   ANNOTATION
      (
         WordXmlNamespace.ANNOTATION_MARKUP_LANGUAGE,
         "annotation",
         Set.of
            (
               AnnotationMlAttribute.IDENTIFIER,
               WordMlAttribute.NAME,
               WordMlAttribute.TYPE
            )
      );
   //@formatter:on

   private final AbstractXmlTagSpecification abstractTagSpecification;

   /**
    * Creates a new {@link WordMlTag} without attributes.
    *
    * @param tagName the namespace and name of the Word ML tag.
    * @throws IllegalArgumentException when <code>tagName</code> is <code>null</code> or blank.
    */

   private AnnotationMlTag(@NonNull XmlNamespaceSpecification xmlNamespaceSpecification, @NonNull String tagName) {
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

   private AnnotationMlTag(@NonNull XmlNamespaceSpecification xmlNamespaceSpecification, @NonNull String tagName, @NonNull Set<@NonNull XmlAttributeSpecification> xmlAttributeSpecifications) {
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
   public String getRegexOpenTag() {
      return this.abstractTagSpecification.regexOpenTag;
   }

   @Override
   public @NonNull String getSelfCloseTag() {
      return this.abstractTagSpecification.selfCloseTag;
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

}
