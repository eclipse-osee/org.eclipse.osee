/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

   // @formatter:off
	public final PageOrientationEnum Portrait = new PageOrientationEnum(0, "Portrait");
	public final PageOrientationEnum Landscape = new PageOrientationEnum(1, "Landscape");
	// @formatter:on

   public PageOrientationAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847091L, namespace, "Page Orientation", mediaType, "Page Orientation: Landscape/Portrait",
         taggerType);
   }

   public class PageOrientationEnum extends EnumToken {
      public PageOrientationEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
