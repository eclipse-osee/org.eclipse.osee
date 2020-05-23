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
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

/**
 * @author Megumi Telles
 */
@JsonSerialize(using = IdSerializer.class)
public interface AttributeId extends Id {
   AttributeId SENTINEL = valueOf(Id.SENTINEL);

   public static AttributeId valueOf(String id) {
      if (id == null) {
         return valueOf((Long) null);
      }
      return valueOf(Long.valueOf(id));
   }

   String UNSPECIFIED = "Unspecified";

   public static AttributeId valueOf(int id) {
      return valueOf(Long.valueOf(id));
   }

   public static AttributeId valueOf(Long id) {
      final class AttributeIdImpl extends BaseId implements AttributeId {
         public AttributeIdImpl(Long txId) {
            super(txId);
         }
      }
      return new AttributeIdImpl(id);
   }
}