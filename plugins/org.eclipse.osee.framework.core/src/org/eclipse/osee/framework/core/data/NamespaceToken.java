/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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