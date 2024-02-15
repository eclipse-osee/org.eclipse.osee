/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.jdt.annotation.NonNull;
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

   public static ArtifactId valueOf(String id) {
      Conditions.assertTrue(Strings.isNumeric(id), "id is not numeric [%s]", id);
      return Id.valueOf(id, ArtifactId::valueOf);
   }

   /**
    * When the {@link Id} implementation is an instance of an {@link ArtifactId} it is cast to an {@link ArtifactId} and
    * returned. When the {@link Id} implementation is not an instance of an {@link ArtifactId} the numeric identifier is
    * extracted from the {@link Id} implementation and a new {@link ArtifactId} is created with just the numeric
    * identifier.
    *
    * @param id the {@link Id} implementation to create a new {@link ArtifactId} from or to be casted to an
    * {@link ArtifactId}.
    * @return the <code>id</code> when it is an implementation of an {@link ArtifactId}; otherwise, a new
    * {@link ArtifactId} with just the numeric identifier from <code>id</code>.
    * @throws NullPointerException when <code>artifact</code> is <code>null</code>.
    * @implNote This method is not type safe in that it does not prevent the creation of an {@link ArtifactId} from
    * other identifier types such as {@link BranchId}, {@link TransactionId}, etc.
    */

   public static ArtifactId valueOf(Id id) {
      if (id instanceof ArtifactId) {
         return (ArtifactId) id;
      }
      return valueOf(id.getId());
   }

   public static ArtifactId valueOf(int id) {
      return valueOf(Long.valueOf(id));
   }

   /**
    * Always returns a new ArtifactId even though the argument passed in is already an ArtifactToken (at least). This is
    * used in the special case where the added information in the argument is undesirable. Such a case occurs with
    * Artifact where its equals method takes into account the branchId if the object being compared to it implements
    * HasBranch. Another possible case is with JSON serialization.
    *
    * @param artifact the {@link Id} implementation to create an {@ArtifactId} implementation from that only contains
    * the numeric identifier.
    * @return an {@link ArtifactId} implementation that only contains the numeric identifier.
    * @throws NullPointerException when <code>artifact</code> is <code>null</code>.
    * @implNote This method is not type safe in that it does not prevent the creation of an {@link ArtifactId} from
    * other identifier types such as {@link BranchId}, {@link TransactionId}, etc.
    */

   public static ArtifactId create(Id artifact) {
      return valueOf(artifact.getId());
   }

   @SuppressWarnings("ComparableType")
   public static @NonNull ArtifactId valueOf(Long id) {
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