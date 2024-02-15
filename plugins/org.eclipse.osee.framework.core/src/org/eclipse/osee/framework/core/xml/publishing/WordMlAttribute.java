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

/**
 * Enumeration of Word ML attribute names. The attributes are assigned to Word ML tags by the enumeration
 * {@link WordMlTag}.
 *
 * @author Loren K. Ashley
 */

public enum WordMlAttribute implements XmlAttributeSpecification {

   BOTTOM("bottom"),
   CODE("code"),
   DESTINATION("dest"),
   FIELD_CHARACTER_TYPE("fldCharType"),
   FOOTER("footer"),
   GUTTER("gutter"),
   HEADER("header"),
   HEIGHT("h"),
   INSTRUCTION("instr"),
   LEFT("left"),
   NAME("name"),
   ORIENTATION("orient"),
   RIGHT("right"),
   TOP("top"),
   TYPE("type"),
   VALUE("val"),
   WIDTH("w");

   private final AbstractXmlNameSpecification abstractXmlNameSpecification;

   WordMlAttribute(String name) {
      //@formatter:off
      this.abstractXmlNameSpecification =
         new AbstractXmlNameSpecification
                (
                   new AbstractXmlNamespaceSpecification
                   (
                      WordXmlNamespace.WORDML.getPrefix(),
                      WordXmlNamespace.WORDML.getUri().orElse( null )
                   ),
                   name
                );
      //@formatter:on
   }

   @Override
   public String getName() {
      return this.abstractXmlNameSpecification.name;
   }

   @Override
   public String getFullname() {
      return this.abstractXmlNameSpecification.fullName;
   }

   @Override
   public String getPrefix() {
      return this.abstractXmlNameSpecification.prefix;
   }

   @Override
   public Optional<String> getUri() {
      return Optional.ofNullable(this.abstractXmlNameSpecification.uri);
   }

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */