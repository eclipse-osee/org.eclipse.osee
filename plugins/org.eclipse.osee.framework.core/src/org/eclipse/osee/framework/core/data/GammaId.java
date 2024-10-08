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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

/**
 * @author Ryan D. Brooks
 */
@JsonSerialize(using = IdSerializer.class)
public interface GammaId extends Id {
   GammaId SENTINEL = valueOf(Id.SENTINEL);

   public static GammaId valueOf(String id) {
      return Id.valueOf(id, GammaId::valueOf);
   }

   public static GammaId valueOf(int id) {
      return valueOf(Long.valueOf(id));
   }

   public static GammaId valueOf(Long id) {
      final class GammaIdImpl extends BaseId implements GammaId {
         public GammaIdImpl(Long id) {
            super(id);
         }
      }
      return new GammaIdImpl(id);
   }
}