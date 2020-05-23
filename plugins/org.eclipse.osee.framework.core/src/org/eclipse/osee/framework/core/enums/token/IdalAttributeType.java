/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.enums.token;

import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.IdalAttributeType.IdalEnum;

/**
 * @author Stephen J. Molaro
 */
public class IdalAttributeType extends AttributeTypeEnum<IdalEnum> {

   public final IdalEnum A = new IdalEnum(0, "A");
   public final IdalEnum B = new IdalEnum(1, "B");
   public final IdalEnum C = new IdalEnum(2, "C");
   public final IdalEnum D = new IdalEnum(3, "D");
   public final IdalEnum E = new IdalEnum(4, "E");
   public final IdalEnum Unspecified = new IdalEnum(5, "Unspecified");

   public IdalAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(2612838829556295211L, namespace, "IDAL", mediaType, "Item Development Assurance Level", taggerType, 6);
   }

   public class IdalEnum extends EnumToken {
      public IdalEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}