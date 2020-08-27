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

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.TestFrequencyAttributeType.TestFrequencyEnum;

/**
 * @author Stephen J. Molaro
 */
public class TestFrequencyAttributeType extends AttributeTypeEnum<TestFrequencyEnum> {

   public final TestFrequencyEnum OneTime = new TestFrequencyEnum(0, "One Time");
   public final TestFrequencyEnum Recurring = new TestFrequencyEnum(1, "Recurring");

   public TestFrequencyAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847103L, namespace, "Test Frequency", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public TestFrequencyAttributeType() {
      this(NamespaceToken.OSEE, 2);
   }

   public class TestFrequencyEnum extends EnumToken {
      public TestFrequencyEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}