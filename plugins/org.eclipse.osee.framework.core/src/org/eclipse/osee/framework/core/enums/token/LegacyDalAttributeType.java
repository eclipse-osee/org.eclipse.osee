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
import org.eclipse.osee.framework.core.enums.token.LegacyDalAttributeType.LegacyDalEnum;

/**
 * @author Stephen J. Molaro
 */
public class LegacyDalAttributeType extends AttributeTypeEnum<LegacyDalEnum> {

   // @formatter:off
	public final LegacyDalEnum A = new LegacyDalEnum(0, "A");
	public final LegacyDalEnum B = new LegacyDalEnum(1, "B");
	public final LegacyDalEnum C = new LegacyDalEnum(2, "C");
	public final LegacyDalEnum D = new LegacyDalEnum(3, "D");
	public final LegacyDalEnum E = new LegacyDalEnum(4, "E");
	public final LegacyDalEnum Unspecified = new LegacyDalEnum(5, "Unspecified");
	// @formatter:on

   public LegacyDalAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1152921504606847120L, namespace, "Legacy DAL", mediaType,
         "Legacy Development Assurance Level (original DAL)", taggerType);
   }

   public class LegacyDalEnum extends EnumToken {
      public LegacyDalEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
