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
import org.eclipse.osee.framework.core.enums.token.TisTestCategoryAttributeType.TisTestCategoryEnum;

/**
 * @author Stephen J. Molaro
 */
public class TisTestCategoryAttributeType extends AttributeTypeEnum<TisTestCategoryEnum> {

   // @formatter:off
	public final TisTestCategoryEnum SPEC_COMP = new TisTestCategoryEnum(0, "SPEC_COMP");
	public final TisTestCategoryEnum DEV = new TisTestCategoryEnum(1, "DEV");
	public final TisTestCategoryEnum USG = new TisTestCategoryEnum(2, "USG");
	// @formatter:on

   public TisTestCategoryAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847119L, namespace, "TIS Test Category", mediaType, "TIS Test Category", taggerType);
   }

   public class TisTestCategoryEnum extends EnumToken {
      public TisTestCategoryEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
