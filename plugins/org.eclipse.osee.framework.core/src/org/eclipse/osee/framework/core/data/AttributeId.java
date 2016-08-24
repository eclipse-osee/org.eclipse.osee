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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.IdSerializer;

/**
 * @author Megumi Telles
 */
@JsonSerialize(using = IdSerializer.class)
public interface AttributeId extends HasLocalId<Integer>, Id {

   public static AttributeId valueOf(String id) {
      return valueOf(Long.valueOf(id));
   }

   @JsonCreator
   public static AttributeId valueOf(long id) {
      final class AttributeToken extends BaseId implements AttributeId {
         public AttributeToken(Long txId) {
            super(txId);
         }

         @Override
         public Integer getLocalId() {
            return getId().intValue();
         }
      }
      return new AttributeToken(id);
   }
}