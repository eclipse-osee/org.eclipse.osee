/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import org.eclipse.osee.framework.core.data.DynamicEnumAttributeType.DynamicEnum;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Ryan D. Brooks
 */
public final class DynamicEnumAttributeType extends AttributeTypeEnum<DynamicEnum> {
   public final DynamicEnum Unspecified = new DynamicEnum(0, "Unspecified");

   public DynamicEnumAttributeType(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType) {
      super(id, namespace, name, mediaType, description, taggerType, 15);
   }

   public class DynamicEnum extends EnumToken {
      public DynamicEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}