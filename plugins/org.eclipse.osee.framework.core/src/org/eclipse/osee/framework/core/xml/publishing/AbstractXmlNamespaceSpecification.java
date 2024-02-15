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

class AbstractXmlNamespaceSpecification {

   final @NonNull String prefix;
   final @Nullable String uri;

   AbstractXmlNamespaceSpecification(@NonNull String prefix, @Nullable String uri) {
      this.prefix = Conditions.requireNonNull(prefix, "prefix");
      this.uri = uri;
   }

   AbstractXmlNamespaceSpecification(@NonNull String prefix) {
      this.prefix = Conditions.requireNonNull(prefix, "prefix");
      this.uri = null;
   }

}
