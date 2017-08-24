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

/**
 * @author Megumi Telles
 */
@JsonSerialize(using = IdSerializer.class)
public interface AttributeId extends HasLocalId<Integer>, Id {
   AttributeId SENTINEL = valueOf(Id.SENTINEL);

   public static AttributeId valueOf(String id) {
      if (id == null) {
         return valueOf((Long) null);
      }
      return valueOf(Long.valueOf(id));
   }

   String UNSPECIFIED = "Unspecified";

   @Override
   default Integer getLocalId() {
      return getId().intValue();
   }

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