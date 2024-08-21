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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

class AbstractXmlNameSpecification extends AbstractXmlNamespaceSpecification {

   final @NonNull String name;
   final @NonNull String fullName;

   AbstractXmlNameSpecification(@NonNull AbstractXmlNamespaceSpecification abstractXmlNamespaceSpecification, @NonNull String name) {
      //@formatter:off
      this
         (
            Conditions.requireNonNull(abstractXmlNamespaceSpecification, "abstractXmlNamespaceSpecification").prefix,
            abstractXmlNamespaceSpecification.uri,
            name
         );
      //@formatter:on
   }

   AbstractXmlNameSpecification(@NonNull String prefix, @Nullable String uri, @NonNull String name) {

      super(Conditions.requireNonNull(prefix, "prefix"), uri);
      //@formatter:off
      this.name =
         Conditions.require
            (
               name,
               Conditions.ValueType.PARAMETER,
               "name",
               "cannot be null or blank",
               Strings::isInvalidOrBlank,
               IllegalArgumentException::new
            );
      //@formatter:on
      this.fullName = this.prefix + ":" + name;
   }

   AbstractXmlNameSpecification(@NonNull String prefix, @NonNull String name) {
      this(prefix, null, name);
   }

}
