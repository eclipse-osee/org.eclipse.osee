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
import org.eclipse.osee.framework.core.enums.token.FeatureValueAttributeType.FeatureValueTypeEnum;

/**
 * @author Stephen J. Molaro
 */
public class FeatureValueAttributeType extends AttributeTypeEnum<FeatureValueTypeEnum> {

   // @formatter:off
	public final FeatureValueTypeEnum String = new FeatureValueTypeEnum(0, "String");
	public final FeatureValueTypeEnum Boolean = new FeatureValueTypeEnum(1, "Boolean");
	public final FeatureValueTypeEnum Decimal = new FeatureValueTypeEnum(2, "Decimal");
	public final FeatureValueTypeEnum Integer = new FeatureValueTypeEnum(3, "Integer");
	// @formatter:on

   public FeatureValueAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(31669009535111027L, namespace, "Feature Value Type", mediaType, "", taggerType);
   }

   public class FeatureValueTypeEnum extends EnumToken {
      public FeatureValueTypeEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
