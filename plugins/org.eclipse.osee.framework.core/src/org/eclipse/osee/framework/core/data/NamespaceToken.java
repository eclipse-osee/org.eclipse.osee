/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdDescription;

/**
 * @author Ryan D. Brooks
 */
public interface NamespaceToken extends NamedId, HasDescription {
   NamespaceToken SENTINEL = valueOf(Id.SENTINEL, Named.SENTINEL, "");
   NamespaceToken OSEE = valueOf(1, "osee", "Namespace for core system and content management types");

   public static NamespaceToken valueOf(long id, String name, String description) {
      final class ArtifactTypeTokenImpl extends NamedIdDescription implements NamespaceToken {

         public ArtifactTypeTokenImpl(Long id, String name, String description) {
            super(id, name, description);
         }
      }
      return new ArtifactTypeTokenImpl(id, name, description);
   }
}