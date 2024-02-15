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

public enum AuxHintAttribute implements XmlAttributeSpecification {

   VALUE("val");

   private final AbstractXmlNameSpecification abstractXmlNameSpecification;

   AuxHintAttribute(String name) {
      //@formatter:off
      this.abstractXmlNameSpecification =
         new AbstractXmlNameSpecification
                (
                   new AbstractXmlNamespaceSpecification
                   (
                      WordXmlNamespace.AUX_HINT.getPrefix(),
                      WordXmlNamespace.AUX_HINT.getUri().orElse( null )
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
