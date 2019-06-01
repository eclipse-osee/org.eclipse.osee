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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Megumi Telles
 */
@JsonSerialize(using = IdSerializer.class)
public interface ArtifactId extends Id {

   public static final ArtifactId SENTINEL = valueOf(Id.SENTINEL);

   default Long getUuid() {
      return getId();
   }

   public static ArtifactId valueOf(String id) {
      Conditions.assertTrue(Strings.isNumeric(id), "id is not numberic [%s]", id);
      return Id.valueOf(id, ArtifactId::valueOf);
   }

   public static ArtifactId valueOf(Id id) {
      if (id instanceof ArtifactId) {
         return (ArtifactId) id;
      }
      return valueOf(id.getId());
   }

   public static ArtifactId valueOf(int id) {
      return valueOf(Long.valueOf(id));
   }

   public static ArtifactId valueOf(Long id) {
      final class ArtifactIdImpl extends BaseId implements ArtifactId, Comparable<ArtifactId> {
         private final Long id;

         @Override
         public Long getId() {
            return id;
         }

         public ArtifactIdImpl(Long artId) {
            super(artId);
            this.id = artId;
         }

         @Override
         public int compareTo(ArtifactId o) {
            return getId().compareTo(o.getId());
         }
      }
      return new ArtifactIdImpl(id);
   }

}