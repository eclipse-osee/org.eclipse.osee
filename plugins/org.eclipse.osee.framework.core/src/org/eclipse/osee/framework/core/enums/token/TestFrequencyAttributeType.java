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
import org.eclipse.osee.framework.core.enums.token.TestFrequencyAttributeType.TestFrequencyEnum;

/**
 * @author Stephen J. Molaro
 */
public class TestFrequencyAttributeType extends AttributeTypeEnum<TestFrequencyEnum> {

   // @formatter:off
	public final TestFrequencyEnum OneTime = new TestFrequencyEnum(0, "One Time");
	public final TestFrequencyEnum Recurring = new TestFrequencyEnum(1, "Recurring");
	// @formatter:on

   public TestFrequencyAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847103L, namespace, "Test Frequency", mediaType, "", taggerType);
   }

   public class TestFrequencyEnum extends EnumToken {
      public TestFrequencyEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
