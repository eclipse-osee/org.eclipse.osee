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
import org.eclipse.osee.framework.core.enums.token.ComponentAttributeType.ComponentEnum;

/**
 * @author Stephen J. Molaro
 */
public class ComponentAttributeType extends AttributeTypeEnum<ComponentEnum> {

   public final ComponentEnum TopLevelProductComponent = new ComponentEnum(0, "Top level product component");
   public final ComponentEnum Unspecified = new ComponentEnum(1, "Unspecified");

   public ComponentAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847125L, namespace, "Component", mediaType, "", taggerType, 2);
   }

   public class ComponentEnum extends EnumToken {
      public ComponentEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}