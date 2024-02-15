/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

@JsonSerialize(using = BranchIdSerializer.class)
@JsonDeserialize(using = BranchIdDeserializer.class)
public interface BranchId extends Id {
   BranchId SENTINEL = valueOf(Id.SENTINEL);

   public static BranchId valueOf(String id) {
      return Id.valueOf(id, BranchId::valueOf);
   }

   public static BranchId create(Long id, ArtifactId view) {
      final class BranchIdImpl extends BaseId implements BranchId {
         private final ArtifactId viewId;

         public BranchIdImpl(Long id, ArtifactId view) {
            super(id);
            this.viewId = view;
         }

         @Override
         public ArtifactId getViewId() {
            return viewId;
         }

         /**
          * Compares both the branch identifier and view artifact identifier of this {@link BranchId} implementation and
          * of <code>obj</code>.
          *
          * @param obj the {@link Object} to be compared to.
          * @return <code>true</code> when <code>obj</code> is an instance of <code>BranchId</code>, the branch
          * identifier of this {@link BranchId} equals the {@link BranchId} of <code>obj</code>, and the view
          * {@link ArtifactId} of this {@link BranchId} equals the view {@link ArtifactId} of <code>obj</code>;
          * otherwise, <code>false</code>.
          * @implNote This implementation requires the implementation of {@link BaseId} to never allow a
          * <code>null</code> value for the member {@link #id}.
          * @implNote The value of the member {@link #view} may be <code>null</code>.
          */

         @SuppressWarnings("unlikely-arg-type")
         @Override
         public boolean equals(Object obj) {

            if (!(obj instanceof BranchId)) {
               return false;
            }

            final var otherBranchId = (BranchId) obj;

            if (!super.equals(otherBranchId)) {
               return false;
            }

            final var otherViewId = otherBranchId.getViewId();

            if (Objects.isNull(this.viewId) && Objects.isNull(otherViewId)) {
               return true;
            }

            if (Objects.isNull(this.viewId)) {
               return false;
            }

            return this.viewId.equals(otherViewId);
         }

         /**
          * {@inheritDoc}
          *
          * @throws NullPointerException when <code>other</code> is <code>null</code>.
          * @implNote This implementation requires the implementation of {@link BaseId} to never allow a
          * <code>null</code> value for the member {@link #id}.
          */

         @Override
         public boolean isSameBranch(BranchId other) {
            return this.id.equals(Objects.requireNonNull(other).getId());
         }
      }

      return new BranchIdImpl(id, view);
   }

   public static BranchId valueOf(Long id) {
      return create(id, ArtifactId.SENTINEL);
   }

   public static BranchId create() {
      return valueOf(Lib.generateUuid());
   }

   default ArtifactId getViewId() {
      return ArtifactId.SENTINEL;
   }

   /**
    * Compares only the branch identifier of this {@link BranchId} and of <code>other</code>.
    *
    * @param other the {@link BranchId} to be compared.
    * @return <code>true</code> when the branch identifier of this {@link BranchId} equals the branch identifier of
    * <code>other</code>; otherwise <code>false</code>.
    * @throws NullPointerException when:
    * <ul>
    * <li>{@link BranchId#getId()} for <code>this</code> returns <code>null</code>.</li>
    * <li>The <code>other</code> {@link BranchId} is <code>null</code>.</li>
    * <li>{@link BranchId#getId()} of <code>other</code> returns <code>null</code>.</li>
    * </ul>
    */

   default boolean isSameBranch(BranchId other) {
      final var thisId = Objects.requireNonNull(this.getId());
      final var otherId = Objects.requireNonNull(Objects.requireNonNull(other).getId());
      return thisId.equals(otherId);
   }
}
