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

public enum XmlAttribute implements XmlAttributeSpecification {

   SPACE("space");

   private final AbstractXmlNameSpecification abstractXmlNameSpecification;

   XmlAttribute(String name) {
      //@formatter:off
      this.abstractXmlNameSpecification =
         new AbstractXmlNameSpecification
                (
                   new AbstractXmlNamespaceSpecification
                   (
                      WordXmlNamespace.XML.getPrefix()
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
