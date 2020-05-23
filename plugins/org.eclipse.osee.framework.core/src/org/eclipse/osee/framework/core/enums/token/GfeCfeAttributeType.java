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
import org.eclipse.osee.framework.core.enums.token.GfeCfeAttributeType.GfeCfeEnum;

/**
 * @author Stephen J. Molaro
 */
public class GfeCfeAttributeType extends AttributeTypeEnum<GfeCfeEnum> {

   public final GfeCfeEnum CFE = new GfeCfeEnum(0, "CFE");
   public final GfeCfeEnum GFE = new GfeCfeEnum(1, "GFE");
   public final GfeCfeEnum Unspecified = new GfeCfeEnum(2, "Unspecified");

   public GfeCfeAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847144L, namespace, "GFE / CFE", mediaType, "", taggerType, 3);
   }

   public class GfeCfeEnum extends EnumToken {
      public GfeCfeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}