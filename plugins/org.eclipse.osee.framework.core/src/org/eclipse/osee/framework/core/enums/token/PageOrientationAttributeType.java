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
import org.eclipse.osee.framework.core.enums.token.PageOrientationAttributeType.PageOrientationEnum;

/**
 * @author Stephen J. Molaro
 */
public class PageOrientationAttributeType extends AttributeTypeEnum<PageOrientationEnum> {

   public final PageOrientationEnum Portrait = new PageOrientationEnum(0, "Portrait");
   public final PageOrientationEnum Landscape = new PageOrientationEnum(1, "Landscape");

   public PageOrientationAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847091L, namespace, "Page Orientation", mediaType, "Page Orientation: Landscape/Portrait",
         taggerType, 2);
   }

   public class PageOrientationEnum extends EnumToken {
      public PageOrientationEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}