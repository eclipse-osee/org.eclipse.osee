/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Megumi Telles
 */
@JsonSerialize(using = IdSerializer.class)
public interface ArtifactId extends Identity<String>, Id {

   public static final ArtifactId SENTINEL = ArtifactId.valueOf(Id.SENTINEL);

   default Long getUuid() {
      return getId();
   }

   public static ArtifactId valueOf(Id id) {
      if (id instanceof ArtifactId) {
         return (ArtifactId) id;
      }
      return valueOf(id.getId());
   }

   public static ArtifactId valueOf(String id) {
      return valueOf(Long.valueOf(id));
   }

   public static ArtifactId valueOf(long id) {
      final class ArtifactIdImpl extends BaseId implements ArtifactId, Comparable<ArtifactId> {
         public ArtifactIdImpl(Long artId) {
            super(artId);
         }

         @Override
         public String getGuid() {
            return null;
         }

         @Override
         public int compareTo(ArtifactId o) {
            return getId().compareTo(o.getId());
         }
      }
      return new ArtifactIdImpl(id);
   }

   @Override
   default Long getId() {
      return getUuid();
   }
}