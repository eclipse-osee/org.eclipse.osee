/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */

@JsonSerialize(using = BranchIdSerializer.class)
@JsonDeserialize(using = BranchIdDeserializer.class)
public interface BranchId extends Identity<Long>, Id {
   BranchId SENTINEL = valueOf(Id.SENTINEL);

   default Long getUuid() {
      return getId();
   }

   @Override
   default Long getGuid() {
      return getId();
   };

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

         @Override
         public boolean equals(Object obj) {
            if (obj instanceof BranchId) {
               return super.equals(obj) && viewId.equals(((BranchId) obj).getViewId());
            }
            return false;
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
}
