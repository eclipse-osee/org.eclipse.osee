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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public interface ApplicabilityId extends Id {
   public static final ApplicabilityId BASE = ApplicabilityId.valueOf(1L);
   public static final ApplicabilityId SENTINEL = valueOf(Id.SENTINEL);

   public static ApplicabilityId valueOf(String id) {
      return Id.valueOf(id, ApplicabilityId::valueOf);
   }

   public static @NonNull ApplicabilityId valueOf(Long id) {
      final class ApplicabilityIdImpl extends BaseId implements ApplicabilityId {
         public ApplicabilityIdImpl(Long id) {
            super(id);
         }
      }
      return new ApplicabilityIdImpl(id);
   }
}