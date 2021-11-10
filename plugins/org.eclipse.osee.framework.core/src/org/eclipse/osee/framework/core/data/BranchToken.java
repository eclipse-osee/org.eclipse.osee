/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public interface BranchToken extends BranchId, NamedId {
   static final int SHORT_NAME_LIMIT = 35;
   BranchToken SENTINEL = create(Id.SENTINEL, Named.SENTINEL);

   default String getShortName() {
      return getShortName(SHORT_NAME_LIMIT);
   }

   default String getShortName(int length) {
      return Strings.truncate(getName(), length);
   }

   public static BranchToken create(String name) {
      return create(Lib.generateUuid(), name);
   }

   public static BranchToken create(BranchId id, String name) {
      if (id instanceof BranchToken) {
         return (BranchToken) id;
      }
      return create(id.getId(), name, ArtifactId.SENTINEL);
   }

   public static BranchToken valueOf(BranchId id) {
      if (id instanceof BranchToken) {
         return (BranchToken) id;
      }
      return create(id.getId(), "Not Loaded", ArtifactId.SENTINEL);
   }

   public static BranchToken create(long id, String name) {
      return create(Long.valueOf(id), name, ArtifactId.SENTINEL);
   }

   public static BranchToken create(Long id, String name, ArtifactId viewId) {
      final class BranchTokenImpl extends NamedIdBase implements BranchToken {
         private final ArtifactId viewId;

         public BranchTokenImpl(Long id, String name, ArtifactId viewId) {
            super(id, name);
            this.viewId = viewId;
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

         @Override
         public String toStringWithId() {
            return String.format("[%s]-[%s]", getName(), getId());
         }

      }
      return new BranchTokenImpl(id, name, viewId);
   }

   public static BranchToken create(Long id, String name) {
      return create(id, name, ArtifactId.SENTINEL);
   }

}